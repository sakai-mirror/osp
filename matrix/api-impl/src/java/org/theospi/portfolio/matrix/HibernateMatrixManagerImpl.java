/**********************************************************************************
* $URL$
* $Id$
***********************************************************************************
*
* Copyright (c) 2005, 2006, 2007 The Sakai Foundation.
*
* Licensed under the Educational Community License, Version 1.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*      http://www.opensource.org/licenses/ecl1.php
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*
**********************************************************************************/
package org.theospi.portfolio.matrix;

import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.zip.Adler32;
import java.util.zip.CheckedOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import org.hibernate.AssertionFailure;
import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Expression;
import org.jdom.Element;
import org.sakaiproject.authz.api.SecurityService;
import org.sakaiproject.content.api.ContentCollection;
import org.sakaiproject.content.api.ContentCollectionEdit;
import org.sakaiproject.content.api.ContentHostingService;
import org.sakaiproject.content.api.ContentResource;
import org.sakaiproject.content.api.ContentResourceEdit;
import org.sakaiproject.content.api.LockManager;
import org.sakaiproject.entity.api.Reference;
import org.sakaiproject.entity.api.ResourceProperties;
import org.sakaiproject.entity.api.ResourcePropertiesEdit;
import org.sakaiproject.exception.IdInvalidException;
import org.sakaiproject.exception.IdUnusedException;
import org.sakaiproject.exception.IdUsedException;
import org.sakaiproject.exception.ImportException;
import org.sakaiproject.exception.InconsistentException;
import org.sakaiproject.exception.OverQuotaException;
import org.sakaiproject.exception.PermissionException;
import org.sakaiproject.exception.ServerOverloadException;
import org.sakaiproject.exception.TypeException;
import org.sakaiproject.metaobj.security.AuthenticationManager;
import org.sakaiproject.metaobj.security.AllowMapSecurityAdvisor;
import org.sakaiproject.metaobj.shared.ArtifactFinder;
import org.sakaiproject.metaobj.shared.DownloadableManager;
import org.sakaiproject.metaobj.shared.EntityContextFinder;
import org.sakaiproject.metaobj.shared.mgt.*;
import org.sakaiproject.metaobj.shared.model.Agent;
import org.sakaiproject.metaobj.shared.model.Artifact;
import org.sakaiproject.metaobj.shared.model.FinderException;
import org.sakaiproject.metaobj.shared.model.FormConsumptionDetail;
import org.sakaiproject.metaobj.shared.model.Id;
import org.sakaiproject.metaobj.shared.model.MimeType;
import org.sakaiproject.metaobj.shared.model.StructuredArtifactDefinitionBean;
import org.sakaiproject.metaobj.shared.model.InvalidUploadException;
import org.sakaiproject.metaobj.worksite.mgt.WorksiteManager;
import org.sakaiproject.service.legacy.resource.DuplicatableToolService;
import org.sakaiproject.site.api.Site;
import org.sakaiproject.site.api.SitePage;
import org.sakaiproject.site.api.ToolConfiguration;
import org.sakaiproject.site.cover.SiteService;
import org.sakaiproject.tool.cover.ToolManager;
import org.sakaiproject.user.api.User;
import org.sakaiproject.user.cover.UserDirectoryService;
import org.sakaiproject.util.ResourceLoader;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import org.theospi.event.EventService;
import org.theospi.event.EventConstants;
import org.theospi.portfolio.guidance.mgt.GuidanceManager;
import org.theospi.portfolio.guidance.model.Guidance;
import org.theospi.portfolio.matrix.model.*;
import org.theospi.portfolio.matrix.model.impl.MatrixContentEntityProducer;
import org.theospi.portfolio.review.mgt.ReviewManager;
import org.theospi.portfolio.review.model.Review;
import org.theospi.portfolio.security.Authorization;
import org.theospi.portfolio.security.AuthorizationFacade;
import org.theospi.portfolio.shared.model.Node;
import org.theospi.portfolio.shared.model.OspException;
import org.theospi.portfolio.style.StyleConsumer;
import org.theospi.portfolio.style.mgt.StyleManager;
import org.theospi.portfolio.style.model.Style;
import org.theospi.portfolio.wizard.WizardFunctionConstants;
import org.theospi.portfolio.workflow.mgt.WorkflowManager;
import org.theospi.portfolio.workflow.model.Workflow;
import org.theospi.portfolio.workflow.model.WorkflowItem;
import org.theospi.utils.zip.UncloseableZipInputStream;

/**
 * @author rpembry
 */
public class HibernateMatrixManagerImpl extends HibernateDaoSupport
   implements MatrixManager, ReadableObjectHome, ArtifactFinder, DownloadableManager,
   PresentableObjectHome, DuplicatableToolService, StyleConsumer, FormConsumer {
   
   static final private String   IMPORT_BASE_FOLDER_ID = "importedMatrices";

   private IdManager idManager;
   private AuthenticationManager authnManager = null;
   private AuthorizationFacade authzManager = null;
   private AgentManager agentManager = null;
   private PresentableObjectHome xmlRenderer;
   private WorksiteManager worksiteManager;
   private LockManager lockManager;
   private boolean loadArtifacts = true;
   private ContentHostingService contentHosting = null;
   private SecurityService securityService;
   private EventService eventService;
   private DefaultScaffoldingBean defaultScaffoldingBean;
   private WorkflowManager workflowManager;
   private StructuredArtifactDefinitionManager structuredArtifactDefinitionManager;
   private GuidanceManager guidanceManager;
   private ReviewManager reviewManager;
   private StyleManager styleManager;

   private static final String SCAFFOLDING_ID_TAG = "scaffoldingId";
   private EntityContextFinder contentFinder = null;
   private String importFolderName;
   private boolean useExperimentalMatrix = false;
   
   private static ResourceLoader messages = new ResourceLoader(
         "org.theospi.portfolio.matrix.bundle.Messages");

   public Scaffolding createDefaultScaffolding() {
      return getDefaultScaffoldingBean().createDefaultScaffolding();
   }

   public List getScaffolding() {
      return getHibernateTemplate().find("from Scaffolding");
   }
   
   /**
    *  {@inheritDoc}
    */
   public List findAvailableScaffolding(String siteIdStr, Agent user) {
      
      Object[] params = new Object[]{getIdManager().getId(siteIdStr), user, new Boolean(true), new Boolean(true)};
      return getHibernateTemplate().find("from Scaffolding s where s.worksiteId=? " +
            "and (s.owner=? or s.published=? or s.preview=?) ", params);
   }
   
   /**
    *  {@inheritDoc}
    */
   public List findAvailableScaffolding(List sites, Agent user) {
      
      if ( sites == null || sites.size() == 0 )
         return new ArrayList();
      
      String[] paramNames = new String[] {"siteIds", "owner", "true"};
      Object[] params = new Object[]{sites, user, new Boolean(true)};
      return getHibernateTemplate().findByNamedParam("from Scaffolding s where s.worksiteId in ( :siteIds ) and ( s.owner = :owner or s.published=:true or s.preview=:true)",
            paramNames, params);
   }
   
   /**
    * gathers all the published scaffolding from the given site (id)
    * @param siteId String
    * @return List of Scaffolding
    */
   protected List findPublishedScaffolding(String siteId) {
      Object[] params = new Object[]{getIdManager().getId(siteId), new Boolean(true)};
      return getHibernateTemplate().find("from Scaffolding s where s.worksiteId=? " +
            "and s.published=?",
            params);
   }

   /**
    * 
    * @param sites A list of site Ids (Ids)
    * @return list of all published scaffolding within specified sites
    */
   public List findPublishedScaffolding(List sites) {
      String[] paramNames = new String[] {"siteIds", "published"};
      Object[] params = new Object[]{sites, new Boolean(true)};
      return getHibernateTemplate().findByNamedParam("from Scaffolding s where s.worksiteId in ( :siteIds ) " +
            "and s.published=:published",
            paramNames, params);
   }
   
   /**
    * Gets all the scaffolding for the data warehouse.  It preloads all the cells, levels, criterion.
    * It sets the back trace from the level and criterion back to the scaffolding and sets the sequence 
    * index number for ordering.  
    * @return List of Scaffolding
    */
   public List getScaffoldingForWarehousing() {
      List scaffolding = getHibernateTemplate().find("from Scaffolding");
      
      for(Iterator i = scaffolding.iterator(); i.hasNext(); ) {
         Scaffolding scaff = (Scaffolding)i.next();
         Set cells = scaff.getScaffoldingCells();
         
         //Load the evaluators for the cells as well.
         for(Iterator ii = cells.iterator(); ii.hasNext(); ) {
            ScaffoldingCell cell = (ScaffoldingCell)ii.next();
            
            cell.setEvaluators(getScaffoldingCellEvaluators(cell.getWizardPageDefinition().getId(), true));
         }
         
         List levels = scaff.getLevels();
         int n = 0;
         for(Iterator ii = levels.iterator(); ii.hasNext(); ) {
            Level level = (Level)ii.next();
            
            level.setSequenceNumber(n++);
            level.setScaffolding(scaff);
         }
         
         List criteria = scaff.getCriteria();
         criteria.size();
         n = 0;
         for(Iterator ii = criteria.iterator(); ii.hasNext(); ) {
            Criterion criterion = (Criterion)ii.next();
            
            criterion.setSequenceNumber(n++);
            criterion.setScaffolding(scaff);
         }
      }
      
      return scaffolding;
   }

   public List getMatrices(Id scaffoldingId) {
      List matrices = getHibernateTemplate().find(
            "from Matrix matrix where matrix.scaffolding.id = ?", new Object[]{scaffoldingId});

      return matrices;
   }

   public List getCellsByScaffoldingCell(Id scaffoldingCellId) {
      List list = getHibernateTemplate().find("from Cell cell where cell.scaffoldingCell.id=?", scaffoldingCellId);
      return list;
   }
   
   public List getPagesByPageDef(Id pageDefId) {
      List list = getHibernateTemplate().find("from WizardPage page where page.pageDefinition.id=?", pageDefId);
      return list;
   }
   
   public List getMatrices(Id scaffoldingId, Id agentId) {
      String query = "from Matrix matrix";
      Object[] params = new Object[]{};

      if (scaffoldingId == null && agentId == null) {
      } else if(scaffoldingId == null) {
         query += " where matrix.owner like ?";
         params = new Object[]{getAgentManager().getAgent(agentId)};
      } else if (agentId == null) {
         query += " where matrix.scaffolding.id like ?";
         params = new Object[]{scaffoldingId};
      } else {
         query += " where matrix.scaffolding.id like ? and matrix.owner like ?";
         params = new Object[]{scaffoldingId, getAgentManager().getAgent(agentId)};
      }

      //TODO move this into a callback
      getHibernateTemplate().setCacheQueries(true);

      List list = getHibernateTemplate().find(query, params);

      return list;
   }

   public Matrix getMatrix(Id scaffoldingId, Id agentId) {
      List list = getMatrices(scaffoldingId, agentId);

      if (list.size() > 0)
         return (Matrix) list.get(0);
      else
         return null;
   }
   
   public List getCells(Matrix matrix) {
      getHibernateTemplate().setCacheQueries(true);
      return getHibernateTemplate().find("from Cell cell where cell.matrix.id=?",
            matrix.getId());
      
   }

   public Cell getCell(Matrix matrix, Criterion rootCriterion, Level level) {
      //TODO should be something easier for this HQL
      
      Object[] params = new Object[]{matrix.getId(),
                                     rootCriterion.getId(), level.getId()};
      getHibernateTemplate().setCacheQueries(true);
      List list = getHibernateTemplate()
            .find("from Cell cell where cell.matrix.id=? and cell.scaffoldingCell.rootCriterion.id=? and cell.scaffoldingCell.level.id=?",
                  params);
      return (Cell) list.get(0);
   }

   public void unlockNextCell(Cell cell) {
      Matrix matrix = cell.getMatrix();
      List levels = matrix.getScaffolding().getLevels();
      int i = levels.indexOf(cell.getScaffoldingCell().getLevel());
      if (i < levels.size() - 1) {
         Level nextLevel = (Level) levels.get(i + 1);
         Cell nextCell = getCell(cell.getMatrix(), cell.getScaffoldingCell().getRootCriterion(), nextLevel);
         if (nextCell.getStatus().equals(MatrixFunctionConstants.LOCKED_STATUS)) {
            nextCell.setStatus(MatrixFunctionConstants.READY_STATUS);
            storeCell(nextCell);
         }
      }
   }
   
   public ScaffoldingCell getNextScaffoldingCell(ScaffoldingCell scaffoldingCell, 
         int progressionOption) {
      Scaffolding scaffolding = scaffoldingCell.getScaffolding();      
      ScaffoldingCell nextCell = null;
      
      if (progressionOption == Scaffolding.HORIZONTAL_PROGRESSION) {
         List columns = scaffolding.getLevels();
         int i = columns.indexOf(scaffoldingCell.getLevel());
         if (i < columns.size() - 1) {
            Level column = (Level)columns.get(i+1);
            nextCell = getScaffoldingCell(scaffoldingCell.getRootCriterion(), column);
         }
      }
      else if (progressionOption == Scaffolding.VERTICAL_PROGRESSION) {
         List rows = scaffolding.getCriteria();
         int i = rows.indexOf(scaffoldingCell.getRootCriterion());
         if (i < rows.size() - 1) {
            Criterion row = (Criterion)rows.get(i+1);
            nextCell = getScaffoldingCell(row, scaffoldingCell.getLevel());
         }
      }
      return nextCell;
   }
   
   public Cell getNextCell(Cell cell, int progressionOption) {
      ScaffoldingCell scaffoldingCell = cell.getScaffoldingCell();
      Scaffolding scaffolding = scaffoldingCell.getScaffolding();      
      Cell nextCell = null;
      
      if (progressionOption == Scaffolding.HORIZONTAL_PROGRESSION) {
         List columns = scaffolding.getLevels();
         int i = columns.indexOf(scaffoldingCell.getLevel());
         if (i < columns.size() - 1) {
            Level column = (Level)columns.get(i+1);
            nextCell = getCell(cell.getMatrix(), scaffoldingCell.getRootCriterion(), column);
         }
      }
      else if (progressionOption == Scaffolding.VERTICAL_PROGRESSION) {
         List rows = scaffolding.getCriteria();
         int i = rows.indexOf(scaffoldingCell.getRootCriterion());
         if (i < rows.size() - 1) {
            Criterion row = (Criterion)rows.get(i+1);
            nextCell = getCell(cell.getMatrix(), row, scaffoldingCell.getLevel());
         }
      }
      return nextCell;
   }
   
   protected Cell getMatrixCellByWizardPageDef(Matrix matrix, Id wizardPageDefId) {
      for (Iterator cells = matrix.getCells().iterator(); cells.hasNext();) {
         Cell cell = (Cell)cells.next();
         if (cell.getScaffoldingCell().getWizardPageDefinition()
               .getId().getValue().equals(wizardPageDefId.getValue())) {
            return cell;
         }
      }
      return null;
   }

   public Criterion getCriterion(Id criterionId) {
      return (Criterion) this.getHibernateTemplate().load(Criterion.class, criterionId);
   }

   public Level getLevel(Id levelId) {
      return (Level) this.getHibernateTemplate().load(Level.class, levelId);
   }
   
   public Cell getCell(Id cellId) {
      Cell cell = (Cell) this.getHibernateTemplate().get(Cell.class, cellId);
      return cell;
   }

   public Cell getCellFromPage(Id pageId) {
      List cells = getHibernateTemplate().find(
         "from Cell where wizard_page_id=?", new Object[]{pageId.getValue()});

      if (cells.size() > 0) {
         return (Cell)cells.get(0);
      }

      return null;
   }
   
   public Id storeCell(Cell cell) {
      this.getHibernateTemplate().saveOrUpdate(cell);
      return cell.getId();
   }

   public Id storePage(WizardPage page) {
      this.getHibernateTemplate().saveOrUpdate(page);
      eventService.postEvent(EventConstants.EVENT_FORM_ADD,page.getId().getValue());
      return page.getId();
   }

   public void publishScaffolding(Id scaffoldingId) {
      Scaffolding scaffolding = this.getScaffolding(scaffoldingId);
      scaffolding.setPreview(false);
      scaffolding.setPublished(true);
      scaffolding.setPublishedBy(authnManager.getAgent());
      scaffolding.setPublishedDate(new Date(System.currentTimeMillis()));
      this.storeScaffolding(scaffolding);
      eventService.postEvent(EventConstants.EVENT_SCAFFOLD_PUBLISH,scaffolding.getId().getValue());

   }
   public void previewScaffolding(Id scaffoldingId) {
      Scaffolding scaffolding = this.getScaffolding(scaffoldingId);
      scaffolding.setPreview(true);
      this.storeScaffolding(scaffolding);

   }
   public Scaffolding storeScaffolding(Scaffolding scaffolding) {
      scaffolding = (Scaffolding)this.store(scaffolding);
      getHibernateTemplate().flush();
      eventService.postEvent(EventConstants.EVENT_SCAFFOLD_ADD_REVISE,scaffolding.getId().getValue());
      return scaffolding;
   }
   public Scaffolding saveNewScaffolding(Scaffolding scaffolding) {
      
      Id id = (Id)this.save(scaffolding);
      getHibernateTemplate().flush();
      scaffolding = getScaffolding(id);
      
      return scaffolding;
   }
   
   public Id storeScaffoldingCell(ScaffoldingCell scaffoldingCell) {
      scaffoldingCell = (ScaffoldingCell)store(scaffoldingCell);
      return scaffoldingCell.getId();
   }

   public Object store(Object obj) {
      obj = this.getHibernateTemplate().merge(obj);
      return obj;
   }

   public Object save(Object obj) {
      obj = this.getHibernateTemplate().save(obj);
      return obj;
   }

   public Matrix createMatrix(Agent owner, Scaffolding scaffolding) {
      Matrix matrix = new Matrix();
      matrix.setOwner(owner);
      matrix.setScaffolding(scaffolding);

      List levels = scaffolding.getLevels();
      List criteria = scaffolding.getCriteria();

      Criterion criterion = null;
      Level level = null;

      for (Iterator criteriaIterator = criteria.iterator(); criteriaIterator.hasNext();) {
         criterion = (Criterion) criteriaIterator.next();

         for (Iterator levelsIterator = levels.iterator(); levelsIterator.hasNext();) {
            level = (Level) levelsIterator.next();

            Cell cell = new Cell();
            cell.getWizardPage().setOwner(owner);
            ScaffoldingCell sCell = getScaffoldingCell(criterion, level);
            cell.setScaffoldingCell(sCell);
            if (sCell != null) {
               String status = sCell.getInitialStatus();
               cell.setStatus(status);
            }
            else
               cell.setStatus(MatrixFunctionConstants.LOCKED_STATUS);

            matrix.add(cell);
         }
      }

      this.getHibernateTemplate().save(matrix);
      return matrix;
   }

   public Attachment attachArtifact(Id pageId, Reference artifactRef) {
      Id artifactId = convertRef(artifactRef);
      detachArtifact(pageId, artifactId);
      WizardPage page = getWizardPage(pageId);
      Attachment attachment = new Attachment();
      attachment.setArtifactId(artifactId);
      attachment.setWizardPage(page);
      attachment.setNewId(getIdManager().createId());
      
      page.getAttachments().add(attachment);

      this.getHibernateTemplate().saveOrUpdate(page);
      return attachment;
   }

   public WizardPage getWizardPage(Id pageId) {
      WizardPage page = (WizardPage) this.getHibernateTemplate().get(WizardPage.class, pageId);
      
      // check for invalid page (in case wizard/matrix is deleted)
      if ( page == null )
      {
         logger.warn("Invalid wizard or matrix page: " + pageId.toString() );
         return null;
      }
      
      page.getAttachments().size();
      page.getPageForms().size();

      removeFromSession(page);
      return page;
   }
   
   protected List getWizardPages() {
      return this.getHibernateTemplate().find("from WizardPage");
   }

   protected Id convertRef(Reference artifactRef) {
      String uuid = getContentHosting().getUuid(artifactRef.getId());
      return getIdManager().getId(uuid);
   }

   public void detachArtifact(final Id pageId, final Id artifactId) {

      HibernateCallback callback = new HibernateCallback() {
         public Object doInHibernate(Session session) throws HibernateException, SQLException {
            WizardPage page = (WizardPage) session.load(WizardPage.class, pageId);
            Set attachments = page.getAttachments();
            Iterator iter = attachments.iterator();
            List toRemove = new ArrayList();
            while (iter.hasNext()) {
               Attachment a = (Attachment) iter.next();
               if (a.getArtifactId()==null || artifactId.equals(a.getArtifactId())) {
                  toRemove.add(a);
               }
            }
            attachments.removeAll(toRemove);
            page.setAttachments(attachments);

            session.saveOrUpdate(page);
            return null;
         }

      };

      getHibernateTemplate().execute(callback);

   }
   
   public void detachForm(final Id pageId, final Id artifactId) {

      HibernateCallback callback = new HibernateCallback() {
         public Object doInHibernate(Session session) throws HibernateException, SQLException {
            WizardPage page = (WizardPage) session.load(WizardPage.class, pageId);
            Set forms = page.getPageForms();
            
            Iterator iter = forms.iterator();
            List toRemove = new ArrayList();
            while (iter.hasNext()) {
               WizardPageForm wpf = (WizardPageForm) iter.next();
               if (wpf.getArtifactId()==null || artifactId.equals(wpf.getArtifactId())) {
                  toRemove.add(wpf);
               }
            }
            forms.removeAll(toRemove);
            page.setPageForms(forms);
            
            session.saveOrUpdate(page);  
            eventService.postEvent(EventConstants.EVENT_FORM_DELETE, pageId.getValue());
            return null;
         }

      };

      getHibernateTemplate().execute(callback);

   }
   
   public Matrix getMatrixByPage(Id pageId) {
      Matrix matrix = null;
      Object[] params = new Object[]{pageId};
      
      List list = this.getHibernateTemplate().find("select cell.matrix from " +
            "Cell cell where cell.wizardPage.id=? ", params);
      if (list.size() == 1) {
         matrix = (Matrix) list.get(0);
      }
         
      return matrix;
   }

   public Matrix getMatrix(Id matrixId) {
      return (Matrix) this.getHibernateTemplate().load(Matrix.class, matrixId);
   }
   
   public List getMatricesForWarehousing() {
      
      List matrices = getMatrices(null, null);

        
        for(Iterator ii = matrices.iterator(); ii.hasNext(); ) {
           Matrix mat = (Matrix)ii.next();
           
           mat.getId();
           //mat.setMatrixTool(tool);
           
           mat.getCells().size();
           
           for(Iterator iii= mat.getCells().iterator(); iii.hasNext(); ) {
              Cell cell = (Cell)iii.next();

              cell.getWizardPage().getPageForms().size();
              cell.getWizardPage().getAttachments().size();
           }
           
           getHibernateTemplate().evict(mat);
        }
      return matrices;
   }
   
   public List getWizardPagesForWarehousing() {
      
      List wizardPages = this.getWizardPages();

        
        for(Iterator ii = wizardPages.iterator(); ii.hasNext(); ) {
           WizardPage wizardPage = (WizardPage)ii.next();
           
           wizardPage.getId();
           wizardPage.getPageForms().size();
           wizardPage.getAttachments().size();
           
           getHibernateTemplate().evict(wizardPage);
        }
      return wizardPages;
   }

   public Scaffolding getScaffolding(Id scaffoldingId) {
      return (Scaffolding) this.getHibernateTemplate().get(Scaffolding.class, scaffoldingId);
      //return getScaffolding(scaffoldingId, false);
   }

   protected Scaffolding getScaffoldingForExport(Id scaffoldingId) {
      Scaffolding scaffolding = (Scaffolding) this.getHibernateTemplate().get(Scaffolding.class, scaffoldingId);

      for (Iterator iter = scaffolding.getScaffoldingCells().iterator(); iter.hasNext();) {
         ScaffoldingCell sCell = (ScaffoldingCell) iter.next();
         Collection evaluators = this.getScaffoldingCellEvaluators(sCell.getWizardPageDefinition().getId(), false);
         sCell.setEvaluators(new HashSet(evaluators));
      }      

      return scaffolding;
   }
   
   protected List getScaffoldingByStyle(Id styleId) {
      Object[] params = new Object[]{styleId};
      return getHibernateTemplate().find("from Scaffolding s where s.style.id=? " , 
               params);
      
   }
   
   protected List getWizardPageDefByStyle(Id styleId) {
      Object[] params = new Object[]{styleId};
      return getHibernateTemplate().find("from WizardPageDefinition wpd where wpd.style.id=? " , 
               params);
      
   }
   
   public ScaffoldingCell getScaffoldingCell(Criterion criterion, Level level) {
      ScaffoldingCell scaffoldingCell = null;
      Object[] params = new Object[]{criterion.getId(), 
            level.getId()};
      
      List list = this.getHibernateTemplate().find("from " +
            "ScaffoldingCell scaffoldingCell where scaffoldingCell.rootCriterion.id=? " +
            "and scaffoldingCell.level.id=?", params);
      if (list.size() == 1) {
         scaffoldingCell = (ScaffoldingCell) list.get(0);
      }
         
      return scaffoldingCell;
   }
   
   public String getScaffoldingCellsStatus(Id scaffoldingCellId) {
      ScaffoldingCell scaffoldingCell = null;
      String result = "";
      Criteria c = this.getSession().createCriteria(ScaffoldingCell.class);
      try {
         c.add(Expression.eq("id", scaffoldingCellId));
      
         scaffoldingCell = (ScaffoldingCell)c.uniqueResult();
         result = scaffoldingCell.getInitialStatus();
         this.removeFromSession(scaffoldingCell);
      } catch (HibernateException e) {
         logger.error("Error returning scaffoldingCell with id: " + scaffoldingCellId);
         return null;
      }
      return result;
   }
   
   public ScaffoldingCell getScaffoldingCell(Id id) {
      ScaffoldingCell scaffoldingCell = (ScaffoldingCell)this.getHibernateTemplate().load(ScaffoldingCell.class, id);
      
      scaffoldingCell.setEvaluators(getScaffoldingCellEvaluators(scaffoldingCell.getWizardPageDefinition().getId(), true));

      return scaffoldingCell;
   }
   
   /**
    * {@inheritDoc}
    */
   public Set<ScaffoldingCell> getScaffoldingCells(Id scaffoldingId) {
      Object[] params = new Object[]{scaffoldingId};
      
      List list = this.getHibernateTemplate().find("from " +
            "ScaffoldingCell scaffoldingCell where scaffoldingCell.scaffolding.id=?", 
            params);
      return new HashSet<ScaffoldingCell>(list);
   }
   
   public ScaffoldingCell getScaffoldingCellByWizardPageDef(Id id) {
      ScaffoldingCell scaffoldingCell = null;
      Object[] params = new Object[]{id};
      
      List list = this.getHibernateTemplate().find("from " +
            "ScaffoldingCell scaffoldingCell where scaffoldingCell.wizardPageDefinition.id=?", 
            params);
      if (list.size() == 1) {
         scaffoldingCell = (ScaffoldingCell) list.get(0);
      }
         
      return scaffoldingCell;
   }
   
   protected Collection getScaffoldingCellEvaluators(Id wizardPageDefId, boolean useAgentId) {
      Collection evaluators = new HashSet();
      Collection viewerAuthzs = getAuthzManager().getAuthorizations(null,
            MatrixFunctionConstants.EVALUATE_MATRIX, wizardPageDefId);

      for (Iterator i = viewerAuthzs.iterator(); i.hasNext();) {
         Authorization evaluator = (Authorization) i.next();
         if (useAgentId)
            evaluators.add(evaluator.getAgent());
         else
            evaluators.add(evaluator.getAgent().getId());
      }
      return evaluators;
   }
   
   public void removeFromSession(Object obj) {
      this.getHibernateTemplate().evict(obj);
   }
   
   public void clearSession() {
      this.getHibernateTemplate().clear();
   }
   
   private Scaffolding getScaffoldingByArtifact(Id artifactId) {

      List list = this.getHibernateTemplate().find("from " +
            "Scaffolding scaffolding where scaffolding.artifactId=?", 
            artifactId.getValue());
      if (list == null) return null;
      if (list.size() == 1) return (Scaffolding)list.get(0);
      return null;      
   }

   List getCellAttachments(Id cellId) {
      return this.getHibernateTemplate().find("from Attachment attachment where attachment.cell=?", cellId.getValue());
   }

   public Attachment getAttachment(Id attachmentId) {
      return (Attachment) this.getHibernateTemplate().load(Attachment.class, attachmentId);
   }
   
   public Set getPageForms(WizardPage page) {
      Set result = new HashSet();
      Set removes = new HashSet();
      if (page.getPageForms() != null) {
         for (Iterator iter = page.getPageForms().iterator(); iter.hasNext();) {
            WizardPageForm wpf = (WizardPageForm) iter.next();
            Node node = getNode(wpf.getArtifactId(), page);
            if (node != null) {
               result.add(node);
            }
            else if ( !isNodeHidden(wpf.getArtifactId()) ) {
               removes.add(wpf.getArtifactId());
            }
         }
         for (Iterator iter2 = removes.iterator(); iter2.hasNext();) {
            Id id = (Id) iter2.next();
            logger.warn("Cell contains stale form references (null node encountered) for Cell: " + page.getId().getValue() + ". Detaching");
            detachForm(page.getId(), id);
         }
      }
      return result;
   }
   
   public Set getPageContents(WizardPage page) {
      Set result = new HashSet();
      Set removes = new HashSet();
      if (page.getAttachments() != null) {
         for (Iterator iter = page.getAttachments().iterator(); iter.hasNext();) {
            Attachment attachment = (Attachment) iter.next();
            Node node = getNode(attachment.getArtifactId(), page);
            if (node != null) {
               result.add(node);
            }
            else if ( !isNodeHidden(attachment.getArtifactId()) ) {
               removes.add(attachment.getArtifactId());
            }
         }
         for (Iterator iter2 = removes.iterator(); iter2.hasNext();) {
            Id id = (Id) iter2.next();
            logger.warn("Cell contains stale artifact references (null node encountered) for Cell: " + page.getId().getValue() + ". Detaching");
            detachArtifact(page.getId(), id);
         }
      }
      return result;
   }

   protected Node getNode(Id artifactId, WizardPage page) {
      Node node = getNode(artifactId);
      if (node == null) {
         return null;
      }
      String siteId = page.getPageDefinition().getSiteId();
      ContentResource wrapped = new ContentEntityWrapper(node.getResource(),
            buildRef(siteId, page.getId().getValue(), node.getResource()));

      return new Node(artifactId, wrapped, node.getTechnicalMetadata().getOwner());
   }

   private boolean isNodeHidden( Id artifactId ) {
      try {
         String id = getContentHosting().resolveUuid(artifactId.getValue());
         if ( id == null )
            return false; // non-existant node is not "hidden"
         getContentHosting().checkResource(id);
      }
      catch (PermissionException e) {
         return true; // not permitted to view indicates "hidden"
      }
      catch (Exception e) {
         return false;  // any other error does not constitute "hidden"
      }
      return false;
   }
   
   public Node getNode(Id artifactId) {
      String id = getContentHosting().resolveUuid(artifactId.getValue());
      if (id == null) {
         return null;
      }

      getSecurityService().pushAdvisor(
         new AllowMapSecurityAdvisor(ContentHostingService.EVENT_RESOURCE_READ,
               getContentHosting().getReference(id)));

      try {
         ContentResource resource = getContentHosting().getResource(id);
         String ownerId = resource.getProperties().getProperty(resource.getProperties().getNamePropCreator());
         Agent owner = getAgentFromId(getIdManager().getId(ownerId));

         return new Node(artifactId, resource, owner);
      }
      catch (PermissionException e) {
         logger.warn(this+".getNode "+e.toString());
         return null;
      }
      catch (Exception e) {
         logger.error(this+".getNode "+e.toString());
         return null;
      }
   }

   public Node getNode(Reference ref) {
      String nodeId = getContentHosting().getUuid(ref.getId());

      return getNode(getIdManager().getId(nodeId));
   }
   /*
   public List getPageArtifacts(WizardPage page)
   {
      List nodeList = new ArrayList();
      Set attachments = page.getAttachments();
      
      for (Iterator attachmentIterator = attachments.iterator(); attachmentIterator.hasNext();) {
         Attachment att = (Attachment)attachmentIterator.next();
         //TODO is it okay to clear the whole thing here?
         getHibernateTemplate().clear();
         
         Node node = getNode(att.getArtifactId(), page);
         if (node != null) {
        	 nodeList.add(node);
         } else
             logger.warn("Cell contains stale artifact references (null node encountered) for Cell: " + page.getId());
         
      } 
      return nodeList;
   }
*/
   public List getCellsByArtifact(Id artifactId) {
      //return this.getHibernateTemplate().find("select distinct attachment.cell from Attachment attachment where attachment.artifactId=?", artifactId.getValue());
      //this.getHibernateTemplate().find
      Criteria c = null;
      try {
         c = this.getSession().createCriteria(Cell.class);
         c.setFetchMode("scaffoldingCell", FetchMode.JOIN);
         c.setFetchMode("scaffoldingCell.scaffolding", FetchMode.JOIN);
         //c.add(Expression.eq("artifactId", artifactId));
         Criteria att = c.createCriteria("attachments");
         att.add(Expression.eq("artifactId", artifactId));
         
         return new ArrayList(c.list());
      } catch (DataAccessResourceFailureException e) {
         logger.error("", e);
      } catch (HibernateException e) {
         logger.error("", e);
      } catch (IllegalStateException e) {
         logger.error("", e);
      }
      return new ArrayList();
   }

   public List getCellsByForm(Id formId) {
      //return this.getHibernateTemplate().find("select distinct attachment.cell from Attachment attachment where attachment.artifactId=?", artifactId.getValue());
      //this.getHibernateTemplate().find
      Criteria c = null;
      try {
         c = this.getSession().createCriteria(Cell.class);
         c.setFetchMode("scaffoldingCell", FetchMode.JOIN);
         c.setFetchMode("scaffoldingCell.scaffolding", FetchMode.JOIN);
         //c.add(Expression.eq("artifactId", artifactId));
         Criteria att = c.createCriteria("pageForms");
         att.add(Expression.eq("artifactId", formId));
         
         return new ArrayList(c.list());
      } catch (DataAccessResourceFailureException e) {
         logger.error("", e);
      } catch (HibernateException e) {
         logger.error("", e);
      } catch (IllegalStateException e) {
         logger.error("", e);
      }
      return new ArrayList();
   }

   protected Agent getAgentFromId(Id agentId) {
      return agentManager.getAgent(agentId);
   }

   /**
    * @return Returns the idManager.
    */
   public IdManager getIdManager() {
      return idManager;
   }

   /**
    * @param idManager The idManager to set.
    */
   public void setIdManager(IdManager idManager) {
      this.idManager = idManager;
   }



   /**
    * @return Returns the authManager.
    */
   public AuthenticationManager getAuthnManager() {
      return authnManager;
   }

   /**
    * @param authnManager The authnManager to set.
    */
   public void setAuthnManager(AuthenticationManager authnManager) {
      this.authnManager = authnManager;
   }

   /**
    * @return Returns the authzManager.
    */
   public AuthorizationFacade getAuthzManager() {
      return authzManager;
   }

   /**
    * @param authzManager The authzManager to set.
    */
   public void setAuthzManager(AuthorizationFacade authzManager) {
      this.authzManager = authzManager;
   }

   /**
    * @return Returns the agentManager.
    */
   public AgentManager getAgentManager() {
      return agentManager;
   }

   /**
    * @param agentManager The agentManager to set.
    */
   public void setAgentManager(AgentManager agentManager) {
      this.agentManager = agentManager;
   }

   /* (non-Javadoc)
    * @see org.theospi.portfolio.matrix.model.hibernate.impl.MatrixManager#deleteMatrix(org.theospi.portfolio.shared.model.Id)
    */
   public void deleteMatrix(Id matrixId) {
      Matrix matrix = getMatrix( matrixId );
      Set cells = matrix.getCells();
       
      // first unlock all resources associated with this matrix
      for (Iterator cellIt=cells.iterator(); cellIt.hasNext();) 
      {
         Cell cell = (Cell)cellIt.next();
         WizardPage page = cell.getWizardPage();
         
         for (Iterator iter = page.getAttachments().iterator(); iter.hasNext();) {
            Attachment att = (Attachment)iter.next();
            getLockManager().removeLock(att.getArtifactId().getValue(), 
                     page.getId().getValue());
         }
      
         for (Iterator iter = page.getPageForms().iterator(); iter.hasNext();) {
            WizardPageForm pageForm = (WizardPageForm)iter.next();
            getLockManager().removeLock(pageForm.getArtifactId().getValue(), 
                     page.getId().getValue());
         }

         List reviews = getReviewManager().getReviewsByParent(
               page.getId().getValue(), 
               page.getPageDefinition().getSiteId(),
               MatrixContentEntityProducer.MATRIX_PRODUCER);
         for (Iterator iter = reviews.iterator(); iter.hasNext();) {
            Review review = (Review)iter.next();
            getLockManager().removeLock(review.getReviewContent().getValue(), 
                     page.getId().getValue());
         } 
      }
      
      // Now delete matrix
      this.getHibernateTemplate().delete(getMatrix(matrixId));
   }

   public void deleteScaffolding(Id scaffoldingId) {
      this.getHibernateTemplate().delete(getScaffolding(scaffoldingId));
      eventService.postEvent(EventConstants.EVENT_SCAFFOLD_DELETE,scaffoldingId.getValue());
   }
   
   public Cell submitCellForEvaluation(Cell cell) {
      Date now = new Date(System.currentTimeMillis());
      getHibernateTemplate().refresh(cell); //TODO not sure if this is necessary
      ScaffoldingCell sCell = cell.getScaffoldingCell();
      WizardPage page = cell.getWizardPage();
      
      //    Actions for current cell
      processContentLockingWorkflow(true, page);
      processStatusChangeWorkflow(MatrixFunctionConstants.PENDING_STATUS, page);
      cell.getWizardPage().setModified(now);
      
      if (sCell.getScaffolding().getWorkflowOption() > 0)
         processWorkflow(sCell.getScaffolding().getWorkflowOption(), cell.getId());

      return cell;
   }

   public WizardPage submitPageForEvaluation(WizardPage page) {
      Date now = new Date(System.currentTimeMillis());
      
      WizardPage thePage = getWizardPage(page.getId());
      getHibernateTemplate().refresh(thePage); //TODO not sure if this is necessary

      processContentLockingWorkflow(true, thePage);
      
      thePage.setStatus(MatrixFunctionConstants.PENDING_STATUS);
      thePage.setModified(now);
      getHibernateTemplate().merge(thePage);
      
      return page;
   }
   
   protected List getEvaluatableCells(Agent agent, Agent role, Id worksiteId) {
      List returned = this.getHibernateTemplate().find("select distinct new " +
            "org.theospi.portfolio.matrix.model.EvaluationContentWrapperForMatrixCell(" +
            "wp.id, " +
            "wp.pageDefinition.title, c.matrix.owner, " +
            "c.wizardPage.modified, wp.pageDefinition.siteId) " +
            "from WizardPage wp, Authorization auth, Cell c " +
            "where wp.pageDefinition.id = auth.qualifier " +
            "and wp.id = c.wizardPage.id " +
            "and auth.function = ? and wp.status = ? and (auth.agent=? " +
            " or auth.agent=?) " +
            " and wp.pageDefinition.siteId=?",
         new Object[]{MatrixFunctionConstants.EVALUATE_MATRIX,
            MatrixFunctionConstants.PENDING_STATUS,
            agent, role,
            worksiteId.getValue()});

      return returned;
   }
   
   public List getEvaluatableWizardPages(Agent agent, Agent role, Id worksiteId) {
      List wizardPages = this.getHibernateTemplate().find("select distinct new " +
            "org.theospi.portfolio.wizard.model.EvaluationContentWrapperForWizardPage(" +
            "cwp.wizardPage.id, " +
            "cwp.wizardPage.pageDefinition.title, cwp.category.wizard.owner, " +
            "cwp.wizardPage.modified, " +
            "cwp.category.wizard.wizard.type, cwp.wizardPage.pageDefinition.siteId) " +
            "from CompletedWizardPage cwp, " +
            "Authorization auth " +
            "where cwp.wizardPage.pageDefinition.id = auth.qualifier " +
            "and auth.function = ? and cwp.wizardPage.status = ? and (auth.agent=? " +
            " or auth.agent=?) " +
            " and cwp.wizardPage.pageDefinition.siteId=?",
         new Object[]{MatrixFunctionConstants.EVALUATE_MATRIX,
            MatrixFunctionConstants.PENDING_STATUS,
            agent, role,
            worksiteId.getValue()});
      
      return wizardPages;
   }
   
   protected List getEvaluatableWizards(Agent agent, Agent role, Id worksiteId) {
     
      List wizards = this.getHibernateTemplate().find("select distinct new " +
            "org.theospi.portfolio.wizard.model.EvaluationContentWrapperForWizard(" +
            "cw.wizard.id, " +
            "cw.wizard.name, cw.owner, " +
            "cw.created, cw.wizard.siteId) " +
            "from CompletedWizard cw, " +
            "Authorization auth " +
            "where cw.wizard.id = auth.qualifier " +
            "and auth.function = ? and cw.status = ? and (auth.agent=? " +
            " or auth.agent=?) " +
            " and cw.wizard.siteId=?",
         new Object[]{WizardFunctionConstants.EVALUATE_WIZARD,
            MatrixFunctionConstants.PENDING_STATUS,
            agent, role,
            worksiteId.getValue()});
      return wizards;
   }
   
   public List getEvaluatableItems(Agent agent) {
      //get all sites
      List allEvals = new ArrayList();
      List sites = getWorksiteManager().getUserSites();
      for (Iterator i = sites.iterator(); i.hasNext();) {
         Site site = (Site) i.next();
         allEvals.addAll(getEvaluatableItems(agent, idManager.getId(site.getId())));
      }
      
      return allEvals;
   }
   
   /**
    *  {@inheritDoc}
    */
   public List getEvaluatableItems(Agent agent, Id worksiteId) {
      List roles = agent.getWorksiteRoles(worksiteId.getValue());
      Agent role = null;
      if (roles.size() > 0)
         role= (Agent)roles.get(0);
      
      List returned = getEvaluatableCells(agent, role, worksiteId);
      List wizardPages = getEvaluatableWizardPages(agent, role, worksiteId);
      List wizards = getEvaluatableWizards(agent, role, worksiteId);
      
      returned.addAll(wizardPages);
      returned.addAll(wizards);

      return returned;
   }
   
   public void packageScffoldingForExport(Id scaffoldingId, OutputStream os) throws IOException {
      Scaffolding oldScaffolding = this.getScaffoldingForExport(scaffoldingId); //, true);
      

      CheckedOutputStream checksum = new CheckedOutputStream(os, new Adler32());
      ZipOutputStream zos = new ZipOutputStream(new BufferedOutputStream(checksum));

      List levels = oldScaffolding.getLevels();
      List criteria = oldScaffolding.getCriteria();
      Set scaffoldingCells = oldScaffolding.getScaffoldingCells();
      List guidanceIds = new ArrayList();
      Set styleIds = new HashSet();
      List formIds = new ArrayList();
      
      levels.size();
      criteria.size();
      scaffoldingCells.size();
      for (Iterator iter = scaffoldingCells.iterator(); iter.hasNext();) {
         ScaffoldingCell sCell = (ScaffoldingCell)iter.next();
         Collection evalWorkflows = sCell.getWizardPageDefinition().getEvalWorkflows();
         for (Iterator iter2 = evalWorkflows.iterator(); iter2.hasNext();) {
            Workflow wf = (Workflow)iter2.next();
            wf.getItems().size();
         }
      }
      removeFromSession(oldScaffolding);
      
      if (oldScaffolding.getStyle() != null) {
         styleIds.add(oldScaffolding.getStyle().getId().getValue());
      }

      for (Iterator iter = scaffoldingCells.iterator(); iter.hasNext();) {
         ScaffoldingCell sCell = (ScaffoldingCell)iter.next();
         sCell.setCells(new HashSet());
         Collection evaluators = sCell.getEvaluators();
         sCell.setEvaluators(new HashSet(evaluators));
         
         List attachments = sCell.getWizardPageDefinition().getAttachments();
         sCell.getWizardPageDefinition().setAttachments( new ArrayList(attachments) );
         
         sCell.getWizardPageDefinition().setPages(new HashSet());
         
         Collection forms = sCell.getWizardPageDefinition().getAdditionalForms();
         sCell.getWizardPageDefinition().setAdditionalForms(new ArrayList(forms));
         
         Collection evalWorkflows = sCell.getWizardPageDefinition().getEvalWorkflows();
         for (Iterator iter2 = evalWorkflows.iterator(); iter2.hasNext();) {
            Workflow wf = (Workflow)iter2.next();
            Collection items = wf.getItems();
            wf.setItems(new HashSet(items));
         }
         sCell.getWizardPageDefinition().setEvalWorkflows(new HashSet(evalWorkflows));
         exportCellForms(zos, sCell, formIds);
         if (sCell.getGuidance() != null) {
            guidanceIds.add(sCell.getGuidance().getId().getValue());
         }
         if (sCell.getWizardPageDefinition().getStyle() != null) {
            styleIds.add(sCell.getWizardPageDefinition().getStyle().getId().getValue());
         }
      }

      if (guidanceIds.size() > 0) {
         exportGuidance(zos, guidanceIds);
      }
      
      if (styleIds.size() > 0) {
         exportStyle(zos, styleIds);
      }

      oldScaffolding.setLevels(new ArrayList(levels));
      oldScaffolding.setCriteria(new ArrayList(criteria));
      oldScaffolding.setScaffoldingCells(new HashSet(scaffoldingCells));
      oldScaffolding.setMatrix(new HashSet());

      removeFromSession(oldScaffolding);

      //Saving the agent is not necessary and causes a StackOverflowError when XMLEncoder tries
      // to serialize.  So, we clear out the agents.
      oldScaffolding.setOwner(null);
      oldScaffolding.setPublishedBy(null);
      
      ByteArrayOutputStream bos = new ByteArrayOutputStream();
      XMLEncoder xenc=new XMLEncoder(bos);
      xenc.writeObject(oldScaffolding);
      xenc.close();

      removeFromSession(oldScaffolding);

      storeFileInZip(zos, new ByteArrayInputStream(bos.toByteArray()),
         "scaffolding");
      this.getHibernateTemplate().clear();
      try {
         this.getHibernateTemplate().flush();
      }
      catch (AssertionFailure af) {
         //TODO There's got to be a better way to catch/prevent this error
         logger.warn("Catching AssertionFailure from Hibernate during a flush");
         this.getSession().clear();
      }
      bos.close();

      zos.finish();
      zos.flush();
   }

   protected void exportGuidance(ZipOutputStream zos, List guidanceIds)
         throws IOException {
      ZipEntry newfileEntry = new ZipEntry("guidance/guidanceList");
      zos.putNextEntry(newfileEntry);
      getGuidanceManager().packageGuidanceForExport(guidanceIds, zos);
      zos.closeEntry();
   }
   
   protected void exportStyle(ZipOutputStream zos, Set styleIds)
         throws IOException {

      ZipEntry newfileEntry = new ZipEntry("style/styleList");
      zos.putNextEntry(newfileEntry);
      getStyleManager().packageStyleForExport(styleIds, zos);
      zos.closeEntry();
   }

   protected void exportCellForms(ZipOutputStream zos, ScaffoldingCell cell, List formIds) throws IOException {
      List forms = cell.getAdditionalForms();
      for (Iterator i=forms.iterator();i.hasNext();) {
         String formId = (String) i.next();
         if (!formIds.contains(formId)) {
            storeFormInZip(zos, formId);
            formIds.add(formId);
         }
      }

      if (cell.getEvaluationDevice() != null) {
         String evalDevId = cell.getEvaluationDevice().getValue();
         if (!formIds.contains(evalDevId)) {
            storeFormInZip(zos, evalDevId);
            formIds.add(evalDevId);
         }
      }

      if (cell.getReflectionDevice() != null) {
         String reflDevId = cell.getReflectionDevice().getValue();
         if (!formIds.contains(reflDevId)) {
            storeFormInZip(zos, reflDevId);
            formIds.add(reflDevId);
         }
      }

      if (cell.getReviewDevice() != null) {
         String revDevId = cell.getReviewDevice().getValue();
         if (!formIds.contains(revDevId)) {
            storeFormInZip(zos, revDevId);
            formIds.add(revDevId);
         }
      }
   }

   protected void fixPageForms(WizardPageDefinition wizardPage, Map formsMap) {
      List forms = wizardPage.getAdditionalForms();
      List newForms = new ArrayList();
      for (Iterator i=forms.iterator();i.hasNext();) {
         String formId = (String) i.next();
         newForms.add(formsMap.get(formId));
      }
      wizardPage.setAdditionalForms(newForms);

      if (wizardPage.getEvaluationDevice() != null) {
         wizardPage.setEvaluationDevice(getIdManager().getId((String) formsMap.get(
               wizardPage.getEvaluationDevice().getValue())));
      }

      if (wizardPage.getReflectionDevice() != null) {
         wizardPage.setReflectionDevice(getIdManager().getId((String) formsMap.get(
               wizardPage.getReflectionDevice().getValue())));
      }

      if (wizardPage.getReviewDevice() != null) {
         wizardPage.setReviewDevice(getIdManager().getId((String) formsMap.get(
               wizardPage.getReviewDevice().getValue())));
      }
   }

   protected void storeFormInZip(ZipOutputStream zos, String formId) throws IOException {

      ZipEntry newfileEntry = new ZipEntry("forms/" + formId + ".form");

      zos.putNextEntry(newfileEntry);

      getStructuredArtifactDefinitionManager().packageFormForExport(formId, zos, false);

      zos.closeEntry();
   }

   protected void storeScaffoldingFile(ZipOutputStream zos, Id fileId) throws IOException {
      if (fileId == null) {
         return;
      }

      Node oldNode = getNode(fileId);

      String newName = oldNode.getName();

      storeFileInZip(zos, oldNode.getInputStream(),
         oldNode.getMimeType().getValue() + File.separator +
         fileId.getValue() + File.separator + newName);
   }
   
   protected void storeFileInZip(ZipOutputStream zos, InputStream in, String entryName)
      throws IOException {

      byte data[] = new byte[1024 * 10];
   
      if (File.separatorChar == '\\') {
         entryName = entryName.replace('\\', '/');
      }
   
      ZipEntry newfileEntry = new ZipEntry(entryName);
   
      zos.putNextEntry(newfileEntry);
   
      BufferedInputStream origin = new BufferedInputStream(in, data.length);
   
      int count;
      while ((count = origin.read(data, 0, data.length)) != -1) {
         zos.write(data, 0, count);
      }
      zos.closeEntry();
      in.close();
   }
   /*
   public Scaffolding uploadScaffolding(String scaffoldingFileName,
         String siteId, InputStream zipFileStream) throws IOException {
      try {
         return uploadScaffolding(scaffoldingFileName, SiteService.findTool(toolId).getId(), zipFileStream);
      }
      catch (InvalidUploadException exp) {
         throw exp;
      }
      catch (Exception exp) {
         throw new InvalidUploadException("Invalid scaffolding file.", exp, "uploadedScaffolding");
      }
   }
   */
   
   /**
    * This unpacks a zipped scaffolding and places it into the siteId. It saves the guidance, styles,
    * and forms, resets the ids, and saves the scaffolding.  It returns the new unpacked scaffolding.
    * 
    * The owner becomes the current agent.
    * 
    * @param siteId String of the site id
    * @param zis ZipInputStream of the packed scaffolding
    * @throws IOException
    */
   protected Scaffolding uploadScaffolding(String siteId, ZipInputStream zis)  throws IOException {
      
      ZipEntry currentEntry = zis.getNextEntry();
      Hashtable fileMap = new Hashtable();
      Scaffolding scaffolding = null;

      String tempDirName = getIdManager().createId().getValue();

      boolean itWorked = false;

      Map formsMap = new Hashtable();
      Map guidanceMap = null;
      Map styleMap = null;

      try {
         ContentCollectionEdit fileParent = getFileDir(tempDirName);
         boolean gotFile = false;
         while (currentEntry != null) {
            logger.debug("current entry name: " + currentEntry.getName());

            if (currentEntry.getName().equals("scaffolding")) {
               try {
                  scaffolding = processScaffolding(zis);
               } catch (ClassNotFoundException e) {
                  logger.error("Class not found loading scaffolding", e);
                  throw new OspException(e);
               }
            }
            else if (!currentEntry.isDirectory()) {
               if (currentEntry.getName().startsWith("forms/")) {
                  processMatrixForm(currentEntry, zis, formsMap,
                        getIdManager().getId(siteId));
               }
               else if (currentEntry.getName().equals("guidance/guidanceList")) {
                  gotFile = true;
                  guidanceMap = processMatrixGuidance(fileParent, siteId, zis);
               }
               else if (currentEntry.getName().equals("style/styleList")) {
                  gotFile = true;
                  styleMap = processMatrixStyle(fileParent, siteId, zis);
               }
            }

            zis.closeEntry();
            currentEntry = zis.getNextEntry();
         }
         if(scaffolding == null)
            throw new InvalidUploadException("The scaffolding file was not found in the import file");
         scaffolding.setId(null);
         scaffolding.setPublished(false);
         scaffolding.setPublishedBy(null);
         scaffolding.setPublishedDate(null);
         scaffolding.setOwner(getAuthnManager().getAgent());
         scaffolding.setWorksiteId(getIdManager().getId(siteId));
         
         resetIds(scaffolding, guidanceMap, formsMap, styleMap, siteId);

         scaffolding = saveNewScaffolding(scaffolding);         

         if (gotFile) {
            fileParent.getPropertiesEdit().addProperty(
                  ResourceProperties.PROP_DISPLAY_NAME, scaffolding.getTitle());
            getContentHosting().commitCollection(fileParent);
         }
         else {
            getContentHosting().cancelCollection(fileParent);
         }

         scaffolding = storeScaffolding(scaffolding);
         
         createEvaluatorAuthzForImport(scaffolding);
         
         itWorked = true;
         return scaffolding;
      }
      catch (Exception exp) {
         throw new RuntimeException(exp);
      }
      finally {
         try {
            zis.closeEntry();
         }
         catch (IOException e) {
            logger.error("", e);
         }
      }
   }

   public Scaffolding uploadScaffolding(Reference uploadedScaffoldingFile, String siteId)
         throws IOException {
      Node file = getNode(uploadedScaffoldingFile);

      ZipInputStream zis = new UncloseableZipInputStream(file.getInputStream());
      return uploadScaffolding(siteId, zis);
      
   }

   protected Map processMatrixGuidance(ContentCollection parent, String siteId,
                                       ZipInputStream zis) throws IOException {
      return getGuidanceManager().importGuidanceList(parent, siteId, zis);
   }
   
   protected Map processMatrixStyle(ContentCollection parent, String siteId,
         ZipInputStream zis) throws IOException {
      return getStyleManager().importStyleList(parent, siteId, zis);
}

   protected void processMatrixForm(ZipEntry currentEntry, ZipInputStream zis, Map formMap, Id worksite)
         throws IOException {
      File file = new File(currentEntry.getName());
      String fileName = file.getName();
      String oldId = fileName.substring(0, fileName.indexOf(".form"));

      StructuredArtifactDefinitionBean bean;
      
      try {
         //we want the bean even if it exists already
         bean = getStructuredArtifactDefinitionManager().importSad(
            worksite, zis, true, false, false);
      } catch(ImportException ie) {
         throw new RuntimeException("the structured artifact failed to import", ie);
      }

      formMap.put(oldId, bean.getId().getValue());
   }

   public void checkPageAccess(String id) {
      Id pageId = getIdManager().getId(id);
      WizardPage page = getWizardPage(pageId);
      // todo need to figure out matrix or wizard authz stuff here

      // this should set the security advisor for the attached artifacts.
      //getPageArtifacts(page);
      boolean canEval = getAuthzManager().isAuthorized(MatrixFunctionConstants.EVALUATE_MATRIX, 
            page.getPageDefinition().getId());
      boolean canReview = getAuthzManager().isAuthorized(MatrixFunctionConstants.REVIEW_MATRIX, 
            getIdManager().getId(page.getPageDefinition().getSiteId()));
      
      if (!canReview) {
         canReview = getAuthzManager().isAuthorized(WizardFunctionConstants.REVIEW_WIZARD, 
            getIdManager().getId(page.getPageDefinition().getSiteId()));
      }
      
      
      boolean owns = page.getOwner().getId().equals(getAuthnManager().getAgent().getId());
      
      if (canEval || canReview || owns) {
         //can I look at files? - own, review or eval
         getPageContents(page);
         
         //can I look at forms? - own, review or eval
         getPageForms(page);
         
         //can I look at reviews/evals/reflections? - own, review or eval
         getReviewManager().getReviewsByParentAndType(
               id, Review.REFLECTION_TYPE,
               page.getPageDefinition().getSiteId(),
               MatrixContentEntityProducer.MATRIX_PRODUCER);
      }
      
      if (canEval || owns) {
         //can I look at reviews/evals/reflections? - own or eval
         getReviewManager().getReviewsByParentAndType(
               id, Review.EVALUATION_TYPE,
               page.getPageDefinition().getSiteId(),
               MatrixContentEntityProducer.MATRIX_PRODUCER);
      }
      
      if (canEval || canReview || owns) {
         //can I look at reviews/evals/reflections? - own or review
         getReviewManager().getReviewsByParentAndType(
               id, Review.FEEDBACK_TYPE,
               page.getPageDefinition().getSiteId(),
               MatrixContentEntityProducer.MATRIX_PRODUCER);         
      }
   }

   /**
    * this creates authorizations for each cell from the evaluators contained in the cell
    * @param scaffolding
    */
   private void createEvaluatorAuthzForImport(Scaffolding scaffolding) {
      for (Iterator iter = scaffolding.getScaffoldingCells().iterator(); iter.hasNext();) {
         ScaffoldingCell sCell = (ScaffoldingCell) iter.next();
         Collection evals = sCell.getEvaluators();
         for (Iterator i = evals.iterator(); i.hasNext();) {
            Id id = (Id)i.next();
            if (id.getValue().startsWith("/site/")) {
               // it's a role
               String[] agentValues = id.getValue().split("/");
               
               String newStrId = id.getValue().replaceAll(agentValues[2], 
                     scaffolding.getWorksiteId().getValue());
               id = idManager.getId(newStrId);
            }
            Agent agent = this.getAgentFromId(id);

            if (agent != null  && agent.getId() != null) {
               this.getAuthzManager().createAuthorization(agent, 
                     MatrixFunctionConstants.EVALUATE_MATRIX, sCell.getWizardPageDefinition().getId());
            }
         }
      }
   }


   /**
    * gets the current user's resource collection
    * 
    * @return ContentCollection
    * @throws TypeException
    * @throws IdUnusedException
    * @throws PermissionException
    */
   protected ContentCollection getUserCollection() throws TypeException, IdUnusedException, PermissionException {
      User user = UserDirectoryService.getCurrentUser();
      String userId = user.getId();
      String wsId = SiteService.getUserSiteId(userId);
      String wsCollectionId = getContentHosting().getSiteCollection(wsId);
      ContentCollection collection = getContentHosting().getCollection(wsCollectionId);
      return collection;
   }
   
   /**
    * This gets the directory in which the import places files into.
    * 
    * This method gets the current users base collection, creates an imported directory,
    * then uses the param to create a new directory.
    * 
    * this uses the bean property importFolderName to name the
    * 
    * @param origName String
    * @return ContentCollectionEdit
    * @throws InconsistentException
    * @throws PermissionException
    * @throws IdUsedException
    * @throws IdInvalidException
    * @throws IdUnusedException
    * @throws TypeException
    */
   protected ContentCollectionEdit getFileDir(String origName) throws InconsistentException,
         PermissionException, IdUsedException, IdInvalidException, IdUnusedException, TypeException {
      ContentCollection userCollection = getUserCollection();
      
      try {
         //TODO use the bean org.theospi.portfolio.admin.model.IntegrationOption.siteOption 
         // in common/components to get the name and id for this site.
         
         ContentCollectionEdit groupCollection = getContentHosting().addCollection(userCollection.getId() + IMPORT_BASE_FOLDER_ID);
         groupCollection.getPropertiesEdit().addProperty(ResourceProperties.PROP_DISPLAY_NAME, getImportFolderName());
         getContentHosting().commitCollection(groupCollection);
      }
      catch (IdUsedException e) {
         // ignore... it is already there.
          if (logger.isDebugEnabled()) {
              logger.debug(e);
          } 
      }
      catch (Exception e) {
         throw new RuntimeException(e);
      }
      
      ContentCollection collection = getContentHosting().getCollection(userCollection.getId() + IMPORT_BASE_FOLDER_ID + "/");
      
      String childId = collection.getId() + origName;
      return getContentHosting().addCollection(childId);
   }

   /**
    * This unpacks the scaffolding in a zip stream and returns it
    * @param zis
    * @return
    * @throws IOException
    * @throws ClassNotFoundException
    */
   protected Scaffolding processScaffolding(ZipInputStream zis) throws IOException, ClassNotFoundException {
      XMLDecoder dec = new XMLDecoder(zis);
      return (Scaffolding)dec.readObject();
   }


   protected void processFile(ZipEntry currentEntry, ZipInputStream zis,
         Hashtable fileMap, ContentCollection fileParent) throws IOException, InconsistentException, PermissionException, IdUsedException, IdInvalidException, OverQuotaException, ServerOverloadException {
      File file = new File(currentEntry.getName());

      MimeType mimeType = new MimeType(file.getParentFile().getParentFile().getParentFile().getName(),
         file.getParentFile().getParentFile().getName());

      String contentType = mimeType.getValue();

      Id oldId = getIdManager().getId(file.getParentFile().getName());

      //Node newNode = fileParent.persistent().createFile(contentType, file.getName(), zis,
      //getFileHome().getType());
      ByteArrayOutputStream bos = new ByteArrayOutputStream();
      int c = zis.read();

      while (c != -1) {
         bos.write(c);
         c = zis.read();
      }

      String fileId = fileParent.getId() + file.getName();
      ContentResourceEdit resource = getContentHosting().addResource(fileId);
      ResourcePropertiesEdit resourceProperties =
            resource.getPropertiesEdit();
      resourceProperties.addProperty (ResourceProperties.PROP_DISPLAY_NAME, file.getName());
      resource.setContent(bos.toByteArray());
      resource.setContentType(contentType);
      getContentHosting().commitResource(resource);

      Id newId = getIdManager().getId(getContentHosting().getUuid(resource.getId()));

      fileMap.put(oldId, newId);
   }

   /**
    * resets the style, criteria, levels, and scaffolding cells
    * @param scaffolding
    * @param guidanceMap
    * @param formsMap
    * @param styleMap
    * @param siteId
    */
   protected void resetIds(Scaffolding scaffolding, Map guidanceMap, Map formsMap, Map styleMap, String siteId) {
      
      if (scaffolding.getStyle() != null) {
         if (styleMap != null) {
            scaffolding.setStyle((Style) styleMap.get(scaffolding.getStyle().getId().getValue()));
         }
         else {
            scaffolding.getStyle().setId(null);
         }
      }      
      
      substituteCriteria(scaffolding);
      substituteLevels(scaffolding);
      substituteScaffoldingCells(scaffolding, guidanceMap, formsMap, styleMap, siteId);
   }

   protected void substituteCriteria(Scaffolding scaffolding) {
      List newCriteria = new ArrayList();
      for (Iterator i=scaffolding.getCriteria().iterator(); i.hasNext();) {
         Criterion criterion = (Criterion)i.next();
         criterion.setId(null);
         newCriteria.add(criterion);
      }
      scaffolding.setCriteria(newCriteria);
   }
   
   protected void substituteLevels(Scaffolding scaffolding) {
      List newLevels = new ArrayList();
      for (Iterator i=scaffolding.getLevels().iterator(); i.hasNext();) {
         Level level = (Level)i.next();

         level.setId(null);
         newLevels.add(level);
      }
      scaffolding.setLevels(newLevels);
   }
   
   protected void substituteScaffoldingCells(Scaffolding scaffolding, Map guidanceMap, Map formsMap, Map styleMap, String siteId) {
      Set sCells = new HashSet(); 
      for (Iterator iter=scaffolding.getScaffoldingCells().iterator(); iter.hasNext();) {
         ScaffoldingCell scaffoldingCell = (ScaffoldingCell) iter.next();
         scaffoldingCell.setId(null);
         
         WizardPageDefinition wpd = scaffoldingCell.getWizardPageDefinition();
         wpd.setId(null);
         wpd.setSiteId(siteId);
         if (wpd.getGuidance() != null) {
            if (guidanceMap != null) {
               Guidance guidance = (Guidance) guidanceMap.get(wpd.getGuidance().getId().getValue());
               wpd.setNewId(getIdManager().createId());
               guidance.setSecurityQualifier(wpd.getNewId());
               wpd.setGuidance(guidance);
            }
            else {
               wpd.getGuidance().setId(null);
            }
         }
         
         if (wpd.getStyle() != null) {
            if (styleMap != null) {
               wpd.setStyle((Style) styleMap.get(wpd.getStyle().getId().getValue()));
            }
            else {
               wpd.getStyle().setId(null);
            }
         }

         fixPageForms(wpd, formsMap);

         Set newWorkflows = new HashSet();
         for (Iterator jiter=wpd.getEvalWorkflows().iterator(); jiter.hasNext();) {
            Workflow w = (Workflow)jiter.next();
            w.setId(null);
            Set newItems = new HashSet();
            for (Iterator kiter=w.getItems().iterator(); kiter.hasNext();) {
               WorkflowItem wfi = (WorkflowItem)kiter.next();
               wfi.setId(null);
               newItems.add(wfi);
            }
            
            newWorkflows.add(w);
         }
         
         //scaffoldingCell.getCells().clear();
         scaffoldingCell.setCells(new HashSet());
         sCells.add(scaffoldingCell);
         //scaffoldingCell.setScaffolding(scaffolding);
      }   
      scaffolding.setScaffoldingCells(sCells);
   }
   
   public void removeExposedMatrixTool(Scaffolding scaffolding) {
      String siteId = scaffolding.getWorksiteId().getValue();
      try {
         Site siteEdit = SiteService.getSite(siteId);

         SitePage page = siteEdit.getPage(scaffolding.getExposedPageId());
         siteEdit.removePage(page);
         SiteService.save(siteEdit);
         scaffolding.setExposedPageId(null);
      } catch (IdUnusedException e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
      } catch (PermissionException e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }
   }
   
   public void exposeMatrixTool(Scaffolding scaffolding) {
      //TODO add logging errors back
      String siteId = scaffolding.getWorksiteId().getValue();
      try {
         Site siteEdit = SiteService.getSite(siteId);


         SitePage page = siteEdit.addPage();

         page.setTitle(scaffolding.getTitle());
         page.setLayout(SitePage.LAYOUT_SINGLE_COL);

         ToolConfiguration tool = page.addTool();
         
         tool.setTool("osp.exposedmatrix", ToolManager.getTool("osp.exposedmatrix"));
         tool.setTitle(scaffolding.getTitle());
         tool.setLayoutHints("0,0");
         tool.getPlacementConfig().setProperty(MatrixManager.EXPOSED_MATRIX_KEY, scaffolding.getId().getValue());

         //LOG.info(this+": SiteService.commitEdit():" +siteId);

         SiteService.save(siteEdit);
         scaffolding.setExposedPageId(page.getId());


      } catch (IdUnusedException e) {
//       TODO Auto-generated catch block
         e.printStackTrace();
      } catch (PermissionException e) {
//       TODO Auto-generated catch block
         e.printStackTrace();
      }
   }

   private boolean findInAuthz(Id qualifier, Agent agent, List authzs) {
      for (Iterator iter = authzs.iterator(); iter.hasNext();) {
         Authorization authz = (Authorization) iter.next();
         // Same item, different agent
         if (!authz.getAgent().equals(agent) && authz.getQualifier().equals(qualifier))
            return true;
      }
      return false;
   }
   
   

   public boolean checkStyleConsumption(Id styleId) {
      //Check Scaffolding and WizardPageDef
      List scaffolding = getScaffoldingByStyle(styleId);
      if (scaffolding != null && !scaffolding.isEmpty() && scaffolding.size() > 0)
         return true;
      
      //Also check for WizardPageDef
      List wizPageDefs = this.getWizardPageDefByStyle(styleId);
      if (wizPageDefs != null && !wizPageDefs.isEmpty() && wizPageDefs.size() > 0)
         return true;
      
      return false;
   }

   public List getStyles(Id objectId) {
      WizardPage wp = getWizardPage(objectId);

      if (wp != null) {
         ScaffoldingCell sCell = getScaffoldingCellByWizardPageDef(
                     wp.getPageDefinition().getId());

         if (sCell != null) {
            List styles = new ArrayList();
            if (sCell.getScaffolding().getStyle() != null) {
               styles.add(sCell.getScaffolding().getStyle());
            }
            if (wp.getPageDefinition().getStyle() != null) {
               styles.add(wp.getPageDefinition().getStyle());
            }
            return styles;
         }
      }

      Matrix matrix = (Matrix) getHibernateTemplate().get(Matrix.class, objectId);

      if (matrix != null) {
         List styles = new ArrayList();
         if (matrix.getScaffolding().getStyle() != null) {
            styles.add(matrix.getScaffolding().getStyle());
         }
         return styles;
      }

      return null;
   }

   /* (non-Javadoc)
    * @see org.theospi.portfolio.shared.mgt.ReadableObjectHome#getType()
    */
   public org.sakaiproject.metaobj.shared.model.Type getType() {
      return new org.sakaiproject.metaobj.shared.model.Type(idManager.getId("matrix"), "Matrix");
      //return getMatrices(null, null);
   }

   public String getExternalType() {
      return getType().getId().getValue();
   }


   /* (non-Javadoc)
    * @see org.theospi.portfolio.shared.mgt.ReadableObjectHome#load(org.theospi.portfolio.shared.model.Id)
    */
   public Artifact load(Id id) {
      Matrix matrix = getMatrix(id);
      loadMatrixCellReviews(matrix);
      matrix.setHome(this);
      return matrix;
   }
   
   private void loadMatrixCellReviews(Matrix matrix) {
      for (Iterator cells = matrix.getCells().iterator(); cells.hasNext();) {
         Cell cell = (Cell) cells.next();
         WizardPage page = cell.getWizardPage();
         List reflections = new ArrayList();
         List evaluations = new ArrayList();
         List feedback = new ArrayList();

         List reviews = getReviewManager().getReviewsByParentAndTypes(page.getId().getValue(),
                 new int[]{Review.REFLECTION_TYPE, Review.EVALUATION_TYPE, Review.FEEDBACK_TYPE},
                 page.getPageDefinition().getSiteId(),
              MatrixContentEntityProducer.MATRIX_PRODUCER);


         for (Iterator reviewsIter = reviews.iterator(); reviewsIter.hasNext();) {
             Review review = (Review) reviewsIter.next();
             if (review.getType() == Review.EVALUATION_TYPE) {
                 evaluations.add(review);
             } else if (review.getType() == Review.REFLECTION_TYPE) {
                 reflections.add(review);
             } else if (review.getType() == Review.FEEDBACK_TYPE) {
                 feedback.add(review);
             }
         }

         page.setReflections(reflections);
         page.setEvaluations(evaluations);
         page.setFeedback(feedback);
         page.getAttachments().size();
         page.getPageForms().size();
         
         //Make sure that the attachments and forms have been added to the security advisor
         getPageContents(page);
         getPageForms(page);
      }
   }

   public Collection findByType(String type) {
      return getHibernateTemplate().find("from Matrix");
   }

   public boolean getLoadArtifacts() {
      return loadArtifacts;
   }

   public void setLoadArtifacts(boolean loadArtifacts) {
      this.loadArtifacts = loadArtifacts;
   }

   /* (non-Javadoc)
    * @see org.theospi.portfolio.shared.mgt.ReadableObjectHome#createInstance()
    */
   public Artifact createInstance() {
      Artifact instance = new Matrix();
      prepareInstance(instance);
      return instance;
   }

   /* (non-Javadoc)
    * @see org.theospi.portfolio.shared.mgt.ReadableObjectHome#prepareInstance(org.theospi.portfolio.shared.model.Artifact)
    */
   public void prepareInstance(Artifact object) {
      object.setHome(this);
      //((Matrix) object).setAgentManager(getAgentManager());

   }

   /* (non-Javadoc)
    * @see org.theospi.portfolio.shared.mgt.ReadableObjectHome#createSample()
    */
   public Artifact createSample() {
      return createInstance();
   }

   /* (non-Javadoc)
    * @see org.theospi.portfolio.shared.mgt.ReadableObjectHome#findByOwner(org.theospi.portfolio.shared.model.Agent)
    */
   public Collection findByOwner(Agent owner) throws FinderException {
      return getMatrices(null, owner.getId());
   }

   /* (non-Javadoc)
    * @see org.theospi.portfolio.shared.mgt.ReadableObjectHome#isInstance(org.theospi.portfolio.shared.model.Artifact)
    */
   public boolean isInstance(Artifact testObject) {
      return (testObject instanceof Matrix);
   }

   /* (non-Javadoc)
    * @see org.theospi.portfolio.shared.mgt.ReadableObjectHome#refresh()
    */
   public void refresh() {
      // TODO Auto-generated method stub

   }

   public String getExternalUri(Id artifactId, String name) {
      throw new UnsupportedOperationException();
   }

   public InputStream getStream(Id artifactId) {
      throw new UnsupportedOperationException();
   }

   public boolean isSystemOnly() {
      return false;
   }

   public Class getInterface() {
      return this.getClass();
   }

   /* (non-Javadoc)
    * @see org.theospi.portfolio.repository.ArtifactFinder#findByOwnerAndType(org.theospi.portfolio.shared.model.Id, java.lang.String)
    */
   public Collection findByOwnerAndType(Id owner, String type) {
      return findByOwner(owner);
   }

   public Collection findByOwnerAndType(Id owner, String type, MimeType mimeType) {
      // not gonna find mime types
      return null;
   }

   /* (non-Javadoc)
    * @see org.theospi.portfolio.repository.ArtifactFinder#findByOwner(org.theospi.portfolio.shared.model.Id)
    */
   public Collection findByOwner(Id owner) {
      try {
         return this.findByOwner(agentManager.getAgent(owner));
      } catch (FinderException e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
         return null;
      }
   }

   public Collection findByWorksiteAndType(Id worksiteId, String type) {
      //TODO implement this
      return new ArrayList();
   }

   public Collection findByWorksite(Id worksiteId) {
      //TODO implement this
      return new ArrayList();
   }
   
   public Element getArtifactAsXml(Artifact artifact) {
      return getXmlRenderer().getArtifactAsXml(artifact);
   }


   /**
    * @return Returns the worksiteManager.
    */
   public WorksiteManager getWorksiteManager() {
      return worksiteManager;
   }
   /**
    * @param worksiteManager The worksiteManager to set.
    */
   public void setWorksiteManager(WorksiteManager worksiteManager) {
      this.worksiteManager = worksiteManager;
   }
   
   public LockManager getLockManager() {
      return lockManager;
   }
   public void setLockManager(LockManager lockManager) {
      this.lockManager = lockManager;
   }

   /**
    * This is called by the download manager to package a scaffolding for download as zip
    * @param params Map of url parameters
    * @param out  OutputStream to push the file
    */
   public String packageForDownload(Map params, OutputStream out) throws IOException {
      packageScffoldingForExport(
         getIdManager().getId(((String[])params.get(SCAFFOLDING_ID_TAG))[0]),
         out);
      
      //Blank filename for now -- no more dangerous, since the request is in the form of a filename
      return "";      
   }

   /**
    * This is the method called when duplicating a site.  It copies all published scaffolding
    * from the fromContext site id to the toContext site id
    * @param fromContext   String  from site id
    * @param toContext     String  to site id
    * @param resourceIds   List
    */
   public void importResources(String fromContext, String toContext, List resourceIds) {      
      ClassLoader currentLoader = Thread.currentThread().getContextClassLoader();
      try {
         Thread.currentThread().setContextClassLoader(getClass().getClassLoader());


         List scaffolding = this.findPublishedScaffolding(fromContext);
         if (scaffolding == null) {
            return;
         }
         
         for (Iterator iter = scaffolding.iterator(); iter.hasNext();) {
            Scaffolding scaffold = (Scaffolding)iter.next();
            Id id = scaffold.getId();
   
            getHibernateTemplate().evict(scaffold);

            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            
            packageScffoldingForExport(id, bos);
            
            InputStream is = new ByteArrayInputStream(bos.toByteArray());
            ZipInputStream zis = new UncloseableZipInputStream(is);
            bos = null;
            
            uploadScaffolding(toContext, zis);
            is = null;
            zis = null;
         }
      } catch (IOException e) {
         logger.error("", e);
         throw new OspException(e);
      }
      finally {
         Thread.currentThread().setContextClassLoader(currentLoader);
      }
      
   }

   public ContentHostingService getContentHosting() {
      return contentHosting;
   }

   public void setContentHosting(ContentHostingService contentHosting) {
      this.contentHosting = contentHosting;
   }

   public PresentableObjectHome getXmlRenderer() {
      return xmlRenderer;
   }

   public void setXmlRenderer(PresentableObjectHome xmlRenderer) {
      this.xmlRenderer = xmlRenderer;
   }

   public SecurityService getSecurityService() {
      return securityService;
   }

   public void setSecurityService(SecurityService securityService) {
      this.securityService = securityService;
   }

   public EntityContextFinder getContentFinder() {
      return contentFinder;
   }

   public void setContentFinder(EntityContextFinder contentFinder) {
      this.contentFinder = contentFinder;
   }
   
   protected String buildRef(String siteId, String contextId, ContentResource resource) {
      return ContentEntityUtil.getInstance().buildRef(
            MatrixContentEntityProducer.MATRIX_PRODUCER, siteId, contextId, resource.getReference());
   }

   public DefaultScaffoldingBean getDefaultScaffoldingBean() {
      return defaultScaffoldingBean;
   }

   public void setDefaultScaffoldingBean(
         DefaultScaffoldingBean defaultScaffoldingBean) {
      this.defaultScaffoldingBean = defaultScaffoldingBean;
   }
   
   public void processWorkflow(Id workflowId, Id pageId) {
      Workflow workflow = getWorkflowManager().getWorkflow(workflowId);
      //Cell cell = getCellFromPage(pageId);
      WizardPage page = getWizardPage(pageId);
      
      Collection items = workflow.getItems();
      for (Iterator i = items.iterator(); i.hasNext();) {
         WorkflowItem wi = (WorkflowItem)i.next();
         //Cell actionCell = this.getMatrixCellByWizardPageDef(cell.getMatrix(), 
         //      wi.getActionObjectId());
         switch (wi.getActionType()) {
            // complete / return part 2
            case(WorkflowItem.STATUS_CHANGE_WORKFLOW):
               processStatusChangeWorkflow(wi, page);
               break;
            
            case(WorkflowItem.NOTIFICATION_WORKFLOW):
               processNotificationWorkflow(wi);
               break;
               
            // Return part 1
            case(WorkflowItem.CONTENT_LOCKING_WORKFLOW):
               processContentLockingWorkflow(wi, page);
               break;
         } // end processWorkflow
      } // end items.iterator
      storePage(page);
   } // end processWorkflow
   
   public void processWorkflow(int workflowOption, Id cellId) {
      Cell cell = getCell(cellId);
      WizardPage page = cell.getWizardPage();
      Date now = new Date(System.currentTimeMillis());

      //Actions for "next" cell
      if (workflowOption == Scaffolding.HORIZONTAL_PROGRESSION || 
            workflowOption == Scaffolding.VERTICAL_PROGRESSION) {

         Cell actionCell = getNextCell(cell, workflowOption);
         //If action cell is null, that means we are at the end of the row/column and have no next cell.
         if (actionCell != null) {
            WizardPage actionPage = actionCell.getWizardPage();
            if (actionPage != null) {               
               processContentLockingWorkflow(false, actionPage);
               processStatusChangeWorkflow(MatrixFunctionConstants.READY_STATUS, actionPage);
               page.setModified(now);
            }             
         }
      }
   }

   /**
    * This method locks and unlocks the page file attachments, the additional filled in forms,
    * and the filled in reflection forms.  There is other locking in the ReviewHelperController 
    * which locks the feedback and evaluation.
    * @param lock boolean true locks the resources, false unlocks
    * @param page WizardPage of the content to lock
    */
   private void processContentLockingWorkflow(boolean lock, WizardPage page) {
      for (Iterator iter = page.getAttachments().iterator(); iter.hasNext();) {
         Attachment att = (Attachment)iter.next();
         if (lock) {
            getLockManager().lockObject(att.getArtifactId().getValue(), 
                  page.getId().getValue(), 
                  "Submitting cell, 4 eval", true);
         }
         else {
            getLockManager().removeLock(att.getArtifactId().getValue(), 
                  page.getId().getValue());
         }         
      }
      
      //the expectations, additional forms
      for (Iterator iter = page.getPageForms().iterator(); iter.hasNext();) {
         WizardPageForm pageForm = (WizardPageForm)iter.next();
         
         if (lock) {
            getLockManager().lockObject(pageForm.getArtifactId().getValue(), 
                  page.getId().getValue(), 
                  "Submitting cell, 4 eval", true);
         }
         else {
            getLockManager().removeLock(pageForm.getArtifactId().getValue(), 
                  page.getId().getValue());
         }         
      }

      //the reflections
      List reflections = getReviewManager().getReviewsByParentAndType(
            page.getId().getValue(), Review.REFLECTION_TYPE,
            page.getPageDefinition().getSiteId(),
            MatrixContentEntityProducer.MATRIX_PRODUCER);
      for (Iterator iter = reflections.iterator(); iter.hasNext();) {
         Review review = (Review)iter.next();
         
         if (lock) {
            getLockManager().lockObject(review.getReviewContent().getValue(), 
                  page.getId().getValue(), 
                  "Submitting cell, 4 eval", true);
         }
         else {
            getLockManager().removeLock(review.getReviewContent().getValue(), 
                  page.getId().getValue());
         }         
      } 
   }

   private void processContentLockingWorkflow(WorkflowItem wi, WizardPage page) {
      processContentLockingWorkflow(wi.getActionValue().equals(WorkflowItem.CONTENT_LOCKING_LOCK), page);     
   }

   private void processNotificationWorkflow(WorkflowItem wi) {
      // TODO implement
      
   }

   private void processStatusChangeWorkflow(String status, WizardPage page) {
      Date now = new Date(System.currentTimeMillis());
      page.setStatus(status);
      page.setModified(now);
   }
   
   private void processStatusChangeWorkflow(WorkflowItem wi, WizardPage page) {
      processStatusChangeWorkflow(wi.getActionValue(), page);
   }

   /**
    * @return Returns the workflowManager.
    */
   public WorkflowManager getWorkflowManager() {
      return workflowManager;
   }

   /**
    * @param workflowManager The workflowManager to set.
    */
   public void setWorkflowManager(WorkflowManager workflowManager) {
      this.workflowManager = workflowManager;
   }

   public StructuredArtifactDefinitionManager getStructuredArtifactDefinitionManager() {
      return structuredArtifactDefinitionManager;
   }

   public void setStructuredArtifactDefinitionManager(StructuredArtifactDefinitionManager structuredArtifactDefinitionManager) {
      this.structuredArtifactDefinitionManager = structuredArtifactDefinitionManager;
   }

   public GuidanceManager getGuidanceManager() {
      return guidanceManager;
   }

   public void setGuidanceManager(GuidanceManager guidanceManager) {
      this.guidanceManager = guidanceManager;
   }

   public ReviewManager getReviewManager() {
      return reviewManager;
   }

   public void setReviewManager(ReviewManager reviewManager) {
      this.reviewManager = reviewManager;
   }

   public StyleManager getStyleManager() {
      return styleManager;
   }

   public void setStyleManager(StyleManager styleManager) {
      this.styleManager = styleManager;
   }
   
   public String getImportFolderName() {
      return importFolderName;
   }

   public void setImportFolderName(String importFolderName) {
      this.importFolderName = importFolderName;
   }

   /**
    * @return the useExperimentalMatrix
    */
   public boolean isUseExperimentalMatrix() {
      return useExperimentalMatrix;
   }

   /**
    * @param useExperimentalMatrix the useExperimentalMatrix to set
    */
   public void setUseExperimentalMatrix(boolean useExperimentalMatrix) {
      this.useExperimentalMatrix = useExperimentalMatrix;
   }

   public boolean checkFormConsumption(Id formId) {
      Collection objectsWithForms = getHibernateTemplate().find("from ObjectWithWorkflow where " +
         "reflection_device_id = ? or evaluation_device_id = ? or review_device_id = ?",
         new Object[] {formId.getValue(), formId.getValue(), formId.getValue()});

      if (objectsWithForms.size() > 0) {
         return true;
      }

      String queryString = "from WizardPageDefinition as wpd left join wpd.additionalForms as af where " +
         "af = ?";
      Collection additionalForms = getHibernateTemplate().find(queryString,
         new Object[] {formId.getValue()});

      return additionalForms.size() > 0;
   }

   /**
    * {@inheritDoc}
    */
   public Collection<FormConsumptionDetail> getFormConsumptionDetails(Id formId) {
      Collection results = new ArrayList();
      Map<String, String> siteMap = new HashMap<String, String>();
      Map<String, String> scaffoldingMap = new HashMap<String, String>();

      String refl_type = messages.getString("reflection_device");
      String eval_type = messages.getString("evaluation_device");
      String review_type = messages.getString("review_device");
      String page_form = messages.getString("page_form");
      
      String cellNameText = messages.getString("cell_name_text");
      String matrixNameText = messages.getString("matrix_name_text");
      String wizPageNameText = messages.getString("wiz_page_name_text");
      String wizardNameText = messages.getString("wiz_name_text");
      
      String matrixReflection = "select new org.sakaiproject.metaobj.shared.model.FormConsumptionDetail(" +
      		   "sc.wizardPageDefinition.reflectionDevice, " +
      		   "sc.scaffolding.worksiteId, " +
      		   "'" + refl_type + "', " +
      		   "concat('" + cellNameText + "', sc.wizardPageDefinition.title), " +
      		   "concat('" + matrixNameText + "', sc.scaffolding.title)) " +
            "from ScaffoldingCell sc " +
            "where sc.wizardPageDefinition.reflectionDevice = :formId ";
      String matrixEval = "select new org.sakaiproject.metaobj.shared.model.FormConsumptionDetail(" +
      		   "sc.wizardPageDefinition.evaluationDevice, " +
      		   "sc.scaffolding.worksiteId, " +
      		   "'" + eval_type + "', " +
      		   "concat('" + cellNameText + "', sc.wizardPageDefinition.title), " +
      		   "concat('" + matrixNameText + "', sc.scaffolding.title)) " +
            "From ScaffoldingCell sc " +
            "where sc.wizardPageDefinition.evaluationDevice = :formId ";
      String matrixReview = "select new org.sakaiproject.metaobj.shared.model.FormConsumptionDetail(" +
      		   "sc.wizardPageDefinition.reviewDevice, " +
      		   "sc.scaffolding.worksiteId, " +
      		   "'" + review_type + "', " +
      		   "concat('" + cellNameText + "', sc.wizardPageDefinition.title), " +
      		   "concat('" + matrixNameText + "', sc.scaffolding.title)) " +
            "From ScaffoldingCell sc " +
            "where sc.wizardPageDefinition.reviewDevice = :formId ";

      String wizardPageReflection = "select new org.sakaiproject.metaobj.shared.model.FormConsumptionDetail(" +
      		   "wpd.reflectionDevice, " +
      		   "w.siteId, " +
      		   "'" + refl_type + "', " +
      		   "concat('" + wizPageNameText + "', wpd.title), " +
      		   "concat('" + wizardNameText + "', w.name)) " +
            "From WizardPageSequence wps " +
            "join wps.wizardPageDefinition wpd " +
            "join wps.category c " +
            "join c.wizard w " +
            "where wpd.reflectionDevice = :formId ";
      String wizardPageEval = "select new org.sakaiproject.metaobj.shared.model.FormConsumptionDetail(" +
      		   "wpd.evaluationDevice, " +
      		   "w.siteId, " +
      		   "'" + eval_type + "', " +
      		   "concat('" + wizPageNameText + "', wpd.title), " +
      		   "concat('" + wizardNameText + "', w.name)) " +
            "From WizardPageSequence wps " +
            "join wps.wizardPageDefinition wpd " +
            "join wps.category c " +
            "join c.wizard w " +
            "where wpd.evaluationDevice = :formId ";
      String wizardPageReview ="select new org.sakaiproject.metaobj.shared.model.FormConsumptionDetail(" +
      		   "wpd.reviewDevice, " +
      		   "w.siteId, " +
      		   "'" + review_type + "', " +
      		   "concat('" + wizPageNameText + "', wpd.title), " +
      		   "concat('" + wizardNameText + "', w.name)) " +
            "From WizardPageSequence wps " +
            "join wps.wizardPageDefinition wpd " +
            "join wps.category c " +
            "join c.wizard w " +
            "where wpd.reviewDevice = :formId ";

      String wizardReflection = "select new org.sakaiproject.metaobj.shared.model.FormConsumptionDetail(" +
      		   "w.reflectionDevice, " +
      		   "w.siteId, " +
      		   "'" + refl_type + "', " +
      		   "concat('" + wizardNameText + "', w.name)) " +
            "from Wizard w " + 
            "where w.reflectionDevice = :formId ";
      String wizardEvalation = "select new org.sakaiproject.metaobj.shared.model.FormConsumptionDetail(" +
      		   "w.evaluationDevice, " +
      		   "w.siteId, " +
      		   "'" + eval_type + "', " +
      		   "concat('" + wizardNameText + "', w.name)) " +
            "from Wizard w " +
            "where w.evaluationDevice = :formId ";
      String wizardReview = "select new org.sakaiproject.metaobj.shared.model.FormConsumptionDetail(" +
      		   "w.reviewDevice, " +
      		   "w.siteId, " +
      		   "'" + review_type + "', " +
      		   "concat('" + wizardNameText + "', w.name)) " +
            "from Wizard w " +
            "where w.reviewDevice = :formId";
      
      Collection objectsWithForms = getHibernateTemplate().findByNamedParam(matrixReflection, "formId", formId);
      objectsWithForms.addAll(getHibernateTemplate().findByNamedParam(matrixEval, "formId", formId));
      objectsWithForms.addAll(getHibernateTemplate().findByNamedParam(matrixReview, "formId", formId));
      objectsWithForms.addAll(getHibernateTemplate().findByNamedParam(wizardPageReflection, "formId", formId));
      objectsWithForms.addAll(getHibernateTemplate().findByNamedParam(wizardPageEval, "formId", formId));
      objectsWithForms.addAll(getHibernateTemplate().findByNamedParam(wizardPageReview, "formId", formId));
      objectsWithForms.addAll(getHibernateTemplate().findByNamedParam(wizardReflection, "formId", formId));
      objectsWithForms.addAll(getHibernateTemplate().findByNamedParam(wizardEvalation, "formId", formId));
      objectsWithForms.addAll(getHibernateTemplate().findByNamedParam(wizardReview, "formId", formId));
      
		results.addAll(objectsWithForms);
      
      String cellQueryString = "select new org.sakaiproject.metaobj.shared.model.FormConsumptionDetail(" +
      		   "af, " +
      		   "sc.scaffolding.worksiteId, " +
      		   "'" + page_form + "', " +
      		   "concat('" + cellNameText + "', sc.wizardPageDefinition.title), " +
               "concat('" + matrixNameText + "', sc.scaffolding.title)) " +
      		"from ScaffoldingCell sc " +
      		"left join sc.wizardPageDefinition.additionalForms as af " +
      		"where af = :formId";
      Collection cellAdditionalForms = getHibernateTemplate().findByNamedParam(
            cellQueryString, "formId", formId.getValue());
      results.addAll(cellAdditionalForms);
      
      String wizPageQueryString = "select new org.sakaiproject.metaobj.shared.model.FormConsumptionDetail(" +
      		   "af, " +
      		   "w.siteId, " +
      		   "'" + page_form + "', " +
      		   "concat('" + wizPageNameText + "', wpd.title), " +
      		   "concat('" + wizardNameText + "', w.name)) " +
      		"From WizardPageSequence wps " +
            "join wps.wizardPageDefinition wpd " +
            "join wps.category c " +
            "join c.wizard w " +
            "join wpd.additionalForms af " +
            "where af = :formId";
      Collection wizPageAdditionalForms = getHibernateTemplate().findByNamedParam(
            wizPageQueryString, "formId", formId.getValue());
      results.addAll(wizPageAdditionalForms);


      return results;
   }

   public EventService getEventService() {
	   return eventService;
   }

   public void setEventService(EventService eventService) {
	   this.eventService = eventService;
   }
}

/*
 * The Open Source Portfolio Initiative Software is Licensed under the Educational Community License Version 1.0:
 *
 * This Educational Community License (the "License") applies to any original work of authorship
 * (the "Original Work") whose owner (the "Licensor") has placed the following notice immediately
 * following the copyright notice for the Original Work:
 *
 * Copyright (c) 2004 Trustees of Indiana University and r-smart Corporation
 *
 * This Original Work, including software, source code, documents, or other related items, is being
 * provided by the copyright holder(s) subject to the terms of the Educational Community License.
 * By obtaining, using and/or copying this Original Work, you agree that you have read, understand,
 * and will comply with the following terms and conditions of the Educational Community License:
 *
 * Permission to use, copy, modify, merge, publish, distribute, and sublicense this Original Work and
 * its documentation, with or without modification, for any purpose, and without fee or royalty to the
 * copyright holder(s) is hereby granted, provided that you include the following on ALL copies of the
 * Original Work or portions thereof, including modifications or derivatives, that you make:
 *
 * - The full text of the Educational Community License in a location viewable to users of the
 * redistributed or derivative work.
 *
 * - Any pre-existing intellectual property disclaimers, notices, or terms and conditions.
 *
 * - Notice of any changes or modifications to the Original Work, including the date the changes were made.
 *
 * - Any modifications of the Original Work must be distributed in such a manner as to avoid any confusion
 *  with the Original Work of the copyright holders.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
 * SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 * The name and trademarks of copyright holder(s) may NOT be used in advertising or publicity pertaining
 * to the Original or Derivative Works without specific, written prior permission. Title to copyright
 * in the Original Work and any associated documentation will at all times remain with the copyright holders.
 *
 * $Header: /opt/CVS/osp2.x/matrix/api-impl/src/java/org/theospi/portfolio/matrix/HibernateMatrixManagerImpl.java,v 1.16 2005/10/26 23:53:01 jellis Exp $
 * $Revision$
 * $Date$
 */
/*
 * Created on May 21, 2004
 *
 */
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
import java.util.*;
import java.util.zip.Adler32;
import java.util.zip.CheckedOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import net.sf.hibernate.AssertionFailure;
import net.sf.hibernate.Criteria;
import net.sf.hibernate.FetchMode;
import net.sf.hibernate.Hibernate;
import net.sf.hibernate.HibernateException;
import net.sf.hibernate.Session;
import net.sf.hibernate.expression.Expression;
import net.sf.hibernate.type.Type;

import org.jdom.Element;
import org.sakaiproject.metaobj.security.AuthenticationManager;
import org.sakaiproject.metaobj.shared.ArtifactFinder;
import org.sakaiproject.metaobj.shared.IdType;
import org.sakaiproject.metaobj.shared.mgt.AgentManager;
import org.sakaiproject.metaobj.shared.mgt.IdManager;
import org.sakaiproject.metaobj.shared.mgt.PresentableObjectHome;
import org.sakaiproject.metaobj.shared.mgt.ReadableObjectHome;
import org.sakaiproject.metaobj.shared.model.*;
import org.sakaiproject.metaobj.worksite.mgt.WorksiteManager;
import org.sakaiproject.service.framework.portal.cover.PortalService;
import org.sakaiproject.service.legacy.content.*;
import org.sakaiproject.service.legacy.entity.*;
import org.sakaiproject.service.legacy.site.ToolConfiguration;
import org.sakaiproject.service.legacy.site.cover.SiteService;
import org.sakaiproject.service.legacy.user.User;
import org.sakaiproject.service.legacy.user.cover.UserDirectoryService;
import org.sakaiproject.service.legacy.resource.DuplicatableToolService;
import org.sakaiproject.service.legacy.security.SecurityService;
import org.sakaiproject.exception.*;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.orm.hibernate.HibernateCallback;
import org.springframework.orm.hibernate.support.HibernateDaoSupport;
import org.theospi.portfolio.matrix.model.Attachment;
import org.theospi.portfolio.matrix.model.AttachmentCriterion;
import org.theospi.portfolio.matrix.model.Cell;
import org.theospi.portfolio.matrix.model.Criterion;
import org.theospi.portfolio.matrix.model.Expectation;
import org.theospi.portfolio.matrix.model.Level;
import org.theospi.portfolio.matrix.model.Matrix;
import org.theospi.portfolio.matrix.model.MatrixTool;
import org.theospi.portfolio.matrix.model.Reflection;
import org.theospi.portfolio.matrix.model.ReviewRubricValue;
import org.theospi.portfolio.matrix.model.ReviewerItem;
import org.theospi.portfolio.matrix.model.Rubric;
import org.theospi.portfolio.matrix.model.RubricSatisfactionBean;
import org.theospi.portfolio.matrix.model.Scaffolding;
import org.theospi.portfolio.matrix.model.ScaffoldingCell;
import org.theospi.portfolio.matrix.model.impl.MatrixContentEntityProducer;
import org.theospi.portfolio.security.Authorization;
import org.theospi.portfolio.security.AuthorizationFacade;
import org.theospi.portfolio.security.AllowMapSecurityAdvisor;
import org.theospi.portfolio.security.AuthorizationFailedException;
import org.theospi.portfolio.shared.model.Node;
import org.theospi.portfolio.shared.model.OspException;
import org.theospi.portfolio.shared.intf.DownloadableManager;
import org.theospi.portfolio.shared.intf.EntityContextFinder;
import org.theospi.portfolio.shared.mgt.ContentEntityWrapper;
import org.theospi.utils.zip.UncloseableZipInputStream;

/**
 * @author rpembry
 */
public class HibernateMatrixManagerImpl extends HibernateDaoSupport
   implements MatrixManager, ReadableObjectHome, ArtifactFinder, DownloadableManager,
   PresentableObjectHome, DuplicatableToolService {

   private IdManager idManager;
   private AuthenticationManager authnManager = null;
   private AuthorizationFacade authzManager = null;
   private AgentManager agentManager = null;
   private PresentableObjectHome xmlRenderer;
   private WorksiteManager worksiteManager;
   //private WritableObjectHome fileHome;
   private LockManager lockManager;
   private boolean loadArtifacts = true;
   private List reviewRubrics = new ArrayList();
   private ContentHostingService contentHosting = null;
   private SecurityService securityService;

   private static final String SCAFFOLDING_ID_TAG = "scaffoldingId";
   private EntityContextFinder contentFinder = null;

   /**
    * All the criteria for a given Cell
    *
    * @param cell
    * @return
    */
   public List getCellCriteria(Cell cell) {
      Criterion rootCriterion = cell.getScaffoldingCell().getRootCriterion();
      Integer zero = new Integer(0);
      ArrayList result = new ArrayList();
      Iterator iter = cell.getMatrix().getMatrixTool().getScaffolding().getCriteria().iterator();
      boolean adding = false;
      while (iter.hasNext()) {
         Criterion next = (Criterion) iter.next();
         if (next.getIndent().equals(zero)) {
            if (adding) break;
            if (next.equals(rootCriterion)) adding = true;
         }
         if (adding) result.add(next);
      }
      return result;
   }

   public List getCellCriteria(Id cellId) {
      return getCellCriteria(getCell(cellId));
   }
   
   public List getCellsByScaffoldingCell(Id scaffoldingCellId) {
      List list = getHibernateTemplate().find("from Cell cell where cell.scaffoldingCell.id=?", scaffoldingCellId.getValue());
      return list;
   }
   
   public List getMatrices(Id matrixToolId, Id agentId) {
      String toolId;
      String ownerId;

      if (matrixToolId == null)
         toolId = "%";
      else
         toolId = matrixToolId.getValue();

      if (agentId == null)
         ownerId = "%";
      else
         ownerId = agentId.getValue();

      Object[] params = new Object[]{toolId, ownerId};
      //TODO move this into a callback
      getHibernateTemplate().setCacheQueries(true);

      List list = getHibernateTemplate().find("from Matrix matrix where matrix.matrixTool like ? and matrix.owner like ?", params);

      return list;
   }

   public Matrix getMatrix(Id matrixToolId, Id agentId) {
      List list = getMatrices(matrixToolId, agentId);

      if (list.size() > 0)
         return (Matrix) list.get(0);
      else
         return null;
   }
   
   public List getCells(Matrix matrix) {
      getHibernateTemplate().setCacheQueries(true);
      return getHibernateTemplate().find("from Cell cell where cell.matrix.id=?",
            matrix.getId().getValue());
      
   }

   public Cell getCell(Matrix matrix, Criterion rootCriterion, Level level) {
      //TODO should be something easier for this HQL
      
      Object[] params = new Object[]{matrix.getId().getValue(),
                                     rootCriterion.getId().getValue(), level.getId().getValue()};
      getHibernateTemplate().setCacheQueries(true);
      List list = getHibernateTemplate()
            .find("from Cell cell where cell.matrix.id=? and cell.scaffoldingCell.rootCriterion.id=? and cell.scaffoldingCell.level.id=?",
                  params);
      return (Cell) list.get(0);
   }

   public void unlockNextCell(Cell cell) {
      Matrix matrix = cell.getMatrix();
      List levels = matrix.getMatrixTool().getScaffolding().getLevels();
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

   public Criterion getCriterion(Id criterionId) {
      return (Criterion) this.getHibernateTemplate().load(Criterion.class, criterionId);
   }

   public Level getLevel(Id levelId) {
      return (Level) this.getHibernateTemplate().load(Level.class, levelId);
   }
   
   public Cell getCell(Id cellId) {
      //return (Cell) this.getHibernateTemplate().load(Cell.class, cellId);
      Cell cell = (Cell) this.getHibernateTemplate().get(Cell.class, cellId);

      Scaffolding scaffolding = cell.getScaffoldingCell().getScaffolding();
      Id xsdId = scaffolding.getPrivacyXsdId();

      if (xsdId != null) {
         String propPage = getContentHosting().resolveUuid(xsdId.getValue());
         getSecurityService().pushAdvisor(
            new AllowMapSecurityAdvisor(ContentHostingService.EVENT_RESOURCE_READ,
                  getContentHosting().getReference(propPage)));
      }

      return cell;
   }

   public Id storeMatrixTool(MatrixTool matrixTool) {
      this.store(matrixTool);
      return matrixTool.getId();
   }
   
   public Id storeCell(Cell cell) {
      this.getHibernateTemplate().saveOrUpdate(cell);
      return cell.getId();
   }

   public void publishScaffolding(Id scaffoldingId) {
      Scaffolding scaffolding = this.getScaffolding(scaffoldingId);
      scaffolding.setPublished(true);
      scaffolding.setPublishedBy(authnManager.getAgent());
      scaffolding.setPublishedDate(new Date(System.currentTimeMillis()));
      this.storeScaffolding(scaffolding);

   }
   public Id storeScaffolding(Scaffolding scaffolding) {
      this.store(scaffolding);
      getHibernateTemplate().flush();
      return scaffolding.getId();
   }
   
   public Id storeScaffoldingCell(ScaffoldingCell scaffoldingCell) {
      this.store(scaffoldingCell);
      return scaffoldingCell.getId();
   }

   public void store(final Object obj) {
      this.getHibernateTemplate().saveOrUpdateCopy(obj);
   }

   public MatrixTool createMatrixTool(String toolId, Scaffolding scaffolding) {
      MatrixTool matrixTool = new MatrixTool(idManager.getId(toolId), 
            scaffolding);

      this.getHibernateTemplate().save(matrixTool);
      return matrixTool;
   }

   public Matrix createMatrix(Agent owner, MatrixTool matrixTool) {
      Matrix matrix = new Matrix();
      matrix.setOwner(owner);
      matrix.setMatrixTool(matrixTool);
      Scaffolding scaffolding = matrixTool.getScaffolding();

      List levels = scaffolding.getLevels();
      List criteria = scaffolding.getRootCriteria();

      Criterion criterion = null;
      Level level = null;

      for (Iterator criteriaIterator = criteria.iterator(); criteriaIterator.hasNext();) {
         criterion = (Criterion) criteriaIterator.next();

         for (Iterator levelsIterator = levels.iterator(); levelsIterator.hasNext();) {
            level = (Level) levelsIterator.next();

            Cell cell = new Cell();
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

   public Attachment attachArtifact(Id cellId, String[] criteriaId, Reference artifactRef,
                                    ElementBean elementBean) {
      Id artifactId = convertRef(artifactRef);
      detachArtifact(cellId, artifactId);
      Cell cell = getCell(cellId);
      Attachment attachment = new Attachment();
      attachment.setArtifactId(artifactId);
      attachment.setCell(cell);
      attachment.setPrivacyResponse(elementBean);

      Set attCrit = new HashSet();
      for (int i = 0; i < criteriaId.length; i++) {
         AttachmentCriterion ac = new AttachmentCriterion();
         ac.setCriterion(this.getCriterion(getIdManager().getId(criteriaId[i])));
         ac.setAttachment(attachment);
         attCrit.add(ac);
      }
      attachment.setAttachmentCriteria(attCrit);
      
      this.getHibernateTemplate().save(attachment);
      return attachment;
   }

   protected Id convertRef(Reference artifactRef) {
      String uuid = getContentHosting().getUuid(artifactRef.getId());
      return getIdManager().getId(uuid);
   }

   public void detachArtifact(final Id cellId, final Id artifactId) {

      HibernateCallback callback = new HibernateCallback() {
         public Object doInHibernate(Session session) throws HibernateException, SQLException {
            Object[] params = new Object[]{cellId, artifactId};
            Type[] types = new Type[]{Hibernate.custom(IdType.class), Hibernate.custom(IdType.class)};

            Cell cell = (Cell) session.load(Cell.class, cellId);
            Set attachments = cell.getAttachments();
            Iterator iter = attachments.iterator();
            List toRemove = new ArrayList();
            while (iter.hasNext()) {
               Attachment a = (Attachment) iter.next();
               if (a.getArtifactId()==null || artifactId.equals(a.getArtifactId())) {
                  toRemove.add(a);
               }
            }
            attachments.removeAll(toRemove);

            session.update(cell);
            return null;
         }

      };

      getHibernateTemplate().execute(callback);


   }



   public boolean isRubricSatisfied(Cell cell) {
      List results = rubricSatisfaction(cell);
      for (Iterator iter = results.iterator(); iter.hasNext();) {
         RubricSatisfactionBean bean = (RubricSatisfactionBean) iter.next();
         if (bean.getActual() < bean.getNeeded()) {
            return false;
         }
      }

      return true;
   }

   public List rubricSatisfaction2(Id cellId) {
      List retList = new ArrayList();

      List list = this.getHibernateTemplate().find("select rubric, count(webdav) " +
            "from Rubric rubric, " +
            "  Cell cell, " +
            "  Attachment attachment, " +
            "  NodeMetadata webdav " +
            "where rubric.criterion.id = attachment.criterion.id " +
            "  and cell.id = attachment.cell.id " +
            "  and webdav.id = attachment.artifactId " +
            "  and rubric.type = webdav.typeId " +
            "  and rubric.mimeType.primaryType = webdav.primaryMimeType " +
            "  and rubric.mimeType.subType = webdav.subMimeType " +
            "  and cell.id=? " +
            "group by rubric", cellId.getValue());

      
      for (Iterator iter = list.iterator(); iter.hasNext();) {
         RubricSatisfactionBean rubSBean = new RubricSatisfactionBean();
         Object[] obj = (Object[]) iter.next();
         rubSBean.setRubricId(((Rubric) obj[0]).getId());
         rubSBean.setNeeded(((Rubric) obj[0]).getQuantity());
         rubSBean.setActual(((Integer) obj[1]).intValue());
         retList.add(rubSBean);

      }
      return retList;

   }

   public List rubricSatisfaction(Cell cell) {
      List rubrics = this.getRubrics(cell);

      List results = new ArrayList();
      for (Iterator iter = rubrics.iterator(); iter.hasNext();) {
         Rubric currentRubric = (Rubric) iter.next();
         List artifacts = this.getCellArtifactsByCriterion(cell.getId(), currentRubric.getCriterion().getId(),
               currentRubric.getType().getValue(), currentRubric.getMimeType().getPrimaryType(),
               currentRubric.getMimeType().getSubType());

         RubricSatisfactionBean rubSBean = new RubricSatisfactionBean();
         rubSBean.setRubricId(currentRubric.getId());
         rubSBean.setNeeded(currentRubric.getQuantity());
         rubSBean.setActual(artifacts.size());
         results.add(rubSBean);
      }

      return results;
   }

   public Matrix getMatrix(Id matrixId) {
      return (Matrix) this.getHibernateTemplate().load(Matrix.class, matrixId);
   }
   
   public MatrixTool getMatrixTool(Id matrixToolId) {
      return (MatrixTool) this.getHibernateTemplate().get(MatrixTool.class, matrixToolId);
   }

   public Scaffolding getScaffolding(Id scaffoldingId) {
      return (Scaffolding) this.getHibernateTemplate().get(Scaffolding.class, scaffoldingId);
      //return getScaffolding(scaffoldingId, false);
   }

   protected Scaffolding getScaffoldingForExport(Id scaffoldingId) {
      Scaffolding scaffolding = (Scaffolding) this.getHibernateTemplate().get(Scaffolding.class, scaffoldingId);

      for (Iterator iter = scaffolding.getScaffoldingCells().iterator(); iter.hasNext();) {
         ScaffoldingCell sCell = (ScaffoldingCell) iter.next();
         Collection reviewers = this.getScaffoldingCellReviewers(sCell.getId(), false);
         sCell.setReviewers(new HashSet(reviewers));
      }      

      return scaffolding;
   }
   
   public ScaffoldingCell getScaffoldingCell(Criterion criterion, Level level) {
      ScaffoldingCell scaffoldingCell = null;
      Object[] params = new Object[]{criterion.getId().getValue(), 
            level.getId().getValue()};
      
      List list = this.getHibernateTemplate().find("from " +
            "ScaffoldingCell scaffoldingCell where scaffoldingCell.rootCriterion=? " +
            "and scaffoldingCell.level=?", params);
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
      
      scaffoldingCell.setReviewers(getScaffoldingCellReviewers(id, true));

      return scaffoldingCell;
   }
   
   protected Collection getScaffoldingCellReviewers(Id scaffoldingCellId, boolean useAgentId) {
      Collection reviewers = new HashSet();
      Collection viewerAuthzs = getAuthzManager().getAuthorizations(null,
            MatrixFunctionConstants.REVIEW_MATRIX, scaffoldingCellId);

      for (Iterator i = viewerAuthzs.iterator(); i.hasNext();) {
         Authorization reviewer = (Authorization) i.next();
         if (useAgentId)
            reviewers.add(reviewer.getAgent());
         else
            reviewers.add(reviewer.getAgent().getId());
      }
      return reviewers;
   }
   
   public void removeFromSession(Object obj) {
      this.getHibernateTemplate().evict(obj);
      try {
         getHibernateTemplate().getSessionFactory().evict(obj.getClass());
      } catch (HibernateException e) {
         logger.error(e);
      }
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

   public List getCellArtifactsByCriterion(Id cellId, Id criterionId,
                                           String type, String primaryMimeType, String subMimeType) {
      Object[] params = new Object[]{cellId.getValue(),
                                     criterionId.getValue(), type, primaryMimeType, subMimeType};
      return this.getHibernateTemplate().find("from AttachmentCriterion ac, NodeMetadata webdav " +
            "where ac.attachment.artifactId = webdav.id " +
            "and ac.attachment.cell.id=? " +
            "and ac.criterion.id=? " +
            "and webdav.typeId=? " +
            "and webdav.primaryMimeType like ? " +
            "and webdav.subMimeType like ? ", params);
   }
   
   public Attachment getAttachment(Id attachmentId) {
      return (Attachment) this.getHibernateTemplate().load(Attachment.class, attachmentId);
   }

   public List getArtifactAssociationCriteria(Id cellId, Id nodeId) {
      Object[] params = new Object[]{cellId.getValue(), nodeId.getValue()};
      return this.getHibernateTemplate().find("select ac.criterion from AttachmentCriterion ac where ac.attachment.cell=? and ac.attachment.artifactId=?", params);
   }
   
   public Set getCellContents(Cell cell) {
      Set result = new HashSet();
      Set removes = new HashSet();
      if (cell.getAttachments() != null) {
         for (Iterator iter = cell.getAttachments().iterator(); iter.hasNext();) {
            Attachment attachment = (Attachment) iter.next();
            Node node = getNode(attachment.getArtifactId(), cell);
            if (node != null) {
               result.add(node);
            }
            else {
               //logger.warn("Cell contains stale artifact references (null node encountered) for Cell: " + cell.getId().getValue() + ". Detaching");
               //detachArtifact(cell.getId(),attachment.getArtifactId());
               removes.add(attachment.getArtifactId());
            }
         }
         for (Iterator iter2 = removes.iterator(); iter2.hasNext();) {
            Id id = (Id) iter2.next();
            logger.warn("Cell contains stale artifact references (null node encountered) for Cell: " + cell.getId().getValue() + ". Detaching");
            detachArtifact(cell.getId(), id);
         }
      }
      return result;
   }

   protected Node getNode(Id artifactId, Cell cell) {
      Node node = getNode(artifactId);

      ContentResource wrapped = new ContentEntityWrapper(node.getResource(),
            buildRef(cell.getId().getValue(), node.getResource()));

      return new Node(artifactId, wrapped, node.getTechnicalMetadata().getOwner());
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
         logger.error("", e);
         throw new RuntimeException(e);
      }
      catch (IdUnusedException e) {
         logger.error("", e);
         throw new RuntimeException(e);
      }
      catch (TypeException e) {
         logger.error("", e);
         throw new RuntimeException(e);
      }
   }

   public Node getNode(Reference ref) {
      String nodeId = getContentHosting().getUuid(ref.getId());

      return getNode(getIdManager().getId(nodeId));
   }

   public Reflection getReflection(Id reflectionId) {
      //return (Reflection) this.getHibernateTemplate().load(Reflection.class, reflectionId);
      return (Reflection) this.getHibernateTemplate().get(Reflection.class, reflectionId);
   }
   
   public List getCellArtifacts(Cell cell)
   {
      List nodeList = new ArrayList();
      Set attachments = cell.getAttachments();
      
      for (Iterator attachmentIterator = attachments.iterator(); attachmentIterator.hasNext();) {
         Attachment att = (Attachment)attachmentIterator.next();
         //TODO is it okay to clear the whole thing here?
         getHibernateTemplate().clear();
         
         Node node = getNode(att.getArtifactId(), cell);
         if (node != null) {
        	 nodeList.add(node);
         } else
             logger.warn("Cell contains stale artifact references (null node encountered) for Cell: " + cell.getId());
         
      } 
      return nodeList;
   }

   public List getCellsByArtifact(Id artifactId) {
      //return this.getHibernateTemplate().find("select distinct attachment.cell from Attachment attachment where attachment.artifactId=?", artifactId.getValue());
      //this.getHibernateTemplate().find
      Criteria c = null;
      try {
         c = this.getSession().createCriteria(Cell.class);
         c.setFetchMode("scaffoldingCell", FetchMode.EAGER);
         c.setFetchMode("scaffoldingCell.scaffolding", FetchMode.EAGER);
         //c.add(Expression.eq("artifactId", artifactId));
         Criteria att = c.createCriteria("attachments");
         att.add(Expression.eq("artifactId", artifactId));
         
         return c.list();
      } catch (DataAccessResourceFailureException e) {
         logger.error("", e);
      } catch (HibernateException e) {
         logger.error("", e);
      } catch (IllegalStateException e) {
         logger.error("", e);
      }
      return new ArrayList();
   }
   
   /**
    * @param criterion
    * @return List of Rubrics
    */
   public List getRubric(Criterion criterion, Level level) {
      Object[] params = new Object[]{criterion.getId().getValue(), level.getId().getValue()};
      return this.getHibernateTemplate().find("from Rubric rubric " +
            "where rubric.criterion.id=? " +
            "and rubric.level.id=? ", params);
   }

   public Rubric getRubric(Id rubricId) {
      return (Rubric) this.getHibernateTemplate().load(Rubric.class, rubricId);
   }

   public List getRubrics(Cell cell) {
      List criteria = getCellCriteria(cell);
      Level level = cell.getScaffoldingCell().getLevel();
      List rubrics = new ArrayList();

      for (Iterator iter = criteria.iterator(); iter.hasNext();) {
         Criterion criterion = (Criterion) iter.next();
         List moreRubrics = getRubric(criterion, level);
         rubrics.addAll(moreRubrics);
      }

      return rubrics;
   }

   public List getRubricByArtifact(Id artifactId) {
      //return (List)this.getHibernateTemplate().load(Artifact.class, artifactId);
      //This query used to be "from Cell, Rubric" but SQL Server didn't work,
      // so it was changed to "from Rubric, Cell".  Not sure why it makes a
      // difference, but it does.
      //return this.getHibernateTemplate().find("select rubric.id from " +
      //      "Rubric rubric, AttachmentCriterion ac, Cell cell " +
      //      "join ac.attachment.cell cell " +
      //      "where cell.scaffoldingCell.level.id = rubric.level.id " +
      //      "and ac.criterion.id = rubric.criterion.id " +
      //      "and ac.attachment.artifactId=?", artifactId.getValue());
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
      this.getHibernateTemplate().delete(getMatrix(matrixId));
   }
   
   private boolean cellHasOpenReview(Id cellId) {
      //Check for an existing "open" review (one that hasn't been completed yet)
      Object[] params = new Object[]{MatrixFunctionConstants.COMPLETE_STATUS, 
            cellId.getValue()};
      List result = this.getHibernateTemplate().find("from ReviewerItem items " +
            "where items.status <> ? and items.cell.id=?", params);
      
      return (result.size() > 0);
   }
   
   public Cell submitCellForReview(Cell cell) {
      getHibernateTemplate().refresh(cell); //TODO not sure if this is necessary
      ScaffoldingCell sCell = cell.getScaffoldingCell();
      
      if (sCell.isGradableReflection()) {
         cell.setStatus(MatrixFunctionConstants.PENDING_STATUS);
         storeCell(cell);
         if (!cellHasOpenReview(cell.getId())) {
            ReviewerItem ri = new ReviewerItem();
            ri.setCreated(new Date(System.currentTimeMillis()));
            ri.setStatus(MatrixFunctionConstants.WAITING_STATUS);
            ri.setCell(cell);
            
            cell.setReviewerItem(ri);
            storeCell(cell);
            //getHibernateTemplate().flush();
   
            //Lock artifacts
            for (Iterator iter = cell.getAttachments().iterator(); iter.hasNext();) {
               Attachment att = (Attachment)iter.next();
               
               getLockManager().lockObject(att.getArtifactId().getValue(), 
                     cell.getId().getValue(), 
                     "Submitting cell for review", true);
               
            }
         }
         else {
            logger.warn("Cell " + cell.getId().getValue() + " already has an open review.");
         }
      }
      unlockNextCell(cell);

      //TODO removing this for sakai - might need it later
      //createReviewerAuthz(cell.getId());
      return cell;
   }
   
   public ReviewRubricValue findReviewRubricValue(String id) {
      for (Iterator iter = getReviewRubrics().iterator(); iter.hasNext();) {
         ReviewRubricValue rrv = (ReviewRubricValue)iter.next();
         if (rrv.getId().equals(id))
            return rrv;
      }
      return null;
   }
   
   public List getReviewableCells(Agent agent, Id worksiteId) {
      List roles = agent.getWorksiteRoles(worksiteId.getValue());
      Agent role = (Agent)roles.get(0);

      List returned = this.getHibernateTemplate().find("select item from " +
         " ReviewerItem item where item.cell.scaffoldingCell.scaffolding.worksiteId=? and" +
         " item.reviewer = ? and item.status = '"+MatrixFunctionConstants.CHECKED_OUT_STATUS+"'",
         new Object[] {worksiteId.getValue(), agent.getId().getValue()});

      returned.addAll(this.getHibernateTemplate().find("select item from " +
            "ReviewerItem item, Authorization auth " +
            "where item.cell.scaffoldingCell.id = auth.qualifier " +
            "and auth.function = ? and item.status not in ('"+MatrixFunctionConstants.COMPLETE_STATUS+"'," +
            "'"+MatrixFunctionConstants.CHECKED_OUT_STATUS+"') and (auth.agent=? " +
            " or auth.agent=?) " +
            " and item.cell.scaffoldingCell.scaffolding.worksiteId=?",
         new Object[]{MatrixFunctionConstants.REVIEW_MATRIX,
            agent.getId().getValue(), role.getId().getValue(),
            worksiteId.getValue()}));


      if (getAuthzManager().isAuthorized(MatrixFunctionConstants.UNLOCK_REVIEW_MATRIX,
         getIdManager().getId(PortalService.getCurrentToolId()))) {
         returned.addAll(this.getHibernateTemplate().find("select item from " +
            "ReviewerItem item " +
            " where item.status = '"+MatrixFunctionConstants.CHECKED_OUT_STATUS+"' " +
            " and item.cell.scaffoldingCell.scaffolding.worksiteId=? and item.reviewer != ?",
            new Object[]{worksiteId.getValue(), agent.getId().getValue()}));
      }

      List unique = new ArrayList();

      for (Iterator i=returned.iterator();i.hasNext();) {
         Object item = i.next();
         if (!unique.contains(item)) {
            unique.add(item);
         }
      }

      return unique;
   }
   
   public void packageScffoldingForExport(Id scaffoldingId, OutputStream os) throws IOException {
      Scaffolding oldScaffolding = this.getScaffoldingForExport(scaffoldingId); //, true);

      CheckedOutputStream checksum = new CheckedOutputStream(os, new Adler32());
      ZipOutputStream zos = new ZipOutputStream(new BufferedOutputStream(checksum));

      storeScaffoldingFile(zos, oldScaffolding.getPrivacyXsdId());

      List levels = oldScaffolding.getLevels();
      List criteria = oldScaffolding.getCriteria();
      Set rubrics = oldScaffolding.getRubric();
      Set scaffoldingCells = oldScaffolding.getScaffoldingCells();
      
      for (Iterator crits = criteria.iterator(); crits.hasNext();) {
         Criterion criterion = (Criterion) crits.next();
         criterion.setCriteria(new ArrayList());
         //TODO when support for subcriteria exists, this will need to be fixed
      }
      
      for (Iterator iter = scaffoldingCells.iterator(); iter.hasNext();) {
         ScaffoldingCell sCell = (ScaffoldingCell)iter.next();
         List expectations = sCell.getExpectations();
         sCell.setExpectations(new ArrayList(expectations));
         sCell.setCells(new HashSet());
         //Collection reviewers = getScaffoldingCellReviewers(sCell.getId());
         Collection reviewers = sCell.getReviewers();
         sCell.setReviewers(new HashSet(reviewers));
         //sCell.setInitialStatus(sCell.getInitialStatus());
      }

      oldScaffolding.setLevels(new ArrayList(levels));
      oldScaffolding.setCriteria(new ArrayList(criteria));
      oldScaffolding.setRubric(new HashSet(rubrics));
      oldScaffolding.setScaffoldingCells(new HashSet(scaffoldingCells));

      removeFromSession(oldScaffolding);

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
   
   public Scaffolding uploadScaffolding(String scaffoldingFileName,
         String toolId, InputStream zipFileStream) throws IOException {
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

   public Scaffolding uploadScaffolding(Reference uploadedScaffoldingFile, ToolConfiguration currentPlacement)
         throws IOException {
      Node file = getNode(uploadedScaffoldingFile);

      ZipInputStream zis = new UncloseableZipInputStream(file.getInputStream());

      ZipEntry currentEntry = zis.getNextEntry();
      Hashtable fileMap = new Hashtable();
      Scaffolding scaffolding = null;

      String tempDirName = getIdManager().createId().getValue();

      boolean itWorked = false;

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
               gotFile = true;
               processFile(currentEntry, zis, fileMap, fileParent);
            }

            zis.closeEntry();
            currentEntry = zis.getNextEntry();
         }

         scaffolding.setId(null);
         scaffolding.setPublished(false);
         scaffolding.setPublishedBy(null);
         scaffolding.setPublishedDate(null);

         resetIds(scaffolding);

         storeScaffolding(scaffolding);
         
         scaffolding.setOwnerId(getAuthnManager().getAgent().getId());
         scaffolding.setWorksiteId(getIdManager().getId(currentPlacement.getSiteId()));

         if (gotFile) {
            fileParent.getPropertiesEdit().addProperty(
                  ResourceProperties.PROP_DISPLAY_NAME, scaffolding.getTitle());
            getContentHosting().commitCollection(fileParent);
         }
         else {
            getContentHosting().cancelCollection(fileParent);
         }

         if (scaffolding.getPrivacyXsdId() != null) {
            scaffolding.setPrivacyXsdId((Id)fileMap.get(scaffolding.getPrivacyXsdId()));
         }

         storeScaffolding(scaffolding);
         
         createReviewerAuthzForImport(scaffolding);
         
         itWorked = true;
         createMatrixTool(currentPlacement.getId(), scaffolding);
         return scaffolding;
      }
      catch (Exception exp) {
         throw new RuntimeException(exp);
      }
      finally {
         if (!itWorked) {
            // fileParent.persistent().destroy();
            // todo clean up
         }
         try {
            zis.closeEntry();
         }
         catch (IOException e) {
            logger.error("", e);
         }
      }
   }

   public void checkCellAccess(String id) {
      Cell cell = getCell(getIdManager().getId(id));
      Id toolId = cell.getMatrix().getMatrixTool().getId();
      if (!getAuthzManager().isAuthorized(MatrixFunctionConstants.VIEW_MATRIX_USERS, toolId) &&
          !getAuthzManager().isAuthorized(MatrixFunctionConstants.REVIEW_MATRIX, cell.getId())) {
         // won't setup security advisor, so it won't load
         return;
      }

      // this should set the security advisor for the attached artifacts.
      getCellArtifacts(cell);
   }

   private void createReviewerAuthzForImport(Scaffolding scaffolding) {
      for (Iterator iter = scaffolding.getScaffoldingCells().iterator(); iter.hasNext();) {
         ScaffoldingCell sCell = (ScaffoldingCell) iter.next();
         Collection revs = sCell.getReviewers();
         for (Iterator i = revs.iterator(); i.hasNext();) {
            //Agent importedAgent = (Agent)i.next();
            //Id id = importedAgent.getId();
            Id id = (Id)i.next();
            if (id.getValue().startsWith("/site/")) {
               // it's a role
               String[] agentValues = id.getValue().split("/");
               
               String newStrId = id.getValue().replaceAll(agentValues[2], 
                     scaffolding.getWorksiteId().getValue());
               id = idManager.getId(newStrId);
            }
            Agent agent = this.getAgentFromId(id);

            if (agent != null) {
               this.getAuthzManager().createAuthorization(agent, 
                     MatrixFunctionConstants.REVIEW_MATRIX, sCell.getId());
            }
         }
      }
   }

   protected ContentCollection getUserCollection() throws TypeException, IdUnusedException, PermissionException {
      User user = UserDirectoryService.getCurrentUser();
      String userId = user.getId();
      String wsId = SiteService.getUserSiteId(userId);
      String wsCollectionId = getContentHosting().getSiteCollection(wsId);
      ContentCollection collection = getContentHosting().getCollection(wsCollectionId);
      return collection;
   }


   protected ContentCollectionEdit getFileDir(String origName) throws InconsistentException,
         PermissionException, IdUsedException, IdInvalidException, IdUnusedException, TypeException {
      ContentCollection collection = getUserCollection();
      String childId = collection.getId() + origName;
      return getContentHosting().addCollection(childId);
   }

   protected Scaffolding processScaffolding(ZipInputStream zis) throws IOException, ClassNotFoundException {
      XMLDecoder dec = new XMLDecoder(zis);
      return (Scaffolding)dec.readObject();
   }


   protected void processFile(ZipEntry currentEntry, ZipInputStream zis,
         Hashtable fileMap, ContentCollection fileParent) throws IOException, InconsistentException, PermissionException, IdUsedException, IdInvalidException, OverQuotaException, ServerOverloadException {
      File file = new File(currentEntry.getName());

      MimeType mimeType = new MimeType(file.getParentFile().getParentFile().getParent(),
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

   protected void resetIds(Scaffolding scaffolding) {
      substituteCriteria(scaffolding);
      substituteLevels(scaffolding);
      substituteScaffoldingCells(scaffolding);
      substituteRubrics(scaffolding);
   }

   protected void substituteIds(Scaffolding scaffolding) {
      substituteScaffoldingCells(scaffolding);
      substituteRubrics(scaffolding);
   }
   
   protected void substituteCriteria(Scaffolding scaffolding) {
      List newCriteria = new ArrayList();
      for (Iterator i=scaffolding.getCriteria().iterator(); i.hasNext();) {
         Criterion criterion = (Criterion)i.next();

         //TODO handle sub criteria
         criterion.setId(null);
         criterion.setCriteria(new ArrayList());
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
   
   protected void substituteScaffoldingCells(Scaffolding scaffolding) {
      Set sCells = new HashSet(); 
      for (Iterator iter=scaffolding.getScaffoldingCells().iterator(); iter.hasNext();) {
         ScaffoldingCell scaffoldingCell = (ScaffoldingCell) iter.next();
         scaffoldingCell.setId(null);
         //scaffoldingCell.getCells().clear();
         scaffoldingCell.setCells(new HashSet());
         
         List expectations = new ArrayList();
         for (Iterator i=scaffoldingCell.getExpectations().iterator(); i.hasNext();) {
            Expectation ex = (Expectation) i.next();
            ex.setId(null);
            expectations.add(ex);
         }
         scaffoldingCell.setExpectations(expectations);
         sCells.add(scaffoldingCell);
         //scaffoldingCell.setScaffolding(scaffolding);
      }   
      scaffolding.setScaffoldingCells(sCells);
   }
   
   protected void substituteRubrics(Scaffolding scaffolding) {
      for (Iterator iter=scaffolding.getRubric().iterator(); iter.hasNext();) {
         Rubric rubric = (Rubric) iter.next();
         rubric.setId(null);
      }   
   }

   /*
   protected WritableObjectHome getFileHome() {
      return fileHome;
   }

   public void setFileHome(WritableObjectHome fileHome) {
      this.fileHome = fileHome;
   }
   */

   private boolean findInAuthz(Id qualifier, Agent agent, List authzs) {
      for (Iterator iter = authzs.iterator(); iter.hasNext();) {
         Authorization authz = (Authorization) iter.next();
         // Same item, different agent
         if (!authz.getAgent().equals(agent) && authz.getQualifier().equals(qualifier))
            return true;
      }
      return false;
   }

   public ReviewerItem getReviewerItem(Id id) {
      return (ReviewerItem) this.getHibernateTemplate().load(ReviewerItem.class, id);
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
      matrix.setHome(this);
      return matrix;
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
   public List getReviewRubrics() {
      return reviewRubrics;
   }
   public void setReviewRubrics(List reviewRubrics) {
      this.reviewRubrics = reviewRubrics;
   }

   public void packageForDownload(Map params, OutputStream out) throws IOException {
      packageScffoldingForExport(
         getIdManager().getId(((String[])params.get(SCAFFOLDING_ID_TAG))[0]),
         out);
   }

   public void importResources(ToolConfiguration fromTool, ToolConfiguration toTool, List resourceIds) {
      ClassLoader currentLoader = Thread.currentThread().getContextClassLoader();

      try {
         Thread.currentThread().setContextClassLoader(getClass().getClassLoader());

         ByteArrayOutputStream bos = new ByteArrayOutputStream();

         MatrixTool orig = getMatrixTool(getIdManager().getId(fromTool.getId()));

         if (orig == null) {
            return;
         }
         
         Scaffolding scaffolding = orig.getScaffolding();
         Id id = scaffolding.getId();
         String title = scaffolding.getTitle();

         getHibernateTemplate().evict(scaffolding);

         packageScffoldingForExport(id, bos);
         ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
         uploadScaffolding(title, toTool.getId(), bis);
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

   protected String buildRef(String contextId, ContentResource resource) {
      return Entity.SEPARATOR + MatrixContentEntityProducer.MATRIX_PRODUCER +
         Entity.SEPARATOR + contextId + resource.getReference();
   }

}

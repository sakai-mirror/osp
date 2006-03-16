/**********************************************************************************
* $URL: https://source.sakaiproject.org/svn/trunk/sakai/legacy/component/src/java/org/sakaiproject/component/framework/email/TestEmailService.java $
* $Id: TestEmailService.java 3831 2005-11-14 20:17:24Z ggolden@umich.edu $
***********************************************************************************
*
* Copyright (c) 2005, 2006 The Sakai Foundation.
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
package org.theospi.portfolio.wizard.mgt.impl;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.BufferedInputStream;
import java.io.InputStream;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.zip.Adler32;
import java.util.zip.CheckedOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import org.jdom.Document;
import org.jdom.CDATA;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;
import org.jdom.DataConversionException;
import org.sakaiproject.api.kernel.tool.cover.ToolManager;
import org.sakaiproject.api.kernel.tool.Placement;
import org.sakaiproject.exception.IdInvalidException;
import org.sakaiproject.exception.IdUnusedException;
import org.sakaiproject.exception.IdUsedException;
import org.sakaiproject.exception.InconsistentException;
import org.sakaiproject.exception.PermissionException;
import org.sakaiproject.exception.TypeException;
import org.sakaiproject.exception.ServerOverloadException;
import org.sakaiproject.exception.ImportException;
import org.sakaiproject.metaobj.shared.ArtifactFinder;
import org.sakaiproject.metaobj.shared.DownloadableManager;
import org.sakaiproject.metaobj.shared.mgt.*;
import org.sakaiproject.metaobj.shared.model.Agent;
import org.sakaiproject.metaobj.shared.model.Artifact;
import org.sakaiproject.metaobj.shared.model.FinderException;
import org.sakaiproject.metaobj.shared.model.Id;
import org.sakaiproject.metaobj.shared.model.MimeType;
import org.sakaiproject.metaobj.shared.model.StructuredArtifactDefinitionBean;
import org.sakaiproject.metaobj.shared.model.Type;
import org.sakaiproject.metaobj.security.AuthenticationManager;
import org.sakaiproject.service.legacy.content.ContentCollection;
import org.sakaiproject.service.legacy.content.ContentCollectionEdit;
import org.sakaiproject.service.legacy.content.ContentHostingService;
import org.sakaiproject.service.legacy.content.ContentResource;
import org.sakaiproject.service.legacy.content.ContentResourceEdit;
import org.sakaiproject.service.legacy.entity.EntityManager;
import org.sakaiproject.service.legacy.entity.Reference;
import org.sakaiproject.service.legacy.entity.ResourceProperties;
import org.sakaiproject.service.legacy.entity.ResourcePropertiesEdit;
import org.sakaiproject.service.legacy.security.SecurityService;
import org.sakaiproject.service.legacy.site.Site;
import org.sakaiproject.service.legacy.site.SitePage;
import org.sakaiproject.service.legacy.site.ToolConfiguration;
import org.sakaiproject.service.legacy.site.cover.SiteService;
import org.sakaiproject.service.legacy.user.User;
import org.sakaiproject.service.legacy.user.cover.UserDirectoryService;
import org.sakaiproject.service.framework.portal.cover.PortalService;
import org.springframework.orm.hibernate.support.HibernateDaoSupport;
import org.theospi.portfolio.guidance.model.*;
import org.theospi.portfolio.review.mgt.ReviewManager;
import org.theospi.portfolio.review.model.Review;
import org.theospi.portfolio.security.AllowMapSecurityAdvisor;
import org.theospi.portfolio.security.Authorization;
import org.theospi.portfolio.security.AuthorizationFacade;
import org.sakaiproject.metaobj.shared.mgt.ContentEntityUtil;
import org.theospi.portfolio.shared.model.Node;
import org.theospi.portfolio.shared.model.OspException;
import org.theospi.portfolio.shared.model.ObjectWithWorkflow;
import org.theospi.portfolio.style.mgt.StyleManager;
import org.theospi.portfolio.style.model.Style;
import org.theospi.portfolio.wizard.impl.WizardEntityProducer;
import org.theospi.portfolio.wizard.mgt.WizardManager;
import org.theospi.portfolio.wizard.model.*;
import org.theospi.portfolio.wizard.WizardFunctionConstants;
import org.theospi.portfolio.workflow.mgt.WorkflowManager;
import org.theospi.portfolio.workflow.model.Workflow;
import org.theospi.portfolio.workflow.model.WorkflowItem;
import org.theospi.portfolio.matrix.MatrixFunctionConstants;
import org.theospi.portfolio.matrix.model.WizardPageDefinition;
import org.theospi.portfolio.guidance.mgt.GuidanceManager;
import net.sf.hibernate.HibernateException;

public class WizardManagerImpl extends HibernateDaoSupport
      implements WizardManager, DownloadableManager, ReadableObjectHome, ArtifactFinder, PresentableObjectHome {

   static final private String   DOWNLOAD_WIZARD_ID_PARAM = "wizardId";

   private AuthorizationFacade authorizationFacade;
   private SecurityService securityService;
   private EntityManager entityManager;
   private IdManager idManager;
   private StructuredArtifactDefinitionManager structuredArtifactDefinitionManager;
   private AgentManager agentManager;
   private AuthenticationManager authManager;
   private GuidanceManager guidanceManager;
   private WorkflowManager workflowManager;
   private ContentHostingService contentHosting;
   private PresentableObjectHome xmlRenderer;
   private ReviewManager reviewManager;
   private StyleManager styleManager;

   protected void init() throws Exception {
      /*
      FunctionManager.registerFunction(WizardFunctionConstants.CREATE_WIZARD);
      FunctionManager.registerFunction(WizardFunctionConstants.EDIT_WIZARD);
      FunctionManager.registerFunction(WizardFunctionConstants.DELETE_WIZARD);
      FunctionManager.registerFunction(WizardFunctionConstants.PUBLISH_WIZARD);
      FunctionManager.registerFunction(WizardFunctionConstants.REVIEW_WIZARD);
      FunctionManager.registerFunction(WizardFunctionConstants.EVALUATE_WIZARD);
      FunctionManager.registerFunction(WizardFunctionConstants.VIEW_WIZARD);
      FunctionManager.registerFunction(WizardFunctionConstants.EXPORT_WIZARD);
      */
   }

   public Wizard createNew() {
      Placement placement = ToolManager.getCurrentPlacement();
      String currentSite = placement.getContext();
      String currentTool = PortalService.getCurrentToolId();
      Agent agent = getAuthManager().getAgent();
      Wizard wizard = new Wizard(getIdManager().createId(), agent, currentSite, currentTool);
      return wizard;
   }

   public Wizard getWizard(Id wizardId) {
      Wizard wizard = (Wizard)getHibernateTemplate().get(Wizard.class, wizardId);

      if (wizard == null) {
         return null;
      }

      getAuthorizationFacade().checkPermission(WizardFunctionConstants.VIEW_WIZARD,
         getIdManager().getId(wizard.getToolId()));

      // setup access to the files
      List refs = new ArrayList();
      
      if (wizard.getStyle() != null) {
         Node node = getNode(wizard.getStyle().getStyleFile());
         refs.add(node.getResource().getReference());
      }         

      WizardCategory rootCategory = (WizardCategory)wizard.getRootCategory();
      loadCategory(rootCategory, refs);

      getSecurityService().pushAdvisor(new AllowMapSecurityAdvisor(ContentHostingService.EVENT_RESOURCE_READ,
         refs));

      return wizard;
   }
   
   public Node getNode(Id artifactId) {
      String id = getContentHosting().resolveUuid(artifactId.getValue());
      if (id == null) {
         return null;
      }

      try {
         ContentResource resource = getContentHosting().getResource(id);
         String ownerId = resource.getProperties().getProperty(resource.getProperties().getNamePropCreator());
         Agent owner = getAgentManager().getAgent(getIdManager().getId(ownerId));
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

   protected void loadCategory(WizardCategory category, List refs) {

      for (Iterator i=category.getChildPages().iterator();i.hasNext();) {
         WizardPageSequence page = (WizardPageSequence) i.next();
         WizardPageDefinition pageDef = page.getWizardPageDefinition(); // make sure this loads
         pageDef.getTitle();
      }

      if (category.getChildCategories() != null) {
         for (Iterator i=category.getChildCategories().iterator();i.hasNext();) {
            loadCategory((WizardCategory) i.next(), refs);
         }
      }
   }

   public Wizard saveWizard(Wizard wizard) {
      Date now = new Date(System.currentTimeMillis());
      wizard.setModified(now);

      if (wizard.getExposeAsTool() != null &&
            wizard.getExposeAsTool().booleanValue() &&
            wizard.getExposedPageId() == null) {
         addTool(wizard);
      }
      else if (wizard.getExposeAsTool() != null &&
            !wizard.getExposeAsTool().booleanValue() &&
            wizard.getExposedPageId() != null) {
         removeTool(wizard);
      }

      if (wizard.isNewObject()) {
         // for some reason the save throws a null pointer exception
         //    if the id isn't set, so generate a new one if need be
         if(wizard.getId() == null)
            wizard.setId(getIdManager().createId());
         wizard.setCreated(now);
         wizard.getRootCategory().setCreated(now);
         wizard.getRootCategory().setModified(now);
         wizard.getRootCategory().setWizard(null);
         getHibernateTemplate().save(wizard, wizard.getId());
         wizard.getRootCategory().setWizard(wizard);
         wizard.setNewObject(false);
      }
      else {
         getHibernateTemplate().saveOrUpdate(wizard);
      }

      return wizard;
   }

   private void removeTool(Wizard wizard) {
      String siteId = wizard.getSiteId();
      try {
         Site siteEdit = SiteService.getSite(siteId);

         SitePage page = siteEdit.getPage(wizard.getExposedPageId());
         siteEdit.removePage(page);
         SiteService.save(siteEdit);
         wizard.setExposedPageId(null);
      } catch (IdUnusedException e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
      } catch (PermissionException e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }
   }

   private void addTool(Wizard wizard) {
      //TODO add logging errors back
      String siteId = wizard.getSiteId();
      try {
         Site siteEdit = SiteService.getSite(siteId);


         SitePage page = siteEdit.addPage();

         page.setTitle(wizard.getName());
         page.setLayout(SitePage.LAYOUT_SINGLE_COL);

         ToolConfiguration tool = page.addTool();
         tool.setTool(ToolManager.getTool("osp.exposedwizard"));
         tool.setTitle(wizard.getName());
         tool.setLayoutHints("0,0");
         tool.getPlacementConfig().setProperty(WizardManager.EXPOSED_WIZARD_KEY, wizard.getId().getValue());

         //LOG.info(this+": SiteService.commitEdit():" +siteId);

         SiteService.save(siteEdit);
         wizard.setExposedPageId(page.getId());


      } catch (IdUnusedException e) {
//       TODO Auto-generated catch block
         e.printStackTrace();
      } catch (PermissionException e) {
//       TODO Auto-generated catch block
         e.printStackTrace();
      }
   }

   public void deleteWizard(Wizard wizard) {
      getHibernateTemplate().delete(wizard);
   }

   public void publishWizard(Wizard wizard) {
      wizard.setPublished(true);
      wizard.setModified(new Date(System.currentTimeMillis()));
      this.saveWizard(wizard);
   }
   
   public String getWizardEntityProducer() {
      return WizardEntityProducer.WIZARD_PRODUCER;
   }

   public Reference decorateReference(Wizard wizard, String reference) {
      String fullRef = ContentEntityUtil.getInstance().buildRef(WizardEntityProducer.WIZARD_PRODUCER,
            wizard.getSiteId(), wizard.getId().getValue(), reference);

      return getEntityManager().newReference(fullRef);
   }

   public List listWizardsByType(String owner, String siteId, String type) {
      Object[] params = new Object[]{owner, new Boolean(true), siteId, type};
      return getHibernateTemplate().find("from Wizard w where " +
            "(w.owner=? or w.published=?) and w.siteId=? and w.type=? order by seq_num", params);
   }
   
   /**
    * Pulls all wizards, deeping loading all parts of each Wizard
    * @return List of Wizard
    */
   public List getWizardsForWarehousing()
   {
      List wizards = getHibernateTemplate().find("from Wizard w");
      
      return wizards;
   }
   
   /**
    * @return List of CompletedWizard
    */
   public List getCompletedWizardsByWizardId(String wizardId)
   {
      List completedWizards = getHibernateTemplate().find(" from CompletedWizard where wizard_id=?",
            new Object[]{wizardId});
      return completedWizards;
   }

   public List listAllWizards(String owner, String siteId) {
      Object[] params = new Object[]{owner, new Boolean(true), siteId};
      return getHibernateTemplate().find("from Wizard w where " +
            "(w.owner=? or w.published=?) and w.siteId=? order by seq_num", params);
   }

   public List findWizardsByOwner(String ownerId, String siteId) {
      Object[] params = new Object[]{ownerId, siteId};
      return getHibernateTemplate().find("from Wizard w where w.owner=? and w.siteId=? order by seq_num", params);
   }

   public List findPublishedWizards(String siteId) {
      Object[] params = new Object[]{new Boolean(true), siteId};
      return getHibernateTemplate().find("from Wizard w where w.published=? and w.siteId=? order by seq_num", params);
   }

   public Wizard getWizard(String id) {
      return getWizard(getIdManager().getId(id));
   }

   public Collection getAvailableForms(String siteId, String type) {
      return getStructuredArtifactDefinitionManager().findHomes(
            getIdManager().getId(siteId));
   }

   public void deleteObjects(List deletedItems) {

      for (Iterator i=deletedItems.iterator();i.hasNext();) {
         try {
            getSession().delete(i.next());
         }
         catch (HibernateException e) {
            throw new OspException(e);
         }
      }

   }

   protected List getCompletedWizards(String owner) {
      List completedWizards = getHibernateTemplate().find(" from CompletedWizard where owner_id=?",
            new Object[]{owner});
      return completedWizards;
   }

   public CompletedWizard getCompletedWizard(Id completedWizardId) {
      CompletedWizard wizard = (CompletedWizard)getHibernateTemplate().get(CompletedWizard.class, completedWizardId);
      return wizard;
   }

   public CompletedWizard getCompletedWizard(Wizard wizard) {
      Agent agent = getAuthManager().getAgent();

      return getUsersWizard(wizard, agent);
   }

   public CompletedWizard getCompletedWizard(Wizard wizard, String userId) {
      Agent agent = getAgentManager().getAgent(userId);

      return getUsersWizard(wizard, agent);
   }
   
   public CompletedWizard getCompletedWizardByPage(Id pageId) {
      CompletedWizard cw = null;
      Object[] params = new Object[]{pageId.getValue()};
      List list = getHibernateTemplate().find("select w.category.wizard from CompletedWizardPage w " +
            "where w.wizardPage.id=?", params);
      
      if (list.size() == 1) {
         cw = (CompletedWizard) list.get(0);
      }
      
      return cw;      
   }

   public CompletedWizard saveWizard(CompletedWizard wizard) {
      getHibernateTemplate().saveOrUpdate(wizard);
      return wizard;
   }

   public CompletedWizard getUsersWizard(Wizard wizard, Agent agent) {
      List completedWizards = getHibernateTemplate().find(" from CompletedWizard where wizard_id=? and owner_id=?",
            new Object[]{wizard.getId().getValue(), agent.getId().getValue()});

      if (completedWizards.size() == 0) {
         CompletedWizard returned = new CompletedWizard(wizard, agent);
         getHibernateTemplate().save(returned);
         return returned;
      }
      else {
         return (CompletedWizard)completedWizards.get(0);
      }
   }

   public void processWorkflow(int workflowOption, Id id) {
      //TODO Unimplemented
   }

   public void processWorkflow(Id workflowId, Id completedWizardId) {
      Workflow workflow = getWorkflowManager().getWorkflow(workflowId);
      CompletedWizard compWizard = this.getCompletedWizard(completedWizardId);

      Collection items = workflow.getItems();
      for (Iterator i = items.iterator(); i.hasNext();) {
         WorkflowItem wi = (WorkflowItem)i.next();
         //Cell actionCell = this.getMatrixCellByScaffoldingCell(cell.getMatrix(),
         //      wi.getActionObjectId());
         switch (wi.getActionType()) {
            case(WorkflowItem.STATUS_CHANGE_WORKFLOW):
               processStatusChangeWorkflow(wi, compWizard);
               break;
            case(WorkflowItem.NOTIFICATION_WORKFLOW):
               processNotificationWorkflow(wi);
               break;
            case(WorkflowItem.CONTENT_LOCKING_WORKFLOW):
               processContentLockingWorkflow(wi, compWizard);
               break;
         }
      }
   }
   
   private void processStatusChangeWorkflow(String status, CompletedWizard actionWizard) {
      actionWizard.setStatus(status);
   }

   private void processStatusChangeWorkflow(WorkflowItem wi, CompletedWizard actionWizard) {
      processStatusChangeWorkflow(wi.getActionValue(), actionWizard);
   }

   private void processContentLockingWorkflow(String lockAction, CompletedWizard actionWizard) {
      //TODO implement
   }

   private void processContentLockingWorkflow(WorkflowItem wi, CompletedWizard actionWizard) {
      processContentLockingWorkflow(wi.getActionValue(), actionWizard);
   }

   private void processNotificationWorkflow(WorkflowItem wi) {
      // TODO implement

   }
   
   public void checkWizardAccess(Id id) {
      CompletedWizard cw = getCompletedWizard(id);
      
      boolean canEval = getAuthorizationFacade().isAuthorized(WizardFunctionConstants.EVALUATE_WIZARD, 
            cw.getWizard().getId());
      boolean canReview = getAuthorizationFacade().isAuthorized(WizardFunctionConstants.REVIEW_WIZARD, 
            getIdManager().getId(cw.getWizard().getToolId()));
      
      boolean owns = cw.getOwner().getId().equals(getAuthManager().getAgent().getId());
      
      if (canEval || owns) {
         //can I look at reviews/evals/reflections? - own or eval
         getReviewManager().getReviewsByParentAndType(
               id.getValue(), Review.EVALUATION_TYPE,
               cw.getWizard().getSiteId(),
               WizardEntityProducer.WIZARD_PRODUCER);
      }
      
      if (canReview || owns) {
         //can I look at reviews/evals/reflections? - own or review
         getReviewManager().getReviewsByParentAndType(
               id.getValue(), Review.REVIEW_TYPE,
               cw.getWizard().getSiteId(),
               WizardEntityProducer.WIZARD_PRODUCER);         
      }
   }

   public AuthorizationFacade getAuthorizationFacade() {
      return authorizationFacade;
   }

   public void setAuthorizationFacade(AuthorizationFacade authorizationFacade) {
      this.authorizationFacade = authorizationFacade;
   }

   public EntityManager getEntityManager() {
      return entityManager;
   }

   public void setEntityManager(EntityManager entityManager) {
      this.entityManager = entityManager;
   }

   public IdManager getIdManager() {
      return idManager;
   }

   public void setIdManager(IdManager idManager) {
      this.idManager = idManager;
   }

   public SecurityService getSecurityService() {
      return securityService;
   }

   public void setSecurityService(SecurityService securityService) {
      this.securityService = securityService;
   }

   public StructuredArtifactDefinitionManager getStructuredArtifactDefinitionManager() {
      return structuredArtifactDefinitionManager;
      //TODO - Fix this soon!
      //return (StructuredArtifactDefinitionManager)
      //   ComponentManager.getInstance().get("structuredArtifactDefinitionManager");
   }

   public void setStructuredArtifactDefinitionManager(StructuredArtifactDefinitionManager structuredArtifactDefinitionManager) {
      this.structuredArtifactDefinitionManager = structuredArtifactDefinitionManager;
   }

   public AgentManager getAgentManager() {
      return agentManager;
   }

   public void setAgentManager(AgentManager agentManager) {
      this.agentManager = agentManager;
   }

   public AuthenticationManager getAuthManager() {
      return authManager;
   }

   public void setAuthManager(AuthenticationManager authManager) {
      this.authManager = authManager;
   }

   public GuidanceManager getGuidanceManager() {
      return guidanceManager;
   }

   public void setGuidanceManager(GuidanceManager guidanceManager) {
      this.guidanceManager = guidanceManager;
   }

   protected Id getToolId() {
      Placement placement = ToolManager.getCurrentPlacement();
      return idManager.getId(placement.getId());
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

   public ContentHostingService getContentHosting() {
      return contentHosting;
   }

   public void setContentHosting(ContentHostingService contentHosting) {
      this.contentHosting = contentHosting;
   }


   public boolean importResource(Id worksiteId, String nodeId)
   {

      String id = getContentHosting().resolveUuid(nodeId);
      try {
         ContentResource resource = getContentHosting().getResource(id);
         MimeType  mimeType = new MimeType(resource.getContentType());

         if(mimeType.equals(new MimeType("application/zip")) ||
               mimeType.equals(new MimeType("application/x-zip-compressed"))) {
            InputStream zipContent = resource.streamContent();
            Wizard bean = importWizard(worksiteId, zipContent);

            return bean != null;
         } else {
            throw new OspException("Unsupported file type");
         }
      } catch(ServerOverloadException soe) {
            logger.warn(soe);
      } catch(IOException ioe) {
            logger.warn(ioe);
      } catch(PermissionException pe) {
         logger.warn("Failed loading content: no permission to view file", pe);
      } catch(TypeException te) {
         logger.warn("Wrong type", te);
      } catch(IdUnusedException iue) {
         logger.warn("UnusedId: ", iue);
      }
      return false;
   }
   
   private Wizard importWizard(Id worksiteId, InputStream in) throws IOException
   {
      ZipInputStream zis = new ZipInputStream(in);

      Wizard bean = readWizardFromZip(zis, worksiteId.getValue());
      return bean;
   }

   private static final String IMPORT_CREATE_DATE_KEY = "createDate";
   private static final String IMPORT_EVALUATORS_KEY = "evaluators";
   //private static final String IMPORT_STYLES_KEY = "style";
   private Wizard readWizardFromZip(ZipInputStream zis, String worksiteId) throws IOException
   {
      ZipEntry currentEntry = zis.getNextEntry();

       if(currentEntry == null)
         return null;

       Map     importData = new HashMap();
      Wizard   wizard = new Wizard(null, getAuthManager().getAgent(), // TODO: parameterize toolid
                              worksiteId, PortalService.getCurrentToolId());
      String tempDirName = getIdManager().createId().getValue();

      // set values not coming from the zip
      wizard.setCreated(new Date(System.currentTimeMillis()));
      wizard.setModified(wizard.getCreated());

      importData.put(IMPORT_CREATE_DATE_KEY, wizard.getCreated());
      importData.put(IMPORT_EVALUATORS_KEY, new HashMap()); // key: userid  value: isRole
      //importData.put(IMPORT_STYLES_KEY, new HashMap());

      Map formsMap = new Hashtable();
      Map guidanceMap = null;
      Map styleMap = null;
      Map resourceMap = new Hashtable();
      try {
         boolean gotFile = false;
         
         // read the wizard
         readWizardXML(wizard, zis, importData);

         ContentCollectionEdit fileParent = getFileDir(tempDirName);

         currentEntry = zis.getNextEntry();
         while(currentEntry != null) {
            if(!currentEntry.isDirectory()) {
               if(currentEntry.getName().startsWith("forms/")) {
                  processMatrixForm(currentEntry, zis, formsMap,
                        getIdManager().getId(worksiteId));
               } else if(currentEntry.getName().startsWith("guidance/")) {
                  guidanceMap = processMatrixGuidance(fileParent, worksiteId, zis);
                  gotFile = true;
               } else if(currentEntry.getName().startsWith("style/")) {
                  styleMap = processMatrixStyle(fileParent, worksiteId, zis);
                  gotFile = true;
               } else {
                  importAttachmentRef(fileParent, currentEntry, worksiteId, zis, resourceMap);
                  gotFile = true;
               }
            }
            zis.closeEntry();
            currentEntry = zis.getNextEntry();
         }

         if (gotFile) {
            fileParent.getPropertiesEdit().addProperty(
                  ResourceProperties.PROP_DISPLAY_NAME, wizard.getName());
            getContentHosting().commitCollection(fileParent);
         }
         else {
            getContentHosting().cancelCollection(fileParent);
         }
         // the wizard needs to be saved so it has an id
         // the id is needed because guidance needs the security qualifier
         //wizard = saveWizard(wizard);
         wizard.setId(getIdManager().createId());
         
         replaceIds(wizard, guidanceMap, formsMap, styleMap);

         // save the wizard
         wizard = saveWizard(wizard);

         // set the wizard evaluators
         Map wizardEvaluators = (Map)importData.get(IMPORT_EVALUATORS_KEY);
         for(Iterator i = wizardEvaluators.keySet().iterator(); i.hasNext(); ) {
            String userId = (String)i.next();

            if (userId.startsWith("/site/")) {
               // it's a role
               String[] agentValues = userId.split("/");

               userId = userId.replaceAll(agentValues[2], worksiteId);
            }
            Agent agent = agentManager.getAgent(idManager.getId(userId));
            if(agent != null)
               authorizationFacade.createAuthorization(agent,
                  WizardFunctionConstants.EVALUATE_WIZARD, wizard.getId());
         }

         //set the authorization for the pages
         setAuthnCat(wizard.getRootCategory(), worksiteId);

      } catch(Exception e) {
         throw new RuntimeException(e);
      }
      finally {
         try {
            zis.closeEntry();
         }
         catch (IOException e) {
            logger.error("", e);
         }
      }
      return wizard;
   }

   private void setAuthnCat(WizardCategory cat, String worksite) {

      List pages = cat.getChildPages();
      for(Iterator i = pages.iterator(); i.hasNext(); ) {
         WizardPageSequence sequence = (WizardPageSequence)i.next();
         WizardPageDefinition pageDef = sequence.getWizardPageDefinition();

         for(Iterator ii = pageDef.getEvaluators().iterator(); ii.hasNext(); ) {
            String strId = (String)ii.next();

            if (strId.startsWith("/site/")) {
               // it's a role
               String[] agentValues = strId.split("/");

               strId = strId.replaceAll(agentValues[2], worksite);
            }
            Agent agent = agentManager.getAgent(idManager.getId(strId));

            if(agent != null)
               authorizationFacade.createAuthorization(agent,
                  MatrixFunctionConstants.EVALUATE_MATRIX, pageDef.getId());
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

   protected Map processMatrixGuidance(ContentCollection parent, String siteId,
                                       ZipInputStream zis) throws IOException {
      return getGuidanceManager().importGuidanceList(parent, siteId, zis);
   }

   protected Map processMatrixStyle(ContentCollection parent, String siteId,
         ZipInputStream zis) throws IOException {
      return getStyleManager().importStyleList(parent, siteId, zis);
   }
   
   protected ContentCollectionEdit getFileDir(String origName) throws InconsistentException,
         PermissionException, IdUsedException, IdInvalidException, IdUnusedException, TypeException {
      ContentCollection collection = getUserCollection();
      String childId = collection.getId() + origName;
      return getContentHosting().addCollection(childId);
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
               worksite, zis, true, true, false);
      } catch(ImportException ie) {
         throw new RuntimeException("the structured artifact failed to import", ie);
      }

      formMap.put(oldId, bean.getId().getValue());
   }

   protected void importAttachmentRef(ContentCollection fileParent, ZipEntry currentEntry, String siteId,
                                      ZipInputStream zis, Map resourceMap) {
      File file = new File(currentEntry.getName());

      MimeType mimeType = new MimeType(file.getParentFile().getParentFile().getParent(),
         file.getParentFile().getParentFile().getName());

      String contentType = mimeType.getValue();

      String oldId = file.getParentFile().getName();

      try {
         ByteArrayOutputStream bos = new ByteArrayOutputStream();
         int c = zis.read();

         while (c != -1) {
            bos.write(c);
            c = zis.read();
         }

         String fileId = ((fileParent!=null)?fileParent.getId():"") + file.getName();
         ContentResourceEdit resource = getContentHosting().addResource(fileId);
         ResourcePropertiesEdit resourceProperties = resource.getPropertiesEdit();
         resourceProperties.addProperty (ResourceProperties.PROP_DISPLAY_NAME, file.getName());
         resource.setContent(bos.toByteArray());
         resource.setContentType(contentType);
         getContentHosting().commitResource(resource);
         resourceMap.put(oldId, resource.getReference());
      }
      catch (Exception exp) {
         throw new RuntimeException(exp);
      }
   }

   private boolean readWizardXML(Wizard wizard, InputStream inStream, Map importData)
   {
      SAXBuilder builder = new SAXBuilder();
      Map evaluatorsMap = (Map)importData.get(IMPORT_EVALUATORS_KEY);
      //Map stylesMap = (Map)importData.get(IMPORT_STYLES_KEY);

      try {
         byte []bytes = readStreamToBytes(inStream);
         Document document = builder.build(new ByteArrayInputStream(bytes));

         Element topNode = document.getRootElement();

         wizard.setName(topNode.getChildTextTrim("name"));
         wizard.setDescription(topNode.getChildTextTrim("description"));
         wizard.setKeywords(topNode.getChildTextTrim("keywords"));
         wizard.setType(topNode.getChildTextTrim("type"));
         wizard.setSequence(Integer.parseInt(topNode.getChildTextTrim("sequence")));

         // Read the evaluators
         List evaluators = topNode.getChild("evaluators").getChildren("evaluator");
         for(Iterator i = evaluators.iterator(); i.hasNext(); ) {
            Element evaluator = (Element)i.next();

            String userId = evaluator.getTextTrim();
            boolean isRole = evaluator.getAttribute("isRole").getBooleanValue();

            evaluatorsMap.put(userId, new Boolean(isRole));
         }

         // read the evaluation, review, reflection
         Element workflow = topNode.getChild("workflow");

         String wfType, wfId;

         wfType = workflow.getChildTextTrim("evaluationDeviceType");
         wfId = workflow.getChildTextTrim("evaluationDevice");
         wizard.setEvaluationDeviceType(wfType);
         wizard.setEvaluationDevice(idManager.getId(wfId));

         wfType = workflow.getChildTextTrim("reflectionDeviceType");
         wfId = workflow.getChildTextTrim("reflectionDevice");
         wizard.setReflectionDeviceType(wfType);
         wizard.setReflectionDevice(idManager.getId(wfId));

         wfType = workflow.getChildTextTrim("reviewDeviceType");
         wfId = workflow.getChildTextTrim("reviewDevice");
         wizard.setReviewDeviceType(wfType);
         wizard.setReviewDevice(idManager.getId(wfId));

         // read the wizard guidance to the list
         String guidanceIdStr = topNode.getChildTextTrim("guidance");
         wizard.setGuidanceId(idManager.getId(guidanceIdStr));

         // read the categories/pages
         readCategoriesAndPages(wizard.getRootCategory(), topNode.getChild("category"), importData);

          //   pull the styles from the xml
         //    WizardStyleItem only works with Resources not IDs
         
         String styleIdStr = topNode.getChildTextTrim("style");
         wizard.setStyleId(getIdManager().getId(styleIdStr));
         //stylesMap.put(styleId, null);
         
      } catch(Exception jdome) {
            throw new OspException(jdome);
      }
      return true;
   }

   private void readCategoriesAndPages(WizardCategory category, Element categoryNode, Map importData)
            throws DataConversionException
   {
      category.setCreated((Date)importData.get(IMPORT_CREATE_DATE_KEY));
      category.setModified((Date)importData.get(IMPORT_CREATE_DATE_KEY));

      category.setTitle(categoryNode.getChildTextTrim("title"));
      category.setDescription(categoryNode.getChildTextTrim("description"));
      category.setKeywords(categoryNode.getChildTextTrim("keywords"));
      category.setSequence(Integer.parseInt(categoryNode.getChildTextTrim("sequence")));

      List pageSequences = categoryNode.getChild("pages").getChildren("pageSequence");
      List pages = new ArrayList();
      for(Iterator i = pageSequences.iterator(); i.hasNext(); ) {
         Element pageSequenceNode = (Element)i.next();
         WizardPageSequence pageSequence = new WizardPageSequence();

         pageSequence.setCategory(category);
         pageSequence.setTitle(pageSequenceNode.getChildTextTrim("title"));
         pageSequence.setSequence(Integer.parseInt(
               pageSequenceNode.getChildTextTrim("sequence")));

         Element pageDefNode = pageSequenceNode.getChild("pageDef");
         WizardPageDefinition wizardPageDefinition = new WizardPageDefinition();


         wizardPageDefinition.setTitle(pageDefNode.getChildTextTrim("title"));
         wizardPageDefinition.setDescription(pageDefNode.getChildTextTrim("description"));
         wizardPageDefinition.setInitialStatus(pageDefNode.getChildTextTrim("initialStatus"));

         // read the page workflow
         String wfType, wfId;
         Element workflow = pageDefNode.getChild("workflow");

         wfType = workflow.getChildTextTrim("evaluationDeviceType");
         wfId = workflow.getChildTextTrim("evaluationDevice");
         wizardPageDefinition.setEvaluationDeviceType(wfType);
         wizardPageDefinition.setEvaluationDevice(idManager.getId(wfId));

         wfType = workflow.getChildTextTrim("reflectionDeviceType");
         wfId = workflow.getChildTextTrim("reflectionDevice");
         wizardPageDefinition.setReflectionDeviceType(wfType);
         wizardPageDefinition.setReflectionDevice(idManager.getId(wfId));

         wfType = workflow.getChildTextTrim("reviewDeviceType");
         wfId = workflow.getChildTextTrim("reviewDevice");
         wizardPageDefinition.setReviewDeviceType(wfType);
         wizardPageDefinition.setReviewDevice(idManager.getId(wfId));

         // read the page guidance
         String guidanceIdStr = pageDefNode.getChildTextTrim("guidance");
         wizardPageDefinition.setGuidanceId(idManager.getId(guidanceIdStr));
         
         // read the page style
         String styleIdStr = pageDefNode.getChildTextTrim("style");
         wizardPageDefinition.setStyleId(getIdManager().getId(styleIdStr));

         // read the into about additional forms
         if(pageDefNode.getChild("additionalForms") != null) {
            List forms = pageDefNode.getChild("additionalForms").getChildren("form");
            List formsList = new ArrayList();
            for(Iterator ii = forms.iterator(); ii.hasNext(); ) {
               Element form = (Element)ii.next();

               String formId = form.getTextTrim();
               formsList.add(formId);
            }
            wizardPageDefinition.setAdditionalForms(formsList);
         }

         // read the evaluators of the page, they are external to the wizard, store
         if(pageDefNode.getChild("evaluators") != null) {
            List evaluators = pageDefNode.getChild("evaluators").getChildren("evaluator");
            List evaluatorsList = new ArrayList();
            for(Iterator ii = evaluators.iterator(); ii.hasNext(); ) {
               Element evaluator = (Element)ii.next();

               String evaluatorId = evaluator.getTextTrim();
               boolean isRole = false;
               if(evaluator.getAttribute("isRole") != null)
                  isRole = evaluator.getAttribute("isRole").getBooleanValue();
               evaluatorsList.add(evaluatorId);
            }
            wizardPageDefinition.setEvaluators(evaluatorsList);
         }

         pageSequence.setWizardPageDefinition(wizardPageDefinition);
         pages.add(pageSequence);
      }
      category.setChildPages(pages);


      List categoryNodes = categoryNode.getChild("childCategories").getChildren("category");
      List categories = new ArrayList();
      for(Iterator i = categoryNodes.iterator(); i.hasNext(); ) {
         Element pageSequenceNode = (Element)i.next();
         WizardCategory childCategory = new WizardCategory();
         childCategory.setParentCategory(category);
         readCategoriesAndPages(childCategory, pageSequenceNode, importData);
         categories.add(childCategory);
      }
      category.setChildCategories(categories);
   }


   protected void replaceIds(Wizard wizard, Map guidanceMap, Map formsMap, Map styleMap)
   {
      replaceCatIds(wizard.getRootCategory(), guidanceMap, formsMap, styleMap);

      if(wizard.getEvaluationDevice() != null && wizard.getEvaluationDevice().getValue() != null)
         wizard.setEvaluationDevice(idManager.getId(
            (String)formsMap.get(wizard.getEvaluationDevice().getValue())  ));

      if(wizard.getReflectionDevice() != null && wizard.getReflectionDevice().getValue() != null)
         wizard.setReflectionDevice(idManager.getId(
            (String)formsMap.get(wizard.getReflectionDevice().getValue())  ));

      if(wizard.getReviewDevice() != null && wizard.getReviewDevice().getValue() != null)
         wizard.setReviewDevice(idManager.getId(
            (String)formsMap.get(wizard.getReviewDevice().getValue())  ));

      if(wizard.getGuidanceId() != null && wizard.getGuidanceId().getValue() != null &&
            wizard.getGuidanceId().getValue().length() > 0) {
         Guidance wizardGuidance = (Guidance)guidanceMap.get( wizard.getGuidanceId().getValue());
         if(wizardGuidance == null)
            throw new NullPointerException("Guidance for Wizard was not found");
         
         wizardGuidance.setSecurityQualifier(wizard.getId());
         getGuidanceManager().saveGuidance(wizardGuidance);
         wizard.setGuidanceId( wizardGuidance.getId() );
      }
      if (wizard.getStyleId() != null && wizard.getStyleId().getValue() != null && 
            wizard.getStyleId().getValue().length() > 0) {
         Style wizardStyle = (Style)styleMap.get( wizard.getStyleId().getValue());
         if(wizardStyle == null)
            throw new NullPointerException("Style for Wizard was not found");
         getStyleManager().storeStyle(wizardStyle);
         wizard.setStyle(wizardStyle);
      }
   }
   protected void replaceCatIds(WizardCategory cat, Map guidanceMap, Map formsMap, Map styleMap)
   {
      for(Iterator i = cat.getChildPages().iterator(); i.hasNext(); ) {
         WizardPageSequence sequence = (WizardPageSequence)i.next();
         WizardPageDefinition definition = (WizardPageDefinition)sequence.getWizardPageDefinition();

         if(definition.getEvaluationDevice() != null && definition.getEvaluationDevice().getValue() != null)
            definition.setEvaluationDevice(idManager.getId(
               (String)formsMap.get(definition.getEvaluationDevice().getValue())  ));

         if(definition.getReflectionDevice() != null && definition.getReflectionDevice().getValue() != null)
            definition.setReflectionDevice(idManager.getId(
               (String)formsMap.get(definition.getReflectionDevice().getValue())  ));

         if(definition.getReviewDevice() != null && definition.getReviewDevice().getValue() != null)
            definition.setReviewDevice(idManager.getId(
               (String)formsMap.get(definition.getReviewDevice().getValue())  ));

         List newAddForms = new ArrayList();
         for(Iterator ii = definition.getAdditionalForms().iterator(); ii.hasNext(); ) {
            String addForm = (String)ii.next();
            if(addForm != null)
               newAddForms.add(formsMap.get(addForm));
         }
         definition.setAdditionalForms(newAddForms);

         if(definition.getGuidanceId() != null && definition.getGuidanceId().getValue() != null && 
               definition.getGuidanceId().getValue().length() > 0) {
            Guidance pageDefGuidance = (Guidance)guidanceMap.get( definition.getGuidanceId().getValue() );
            
            if(pageDefGuidance == null)
               throw new NullPointerException("Guidance for Wizard Page was not found");
            
            pageDefGuidance.setSecurityQualifier(definition.getId());
            getGuidanceManager().saveGuidance(pageDefGuidance);
            definition.setGuidanceId( pageDefGuidance.getId() );

            definition.setGuidance(pageDefGuidance);
         }
         
         if(definition.getStyleId() != null && definition.getStyleId().getValue() != null && 
               definition.getStyleId().getValue().length() > 0) {
            Style pageDefStyle= (Style)styleMap.get( definition.getStyleId().getValue() );
            
            if(pageDefStyle== null)
               throw new NullPointerException("Style for Wizard Page was not found");
            
            getStyleManager().storeStyle(pageDefStyle);
            definition.setStyle(pageDefStyle);
         }
      }
      for(Iterator i = cat.getChildCategories().iterator(); i.hasNext(); ) {
         WizardCategory childCat = (WizardCategory)i.next();

         replaceCatIds(childCat, guidanceMap, formsMap, styleMap);
      }
   }
   
   
   
   
   


   public void packageForDownload(Map params, OutputStream out) throws IOException {

      String[] formIdObj = (String[])params.get(DOWNLOAD_WIZARD_ID_PARAM);
      packageWizardForExport(formIdObj[0], out);
   }


   public void packageWizardForExport(String wizardId, OutputStream os) throws IOException
   {
      getAuthorizationFacade().checkPermission(WizardFunctionConstants.EXPORT_WIZARD, getToolId());

      CheckedOutputStream checksum = new CheckedOutputStream(os, new Adler32());

      ZipOutputStream zos = new ZipOutputStream(new BufferedOutputStream(checksum));
      try {
         putWizardIntoZip(wizardId, zos);
      } catch(ServerOverloadException soe) {
         logger.warn(soe);
      }

      zos.finish();
      zos.flush();
   }

   /**
    * Puts the wizard definition xml into the zip, then places all the forms
    * into the stream, then
    * @param wizardId String the wizard to export
    * @param zos ZipOutputStream the place to export the wizard too
    * @throws IOException
    * @throws ServerOverloadException
    */
   public void putWizardIntoZip(String wizardId, ZipOutputStream zos)
                     throws IOException, ServerOverloadException
   {

      Map  exportForms = new HashMap(); /* key: form id   value: not needed */
      Map  exportFiles = new HashMap(); /* key: uuid   value: ContentResource  */
      List exportGuidanceIds = new ArrayList(); /* List of guidance id */
      Set exportStyleIds = new HashSet(); /* Set of style id */

      Wizard   wiz = getWizard(wizardId);
      Document document = new Document(wizardToXML(wiz, exportForms, exportFiles, exportGuidanceIds, exportStyleIds));
      ZipEntry newfileEntry = null;


      storeFileInZip(zos, new java.io.StringReader(
               (new XMLOutputter()).outputString(document)), "wizardDefinition.xml");

      // put the forms into the zip
      for(Iterator i = exportForms.keySet().iterator(); i.hasNext(); ) {
         String id = (String)i.next();

         if(id != null && id.length() > 0) {
            newfileEntry = new ZipEntry("forms/" + id + ".form");

            zos.putNextEntry(newfileEntry);
            structuredArtifactDefinitionManager.packageFormForExport(id, zos);
            zos.closeEntry();
         }
      }

      // put the resources into the zip
      for(Iterator i = exportFiles.keySet().iterator(); i.hasNext(); ) {
         String id = (String)i.next();

         if(id != null && id.length() > 0) {
            ContentResource resource = (ContentResource)exportFiles.get(id);
            storeFileInZip(zos,
                  resource.streamContent(),
                  exportPathFromResource(resource, id)
                   );
         }
      }

      // put the guidance into the stream
      if(exportGuidanceIds.size() > 0) {
         newfileEntry = new ZipEntry("guidance/guidanceList");
         zos.putNextEntry(newfileEntry);
         getGuidanceManager().packageGuidanceForExport(exportGuidanceIds, zos);
         zos.closeEntry();
      }
      //CWM add style here
      // put the guidance into the stream
      if(exportStyleIds.size() > 0) {
         newfileEntry = new ZipEntry("style/styleList");
         zos.putNextEntry(newfileEntry);
         getStyleManager().packageStyleForExport(exportStyleIds, zos);
         zos.closeEntry();
      }

      exportForms.clear();
      exportFiles.clear();
      exportGuidanceIds.clear();
      exportStyleIds.clear();
   }

   private String exportPathFromResource(ContentResource resource, String id)
   {
      String fileName = resource.getProperties().getProperty(
               resource.getProperties().getNamePropDisplayName());
      return resource.getContentType() +"/" + id + "/" +
      fileName.substring(fileName.lastIndexOf('\\')+1);
   }

   private Element wizardToXML(Wizard wiz, Map exportForms, Map exportFiles, List exportGuidanceIds, Set exportStyleIds)
   {
       Element rootNode = new Element("ospiWizard");

       if(wiz == null)
         return rootNode;

       rootNode.setAttribute("formatVersion", "2.1");

       Element attrNode = new Element("name");
       attrNode.addContent(new CDATA(wiz.getName()));
       rootNode.addContent(attrNode);

       attrNode = new Element("description");
       attrNode.addContent(new CDATA(wiz.getDescription()));
       rootNode.addContent(attrNode);

       attrNode = new Element("keywords");
       attrNode.addContent(new CDATA(wiz.getKeywords()));
       rootNode.addContent(attrNode);

       attrNode = new Element("type");
       attrNode.addContent(new CDATA(wiz.getType()));
       rootNode.addContent(attrNode);

       attrNode = new Element("sequence");
       attrNode.addContent(new CDATA(wiz.getSequence()+""));
       rootNode.addContent(attrNode);

       //   put the wizard evaluators into the xml
      Element evaluatorsNode = new Element("evaluators");
      Collection evaluators = getWizardEvaluators(wiz.getId(), true);
      for (Iterator i = evaluators.iterator(); i.hasNext();) {
         Agent agent = (Agent) i.next();
         attrNode = new Element("evaluator");
         attrNode.setAttribute("isRole", Boolean.toString(agent.isRole()));
         attrNode.addContent(new CDATA(agent.getId().getValue()));
         evaluatorsNode.addContent(attrNode);
      }
      rootNode.addContent(evaluatorsNode);


      // put the evaluation, review, reflection
       rootNode.addContent(putWorkflowObjectToXml(wiz, exportForms));


       //   add the wizard guidance to the list
       attrNode = new Element("guidance");
       if(wiz.getGuidanceId() != null) {
          exportGuidanceIds.add(wiz.getGuidanceId().getValue());
          attrNode.addContent(new CDATA(wiz.getGuidanceId().getValue()));
       }
       rootNode.addContent(attrNode);


       //   put the categories/pages into the xml
       rootNode.addContent(putCategoryToXml(wiz.getRootCategory(), exportForms, exportGuidanceIds, exportStyleIds));


       //  add the wizard style to the list
       attrNode = new Element("style");
       if (wiz.getStyle() != null) {
          exportStyleIds.add(wiz.getStyle().getId().getValue());
          attrNode.addContent(new CDATA(wiz.getStyle().getId().getValue()));
       }
       rootNode.addContent(attrNode);
       
       /*
       //   put the styles into the xml
       
       //for(Iterator i = wiz.getStyle().iterator(); i.hasNext(); ) {
           Style style = wiz.getStyle();
           String          resId = style.getStyleFile().getValue();
           String nodeId = getContentHosting().resolveUuid(resId);

          //Element       styleNode = new Element("style");
           //attrNode.addContent(new CDATA(nodeId));
           attrNode.addContent(new CDATA(style.getId().getValue()));
          //attrNode.addContent(styleNode);

         //String id = getContentHosting().resolveUuid(nodeId);
          ContentResource resource = null;
            try {
               resource = getContentHosting().getResource(nodeId);
            } catch(PermissionException pe) {
              logger.warn("Failed loading content: no permission to view file", pe);
            } catch(TypeException pe) {
              logger.warn("Wrong type", pe);
          } catch(IdUnusedException pe) {
              logger.warn("UnusedId: ", pe);
          }

          exportFiles.put(nodeId, resource);
       //}
       rootNode.addContent(attrNode);
*/
       return rootNode;
   }


   private Element putCategoryToXml(WizardCategory cat, Map exportForms, List exportGuidanceIds, Set exportStyleIds)
   {
      Element categoryNode = new Element("category");

      if(cat == null)
         return categoryNode;

      Element attrNode = new Element("title");
      attrNode.addContent(new CDATA(cat.getTitle()));
      categoryNode.addContent(attrNode);

      attrNode = new Element("description");
      attrNode.addContent(new CDATA(cat.getDescription()));
      categoryNode.addContent(attrNode);

      attrNode = new Element("keywords");
      attrNode.addContent(new CDATA(cat.getKeywords()));
      categoryNode.addContent(attrNode);

      attrNode = new Element("sequence");
      attrNode.addContent(new CDATA(cat.getSequence()+""));
      categoryNode.addContent(attrNode);

      Element pagesNode = new Element("pages");
      for(Iterator i = cat.getChildPages().iterator(); i.hasNext(); ) {
        WizardPageSequence pageSequence = (WizardPageSequence)i.next();
        pagesNode.addContent(putPageSequenceToXml(pageSequence, exportForms, exportGuidanceIds, exportStyleIds));
      }
      categoryNode.addContent(pagesNode);

      Element childCategoriesNode = new Element("childCategories");
      for(Iterator i = cat.getChildCategories().iterator(); i.hasNext(); ) {
         WizardCategory childCat = (WizardCategory)i.next();
         childCategoriesNode.addContent(putCategoryToXml(childCat, exportForms, exportGuidanceIds, exportStyleIds));
      }
      categoryNode.addContent(childCategoriesNode);

      return categoryNode;
   }

   private Element putPageSequenceToXml(WizardPageSequence pageSequence,
                              Map exportForms, List exportGuidanceIds, Set exportStyleIds)
   {
      Element pageSequenceNode = new Element("pageSequence");

      if(pageSequence == null)
         return pageSequenceNode;

      Element attrNode = new Element("title");
      attrNode.addContent(new CDATA(pageSequence.getTitle()));
      pageSequenceNode.addContent(attrNode);

      attrNode = new Element("sequence");
      attrNode.addContent(new CDATA(pageSequence.getSequence() + ""));
      pageSequenceNode.addContent(attrNode);

      pageSequenceNode.addContent(putPageDefinitionToXml(
            pageSequence.getWizardPageDefinition(), exportForms, exportGuidanceIds, exportStyleIds));

      return pageSequenceNode;
   }

   private Element putPageDefinitionToXml(WizardPageDefinition pageDef,
                     Map exportForms, List exportGuidanceIds, Set exportStyleIds)
   {
      Element pageDefNode = new Element("pageDef");

      if(pageDef == null)
         return pageDefNode;

      pageDefNode.addContent(putWorkflowObjectToXml(pageDef, exportForms));

      Element attrNode = new Element("title");
      attrNode.addContent(new CDATA(pageDef.getTitle()));
      pageDefNode.addContent(attrNode);

      attrNode = new Element("description");
      attrNode.addContent(new CDATA(pageDef.getDescription()));
      pageDefNode.addContent(attrNode);

      attrNode = new Element("initialStatus");
      attrNode.addContent(new CDATA(pageDef.getInitialStatus()));
      pageDefNode.addContent(attrNode);


      Element additionalFormsNode = new Element("additionalForms");
      for(Iterator i = pageDef.getAdditionalForms().iterator(); i.hasNext(); ) {
         String additionalForm = (String)i.next();

         attrNode = new Element("form");
         attrNode.addContent(new CDATA(additionalForm));
         additionalFormsNode.addContent(attrNode);

         exportForms.put(additionalForm, new Integer(0));
      }
      pageDefNode.addContent(additionalFormsNode);

      attrNode = new Element("guidance");
      if(pageDef.getGuidance() != null && pageDef.getGuidance().getId() != null) {
         exportGuidanceIds.add(pageDef.getGuidance().getId().getValue());
         attrNode.addContent(new CDATA(pageDef.getGuidance().getId().getValue()));
      }
      pageDefNode.addContent(attrNode);

      attrNode = new Element("style");
      if (pageDef.getStyle() != null && pageDef.getStyle().getId() != null) {
         exportStyleIds.add(pageDef.getStyle().getId().getValue());
         attrNode.addContent(new CDATA(pageDef.getStyle().getId().getValue()));
      }
      pageDefNode.addContent(attrNode);

      Element     evaluatorsNode = new Element("evaluators");
      Collection  evaluators = getWizardPageDefEvaluators(pageDef.getId(), true);
      for(Iterator i = evaluators.iterator(); i.hasNext(); ) {
         Agent agent = (Agent) i.next();
         attrNode = new Element("evaluator");
         attrNode.setAttribute("isRole", Boolean.toString(agent.isRole()));
         attrNode.addContent(new CDATA(agent.getId().getValue()));
         evaluatorsNode.addContent(attrNode);
      }
      pageDefNode.addContent(evaluatorsNode);

      return pageDefNode;
   }


   protected Collection getWizardPageDefEvaluators(Id wizardPageDefId, boolean useAgentId) {
         Collection evaluators = new HashSet();
         Collection viewerAuthzs = authorizationFacade.getAuthorizations(null,
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
   
   public WizardPageSequence getWizardPageSeqByDef(Id id) {
      Object[] params = new Object[]{id.getValue()};
      List seqs = getHibernateTemplate().find("from WizardPageSequence w where w.wizardPageDefinition=?", params);
      if (seqs.size() > 0)
         return (WizardPageSequence)seqs.get(0);
      
      return null;
   }
   
   public List getCompletedWizardPagesByPageDef(Id id) {
      Object[] params = new Object[]{id.getValue()};
      return getHibernateTemplate().find("from CompletedWizardPage w where w.wizardPageDefinition.wizardPageDefinition=?", params);
   }


   protected Collection getWizardEvaluators(Id wizardId, boolean useAgentId) {
         Collection evaluators = new HashSet();
         Collection viewerAuthzs = authorizationFacade.getAuthorizations(null,
              WizardFunctionConstants.EVALUATE_WIZARD, wizardId);
         for (Iterator i = viewerAuthzs.iterator(); i.hasNext();) {
            Authorization evaluator = (Authorization) i.next();
            if (useAgentId)
               evaluators.add(evaluator.getAgent());
            else
               evaluators.add(evaluator.getAgent().getId());
         }
         return evaluators;
      }

   /**
    * 2.1 - only does type="form".
    * @param objWorkflow
    * @return Element
    */
   private Element putWorkflowObjectToXml(ObjectWithWorkflow objWorkflow, Map exportForms)
   {
      Element workflowObjNode = new Element("workflow");

      Element attrNode = new Element("evaluationDevice");
      if(objWorkflow.getEvaluationDevice() != null)
         attrNode.addContent(new CDATA(objWorkflow.getEvaluationDevice().getValue()));
      workflowObjNode.addContent(attrNode);

      if(objWorkflow.getEvaluationDevice() != null)
         exportForms.put(objWorkflow.getEvaluationDevice().getValue(), new Integer(0));

      attrNode = new Element("evaluationDeviceType");
      attrNode.addContent(new CDATA(objWorkflow.getEvaluationDeviceType()));
      workflowObjNode.addContent(attrNode);

      attrNode = new Element("reflectionDevice");
      if(objWorkflow.getReflectionDevice() != null)
         attrNode.addContent(new CDATA(objWorkflow.getReflectionDevice().getValue()));
      workflowObjNode.addContent(attrNode);

      if(objWorkflow.getReflectionDevice() != null)
         exportForms.put(objWorkflow.getReflectionDevice().getValue(), new Integer(0));

      attrNode = new Element("reflectionDeviceType");
      attrNode.addContent(new CDATA(objWorkflow.getReflectionDeviceType()));
      workflowObjNode.addContent(attrNode);

      attrNode = new Element("reviewDevice");
      if(objWorkflow.getReviewDevice() != null)
         attrNode.addContent(new CDATA(objWorkflow.getReviewDevice().getValue()));
      workflowObjNode.addContent(attrNode);

      if(objWorkflow.getReviewDevice() != null)
         exportForms.put(objWorkflow.getReviewDevice().getValue(), new Integer(0));

      attrNode = new Element("reviewDeviceType");
      attrNode.addContent(new CDATA(objWorkflow.getReviewDeviceType()));
      workflowObjNode.addContent(attrNode);

      return workflowObjNode;
   }


   protected void storeFileInZip(ZipOutputStream zos, Reader in,
         String entryName) throws IOException {

      char data[] = new char[1024 * 10];

      if (File.separatorChar == '\\') {
         entryName = entryName.replace('\\', '/');
      }

      ZipEntry newfileEntry = new ZipEntry(entryName);

      zos.putNextEntry(newfileEntry);

      BufferedReader origin = new BufferedReader(in, data.length);
      OutputStreamWriter osw = new OutputStreamWriter(zos);
      int count;
      while ((count = origin.read(data, 0, data.length)) != -1) {
         osw.write(data, 0, count);
      }
      origin.close();
      osw.flush();
      zos.closeEntry();
      in.close();
   }


   protected void storeFileInZip(ZipOutputStream zos, InputStream in,
         String entryName) throws IOException {

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
      origin.close();
      zos.closeEntry();
      in.close();
   }

   private byte[] readStreamToBytes(InputStream inStream) throws IOException {
      ByteArrayOutputStream bytes = new ByteArrayOutputStream();
      byte data[] = new byte[10 * 1024];

      int count;
      while ((count = inStream.read(data, 0, 10 * 1024)) != -1) {
         bytes.write(data, 0, count);
      }
      byte[] tmp = bytes.toByteArray();
      bytes.close();
      return tmp;
   }

   public PresentableObjectHome getXmlRenderer() {
      return xmlRenderer;
   }

   public void setXmlRenderer(PresentableObjectHome xmlRenderer) {
      this.xmlRenderer = xmlRenderer;
   }

   public Element getArtifactAsXml(Artifact art) {
      return getXmlRenderer().getArtifactAsXml(art);
   }

   public Collection findByOwnerAndType(Id owner, String type) {
      return findByOwner(owner);
   }

   public Collection findByOwnerAndType(Id owner, String type, MimeType mimeType) {
      // TODO Auto-generated method stub
      return null;
   }

   public Collection findByOwner(Id owner) {
      return getCompletedWizards(owner.getValue());
   }

   public Collection findByWorksiteAndType(Id worksiteId, String type) {
      // TODO Auto-generated method stub
      return null;
   }

   public Collection findByWorksite(Id worksiteId) {
      // TODO Auto-generated method stub
      return null;
   }

   public Artifact load(Id id) {
      CompletedWizard cw = this.getCompletedWizard(id);
      cw.setHome(this);
      return cw;
   }

   public Collection findByType(String type) {
      // TODO Auto-generated method stub
      return null;
   }

   public boolean getLoadArtifacts() {
      // TODO Auto-generated method stub
      return false;
   }

   public void setLoadArtifacts(boolean loadArtifacts) {
      // TODO Auto-generated method stub

   }

   public Type getType() {
      return new org.sakaiproject.metaobj.shared.model.Type(idManager.getId("completedWizard"), "Completed Wizard");
   }

   public String getExternalType() {
      return getType().getId().getValue();
   }

   public Artifact createInstance() {
      Artifact instance = new CompletedWizard();
      prepareInstance(instance);
      return instance;
   }

   public void prepareInstance(Artifact object) {
      object.setHome(this);
   }

   public Artifact createSample() {
      // TODO Auto-generated method stub
      return null;
   }

   public Collection findByOwner(Agent owner) throws FinderException {
      // TODO Auto-generated method stub
      return null;
   }

   public boolean isInstance(Artifact testObject) {
      // TODO Auto-generated method stub
      return false;
   }

   public void refresh() {
      // TODO Auto-generated method stub

   }

   public String getExternalUri(Id artifactId, String name) {
      // TODO Auto-generated method stub
      return null;
   }

   public InputStream getStream(Id artifactId) {
      // TODO Auto-generated method stub
      return null;
   }

   public boolean isSystemOnly() {
      // TODO Auto-generated method stub
      return false;
   }

   public Class getInterface() {
      // TODO Auto-generated method stub
      return null;
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
}

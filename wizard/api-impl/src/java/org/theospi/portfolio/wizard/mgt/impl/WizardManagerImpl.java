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
import java.io.InputStreamReader;
import java.io.BufferedInputStream;
import java.io.InputStream;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
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
import org.sakaiproject.exception.IdUnusedException;
import org.sakaiproject.exception.PermissionException;
import org.sakaiproject.exception.TypeException;
import org.sakaiproject.exception.ServerOverloadException;
import org.sakaiproject.metaobj.shared.SharedFunctionConstants;
import org.sakaiproject.metaobj.shared.DownloadableManager;
import org.sakaiproject.metaobj.shared.mgt.AgentManager;
import org.sakaiproject.metaobj.shared.mgt.IdManager;
import org.sakaiproject.metaobj.shared.mgt.StructuredArtifactDefinitionManager;
import org.sakaiproject.metaobj.shared.model.Agent;
import org.sakaiproject.metaobj.shared.model.Id;
import org.sakaiproject.metaobj.shared.model.MimeType;
import org.sakaiproject.metaobj.shared.model.StructuredArtifactDefinitionBean;
import org.sakaiproject.metaobj.security.impl.sakai.AuthnManager;
import org.sakaiproject.metaobj.security.AuthenticationManager;
import org.sakaiproject.service.legacy.content.ContentHostingService;
import org.sakaiproject.service.legacy.content.ContentResource;
import org.sakaiproject.service.legacy.entity.EntityManager;
import org.sakaiproject.service.legacy.entity.Reference;
import org.sakaiproject.service.legacy.security.SecurityService;
import org.sakaiproject.service.legacy.site.Site;
import org.sakaiproject.service.legacy.site.SitePage;
import org.sakaiproject.service.legacy.site.ToolConfiguration;
import org.sakaiproject.service.legacy.site.cover.SiteService;
import org.sakaiproject.service.framework.portal.cover.PortalService;
import org.springframework.orm.hibernate.support.HibernateDaoSupport;
import org.theospi.portfolio.guidance.model.*;
import org.theospi.portfolio.security.AllowMapSecurityAdvisor;
import org.theospi.portfolio.security.Authorization;
import org.theospi.portfolio.security.AuthorizationFacade;
import org.theospi.portfolio.shared.mgt.ContentEntityUtil;
import org.theospi.portfolio.shared.model.OspException;
import org.theospi.portfolio.shared.model.ObjectWithWorkflow;
import org.theospi.portfolio.wizard.impl.WizardEntityProducer;
import org.theospi.portfolio.wizard.mgt.WizardManager;
import org.theospi.portfolio.wizard.model.*;
import org.theospi.portfolio.wizard.WizardFunctionConstants;
import org.theospi.portfolio.workflow.mgt.WorkflowManager;
import org.theospi.portfolio.workflow.model.Workflow;
import org.theospi.portfolio.workflow.model.WorkflowItem;
import org.theospi.portfolio.matrix.MatrixFunctionConstants;
import org.theospi.portfolio.matrix.model.Attachment;
import org.theospi.portfolio.matrix.model.Cell;
import org.theospi.portfolio.matrix.model.WizardPageDefinition;
import org.theospi.portfolio.guidance.mgt.GuidanceManager;
import net.sf.hibernate.HibernateException;

public class WizardManagerImpl extends HibernateDaoSupport implements WizardManager, DownloadableManager {

   static final private String	DOWNLOAD_WIZARD_ID_PARAM = "wizardId";
	   
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
   
   private Map  exportForms = new HashMap();
   /** key: uuid   value: ContentResource  */
   private Map  exportFiles = new HashMap();
   private List exportGuidanceIds = new ArrayList();

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
      
      for (Iterator i=wizard.getWizardStyleItems().iterator();i.hasNext();) {
         WizardStyleItem item = (WizardStyleItem)i.next();
         refs.add(item.getBaseReference().getBase().getReference());
      }

      WizardCategory rootCategory = (WizardCategory)wizard.getRootCategory();
      loadCategory(rootCategory, refs);

      getSecurityService().pushAdvisor(new AllowMapSecurityAdvisor(ContentHostingService.EVENT_RESOURCE_READ,
         refs));

      return wizard;
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
      if (bean != null) {
         saveWizard(bean);
      }
      return bean;
   }
   
   
   private Wizard readWizardFromZip(ZipInputStream zis, String worksiteId) throws IOException
   {
	   ZipEntry currentEntry = zis.getNextEntry();

       if(currentEntry == null)
    	   return null;
	   
	   Wizard wizard = new Wizard(null, getAuthManager().getAgent(), 
			   						worksiteId, PortalService.getCurrentToolId());
	   
	   //	set values not coming from the zip
	   wizard.setCreated(new Date(System.currentTimeMillis()));
	   wizard.setModified(wizard.getCreated());
	   
	   //	read the wizard
	   readWizardXML(wizard, zis, wizard.getCreated());
	   
	   //	TODO: link together 
	   return wizard;
   }
   
   private boolean readWizardXML(Wizard wizard, InputStream inStream, Date createdDate)
   {
		SAXBuilder builder = new SAXBuilder();

		try {
		   byte []bytes = readStreamToBytes(inStream);
		   Document document	= builder.build(new ByteArrayInputStream(bytes));

		   Element topNode = document.getRootElement();

		   wizard.setName(topNode.getChildTextTrim("name"));
		   wizard.setDescription(topNode.getChildTextTrim("description"));
		   wizard.setKeywords(topNode.getChildTextTrim("keywords"));
		   wizard.setType(topNode.getChildTextTrim("type"));
		   wizard.setSequence(Integer.parseInt(topNode.getChildTextTrim("sequence")));
		   
		   //	Read the evaluators
		   List evaluators = topNode.getChild("evaluators").getChildren("evaluator");
		   for(Iterator i = evaluators.iterator(); i.hasNext(); ) {
			   Element evaluator = (Element)i.next();
			   
			   String userId = evaluator.getTextTrim();
			   boolean isRole = evaluator.getAttribute("isRole").getBooleanValue();
		   }
		   
		   //	read the evaluation, review, reflection
		   Element workflow = topNode.getChild("workflow");

		   workflow.getChildTextTrim("evaluationDevice");
		   workflow.getChildTextTrim("evaluationDeviceType");
		   workflow.getChildTextTrim("reflectionDevice");
		   workflow.getChildTextTrim("reflectionDeviceType");
		   workflow.getChildTextTrim("reviewDevice");
		   workflow.getChildTextTrim("reviewDeviceType");
		   
		   //	read the wizard guidance to the list
		   String guidanceIdStr = topNode.getChildTextTrim("guidance");
		   
		   //	read the categories/pages
		   readCategoriesAndPages(wizard.getRootCategory(), topNode.getChild("category"), createdDate);
		   
		    //	put the styles into the xml
		   List styles = topNode.getChild("styles").getChildren("style");
		   for(Iterator i = styles.iterator(); i.hasNext(); ) {
			   Element style = (Element)i.next();
			   
			   String styleId = style.getTextTrim();
		   }
		} catch(Exception jdome) {
	         throw new OspException(jdome);
		}
		return true;
   }
   
   private void readCategoriesAndPages(WizardCategory category, Element categoryNode, Date createdDate)
   			throws DataConversionException
   {
	   category.setCreated(createdDate);
	   category.setModified(createdDate);
	   
	   category.setTitle(categoryNode.getChildTextTrim("title"));
	   category.setDescription(categoryNode.getChildTextTrim("description"));
	   category.setKeywords(categoryNode.getChildTextTrim("keywords"));
	   category.setSequence(Integer.parseInt(categoryNode.getChildTextTrim("sequence")));

	   List pageSequences = categoryNode.getChild("pages").getChildren("pageSequence");
	   List pages = new ArrayList();
	   for(Iterator i = pageSequences.iterator(); i.hasNext(); ) {
		   Element pageSequenceNode = (Element)i.next();
		   WizardPageSequence pageSequence = new WizardPageSequence();
		   
		   pageSequence.setTitle(pageSequenceNode.getChildTextTrim("title"));
		   pageSequence.setSequence(Integer.parseInt(
				   pageSequenceNode.getChildTextTrim("sequence")));

		   Element pageDefNode = pageSequenceNode.getChild("pageDef");
		   WizardPageDefinition wizardPageDefinition = new WizardPageDefinition();
		   
		   wizardPageDefinition.setTitle(pageDefNode.getChildTextTrim("title"));
		   wizardPageDefinition.setDescription(pageDefNode.getChildTextTrim("description"));
		   wizardPageDefinition.setInitialStatus(pageDefNode.getChildTextTrim("initialStatus"));
		   
		   pageDefNode.getChildTextTrim("evaluationDevice");
		   pageDefNode.getChildTextTrim("evaluationDeviceType");
		   pageDefNode.getChildTextTrim("reflectionDevice");
		   pageDefNode.getChildTextTrim("reflectionDeviceType");
		   pageDefNode.getChildTextTrim("reviewDevice");
		   pageDefNode.getChildTextTrim("reviewDeviceType");
		   
		   pageDefNode.getChildTextTrim("guidance");

		   List forms = categoryNode.getChild("additionalForms").getChildren("form");
		   for(Iterator ii = forms.iterator(); ii.hasNext(); ) {
			   Element form = (Element)ii.next();
			   
			   String formId = form.getTextTrim();
		   }

		   List evaluators = categoryNode.getChild("evaluators").getChildren("evaluator");
		   for(Iterator ii = evaluators.iterator(); ii.hasNext(); ) {
			   Element evaluator = (Element)ii.next();
			   
			   String formId = evaluator.getTextTrim();
			   boolean isRole = evaluator.getAttribute("isRole").getBooleanValue();
		   }
		   
		   pageSequence.setWizardPageDefinition(wizardPageDefinition);
		   pages.add(pageSequence);
	   }
	   category.setChildPages(pages);

	   
	   List categoryNodes = categoryNode.getChild("childCategories").getChildren("category");
	   List categories = new ArrayList();
	   for(Iterator i = pageSequences.iterator(); i.hasNext(); ) {
		   Element pageSequenceNode = (Element)i.next();
		   WizardCategory childCategory = new WizardCategory();
		   readCategoriesAndPages(childCategory, pageSequenceNode, createdDate);
		   categories.add(childCategory);
	   }
	   category.setChildCategories(categories);
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
   
   public void putWizardIntoZip(String wizardId, ZipOutputStream zos) 
   						throws IOException, ServerOverloadException
   {
	   //	make 100% sure that these are empty
	   exportForms.clear();
	   exportFiles.clear();
	   exportGuidanceIds.clear();
	   
	   Wizard	wiz = getWizard(wizardId);
	   Document document = new Document(wizardToXML(wiz));
	   ZipEntry newfileEntry = null;
	   

	   storeFileInZip(zos, new java.io.StringReader(
					(new XMLOutputter()).outputString(document)), "wizardDefinition.xml");
	   
	   //	put the forms into the zip
	   for(Iterator i = exportForms.keySet().iterator(); i.hasNext(); ) {
		   String id = (String)i.next();
		   
		   if(id != null && id.length() > 0) {
			   newfileEntry = new ZipEntry("forms/" + id + ".form");
			   
			   zos.putNextEntry(newfileEntry);
			   structuredArtifactDefinitionManager.packageFormForExport(id, zos);
			   zos.closeEntry();
		   }
	   }
	   
	   //	put the forms into the zip
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
	   newfileEntry = new ZipEntry("guidance/guidanceList");
	   zos.putNextEntry(newfileEntry);
	   getGuidanceManager().packageGuidanceForExport(exportGuidanceIds, zos);
	   zos.closeEntry();
	   
	   
	   exportForms.clear();
	   exportFiles.clear();
	   exportGuidanceIds.clear();
   }
   
   private String exportPathFromResource(ContentResource resource, String id)
   {
	   String fileName = resource.getProperties().getProperty(
               resource.getProperties().getNamePropDisplayName());
	   return resource.getContentType() +"/" + id + "/" + 
	   fileName.substring(fileName.lastIndexOf('\\')+1);
   }
   
   private Element wizardToXML(Wizard wiz)
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

	    //	put the wizard evaluators into the xml
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
		
		
		//	put the evaluation, review, reflection
	    rootNode.addContent(putWorkflowObjectToXml(wiz));

	    
	    //	add the wizard guidance to the list
	    exportGuidanceIds.add(wiz.getGuidanceId().getValue());
	    
	    attrNode = new Element("guidance");
	    attrNode.addContent(new CDATA(wiz.getGuidanceId().getValue()));
	    rootNode.addContent(attrNode);
	    
	    
	    //	put the categories/pages into the xml
	    rootNode.addContent(putCategoryToXml(wiz.getRootCategory()));
	    
	    
	    //	put the styles into the xml
	    attrNode = new Element("styles");
	    for(Iterator i = wiz.getWizardStyleItems().iterator(); i.hasNext(); ) {
	        WizardStyleItem style = (WizardStyleItem)i.next();
	        String 			resId = style.getBaseReference().getBase().getId();
	        
		    Element 		styleNode = new Element("style");
		    styleNode.addContent(new CDATA(resId));
		    attrNode.addContent(styleNode);
		    
    		String nodeId = getContentHosting().getUuid(resId);
    		String id = getContentHosting().resolveUuid(nodeId);
		    ContentResource resource = null;
   			try {
   				resource = getContentHosting().getResource(id);
   			} catch(PermissionException pe) {
		        logger.warn("Failed loading content: no permission to view file", pe);
   			} catch(TypeException pe) {
		        logger.warn("Wrong type", pe);
		    } catch(IdUnusedException pe) {
		        logger.warn("UnusedId: ", pe);
		    }
		    
		    exportFiles.put(nodeId, resource);
	    }
	    rootNode.addContent(attrNode);
	    
	    return rootNode;
   }
   
   
   private Element putCategoryToXml(WizardCategory cat)
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
		  pagesNode.addContent(putPageSequenceToXml(pageSequence));
	   }
	   categoryNode.addContent(pagesNode);
	   
	   Element childCategoriesNode = new Element("childCategories");
	   for(Iterator i = cat.getChildCategories().iterator(); i.hasNext(); ) {
		   WizardCategory childCat = (WizardCategory)i.next();
		   childCategoriesNode.addContent(putCategoryToXml(childCat));
	   }
	   categoryNode.addContent(childCategoriesNode);
	   
	   return categoryNode;
   }
   
   private Element putPageSequenceToXml(WizardPageSequence pageSequence)
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
	   
	   pageSequenceNode.addContent(putPageDefinitionToXml(pageSequence.getWizardPageDefinition()));
	   
	   return pageSequenceNode;
   }
   
   private Element putPageDefinitionToXml(WizardPageDefinition pageDef)
   {
	   Element pageDefNode = new Element("pageDef");
	   
	   if(pageDef == null)
		   return pageDefNode;

	   pageDefNode.addContent(putWorkflowObjectToXml(pageDef));
	   
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
	   
	   exportGuidanceIds.add(pageDef.getGuidance().getId().getValue());
	    
	   attrNode = new Element("guidance");
	   attrNode.addContent(new CDATA(pageDef.getGuidance().getId().getValue()));
	   pageDefNode.addContent(attrNode);
	   

	   Element		evaluatorsNode = new Element("evaluators");
	   Collection	evaluators = getWizardPageDefEvaluators(pageDef.getId(), false);
	   for(Iterator i = evaluators.iterator(); i.hasNext(); ) {
		   Id id = (Id)i.next();
		   attrNode = new Element("evaluator");
		   attrNode.addContent(new CDATA(id.getValue()));
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
   
   
   protected Collection getWizardEvaluators(Id wizardPageDefId, boolean useAgentId) {
	      Collection evaluators = new HashSet();
	      Collection viewerAuthzs = authorizationFacade.getAuthorizations(null,
	    		  WizardFunctionConstants.EVALUATE_WIZARD, wizardPageDefId);
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
   private Element putWorkflowObjectToXml(ObjectWithWorkflow objWorkflow)
   {
	   Element workflowObjNode = new Element("workflow");

	   Element attrNode = new Element("evaluationDevice");
	   attrNode.addContent(new CDATA(objWorkflow.getEvaluationDevice().getValue()));
	   workflowObjNode.addContent(attrNode);

	   exportForms.put(objWorkflow.getEvaluationDevice().getValue(), new Integer(0));
	   
	   attrNode = new Element("evaluationDeviceType");
	   attrNode.addContent(new CDATA(objWorkflow.getEvaluationDeviceType()));
	   workflowObjNode.addContent(attrNode);
	   
	   attrNode = new Element("reflectionDevice");
	   attrNode.addContent(new CDATA(objWorkflow.getReflectionDevice().getValue()));
	   workflowObjNode.addContent(attrNode);

	   exportForms.put(objWorkflow.getReflectionDevice().getValue(), new Integer(0));
	   
	   attrNode = new Element("reflectionDeviceType");
	   attrNode.addContent(new CDATA(objWorkflow.getReflectionDeviceType()));
	   workflowObjNode.addContent(attrNode);
	   
	   attrNode = new Element("reviewDevice");
	   attrNode.addContent(new CDATA(objWorkflow.getReviewDevice().getValue()));
	   workflowObjNode.addContent(attrNode);
	   
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
}

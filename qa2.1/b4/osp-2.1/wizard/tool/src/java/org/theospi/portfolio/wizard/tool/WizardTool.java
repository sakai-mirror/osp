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
package org.theospi.portfolio.wizard.tool;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.api.kernel.component.cover.ComponentManager;
import org.sakaiproject.api.kernel.session.ToolSession;
import org.sakaiproject.api.kernel.session.cover.SessionManager;
import org.sakaiproject.api.kernel.tool.Placement;
import org.sakaiproject.api.kernel.tool.Tool;
import org.sakaiproject.api.kernel.tool.cover.ToolManager;
import org.sakaiproject.metaobj.shared.mgt.IdManager;
import org.sakaiproject.metaobj.shared.model.Id;
import org.sakaiproject.metaobj.shared.model.StructuredArtifactDefinitionBean;
import org.sakaiproject.metaobj.worksite.mgt.WorksiteManager;
import org.sakaiproject.service.framework.portal.cover.PortalService;
import org.sakaiproject.service.legacy.content.ContentHostingService;
import org.sakaiproject.service.legacy.content.ContentResource;
import org.sakaiproject.service.legacy.authzGroup.Member;
import org.sakaiproject.service.legacy.entity.Reference;
import org.sakaiproject.service.legacy.filepicker.FilePickerHelper;
import org.sakaiproject.service.legacy.site.Group;
import org.sakaiproject.service.legacy.site.Site;
import org.sakaiproject.service.legacy.site.cover.SiteService;
import org.sakaiproject.service.legacy.user.User;
import org.sakaiproject.service.legacy.user.cover.UserDirectoryService;
import org.sakaiproject.exception.IdUnusedException;
import org.sakaiproject.exception.PermissionException;
import org.sakaiproject.exception.TypeException;
import org.theospi.portfolio.guidance.mgt.GuidanceManager;
import org.theospi.portfolio.guidance.model.Guidance;
import org.theospi.portfolio.review.ReviewHelper;
import org.theospi.portfolio.review.mgt.ReviewManager;
import org.theospi.portfolio.review.model.Review;
import org.theospi.portfolio.security.AudienceSelectionHelper;
import org.theospi.portfolio.security.AuthorizationFacade;
import org.theospi.portfolio.wizard.WizardFunctionConstants;
import org.theospi.portfolio.wizard.mgt.WizardManager;
import org.theospi.portfolio.wizard.model.CompletedWizard;
import org.theospi.portfolio.wizard.model.Wizard;
import org.theospi.portfolio.workflow.mgt.WorkflowManager;
import org.theospi.portfolio.shared.tool.BuilderTool;
import org.theospi.portfolio.shared.tool.BuilderScreen;
import org.theospi.portfolio.shared.model.OspException;
import org.theospi.portfolio.style.StyleHelper;
import org.theospi.portfolio.matrix.MatrixManager;

public class WizardTool extends BuilderTool {

   protected final Log logger = LogFactory.getLog(getClass());
	
   private WizardManager wizardManager;
   private GuidanceManager guidanceManager;
   private AuthorizationFacade authzManager;
   private MatrixManager matrixManager;
   private WorkflowManager workflowManager;
   private ContentHostingService contentHosting;
   private ReviewManager reviewManager;

   private IdManager idManager;
   private DecoratedWizard current = null;

   private String expandedGuidanceSection = "false";
   private List wizardTypes = null;
   private DecoratedCategory currentCategory;
   private DecoratedCategoryChild moveCategoryChild;
   private List deletedItems = new ArrayList();
   private int nextWizard = 0;
   private String currentUserId;
   
   //	import variables
   private String importFilesString = "";
   private List importFiles = new ArrayList();

   
   //	Constants
   
   public final static String LIST_PAGE = "listWizards";
   public final static String EDIT_PAGE = "editWizard";
   public final static String EDIT_PAGES_PAGE = "editWizardPages";
   public final static String EDIT_SUPPORT_PAGE = "editWizardSupport";
   public final static String EDIT_DESIGN_PAGE = "editWizardDesign";
   public final static String EDIT_PROPERTIES_PAGE = "editWizardProperties";
   public final static String IMPORT_PAGE = "importWizard";

   private BuilderScreen[] screens = {
      new BuilderScreen(EDIT_PAGE),
      new BuilderScreen(EDIT_PAGES_PAGE),
      new BuilderScreen(EDIT_SUPPORT_PAGE),
      new BuilderScreen(EDIT_DESIGN_PAGE),
      new BuilderScreen(EDIT_PROPERTIES_PAGE)
      };

   public WizardTool() {
      setScreens(screens);
   }

   protected void saveScreen(BuilderScreen screen) {
      processActionSave(screen.getNavigationKey());
   }

   public WizardManager getWizardManager() {
      return wizardManager;
   }

   public void setWizardManager(WizardManager wizardManager) {
      this.wizardManager = wizardManager;
   }
   
   
   
   /**
    * @return Returns the currentUserId.
    */
   public String getCurrentUserId() {
      return currentUserId;
   }

   /**
    * @param currentUserId The currentUserId to set.
    */
   public void setCurrentUserId(String currentUserId) {
      this.currentUserId = currentUserId;
   }

   public String getOwnerCheckMessage() {
      String message = "";
      String readOnly = "";
      try {
         if (!currentUserId.equalsIgnoreCase(SessionManager.getCurrentSessionUserId())) {
            readOnly = getMessageFromBundle("read_only");
         }
         User user = UserDirectoryService.getUser(currentUserId);
         message = getMessageFromBundle("wizard_owner_message", new Object[]{
               readOnly, user.getDisplayName()});
      } catch (IdUnusedException e) {
         throw new OspException(e);
      }

      return message;
   }

   public DecoratedWizard getCurrent() {
      ToolSession session = SessionManager.getCurrentToolSession();

      if (current == null)
      {
         //This should have come from the eval tool...
         String id = (String)session.getAttribute("CURRENT_WIZARD_ID");
         String userId = "";
         if (id != null) {
            userId = (String)session.getAttribute("WIZARD_USER_ID");   
            session.removeAttribute("WIZARD_USER_ID");
            session.removeAttribute("CURRENT_WIZARD_ID");
         }
         else {
            Placement placement = ToolManager.getCurrentPlacement();

            id = placement.getPlacementConfig().getProperty(
                  WizardManager.EXPOSED_WIZARD_KEY);
            userId = SessionManager.getCurrentSessionUserId();
            this.setCurrentUserId(userId);
         }
         Wizard wizard = getWizardManager().getWizard(id);
         setCurrent(new DecoratedWizard(this, wizard));
         current.setRunningWizard(new DecoratedCompletedWizard(this, current,
               getWizardManager().getCompletedWizard(wizard, userId)));

      }
      Wizard wizard = current.getBase();

      if (session.getAttribute(GuidanceManager.CURRENT_GUIDANCE) != null) {
         Guidance guidance = (Guidance)session.getAttribute(GuidanceManager.CURRENT_GUIDANCE);
         wizard.setGuidanceId(guidance.getId());

         session.removeAttribute(GuidanceManager.CURRENT_GUIDANCE);
         setExpandedGuidanceSection("true");
      }
      if (wizard.getGuidanceId() != null && wizard.getGuidance() == null) {
         wizard.setGuidance(getGuidanceManager().getGuidance(wizard.getGuidanceId()));
      }

      if (wizard.getExposedPageId() != null && !wizard.getExposedPageId().equals("") &&
            (wizard.getExposeAsTool() == null || wizard.getExposeAsTool().booleanValue())) {
         wizard.setExposeAsTool(new Boolean(true));
      }

      return current;
   }

   public void setCurrent(DecoratedWizard current) {
      this.current = current;
   }

   public Reference decorateReference(String reference) {
      return getWizardManager().decorateReference(getCurrent().getBase(), reference);
   }

   public List getWizards() {
      Placement placement = ToolManager.getCurrentPlacement();
      String currentSiteId = placement.getContext();
      List returned = new ArrayList();

      String user = currentUserId!=null ?
            currentUserId : SessionManager.getCurrentSessionUserId();
      setCurrentUserId(user);

      List wizards = getWizardManager().listAllWizards(user, currentSiteId);

      DecoratedWizard lastWizard = null;

      for (Iterator i=wizards.iterator();i.hasNext();) {
         Wizard wizard = (Wizard)i.next();
         DecoratedWizard current = new DecoratedWizard(this, wizard);
         returned.add(current);
         if (lastWizard != null) {
            lastWizard.setNext(current);
            current.setPrev(lastWizard);
         }
         lastWizard = current;
      }

      if (lastWizard != null) {
         setNextWizard(lastWizard.getBase().getSequence() + 1);
      }

      return returned;
   }

   public String processActionPublish(Wizard wizard) {
      getWizardManager().publishWizard(wizard);
      current = null;
      return LIST_PAGE;
   }

   public String processActionEdit(Wizard wizard) {
      wizard = getWizardManager().getWizard(wizard.getId());
      setCurrent(new DecoratedWizard(this, wizard));
      return startBuilder();
   }

   public String processActionDelete(Wizard wizard) {
      getWizardManager().deleteWizard(wizard);
      current = null;
      return LIST_PAGE;
   }

   public String processActionCancel() {
      setCurrent(null);
      return LIST_PAGE;
   }

   public String processActionChangeUser() {


      return LIST_PAGE;
   }

   protected Id cleanBlankId(String id) {
      if (id.equals("")) return null;
      return getIdManager().getId(id);
   }

   public String processActionSaveFinished() {
      processActionSave(getCurrentScreen().getNavigationKey());
      return LIST_PAGE;
   }

   protected void processActionSave(String currentView) {
      if (currentView.equals(EDIT_PAGE) && getCurrent().getBase().getType().equals(Wizard.WIZARD_TYPE_SEQUENTIAL)) {
         boolean foundOne = false;
         List pageList = getCurrent().getRootCategory().getBase().getChildPages();
         List decoratedPageList = getCurrent().getRootCategory().getCategoryPageList();
         for (Iterator i=decoratedPageList.iterator();i.hasNext();) {
            DecoratedWizardPage page = (DecoratedWizardPage) i.next();
            if (!pageList.contains(page.getBase())) {
               pageList.add(page.getBase());
               page.getBase().setCategory(getCurrent().getRootCategory().getBase());
               foundOne = true;
            }
         }
         if (foundOne) {
            getCurrent().getRootCategory().resequencePages();
         }
      }
      getWizardManager().deleteObjects(deletedItems);
      deletedItems.clear();
      Wizard wizard = getCurrent().getBase();
      wizard.setEvalWorkflows(getWorkflowManager().createEvalWorkflows(wizard));

      getWizardManager().saveWizard(wizard);
   }

   public String processActionNew() {
      Wizard newWizard = getWizardManager().createNew();

      newWizard.setSequence(getNextWizard());

      setCurrent(new DecoratedWizard(this, newWizard));

      return startBuilder();
   }

   public String processActionRemoveGuidance() {
      //Placement placement = ToolManager.getCurrentPlacement();
      //String currentSite = placement.getContext();
      Wizard wizard = getCurrent().getBase();
      getGuidanceManager().deleteGuidance(wizard.getGuidance());
      wizard.setGuidance(null);
      //session.setAttribute(WizardManager.CURRENT_WIZARD, getCurrent().getBase());

      return getCurrentScreen().getNavigationKey();
   }

   public void processActionGuidanceHelper() {
      showGuidance("tool");
   }

   public void processActionViewGuidance() {
      showGuidance("view");
   }

   protected void showGuidance(String view) {
      ExternalContext context = FacesContext.getCurrentInstance().getExternalContext();
      //Tool tool = ToolManager.getCurrentTool();
      ToolSession session = SessionManager.getCurrentToolSession();

      Placement placement = ToolManager.getCurrentPlacement();
      String currentSite = placement.getContext();
      //session.setAttribute(tool.getId() + Tool.HELPER_DONE_URL, "");
      //session.setAttribute(WizardManager.CURRENT_WIZARD_ID, getCurrent().getBase().getId());
      Wizard wizard = getCurrent().getBase();

      Guidance guidance = wizard.getGuidance();
      if (guidance == null) {
         guidance = getGuidanceManager().createNew(wizard.getName() + " Guidance", 
               currentSite, wizard.getId(), WizardFunctionConstants.VIEW_WIZARD, 
               WizardFunctionConstants.EDIT_WIZARD);
      }

      session.setAttribute(GuidanceManager.CURRENT_GUIDANCE, guidance);

      try {
         context.redirect("osp.guidance.helper/" + view);
      }
      catch (IOException e) {
         throw new RuntimeException("Failed to redirect to helper", e);
      }
   }

   public void processActionEvaluate() {
      processActionReviewHelper(Review.EVALUATION_TYPE);
   }

   public void processActionReview() {
      processActionReviewHelper(Review.REVIEW_TYPE);
   }

   protected void processActionReviewHelper(int type) {
      ExternalContext context = FacesContext.getCurrentInstance().getExternalContext();
      ToolSession session = SessionManager.getCurrentToolSession();

      //CWM use a constant for the below values
      session.setAttribute("process_type_key", CompletedWizard.PROCESS_TYPE_KEY);
      session.setAttribute(CompletedWizard.PROCESS_TYPE_KEY,
            current.getRunningWizard().getBase().getId().getValue());
      session.setAttribute(ReviewHelper.REVIEW_TYPE_KEY,
            Integer.toString(type));

      try {
         context.redirect("osp.review.processor.helper/reviewHelper.osp");
      }
      catch (IOException e) {
         throw new RuntimeException("Failed to redirect to helper", e);
      }

   }

   public void processActionAudienceHelper() {
      ExternalContext context = FacesContext.getCurrentInstance().getExternalContext();
      //Tool tool = ToolManager.getCurrentTool();
      ToolSession session = SessionManager.getCurrentToolSession();

      //Placement placement = ToolManager.getCurrentPlacement();
      //String currentSite = placement.getContext();
      Wizard wizard = getCurrent().getBase();

      session.setAttribute(AudienceSelectionHelper.AUDIENCE_FUNCTION,
            WizardFunctionConstants.EVALUATE_WIZARD);
      session.setAttribute(AudienceSelectionHelper.AUDIENCE_QUALIFIER,
            wizard.getId().getValue());
      session.setAttribute(AudienceSelectionHelper.AUDIENCE_INSTRUCTIONS,
            getMessageFromBundle("audience_instructions"));
      session.setAttribute(AudienceSelectionHelper.AUDIENCE_GLOBAL_TITLE,
            getMessageFromBundle("audience_global_title"));
      session.setAttribute(AudienceSelectionHelper.AUDIENCE_INDIVIDUAL_TITLE,
            getMessageFromBundle("audience_individual_title"));
      session.setAttribute(AudienceSelectionHelper.AUDIENCE_GROUP_TITLE,
            getMessageFromBundle("audience_group_title"));
      session.setAttribute(AudienceSelectionHelper.AUDIENCE_PUBLIC_FLAG, "false");
      session.setAttribute(AudienceSelectionHelper.AUDIENCE_PUBLIC_TITLE,
            null);
      session.setAttribute(AudienceSelectionHelper.AUDIENCE_SELECTED_TITLE,
            getMessageFromBundle("audience_selected_title"));
      session.setAttribute(AudienceSelectionHelper.AUDIENCE_FILTER_INSTRUCTIONS,
            getMessageFromBundle("audience_filter_instructions"));
      session.setAttribute(AudienceSelectionHelper.AUDIENCE_GUEST_EMAIL, null);
      session.setAttribute(AudienceSelectionHelper.AUDIENCE_WORKSITE_LIMITED, "true");

      //Guidance guidance = wizard.getGuidance();
      //if (guidance == null) {
      //   guidance = getGuidanceManager().createNew(wizard.getName() + " Guidance", currentSite, null, "", "");
      //}

      //session.setAttribute(GuidanceManager.CURRENT_GUIDANCE, guidance);

      try {
         context.redirect("osp.audience.helper/tool");
      }
      catch (IOException e) {
         throw new RuntimeException("Failed to redirect to helper", e);
      }
   }

   public boolean isMaintainer() {
      return new Boolean(getAuthzManager().isAuthorized(WorksiteManager.WORKSITE_MAINTAIN,
         getIdManager().getId(PortalService.getCurrentSiteId()))).booleanValue();
   }

   public String processPermissions()
   {
      ExternalContext context = FacesContext.getCurrentInstance().getExternalContext();

       //todo userCan = null;

      try {
           context.redirect("osp.permissions.helper/editPermissions?" +
                 "message=" + getPermissionsMessage() +
                 "&name=wizard" +
                 "&qualifier=" + ToolManager.getCurrentPlacement().getId() +
                 "&returnView=matrixRedirect");
       }
       catch (IOException e) {
           throw new RuntimeException("Failed to redirect to helper", e);
       }
       return null;
   }

   public String getPermissionsMessage() {
      return getMessageFromBundle("perm_description", new Object[]{
         getTool().getTitle(), getWorksite().getTitle()});
   }

   public Tool getTool() {
      return ToolManager.getCurrentTool();
   }

   public Site getWorksite() {
      try {
         return SiteService.getSite(PortalService.getCurrentSiteId());
      }
      catch (IdUnusedException e) {
         throw new OspException(e);
      }
   }

   public String importWizard()
   {
	   return IMPORT_PAGE;
   }

   public String processPickImportFiles()
   {
	      ExternalContext context = FacesContext.getCurrentInstance().getExternalContext();
	      ToolSession session = SessionManager.getCurrentToolSession();
	      session.setAttribute(FilePickerHelper.FILE_PICKER_ATTACH_LINKS, new Boolean(true).toString());
	      /*
	      List wsItemRefs = EntityManager.newReferenceList();

	      for (Iterator i=importFiles.iterator();i.hasNext();) {
	         WizardStyleItem wsItem = (WizardStyleItem)i.next();
	         wsItemRefs.add(wsItem.getBaseReference().getBase());
	      }*/

	      session.setAttribute(FilePickerHelper.FILE_PICKER_ATTACHMENTS, importFiles);
	      session.setAttribute(FilePickerHelper.FILE_PICKER_RESOURCE_FILTER,
	            ComponentManager.get("org.sakaiproject.service.legacy.content.ContentResourceFilter.wizardImportFile"));
         
	      try {
	         context.redirect("sakai.filepicker.helper/tool");
	      }
	      catch (IOException e) {
	         throw new RuntimeException("Failed to redirect to helper", e);
	      }
	      return null;
   }
   
   /**
    * This is called to put the file names into the text box.
    * It updates the list of files if the user is returning from the file picker
    * @return String the names of the files being imported
    */
   public String getImportFilesString()
   {
		ToolSession session = SessionManager.getCurrentToolSession();
		if (session.getAttribute(FilePickerHelper.FILE_PICKER_CANCEL) == null
				&& session.getAttribute(FilePickerHelper.FILE_PICKER_ATTACHMENTS) != null) {

			List refs = (List) session.getAttribute(FilePickerHelper.FILE_PICKER_ATTACHMENTS);
			importFiles.clear();
			importFilesString = "";
			for (int i = 0; i < refs.size(); i++) {
				Reference ref = (Reference) refs.get(i);
	    		String nodeId = getContentHosting().getUuid(ref.getId());
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
	    		
			    importFilesString += resource.getProperties().getProperty(
	                        resource.getProperties().getNamePropDisplayName()) + " ";
			}
			importFiles = refs;
			session.removeAttribute(FilePickerHelper.FILE_PICKER_ATTACHMENTS);
		}
      else if (session.getAttribute(FilePickerHelper.FILE_PICKER_CANCEL) == null
            && session.getAttribute(FilePickerHelper.FILE_PICKER_ATTACHMENTS) == null) {
         importFiles.clear();
         importFilesString = "";
      }

		return importFilesString;
   }
   public void setImportFilesString(String importFilesString)
   {
	   this.importFilesString = importFilesString;
   }
   
   
   /**
    * Called when the user clicks the Import Button
    * @return String next view
    */
   public String processImportWizards()
   {
	   if(importFiles.size() == 0) {
		   return IMPORT_PAGE;
	   }
	   
	   for(Iterator i = importFiles.iterator(); i.hasNext(); ) {
		   Reference ref = (Reference)i.next();
		   
		   wizardManager.importResource(
				   getIdManager().getId(getWorksite().getId()),
				   getContentHosting().getUuid(ref.getId()));
	   }
	   
	   return LIST_PAGE;
   }
   
   
   public String processActionSelectStyle() {      
      ExternalContext context = FacesContext.getCurrentInstance().getExternalContext();
      ToolSession session = SessionManager.getCurrentToolSession();
      session.removeAttribute(StyleHelper.CURRENT_STYLE);
      session.removeAttribute(StyleHelper.CURRENT_STYLE_ID);
      
      session.setAttribute(StyleHelper.STYLE_SELECTABLE, "true");
      
      Wizard wizard = getCurrent().getBase();
      
      if (wizard.getStyle() != null)
         session.setAttribute(StyleHelper.CURRENT_STYLE_ID, wizard.getStyle().getId().getValue());
      
      try {
         context.redirect("osp.style.helper/listStyle");
      }
      catch (IOException e) {
         throw new RuntimeException("Failed to redirect to helper", e);
      }
      return null;
   }
   
   public List getUserListForSelect() {
      Placement placement = ToolManager.getCurrentPlacement();
      String currentSiteId = placement.getContext();
      List theList = new ArrayList(getUserList(currentSiteId));
      
      String user = currentUserId!=null ? 
            currentUserId : SessionManager.getCurrentSessionUserId();
      setCurrentUserId(user);
      
      return theList;
   }
   
   private Set getUserList(String worksiteId) {
      Set members = new HashSet();
      Set users = new HashSet();
      
      try {
         Site site = SiteService.getSite(worksiteId);
         if (site.hasGroups()) {
            String currentUser = SessionManager.getCurrentSessionUserId();
            Collection groups = site.getGroupsWithMember(currentUser);
            for (Iterator iter = groups.iterator(); iter.hasNext();) {
               Group group = (Group) iter.next();
               members.addAll(group.getMembers());
            }
         }
         else {
            members.addAll(site.getMembers());
         }
         
         Collections.sort(new ArrayList(members));
         
         for (Iterator memb = members.iterator(); memb.hasNext();) {
            Member member = (Member) memb.next();
            User user = UserDirectoryService.getUser(member.getUserId());
            users.add(createSelect(user.getId(), user.getSortName()));
         }
      } catch (IdUnusedException e) {
         throw new OspException(e);
      }
      return users;
   }
   
   public boolean getCanCreate() {
      return getAuthzManager().isAuthorized(WizardFunctionConstants.CREATE_WIZARD, 
            getIdManager().getId(ToolManager.getCurrentPlacement().getId()));
   }
   
   public boolean getCanView() {
      return getAuthzManager().isAuthorized(WizardFunctionConstants.VIEW_WIZARD, 
            getIdManager().getId(ToolManager.getCurrentPlacement().getId()));
   }
   
   public boolean getCanReview() {
      return getAuthzManager().isAuthorized(WizardFunctionConstants.REVIEW_WIZARD, 
            current.getBase().getId());
   }
   
   public boolean getCanEvaluate() {
      return getAuthzManager().isAuthorized(WizardFunctionConstants.EVALUATE_WIZARD, 
            current.getBase().getId());
   }
   
   public boolean getCanPublish(Wizard wizard) {
      return getAuthzManager().isAuthorized(WizardFunctionConstants.PUBLISH_WIZARD, 
            wizard.getId()) && !wizard.isPublished();
   }
   
   public boolean getCanDelete(Wizard wizard) {
      return getAuthzManager().isAuthorized(WizardFunctionConstants.DELETE_WIZARD, 
            wizard.getId()) && wizard.getOwner().getId().getValue().equalsIgnoreCase(
                  SessionManager.getCurrentSessionUserId());
   }
   
   public boolean getCanEdit(Wizard wizard) {
      return getAuthzManager().isAuthorized(WizardFunctionConstants.EDIT_WIZARD, 
            wizard.getId()) && wizard.getOwner().getId().getValue().equalsIgnoreCase(
                  SessionManager.getCurrentSessionUserId());
   }
   
   public boolean getCanExport(Wizard wizard) {
      return getAuthzManager().isAuthorized(WizardFunctionConstants.EXPORT_WIZARD, 
            wizard.getId()) && wizard.getOwner().getId().getValue().equalsIgnoreCase(
                  SessionManager.getCurrentSessionUserId());
   }
   
   protected Collection getFormsForSelect(String type) {
      Placement placement = ToolManager.getCurrentPlacement();
      String currentSiteId = placement.getContext();
      Collection forms = 
               getWizardManager().getAvailableForms(currentSiteId, type);
      
      List retForms = new ArrayList();
      for(Iterator iter = forms.iterator(); iter.hasNext();) {
         StructuredArtifactDefinitionBean sad = (StructuredArtifactDefinitionBean) iter.next(); 
         retForms.add(createSelect(sad.getId().getValue(), sad.getDescription()));
      }
      
      return retForms;
   }
   
   public Collection getCommentFormsForSelect() {
      return getFormsForSelect(WizardFunctionConstants.COMMENT_TYPE);      
   }
   
   public Collection getReflectionFormsForSelect() {
      return getFormsForSelect(WizardFunctionConstants.REFLECTION_TYPE);
   }
   
   public Collection getEvaluationFormsForSelect() {
      return getFormsForSelect(WizardFunctionConstants.EVALUATION_TYPE);
   }
   
   protected Collection getWizardsForSelect(String type) {
      //TODO is only here just in case we decide to give wizards types
      // The type isn't being used yet
      Placement placement = ToolManager.getCurrentPlacement();
      String currentSiteId = placement.getContext();
      List wizards = getWizardManager().listWizardsByType(
            SessionManager.getCurrentSessionUserId(), currentSiteId, type);
      
      List retWizards = new ArrayList();
      for(Iterator iter = wizards.iterator(); iter.hasNext();) {
         Wizard wizard = (Wizard)iter.next();
         retWizards.add(createSelect(wizard.getId().getValue(), wizard.getName()));
      }
      
      return retWizards;
   }
   
   public Collection getCommentWizardsForSelect() {
      return getWizardsForSelect(WizardFunctionConstants.COMMENT_TYPE);
   }
   
   public Collection getReflectionWizardsForSelect() {
      return getWizardsForSelect(WizardFunctionConstants.REFLECTION_TYPE);
   }
   
   public Collection getEvaluationWizardsForSelect() {
      return getWizardsForSelect(WizardFunctionConstants.EVALUATION_TYPE);
   }
   
   private String safeGetValue(Id id) {
      if (id == null)
         return "";
      else
         return id.getValue();
   }

   public GuidanceManager getGuidanceManager() {
      return guidanceManager;
   }

   public void setGuidanceManager(GuidanceManager guidanceManager) {
      this.guidanceManager = guidanceManager;
   }

   public String getCommentItem() {
      return safeGetValue(current.getBase().getReviewDevice());
   }

   public void setCommentItem(String commentItem) {
      current.getBase().setReviewDevice(getIdManager().getId(commentItem));
   }

   public String getEvaluationItem() {
      return safeGetValue(current.getBase().getEvaluationDevice());
   }

   public void setEvaluationItem(String evaluationItem) {
      current.getBase().setEvaluationDevice(getIdManager().getId(evaluationItem));
   }

   public String getReflectionItem() {
      return safeGetValue(current.getBase().getReflectionDevice());
   }

   public void setReflectionItem(String reflectionItem) {
      current.getBase().setReflectionDevice(getIdManager().getId(reflectionItem));
   }

   public String getExpandedGuidanceSection() {
      return expandedGuidanceSection;
   }

   public void setExpandedGuidanceSection(String expandedGuidanceSection) {
      this.expandedGuidanceSection = expandedGuidanceSection;
   }

   public IdManager getIdManager() {
      return idManager;
   }

   public void setIdManager(IdManager idManager) {
      this.idManager = idManager;
   }

   public List getWizardTypes() {
      if (wizardTypes == null) {
         wizardTypes = new ArrayList();
         wizardTypes.add(createSelect(Wizard.WIZARD_TYPE_SEQUENTIAL,
               getMessageFromBundle(Wizard.WIZARD_TYPE_SEQUENTIAL)));
         wizardTypes.add(createSelect(Wizard.WIZARD_TYPE_HIERARCHICAL,
               getMessageFromBundle(Wizard.WIZARD_TYPE_HIERARCHICAL)));
      }
      return wizardTypes;
   }

   public void setWizardTypes(List wizardTypes) {
      this.wizardTypes = wizardTypes;
   }

   public DecoratedCategory getCurrentCategory() {
      return currentCategory;
   }

   public void setCurrentCategory(DecoratedCategory currentCategory) {
      this.currentCategory = currentCategory;
   }

   public DecoratedCategoryChild getMoveCategoryChild() {
      return moveCategoryChild;
   }

   public void setMoveCategoryChild(DecoratedCategoryChild moveCategoryChild) {
      this.moveCategoryChild = moveCategoryChild;
   }

   public boolean isMoving() {
      return getMoveCategoryChild() != null;
   }

   public List getDeletedItems() {
      return deletedItems;
   }

   public void setDeletedItems(List deletedItems) {
      this.deletedItems = deletedItems;
   }

   public String getMovingInstructions() {
      String key = null;

      if (getMoveCategoryChild() == null) {
         return null;
      }

      if (getMoveCategoryChild().isCategory()) {
         key = "move_category_instructions";
      }
      else {
         key = "move_page_instructions";
      }

      return getMessageFromBundle(key, new Object[]{getMoveCategoryChild().getTitle()});
   }

   public int getNextWizard() {
      return nextWizard;
   }

   public void setNextWizard(int nextWizard) {
      this.nextWizard = nextWizard;
   }

   public AuthorizationFacade getAuthzManager() {
      return authzManager;
   }

   public void setAuthzManager(AuthorizationFacade authzManager) {
      this.authzManager = authzManager;
   }

   public MatrixManager getMatrixManager() {
      return matrixManager;
   }

   public void setMatrixManager(MatrixManager matrixManager) {
      this.matrixManager = matrixManager;
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

   /**
    * @return Returns the reviewManager.
    */
   public ReviewManager getReviewManager() {
      return reviewManager;
   }

   /**
    * @param reviewManager The reviewManager to set.
    */
   public void setReviewManager(ReviewManager reviewManager) {
      this.reviewManager = reviewManager;
   }

}

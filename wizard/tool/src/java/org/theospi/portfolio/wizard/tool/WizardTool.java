/**********************************************************************************
* $URL$
* $Id$
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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.authz.api.Member;
import org.sakaiproject.component.cover.ComponentManager;
import org.sakaiproject.content.api.ContentHostingService;
import org.sakaiproject.content.api.ContentResource;
import org.sakaiproject.content.api.FilePickerHelper;
import org.sakaiproject.entity.api.Reference;
import org.sakaiproject.exception.IdUnusedException;
import org.sakaiproject.exception.PermissionException;
import org.sakaiproject.exception.TypeException;
import org.sakaiproject.exception.ImportException;
import org.sakaiproject.exception.UnsupportedFileTypeException;
import org.sakaiproject.metaobj.security.AuthenticationManager;
import org.sakaiproject.metaobj.shared.mgt.IdManager;
import org.sakaiproject.metaobj.shared.model.Agent;
import org.sakaiproject.metaobj.shared.model.Id;
import org.sakaiproject.metaobj.shared.model.StructuredArtifactDefinitionBean;
import org.sakaiproject.metaobj.worksite.mgt.WorksiteManager;
import org.sakaiproject.site.api.Group;
import org.sakaiproject.site.api.Site;
import org.sakaiproject.site.cover.SiteService;
import org.sakaiproject.tool.api.*;
import org.sakaiproject.tool.cover.*;
import org.sakaiproject.tool.cover.SessionManager;
import org.sakaiproject.tool.cover.ToolManager;
import org.sakaiproject.user.api.User;
import org.sakaiproject.user.api.UserNotDefinedException;
import org.sakaiproject.user.cover.UserDirectoryService;
import org.theospi.portfolio.guidance.mgt.GuidanceHelper;
import org.theospi.portfolio.guidance.mgt.GuidanceManager;
import org.theospi.portfolio.guidance.model.Guidance;
import org.theospi.portfolio.matrix.MatrixFunctionConstants;
import org.theospi.portfolio.matrix.MatrixManager;
import org.theospi.portfolio.matrix.WizardPageHelper;
import org.theospi.portfolio.matrix.model.WizardPage;
import org.theospi.portfolio.review.ReviewHelper;
import org.theospi.portfolio.review.mgt.ReviewManager;
import org.theospi.portfolio.review.model.Review;
import org.theospi.portfolio.security.AudienceSelectionHelper;
import org.theospi.portfolio.security.Authorization;
import org.theospi.portfolio.security.AuthorizationFacade;
import org.theospi.portfolio.shared.model.OspException;
import org.theospi.portfolio.shared.tool.BuilderScreen;
import org.theospi.portfolio.shared.tool.BuilderTool;
import org.theospi.portfolio.style.StyleHelper;
import org.theospi.portfolio.wizard.WizardFunctionConstants;
import org.theospi.portfolio.wizard.mgt.WizardManager;
import org.theospi.portfolio.wizard.model.CompletedWizard;
import org.theospi.portfolio.wizard.model.CompletedWizardPage;
import org.theospi.portfolio.wizard.model.Wizard;
import org.theospi.portfolio.wizard.model.WizardPageSequence;
import org.theospi.portfolio.workflow.mgt.WorkflowManager;
import org.theospi.utils.mvc.impl.ToolFinishedView;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.*;

public class WizardTool extends BuilderTool {
   
   static final private String   BAD_FILE_TYPE_ID = "badFileType";
   static final private String   BAD_IMPORT_ID = "badImport";

   protected final Log logger = LogFactory.getLog(getClass());
	
   private WizardManager wizardManager;
   private GuidanceManager guidanceManager;
   private AuthenticationManager authManager;
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
   
   private String lastSaveWizard = "";
   private boolean pageSaved = false;
   private String lastSavePage = "";
   private String lastError = "";
   
   //	import variables
   private String importFilesString = "";
   private List importFiles = new ArrayList();

   
   //	Constants
   
   public final static String LIST_PAGE = "listWizards";
   public final static String EDIT_PAGE = "editWizard";
   public final static String EDIT_PAGE_TYPE = "editWizardType";
   public final static String EDIT_PAGES_PAGE = "editWizardPages";
   public final static String EDIT_SUPPORT_PAGE = "editWizardSupport";
   public final static String EDIT_DESIGN_PAGE = "editWizardDesign";
   public final static String EDIT_PROPERTIES_PAGE = "editWizardProperties";
   public final static String CONFIRM_SUBMIT_PAGE = "confirmSubmit";
   public final static String IMPORT_PAGE = "importWizard";
   public final static String CONFIRM_DELETE_PAGE = "confirmDeleteWizard";

   private BuilderScreen[] screens = {
     // new BuilderScreen(EDIT_PAGE_TYPE),
      new BuilderScreen(EDIT_PAGE),
      new BuilderScreen(EDIT_SUPPORT_PAGE),
      new BuilderScreen(EDIT_PAGES_PAGE),
     // new BuilderScreen(EDIT_DESIGN_PAGE),
     // new BuilderScreen(EDIT_PROPERTIES_PAGE)
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
         if (currentUserId == null) 
            setCurrentUserId(SessionManager.getCurrentSessionUserId());
            
         if (!currentUserId.equalsIgnoreCase(SessionManager.getCurrentSessionUserId())) {
            readOnly = getMessageFromBundle("read_only");
         }
         User user = UserDirectoryService.getUser(currentUserId);
         message = getMessageFromBundle("wizard_owner_message", new Object[]{
               readOnly, user.getDisplayName()});
      } catch (UserNotDefinedException e) {
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
            this.setCurrentUserId(userId);
         }
         else {
            Placement placement = ToolManager.getCurrentPlacement();

            id = placement.getPlacementConfig().getProperty(
                  WizardManager.EXPOSED_WIZARD_KEY);
            userId = SessionManager.getCurrentSessionUserId();
            this.setCurrentUserId(userId);
         }
         if(id == null)
            return null;
         Wizard wizard = getWizardManager().getWizard(id, WizardManager.WIZARD_VIEW_CHECK);
         if(wizard == null)
            return null;
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
         current.setTotalPages(getWizardManager().getTotalPageCount(wizard));
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
   
   public void clearInterface()
   {
      lastSaveWizard = "";
      pageSaved = false;
      lastSavePage = "";
      lastError = "";
   }

   public String processActionPublish(Wizard wizard) {
      clearInterface();
      getWizardManager().publishWizard(wizard);
      current = null;
      return LIST_PAGE;
   }

   public String processActionEdit(Wizard wizard) {
      clearInterface();
      wizard = getWizardManager().getWizard(wizard.getId(), WizardManager.WIZARD_EDIT_CHECK);
      setCurrent(new DecoratedWizard(this, wizard, false));
      return startBuilder();
   }

   public String processActionDelete(Wizard wizard) {
      clearInterface();
      getWizardManager().deleteWizard(wizard);
      current = null;
      return LIST_PAGE;
   }

   public String processActionConfirmDelete(Wizard wizard) {
      clearInterface();
      setCurrent(new DecoratedWizard(this, wizard, false));
      return CONFIRM_DELETE_PAGE;
   }
   
   public String processActionCancel() {
      clearInterface();
      setCurrent(null);
      cancelBoundValues();
      return LIST_PAGE;
   }

   public String processActionChangeUser() {
      clearInterface();
      return LIST_PAGE;
   }

   protected Id cleanBlankId(String id) {
      if (id.equals("")) return null;
      return getIdManager().getId(id);
   }

   public String processActionSaveFinished() {
      clearInterface();
      processActionSave(getCurrentScreen().getNavigationKey());
      return LIST_PAGE;
   }

   protected void processActionSave(String currentView) {
      clearInterface();
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
      clearInterface();
      Session session = SessionManager.getCurrentSession();
      Wizard newWizard = getWizardManager().createNew();
      newWizard.setSequence(getNextWizard());

      setCurrent(new DecoratedWizard(this, newWizard, true));
      session.setAttribute("newWizard", "true");

      return EDIT_PAGE_TYPE;
   }
   
   
   public void processSubmitWizard(CompletedWizard completedWizard)
   {
      clearInterface();
      completedWizard.setStatus(MatrixFunctionConstants.PENDING_STATUS);
      getWizardManager().saveWizard(completedWizard);
      lastSaveWizard = completedWizard.getWizard().getName();
   }

   public String processActionNewSteps() {
      return startBuilder();
   }

   public String processActionRemoveGuidance() {
      clearInterface();
      //Placement placement = ToolManager.getCurrentPlacement();
      //String currentSite = placement.getContext();
      Wizard wizard = getCurrent().getBase();
      getGuidanceManager().deleteGuidance(wizard.getGuidance());
      wizard.setGuidance(null);
      //session.setAttribute(WizardManager.CURRENT_WIZARD, getCurrent().getBase());

      return getCurrentScreen().getNavigationKey();
   }

   public void processActionGuidanceHelper(Wizard w, int types) {
      clearInterface();
      showGuidance(w, "tool", (types & 1) != 0, (types & 2) != 0, (types & 4) != 0);
   }
   public void processActionGuidanceHelper() {
      clearInterface();
      processActionGuidanceHelper(getCurrent().getBase(), 7);
   }

   public void processActionViewGuidance() {
      clearInterface();
      showGuidance(getCurrent().getBase(), "view", true, true, true);
   }

   protected void showGuidance(Wizard wizard, String view, boolean instructions, boolean rationale, boolean examples) {
      ExternalContext context = FacesContext.getCurrentInstance().getExternalContext();
      //Tool tool = ToolManager.getCurrentTool();
      ToolSession session = SessionManager.getCurrentToolSession();

      Placement placement = ToolManager.getCurrentPlacement();
      String currentSite = placement.getContext();
      //session.setAttribute(tool.getId() + Tool.HELPER_DONE_URL, "");
      //session.setAttribute(WizardManager.CURRENT_WIZARD_ID, getCurrent().getBase().getId());
      //Wizard wizard = getCurrent().getBase();

      Guidance guidance = wizard.getGuidance();
      if (guidance == null) {
         guidance = getGuidanceManager().createNew(wizard.getName() + " Guidance", 
               currentSite, wizard.getId(), WizardFunctionConstants.OPERATE_WIZARD, 
               WizardFunctionConstants.EDIT_WIZARD);
      }

      session.setAttribute(GuidanceHelper.SHOW_INSTRUCTION_FLAG, new Boolean(instructions));
      session.setAttribute(GuidanceHelper.SHOW_RATIONALE_FLAG, new Boolean(rationale));
      session.setAttribute(GuidanceHelper.SHOW_EXAMPLE_FLAG, new Boolean(examples));
      
      session.setAttribute(GuidanceManager.CURRENT_GUIDANCE, guidance);

      try {
         context.redirect("osp.guidance.helper/" + view);
      }
      catch (IOException e) {
         throw new RuntimeException("Failed to redirect to helper", e);
      }
   }

   public String processExecPage(WizardPageSequence pageSeq) {
      clearInterface();
      ExternalContext context = FacesContext.getCurrentInstance().getExternalContext();
      ToolSession session = SessionManager.getCurrentToolSession();
      
      WizardPage page = null;
      
      List cpages = getWizardManager().getCompletedWizardPagesByPageDef(pageSeq.getWizardPageDefinition().getId());
      String currentUser = getCurrentUserId();
      for(Iterator i = cpages.iterator(); i.hasNext();) {
         CompletedWizardPage wizpage = (CompletedWizardPage)i.next();
         
         WizardPage wpage = getMatrixManager().getWizardPage(wizpage.getWizardPage().getId());
         if(currentUser.equalsIgnoreCase(wpage.getOwner().getId().getValue())) {
            page = wpage;
            break;
         }
      }
      if(page == null)
         throw new NullPointerException("Failed to find the requested page");

      session.removeAttribute(WizardPageHelper.SEQUENTIAL_WIZARD_PAGES);
      session.removeAttribute(WizardPageHelper.SEQUENTIAL_WIZARD_CURRENT_STEP);
      
      session.setAttribute(WizardPageHelper.WIZARD_PAGE, page);
      String redirectAddress = "osp.wizard.page.helper/wizardPage.osp";
      
      if (!getCurrentUserId().equalsIgnoreCase(SessionManager.getCurrentSessionUserId()))
         session.setAttribute("readOnlyMatrix", "true");
      session.setAttribute(WizardPageHelper.WIZARD_OWNER, getCurrent().getRunningWizard().getBase().getOwner());

      if (Wizard.WIZARD_TYPE_SEQUENTIAL.equals(
            getCurrent().getBase().getType())) {
         redirectAddress = "osp.wizard.page.helper/sequentialWizardPage.osp";
      }

      try {
         context.redirect(redirectAddress);
      }
      catch (IOException e) {
         throw new RuntimeException("Failed to redirect to helper", e);
      }

      return null;
   }

   public String processExecPages() {
      clearInterface();
      ExternalContext context = FacesContext.getCurrentInstance().getExternalContext();
      ToolSession session = SessionManager.getCurrentToolSession();
      
      CompletedWizard cwiz = current.getRunningWizard().getBase();
      
      ArrayList pages = new ArrayList();
      
      for(Iterator i = cwiz.getRootCategory().getChildPages().iterator(); i.hasNext();) {
         CompletedWizardPage wizpage = (CompletedWizardPage)i.next();
         
         WizardPage page = getMatrixManager().getWizardPage(wizpage.getWizardPage().getId());
         pages.add(page);
      }
      
      session.setAttribute(WizardPageHelper.WIZARD_PAGE, pages);
      String redirectAddress = "osp.wizard.page.helper/wizardPage.osp";
      
      if (!getCurrentUserId().equalsIgnoreCase(SessionManager.getCurrentSessionUserId()))
         session.setAttribute("readOnlyMatrix", "true");
      session.setAttribute(WizardPageHelper.WIZARD_OWNER, getCurrent().getRunningWizard().getBase().getOwner());

      session.removeAttribute(WizardPageHelper.SEQUENTIAL_WIZARD_PAGES);
      session.removeAttribute(WizardPageHelper.SEQUENTIAL_WIZARD_CURRENT_STEP);
      
      Map map = (Map) session.getAttribute(ToolFinishedView.ALTERNATE_DONE_URL_MAP);

      if (map == null) {
         map = new HashMap();
      }

      if (Wizard.WIZARD_TYPE_SEQUENTIAL.equals(
            getCurrent().getBase().getType())) {
         session.setAttribute(WizardPageHelper.SEQUENTIAL_WIZARD_PAGES, pages);
         session.setAttribute(WizardPageHelper.SEQUENTIAL_WIZARD_CURRENT_STEP,
               new Integer(0));
         redirectAddress = "osp.wizard.page.helper/sequentialWizardPage.osp";
         map.put("finishSeqWizard", LIST_PAGE);
      }

      map.put("submitWizard", CONFIRM_SUBMIT_PAGE);
      map.put("submitWizardPage", LIST_PAGE);
      session.setAttribute(ToolFinishedView.ALTERNATE_DONE_URL_MAP, map);

      try {
         context.redirect(redirectAddress);
      }
      catch (IOException e) {
         throw new RuntimeException("Failed to redirect to helper", e);
      }

      return null;
   }

   public void processEditReflection() {
      ExternalContext context = FacesContext.getCurrentInstance().getExternalContext();
      ToolSession session = SessionManager.getCurrentToolSession();

      //CWM use a constant for the below values
      session.setAttribute("process_type_key", CompletedWizard.PROCESS_TYPE_KEY);
      session.setAttribute(CompletedWizard.PROCESS_TYPE_KEY,
            current.getRunningWizard().getBase().getId().getValue());
      session.setAttribute(ReviewHelper.REVIEW_TYPE_KEY,
            Integer.toString(Review.REFLECTION_TYPE));

      try {
         context.redirect("osp.review.processor.helper/reviewHelper.osp?current_review_id=" +
               ((Review)current.getRunningWizard().getReflections().get(0))
                                    .getReviewContentNode().getResource().getId());
      }
      catch (IOException e) {
         throw new RuntimeException("Failed to redirect to helper", e);
      }
   }

   /**
    * This is the action for redirecting the user to the "add reflection" form for 
    * to a completed wizard.
    */
   public void processActionReflection() {
         processActionReviewHelper(Review.REFLECTION_TYPE);
   }

   /**
    * This is the action for redirecting the user to the "add evaluation" form for 
    * to a completed wizard.
    */
   public void processActionEvaluate() {
      if(getCanEvaluate())
         processActionReviewHelper(Review.EVALUATION_TYPE);
   }


   /**
    * This is the action for redirecting the user to the "add feedback/review" form for 
    * to a completed wizard.
    */
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
      session.setAttribute(AudienceSelectionHelper.AUDIENCE_BROWSE_INDIVIDUAL,
            getMessageFromBundle("audience_browse_individual"));

      //Guidance guidance = wizard.getGuidance();
      //if (guidance == null) {
      //   guidance = getGuidanceManager().createNew(wizard.getName() + " Guidance", currentSite, null, "", "");
      //}

      //session.setAttribute(GuidanceManager.CURRENT_GUIDANCE, guidance);

      try {
         context.redirect("osp.audience.helper/tool.jsf?panel=Main");
      }
      catch (IOException e) {
         throw new RuntimeException("Failed to redirect to helper", e);
      }
   }

   public boolean isMaintainer() {
      return new Boolean(getAuthzManager().isAuthorized(WorksiteManager.WORKSITE_MAINTAIN,
         getIdManager().getId(ToolManager.getCurrentPlacement().getContext()))).booleanValue();
   }

   public String processPermissions()
   {
      clearInterface();
      ExternalContext context = FacesContext.getCurrentInstance().getExternalContext();

       //todo userCan = null;

      try {
           context.redirect("osp.permissions.helper/editPermissions?" +
                 "message=" + getPermissionsMessage() +
                 "&name=wizard" +
                 "&qualifier=" + ToolManager.getCurrentPlacement().getContext() +
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
         return SiteService.getSite(ToolManager.getCurrentPlacement().getContext());
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
      clearInterface();
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
	            ComponentManager.get("org.sakaiproject.content.api.ContentResourceFilter.wizardImportFile"));
         
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
			//importFiles.clear();
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
      clearInterface();
	   if(importFiles.size() == 0) {
		   return IMPORT_PAGE;
	   }
	   
	   for(Iterator i = importFiles.iterator(); i.hasNext(); ) {
		   Reference ref = (Reference)i.next();
		   
         try {
            wizardManager.importResource(
				   getIdManager().getId(getWorksite().getId()),
				   getContentHosting().getUuid(ref.getId()));
         } catch(ImportException ie) {
            lastError = BAD_IMPORT_ID;
         } catch(UnsupportedFileTypeException ufte) {
            lastError = BAD_FILE_TYPE_ID;
         }
	   }
	   
	   return LIST_PAGE;
   }
   
   /**
    * This gets the list of evluators for the wizard
    * @param wizard
    * @return List
    */
   protected List getEvaluators(Wizard wizard) {
      ResourceBundle myResources = 
         ResourceBundle.getBundle("org.theospi.portfolio.wizard.bundle.Messages");

      List evalList = new ArrayList();
      Id id = wizard.getId() == null ? wizard.getNewId() : wizard.getId();
      
      List evaluators = getAuthzManager().getAuthorizations(null, 
            WizardFunctionConstants.EVALUATE_WIZARD, id);
      
      for (Iterator iter = evaluators.iterator(); iter.hasNext();) {
         Authorization az = (Authorization) iter.next();
         Agent agent = az.getAgent();
         String userId = az.getAgent().getEid().getValue();
         if (agent.isRole()) {
            evalList.add(MessageFormat.format(myResources.getString("decorated_role_format"), 
                  new Object[]{agent.getDisplayName()}));
         }
         else {
            evalList.add(MessageFormat.format(myResources.getString("decorated_user_format"),
                  new Object[]{agent.getDisplayName(), userId}));
         }
      }
      
      return evalList;
   }
   
   
   public String processActionSelectStyle() {      
      clearInterface();
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
            try {
               Member member = (Member) memb.next();
               User user = UserDirectoryService.getUser(member.getUserId());
               users.add(createSelect(user.getId(), user.getSortName()));
            }
            catch (UserNotDefinedException e) {
               //TODO replace with a message bundle
               logger.warn("User " + e.getId() + " cannot be found");
            }
         }
      }
      catch (IdUnusedException e) {
         throw new OspException(e);
      }
      return users;
   }
   
   public boolean getCanCreate() {
      return getAuthzManager().isAuthorized(WizardFunctionConstants.CREATE_WIZARD, 
            getIdManager().getId(ToolManager.getCurrentPlacement().getContext()));
   }
   
   public boolean getCanView() {
      return getAuthzManager().isAuthorized(WizardFunctionConstants.VIEW_WIZARD, 
            getIdManager().getId(ToolManager.getCurrentPlacement().getContext()));
   }

   /**
    * This is to check if the current user is authorized by tool permissions to review
    * the current wizard.  individual wizard permissions for review does not exist
    * @return boolean is authorized
    */
   public boolean getCanReview() {
      return getCanReview(current.getBase());
   }

   /**
    * This is to check if the current user is authorized by tool permissions to review
    * the wizards.  individual wizard permissions for review does not exist
    * @return boolean is authorized
    */
   public boolean getCanReview(Wizard wizard) {
      
      return getAuthzManager().isAuthorized(WizardFunctionConstants.REVIEW_WIZARD, 
            wizard.getId());
   }

   /**
    * This is to check if the current user is authorized by tool permissions to review
    * the various wizards
    * @return boolean is authorized
    */
   public boolean getCanReviewTool() {
      return getAuthzManager().isAuthorized(WizardFunctionConstants.REVIEW_WIZARD, 
            getIdManager().getId(ToolManager.getCurrentPlacement().getContext()));
   }

   /**
    * This is to check if the current user is authorized by tool permissions to evaluate
    * the various wizards
    * @return boolean is authorized
    */
   public boolean getCanEvaluateTool() {
      return getAuthzManager().isAuthorized(WizardFunctionConstants.EVALUATE_WIZARD, 
            getIdManager().getId(ToolManager.getCurrentPlacement().getContext()));
   }

   /**
    * This is to check if the current user is listed as an evaluator of the current wizard
    * @return boolean is authorized
    */
   public boolean getCanEvaluate() {
      return getCanEvaluate(current.getBase());
   }


   /**
    * This is to check if the current user is listed as an evaluator of the given wizard
    * @param Wizard wizard to check
    * @return boolean is authorized
    */
   public boolean getCanEvaluate(Wizard wizard) {
      return getAuthzManager().isAuthorized(WizardFunctionConstants.EVALUATE_WIZARD, 
            wizard.getId());
   }


   /**
    * This is to check if the current user is can operate on the wizard.
    * The operate permission mean view or review or evaluate wizard
    * @param Wizard wizard to check
    * @return boolean is authorized
    */
   public boolean getCanOperate(Wizard wizard) {
      return getAuthzManager().isAuthorized(WizardFunctionConstants.OPERATE_WIZARD, 
            wizard.getId());
   }
   
   public boolean getCanPublish(Wizard wizard) {
      return getAuthzManager().isAuthorized(WizardFunctionConstants.PUBLISH_WIZARD, 
            wizard.getId()) && !wizard.isPublished();
   }
   
   public boolean getCanDelete(Wizard wizard) {
      if (wizard.getOwner() == null) return false;
      return getAuthzManager().isAuthorized(WizardFunctionConstants.DELETE_WIZARD, 
            wizard.getId()) && SessionManager.getCurrentSessionUserId().equalsIgnoreCase(
                  wizard.getOwner().getId().getValue()) && !wizard.isPublished();
   }
   
   public boolean getCanEdit(Wizard wizard) {
      if (wizard.getOwner() == null) return false;
      return getAuthzManager().isAuthorized(WizardFunctionConstants.EDIT_WIZARD, 
            wizard.getId()) && SessionManager.getCurrentSessionUserId().equalsIgnoreCase(
                  wizard.getOwner().getId().getValue());
   }
   
   public boolean getCanExport(Wizard wizard) {
      if (wizard.getOwner() == null) return false;
      return getAuthzManager().isAuthorized(WizardFunctionConstants.EXPORT_WIZARD, 
            wizard.getId()) && SessionManager.getCurrentSessionUserId().equalsIgnoreCase(
                  wizard.getOwner().getId().getValue());
   }
   
   protected Collection getFormsForSelect(String type) {
      Placement placement = ToolManager.getCurrentPlacement();
      String currentSiteId = placement.getContext();
      Collection forms = 
               getWizardManager().getAvailableForms(currentSiteId, type);
      
      List retForms = new ArrayList();
      for(Iterator iter = forms.iterator(); iter.hasNext();) {
         StructuredArtifactDefinitionBean sad = (StructuredArtifactDefinitionBean) iter.next(); 
         retForms.add(createSelect(sad.getId().getValue(), sad.getDecoratedDescription()));
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
               getMessageFromBundle(Wizard.WIZARD_TYPE_SEQUENTIAL) + 
               getMessageFromBundle(Wizard.WIZARD_TYPE_SEQUENTIAL+"_additional")));
         wizardTypes.add(createSelect(Wizard.WIZARD_TYPE_HIERARCHICAL,
               getMessageFromBundle(Wizard.WIZARD_TYPE_HIERARCHICAL)+
               getMessageFromBundle(Wizard.WIZARD_TYPE_HIERARCHICAL+"_additional")));
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
   
   /**
    * @return AuthenticationManager
    */
   public AuthenticationManager getAuthManager() {
      return authManager;
   }

   /**
    * @param manager
    */
   public void setAuthManager(AuthenticationManager manager) {
      authManager = manager;
   }

   public String getLastSaveWizard() {
      return lastSaveWizard;
   }

   public void setLastSaveWizard(String lastSaveWizard) {
      this.lastSaveWizard = lastSaveWizard;
   }

   protected void checkSubmittedPage()
   {
      ToolSession session = SessionManager.getCurrentToolSession();
      if(session.getAttribute("submittedPage") != null) {
         WizardPage page = (WizardPage)session.getAttribute("submittedPage");
         session.removeAttribute("submittedPage");
         
         lastSavePage = page.getPageDefinition().getTitle();
      }
   }
   protected void checkSavedPage()
   {
      ToolSession session = SessionManager.getCurrentToolSession();
      if(session.getAttribute("savedPage") != null) {
         WizardPage page = (WizardPage)session.getAttribute("savedPage");
         session.removeAttribute("savedPage");
         
         pageSaved = true;
      }
   }
   public boolean isPageSaved() {
      checkSavedPage();
      return pageSaved;
   }

   public void setPageSaved(boolean pageSaved) {
      this.pageSaved = pageSaved;
   }

   public String getLastSavePage() {
      checkSubmittedPage();
      return lastSavePage;
   }

   public void setLastSavePage(String lastSavePage) {
      this.lastSavePage = lastSavePage;
   }

   public String getLastError() {
      return lastError;
   }

   public void setLastError(String lastError) {
      this.lastError = lastError;
   }

}

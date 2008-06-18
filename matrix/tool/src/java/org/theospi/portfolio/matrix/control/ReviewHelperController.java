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
package org.theospi.portfolio.matrix.control;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.sakaiproject.util.ResourceLoader;
import org.sakaiproject.content.api.ContentCollection;
import org.sakaiproject.content.api.ContentCollectionEdit;
import org.sakaiproject.content.api.ContentHostingService;
import org.sakaiproject.content.api.ContentResource;
import org.sakaiproject.content.api.LockManager;
import org.sakaiproject.content.api.ResourceEditingHelper;
import org.sakaiproject.entity.api.ResourceProperties;
import org.sakaiproject.exception.IdUnusedException;
import org.sakaiproject.exception.IdUsedException;
import org.sakaiproject.exception.PermissionException;
import org.sakaiproject.exception.TypeException;
import org.sakaiproject.metaobj.shared.FormHelper;
import org.sakaiproject.metaobj.shared.Helper;
import org.sakaiproject.metaobj.shared.mgt.IdManager;
import org.sakaiproject.metaobj.shared.model.Id;
import org.sakaiproject.metaobj.utils.mvc.intf.Controller;
import org.sakaiproject.site.cover.SiteService;
import org.sakaiproject.tool.api.Placement;
import org.sakaiproject.tool.cover.ToolManager;
import org.sakaiproject.user.api.User;
import org.sakaiproject.user.cover.UserDirectoryService;
import org.springframework.validation.Errors;
import org.springframework.web.servlet.ModelAndView;
import org.theospi.portfolio.matrix.MatrixManager;
import org.theospi.portfolio.matrix.model.WizardPage;
import org.theospi.portfolio.review.ReviewHelper;
import org.theospi.portfolio.review.mgt.ReviewManager;
import org.theospi.portfolio.review.model.Review;
import org.theospi.portfolio.shared.model.Node;
import org.theospi.portfolio.shared.model.ObjectWithWorkflow;
import org.theospi.portfolio.style.mgt.StyleManager;
import org.theospi.portfolio.wizard.mgt.WizardManager;
import org.theospi.portfolio.wizard.model.CompletedWizard;
import org.theospi.portfolio.workflow.model.Workflow;

public class ReviewHelperController implements Controller {

   private static ResourceLoader myResources = new ResourceLoader("org.theospi.portfolio.matrix.bundle.Messages");
	
   private MatrixManager matrixManager;
   private IdManager idManager = null;
   private ReviewManager reviewManager;
   private WizardManager wizardManager;
   private LockManager lockManager;
   private ContentHostingService contentHosting;
   private StyleManager styleManager;

   public ModelAndView handleRequest(Object requestModel, Map request, Map session, Map application, Errors errors) {
      String strId = null;
      String lookupId = null;
      String returnView = "return";
      String manager = "";

      if (request.get("process_type_key") != null) {
         session.put("process_type_key", request.get("process_type_key"));
         session.put(ReviewHelper.REVIEW_TYPE_KEY, request.get(ReviewHelper.REVIEW_TYPE_KEY));
      }

      String processTypeKey = (String)session.get("process_type_key");


      if (processTypeKey != null && !processTypeKey.equals(WizardPage.PROCESS_TYPE_KEY)) {
         lookupId = processTypeKey;
         returnView = "helperDone";
         manager = "org.theospi.portfolio.wizard.mgt.WizardManager";
      }
      else if (processTypeKey != null) {
         lookupId = processTypeKey;
         manager = "matrixManager";
      }
      strId = (String) request.get(lookupId);
      if (strId==null) {
         strId = (String) session.get(lookupId);
      }

      // 
      // If this is the second pass, 
      // then we are creating a new [feedback | evaluation | reflection] review
      //
      String secondPass = (String)session.get("secondPass");
      if (secondPass != null) {
         strId = (String)session.get(lookupId);
         String formType = (String)session.get(ResourceEditingHelper.CREATE_SUB_TYPE);
         String itemId = (String)session.get(ReviewHelper.REVIEW_ITEM_ID);
         String currentReviewId = (String)session.get(ResourceEditingHelper.ATTACHMENT_ID);

         Map<String, Object> model = new HashMap<String, Object>();
         model.put(lookupId, strId);

         Placement placement = ToolManager.getCurrentPlacement();
         String currentSite = placement.getContext();

         // check if this is a new review
         if ( currentReviewId == null ) {
            Review review = getReviewManager().createNew("New Review", currentSite);
            review.setDeviceId(formType);
            review.setParent(strId);
            review.setItemId(itemId);
            String strType = (String)session.get(ReviewHelper.REVIEW_TYPE);
            review.setType(Integer.parseInt(strType));

            if (FormHelper.RETURN_ACTION_SAVE.equals((String)session.get(FormHelper.RETURN_ACTION_TAG)) 
                && session.get(FormHelper.RETURN_REFERENCE_TAG) != null) 
            {
               String artifactId = (String)session.get(FormHelper.RETURN_REFERENCE_TAG);
               session.remove(FormHelper.RETURN_REFERENCE_TAG);
               Node node = getMatrixManager().getNode(getIdManager().getId(artifactId));
               
               review.setReviewContentNode(node);
               review.setReviewContent(node.getId());
               getReviewManager().saveReview(review);
            }
            
            // Lock review content (reflection, feedback, evaluation)
            getLockManager().lockObject(review.getReviewContent().getValue(),
                                        strId, "lock all review content", true);
                  
         }
         
         // otherwise this is an existing review being edited
         else {
            // Lock review content (reflection, feedback, evaluation)
            currentReviewId = contentHosting.getUuid( currentReviewId );
            getLockManager().lockObject(currentReviewId,
                                        strId, "lock all review content", true);
                  
         }

         // Clean up session attributes         
         session.remove(ResourceEditingHelper.CREATE_TYPE);
         session.remove(ResourceEditingHelper.CREATE_SUB_TYPE);
         session.remove(ReviewHelper.REVIEW_TYPE);
         session.remove(ResourceEditingHelper.CREATE_PARENT);
         session.remove(ReviewHelper.REVIEW_ITEM_ID);
         session.remove(lookupId);
         //session.remove("process_type_key");
         session.remove("secondPass");
         session.remove(FormHelper.RETURN_ACTION_TAG);
         
         // Check for workflow post process
         if (session.get(ReviewHelper.REVIEW_POST_PROCESSOR_WORKFLOWS) != null) {
            Set workflows = (Set)session.get(ReviewHelper.REVIEW_POST_PROCESSOR_WORKFLOWS);
            List wfList = Arrays.asList(workflows.toArray());
            Collections.sort(wfList, Workflow.getComparator());
            model.put("workflows", wfList);
            model.put("manager", manager);
            model.put("obj_id", strId);
            return new ModelAndView("postProcessor", model);
         }
         
         return new ModelAndView(returnView, model);
      }

      //
      // This is the first pass, 
      // so we are presenting the form to create a new [feedback | evaluation | reflection] review
      //
      String ownerEid = null;
      String pageTitle = null;
      Id id = getIdManager().getId(strId);
      ObjectWithWorkflow obj = null;
      if (lookupId.equals(WizardPage.PROCESS_TYPE_KEY)) {
         WizardPage page = matrixManager.getWizardPage(id);
         obj = page.getPageDefinition();
         pageTitle = page.getPageDefinition().getTitle();
         ownerEid = page.getOwner().getEid().getValue();
      }
      else {
         CompletedWizard cw = wizardManager.getCompletedWizard(id);
         obj = cw.getWizard();
         ownerEid = cw.getOwner().getEid().getValue();
      }


      String type = (String)session.get(ReviewHelper.REVIEW_TYPE_KEY);
      session.remove(ReviewHelper.REVIEW_TYPE_KEY);
      int intType = Integer.parseInt(type);

      String formTypeId = "";
      String formTypeTitleKey = "";
      session.remove(ReviewHelper.REVIEW_POST_PROCESSOR_WORKFLOWS);
      switch (intType) {
         case Review.FEEDBACK_TYPE:
            formTypeId = obj.getReviewDevice().getValue();
            formTypeTitleKey = "osp.reviewType." + Review.FEEDBACK_TYPE;
            break;
         case Review.EVALUATION_TYPE:
            formTypeId = obj.getEvaluationDevice().getValue();
            session.put(ReviewHelper.REVIEW_POST_PROCESSOR_WORKFLOWS,
                  obj.getEvalWorkflows());
            formTypeTitleKey = "osp.reviewType." + Review.EVALUATION_TYPE;
            break;
         case Review.REFLECTION_TYPE:
            formTypeId = obj.getReflectionDevice().getValue();
            // set ownerEid to null since we don't need it for the reflection
            ownerEid = null;
            formTypeTitleKey = "osp.reviewType." + Review.REFLECTION_TYPE;
            break;
      }


      String formView = "formCreator";
      session.put(ReviewHelper.REVIEW_TYPE, type);
      session.put(ResourceEditingHelper.CREATE_TYPE,
            ResourceEditingHelper.CREATE_TYPE_FORM);


      formView = setupSessionInfo(request, session, pageTitle, formTypeId, formTypeTitleKey, ownerEid, strId);
      session.put("page_id", strId);
      session.put("secondPass", "true");
      return new ModelAndView(formView, Helper.HELPER_SESSION_ID, request.get(Helper.HELPER_SESSION_ID));

   }

   /**
    * 
    * @param request
    * @param session
    * @param pageTitle
    * @param formTypeId
    * @param formTypeTitleKey
    * @param ownerEid The eid of the user that owns the object in question (wizard or page)
    * @param pageId the id of the page
    * @return
    */
   protected String setupSessionInfo(Map request, Map<String, Object> session,
                                     String pageTitle, String formTypeId, String formTypeTitleKey,
                                     String ownerEid, String pageId) {
      String retView = "formCreator";

      // check if this is a request for a new rewiew (i.e. no current_review_id)
      if (request.get("current_review_id") == null) {
         session.remove(ResourceEditingHelper.ATTACHMENT_ID);
         session.put(ResourceEditingHelper.CREATE_TYPE,
               ResourceEditingHelper.CREATE_TYPE_FORM);
         session.put(ResourceEditingHelper.CREATE_SUB_TYPE, formTypeId);

         String objectId = (String)request.get("objectId");
         String objectTitle = (String)request.get("objectTitle");

         String itemId = (String)request.get("itemId");
         session.put(ReviewHelper.REVIEW_ITEM_ID, itemId);

         String formTypeTitle = myResources.getString(formTypeTitleKey);

         List contentResourceList = null;
         try {
            String folderBase = getUserCollection().getId();

            Placement placement = ToolManager.getCurrentPlacement();
            String currentSite = placement.getContext();

            String rootDisplayName = myResources.getString("portfolioInteraction.displayName");
            String rootDescription = myResources.getString("portfolioInteraction.description");

            String folderPath = createFolder(folderBase, "portfolio-interaction", rootDisplayName, rootDescription);
            folderPath = createFolder(folderPath, currentSite, SiteService.getSiteDisplay(currentSite), null);
            folderPath = createFolder(folderPath, objectId, objectTitle, null);
            folderPath = createFolder(folderPath, formTypeId, formTypeTitle, null);
           
            contentResourceList = this.getContentHosting().getAllResources(folderPath);

            session.put(FormHelper.PARENT_ID_TAG, folderPath);
         } catch (TypeException e) {
            throw new RuntimeException("Failed to redirect to helper", e);
         } catch (IdUnusedException e) {
            throw new RuntimeException("Failed to redirect to helper", e);
         } catch (PermissionException e) {
            throw new RuntimeException("Failed to redirect to helper", e);
         }

         //CWM OSP-UI-09 - for auto naming
         session.put(FormHelper.NEW_FORM_DISPLAY_NAME_TAG, getFormDisplayName(objectTitle, pageTitle, formTypeTitle, ownerEid, 1, contentResourceList));
      } 
		
      // Otherwise, editting an existing review
      else {
         String currentReviewId = (String)request.get("current_review_id");
         session.remove(ResourceEditingHelper.CREATE_TYPE);
         session.remove(ResourceEditingHelper.CREATE_SUB_TYPE);
         session.remove(ResourceEditingHelper.CREATE_PARENT);
         session.put(ResourceEditingHelper.CREATE_TYPE,
            ResourceEditingHelper.CREATE_TYPE_FORM);
         session.put(ResourceEditingHelper.ATTACHMENT_ID, currentReviewId);
         
         // unlock review content for edit
         String reviewContentId = contentHosting.getUuid( currentReviewId );
         if ( getLockManager().isLocked(reviewContentId) ) {
            getLockManager().removeLock(reviewContentId, pageId );
         }
         
         retView = "formEditor";
      }
      session.put(FormHelper.FORM_STYLES,
         getStyleManager().createStyleUrlList(getStyleManager().getStyles(getIdManager().getId(pageId))));

      return retView;
   }

   /**
    * 
    * @param objectTitle
    * @param pageTitle
    * @param formTypeName
    * @param ownerEid
    * @param count: this keeps track of the number of times getFormDisplayName is called for naming reasons
    * @param contentResourceList: a list of the resources for looking up the names to compare to the new name
    * @return
    */
   protected String getFormDisplayName(String objectTitle, String pageTitle, String formTypeName, String ownerEid, int count, List contentResourceList) {
      String includePageTitle = "";
      String includeOwner = "";
      String name = "";

      if (pageTitle != null && pageTitle.length() > 0) {
         includePageTitle = pageTitle + "-";
      }

      if (ownerEid != null && ownerEid.length() > 0) {
         includeOwner = ownerEid + "-";
      }

      name = objectTitle + "-" + includePageTitle + includeOwner + formTypeName;
      
      if(count > 1){
    	  name = name + " (" + count + ")";
      }
      
      count++;
      
      //if the name already exists, then recursively loop through this function untill there is an unique name      
      return formDisplayNameExists(name, contentResourceList) && contentResourceList != null ? 
    		  getFormDisplayName(objectTitle, pageTitle, formTypeName, ownerEid, count, contentResourceList) : name;
   }
   
   /**
    * 
    * @param name
    * @param contentResourceList
    * @return
    * 
    * returns true if the name passed exists in the list of contentResource
    * otherwise returns false
    */
   protected boolean formDisplayNameExists(String name, List contentResourceList){
	   
	   
	   if(contentResourceList != null){
		   ContentResource cr;
		   for(int i = 0; i < contentResourceList.size(); i++){
			   cr = (ContentResource) contentResourceList.get(i);
			   if(name.equals(cr.getProperties().getProperty(cr.getProperties().getNamePropDisplayName()).toString())){
				   return true;
			   }
		   }
	   }
  
	   return false;
   }

   /**
    * 
    * @param base
    * @param append
    * @param appendDisplay
    * @param appendDescription
    * @return
    */
   protected String createFolder(String base, String append, String appendDisplay, String appendDescription) {
      String folder = base + append + "/";

      try {
         ContentCollectionEdit propFolder = getContentHosting().addCollection(folder);
         propFolder.getPropertiesEdit().addProperty(ResourceProperties.PROP_DISPLAY_NAME, appendDisplay);
         propFolder.getPropertiesEdit().addProperty(ResourceProperties.PROP_DESCRIPTION, appendDescription);
         getContentHosting().commitCollection(propFolder);
         return propFolder.getId();
      }
      catch (IdUsedException e) {
         // ignore... it is already there.
      }
      catch (Exception e) {
         throw new RuntimeException(e);
      }
      return folder;
   }

   /**
    * 
    * @return
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
    * @return Returns the matrixManager.
    */
   public MatrixManager getMatrixManager() {
      return matrixManager;
   }

   /**
    * @param matrixManager The matrixManager to set.
    */
   public void setMatrixManager(MatrixManager matrixManager) {
      this.matrixManager = matrixManager;
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
    * @return Returns the wizardManager.
    */
   public WizardManager getWizardManager() {
      return wizardManager;
   }

   /**
    * @param wizardManager The wizardManager to set.
    */
   public void setWizardManager(WizardManager wizardManager) {
      this.wizardManager = wizardManager;
   }

   public LockManager getLockManager() {
      return lockManager;
   }
   public void setLockManager(LockManager lockManager) {
      this.lockManager = lockManager;
   }

   /**
    * @return the contentHosting
    */
   public ContentHostingService getContentHosting() {
      return contentHosting;
   }

   /**
    * @param contentHosting the contentHosting to set
    */
   public void setContentHosting(ContentHostingService contentHosting) {
      this.contentHosting = contentHosting;
   }

   public StyleManager getStyleManager() {
      return styleManager;
   }

   public void setStyleManager(StyleManager styleManager) {
      this.styleManager = styleManager;
   }

}

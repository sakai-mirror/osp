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

import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

import org.sakaiproject.content.api.ContentCollection;
import org.sakaiproject.content.api.ContentCollectionEdit;
import org.sakaiproject.content.api.ContentHostingService;
import org.sakaiproject.content.api.LockManager;
import org.sakaiproject.content.api.ResourceEditingHelper;
import org.sakaiproject.entity.api.ResourceProperties;
import org.sakaiproject.exception.IdUnusedException;
import org.sakaiproject.exception.IdUsedException;
import org.sakaiproject.exception.PermissionException;
import org.sakaiproject.exception.TypeException;
import org.sakaiproject.metaobj.shared.FormHelper;
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
import org.theospi.portfolio.wizard.mgt.WizardManager;
import org.theospi.portfolio.wizard.model.CompletedWizard;

public class ReviewHelperController implements Controller {
   
   private MatrixManager matrixManager;
   private IdManager idManager = null;
   private ReviewManager reviewManager;
   private WizardManager wizardManager;
   private LockManager lockManager;
   private ContentHostingService contentHosting;

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
      String secondPass = (String)session.get("secondPass");
      if (secondPass != null) {
         strId = (String)session.get(lookupId);
         String formType = (String)session.get(ResourceEditingHelper.CREATE_SUB_TYPE);
         
         Map model = new HashMap();
         model.put(lookupId, strId);
         
         Placement placement = ToolManager.getCurrentPlacement();
         String currentSite = placement.getContext();
         //cwm add security stuff for review
         Review review = getReviewManager().createNew( 
               "New Review", currentSite);
         review.setDeviceId(formType);
         review.setParent(strId);
         String strType = (String)session.get(ReviewHelper.REVIEW_TYPE);
         review.setType(Integer.parseInt(strType));
         
         session.remove(ResourceEditingHelper.CREATE_TYPE);
         session.remove(ResourceEditingHelper.CREATE_SUB_TYPE);
         session.remove(ReviewHelper.REVIEW_TYPE);
         session.remove(ResourceEditingHelper.CREATE_PARENT);
         session.remove(lookupId);
         //session.remove("process_type_key");
         session.remove("secondPass");
         
         if (FormHelper.RETURN_ACTION_SAVE.equals((String)session.get(FormHelper.RETURN_ACTION_TAG)) && 
               session.get(FormHelper.RETURN_REFERENCE_TAG) != null) {
            String artifactId = (String)session.get(FormHelper.RETURN_REFERENCE_TAG);
            Node node = getMatrixManager().getNode(getIdManager().getId(artifactId));
            
            review.setReviewContentNode(node);
            review.setReviewContent(node.getId());
            getReviewManager().saveReview(review);
            
            session.remove(FormHelper.RETURN_REFERENCE_TAG);
            session.remove(FormHelper.RETURN_ACTION_TAG);
            
            if(review.getType() == Review.EVALUATION_TYPE || review.getType() == Review.FEEDBACK_TYPE)
               getLockManager().lockObject(review.getReviewContent().getValue(), 
                  strId, "evals and review always locked", true);
            
            if (session.get(ReviewHelper.REVIEW_POST_PROCESSOR_WORKFLOWS) != null) {
               model.put("workflows", session.get(ReviewHelper.REVIEW_POST_PROCESSOR_WORKFLOWS));
               model.put("manager", manager);
               model.put("obj_id", strId);
               return new ModelAndView("postProcessor", model);
            }   
         }
         /*
         if (session.get(FilePickerHelper.FILE_PICKER_CANCEL) == null &&
               session.get(FilePickerHelper.FILE_PICKER_ATTACHMENTS) != null) {
            // here is where we setup the id
            List refs = (List)session.get(FilePickerHelper.FILE_PICKER_ATTACHMENTS);
            if (refs.size() == 1) {
               Reference ref = (Reference)refs.get(0);
//               ref.getReference()
               Node node = getMatrixManager().getNode(ref);
               review.setReviewContentNode(node);
               review.setReviewContent(node.getId());
               getReviewManager().saveReview(review);

               if(review.getType() == Review.EVALUATION_TYPE || review.getType() == Review.FEEDBACK_TYPE)
                  getLockManager().lockObject(review.getReviewContent().getValue(), 
                     strId, "evals and review always locked", true);
            }
            else {
               review.setReviewContent(null);
            }
            session.remove(FilePickerHelper.FILE_PICKER_ATTACHMENTS);
            session.remove(FilePickerHelper.FILE_PICKER_CANCEL);
         
            if (session.get(ReviewHelper.REVIEW_POST_PROCESSOR_WORKFLOWS) != null) {
               model.put("workflows", session.get(ReviewHelper.REVIEW_POST_PROCESSOR_WORKFLOWS));
               model.put("manager", manager);
               model.put("obj_id", strId);
               return new ModelAndView("postProcessor", model);
            }         
         }        
         */
         //session.remove(FilePickerHelper.FILE_PICKER_CANCEL);
         session.remove(FormHelper.RETURN_ACTION_TAG);
         return new ModelAndView(returnView, model);
      }
      
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
      
      
      formView = setupSessionInfo(request, session, pageTitle, formTypeId, formTypeTitleKey, ownerEid);
      /*
      if (request.get("current_review_id") == null) {
//       CWM OSP-UI-07 - fix the parent path
         session.put(ResourceEditingHelper.CREATE_PARENT, "/user/" + 
               SessionManager.getCurrentSessionUserId() + "/");
         session.put(ResourceEditingHelper.CREATE_SUB_TYPE, formTypeId);
         session.remove(ResourceEditingHelper.ATTACHMENT_ID);
         
      } else {
         session.put(ResourceEditingHelper.ATTACHMENT_ID, request.get("current_review_id"));
      }
      */
      session.put("page_id", strId);
      session.put("secondPass", "true");
      return new ModelAndView(formView);
      
   }
   
   /**
    * 
    * @param request
    * @param session
    * @param pageTitle
    * @param formTypeId
    * @param formTypeTitleKey
    * @param ownerEid The eid of the user that owns the object in question (wizard or page)
    * @return
    */
   protected String setupSessionInfo(Map request, Map<String, Object> session, 
         String pageTitle, String formTypeId, String formTypeTitleKey, String ownerEid) {
      String retView = "formCreator";
      //session.put(ResourceEditingHelper.CREATE_TYPE,
      //      ResourceEditingHelper.CREATE_TYPE_FORM);
      
      
      if (request.get("current_review_id") == null) {
         session.remove(ResourceEditingHelper.ATTACHMENT_ID);
         session.put(ResourceEditingHelper.CREATE_TYPE,
               ResourceEditingHelper.CREATE_TYPE_FORM);
         session.put(ResourceEditingHelper.CREATE_SUB_TYPE, formTypeId);
         
         String objectId = (String)request.get("objectId");
         String objectTitle = (String)request.get("objectTitle");
         String objectDesc = (String)request.get("objectDesc");
         
         //StructuredArtifactDefinitionBean bean = getStructuredArtifactDefinitionManager().loadHome(formTypeId);
         
         ResourceBundle myResources = 
            ResourceBundle.getBundle("org.theospi.portfolio.matrix.bundle.Messages");
         String formTypeTitle = myResources.getString(formTypeTitleKey);
         
         try {
            String folderBase = getUserCollection().getId();
            
            Placement placement = ToolManager.getCurrentPlacement();
            String currentSite = placement.getContext();
            
            String folderPath = createFolder(folderBase, "portfolio-interaction", "Portfolio Interaction", "Folder to store forms uesd when interacting with Portfolio tools");
            folderPath = createFolder(folderPath, currentSite, SiteService.getSiteDisplay(currentSite), null);
            folderPath = createFolder(folderPath, objectId, objectTitle, objectDesc);
            folderPath = createFolder(folderPath, formTypeId, formTypeTitle, null);
            
            session.put(FormHelper.PARENT_ID_TAG, folderPath);
         } catch (TypeException e) {
            throw new RuntimeException("Failed to redirect to helper", e);
         } catch (IdUnusedException e) {
            throw new RuntimeException("Failed to redirect to helper", e);
         } catch (PermissionException e) {
            throw new RuntimeException("Failed to redirect to helper", e);
         }
         
         //CWM OSP-UI-09 - for auto naming
         session.put(FormHelper.NEW_FORM_DISPLAY_NAME_TAG, getFormDisplayName(objectTitle, pageTitle, formTypeTitle, ownerEid));
      } else {
         //session.put(ResourceEditingHelper.ATTACHMENT_ID, request.get("current_form_id"));
         session.remove(ResourceEditingHelper.CREATE_TYPE);
         session.remove(ResourceEditingHelper.CREATE_SUB_TYPE);
         session.remove(ResourceEditingHelper.CREATE_PARENT);
         session.put(ResourceEditingHelper.CREATE_TYPE,
            ResourceEditingHelper.CREATE_TYPE_FORM);
         session.put(ResourceEditingHelper.ATTACHMENT_ID, request.get("current_review_id"));
         retView = "formEditor";
      }
      return retView;
   }

   /**
    * 
    * @param objectTitle
    * @param pageTitle
    * @param formTypeName
    * @param ownerEid
    * @return
    */
   protected String getFormDisplayName(String objectTitle, String pageTitle, String formTypeName, String ownerEid) {
      String includePageTitle = "";
      String includeOwner = "";
      
      if (pageTitle != null && pageTitle.length() > 0)
         includePageTitle = pageTitle + "-";
      
      if (ownerEid != null && ownerEid.length() > 0)
         includeOwner = ownerEid + "-";
      
      return objectTitle + "-" + includePageTitle + includeOwner + formTypeName;
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
   
}

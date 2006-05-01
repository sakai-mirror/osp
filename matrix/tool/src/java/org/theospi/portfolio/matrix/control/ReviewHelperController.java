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
package org.theospi.portfolio.matrix.control;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.sakaiproject.tool.cover.SessionManager;
import org.sakaiproject.tool.api.Placement;
import org.sakaiproject.tool.cover.ToolManager;
import org.sakaiproject.metaobj.shared.mgt.IdManager;
import org.sakaiproject.metaobj.shared.model.Id;
import org.sakaiproject.metaobj.utils.mvc.intf.Controller;
import org.sakaiproject.entity.api.Reference;
import org.sakaiproject.content.api.FilePickerHelper;
import org.sakaiproject.content.api.ResourceEditingHelper;
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
         
         session.remove(FilePickerHelper.FILE_PICKER_CANCEL);
         return new ModelAndView(returnView, model);
      }
      
      Id id = getIdManager().getId(strId);
      ObjectWithWorkflow obj = null;
      if (lookupId.equals(WizardPage.PROCESS_TYPE_KEY)) {
         WizardPage page = matrixManager.getWizardPage(id);
         obj = page.getPageDefinition();
      }
      else {
         CompletedWizard cw = wizardManager.getCompletedWizard(id);
         obj = cw.getWizard();
      }

      
      
      String type = (String)session.get(ReviewHelper.REVIEW_TYPE_KEY);
      session.remove(ReviewHelper.REVIEW_TYPE_KEY);
      int intType = Integer.parseInt(type);
      
      String formTypeId = "";
      switch (intType) {
         case Review.REVIEW_TYPE:
            formTypeId = obj.getReviewDevice().getValue();
            break;
         case Review.EVALUATION_TYPE:
            formTypeId = obj.getEvaluationDevice().getValue();
            session.put(ReviewHelper.REVIEW_POST_PROCESSOR_WORKFLOWS, 
                  obj.getEvalWorkflows());
            break;
         case Review.REFLECTION_TYPE:
            formTypeId = obj.getReflectionDevice().getValue();
            break;
      }      
      
      
      
      session.put(ReviewHelper.REVIEW_TYPE, type);
      session.put(ResourceEditingHelper.CREATE_TYPE,
            ResourceEditingHelper.CREATE_TYPE_FORM);
      
      
      if (request.get("current_review_id") == null) {
//       CWM fix the parent path
         session.put(ResourceEditingHelper.CREATE_PARENT, "/user/" + 
               SessionManager.getCurrentSessionUserId() + "/");
         session.put(ResourceEditingHelper.CREATE_SUB_TYPE, formTypeId);
         session.remove(ResourceEditingHelper.ATTACHMENT_ID);
         
      } else {
         session.put(ResourceEditingHelper.ATTACHMENT_ID, request.get("current_review_id"));
      }
      
      session.put("page_id", strId);
      session.put("secondPass", "true");
      return new ModelAndView("success");
      
   }
/*   
   protected void createPermissions(Review review, String type) {
      Node node = review.getReviewContentNode();
      Agent reviewOwner = node.getTechnicalMetadata().getOwner();
      Agent owner = getObjectOwner(getIdManager().getId(review.getParent()), type);
      if (!reviewOwner.getId().equals(owner.getId())) {
         //authz for owner
         //getAuthzManager().createAuthorization(owner, 
         //      ContentHostingService.EVENT_RESOURCE_READ, node.getId());
      }
      else {
         //Authz for reviewer/eval
      }
   }
   
   protected Agent getObjectOwner(Id id, String type) {
      
      if (type.equals(WizardPage.PROCESS_TYPE_KEY)) {
         //WizardPage page = matrixManager.getWizardPage(id);
         Matrix matrix = getMatrixManager().getMatrixByPage(id);
         if (matrix != null) {
            return matrix.getOwner();
         }
         else {
            CompletedWizard cw = getWizardManager().getCompletedWizardByPage(id);
            return cw.getOwner();
         }
      }
      else {
         CompletedWizard cw = wizardManager.getCompletedWizard(id);
         return cw.getOwner();
      }
   }
 */  
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
   
}

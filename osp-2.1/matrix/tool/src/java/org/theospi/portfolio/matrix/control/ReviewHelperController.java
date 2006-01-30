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

import org.sakaiproject.api.kernel.session.cover.SessionManager;
import org.sakaiproject.api.kernel.tool.Placement;
import org.sakaiproject.api.kernel.tool.cover.ToolManager;
import org.sakaiproject.metaobj.shared.mgt.IdManager;
import org.sakaiproject.metaobj.shared.model.Id;
import org.sakaiproject.metaobj.utils.mvc.intf.Controller;
import org.sakaiproject.service.legacy.entity.Reference;
import org.sakaiproject.service.legacy.filepicker.FilePickerHelper;
import org.sakaiproject.service.legacy.filepicker.ResourceEditingHelper;
import org.springframework.validation.Errors;
import org.springframework.web.servlet.ModelAndView;
import org.theospi.portfolio.matrix.MatrixManager;
import org.theospi.portfolio.matrix.model.WizardPage;
import org.theospi.portfolio.review.ReviewHelper;
import org.theospi.portfolio.review.mgt.ReviewManager;
import org.theospi.portfolio.review.model.Review;

public class ReviewHelperController implements Controller {
   
   private MatrixManager matrixManager;
   private IdManager idManager = null;
   private ReviewManager reviewManager;

   public ModelAndView handleRequest(Object requestModel, Map request, Map session, Map application, Errors errors) {
      String strId = (String) request.get("page_id");
      if (strId== null) {
         strId = (String)session.get("page_id");
         String formType = (String)session.get(ResourceEditingHelper.CREATE_SUB_TYPE);
         
         Map model = new HashMap();
         model.put("page_id", strId);
         
         Placement placement = ToolManager.getCurrentPlacement();
         String currentSite = placement.getContext();
         Review review = getReviewManager().createNew( 
               "New Review", currentSite, getIdManager().getId(strId), "", "");
         review.setDeviceId(formType);
         review.setParent(strId);
         String strType = (String)session.get(ReviewHelper.REVIEW_TYPE);
         review.setType(Integer.parseInt(strType));
         
         session.remove(ResourceEditingHelper.CREATE_TYPE);
         session.remove(ResourceEditingHelper.CREATE_SUB_TYPE);
         session.remove(ReviewHelper.REVIEW_TYPE);
         session.remove(ResourceEditingHelper.CREATE_PARENT);
         session.remove("page_id");
         
         if (session.get(FilePickerHelper.FILE_PICKER_CANCEL) == null &&
               session.get(FilePickerHelper.FILE_PICKER_ATTACHMENTS) != null) {
            // here is where we setup the id
            List refs = (List)session.get(FilePickerHelper.FILE_PICKER_ATTACHMENTS);
            if (refs.size() == 1) {
               Reference ref = (Reference)refs.get(0);
               review.setReviewContent(getMatrixManager().getNode(ref).getId());
               getReviewManager().saveReview(review);
            }
            else {
               review.setReviewContent(null);
            }
            session.remove(FilePickerHelper.FILE_PICKER_ATTACHMENTS);
            session.remove(FilePickerHelper.FILE_PICKER_CANCEL);
         
            if (session.get(ReviewHelper.REVIEW_POST_PROCESSOR_WORKFLOWS) != null) {
               model.put("workflows", session.get(ReviewHelper.REVIEW_POST_PROCESSOR_WORKFLOWS));
               return new ModelAndView("postProcessor", model);
            }         
         }        
         
         session.remove(FilePickerHelper.FILE_PICKER_CANCEL);
         return new ModelAndView("return", model);
      }
      
      Id id = getIdManager().getId(strId);
      WizardPage page = matrixManager.getWizardPage(id);

      
      
      String type = (String)request.get("org_theospi_portfolio_review_type");
      int intType = Integer.parseInt(type);
      
      String formTypeId = "";
      switch (intType) {
         case Review.REVIEW_TYPE:
            formTypeId = page.getPageDefinition().getReviewDevice().getValue();
            break;
         case Review.EVALUATION_TYPE:
            formTypeId = page.getPageDefinition().getEvaluationDevice().getValue();
            session.put(ReviewHelper.REVIEW_POST_PROCESSOR_WORKFLOWS, 
                  page.getPageDefinition().getEvalWorkflows());
            break;
         case Review.REFLECTION_TYPE:
            formTypeId = page.getPageDefinition().getReflectionDevice().getValue();
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
         
      } else {
         session.put(ResourceEditingHelper.ATTACHMENT_ID, request.get("current_review_id"));
      }
      
      session.put("page_id", page.getId().getValue());
      return new ModelAndView("success");
      
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
   
}

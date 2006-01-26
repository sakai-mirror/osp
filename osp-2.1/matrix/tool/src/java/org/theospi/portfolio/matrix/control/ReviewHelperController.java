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
import java.util.Map;

import org.sakaiproject.metaobj.shared.mgt.IdManager;
import org.sakaiproject.metaobj.shared.model.Id;
import org.sakaiproject.metaobj.utils.mvc.intf.Controller;
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

   public ModelAndView handleRequest(Object requestModel, Map request, Map session, Map application, Errors errors) {
      String strId = (String) request.get("page_id");
      if (strId== null) {
         strId = (String)session.get("page_id");
         
         Map model = new HashMap();
         model.put("page_id", strId);
         
         session.remove("page_id");
         session.remove(ReviewHelper.REVIEW_TYPE);
         session.remove(ReviewHelper.REVIEW_FORM_TYPE);
         session.remove(ReviewHelper.REVIEW_PARENT);
         session.remove(ReviewHelper.REVIEW_BUNDLE_PREFIX);
         if (session.get(ReviewHelper.REVIEW_POST_PROCESSOR_WORKFLOWS) != null) {
            model.put("workflows", session.get(ReviewHelper.REVIEW_POST_PROCESSOR_WORKFLOWS));
            return new ModelAndView("postProcessor", model);
         }
         return new ModelAndView("return", model);
      }
      
      Id id = getIdManager().getId(strId);
      WizardPage page = matrixManager.getWizardPage(id);
      
      session.put(ReviewHelper.REVIEW_PARENT, 
            page.getId().getValue());
      
      session.put(ReviewManager.CURRENT_REVIEW_ID, request.get("current_review_id"));
      
      String type = (String)request.get("org_theospi_portfolio_review_type");
      int intType = Integer.parseInt(type);
      
      String bundlePrefix = ""; 
      String formType = "";
      switch (intType) {
         case Review.REVIEW_TYPE:
            bundlePrefix = "review_";
            formType = page.getPageDefinition().getReviewDevice().getValue();
            break;
         case Review.EVALUATION_TYPE:
            bundlePrefix = "eval_";
            formType = page.getPageDefinition().getEvaluationDevice().getValue();
            session.put(ReviewHelper.REVIEW_POST_PROCESSOR_WORKFLOWS, 
                  page.getPageDefinition().getEvalWorkflows());
            break;
         case Review.REFLECTION_TYPE:
            bundlePrefix = "reflection_";
            formType = page.getPageDefinition().getReflectionDevice().getValue();
            break;
      }      
      session.put(ReviewHelper.REVIEW_FORM_TYPE, formType);
      session.put(ReviewHelper.REVIEW_TYPE, type);
      session.put(ReviewHelper.REVIEW_BUNDLE_PREFIX, bundlePrefix); 
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
   
}

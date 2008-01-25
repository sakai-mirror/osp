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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.sakaiproject.content.api.LockManager;
import org.sakaiproject.metaobj.shared.mgt.IdManager;
import org.sakaiproject.metaobj.shared.model.Id;
import org.sakaiproject.metaobj.utils.mvc.intf.Controller;
import org.springframework.validation.Errors;
import org.springframework.web.servlet.ModelAndView;
import org.theospi.portfolio.matrix.MatrixFunctionConstants;
import org.theospi.portfolio.matrix.MatrixManager;
import org.theospi.portfolio.matrix.WizardPageHelper;
import org.theospi.portfolio.matrix.model.Attachment;
import org.theospi.portfolio.matrix.model.WizardPage;
import org.theospi.portfolio.matrix.model.WizardPageForm;
import org.theospi.portfolio.matrix.model.impl.MatrixContentEntityProducer;
import org.theospi.portfolio.review.mgt.ReviewManager;
import org.theospi.portfolio.review.model.Review;

public class ManageCellStatusController implements Controller {

   private MatrixManager matrixManager = null;
   private IdManager idManager = null;
   private LockManager lockManager = null;
   private ReviewManager reviewManager = null;
   
   /* (non-Javadoc)
    * @see org.theospi.utils.mvc.intf.Controller#handleRequest(java.lang.Object, java.util.Map, java.util.Map, java.util.Map, org.springframework.validation.Errors)
    */
   
   public ModelAndView handleRequest(Object requestModel, Map request, Map session, Map application, Errors errors) {
      String viewName = "success";
      String viewAppend = "";
      Id id = idManager.getId((String)request.get("page_id"));
      
      Map<String, Object> model = new HashMap<String, Object>();
      model.put("page_id", id);
      WizardPage page = getMatrixManager().getWizardPage(id);
      
      List<String> statusArray = new ArrayList<String>(4);
      statusArray.add(MatrixFunctionConstants.READY_STATUS);
      statusArray.add(MatrixFunctionConstants.PENDING_STATUS);
      statusArray.add(MatrixFunctionConstants.COMPLETE_STATUS);
      statusArray.add(MatrixFunctionConstants.LOCKED_STATUS);
      
      model.put("statuses", statusArray);
      model.put("readOnlyMatrix", (String)request.get("readOnlyMatrix"));
      
      String cancel = (String)request.get("cancel");
      String next = (String)request.get("continue");
      
      String changeOption = (String)request.get("changeOption");
      
      boolean setSingle = "changeUserOnly".equalsIgnoreCase(changeOption) ? true : false;
      boolean setAll = "changeAll".equalsIgnoreCase(changeOption) ? true : false;
      String newStatusValue = (String)request.get("newStatusValue");
      
      String isWizard = (String)request.get("isWizard");
      String sequential = (String)request.get("sequential");
      if (isWizard != null) {
         model.put("isWizard", isWizard);
      }

      if (sequential == null || sequential.equals("")) {
         sequential = "false";
      }
      if (sequential != null) {
         model.put("sequential", sequential);
         if (Boolean.parseBoolean(isWizard) && !Boolean.parseBoolean(sequential)) {
            viewAppend = "Hier";
         }
      }
      
      if (cancel != null) {
         viewName = "done";
      }
      else if (next != null && setSingle) {
         viewName = "done" + viewAppend;
         setPageStatus(page, newStatusValue);
      }
      else if (next != null && setAll) {
         List<WizardPage> allPages = getMatrixManager().getPagesByPageDef(page.getPageDefinition().getId());
         viewName = "done" + viewAppend;
         for (Iterator<WizardPage> iter = allPages.iterator(); iter.hasNext();) {
            WizardPage iterPage = (WizardPage) iter.next();
            setPageStatus(iterPage, newStatusValue);
         }
      }

      session.put(WizardPageHelper.WIZARD_OWNER, page.getOwner());
      return new ModelAndView(viewName, model);
   }
   
   protected void setPageStatus(WizardPage page, String status) {
      //Set the status only if it needs to be changed
      if (!page.getStatus().equals(status)) {
         page.setStatus(status);
         getMatrixManager().storePage(page);
         if (status.equals(MatrixFunctionConstants.READY_STATUS)) {
            //Unlock page's content
            for (Iterator<Attachment> iter = page.getAttachments().iterator(); iter.hasNext();) {
               Attachment att = (Attachment) iter.next();
               getLockManager().removeLock(att.getArtifactId().getValue(), 
                     page.getId().getValue());
            }
            for (Iterator<WizardPageForm> iter2 = page.getPageForms().iterator(); iter2.hasNext();) {
               WizardPageForm form = (WizardPageForm) iter2.next();
               getLockManager().removeLock(form.getArtifactId().getValue(), 
                     page.getId().getValue());
            }
            //unlock reflection form too 
            List<Review> reflections = getReviewManager().getReviewsByParentAndType(page.getId().getValue(), Review.REFLECTION_TYPE, page.getPageDefinition().getSiteId(),
                  MatrixContentEntityProducer.MATRIX_PRODUCER);
            for (Iterator<Review> iter3 = reflections.iterator(); iter3.hasNext();) {
               Review review = (Review)iter3.next();
               getLockManager().removeLock(review.getReviewContent().getValue(), 
                     page.getId().getValue());
            }
         }
         else {
            //lock everything
            for (Iterator<Attachment> iter = page.getAttachments().iterator(); iter.hasNext();) {
               Attachment att = (Attachment) iter.next();
               getLockManager().lockObject(att.getArtifactId().getValue(), 
                     page.getId().getValue(), "locked by status manager", true);
            }
            for (Iterator<WizardPageForm> iter2 = page.getPageForms().iterator(); iter2.hasNext();) {
               WizardPageForm form = (WizardPageForm) iter2.next();
               getLockManager().lockObject(form.getArtifactId().getValue(), 
                     page.getId().getValue(), "locked by status manager", true);
            }
            //lock reflection form too 
            List<Review> reflections = getReviewManager().getReviewsByParentAndType(page.getId().getValue(), Review.REFLECTION_TYPE, page.getPageDefinition().getSiteId(),
                  MatrixContentEntityProducer.MATRIX_PRODUCER);
            for (Iterator<Review> iter3 = reflections.iterator(); iter3.hasNext();) {
               Review review = (Review)iter3.next();
               getLockManager().lockObject(review.getReviewContent().getValue(), 
                     page.getId().getValue(), "locked by status manager", true);
            }
         }
      }
   }

   public IdManager getIdManager() {
      return idManager;
   }
   public void setIdManager(IdManager idManager) {
      this.idManager = idManager;
   }
   public MatrixManager getMatrixManager() {
      return matrixManager;
   }
   public void setMatrixManager(MatrixManager matrixManager) {
      this.matrixManager = matrixManager;
   }
   public LockManager getLockManager() {
      return lockManager;
   }
   public void setLockManager(LockManager lockManager) {
      this.lockManager = lockManager;
   }

   /**
    * @return the reviewManager
    */
   public ReviewManager getReviewManager() {
      return reviewManager;
   }

   /**
    * @param reviewManager the reviewManager to set
    */
   public void setReviewManager(ReviewManager reviewManager) {
      this.reviewManager = reviewManager;
   }
}

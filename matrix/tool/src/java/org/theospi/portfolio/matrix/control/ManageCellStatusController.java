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
package org.theospi.portfolio.matrix.control;

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
      Id id = idManager.getId((String)request.get("page_id"));
      
      Map<String, Object> model = new HashMap<String, Object>();
      model.put("page_id", id);
      //Cell cell = getMatrixManager().getCellFromPage(id);
      WizardPage page = getMatrixManager().getWizardPage(id);
      String newStatus = getNewPageStatus(page);
      model.put("newStatus", newStatus);
      model.put("readOnlyMatrix", (String)request.get("readOnlyMatrix"));
      
      String cancel = (String)request.get("cancel");
      String next = (String)request.get("continue");
      
      String changeOption = (String)request.get("changeOption");
      
      boolean setSingle = "changeUserOnly".equalsIgnoreCase(changeOption) ? true : false;
      boolean setAll = "changeAll".equalsIgnoreCase(changeOption) ? true : false;
      
      if (cancel != null) {
         viewName = "done";
      }
      else if (next != null && setSingle) {
         viewName = "done";
         setPageStatus(page, newStatus);
      }
      else if (next != null && setAll) {
         //Set allCells = cell.getScaffoldingCell().getCells();
         List allPages = getMatrixManager().getPagesByPageDef(page.getPageDefinition().getId());
         //cell.getWizardPage().getPageDefinition().get
         viewName = "done";
         for (Iterator iter = allPages.iterator(); iter.hasNext();) {
            WizardPage iterPage = (WizardPage) iter.next();
            setPageStatus(iterPage, newStatus);
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
            for (Iterator iter = page.getAttachments().iterator(); iter.hasNext();) {
               Attachment att = (Attachment) iter.next();
               getLockManager().removeLock(att.getArtifactId().getValue(), 
                     page.getId().getValue());
            }
            for (Iterator iter2 = page.getPageForms().iterator(); iter2.hasNext();) {
               WizardPageForm form = (WizardPageForm) iter2.next();
               getLockManager().removeLock(form.getArtifactId().getValue(), 
                     page.getId().getValue());
            }
            //unlock reflection form too 
            List reflections = getReviewManager().getReviewsByParentAndType(page.getId().getValue(), Review.REFLECTION_TYPE, page.getPageDefinition().getSiteId(),
                  MatrixContentEntityProducer.MATRIX_PRODUCER);
            for (Iterator iter3 = reflections.iterator(); iter3.hasNext();) {
               Review review = (Review)iter3.next();
               getLockManager().removeLock(review.getReviewContent().getValue(), 
                     page.getId().getValue());
            }
         }
      }
   }
   
   protected String getNewPageStatus (WizardPage page) {
      //TODO setting to READY for now no matter what the previous status is
      String status = page.getStatus();
      /*
      if (cell.getStatus().equals(MatrixFunctionConstants.LOCKED_STATUS))
         status = MatrixFunctionConstants.READY_STATUS;
      else if (cell.getStatus().equals(MatrixFunctionConstants.READY_STATUS))
         status = MatrixFunctionConstants.LOCKED_STATUS;
      */
      status = MatrixFunctionConstants.READY_STATUS;
      return status;
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

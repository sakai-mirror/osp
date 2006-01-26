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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.metaobj.shared.mgt.IdManager;
import org.sakaiproject.metaobj.utils.mvc.intf.CustomCommandController;
import org.sakaiproject.metaobj.utils.mvc.intf.FormController;
import org.sakaiproject.metaobj.utils.mvc.intf.LoadObjectController;
import org.sakaiproject.service.legacy.content.LockManager;
import org.springframework.validation.Errors;
import org.springframework.web.servlet.ModelAndView;
import org.theospi.portfolio.matrix.MatrixFunctionConstants;
import org.theospi.portfolio.matrix.MatrixManager;
import org.theospi.portfolio.matrix.model.Attachment;
import org.theospi.portfolio.matrix.model.Cell;
import org.theospi.portfolio.matrix.model.ReviewRubricValue;
import org.theospi.portfolio.matrix.model.ReviewerItem;


import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * @author chmaurer
 */
public class ReviewCellController implements LoadObjectController, CustomCommandController, FormController {

   protected final Log logger = LogFactory.getLog(getClass());
   private MatrixManager matrixManager = null;
   private IdManager idManager = null;
   private LockManager lockManager;


   /* (non-Javadoc)
    * @see org.theospi.utils.mvc.intf.LoadObjectController#fillBackingObject(java.lang.Object, java.util.Map, java.util.Map, java.util.Map)
    */
   public Object fillBackingObject(Object incomingModel, Map request, Map session, Map application) throws Exception {
      return incomingModel;
   }

   /* (non-Javadoc)
    * @see org.theospi.utils.mvc.intf.Controller#formBackingObject(java.util.Map, java.util.Map, java.util.Map)
    */
   public Object formBackingObject(Map request, Map session, Map application) {
      String strId = (String)request.get("reviewerItem_id");
      return matrixManager.getReviewerItem(idManager.getId(strId));
   }
   
   public Map referenceData(Map request, Object command, Errors errors) {
      Map model = new HashMap();
      model.put("reviewRubrics", matrixManager.getReviewRubrics());
      
      return model;
   }

   /* (non-Javadoc)
    * @see org.theospi.utils.mvc.intf.Controller#handleRequest(java.lang.Object, java.util.Map, java.util.Map, java.util.Map, org.springframework.validation.Errors)
    */
   public ModelAndView handleRequest(Object requestModel, Map request, Map session, Map application, Errors errors) {
      String action = (String)request.get("action");
      ReviewerItem reviewerItem = (ReviewerItem) requestModel;
      
      if (action.equals("Printable View"))
      {
          matrixManager.store(reviewerItem);
          //return new ModelAndView("print", "reviewerItem_id", reviewerItem.getId());
          return new ModelAndView("print", "reviewerItem", reviewerItem);
      }
      else if (action.equals("Save"))
      {
         matrixManager.store(reviewerItem);
      }
      else if (action.equals("Submit"))
      {
         Cell cell = reviewerItem.getCell();
         ReviewRubricValue rrv = getMatrixManager().findReviewRubricValue(reviewerItem.getGrade());
         cell.setStatus(rrv.getNextStatus());
         if (rrv.isUnlockContent()) {
            for (Iterator iter = cell.getAttachments().iterator(); iter.hasNext();) {
               Attachment att = (Attachment) iter.next();
               getLockManager().removeLock(att.getArtifactId().getValue(), 
                     cell.getId().getValue());
            }
         }
         reviewerItem.setStatus(MatrixFunctionConstants.COMPLETE_STATUS);
         matrixManager.storeCell(cell);
      }

      return new ModelAndView("success");
   }
   
   /**
    * @return
    */
   public MatrixManager getMatrixManager() {
      return matrixManager;
   }

   /**
    * @param manager
    */
   public void setMatrixManager(MatrixManager manager) {
      matrixManager = manager;
   }

   /**
    * @return
    */
   public IdManager getIdManager() {
      return idManager;
   }

   /**
    * @param manager
    */
   public void setIdManager(IdManager manager) {
      idManager = manager;
   }
   public LockManager getLockManager() {
      return lockManager;
   }
   public void setLockManager(LockManager lockManager) {
      this.lockManager = lockManager;
   }

}

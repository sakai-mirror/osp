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
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.sakaiproject.metaobj.shared.mgt.IdManager;
import org.sakaiproject.metaobj.shared.model.Id;
import org.sakaiproject.metaobj.utils.mvc.intf.Controller;
import org.sakaiproject.service.legacy.content.LockManager;
import org.springframework.validation.Errors;
import org.springframework.web.servlet.ModelAndView;
import org.theospi.portfolio.matrix.MatrixFunctionConstants;
import org.theospi.portfolio.matrix.MatrixManager;
import org.theospi.portfolio.matrix.model.Attachment;
import org.theospi.portfolio.matrix.model.Cell;

public class ManageCellStatusController implements Controller {

   private MatrixManager matrixManager = null;
   private IdManager idManager = null;
   private LockManager lockManager = null;
   
   /* (non-Javadoc)
    * @see org.theospi.utils.mvc.intf.Controller#handleRequest(java.lang.Object, java.util.Map, java.util.Map, java.util.Map, org.springframework.validation.Errors)
    */
   
   public ModelAndView handleRequest(Object requestModel, Map request, Map session, Map application, Errors errors) {
      String viewName = "success";
      Id id = idManager.getId((String)request.get("page_id"));
      
      Map model = new HashMap();
      model.put("page_id", id);
      Cell cell = getMatrixManager().getCellFromPage(id);
      String newStatus = getNewCellStatus(cell);
      model.put("newStatus", newStatus);
      model.put("readOnlyMatrix", (String)request.get("readOnlyMatrix"));
      
      String cancel = (String)request.get("cancel");
      String next = (String)request.get("continue");
      String setSingle = (String)request.get("changeUserOnly");
      String setAll = (String)request.get("changeAll");
      
      if (cancel != null) {
         viewName = "done";
      }
      else if (next != null && setSingle != null) {
         viewName = "done";
         setCellStatus(cell, newStatus);
      }
      else if (next != null && setAll != null) {
         Set allCells = cell.getScaffoldingCell().getCells();
         viewName = "done";
         for (Iterator iter = allCells.iterator(); iter.hasNext();) {
            Cell iterCell = (Cell) iter.next();
            setCellStatus(iterCell, newStatus);
         }
      }

      return new ModelAndView(viewName, model);
   }
   
   protected void setCellStatus(Cell cell, String status) {
      //Set the status only if it needs to be changed
      if (!cell.getStatus().equals(status)) {
         cell.setStatus(status);
         getMatrixManager().storeCell(cell);
         if (status.equals(MatrixFunctionConstants.READY_STATUS)) {
            //Unlock cell's content
            for (Iterator iter = cell.getAttachments().iterator(); iter.hasNext();) {
               Attachment att = (Attachment) iter.next();
               getLockManager().removeLock(att.getArtifactId().getValue(), 
                     cell.getId().getValue());
            }
         }
      }
   }
   
   protected String getNewCellStatus (Cell cell) {
      //TODO setting to READY for now no matter what the previous status is
      String status = cell.getStatus();
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
}

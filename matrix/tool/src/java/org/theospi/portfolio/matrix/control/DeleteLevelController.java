
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
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.metaobj.utils.mvc.intf.Controller;
import org.sakaiproject.content.api.LockManager;
import org.springframework.validation.Errors;
import org.springframework.web.servlet.ModelAndView;
import org.theospi.portfolio.matrix.MatrixManager;
import org.theospi.portfolio.matrix.model.Cell;
import org.theospi.portfolio.matrix.model.Scaffolding;
import org.theospi.portfolio.matrix.model.ScaffoldingCell;

/**
 * @author chmaurer
 */
public class DeleteLevelController implements Controller {

   protected final Log logger = LogFactory.getLog(getClass());
   
   private MatrixManager matrixManager;
   private LockManager lockManager;
   
   /* (non-Javadoc)
    * @see org.theospi.utils.mvc.intf.Controller#handleRequest(java.lang.Object, java.util.Map, java.util.Map, java.util.Map, org.springframework.validation.Errors)
    */
   public ModelAndView handleRequest(Object requestModel, Map request, Map session, Map application, Errors errors) {
      EditedScaffoldingStorage sessionBean = (EditedScaffoldingStorage)session.get(
            EditedScaffoldingStorage.EDITED_SCAFFOLDING_STORAGE_SESSION_KEY);
      Scaffolding scaffolding = sessionBean.getScaffolding();
      Map model = new HashMap();
      
      String levelId = (String)request.get("level_id");
      String levelIndex = (String)request.get("index");
      
      if (levelIndex != null) {
         scaffolding.getLevels().remove(Integer.parseInt(levelIndex));
      }
      
      if (levelId != null && !levelId.equals("")) {
         Set scaffoldingCells = scaffolding.getScaffoldingCells();
         for (Iterator iter = scaffoldingCells.iterator(); iter.hasNext();) {
            ScaffoldingCell sCell = (ScaffoldingCell) iter.next();
            if (sCell.getLevel().getId().getValue().equals(levelId)) {
               Set cells = sCell.getCells();
               for (Iterator i=cells.iterator(); i.hasNext();) {
                  Cell cell = (Cell) i.next();
                  lockManager.removeAllLocks(cell.getId().getValue());
                  i.remove();
               }
               
               iter.remove();
            }
         }
      }
      
      sessionBean.setScaffolding(scaffolding);
      session.put(EditedScaffoldingStorage.EDITED_SCAFFOLDING_STORAGE_SESSION_KEY,
            sessionBean);
      
      model.put(EditedScaffoldingStorage.STORED_SCAFFOLDING_FLAG, "true");
      return new ModelAndView("success", model);
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

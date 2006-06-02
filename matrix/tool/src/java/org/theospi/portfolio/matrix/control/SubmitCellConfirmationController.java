
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
import java.util.Map;

import org.sakaiproject.metaobj.shared.mgt.IdManager;
import org.sakaiproject.metaobj.shared.model.Id;
import org.sakaiproject.metaobj.utils.mvc.intf.CustomCommandController;
import org.sakaiproject.metaobj.utils.mvc.intf.LoadObjectController;
import org.springframework.validation.Errors;
import org.springframework.web.servlet.ModelAndView;
import org.theospi.portfolio.matrix.MatrixManager;
import org.theospi.portfolio.matrix.WizardPageHelper;
import org.theospi.portfolio.matrix.model.Cell;
import org.theospi.portfolio.matrix.model.WizardPage;

/**
 * @author chmaurer
 */
public class SubmitCellConfirmationController implements LoadObjectController, CustomCommandController {

   IdManager idManager = null;
   MatrixManager matrixManager = null;
   
   /* (non-Javadoc)
    * @see org.theospi.utils.mvc.intf.CustomCommandController#formBackingObject(java.util.Map, java.util.Map, java.util.Map)
    */
   public Object formBackingObject(Map request, Map session, Map application) {
      return new HashMap();
   }
   
   /* (non-Javadoc)
    * @see org.theospi.utils.mvc.intf.LoadObjectController#fillBackingObject(java.lang.Object, java.util.Map, java.util.Map, java.util.Map)
    */
   public Object fillBackingObject(Object incomingModel, Map request, Map session, Map application) throws Exception {
      return incomingModel;
   }

   /* (non-Javadoc)
    * @see org.theospi.utils.mvc.intf.Controller#handleRequest(java.lang.Object, java.util.Map, java.util.Map, java.util.Map, org.springframework.validation.Errors)
    */
   public ModelAndView handleRequest(Object requestModel, Map request, Map session, Map application, Errors errors) {
      boolean isCellPage = false;
      WizardPage page = (WizardPage) session.get(WizardPageHelper.WIZARD_PAGE);
      Id cellId = idManager.getId((String) request.get("page_id"));
      Cell cell = getMatrixManager().getCellFromPage(cellId);
      if (page == null) {
         page = cell.getWizardPage();
         isCellPage = true;
      }
      String submitAction = (String)request.get("submit");
      String cancelAction = (String)request.get("cancel");
      if (submitAction != null) {
         if (!isCellPage) {
            getMatrixManager().submitPageForEvaluation(page);
            session.put("altDoneURL", "submitWizardPage");
            session.put("submittedPage", page);
            return new ModelAndView("done", "page_id", page.getId().getValue());
         }
         else {
            getMatrixManager().submitCellForEvaluation(cell);
         }
         return new ModelAndView("continue", "page_id", page.getId().getValue());
      }
      if (cancelAction != null) {
         return new ModelAndView("continue", "page_id", page.getId().getValue());
      }
      return new ModelAndView("success", "page", page);
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
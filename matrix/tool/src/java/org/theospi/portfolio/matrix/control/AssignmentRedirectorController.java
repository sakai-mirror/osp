/**********************************************************************************
* $URL: $
* $Id: $
***********************************************************************************
*
* Copyright (c) 2007 The Sakai Foundation.
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

import java.util.Map;

import org.sakaiproject.metaobj.shared.mgt.IdManager;
import org.sakaiproject.metaobj.utils.mvc.intf.LoadObjectController;
import org.springframework.validation.Errors;
import org.springframework.web.servlet.ModelAndView;
import org.theospi.portfolio.matrix.MatrixManager;
import org.theospi.portfolio.matrix.model.ScaffoldingCell;
import org.theospi.portfolio.matrix.model.WizardPageDefinition;
import org.theospi.portfolio.style.StyleHelper;
import org.theospi.portfolio.style.model.Style;

public class AssignmentRedirectorController implements LoadObjectController {

   private MatrixManager matrixManager;
   private IdManager idManager = null;

   public ModelAndView handleRequest(Object requestModel, Map request, Map session, Map application, Errors errors) {
      String assignPickerAction = (String) request.get("assignPickerAction");
      String pageId = (String) session.get("pageDef_id");
      if (pageId == null) {
         pageId = (String) request.get("pageDef_id");
         session.put("pageDef_id", pageId);
      }      
      
      if (assignPickerAction != null) {
         session.put("assignReturnView", request.get("assignReturnView"));
         return new ModelAndView("assignRedirector");
      }

      session.put(EditedScaffoldingStorage.STORED_SCAFFOLDING_FLAG, "true");
      String retView = (String)session.get("assignReturnView");
      session.remove("assignReturnView");
      return new ModelAndView(retView);
   }

   public Object fillBackingObject(Object incomingModel, Map request, Map session, Map application) throws Exception {
      String pageId = (String) request.get("pageDef_id");
      if (pageId == null)
         pageId = (String) session.get("pageDef_id");
      
      EditedScaffoldingStorage sessionBean = (EditedScaffoldingStorage)session.get(
            EditedScaffoldingStorage.EDITED_SCAFFOLDING_STORAGE_SESSION_KEY);
      ScaffoldingCell scaffoldingCell = sessionBean.getScaffoldingCell();
      WizardPageDefinition pageDef = scaffoldingCell.getWizardPageDefinition();
      
      return null;
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

}

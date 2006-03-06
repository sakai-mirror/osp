/**********************************************************************************
* $URL$
* $Id$
***********************************************************************************
*
* Copyright (c) 2006 The Sakai Foundation.
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
import org.sakaiproject.metaobj.utils.mvc.intf.FormController;
import org.sakaiproject.metaobj.utils.mvc.intf.LoadObjectController;
import org.springframework.validation.Errors;
import org.springframework.web.servlet.ModelAndView;
import org.theospi.portfolio.matrix.MatrixManager;
import org.theospi.portfolio.matrix.model.WizardPage;
import org.theospi.portfolio.style.StyleHelper;
import org.theospi.portfolio.style.model.Style;

public class StyleRedirectorController implements FormController, LoadObjectController {

   private MatrixManager matrixManager;
   private IdManager idManager = null;
   
   public Map referenceData(Map request, Object command, Errors errors) {
      // TODO Auto-generated method stub
      return null;
   }

   public ModelAndView handleRequest(Object requestModel, Map request, Map session, Map application, Errors errors) {
      String stylePickerAction = (String) request.get("stylePickerAction");
      String pageId = (String) session.get("page_id");
      if (pageId == null) {
         pageId = (String) request.get("page_id");
         session.put("page_id", pageId);
      }      
      
      if (stylePickerAction != null) {
         String currentStyleId = (String)request.get("currentStyleId");
         if (currentStyleId != null)
            session.put(StyleHelper.CURRENT_STYLE_ID, currentStyleId);
         else
            session.remove(StyleHelper.CURRENT_STYLE_ID);
         
         session.put(StyleHelper.STYLE_SELECTABLE, "true");
         
         return new ModelAndView("styleRedirector");
      }
      
      return new ModelAndView("page", "page_id", pageId);
   }

   public Object fillBackingObject(Object incomingModel, Map request, Map session, Map application) throws Exception {
      String pageId = (String) request.get("page_id");
      if (pageId == null) {
         pageId = (String) session.get("page_id");
         //session.remove("page_id");
      }
      WizardPage page = getMatrixManager().getWizardPage(getIdManager().getId(pageId));
      if (session.get(StyleHelper.CURRENT_STYLE) != null) {
         Style style = (Style)session.get(StyleHelper.CURRENT_STYLE);
         page.setStyle(style);
      }
      else if (session.get(StyleHelper.UNSELECTED_STYLE) != null) {
         page.setStyle(null);
         session.remove(StyleHelper.UNSELECTED_STYLE);
      }
      
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

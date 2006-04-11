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

import org.sakaiproject.metaobj.shared.model.Agent;
import org.sakaiproject.metaobj.shared.model.Id;
import org.springframework.validation.Errors;
import org.theospi.portfolio.matrix.model.WizardPageDefinition;
import org.theospi.portfolio.matrix.model.ScaffoldingCell;
import org.theospi.portfolio.matrix.model.WizardPage;
import org.theospi.portfolio.matrix.model.Cell;
import org.theospi.portfolio.matrix.WizardPageHelper;
import org.theospi.portfolio.style.model.Style;
import org.theospi.portfolio.wizard.mgt.WizardManager;
import org.theospi.portfolio.wizard.model.CompletedWizard;

import java.util.Map;
import java.util.List;
import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Jan 24, 2006
 * Time: 3:46:49 PM
 * To change this template use File | Settings | File Templates.
 */
public class WizardPageController extends CellController {
   
   private WizardManager wizardManager;

   /* (non-Javadoc)
    * @see org.theospi.utils.mvc.intf.FormController#referenceData(java.util.Map, java.lang.Object, org.springframework.validation.Errors)
    */
   public Map referenceData(Map request, Object command, Errors errors) {
      Map model = super.referenceData(request, command, errors);
      
      Agent owner = (Agent)request.get("wizardowner");
      model.put("readOnlyMatrix", super.isReadOnly(owner));
      //session.removeAttribute("readOnlyMatrix");
      model.put("pageTitleKey", "view_wizardPage");
      model.put("helperPage", "true");
      return model;
   }
   
   protected Style getDefaultStyle(Id pageId) {
      //Get the wizard default style
      CompletedWizard cw = getWizardManager().getCompletedWizardByPage(pageId);
      return cw.getWizard().getStyle();
   }

   public Object fillBackingObject(Object incomingModel, Map request, Map session, Map application) throws Exception {
      WizardPage page = (WizardPage) session.get(WizardPageHelper.WIZARD_PAGE);
      Id pageId = null;
      if (page != null)
         pageId = page.getId();
      else
         pageId = getIdManager().getId((String)request.get("page_id"));
      page = getMatrixManager().getWizardPage(pageId);
      session.put(WizardPageHelper.WIZARD_PAGE, page);
      session.remove(WizardPageHelper.CANCELED);
      
      Agent owner = (Agent)session.get("wizardowner");
      request.put("wizardowner", owner);
      session.remove("wizardowner");

      Cell cell = createCellWrapper(page);

      CellFormBean cellBean = (CellFormBean) incomingModel;
      cellBean.setCell(cell);
      List nodeList = new ArrayList(getMatrixManager().getPageContents(page));
      cellBean.setNodes(nodeList);

      return cellBean;
   }

   public static Cell createCellWrapper(WizardPage page) {
      Cell cell = new Cell();
      cell.setWizardPage(page);
      if (page.getId() == null) {
         cell.setId(page.getNewId());
      }
      else {
         cell.setId(page.getId());
      }

      WizardPageDefinition pageDef = page.getPageDefinition();

      ScaffoldingCell cellDef = new ScaffoldingCell();
      cellDef.setWizardPageDefinition(pageDef);
      if (pageDef.getId() == null) {
         cellDef.setId(pageDef.getNewId());
      }
      else {
         cellDef.setId(pageDef.getId());
      }

      cell.setScaffoldingCell(cellDef);
      return cell;
   }

   public WizardManager getWizardManager() {
      return wizardManager;
   }

   public void setWizardManager(WizardManager wizardManager) {
      this.wizardManager = wizardManager;
   }

}

/**********************************************************************************
* $URL:https://source.sakaiproject.org/svn/osp/trunk/matrix/tool/src/java/org/theospi/portfolio/matrix/control/WizardPageDefinitionController.java $
* $Id:WizardPageDefinitionController.java 9134 2006-05-08 20:28:42Z chmaurer@iupui.edu $
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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.sakaiproject.metaobj.utils.mvc.intf.CancelableController;
import org.sakaiproject.tool.cover.ToolManager;
import org.springframework.validation.Errors;
import org.springframework.web.servlet.ModelAndView;
import org.theospi.portfolio.matrix.WizardPageHelper;
import org.theospi.portfolio.matrix.model.ScaffoldingCell;
import org.theospi.portfolio.matrix.model.WizardPageDefinition;
import org.theospi.portfolio.wizard.WizardFunctionConstants;
import org.theospi.portfolio.wizard.model.WizardPageSequence;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Jan 18, 2006
 * Time: 3:18:48 PM
 * To change this template use File | Settings | File Templates.
 */
public class WizardPageDefinitionController extends EditScaffoldingCellController implements CancelableController {

   private List pageList = null;
   
   /* (non-Javadoc)
    * @see org.theospi.utils.mvc.intf.FormController#referenceData(java.util.Map, java.lang.Object, org.springframework.validation.Errors)
    */
   public Map referenceData(Map request, Object command, Errors errors) {
      Map model = super.referenceData(request, command, errors);
      
      ScaffoldingCell sCell = (ScaffoldingCell) command;
      
      boolean wizardPublished = false;
      
      if(sCell.getWizardPageDefinition() != null && getWizardManager() != null)
         if(sCell.getWizardPageDefinition().getId() != null) {
            WizardPageSequence wps = getWizardManager().getWizardPageSeqByDef(sCell.getWizardPageDefinition().getId());
            if(wps.getCategory() != null)
               if(wps.getCategory().getWizard() != null)
                  wizardPublished = wps.getCategory().getWizard().isPublished();
         }
      
      model.put("wizardPublished", new Boolean(wizardPublished));
      model.put("helperPage", "true");
      model.put("pageTitleKey", "title_editWizardPage");
      model.put("pageInstructionsKey", "instructions_wizardPageSettings");
      return model;
   }

   public Object fillBackingObject(Object incomingModel, Map request, Map session, Map application) throws Exception {
      Object pages = session.get(WizardPageHelper.WIZARD_PAGE);
      
      WizardPageDefinition page = null;
      if(pages instanceof WizardPageDefinition) {
         pageList = new ArrayList();
         pageList.add(pages);
      }
      if(pages instanceof List) {
         pageList = (List)pages;
      }
      
      page = (WizardPageDefinition)pageList.get(0);
      
      session.remove(WizardPageHelper.CANCELED);
      page.setSiteId(ToolManager.getCurrentPlacement().getContext());
      ScaffoldingCell cell = new ScaffoldingCell();
      cell.setWizardPageDefinition(page);
      if (page.getId() == null) {
         cell.setId(page.getNewId());
      }
      else {
         cell.setId(page.getId());
      }
      EditedScaffoldingStorage sessionBean = new EditedScaffoldingStorage(cell);
      session.put(EditedScaffoldingStorage.EDITED_SCAFFOLDING_STORAGE_SESSION_KEY, sessionBean);
      checkForGuidance(session, cell);
      return cell;
   }

   public ModelAndView handleRequest(Object requestModel, Map request, Map session, Map application, Errors errors) {
      return super.handleRequest(requestModel, request, session, application, errors);    //To change body of overridden methods use File | Settings | File Templates.
   }

   protected boolean isPublished(ScaffoldingCell scaffoldingCell) {
      return false;
   }

   protected void saveScaffoldingCell(Map request, ScaffoldingCell scaffoldingCell) {
      // do nothing... let caller deal with it...
      scaffoldingCell.getWizardPageDefinition().setEvalWorkflows(
            new HashSet(super.createEvalWorkflows(scaffoldingCell.getWizardPageDefinition())));
   }

   protected void prepareModelWithScaffoldingId(Map model, ScaffoldingCell scaffoldingCell) {
      // do nothing... don't care about scaffolding id
   }

   public boolean isCancel(Map request) {
      Object cancel = request.get("canceling");
      if (cancel == null) {
         return false;
      }
      return cancel.equals("true");
   }

   public ModelAndView processCancel(Map request, Map session, Map application, Object command, Errors errors) throws Exception {
      return new ModelAndView("return", WizardPageHelper.CANCELED, "true");
   }
   
   protected String getGuidanceViewPermission() {
      return WizardFunctionConstants.VIEW_WIZARDPAGE_GUIDANCE;
   }
   
   protected String getGuidanceEditPermission() {
      return WizardFunctionConstants.EDIT_WIZARDPAGE_GUIDANCE;
   }
   
   protected String getGuidanceTitle() {
      return "Guidance for Wizard Page";
   }
   
   protected String getStyleReturnView() {
      return "page";
   }
}

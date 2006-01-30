
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.metaobj.shared.mgt.IdManager;
import org.sakaiproject.metaobj.shared.model.Id;
import org.sakaiproject.metaobj.utils.mvc.intf.FormController;
import org.sakaiproject.metaobj.utils.mvc.intf.LoadObjectController;
import org.sakaiproject.metaobj.worksite.mgt.WorksiteManager;
import org.sakaiproject.api.kernel.tool.ToolManager;
import org.springframework.validation.Errors;
import org.springframework.web.servlet.ModelAndView;
import org.theospi.portfolio.matrix.MatrixFunctionConstants;
import org.theospi.portfolio.matrix.MatrixManager;
import org.theospi.portfolio.matrix.model.Criterion;
import org.theospi.portfolio.matrix.model.Level;
import org.theospi.portfolio.matrix.model.Scaffolding;
import org.theospi.portfolio.matrix.model.ScaffoldingCell;
import org.theospi.portfolio.security.AuthorizationFacade;
import org.theospi.portfolio.workflow.mgt.WorkflowManager;

/**
 * @author chmaurer
 */
public class ViewScaffoldingController implements FormController, LoadObjectController {

   protected final Log logger = LogFactory.getLog(getClass());
   private MatrixManager matrixManager;
   private IdManager idManager;
   private WorksiteManager worksiteManager = null;
   private AuthorizationFacade authzManager;
   private ToolManager toolManager;
   private WorkflowManager workflowManager;

   /* (non-Javadoc)
    * @see org.theospi.utils.mvc.intf.LoadObjectController#fillBackingObject(java.lang.Object, java.util.Map, java.util.Map, java.util.Map)
    */
   public Object fillBackingObject(Object incomingModel, Map request,
         Map session, Map application) throws Exception {
      
      MatrixGridBean grid = (MatrixGridBean)incomingModel;
      
      Id sId = getIdManager().getId((String)request.get("scaffolding_id"));
      Scaffolding scaffolding = getMatrixManager().getScaffolding(sId);
      
      List levels = scaffolding.getLevels();
      List criteria = scaffolding.getCriteria();
      List matrixContents = new ArrayList();
      Criterion criterion = new Criterion();
      Level level = new Level();
      List row = new ArrayList();

      Set cells = scaffolding.getScaffoldingCells();
      boolean firstRow = true;
      boolean firstColumn = true;
       
      for (Iterator criteriaIterator = criteria.iterator(); criteriaIterator.hasNext();) {
         row = new ArrayList();
         criterion = (Criterion) criteriaIterator.next();
         for (Iterator levelsIterator = levels.iterator(); levelsIterator.hasNext();) {
            level = (Level) levelsIterator.next();
            ScaffoldingCell scaffoldingCell = getScaffoldingCell(cells, criterion, level);
            if (scaffoldingCell == null) {
               String status = MatrixFunctionConstants.READY_STATUS;
               if ((scaffolding.getWorkflowOption() == Scaffolding.HORIZONTAL_PROGRESSION && !firstColumn) ||
                     (scaffolding.getWorkflowOption() == Scaffolding.VERTICAL_PROGRESSION && !firstRow) ||
                     (scaffolding.getWorkflowOption() == Scaffolding.MANUAL_PROGRESSION)) {
                  status = MatrixFunctionConstants.LOCKED_STATUS;
               }
               
               scaffoldingCell = new ScaffoldingCell(criterion, level, status, scaffolding);
               scaffoldingCell.getWizardPageDefinition().setSiteId(scaffolding.getWorksiteId().getValue());
               getMatrixManager().storeScaffoldingCell(scaffoldingCell);
            }
            row.add(scaffoldingCell);
            firstColumn = false;
         }
         matrixContents.add(row);
         firstRow = false;
         //Need to reset firstColumn when moving to the next row
         firstColumn = true;
      }
      
      //grid.setMaintainer(getAuthzManager().isAuthorized(WorksiteManager.WORKSITE_MAINTAIN,
      //      getWorksiteManager().getCurrentWorksiteId()));

      grid.setScaffolding(scaffolding);
      grid.setColumnLabels(levels);
      grid.setRowLabels(criteria);
      grid.setMatrixContents(matrixContents);
      
      //processWorkflow(scaffolding);
      
      //Make sure these are not in session.
      session.remove(EditedScaffoldingStorage.STORED_SCAFFOLDING_FLAG);
      session.remove(EditedScaffoldingStorage.EDITED_SCAFFOLDING_STORAGE_SESSION_KEY);
      
      return incomingModel;
   }
   /*
   private void processWorkflow(Scaffolding scaffolding) {
      Set cells = scaffolding.getScaffoldingCells();      
      
      for (Iterator cellIterator = cells.iterator(); cellIterator.hasNext();) {
         ScaffoldingCell sCell = (ScaffoldingCell) cellIterator.next();
         Workflow oldWorkflow = null;
         if (sCell.getSubmitWorkflow() != null) {
            oldWorkflow = sCell.getSubmitWorkflow();
            //sCell.setSubmitWorkflowId(null);
            sCell.setSubmitWorkflow(null);
            //getMatrixManager().storeScaffoldingCell(sCell);
         }
         
         Workflow wf = new Workflow();
         wf.setTitle("New Workflow");
         wf.setNewObject(true);
         WorkflowItem wfi_current_status = new WorkflowItem();
         wfi_current_status.setActionType(WorkflowItem.STATUS_CHANGE_WORKFLOW);
         wfi_current_status.setActionObjectId(sCell.getId());
         wfi_current_status.setActionValue(MatrixFunctionConstants.PENDING_STATUS);
         wf.add(wfi_current_status);
         
         WorkflowItem wfi_current_lock = new WorkflowItem();
         wfi_current_lock.setActionType(WorkflowItem.CONTENT_LOCKING_WORKFLOW);
         wfi_current_lock.setActionObjectId(sCell.getId());
         wfi_current_lock.setActionValue(WorkflowItem.CONTENT_LOCKING_LOCK);
         wf.add(wfi_current_lock);
         
         //Only horizontal and vertical progressions have a "next" cell for a status change
         switch (scaffolding.getWorkflowOption()) {
            case Scaffolding.HORIZONTAL_PROGRESSION:
            case Scaffolding.VERTICAL_PROGRESSION:
               ScaffoldingCell nextCell = getMatrixManager().getNextScaffoldingCell(
                     sCell, scaffolding.getWorkflowOption());
               if (nextCell != null) {
                  WorkflowItem wfi_next = new WorkflowItem();
                  wfi_next.setActionType(WorkflowItem.STATUS_CHANGE_WORKFLOW);
                  wfi_next.setActionObjectId(nextCell.getId());
                  wfi_next.setActionValue(MatrixFunctionConstants.READY_STATUS);
                  wf.add(wfi_next);
                  
                  WorkflowItem wfi_next_lock = new WorkflowItem();
                  wfi_next_lock.setActionType(WorkflowItem.CONTENT_LOCKING_WORKFLOW);
                  wfi_next_lock.setActionObjectId(nextCell.getId());
                  wfi_next_lock.setActionValue(WorkflowItem.CONTENT_LOCKING_LOCK);
                  wf.add(wfi_next_lock);
               }
               break;               
         }
         sCell.setSubmitWorkflow(wf);
         getMatrixManager().storeScaffoldingCell(sCell);
         
         if (oldWorkflow != null) {
            //getMatrixManager().clearSession();
            //oldWorkflow.setItems(null);
            getWorkflowManager().deleteWorkflow(oldWorkflow);
         }
      }
   }
   */
   /* (non-Javadoc)
    * @see org.theospi.utils.mvc.intf.FormController#referenceData(java.util.Map, java.lang.Object, org.springframework.validation.Errors)
    */
   public Map referenceData(Map request, Object command, Errors errors) {
      Map model = new HashMap();
      String worksiteId = getWorksiteManager().getCurrentWorksiteId().getValue();
      model.put("worksite", getWorksiteManager().getSite(worksiteId));
      model.put("tool", getToolManager().getCurrentPlacement());
      
      return model;
   }
   
   /* (non-Javadoc)
    * @see org.theospi.utils.mvc.intf.Controller#handleRequest(java.lang.Object, java.util.Map, java.util.Map, java.util.Map, org.springframework.validation.Errors)
    */
   public ModelAndView handleRequest(Object requestModel, Map request,
         Map session, Map application, Errors errors) {
      Map model = new HashMap();
      model.put(EditedScaffoldingStorage.STORED_SCAFFOLDING_FLAG, "false");
      return new ModelAndView("success", model);
   }
   
   private ScaffoldingCell getScaffoldingCell(Set cells, Criterion criterion, Level level) {
      for (Iterator iter=cells.iterator(); iter.hasNext();) {
         ScaffoldingCell scaffoldingCell = (ScaffoldingCell) iter.next();
         if (scaffoldingCell.getRootCriterion().getId().getValue().equals(criterion.getId().getValue()) && 
               scaffoldingCell.getLevel().getId().getValue().equals(level.getId().getValue())) {
            return scaffoldingCell;
         }
      }
      return null;
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
    * @return Returns the worksiteManager.
    */
   public WorksiteManager getWorksiteManager() {
      return worksiteManager;
   }
   /**
    * @param worksiteManager The worksiteManager to set.
    */
   public void setWorksiteManager(WorksiteManager worksiteManager) {
      this.worksiteManager = worksiteManager;
   }
   /**
    * @return Returns the authzManager.
    */
   public AuthorizationFacade getAuthzManager() {
      return authzManager;
   }
   /**
    * @param authzManager The authzManager to set.
    */
   public void setAuthzManager(AuthorizationFacade authzManager) {
      this.authzManager = authzManager;
   }

   public ToolManager getToolManager() {
      return toolManager;
   }

   public void setToolManager(ToolManager toolManager) {
      this.toolManager = toolManager;
   }

   /**
    * @return Returns the workflowManager.
    */
   public WorkflowManager getWorkflowManager() {
      return workflowManager;
   }

   /**
    * @param workflowManager The workflowManager to set.
    */
   public void setWorkflowManager(WorkflowManager workflowManager) {
      this.workflowManager = workflowManager;
   }
}

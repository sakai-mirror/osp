
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

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.theospi.portfolio.matrix.MatrixFunctionConstants;
import org.theospi.portfolio.matrix.MatrixManager;
import org.theospi.portfolio.matrix.model.Criterion;
import org.theospi.portfolio.matrix.model.Level;
import org.theospi.portfolio.matrix.model.Scaffolding;
import org.theospi.portfolio.matrix.model.ScaffoldingCell;
import org.sakaiproject.service.legacy.content.LockManager;
import org.theospi.portfolio.security.AuthorizationFacade;
import org.sakaiproject.metaobj.shared.mgt.IdManager;
import org.sakaiproject.metaobj.shared.model.Id;

public class BaseScaffoldingController {
   
   private AuthorizationFacade authzManager;
   private MatrixManager matrixManager;
   private IdManager idManager;
   private LockManager lockManager = null;
   
   /* (non-Javadoc)
    * @see org.theospi.utils.mvc.intf.CustomCommandController#formBackingObject(java.util.Map, java.util.Map, java.util.Map)
    */
   public Object formBackingObject(Map request, Map session, Map application) {
      Scaffolding scaffolding;
      if (request.get(EditedScaffoldingStorage.STORED_SCAFFOLDING_FLAG) == null &&
            session.get(EditedScaffoldingStorage.STORED_SCAFFOLDING_FLAG) == null) {
         
         if (request.get("scaffolding_id") != null && !request.get("scaffolding_id").equals("")) {
            Id id = getIdManager().getId((String)request.get("scaffolding_id"));
            scaffolding = getMatrixManager().getScaffolding(id);
         }
         else {
            scaffolding = getMatrixManager().createDefaultScaffolding();
         }
            EditedScaffoldingStorage sessionBean = new EditedScaffoldingStorage(scaffolding);
            session.put(EditedScaffoldingStorage.EDITED_SCAFFOLDING_STORAGE_SESSION_KEY,
                  sessionBean);
         
      }
      else {
         EditedScaffoldingStorage sessionBean = (EditedScaffoldingStorage)session.get(
               EditedScaffoldingStorage.EDITED_SCAFFOLDING_STORAGE_SESSION_KEY);
         scaffolding = sessionBean.getScaffolding();
         session.remove(EditedScaffoldingStorage.STORED_SCAFFOLDING_FLAG);
      }
        //Traversing the collections to un-lazily load
      scaffolding.getLevels().size();
      scaffolding.getCriteria().size();
      traverseScaffoldingCells(scaffolding);
      
      return scaffolding;
   }
   
   protected void traverseScaffoldingCells(Scaffolding scaffolding) {
      scaffolding.getScaffoldingCells().size();
      for (Iterator iter=scaffolding.getScaffoldingCells().iterator(); iter.hasNext();) {
         ScaffoldingCell sCell = (ScaffoldingCell)iter.next();
         sCell.getCells().size();
      }
   }
   
   protected boolean isDirtyProgression(Scaffolding scaffolding) {
      int newProgression = scaffolding.getWorkflowOption();
      if (scaffolding.getId() == null)
         return true;
      
      Scaffolding origScaff = matrixManager.getScaffolding(scaffolding.getId());
      int origProgression = origScaff.getWorkflowOption();
      
      return (newProgression != origProgression);
   }

   protected void saveScaffolding(Scaffolding scaffolding) {
      boolean isDirty = isDirtyProgression(scaffolding);
      getMatrixManager().storeScaffolding(scaffolding);
      //regen the cells
      regenerateCells(scaffolding, isDirty);
   }
   
   protected void regenerateCells(Scaffolding scaffolding, boolean dirtyProgression) {
      List levels = scaffolding.getLevels();
      List criteria = scaffolding.getCriteria();
      Criterion criterion = new Criterion();
      Level level = new Level();
      Set cells = scaffolding.getScaffoldingCells();
      boolean firstRow = true;
      boolean firstColumn = true;
      
      for (Iterator criteriaIterator = criteria.iterator(); criteriaIterator.hasNext();) {
         criterion = (Criterion) criteriaIterator.next();
         for (Iterator levelsIterator = levels.iterator(); levelsIterator.hasNext();) {
            level = (Level) levelsIterator.next();
            ScaffoldingCell scaffoldingCell = getScaffoldingCell(cells, criterion, level);
            String status = MatrixFunctionConstants.READY_STATUS;
            if ((scaffolding.getWorkflowOption() == Scaffolding.HORIZONTAL_PROGRESSION && !firstColumn) ||
                  (scaffolding.getWorkflowOption() == Scaffolding.VERTICAL_PROGRESSION && !firstRow) ||
                  (scaffolding.getWorkflowOption() == Scaffolding.MANUAL_PROGRESSION)) {
               status = MatrixFunctionConstants.LOCKED_STATUS;
            }
            if (scaffoldingCell == null) {
               scaffoldingCell = new ScaffoldingCell(criterion, level, status, scaffolding);
               scaffoldingCell.getWizardPageDefinition().setSiteId(scaffolding.getWorksiteId().getValue());
               scaffoldingCell.getWizardPageDefinition().setToolId(scaffolding.getToolId().getValue());
               scaffoldingCell.getWizardPageDefinition().setTitle(getDefaultTitle(scaffolding, criterion, level));
               getMatrixManager().storeScaffoldingCell(scaffoldingCell);
            }
            else if (dirtyProgression){
               scaffoldingCell.setInitialStatus(status);
               getMatrixManager().storeScaffoldingCell(scaffoldingCell);
            }
            firstColumn = false;
         }
         firstRow = false;
         //Need to reset firstColumn when moving to the next row
         firstColumn = true;
      }
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
   
   protected String getDefaultTitle(Scaffolding scaffolding, Criterion criterion, Level level) {
      String title = scaffolding.getRowLabel() + ": " + criterion.getDescription() + "; " +
            scaffolding.getColumnLabel() + ": " + level.getDescription();
      
      return title;
   }

   public AuthorizationFacade getAuthzManager() {
      return authzManager;
   }

   public void setAuthzManager(AuthorizationFacade authzManager) {
      this.authzManager = authzManager;
   }

   public MatrixManager getMatrixManager() {
      return matrixManager;
   }

   public void setMatrixManager(MatrixManager matrixManager) {
      this.matrixManager = matrixManager;
   }

   public IdManager getIdManager() {
      return idManager;
   }

   public void setIdManager(IdManager idManager) {
      this.idManager = idManager;
   }

   public LockManager getLockManager() {
      return lockManager;
   }

   public void setLockManager(LockManager lockManager) {
      this.lockManager = lockManager;
   }
}

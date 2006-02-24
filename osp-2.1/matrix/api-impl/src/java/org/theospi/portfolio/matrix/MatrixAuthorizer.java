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
package org.theospi.portfolio.matrix;

import java.util.Iterator;
import java.util.List;

import org.sakaiproject.metaobj.shared.model.Agent;
import org.sakaiproject.metaobj.shared.model.Id;
import org.sakaiproject.service.legacy.content.ContentHostingService;
import org.theospi.portfolio.security.AuthorizationFacade;
import org.theospi.portfolio.security.app.ApplicationAuthorizer;
import org.theospi.portfolio.matrix.model.Cell;
import org.theospi.portfolio.matrix.model.Matrix;
import org.theospi.portfolio.matrix.model.Scaffolding;
import org.theospi.portfolio.matrix.model.ScaffoldingCell;



/**
 * @author rpembry
 *         <p/>
 *         <p/>
 *         createAuth(reviewer, "review", cellId) when a cell is submitted for review
 *         call listAuth(reviewer, "review", null) to find all the cells to review
 *         isAuth(review, "review", cellId) when a reviewer attempts to review a cell
 *         Node/Repository impl will callback here to see if there is locked content that prohibits edits or deletes.
 * @author rpembry
 */
public class MatrixAuthorizer implements ApplicationAuthorizer {
   
   private MatrixManager matrixManager;
   private AuthorizationFacade explicitAuthz;

   protected final org.apache.commons.logging.Log logger = org.apache.commons.logging.LogFactory
      .getLog(getClass());
   protected List functions;


   /* (non-Javadoc)
    * @see org.theospi.portfolio.security.app.ApplicationAuthorizer#isAuthorized(org.theospi.portfolio.security.AuthorizationFacade, org.theospi.portfolio.shared.model.Agent, java.lang.String, org.theospi.portfolio.shared.model.Id)
    */
   public Boolean isAuthorized(AuthorizationFacade facade, Agent agent,
                               String function, Id id) {
      logger.debug("isAuthorized?(...) invoked in MatrixAuthorizer");
         
      if (MatrixFunctionConstants.EVALUATE_MATRIX.equals(function) ||
            MatrixFunctionConstants.REVIEW_MATRIX.equals(function)) {
         return new Boolean(facade.isAuthorized(function,id));
      }
      else if (MatrixFunctionConstants.DELETE_SCAFFOLDING.equals(function)) {
         Scaffolding scaffolding = getMatrixManager().getScaffolding(id);
         if (!scaffolding.isPublished() && scaffolding.getOwner().equals(agent))
            return new Boolean(true);
      }
      else if (ContentHostingService.EVENT_RESOURCE_READ.equals(function)) {
         return isFileAuth(facade, agent, id);
      }
      else if (function.equals(MatrixFunctionConstants.CREATE_SCAFFOLDING)) {
         return new Boolean(facade.isAuthorized(agent,function,id));
      }
      else if (function.equals(MatrixFunctionConstants.VIEW_SCAFFOLDING_GUIDANCE)) {
         //If I can eval, review, or own it
         ScaffoldingCell sCell = getMatrixManager().getScaffoldingCellByWizardPageDef(id);
         //sCell.getWizardPageDefinition().get
         Boolean returned = null;
         for (Iterator iter=sCell.getCells().iterator(); iter.hasNext();) {
            Cell cell = (Cell)iter.next();
            returned = Boolean.valueOf(facade.isAuthorized(agent, MatrixFunctionConstants.EVALUATE_MATRIX, cell.getId()));
            if (returned == null || !returned.booleanValue()) {
               returned = Boolean.valueOf(facade.isAuthorized(agent, MatrixFunctionConstants.REVIEW_MATRIX, cell.getId()));
            }
            if (returned == null || !returned.booleanValue()) {
               Matrix matrix = cell.getMatrix();
               if (matrix != null) {
                  returned = Boolean.valueOf(matrix.getOwner().equals(agent));
               }
            }
            if (returned.booleanValue())
               return returned;
         }
         returned = Boolean.valueOf(sCell.getScaffolding().getOwner().equals(agent));
         if (returned.booleanValue())
            return returned;
      }
      else if (function.equals(MatrixFunctionConstants.EDIT_SCAFFOLDING_GUIDANCE)) {
         ScaffoldingCell sCell = getMatrixManager().getScaffoldingCellByWizardPageDef(id);
         Agent owner = null;
         if (sCell != null) {
            owner = sCell.getScaffolding().getOwner();
         }
         return new Boolean(agent.equals(owner));
      }
      
      return null;  //don't care
   }
   
   protected Boolean isCellAuthForEval(AuthorizationFacade facade, Agent agent, Id cellId) {
      return new Boolean(facade.isAuthorized(agent, MatrixFunctionConstants.EVALUATE_MATRIX, cellId));
   }

   public Boolean isFileAuth(AuthorizationFacade facade, Agent agent, Id artifactId) {
      // check if this id is attached to any cell
      if (artifactId == null)
         return new Boolean(true);

      List cells = getMatrixManager().getCellsByArtifact(artifactId);

      if (cells.size() == 0) {
         return null;
      }

      // does this user have access to any of the above cells
      for (Iterator i = cells.iterator(); i.hasNext();) {
         Cell cell = (Cell) i.next();
         Id toolId = cell.getMatrix().getScaffolding().getToolId();
         if (getExplicitAuthz().isAuthorized(agent, 
                  MatrixFunctionConstants.REVIEW_MATRIX, toolId) || 
               getExplicitAuthz().isAuthorized(agent, 
                  MatrixFunctionConstants.EVALUATE_MATRIX, toolId)) {
            return new Boolean(true);
         }

         Boolean returned = isCellAuthForEval(facade, agent, cell.getId());
         if (returned != null && returned.booleanValue()) {
            return returned;
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

   public List getFunctions() {
      return functions;
   }

   public void setFunctions(List functions) {
      this.functions = functions;
   }

   public AuthorizationFacade getExplicitAuthz() {
      return explicitAuthz;
   }

   public void setExplicitAuthz(AuthorizationFacade explicitAuthz) {
      this.explicitAuthz = explicitAuthz;
   }
}

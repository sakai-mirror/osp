/**********************************************************************************
* $URL:https://source.sakaiproject.org/svn/osp/trunk/matrix/api-impl/src/java/org/theospi/portfolio/matrix/MatrixAuthorizer.java $
* $Id:MatrixAuthorizer.java 9134 2006-05-08 20:28:42Z chmaurer@iupui.edu $
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

import org.sakaiproject.content.api.ContentHostingService;
import org.sakaiproject.metaobj.shared.mgt.IdManager;
import org.sakaiproject.metaobj.shared.model.Agent;
import org.sakaiproject.metaobj.shared.model.Id;
import org.theospi.portfolio.matrix.model.Cell;
import org.theospi.portfolio.matrix.model.Scaffolding;
import org.theospi.portfolio.matrix.model.ScaffoldingCell;
import org.theospi.portfolio.matrix.model.WizardPage;
import org.theospi.portfolio.security.AuthorizationFacade;
import org.theospi.portfolio.security.app.ApplicationAuthorizer;

import java.util.Iterator;
import java.util.List;



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
   private IdManager idManager;

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
            MatrixFunctionConstants.REVIEW_MATRIX.equals(function) ||
            MatrixFunctionConstants.USE_SCAFFOLDING.equals(function)) {
         return new Boolean(facade.isAuthorized(function,id));
      }
      else if (MatrixFunctionConstants.DELETE_SCAFFOLDING.equals(function)) {
         Scaffolding scaffolding = getMatrixManager().getScaffolding(id);
         if (scaffolding == null)
            return new Boolean(facade.isAuthorized(agent,function,id));
         
         if (!scaffolding.isPublished() && (scaffolding.getOwner().equals(agent)) || 
               facade.isAuthorized(agent,function,scaffolding.getWorksiteId()))
            return new Boolean(true);
      }
      else if (ContentHostingService.EVENT_RESOURCE_READ.equals(function)) {
         return isFileAuth(facade, agent, id);
      }
      else if (function.equals(MatrixFunctionConstants.CREATE_SCAFFOLDING)) {
         return new Boolean(facade.isAuthorized(agent,function,id));
      }
      else if (function.equals(MatrixFunctionConstants.EDIT_SCAFFOLDING)) {
         return new Boolean(facade.isAuthorized(agent,function,id));
      }
      else if (function.equals(MatrixFunctionConstants.EXPORT_SCAFFOLDING)) {
         return new Boolean(facade.isAuthorized(agent,function,id));
      }
      else if (function.equals(MatrixFunctionConstants.VIEW_SCAFFOLDING_GUIDANCE)) {
         //If I can eval, review, or own it
         ScaffoldingCell sCell = getMatrixManager().getScaffoldingCellByWizardPageDef(id);
         //sCell.getWizardPageDefinition().get
         
         if(sCell == null)
            throw new NullPointerException("The cell was not found.  Wizard Page Def for cell: " + id.getValue());
            
         Boolean returned = null;

         Id worksiteId = sCell.getScaffolding().getWorksiteId();

         // first check global perms for the site
         if (checkPerms(facade, new String[]{MatrixFunctionConstants.USE_SCAFFOLDING,
            MatrixFunctionConstants.EVALUATE_MATRIX, MatrixFunctionConstants.REVIEW_MATRIX}, worksiteId)) {
            return Boolean.valueOf(true);
         }

         for (Iterator iter=sCell.getCells().iterator(); iter.hasNext();) {
            Cell cell = (Cell)iter.next();
            if (checkPerms(facade, new String[]{MatrixFunctionConstants.EVALUATE_MATRIX,
               MatrixFunctionConstants.REVIEW_MATRIX}, cell.getId())) {
               return Boolean.valueOf(true);
            }
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
      else if (function.equals(MatrixFunctionConstants.EVALUATE_SPECIFIC_MATRIXCELL)) {
         WizardPage page = getMatrixManager().getWizardPage(id);
         Id siteId = idManager.getId(page.getPageDefinition().getSiteId());
//       make sure that the target site gets tested
         
         facade.pushAuthzGroups(siteId.getValue());
         return new Boolean(facade.isAuthorized(agent, MatrixFunctionConstants.EVALUATE_MATRIX, siteId));
      }
            
      return null;  //don't care
   }

   protected boolean checkPerms(AuthorizationFacade facade, String[] functions, Id qualifier) {
      for (int i=0;i<functions.length;i++) {
         if (facade.isAuthorized(functions[i], qualifier)) {
            return true;
         }
      }
      return false;
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
         Id siteId = cell.getMatrix().getScaffolding().getWorksiteId();
         if (getExplicitAuthz().isAuthorized(agent, 
                  MatrixFunctionConstants.REVIEW_MATRIX, siteId) || 
               getExplicitAuthz().isAuthorized(agent, 
                  MatrixFunctionConstants.EVALUATE_MATRIX, siteId)) {
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

   /**
    * @return the idManager
    */
   public IdManager getIdManager() {
      return idManager;
   }

   /**
    * @param idManager the idManager to set
    */
   public void setIdManager(IdManager idManager) {
      this.idManager = idManager;
   }
}

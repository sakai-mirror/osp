/*
 * The Open Source Portfolio Initiative Software is Licensed under the Educational Community License Version 1.0:
 *
 * This Educational Community License (the "License") applies to any original work of authorship
 * (the "Original Work") whose owner (the "Licensor") has placed the following notice immediately
 * following the copyright notice for the Original Work:
 *
 * Copyright (c) 2004 Trustees of Indiana University and r-smart Corporation
 *
 * This Original Work, including software, source code, documents, or other related items, is being
 * provided by the copyright holder(s) subject to the terms of the Educational Community License.
 * By obtaining, using and/or copying this Original Work, you agree that you have read, understand,
 * and will comply with the following terms and conditions of the Educational Community License:
 *
 * Permission to use, copy, modify, merge, publish, distribute, and sublicense this Original Work and
 * its documentation, with or without modification, for any purpose, and without fee or royalty to the
 * copyright holder(s) is hereby granted, provided that you include the following on ALL copies of the
 * Original Work or portions thereof, including modifications or derivatives, that you make:
 *
 * - The full text of the Educational Community License in a location viewable to users of the
 * redistributed or derivative work.
 *
 * - Any pre-existing intellectual property disclaimers, notices, or terms and conditions.
 *
 * - Notice of any changes or modifications to the Original Work, including the date the changes were made.
 *
 * - Any modifications of the Original Work must be distributed in such a manner as to avoid any confusion
 *  with the Original Work of the copyright holders.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
 * SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 * The name and trademarks of copyright holder(s) may NOT be used in advertising or publicity pertaining
 * to the Original or Derivative Works without specific, written prior permission. Title to copyright
 * in the Original Work and any associated documentation will at all times remain with the copyright holders.
 *
 * $Header: /opt/CVS/osp2.x/matrix/api-impl/src/java/org/theospi/portfolio/matrix/MatrixAuthorizer.java,v 1.4 2005/09/13 13:31:29 chmaurer Exp $
 * $Revision$
 * $Date$
 */
/*
 * Created on May 20, 2004
 *
 */
package org.theospi.portfolio.matrix;

import java.util.Iterator;
import java.util.List;

import org.sakaiproject.metaobj.shared.model.Agent;
import org.sakaiproject.metaobj.shared.model.Id;
import org.sakaiproject.metaobj.worksite.mgt.WorksiteManager;
import org.sakaiproject.service.legacy.content.ContentHostingService;
import org.theospi.portfolio.security.AuthorizationFacade;
import org.theospi.portfolio.security.app.ApplicationAuthorizer;
import org.theospi.portfolio.matrix.model.Cell;



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
         
      if (MatrixFunctionConstants.REVIEW_MATRIX.equals(function)) {
         return new Boolean(facade.isAuthorized(function,id));
      }
      else if (MatrixFunctionConstants.VIEW_MATRIX.equals(function)) {
         if (!agent.getId().equals(getMatrixManager().getMatrix(id).getOwner().getId())) return new Boolean(false);
      }
      else if (ContentHostingService.EVENT_RESOURCE_READ.equals(function)) {
         return isFileAuth(facade, agent, id);
      }
      else if (function.equals(MatrixFunctionConstants.CREATE_SCAFFOLDING)) {
         return new Boolean(facade.isAuthorized(agent,function,id));
      }
      
      return null;  //don't care
   }
   
   protected Boolean isCellAuthForReview(AuthorizationFacade facade, Agent agent, Id cellId) {
      return new Boolean(facade.isAuthorized(agent, MatrixFunctionConstants.REVIEW_MATRIX, cellId));
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
         Id toolId = cell.getMatrix().getMatrixTool().getId();
         if (getExplicitAuthz().isAuthorized(agent,
               MatrixFunctionConstants.VIEW_MATRIX_USERS, toolId)) {
            return new Boolean(true);
         }

         Boolean returned = isCellAuthForReview(facade, agent, cell.getId());
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

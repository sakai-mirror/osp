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
 * $Header: /opt/CVS/osp2.x/matrix/tool/src/java/org/theospi/portfolio/matrix/control/BaseScaffoldingCellController.java,v 1.2 2005/09/02 20:07:46 chmaurer Exp $
 * $Revision$
 * $Date$
 */


package org.theospi.portfolio.matrix.control;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.theospi.portfolio.matrix.MatrixFunctionConstants;
import org.theospi.portfolio.matrix.MatrixManager;
import org.theospi.portfolio.matrix.model.Cell;
import org.theospi.portfolio.matrix.model.ScaffoldingCell;
import org.theospi.portfolio.security.Authorization;
import org.theospi.portfolio.security.AuthorizationFacade;
import org.sakaiproject.metaobj.shared.mgt.IdManager;
import org.sakaiproject.metaobj.shared.model.Agent;
import org.sakaiproject.metaobj.shared.model.Id;

public class BaseScaffoldingCellController {
   
   private AuthorizationFacade authzManager;
   private MatrixManager matrixManager;
   private IdManager idManager;
   
   public Object fillBackingObject(Object incomingModel, Map request,
         Map session, Map application) throws Exception {
       ScaffoldingCell scaffoldingCell = (ScaffoldingCell) incomingModel;
      if (request.get(EditedScaffoldingStorage.STORED_SCAFFOLDING_FLAG) == null) {
         Id sCellId = ((ScaffoldingCell) incomingModel).getId();
         if (sCellId == null) {
            sCellId = getIdManager().getId((String)request.get("scaffoldingCell_id"));
         }
         
         scaffoldingCell = getMatrixManager().getScaffoldingCell(sCellId);
         
         EditedScaffoldingStorage sessionBean = new EditedScaffoldingStorage(scaffoldingCell);
         session.put(EditedScaffoldingStorage.EDITED_SCAFFOLDING_STORAGE_SESSION_KEY,
               sessionBean);
      }
      else {
         EditedScaffoldingStorage sessionBean = (EditedScaffoldingStorage)session.get(
               EditedScaffoldingStorage.EDITED_SCAFFOLDING_STORAGE_SESSION_KEY);
         scaffoldingCell = sessionBean.getScaffoldingCell();
      }
      //Traversing the collection to un-lazily load
      scaffoldingCell.getExpectations().size();
      getMatrixManager().removeFromSession(scaffoldingCell);
      scaffoldingCell.getScaffolding().isPublished();
      return scaffoldingCell;
   }
   
   protected void saveScaffoldingCell (Map request, ScaffoldingCell scaffoldingCell) {
      
      createAuths(scaffoldingCell);

      getMatrixManager().removeFromSession(scaffoldingCell);
      ScaffoldingCell oldScaffoldingCell = getMatrixManager().getScaffoldingCell(scaffoldingCell.getRootCriterion(), scaffoldingCell.getLevel());
      //String oldStatus = matrixManager.getScaffoldingCellsStatus(scaffoldingCell.getId());
      getMatrixManager().removeFromSession(oldScaffoldingCell);

      String oldStatus = oldScaffoldingCell.getInitialStatus();
      getMatrixManager().storeScaffoldingCell(scaffoldingCell);
      List cells = getMatrixManager().getCellsByScaffoldingCell(
            scaffoldingCell.getId());
      for (Iterator iter = cells.iterator(); iter.hasNext();) {
         Cell cell = (Cell)iter.next();
         if (!oldStatus.equals(scaffoldingCell.getInitialStatus()) && 
               (cell.getStatus().equals(MatrixFunctionConstants.LOCKED_STATUS) ||
               cell.getStatus().equals(MatrixFunctionConstants.READY_STATUS))) {
            cell.setStatus(scaffoldingCell.getInitialStatus());
            getMatrixManager().storeCell(cell);
         }
      }
   }
   
   protected void createAuths(ScaffoldingCell scaffoldingCell) {
      Collection viewers = new HashSet(scaffoldingCell.getReviewers());

      Collection oldViewerAuthzs = getAuthzManager().getAuthorizations(null,
      MatrixFunctionConstants.REVIEW_MATRIX, scaffoldingCell.getId());

      for (Iterator i = viewers.iterator(); i.hasNext();) {
         Agent reviewer = (Agent) i.next();
         
         Authorization newAuthz = new Authorization(reviewer,
               MatrixFunctionConstants.REVIEW_MATRIX, scaffoldingCell.getId());

         if (oldViewerAuthzs.contains(newAuthz)) {
            oldViewerAuthzs.remove(newAuthz);
         } else {
            getAuthzManager().createAuthorization(newAuthz.getAgent(),
                  newAuthz.getFunction(), newAuthz.getQualifier());
         }
      }

      // any leftovers must be removes...
      for (Iterator i = oldViewerAuthzs.iterator(); i.hasNext();) {
         Authorization authz = (Authorization) i.next();
         getAuthzManager().deleteAuthorization(authz.getAgent(),
               authz.getFunction(), authz.getQualifier());
      }
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

}

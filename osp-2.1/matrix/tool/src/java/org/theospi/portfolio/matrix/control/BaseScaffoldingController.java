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
 * $Header: /opt/CVS/osp2.x/matrix/tool/src/java/org/theospi/portfolio/matrix/control/BaseScaffoldingController.java,v 1.2 2005/09/07 17:44:19 chmaurer Exp $
 * $Revision$
 * $Date$
 */


package org.theospi.portfolio.matrix.control;

import java.util.Iterator;
import java.util.Map;

import org.sakaiproject.service.framework.portal.cover.PortalService;
import org.theospi.portfolio.matrix.MatrixManager;
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
      //return new Scaffolding();
      Scaffolding scaffolding;
      if (request.get(EditedScaffoldingStorage.STORED_SCAFFOLDING_FLAG) == null &&
            session.get(EditedScaffoldingStorage.STORED_SCAFFOLDING_FLAG) == null) {
         
         if (request.get("scaffolding_id") != null && !request.get("scaffolding_id").equals("")) {
            Id id = getIdManager().getId((String)request.get("scaffolding_id"));
            scaffolding = getMatrixManager().getScaffolding(id);
         }
         else {
            scaffolding = new Scaffolding();
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

   protected void saveMatrixTool(Scaffolding scaffolding) {
      getMatrixManager().storeScaffolding(scaffolding);
      
      //Lock file if it's been set

      if (scaffolding.getPrivacyXsdId() != null) {
         //getLockManager().addLock(scaffolding.getPrivacyXsdId(), scaffolding.getId(), "Locking permission statement definition file for scaffolding");
         getLockManager().lockObject(scaffolding.getPrivacyXsdId().getValue(), 
               scaffolding.getId().getValue(), 
               "Locking permission statement definition file for scaffolding", true);
      }
      else { //unlock
         getLockManager().removeAllLocks(scaffolding.getId().getValue());
      }
      
      String toolId = PortalService.getCurrentToolId();
      if (getMatrixManager().getMatrixTool(getIdManager().getId(toolId)) == null) {
         getMatrixManager().createMatrixTool(toolId, scaffolding);
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

   public LockManager getLockManager() {
      return lockManager;
   }

   public void setLockManager(LockManager lockManager) {
      this.lockManager = lockManager;
   }

}

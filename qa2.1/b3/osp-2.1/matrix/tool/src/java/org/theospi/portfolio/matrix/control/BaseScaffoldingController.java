
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
import java.util.Map;

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

   protected void saveScaffolding(Scaffolding scaffolding) {
      getMatrixManager().storeScaffolding(scaffolding);
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

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
package org.theospi.portfolio.matrix.model.impl;

import org.sakaiproject.metaobj.shared.mgt.ReferenceParser;
import org.theospi.portfolio.matrix.MatrixManager;
import org.theospi.portfolio.security.mgt.OspHttpAccessBase;
import org.sakaiproject.entity.api.EntityAccessOverloadException;
import org.sakaiproject.entity.api.EntityCopyrightException;
import org.sakaiproject.entity.api.EntityNotDefinedException;
import org.sakaiproject.entity.api.EntityPermissionException;
import org.sakaiproject.entity.api.Reference;
import org.sakaiproject.metaobj.shared.mgt.IdManager;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Nov 8, 2005
 * Time: 5:30:11 PM
 * To change this template use File | Settings | File Templates.
 */
public class MatrixHttpAccess extends OspHttpAccessBase {

   private IdManager idManager;
   private MatrixManager matrixManager;

   protected void checkSource(Reference ref, ReferenceParser parser)
         throws EntityPermissionException, EntityNotDefinedException, EntityAccessOverloadException, EntityCopyrightException {

      getMatrixManager().checkPageAccess(parser.getId());
   }

   public IdManager getIdManager() {
      return idManager;
   }

   public void setIdManager(IdManager idManager) {
      this.idManager = idManager;
   }

   public MatrixManager getMatrixManager() {
      return matrixManager;
   }

   public void setMatrixManager(MatrixManager matrixManager) {
      this.matrixManager = matrixManager;
   }

}

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

import org.theospi.portfolio.shared.mgt.OspHttpAccess;
import org.theospi.portfolio.shared.mgt.ReferenceParser;
import org.theospi.portfolio.matrix.MatrixManager;
import org.theospi.portfolio.matrix.MatrixFunctionConstants;
import org.theospi.portfolio.matrix.model.Cell;
import org.theospi.portfolio.security.AuthorizationFacade;
import org.sakaiproject.service.legacy.entity.Reference;
import org.sakaiproject.service.legacy.content.ContentHostingService;
import org.sakaiproject.exception.PermissionException;
import org.sakaiproject.exception.IdUnusedException;
import org.sakaiproject.exception.ServerOverloadException;
import org.sakaiproject.exception.CopyrightException;
import org.sakaiproject.metaobj.shared.mgt.IdManager;
import org.sakaiproject.metaobj.shared.model.Id;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Nov 8, 2005
 * Time: 5:30:11 PM
 * To change this template use File | Settings | File Templates.
 */
public class MatrixHttpAccess extends OspHttpAccess {

   private IdManager idManager;
   private MatrixManager matrixManager;

   protected void checkSource(Reference ref, ReferenceParser parser)
         throws PermissionException, IdUnusedException, ServerOverloadException, CopyrightException {

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

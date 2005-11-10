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

      getMatrixManager().checkCellAccess(parser.getId());
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

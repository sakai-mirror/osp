package org.theospi.portfolio.guidance.impl;

import org.theospi.portfolio.shared.mgt.OspHttpAccess;
import org.theospi.portfolio.shared.mgt.ReferenceParser;
import org.theospi.portfolio.guidance.mgt.GuidanceManager;
import org.sakaiproject.service.legacy.entity.Reference;
import org.sakaiproject.exception.PermissionException;
import org.sakaiproject.exception.IdUnusedException;
import org.sakaiproject.exception.ServerOverloadException;
import org.sakaiproject.exception.CopyrightException;
import org.sakaiproject.metaobj.shared.mgt.IdManager;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Nov 14, 2005
 * Time: 6:05:46 PM
 * To change this template use File | Settings | File Templates.
 */
public class GuidanceHttpAccess extends OspHttpAccess {

   private IdManager idManager;
   private GuidanceManager guidanceManager;
   
   protected void checkSource(Reference ref, ReferenceParser parser)
      throws PermissionException, IdUnusedException, ServerOverloadException, CopyrightException {
      // should setup access rights, etc.
      getGuidanceManager().getGuidance(getIdManager().getId(parser.getId()));
   }

   public IdManager getIdManager() {
      return idManager;
   }

   public void setIdManager(IdManager idManager) {
      this.idManager = idManager;
   }

   public GuidanceManager getGuidanceManager() {
      return guidanceManager;
   }

   public void setGuidanceManager(GuidanceManager guidanceManager) {
      this.guidanceManager = guidanceManager;
   }
}

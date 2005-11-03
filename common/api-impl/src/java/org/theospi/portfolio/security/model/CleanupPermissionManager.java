package org.theospi.portfolio.security.model;

import org.theospi.portfolio.security.AuthorizationFacade;
import org.theospi.portfolio.worksite.model.SiteTool;
import org.sakaiproject.metaobj.shared.mgt.IdManager;
import org.sakaiproject.metaobj.shared.model.Id;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Aug 16, 2005
 * Time: 11:29:33 AM
 * To change this template use File | Settings | File Templates.
 */
public class CleanupPermissionManager extends SimpleToolPermissionManager {

   private CleanupableService service;
   private AuthorizationFacade authzManager;
   private IdManager idManager;

   public void toolRemoved(SiteTool siteTool) {
      Id toolId = getIdManager().getId(siteTool.getToolId());
      getService().cleanupTool(toolId);
      getAuthzManager().deleteAuthorizations(toolId);
   }

   public IdManager getIdManager() {
      return idManager;
   }

   public void setIdManager(IdManager idManager) {
      this.idManager = idManager;
   }

   public AuthorizationFacade getAuthzManager() {
      return authzManager;
   }

   public void setAuthzManager(AuthorizationFacade authzManager) {
      this.authzManager = authzManager;
   }

   public CleanupableService getService() {
      return service;
   }

   public void setService(CleanupableService service) {
      this.service = service;
   }

}

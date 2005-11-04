package org.theospi.portfolio.security.impl.sakai;

import org.sakaiproject.component.legacy.security.SakaiSecurity;
import org.sakaiproject.service.legacy.user.User;
import org.sakaiproject.service.legacy.content.ContentHostingService;
import org.sakaiproject.service.legacy.entity.Reference;
import org.sakaiproject.service.legacy.entity.EntityManager;
import org.sakaiproject.metaobj.shared.mgt.AgentManager;
import org.sakaiproject.metaobj.shared.mgt.IdManager;
import org.sakaiproject.metaobj.shared.model.Agent;
import org.sakaiproject.metaobj.shared.model.Id;
import org.theospi.portfolio.security.AuthorizationFacade;

import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Jul 28, 2005
 * Time: 2:36:50 PM
 * To change this template use File | Settings | File Templates.
 */
public class AuthzSecurityService extends SakaiSecurity {

   private AuthorizationFacade authzManager;
   private Map authzMappings;
   private AgentManager agentManager;
   private IdManager idManager;
   private ContentHostingService contentHosting;
   private EntityManager entityManager;

   public void init() {
      super.init();
   }

   public void destroy() {
      super.destroy();
   }

   public boolean unlock(User u, String lock, String resource) {
      boolean returned = super.unlock(u, lock, resource);
      if (returned) {
         return returned;
      }

      if (!getAuthzMappings().containsKey(lock)) {
         return returned;
      }

      String mappedFunction = (String)getAuthzMappings().get(lock);
      Reference ref = getEntityManager().newReference(resource);
      Id resourceId = getIdManager().getId(getContentHosting().getUuid(ref.getId()));

      if (u == null || u.getDisplayName() == null || u.getDisplayName().length() == 0) {
         return getAuthzManager().isAuthorized(mappedFunction, resourceId);
      }
      else {
         Agent agent = getAgentManager().getAgent(u.getDisplayName());
         return getAuthzManager().isAuthorized(agent, mappedFunction, resourceId);
      }

   } // unlock

   public AuthorizationFacade getAuthzManager() {
      return authzManager;
   }

   public void setAuthzManager(AuthorizationFacade authzManager) {
      this.authzManager = authzManager;
   }

   public Map getAuthzMappings() {
      return authzMappings;
   }

   public void setAuthzMappings(Map authzMappings) {
      this.authzMappings = authzMappings;
   }

   public AgentManager getAgentManager() {
      return agentManager;
   }

   public void setAgentManager(AgentManager agentManager) {
      this.agentManager = agentManager;
   }

   public IdManager getIdManager() {
      return idManager;
   }

   public void setIdManager(IdManager idManager) {
      this.idManager = idManager;
   }

   public ContentHostingService getContentHosting() {
      return contentHosting;
   }

   public void setContentHosting(ContentHostingService contentHosting) {
      this.contentHosting = contentHosting;
   }

   public EntityManager getEntityManager() {
      return entityManager;
   }

   public void setEntityManager(EntityManager entityManager) {
      this.entityManager = entityManager;
   }
}

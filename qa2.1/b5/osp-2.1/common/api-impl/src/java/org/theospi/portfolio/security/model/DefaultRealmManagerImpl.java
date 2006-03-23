package org.theospi.portfolio.security.model;

import org.sakaiproject.api.kernel.session.cover.SessionManager;
import org.sakaiproject.exception.IdInvalidException;
import org.sakaiproject.exception.IdUnusedException;
import org.sakaiproject.exception.IdUsedException;
import org.sakaiproject.exception.PermissionException;
import org.sakaiproject.service.legacy.authzGroup.AuthzGroup;
import org.sakaiproject.service.legacy.authzGroup.AuthzGroupService;
import org.sakaiproject.service.legacy.authzGroup.Role;
import org.theospi.portfolio.security.DefaultRealmManager;

import java.util.Iterator;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Feb 27, 2006
 * Time: 2:47:41 PM
 * To change this template use File | Settings | File Templates.
 */
public class DefaultRealmManagerImpl implements DefaultRealmManager {

   private AuthzGroupService authzGroupService;
   private String newRealmName;
   private List roles;
   private boolean newlyCreated;

   public void init() {

      org.sakaiproject.api.kernel.session.Session sakaiSession = SessionManager.getCurrentSession();
      String userId = sakaiSession.getUserId();
      try {
         sakaiSession.setUserId("admin");
         sakaiSession.setUserEid("admin");
         try {
            AuthzGroup group = getAuthzGroupService().getAuthzGroup(newRealmName);
            if (group != null) {
               newlyCreated = false;
               return;
            }
         } catch (IdUnusedException e) {
            // no worries... must not be created yet.
         }

         newlyCreated = true;

         try {
            AuthzGroup newRealm = getAuthzGroupService().addAuthzGroup(newRealmName);
            addRoles(newRealm);
            getAuthzGroupService().save(newRealm);
         } catch (IdUnusedException e) {
            throw new RuntimeException(e);
         } catch (PermissionException e) {
            throw new RuntimeException(e);
         } catch (IdUsedException e) {
            throw new RuntimeException(e);
         } catch (IdInvalidException e) {
            throw new RuntimeException(e);
         }
      } finally {
         sakaiSession.setUserId(userId);
         sakaiSession.setUserEid(userId);
      }

   }

   protected void addRoles(AuthzGroup newRealm) throws IdUsedException {
      for (Iterator i=getRoles().iterator();i.hasNext();) {
         Object roleInfo = i.next();
         if (roleInfo instanceof String) {
            newRealm.addRole((String) roleInfo);
         }
         else {
            RealmRole role = (RealmRole) roleInfo;
            Role newRole = newRealm.addRole(role.getRole());
            if (role.isMaintain()) {
               newRealm.setMaintainRole(newRole.getId());
            }
         }
      }
   }

   public AuthzGroupService getAuthzGroupService() {
      return authzGroupService;
   }

   public void setAuthzGroupService(AuthzGroupService authzGroupService) {
      this.authzGroupService = authzGroupService;
   }

   public String getNewRealmName() {
      return newRealmName;
   }

   public void setNewRealmName(String newRealmName) {
      this.newRealmName = newRealmName;
   }

   public List getRoles() {
      return roles;
   }

   public void setRoles(List roles) {
      this.roles = roles;
   }

   public boolean isNewlyCreated() {
      return newlyCreated;
   }

   public void setNewlyCreated(boolean newlyCreated) {
      this.newlyCreated = newlyCreated;
   }

}


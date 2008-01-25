/**********************************************************************************
* $URL:https://source.sakaiproject.org/svn/osp/trunk/common/api-impl/src/java/org/theospi/portfolio/security/mgt/impl/PermissionManagerImpl.java $
* $Id:PermissionManagerImpl.java 9134 2006-05-08 20:28:42Z chmaurer@iupui.edu $
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
package org.theospi.portfolio.security.mgt.impl;

import java.util.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.authz.api.GroupNotDefinedException;
import org.sakaiproject.authz.api.Role;
import org.sakaiproject.authz.api.AuthzGroup;
import org.sakaiproject.authz.api.AuthzPermissionException;
import org.sakaiproject.authz.cover.AuthzGroupService;
import org.sakaiproject.metaobj.shared.mgt.AgentManager;
import org.sakaiproject.metaobj.shared.model.Agent;
import org.sakaiproject.metaobj.shared.model.Id;
import org.sakaiproject.metaobj.shared.model.OspRole;
import org.sakaiproject.site.api.Site;
import org.sakaiproject.entity.api.Reference;
import org.sakaiproject.entity.cover.EntityManager;
import org.theospi.portfolio.security.Authorization;
import org.theospi.portfolio.security.AuthorizationFacade;
import org.theospi.portfolio.security.mgt.PermissionManager;
import org.theospi.portfolio.security.mgt.ToolPermissionManager;
import org.theospi.portfolio.security.model.Permission;
import org.theospi.portfolio.security.model.PermissionsEdit;
import org.theospi.portfolio.shared.model.OspException;

public class PermissionManagerImpl implements PermissionManager {
   protected final transient Log logger = LogFactory.getLog(getClass());

   private AgentManager agentManager;
   private AuthorizationFacade authzManager;

   private Map tools;

   public List getWorksiteRoles(PermissionsEdit edit) {
      AuthzGroup authzGroup = getAuthzGroup(edit);
      Set roles = authzGroup.getRoles();
      List returned = new ArrayList();
      returned.addAll(roles);
      return returned;
   }

   public PermissionsEdit fillPermissions(PermissionsEdit edit) {
      edit.setPermissions(new ArrayList());

      edit = fillPermissionsInternal(edit, edit.getQualifier(), false);

      ToolPermissionManager mgr = getToolManager(edit);
      List quals = mgr.getReadOnlyQualifiers(edit);

      for (Iterator i=quals.iterator();i.hasNext();) {
         Id qualifier = (Id)i.next();
         fillPermissionsInternal(edit, qualifier, true);
      }

      return edit;
   }

   protected PermissionsEdit fillPermissionsInternal(
      PermissionsEdit edit, Id qualifier, boolean readOnly) {

      /*Realm siteRealm = RealmService.getRealm("/site/" +
         edit.getSiteId());

      Set roles = siteRealm.getRoles();
      */
      AuthzGroup authzGroup = getAuthzGroup(edit);
      
      if (authzGroup == null) {
         return edit;
      }
      
      Set roles = authzGroup.getRoles();

      Reference ref = EntityManager.newReference(authzGroup.getId());
      Collection realms = ref.getAuthzGroups();

      List functions = getAppFunctions(edit);

      for (Iterator i=roles.iterator();i.hasNext();) {
         Role role = (Role)i.next();
         Agent currentRole = getAgentManager().getWorksiteRole(role.getId(), edit.getSiteId());
         
         Set abilities = AuthzGroupService.getAllowedFunctions(role.getId(), realms);
         
         for (Iterator j=functions.iterator();j.hasNext();) {
            String func = (String) j.next();

            if (abilities.contains(func)) {
               edit.getPermissions().add(
                  new Permission(currentRole, func, readOnly));
            }
         }
      }

      return edit;
   }

   protected AuthzGroup getAuthzGroup(PermissionsEdit edit) {
      try {
         return AuthzGroupService.getInstance().getAuthzGroup("/site/" +
            edit.getSiteId());
      } catch (GroupNotDefinedException e) {
         //This should be an okay exception to swallow.  If we can't find the realm, just skip it.
         // This came up when using the sites tool to create a site.  Since there wasn't 
         //   a realm yet, couldn't set permissions
         logger.warn("Cannot find realm corresponding to site: " + e.getId() + ".  Skipping it for setting permissions.", e);
         //throw new OspException(e);
      }
      return null;
   }

   public void updatePermissions(PermissionsEdit edit) {
      AuthorizationFacade manager = getAuthzManager();
      List origPermissions = null;

      PermissionsEdit orig = (PermissionsEdit)edit.clone();
      orig = fillPermissions(orig);
      origPermissions = orig.getPermissions();
      AuthzGroup currentGroup = getAuthzGroup(edit);

      for (Iterator i=edit.getPermissions().iterator();i.hasNext();) {
         Permission perm = (Permission)i.next();

         if (origPermissions.contains(perm)) {
            origPermissions.remove(perm);
         }
         else if (!perm.isReadOnly()) {
            Role currentRole = getRole(currentGroup, perm);
            currentRole.allowFunction(perm.getFunction());
         }
      }

      for (Iterator i=origPermissions.iterator();i.hasNext();) {
         Permission perm = (Permission)i.next();
         Role currentRole = getRole(currentGroup, perm);
         currentRole.disallowFunction(perm.getFunction());
      }

      try {
         AuthzGroupService.save(currentGroup);
      } catch (GroupNotDefinedException e) {
         throw new OspException(e);
      } catch (AuthzPermissionException e) {
         throw new OspException(e);
      }

   }

   protected Role getRole(AuthzGroup currentGroup, Permission perm) {
      if (perm.getAgent() instanceof OspRole) {
         return currentGroup.getRole(((OspRole)perm.getAgent()).getRoleName());
      }
      return null;
   }

   public void duplicatePermissions(Id srcQualifier, Id targetQualifier, Site newSite) {
      AuthorizationFacade manager = getAuthzManager();
      List origPermissions = manager.getAuthorizations(null, null, srcQualifier);

      for (Iterator i=origPermissions.iterator();i.hasNext();) {
         Authorization authz = (Authorization)i.next();
         Agent agent = authz.getAgent();
         if (newSite != null && agent instanceof OspRole) {
            agent = getAgentManager().getTempWorksiteRole(
               ((OspRole)agent).getRoleName(), newSite.getId());
         }

         if (agent != null) {
            manager.createAuthorization(agent, authz.getFunction(), targetQualifier);
         }
      }
   }

   public void addTools(Map newTools) {
      getTools().putAll(newTools);
   }

   public List getAppFunctions(PermissionsEdit edit) {
      ToolPermissionManager mgr = getToolManager(edit);

      return mgr.getFunctions(edit);
   }

   protected ToolPermissionManager getToolManager(PermissionsEdit edit) {
      return (ToolPermissionManager)getTools().get(edit.getName());
   }

   public AgentManager getAgentManager() {
      return agentManager;
   }

   public void setAgentManager(AgentManager agentManager) {
      this.agentManager = agentManager;
   }

   public AuthorizationFacade getAuthzManager() {
      return authzManager;
   }

   public void setAuthzManager(AuthorizationFacade authzManager) {
      this.authzManager = authzManager;
   }

   public Map getTools() {
      return tools;
   }

   public void setTools(Map tools) {
      this.tools = tools;
   }
}

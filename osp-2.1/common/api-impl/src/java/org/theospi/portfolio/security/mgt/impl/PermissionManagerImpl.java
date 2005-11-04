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
 * $Header: /opt/CVS/osp2.x/common/api-impl/src/java/org/theospi/portfolio/security/mgt/impl/PermissionManagerImpl.java,v 1.3 2005/10/26 16:56:26 andersjb Exp $
 * $Revision$
 * $Date$
 */
package org.theospi.portfolio.security.mgt.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.theospi.portfolio.security.mgt.PermissionManager;
import org.theospi.portfolio.security.mgt.ToolPermissionManager;
import org.theospi.portfolio.security.model.PermissionsEdit;
import org.theospi.portfolio.security.model.Permission;
import org.theospi.portfolio.security.Authorization;
import org.theospi.portfolio.security.AuthorizationFacade;
import org.theospi.portfolio.shared.model.OspException;
//import org.sakaiproject.service.legacy.realm.Realm;
import org.sakaiproject.service.legacy.authzGroup.Role;
//import org.sakaiproject.service.legacy.realm.cover.RealmService;
import org.sakaiproject.service.legacy.site.Site;
import org.sakaiproject.exception.IdUnusedException;
import org.sakaiproject.metaobj.shared.mgt.AgentManager;
import org.sakaiproject.metaobj.shared.model.Id;
import org.sakaiproject.metaobj.shared.model.Agent;
import org.sakaiproject.metaobj.shared.model.OspRole;
import org.sakaiproject.service.legacy.authzGroup.cover.AuthzGroupService;

import java.util.*;

public class PermissionManagerImpl implements PermissionManager {
   protected final transient Log logger = LogFactory.getLog(getClass());

   private AgentManager agentManager;
   private AuthorizationFacade authzManager;

   private Map tools;

   public List getWorksiteRoles(PermissionsEdit edit) {
      try {

    	 Set roles = AuthzGroupService.getInstance().getAuthzGroup("/site/" +
    	            edit.getSiteId()).getRoles();
         List returned = new ArrayList();
         returned.addAll(roles);
         return returned;
      } catch (IdUnusedException e) {
         logger.error("", e);
         throw new OspException(e);
      }
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

      try {
         /*Realm siteRealm = RealmService.getRealm("/site/" +
            edit.getSiteId());

         Set roles = siteRealm.getRoles();
         */
    	 Set roles = AuthzGroupService.getInstance().getAuthzGroup("/site/" +
    	            edit.getSiteId()).getRoles();

         for (Iterator i=roles.iterator();i.hasNext();) {
            Role role = (Role)i.next();
            Agent currentRole = getAgentManager().getWorksiteRole(role.getId(), edit.getSiteId());
            List authzs = getAuthzManager().getAuthorizations(currentRole, null, qualifier);

            for (Iterator j=authzs.iterator();j.hasNext();) {
               Authorization authz = (Authorization)j.next();
               edit.getPermissions().add(
                  new Permission(currentRole, authz.getFunction(), readOnly));
            }
         }
      } catch (IdUnusedException e) {
         logger.error("", e);
         throw new OspException(e);
      }

      return edit;
   }

   public void updatePermissions(PermissionsEdit edit) {
      AuthorizationFacade manager = getAuthzManager();
      List origPermissions = null;

      PermissionsEdit orig = (PermissionsEdit)edit.clone();
      orig = fillPermissions(orig);
      origPermissions = orig.getPermissions();

      for (Iterator i=edit.getPermissions().iterator();i.hasNext();) {
         Permission perm = (Permission)i.next();

         if (origPermissions.contains(perm)) {
            origPermissions.remove(perm);
         }
         else if (!perm.isReadOnly()) {
            manager.createAuthorization(perm.getAgent(), perm.getFunction(), edit.getQualifier());
         }
      }

      for (Iterator i=origPermissions.iterator();i.hasNext();) {
         Permission perm = (Permission)i.next();

         manager.deleteAuthorization(perm.getAgent(), perm.getFunction(), edit.getQualifier());
      }

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

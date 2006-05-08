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
 * $Header: /root/osp/src/portfolio/org/theospi/portfolio/admin/service/SakaiRoleCreationIntegrationPlugin.java,v 1.5 2005/01/13 21:57:08 chmaurer Exp $
 * $Revision: 1.5 $
 * $Date$
 */
package org.theospi.portfolio.admin.service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.theospi.portfolio.admin.model.IntegrationOption;
import org.theospi.portfolio.shared.model.OspException;
import org.sakaiproject.site.cover.SiteService;
import org.sakaiproject.site.api.Site;
import org.sakaiproject.authz.api.AuthzGroup;
import org.sakaiproject.authz.api.AuthzPermissionException;
import org.sakaiproject.authz.api.GroupNotDefinedException;
import org.sakaiproject.authz.api.Role;
import org.sakaiproject.authz.api.RoleAlreadyDefinedException;
import org.sakaiproject.authz.cover.AuthzGroupService;
import org.sakaiproject.metaobj.worksite.mgt.WorksiteManager;

import java.util.List;
import java.util.Iterator;
import java.util.HashSet;

public class SakaiRoleCreationIntegrationPlugin extends IntegrationPluginBase {
   protected final transient Log logger = LogFactory.getLog(getClass());

   private WorksiteManager worksiteManager;

   protected boolean currentlyIncluded(IntegrationOption option) {
      RoleIntegrationOption roleOption = (RoleIntegrationOption)option;

      if (roleOption instanceof ExistingWorksitesRoleIntegrationOption) {
         return existingWorksitesHasRole(
            (ExistingWorksitesRoleIntegrationOption)roleOption);
      }

      AuthzGroup realm = null;
      try {
         realm = AuthzGroupService.getAuthzGroup(roleOption.getRealm());
      } catch (GroupNotDefinedException e) {
         logger.error("", e);
         throw new OspException(e);
      }

      Role role = realm.getRole(roleOption.getRoleId());
      return (role != null);
   }

   protected boolean existingWorksitesHasRole(ExistingWorksitesRoleIntegrationOption roleOption) {
      List sites = SiteService.getSites(org.sakaiproject.site.api.SiteService.SelectionType.ANY,
            null, null, null, org.sakaiproject.site.api.SiteService.SortType.NONE, null);

      for (Iterator i=sites.iterator();i.hasNext();) {
         Site site = (Site)i.next();
         if (site.isType(roleOption.getWorksiteType())) {
            if (!checkSite(site, roleOption)) {
               return false;
            }
         }
      }

      return true;
   }

   protected boolean checkSite(Site site, ExistingWorksitesRoleIntegrationOption roleOption) {
      AuthzGroup siteRealm = getWorksiteManager().getSiteRealm(site.getId());

      return (siteRealm.getRole(roleOption.getRoleId()) != null);
   }

   public IntegrationOption updateOption(IntegrationOption option) {
      RoleIntegrationOption roleOption = (RoleIntegrationOption)option;

      if (option.isInclude() && !currentlyIncluded(roleOption)) {
         addRole(roleOption);
      }
      else if (currentlyIncluded(roleOption)) {
         removeRole(roleOption);
      }

      return option;
   }

   public boolean executeOption(IntegrationOption option) {
      updateOption(option);
      return true;
   }

   protected void addRole(RoleIntegrationOption roleOption) {
      if (roleOption instanceof ExistingWorksitesRoleIntegrationOption) {
         addRoleToAllWorksites((ExistingWorksitesRoleIntegrationOption)roleOption);
         return;
      }

      AuthzGroup realm = null;
      try {
         realm = AuthzGroupService.getAuthzGroup(roleOption.getRealm());
      } catch (GroupNotDefinedException e) {
         logger.error("", e);
         throw new OspException(e);
      }

      addRole(realm, roleOption);
   }

   protected void addRoleToAllWorksites(ExistingWorksitesRoleIntegrationOption roleOption) {
      List sites = SiteService.getSites(org.sakaiproject.site.api.SiteService.SelectionType.ANY,
            null, null, null, org.sakaiproject.site.api.SiteService.SortType.NONE, null);

      for (Iterator i=sites.iterator();i.hasNext();) {
         Site site = (Site)i.next();
         if (site.isType(roleOption.getWorksiteType())) {
            AuthzGroup siteRealm = getWorksiteManager().getSiteRealm(site.getId());
            addRole(siteRealm, roleOption);
         }
      }
   }

   protected void addRole(AuthzGroup realm, RoleIntegrationOption roleOption) {
      AuthzGroup edit = null;
      Role copy = realm.getRole(roleOption.getCopyOf());

      try {
         edit = AuthzGroupService.getAuthzGroup(realm.getId());
         Role newRole = edit.addRole(roleOption.getRoleId(), copy);

         if (roleOption.getPermissionsOn() != null) {
            newRole.allowFunctions(new HashSet(roleOption.getPermissionsOn()));
         }

         if (roleOption.getPermissionsOff() != null) {
            newRole.disallowFunctions(new HashSet(roleOption.getPermissionsOff()));
         }

         AuthzGroupService.save(edit);
      } catch (GroupNotDefinedException e) {
         logger.error("", e);
         throw new OspException(e);
      } catch (AuthzPermissionException e) {
         logger.error("", e);
         throw new OspException(e);
      } catch (RoleAlreadyDefinedException e) {
         logger.error("", e);
         throw new OspException(e);
      }
   }

   protected void removeRole(RoleIntegrationOption roleOption) {
      if (roleOption instanceof ExistingWorksitesRoleIntegrationOption) {
         removeRoleFromAllWorksites((ExistingWorksitesRoleIntegrationOption)roleOption);
         return;
      }

      AuthzGroup realm = null;
      try {
         realm = AuthzGroupService.getAuthzGroup(roleOption.getRealm());
      } catch (GroupNotDefinedException e) {
         logger.error("", e);
         throw new OspException(e);
      }
      removeRole(realm, roleOption);
   }

   protected void removeRoleFromAllWorksites(ExistingWorksitesRoleIntegrationOption roleOption) {
      List sites = SiteService.getSites(org.sakaiproject.site.api.SiteService.SelectionType.ANY,
            null, null, null, org.sakaiproject.site.api.SiteService.SortType.NONE, null);

      for (Iterator i=sites.iterator();i.hasNext();) {
         Site site = (Site)i.next();
         if (site.isType(roleOption.getWorksiteType())) {
            AuthzGroup siteRealm = getWorksiteManager().getSiteRealm(site.getId());
            removeRole(siteRealm, roleOption);
         }
      }
   }

   protected void removeRole(AuthzGroup realm, RoleIntegrationOption roleOption) {
      AuthzGroup edit = null;
      Role remove = realm.getRole(roleOption.getRoleId());

      try {
         edit = AuthzGroupService.getAuthzGroup(realm.getId());
         edit.removeRole(remove.getDescription());
         AuthzGroupService.save(edit);
      } catch (GroupNotDefinedException e) {
         logger.error("", e);
         throw new OspException(e);
      } catch (AuthzPermissionException e) {
         logger.error("", e);
         throw new OspException(e);
      }
   }

   public WorksiteManager getWorksiteManager() {
      return worksiteManager;
   }

   public void setWorksiteManager(WorksiteManager worksiteManager) {
      this.worksiteManager = worksiteManager;
   }

}

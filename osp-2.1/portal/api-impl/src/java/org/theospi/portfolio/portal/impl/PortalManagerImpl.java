/**********************************************************************************
* $URL$
* $Id$
***********************************************************************************
*
* Copyright (c) 2006 The Sakai Foundation.
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
package org.theospi.portfolio.portal.impl;

import org.sakaiproject.exception.IdUnusedException;
import org.sakaiproject.javax.PagingPosition;
import org.sakaiproject.service.legacy.site.Site;
import org.sakaiproject.service.legacy.site.SitePage;
import org.sakaiproject.service.legacy.site.SiteService;
import org.sakaiproject.service.legacy.user.User;
import org.sakaiproject.service.legacy.user.UserDirectoryService;
import org.sakaiproject.service.framework.config.cover.ServerConfigurationService;
import org.theospi.portfolio.portal.intf.PortalManager;
import org.theospi.portfolio.portal.model.ToolCategory;
import org.theospi.portfolio.portal.model.SiteType;

import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Feb 11, 2006
 * Time: 8:38:52 AM
 * To change this template use File | Settings | File Templates.
 */
public class PortalManagerImpl implements PortalManager {

   private UserDirectoryService userDirectoryService;
   private SiteService siteService;

   public User getCurrentUser() {
      return getUserDirectoryService().getCurrentUser();
   }

   public Map getSitesByType() {
      Map typeMap = new Hashtable();

      int index = 0;
      User currentUser = getCurrentUser();
      if (currentUser != null && currentUser.getId().length() > 0) {
         addMyWorkspace(typeMap);
         index++;
      }
      else {
         return createGatewayMap(typeMap);
      }

      List types = getSiteService().getSiteTypes();

      for (Iterator i=types.iterator();i.hasNext();) {
         String type = (String) i.next();
         List sites = getSiteService().getSites(SiteService.SelectionType.ACCESS, type, null,
				null, SiteService.SortType.TITLE_ASC, null);
         if (sites.size() > 0) {
            typeMap.put(new SiteType(type, index), sites);
         }
         index++;
      }

      return typeMap;
   }

   protected Map createGatewayMap(Map typeMap) {
      String gatewayId = ServerConfigurationService.getGatewaySiteId();
      try {
         Site gateway = getSiteService().getSite(gatewayId);
         List sites = new ArrayList();
         sites.add(gateway);
         typeMap.put(SiteType.GATEWAY, sites);
         return typeMap;
      }
      catch (IdUnusedException e) {
         throw new RuntimeException(e);
      }
   }

   protected void addMyWorkspace(Map typeMap) {
      String myWorkspaceId = getSiteService().getUserSiteId(getCurrentUser().getId());
      try {
         Site myWorkspace = getSiteService().getSite(myWorkspaceId);
         List sites = new ArrayList();
         sites.add(myWorkspace);
         typeMap.put(SiteType.MY_WORKSPACE, sites);
      }
      catch (IdUnusedException e) {
         throw new RuntimeException(e);
      }
   }

   public List getSitesForType(String type, SiteService.SortType sort, PagingPosition page) {
      return getSiteService().getSites(SiteService.SelectionType.ACCESS, type, null,
				null, sort, page);
   }

   public Map getPagesByCategory(String siteId) {
      try {
         Site site = getSiteService().getSite(siteId);

         List pages = site.getPages();
         return categorizePages(pages);
      }
      catch (IdUnusedException e) {
         throw new RuntimeException(e);
      }
   }

   protected Map categorizePages(List pages) {
      // todo read in page categories
      Map pageMap = new Hashtable();
      pageMap.put(ToolCategory.UNCATEGORIZED, pages);
      return pageMap;
   }


   public List getToolsForPage(String pageId) {
      SitePage page = getSiteService().findPage(pageId);
      return page.getTools();
   }

   public Site getSite(String siteId) {
      try {
         return getSiteService().getSite(siteId);
      }
      catch (IdUnusedException e) {
         throw new RuntimeException(e);
      }
   }

   public SitePage getSitePage(String pageId) {
      return getSiteService().findPage(pageId);
   }

   public String getPageCategory(String pageId) {
      // todo find the category
      return ToolCategory.UNCATEGORIZED.getKey();
   }

   public UserDirectoryService getUserDirectoryService() {
      return userDirectoryService;
   }

   public void setUserDirectoryService(UserDirectoryService userDirectoryService) {
      this.userDirectoryService = userDirectoryService;
   }

   public SiteService getSiteService() {
      return siteService;
   }

   public void setSiteService(SiteService siteService) {
      this.siteService = siteService;
   }
}

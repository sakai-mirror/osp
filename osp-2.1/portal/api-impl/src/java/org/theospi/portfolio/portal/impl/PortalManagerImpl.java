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
import org.sakaiproject.service.framework.config.cover.ServerConfigurationService;
import org.sakaiproject.service.legacy.site.Site;
import org.sakaiproject.service.legacy.site.SitePage;
import org.sakaiproject.service.legacy.site.SiteService;
import org.sakaiproject.service.legacy.user.User;
import org.sakaiproject.service.legacy.user.UserDirectoryService;
import org.sakaiproject.api.kernel.tool.Placement;
import org.theospi.portfolio.portal.intf.PortalManager;
import org.theospi.portfolio.portal.model.SiteType;
import org.theospi.portfolio.portal.model.ToolCategory;
import org.theospi.portfolio.portal.model.SitePageWrapper;

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

   private Map siteTypes;
   private static final String TYPE_PREFIX = "org.theospi.portfolio.portal.";

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
            SiteType siteType = (SiteType) getSiteTypes().get(type);
            if (siteType == null) {
               siteType = SiteType.OTHER;
            }
            typeMap.put(siteType, sites);
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
      String baseType = extractType(type);
      return getSiteService().getSites(SiteService.SelectionType.ACCESS, baseType, null,
				null, sort, page);
   }

   protected String extractType(String type) {
      return type.substring(TYPE_PREFIX.length());
   }

   public Map getPagesByCategory(String siteId) {
      try {
         Site site = getSiteService().getSite(siteId);

         List pages = site.getPages();
         Map categories = categorizePages(pages, (SiteType) getSiteTypes().get(site.getType()));

         List categoryList = new ArrayList(categories.keySet());

         Collections.sort(categoryList);

         int index = 0;
         for (Iterator i=categoryList.iterator();i.hasNext();) {
            ToolCategory category = (ToolCategory) i.next();
            category.setOrder(index);
            index++;
         }

         return categories;
      }
      catch (IdUnusedException e) {
         throw new RuntimeException(e);
      }
   }

   protected Map categorizePages(List pages, SiteType type) {
      Map pageMap = new Hashtable();

      int index = 0;
      for (Iterator i=pages.iterator();i.hasNext();) {
         SitePage page = (SitePage) i.next();
         categorizePage(page, pageMap, type, index);
         index++;
      }

      return pageMap;
   }

   protected void categorizePage(SitePage page, Map pageMap, SiteType siteType, int index) {
      ToolCategory[] categories = findCategories(page, siteType, index);

      for (int i=0;i<categories.length;i++) {
         List pages = (List) pageMap.get(categories[i]);
         if (pages == null) {
            pages = new ArrayList();
            pageMap.put(categories[i], pages);
         }
         pages.add(new SitePageWrapper(page, index));
      }
   }

   protected ToolCategory[] findCategories(SitePage page, SiteType siteType, int index) {
      List tools = page.getTools();

      if (tools.size() == 0) {
         return createUncategorized(index);
      }

      Placement tool = (Placement) tools.get(0);
      String toolId = tool.getTool().getId();
      List toolCategories = new ArrayList();
      if (siteType != null && siteType.getToolCategories() != null) {
         for (Iterator i=siteType.getToolCategories().iterator();i.hasNext();){
            ToolCategory category = (ToolCategory) i.next();
            if (category.getToolIds().contains(toolId)) {
               toolCategories.add(category);
            }
         }
      }

      if (toolCategories.size() == 0) {
         return createUncategorized(index);
      }

      return (ToolCategory[]) toolCategories.toArray(new ToolCategory[toolCategories.size()]);
   }

   private ToolCategory[] createUncategorized(int index) {
      try {
         ToolCategory category = (ToolCategory) ToolCategory.UNCATEGORIZED.clone();
         category.setOrder(index);
         return new ToolCategory[]{category};

      }
      catch (CloneNotSupportedException e) {
         throw new RuntimeException(e);
      }
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

   public String getPageCategory(String siteId, String pageId) {
      Site site = getSite(siteId);
      SitePage page = site.getPage(siteId);
      SiteType siteType = (SiteType) getSiteTypes().get(decorateSiteType(site));

      ToolCategory[] categories = findCategories(page, siteType, 0);

      if (categories.length == 0){
         return ToolCategory.UNCATEGORIZED.getKey();
      }

      return categories[0].getKey();
   }

   public String decorateSiteType(String siteTypeKey) {
      return TYPE_PREFIX + siteTypeKey;
   }

   public String decorateSiteType(Site site) {
      if (getSiteService().isUserSite(site.getId())){
         return SiteType.MY_WORKSPACE.getKey();
      }
      else {
         return decorateSiteType(site.getType());
      }
   }

   public SiteType getSiteType(String siteTypeKey) {
      for (Iterator i=getSiteTypes().values().iterator();i.hasNext();) {
         SiteType siteType = (SiteType) i.next();
         if (siteType.getKey().equals(siteTypeKey)) {
            return siteType;
         }
      }
      return null;
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

   public Map getSiteTypes() {
      return siteTypes;
   }

   public void setSiteTypes(Map siteTypes) {
      this.siteTypes = siteTypes;
   }
}

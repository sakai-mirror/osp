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
package org.theospi.portfolio.portal.intf;

import org.sakaiproject.javax.PagingPosition;
import org.sakaiproject.service.legacy.site.Site;
import org.sakaiproject.service.legacy.site.SitePage;
import org.sakaiproject.service.legacy.site.SiteService;
import org.sakaiproject.service.legacy.user.User;
import org.theospi.portfolio.portal.model.SiteType;
import org.theospi.portfolio.portal.model.ToolCategory;

import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Feb 11, 2006
 * Time: 8:05:11 AM
 * To change this template use File | Settings | File Templates.
 */
public interface PortalManager {

   public static final String CONTEXT = "org.theospi.portfolio.portal.context";
   public static final String SITE_TYPE = "org.theospi.portfolio.portal.siteType";
   public static final String SITE_ID = "org.theospi.portfolio.portal.siteId";
   public static final String TOOL_CATEGORY = "org.theospi.portfolio.portal.toolCategory";

   public User getCurrentUser();

   public Map getSitesByType();

   public List getSitesForType(String type, SiteService.SortType sort, PagingPosition page);

   public Map getPagesByCategory(String siteId);

   public List getToolsForPage(String pageId);

   public Site getSite(String siteId);

   public SitePage getSitePage(String pageId);

   public String getPageCategory(String siteId, String pageId);

   public String decorateSiteType(Site site);

   public SiteType getSiteType(String siteTypeKey);

   public ToolCategory getToolCategory(String siteType, String toolCategoryKey);

   public boolean isAvailable(String toolId, String siteId);

   public SitePage getPage(String toolId, String siteId);

   public boolean isUserInRole(String roleId, String siteId);

}

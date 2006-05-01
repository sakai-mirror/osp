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
package org.theospi.portfolio.portal.tool;

import org.theospi.portfolio.shared.tool.HelperToolBase;
import org.theospi.portfolio.shared.tool.PagingList;
import org.theospi.portfolio.portal.intf.PortalManager;
import org.sakaiproject.site.api.SiteService;

import java.util.List;


/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Feb 16, 2006
 * Time: 10:01:00 AM
 * To change this template use File | Settings | File Templates.
 */
public class SiteTypeTool extends HelperToolBase {

   private PortalManager portalManager;

   private PagingList sites = null;

   public PagingList getSites() {
      String siteType = (String) getAttribute(PortalManager.SITE_TYPE);
      if (siteType != null) {
         List sitesBase = getPortalManager().getSitesForType(siteType, SiteService.SortType.TITLE_ASC, null);
         setSites(new PagingList(sitesBase));
         removeAttribute(PortalManager.SITE_TYPE);
      }

      return sites;
   }

   public void setSites(PagingList sites) {
      this.sites = sites;
   }

   public PortalManager getPortalManager() {
      return portalManager;
   }

   public void setPortalManager(PortalManager portalManager) {
      this.portalManager = portalManager;
   }

}

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
package org.theospi.portfolio.list.impl;

import org.theospi.portfolio.list.intf.CustomLinkListGenerator;
import org.sakaiproject.service.framework.config.cover.ServerConfigurationService;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Feb 10, 2006
 * Time: 3:15:45 PM
 * To change this template use File | Settings | File Templates.
 */
public abstract class BaseListGenerator implements CustomLinkListGenerator {

   protected abstract String getSiteId(Object entity);

   protected abstract String getPageId(Object entity);

   public String getCustomLink(Object entry) {
      // http://nightly2.sakaiproject.org:8084/portal/site/804a576b-6d03-474f-008c-74bd96a80676
      // http://iter-odd.rsmart.com:8081/portal/site/f66ba891-fdb8-44bf-0063-04b663b88c23/page/77a3d5ad-7a1a-4aa8-80ba-0fa2504a3b36
      String siteId = getSiteId(entry);
      String pageId = getPageId(entry);

      String link = "/site/" + siteId;
      if (pageId != null) {
         link = link + "/page/" + pageId;
      }
      return ServerConfigurationService.getPortalUrl() + link;
   }

}

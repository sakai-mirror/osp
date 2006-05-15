/**********************************************************************************
* $URL$
* $Id$
***********************************************************************************
*
* Copyright (c) 2005, 2006 The Sakai Foundation.
*
* Licensed under the Educational Community License, Version 1.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*Ê Ê Ê http://www.opensource.org/licenses/ecl1.php
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*
**********************************************************************************/

package org.theospi.portfolio.worksite.mgt.impl;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.metaobj.worksite.mgt.WorksiteManager;
import org.sakaiproject.site.api.Site;
import org.sakaiproject.site.api.SitePage;
import org.sakaiproject.site.api.ToolConfiguration;
import org.theospi.portfolio.list.impl.WorksiteBaseGenerator;
import org.theospi.portfolio.list.intf.ListGenerator;

public class WorksiteListGenerator extends WorksiteBaseGenerator implements ListGenerator {
   protected final transient Log logger = LogFactory.getLog(getClass());

   private static final String SITE_ID_PARAM = "selectedSiteId";

   private WorksiteManager worksiteManager;
   private List columns;
   private List defaultColumns;
   public void init(){
       super.init();
   }
   /**
    * @return array of coluimn names (should be bean names)
    */
   public List getColumns() {
      return columns;
   }

   /**
    * @return array of columns a user has by default
    */
   public List getDefaultColumns() {
      return defaultColumns;
   }

   public void setColumns(List columns) {
      this.columns = columns;
   }

   public void setDefaultColumns(List defaultColumns) {
      this.defaultColumns = defaultColumns;
   }

   /**
    * @return the current user's list of objects
    *         (whatever that means to the implentation)
    */
   public List getObjects() {
      return getWorksiteManager().getUserSites();
   }

   public ToolConfiguration getToolInfo(Map request) {
      String siteId = (String) request.get(SITE_ID_PARAM);

      Site site = getWorksiteManager().getSite(siteId);
      List pages = site.getPages();
      for (Iterator i=pages.iterator();i.hasNext();) {
         SitePage page = (SitePage)i.next();
         if (page.getTitle().equals("Home")) {
            return (ToolConfiguration)page.getTools().get(0);
         }
      }

      return null;
   }

   public boolean isNewWindow(Object entry) {
      return false;
   }

   /**
    * @param entry
    * @return
    */
   public Map getToolParams(Object entry) {
      Site site = (Site)entry;
      Map model = new HashMap();

      model.put(SITE_ID_PARAM, site.getId());

      return model;
   }

   public WorksiteManager getWorksiteManager() {
      return worksiteManager;
   }

   public void setWorksiteManager(WorksiteManager worksiteManager) {
      this.worksiteManager = worksiteManager;
   }

   protected String getSiteId(Object entity) {
      Site site = (Site) entity;
      return site.getId();
   }

   protected String getPageId(Object entity) {
      Site site = (Site) entity;
      List pages = site.getPages();
      if (pages.size() > 0) {
         return ((SitePage)pages.get(0)).getId();

      }
      return null;
   }
}

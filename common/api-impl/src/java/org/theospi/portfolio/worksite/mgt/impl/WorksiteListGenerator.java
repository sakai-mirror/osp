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
 * $Header: /opt/CVS/osp/src/portfolio/org/theospi/portfolio/worksite/mgt/impl/WorksiteListGenerator.java,v 1.4 2005/08/29 18:24:53 jellis Exp $
 * $Revision: 8645 $
 * $Date$
 */
package org.theospi.portfolio.worksite.mgt.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.theospi.portfolio.list.intf.ListGenerator;
import org.theospi.portfolio.list.impl.WorksiteBaseGenerator;
import org.sakaiproject.site.api.ToolConfiguration;
import org.sakaiproject.site.api.Site;
import org.sakaiproject.site.api.SitePage;
import org.sakaiproject.metaobj.worksite.mgt.WorksiteManager;

import java.util.*;

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

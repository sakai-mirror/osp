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
 * $Header: /root/osp/src/portfolio/org/theospi/portfolio/admin/service/SakaiIntegrationServiceImpl.java,v 1.3 2004/12/15 21:47:09 jellis Exp $
 * $Revision: 1.3 $
 * $Date: 2004/12/15 21:47:09 $
 */
package org.theospi.portfolio.admin.service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.theospi.portfolio.admin.intf.SakaiIntegrationService;
import org.theospi.portfolio.admin.intf.SakaiIntegrationPlugin;
import org.theospi.portfolio.admin.startup.ServerListener;
import org.theospi.portfolio.admin.startup.ServerListeningService;
import org.theospi.portfolio.admin.model.IntegrationOption;
import org.sakaiproject.api.kernel.session.cover.SessionManager;
import org.sakaiproject.api.kernel.component.cover.ComponentManager;
import org.sakaiproject.api.app.scheduler.SchedulerManager;
import org.sakaiproject.exception.IdUsedException;
import org.sakaiproject.service.legacy.content.ContentCollectionEdit;
import org.sakaiproject.service.legacy.content.ContentHostingService;
import org.sakaiproject.service.legacy.entity.ResourceProperties;

import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;

public class SakaiIntegrationServiceImpl implements SakaiIntegrationService, ServerListener {
   protected final transient Log logger = LogFactory.getLog(getClass());

   private List integrationPlugins;
   private List dependantBeans;
   private long pollingInterval;
   private SchedulerManager schedulerManager;
   private ContentHostingService contentHostingService;
   private List initUsers = new ArrayList();

   public void triggerEvent(String event) {
      if (event.equals(ServerListeningService.SERVER_STARTUP_COMPLETE)) {
         org.sakaiproject.api.kernel.session.Session sakaiSession = SessionManager.getCurrentSession();
         String userId = sakaiSession.getUserId();

         try {
            sakaiSession.setUserId("admin");
            sakaiSession.setUserEid("admin");

            for (Iterator i=getIntegrationPlugins().iterator();i.hasNext();) {
               String pluginId = (String) i.next();
               SakaiIntegrationPlugin plugin = (SakaiIntegrationPlugin) ComponentManager.get(pluginId);
               executePlugin(plugin);
            }
         } finally {
            sakaiSession.setUserEid(userId);
            sakaiSession.setUserId(userId);
         }

      }
   }

   protected void executePlugin(SakaiIntegrationPlugin plugin) {
      for (Iterator i=plugin.getPotentialIntegrations().iterator();i.hasNext();) {
         if (!plugin.executeOption((IntegrationOption) i.next())) {
            break;
         }
      }
   }

   public void init() {
      // go through each integration plugin and execute it...
      org.sakaiproject.api.kernel.session.Session sakaiSession = SessionManager.getCurrentSession();
      String userId = sakaiSession.getUserId();

      try {
         sakaiSession.setUserId("admin");
         sakaiSession.setUserEid("admin");
         ServerListeningService.getInstance().addListener(this);
         createUserResourceDir();
      } finally {
         sakaiSession.setUserEid(userId);
         sakaiSession.setUserId(userId);
      }
   }

   protected void createUserResourceDir() {
      for (Iterator iter = getInitUsers().iterator(); iter.hasNext();) {
         String userId = (String)iter.next();
         try {
            ContentCollectionEdit userCollection = getContentHostingService().addCollection("/user/" + userId);
            userCollection.getPropertiesEdit().addProperty(ResourceProperties.PROP_DISPLAY_NAME, userId);
            getContentHostingService().commitCollection(userCollection);
         }
         catch (IdUsedException e) {
            // ignore... it is already there.
         }
         catch (Exception e) {
            throw new RuntimeException(e);
         }
      }
   }

   public List getIntegrationPlugins() {
      return integrationPlugins;
   }

   public void setIntegrationPlugins(List integrationPlugins) {
      this.integrationPlugins = integrationPlugins;
   }

   public List getDependantBeans() {
      return dependantBeans;
   }

   public void setDependantBeans(List dependantBeans) {
      this.dependantBeans = dependantBeans;
   }

   public long getPollingInterval() {
      return pollingInterval;
   }

   public void setPollingInterval(long pollingInterval) {
      this.pollingInterval = pollingInterval;
   }

   public SchedulerManager getSchedulerManager() {
      return schedulerManager;
   }

   public void setSchedulerManager(SchedulerManager schedulerManager) {
      this.schedulerManager = schedulerManager;
   }

   public ContentHostingService getContentHostingService() {
      return contentHostingService;
   }

   public void setContentHostingService(ContentHostingService contentHostingService) {
      this.contentHostingService = contentHostingService;
   }

   public List getInitUsers() {
      return initUsers;
   }

   public void setInitUsers(List initUsers) {
      this.initUsers = initUsers;
   }
}

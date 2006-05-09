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
*Ê Ê Ê http://www.opensource.org/licenses/ecl1.php
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*
**********************************************************************************/

package org.theospi.portfolio.admin.service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.theospi.portfolio.admin.intf.SakaiIntegrationService;
import org.theospi.portfolio.admin.intf.SakaiIntegrationPlugin;
import org.theospi.portfolio.admin.startup.ServerListener;
import org.theospi.portfolio.admin.startup.ServerListeningService;
import org.theospi.portfolio.admin.model.IntegrationOption;
import org.sakaiproject.tool.api.Session;
import org.sakaiproject.tool.cover.SessionManager;
import org.sakaiproject.component.cover.ComponentManager;
import org.sakaiproject.api.app.scheduler.SchedulerManager;
import org.sakaiproject.exception.IdUsedException;
import org.sakaiproject.content.api.ContentCollectionEdit;
import org.sakaiproject.content.api.ContentHostingService;
import org.sakaiproject.entity.api.ResourceProperties;

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
         Session sakaiSession = SessionManager.getCurrentSession();
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
      Session sakaiSession = SessionManager.getCurrentSession();
      String userId = sakaiSession.getUserId();

      try {
         sakaiSession.setUserId("admin");
         sakaiSession.setUserEid("admin");
         ServerListeningService.getInstance().addListener(this);
         createUserResourceDir();
      } catch (Exception e) {
         logger.warn("Temporarily catching all exceptions in osp.SakaiIntegrationServiceImpl.init()", e);
      } finally {
         sakaiSession.setUserEid(userId);
         sakaiSession.setUserId(userId);
      }
   }

   protected void createUserResourceDir() {
      for (Iterator iter = getInitUsers().iterator(); iter.hasNext();) {
         String userId = (String)iter.next();
         try {
            ContentCollectionEdit userCollection = getContentHostingService().addCollection("/user/" + userId + "/");
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

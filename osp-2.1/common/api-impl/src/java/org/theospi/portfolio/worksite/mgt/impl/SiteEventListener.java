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
*      http://www.opensource.org/licenses/ecl1.php
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*
**********************************************************************************/
package org.theospi.portfolio.worksite.mgt.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.service.legacy.event.Event;
import org.sakaiproject.service.legacy.event.cover.EventTrackingService;
import org.sakaiproject.service.legacy.site.cover.SiteService;
import org.sakaiproject.service.legacy.site.Site;
import org.sakaiproject.service.legacy.site.SitePage;
import org.sakaiproject.service.legacy.site.ToolConfiguration;
import org.sakaiproject.service.legacy.entity.Reference;
import org.sakaiproject.service.legacy.entity.EntityManager;
import org.sakaiproject.service.framework.component.ComponentManager;
import org.sakaiproject.api.kernel.tool.Tool;
import org.sakaiproject.api.kernel.tool.cover.ToolManager;
import org.sakaiproject.exception.IdUnusedException;
import org.theospi.portfolio.shared.model.OspException;
import org.theospi.portfolio.worksite.intf.ToolEventListener;
import org.theospi.portfolio.worksite.model.SiteTool;
import org.springframework.orm.hibernate.support.HibernateDaoSupport;

import java.util.*;

public class SiteEventListener extends HibernateDaoSupport implements Observer {
   protected final transient Log logger = LogFactory.getLog(getClass());

   public final static String LISTENER_PROPERTY_TAG = "theospi.toolListenerId";
   private ComponentManager componentManager;
   private EntityManager entityManager;
   
   private List siteHelperTools = new ArrayList();

   /**
    * This method is called whenever the observed object is changed. An
    * application calls an <tt>Observable</tt> object's
    * <code>notifyObservers</code> method to have all the object's
    * observers notified of the change.
    *
    * @param o   the observable object.
    * @param arg an argument passed to the <code>notifyObservers</code>
    *            method.
    */
   public void update(Observable o, Object arg) {
      if (arg instanceof Event) {
         processEvent((Event)arg, o);
      }
   }

   protected void processEvent(Event event, Observable arg) {
      if (event.getEvent().equals(SiteService.SECURE_ADD_SITE) ||
         event.getEvent().equals(SiteService.SECURE_UPDATE_SITE)) {
         // check out the update
         try {
            Reference ref = getEntityManager().newReference(event.getResource());
            Site site = SiteService.getSite(ref.getId());
            processSite(site);

            if (event.getEvent().equals(SiteService.SECURE_UPDATE_SITE)) {
               processUpdate(site);
            }
         } catch (IdUnusedException e) {
            logger.error("error getting site object", e);
            throw new OspException(e);
         }
      }
      else if (event.getEvent().equals(SiteService.SECURE_REMOVE_SITE)) {
         removeAll(getEntityManager().newReference(event.getResource()));
      }
   }

   protected void processUpdate(Site site) {
      Collection siteTools = getSiteTools(site.getId());

      for (Iterator i=siteTools.iterator();i.hasNext();) {
         SiteTool tool = (SiteTool)i.next();

         ToolConfiguration toolConfig = SiteService.findTool(tool.getToolId());

         if (toolConfig == null) {
            removeTool(tool);
         }
      }
   }

   protected void removeTool(SiteTool tool) {
      ToolEventListener listener = (ToolEventListener) componentManager.get(tool.getListenerId());

      listener.toolRemoved(tool);

      getHibernateTemplate().delete(tool);

   }

   protected void removeAll(Reference reference) {
      Collection siteTools = getSiteTools(reference.getId());

      for (Iterator i=siteTools.iterator();i.hasNext();) {
         SiteTool tool = (SiteTool)i.next();
         removeTool(tool);
      }
   }

   protected void processSite(Site site) {
      List pages = site.getPages();
      
      for (Iterator i=getSiteHelperTools().iterator(); i.hasNext();) {
         String toolId = (String)i.next();
         Tool toolPlacement = ToolManager.getTool(toolId);
         String listenerId = 
            toolPlacement.getRegisteredConfig().getProperty(LISTENER_PROPERTY_TAG);
         if (listenerId != null) {
            storeHelperTool(site.getId(), toolId, listenerId);
            ToolEventListener listener = (ToolEventListener) componentManager.get(listenerId);

            if (listener != null) {
               listener.helperSiteChanged(site);
            }
         }
      }  
      
      for (Iterator i=pages.iterator();i.hasNext();) {
         processPage((SitePage)i.next());
      }
   }
   
   protected void storeHelperTool(String siteId, String toolId, String listenerId) {
      SiteTool tool = new SiteTool();
      tool.setSiteId(siteId);
      tool.setToolId(toolId);
      if (getSiteTool(tool.getSiteId(), tool.getToolId()).size() > 0) {
         return;
      }

      tool.setListenerId(listenerId);

      getHibernateTemplate().saveOrUpdate(tool);
   }

   protected void processPage(SitePage sitePage) {
      List tools = sitePage.getTools();

      for (Iterator i=tools.iterator();i.hasNext();) {
         processTool((ToolConfiguration)i.next());
      }
   }

   protected void processTool(ToolConfiguration toolConfiguration) {
      String listenerId =
            toolConfiguration.getConfig().getProperty(LISTENER_PROPERTY_TAG);
      if (listenerId != null) {
         storeTool(toolConfiguration);
         ToolEventListener listener = (ToolEventListener) componentManager.get(listenerId);

         if (listener != null) {
            listener.toolSiteChanged(toolConfiguration);
         }
      }
   }

   public void init() {
      EventTrackingService.addObserver(this);
   }

   protected void storeTool(ToolConfiguration toolConfiguration) {
      SiteTool tool = new SiteTool();
      tool.setSiteId(toolConfiguration.getContainingPage().getContainingSite().getId());
      tool.setToolId(toolConfiguration.getId());
      if (getSiteTool(tool.getSiteId(), tool.getToolId()).size() > 0) {
         return;
      }

      tool.setListenerId(toolConfiguration.getConfig().getProperty(LISTENER_PROPERTY_TAG));

      getHibernateTemplate().saveOrUpdate(tool);
   }

   protected Collection getSiteTool(String siteId, String toolId) {
      return getHibernateTemplate().find("from SiteTool where site_id=? and tool_id=?",
         new Object[]{siteId, toolId});
   }

   protected Collection getSiteTools(String siteId) {
      return getHibernateTemplate().find("from SiteTool where site_id=?", siteId);
   }

   public ComponentManager getComponentManager() {
      return componentManager;
   }

   public void setComponentManager(ComponentManager componentManager) {
      this.componentManager = componentManager;
   }

   public EntityManager getEntityManager() {
      return entityManager;
   }

   public void setEntityManager(EntityManager entityManager) {
      this.entityManager = entityManager;
   }

   public List getSiteHelperTools() {
      return siteHelperTools;
   }

   public void setSiteHelperTools(List siteHelperTools) {
      this.siteHelperTools = siteHelperTools;
   }
}

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
 * $Header: /opt/CVS/osp2.x/common/api-impl/src/java/org/theospi/portfolio/worksite/mgt/impl/SiteEventListener.java,v 1.3 2005/10/26 16:56:26 andersjb Exp $
 * $Revision$
 * $Date$
 */
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

      for (Iterator i=pages.iterator();i.hasNext();) {
         processPage((SitePage)i.next());
      }
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
}

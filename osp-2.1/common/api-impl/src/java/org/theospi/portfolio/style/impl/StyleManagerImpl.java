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

package org.theospi.portfolio.style.impl;

import java.sql.SQLException;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import net.sf.hibernate.HibernateException;
import net.sf.hibernate.Session;

import org.sakaiproject.exception.IdUnusedException;
import org.sakaiproject.exception.PermissionException;
import org.sakaiproject.exception.TypeException;
import org.sakaiproject.metaobj.security.AuthenticationManager;
import org.sakaiproject.metaobj.shared.mgt.AgentManager;
import org.sakaiproject.metaobj.shared.mgt.IdManager;
import org.sakaiproject.metaobj.shared.model.Agent;
import org.sakaiproject.metaobj.shared.model.Id;
import org.sakaiproject.metaobj.worksite.mgt.WorksiteManager;
import org.sakaiproject.service.legacy.content.ContentHostingService;
import org.sakaiproject.service.legacy.content.ContentResource;
import org.sakaiproject.service.legacy.content.LockManager;
import org.sakaiproject.service.legacy.entity.Reference;
import org.sakaiproject.service.legacy.site.Site;
import org.springframework.orm.hibernate.HibernateCallback;
import org.springframework.orm.hibernate.HibernateObjectRetrievalFailureException;
import org.springframework.orm.hibernate.support.HibernateDaoSupport;
import org.theospi.portfolio.security.AuthorizationFacade;
import org.theospi.portfolio.shared.model.Node;
import org.theospi.portfolio.style.StyleFunctionConstants;
import org.theospi.portfolio.style.mgt.StyleManager;
import org.theospi.portfolio.style.model.Style;

public class StyleManagerImpl extends HibernateDaoSupport
   implements StyleManager {

   private AuthorizationFacade authzManager = null;
   private IdManager idManager = null;
   private WorksiteManager worksiteManager;
   private AuthenticationManager authnManager = null;
   private LockManager lockManager;
   private ContentHostingService contentHosting = null;
   private AgentManager agentManager;
   private List globalSites;
   private List globalSiteTypes;
   
   
   public Style storeStyle(Style style) {
      return storeStyle(style, true);
   }
   
   public Style storeStyle (Style style, boolean checkAuthz) {
      style.setModified(new Date(System.currentTimeMillis()));

      boolean newStyle = (style.getId() == null);

      if (newStyle) {
         style.setCreated(new Date(System.currentTimeMillis()));

         if (checkAuthz) {
            getAuthzManager().checkPermission(StyleFunctionConstants.CREATE_STYLE,
               getIdManager().getId(style.getSiteId()));
         }
      } else {
         if (checkAuthz) {
            getAuthzManager().checkPermission(StyleFunctionConstants.EDIT_STYLE,
                  style.getId());
         }
      }
      getHibernateTemplate().saveOrUpdateCopy(style);
      lockStyleFiles(style);

      return style;
   }
   
   protected void lockStyleFiles(Style style){
      getLockManager().removeAllLocks(style.getId().getValue());
      getLockManager().lockObject(style.getStyleFile().getValue(), 
            style.getId().getValue(), "saving a style", true);
      
   }
   
   public Style getStyle(Id styleId) {
      return (Style) getHibernateTemplate().load(Style.class, styleId);
   }
   
   public Style getLightWeightStyle(final Id styleId) {
      HibernateCallback callback = new HibernateCallback() {

         public Object doInHibernate(Session session) throws HibernateException, SQLException {
            Style style = (Style) session.load(Style.class, styleId);
            return style;
         }

      };

      try {
         Style style = (Style) getHibernateTemplate().execute(callback);
         return style;
      } catch (HibernateObjectRetrievalFailureException e) {
         logger.debug(e);
         return null;
      }
   }
   
   public Collection findPublishedStyles(String currentWorksiteId) {
      String query = "from Style s where s.globalState = ? or (s.siteId = ? and (s.owner = ? or s.siteState = ? )) ";
      Object[] params = new Object[]{new Integer(Style.STATE_PUBLISHED),
                                     currentWorksiteId,
                                     getAuthnManager().getAgent().getId().getValue(),
                                     new Integer(Style.STATE_PUBLISHED)};
      return getHibernateTemplate().find(query, params);
   }
   
   public Collection findPublishedStyles() {
      // only for the appropriate worksites
      String query = "from Style s where s.owner = ? or s.globalState = ? or (s.siteState = ?  s.and siteId in (";

      List sites = getWorksiteManager().getUserSites();
      for (Iterator i=sites.iterator();i.hasNext();) {
         Site site = (Site)i.next();
         query += "'" + site.getId() + "'";
         query += ",";
      }

      query += "''))";

      Object[] params = new Object[]{getAuthnManager().getAgent().getId().getValue(),
                                     new Integer(Style.STATE_PUBLISHED),
                                     new Integer(Style.STATE_PUBLISHED)};
      return getHibernateTemplate().find(query, params);
   }
   
   public Collection findGlobalStyles(Agent agent) {
      String query = "from Style s where ((s.siteId is null and (s.globalState = ? or s.owner = ?)) or s.globalState = 1)";
      Object[] params = new Object[]{new Integer(Style.STATE_PUBLISHED),
                                     agent.getId().getValue()};
      return getHibernateTemplate().find(query, params);
   }
   
   public Collection findStylesByOwner(Agent owner, String siteId) {
      return getHibernateTemplate().find("from Style where owner_id=? and site_id=? Order by name",
            new Object[]{owner.getId().getValue(), siteId});
   }
   
   public boolean isGlobal() {
      String siteId = getWorksiteManager().getCurrentWorksiteId().getValue();

      if (getGlobalSites().contains(siteId)) {
         return true;
      }

      Site site = getWorksiteManager().getSite(siteId);
      if (site.getType() != null && getGlobalSiteTypes().contains(site.getType())) {
         return true;
      }

      return false;
   }
   
   public Node getNode(Id artifactId) {
      String id = getContentHosting().resolveUuid(artifactId.getValue());
      if (id == null) {
         return null;
      }

      try {
         ContentResource resource = getContentHosting().getResource(id);
         String ownerId = resource.getProperties().getProperty(resource.getProperties().getNamePropCreator());
         Agent owner = getAgentManager().getAgent(getIdManager().getId(ownerId));
         return new Node(artifactId, resource, owner);
      }
      catch (PermissionException e) {
         logger.error("", e);
         throw new RuntimeException(e);
      }
      catch (IdUnusedException e) {
         logger.error("", e);
         throw new RuntimeException(e);
      }
      catch (TypeException e) {
         logger.error("", e);
         throw new RuntimeException(e);
      }
   }
   
   public Node getNode(Reference ref) {
      String nodeId = getContentHosting().getUuid(ref.getId());

      return getNode(getIdManager().getId(nodeId));
   }

   public AuthenticationManager getAuthnManager() {
      return authnManager;
   }

   public void setAuthnManager(AuthenticationManager authnManager) {
      this.authnManager = authnManager;
   }

   public AuthorizationFacade getAuthzManager() {
      return authzManager;
   }

   public void setAuthzManager(AuthorizationFacade authzManager) {
      this.authzManager = authzManager;
   }

   public List getGlobalSites() {
      return globalSites;
   }

   public void setGlobalSites(List globalSites) {
      this.globalSites = globalSites;
   }

   public List getGlobalSiteTypes() {
      return globalSiteTypes;
   }

   public void setGlobalSiteTypes(List globalSiteTypes) {
      this.globalSiteTypes = globalSiteTypes;
   }

   public IdManager getIdManager() {
      return idManager;
   }

   public void setIdManager(IdManager idManager) {
      this.idManager = idManager;
   }

   public WorksiteManager getWorksiteManager() {
      return worksiteManager;
   }

   public void setWorksiteManager(WorksiteManager worksiteManager) {
      this.worksiteManager = worksiteManager;
   }

   public LockManager getLockManager() {
      return lockManager;
   }

   public void setLockManager(LockManager lockManager) {
      this.lockManager = lockManager;
   }

   public AgentManager getAgentManager() {
      return agentManager;
   }

   public void setAgentManager(AgentManager agentManager) {
      this.agentManager = agentManager;
   }

   public ContentHostingService getContentHosting() {
      return contentHosting;
   }

   public void setContentHosting(ContentHostingService contentHosting) {
      this.contentHosting = contentHosting;
   }
}

/**********************************************************************************
* $URL:https://source.sakaiproject.org/svn/osp/trunk/common/api-impl/src/java/org/theospi/portfolio/security/impl/sakai/WorksiteAwareAuthorizationFacade.java $
* $Id:WorksiteAwareAuthorizationFacade.java 9134 2006-05-08 20:28:42Z chmaurer@iupui.edu $
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
package org.theospi.portfolio.security.impl.sakai;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.metaobj.security.impl.sakai.SecurityBase;
import org.sakaiproject.metaobj.shared.mgt.AgentManager;
import org.sakaiproject.metaobj.shared.model.Agent;
import org.sakaiproject.metaobj.shared.model.Id;
import org.sakaiproject.thread_local.api.ThreadLocalManager;
import org.sakaiproject.site.api.SiteService.SelectionType;
import org.sakaiproject.site.api.Site;
import org.sakaiproject.site.cover.SiteService;
import org.sakaiproject.tool.cover.ToolManager;
import org.theospi.portfolio.security.Authorization;
import org.theospi.portfolio.security.impl.simple.SimpleAuthorizationFacade;

public class WorksiteAwareAuthorizationFacade extends SimpleAuthorizationFacade {
   protected final transient Log logger = LogFactory.getLog(getClass());

   private AgentManager agentManager = null;
   private SecurityBase sakaiSecurityBase;
   private ThreadLocalManager threadLocalManager;
   private static final String AUTHZ_GROUPS_LIST =
         "org.theospi.portfolio.security.impl.sakai.WorksiteAwareAuthorizationFacade.authzGroups";


   /**
    * @param agent
    * @param function
    * @param id
    */
   public void createAuthorization(Agent agent, String function, Id id) {
      // don't want to include roles here otherwise can't explicitly create authz for both role and user
      // see bug http://cvs.theospi.org:14443/jira/browse/OSP-459
      Authorization auth = getAuthorization(agent, function, id, false);
      if (auth == null) {
         auth = new Authorization(agent, function, id);
      }

      getHibernateTemplate().saveOrUpdate(auth);
   }

   public void deleteAuthorization(Agent agent, String function, Id id) {
      Authorization auth = getAuthorization(agent, function, id, false);
      if (auth != null) {
         getHibernateTemplate().delete(auth);
      }
   }

   public void pushAuthzGroups(Collection authzGroups) {
      List authzGroupList = getAuthzGroupsList();
      authzGroupList.addAll(authzGroups);
   }

   public void pushAuthzGroups(String siteId) {
      getAuthzGroupsList().add(siteId);
   }

   protected Authorization getAuthorization(Agent agent, String function, Id id, boolean includeRoles) {
      if (includeRoles){
         Set roles = getAgentRoles(agent);

         for (Iterator i=roles.iterator();i.hasNext();) {
            Agent roleAgent = (Agent)i.next();
            if (roleAgent != null) {
               Authorization authz = super.getAuthorization(roleAgent, function, id);
               if (authz != null) {
                  return authz;
               }
            }
         }
      }
      return super.getAuthorization(agent, function, id);

   }

   protected Authorization getAuthorization(Agent agent, String function, Id id) {
      return getAuthorization(agent, function, id, true);
   }

   protected List findByAgent(Agent agent) {
      Set roles = getAgentRoles(agent);

      List authzs = new ArrayList();

      for (Iterator i=roles.iterator();i.hasNext();) {
         Agent next = (Agent)i.next();
         if (next != null) {
            authzs.addAll(super.findByAgent((Agent)i.next()));
         }
      }

      authzs.addAll(super.findByAgent(agent));
      return authzs;
   }

   protected List findByAgentFunction(Agent agent, String function) {
      Set roles = getAgentRoles(agent);

      List authzs = new ArrayList();

      for (Iterator i=roles.iterator();i.hasNext();) {
         Agent next = (Agent)i.next();

         if (next != null) {
            authzs.addAll(super.findByAgentFunction(
               next, function));
         }
      }

      authzs.addAll(super.findByAgentFunction(agent, function));
      return authzs;
   }

   protected List findByAgentId(Agent agent, Id id) {
      Set roles = getAgentRoles(agent);

      List authzs = new ArrayList();

      for (Iterator i=roles.iterator();i.hasNext();) {
         Agent next = (Agent)i.next();
         if (next != null) {
            authzs.addAll(super.findByAgentId(
               (Agent)i.next(), id));
         }
      }

      authzs.addAll(super.findByAgentId(agent, id));
      return authzs;
   }

   protected Set getAgentRoles(Agent agent) {
      Set agentRoles = new HashSet();
      List authzGroups = getAuthzGroupsList();

      for (Iterator i = authzGroups.iterator();i.hasNext();) {
         String site = (String)i.next();
         agentRoles.addAll(agent.getWorksiteRoles(site));
      }

      // If this is a user's My Workspace, aggregate roles from all member sites
      if ( ToolManager.getCurrentPlacement() != null && SiteService.isUserSite(ToolManager.getCurrentPlacement().getContext()) ) {
         List allSites = SiteService.getSites(SelectionType.ACCESS, null, null,
                                            null, null, null);
         allSites.addAll ( SiteService.getSites(SelectionType.UPDATE, null, null,
                                            null, null, null) );
         for (Iterator it = allSites.iterator();it.hasNext();) {
            Site site = (Site)it.next();
            agentRoles.addAll(agent.getWorksiteRoles( site.getId() ));
         }
         
         // finally, add user agent for user-based aurhorizations
         agentRoles.add(agent);
      }

      // Otherwise just get roles from current worksite
      else  {
         agentRoles.addAll(agent.getWorksiteRoles());
      }
         
      return agentRoles;
   }

   public AgentManager getAgentManager() {
      return agentManager;
   }

   public void setAgentManager(AgentManager agentManager) {
      this.agentManager = agentManager;
   }

   public SecurityBase getSakaiSecurityBase() {
      return sakaiSecurityBase;
   }

   public void setSakaiSecurityBase(SecurityBase sakaiSecurityBase) {
      this.sakaiSecurityBase = sakaiSecurityBase;
   }

   public ThreadLocalManager getThreadLocalManager() {
      return threadLocalManager;
   }

   public void setThreadLocalManager(ThreadLocalManager threadLocalManager) {
      this.threadLocalManager = threadLocalManager;
   }

   protected List getAuthzGroupsList() {
      List returned = (List)threadLocalManager.get(AUTHZ_GROUPS_LIST);

      if (returned == null) {
         returned = new ArrayList();
         threadLocalManager.set(AUTHZ_GROUPS_LIST, returned);
      }
      return returned;
   }
}

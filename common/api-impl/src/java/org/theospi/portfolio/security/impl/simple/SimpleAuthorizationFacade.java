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
package org.theospi.portfolio.security.impl.simple;

import org.springframework.orm.hibernate3.HibernateObjectRetrievalFailureException;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import org.theospi.portfolio.security.Authorization;
import org.theospi.portfolio.security.AuthorizationFacade;
import org.theospi.portfolio.security.AuthorizationFailedException;
import org.theospi.portfolio.shared.model.OspException;
import org.sakaiproject.metaobj.shared.model.Id;
import org.sakaiproject.metaobj.shared.model.Agent;
import org.sakaiproject.metaobj.security.AuthenticationManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;
import java.util.Collection;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: May 19, 2004
 * Time: 4:55:05 PM
 * To change this template use File | Settings | File Templates.
 * @jira OSP-323 PostgreSQL Table Creation
 */
public class SimpleAuthorizationFacade extends HibernateDaoSupport implements AuthorizationFacade {

   private AuthenticationManager authManager = null;

   public void checkPermission(String function, Id id) throws AuthorizationFailedException {
      if (!isAuthorized(function, id)) {
         throw new AuthorizationFailedException(function, id);
      }
   }

   public void checkPermission(Agent agent, String function, Id id) throws AuthorizationFailedException {
      if (!isAuthorized(agent, function, id)) {
         throw new AuthorizationFailedException(agent, function, id);
      }
   }

   /**
    * @param function
    * @param id
    * @return
    */
   public boolean isAuthorized(String function, Id id) {
      return isAuthorized(getAuthManager().getAgent(), function, id);
   }

   /**
    * @param agent
    * @param function
    * @param id
    * @return
    */
   public boolean isAuthorized(Agent agent, String function, Id id) {

      return (getAuthorization(agent, function, id) != null);

   }

   /**
    * @jira OSP-323 PostgreSQL Table Creation
    */
   protected Authorization getAuthorization(Agent agent, String function, Id id) {
      try {
         getHibernateTemplate().setCacheQueries(true);
         return (Authorization) safePopList(getHibernateTemplate().findByNamedQuery("getAuthorization",
            new Object[]{agent.getId().getValue(), function, id.getValue()}));
      } catch (HibernateObjectRetrievalFailureException e) {
         logger.error("",e);
         throw new OspException(e);
      }
   }

   protected Object safePopList(List list) {
      if (list == null) return null;
      if (list.size() == 0) return null;
      return list.get(0);
   }

   /**
    * at least one param must be non-null
    *
    * @param agent
    * @param function
    * @param id
    * @return
    */
   public List getAuthorizations(Agent agent, String function, Id id) {
      List returned = null;

      if (agent != null && function != null && id != null) {
         returned = new ArrayList();
         Authorization authz = getAuthorization(agent, function, id);

         if (authz != null) {
            returned.add(authz);
         }
      }
      // agent stuff
      else if (agent != null && function != null && id == null) {
         returned = findByAgentFunction(agent, function);
      } else if (agent != null && function == null && id != null) {
         returned = findByAgentId(agent, id);
      } else if (agent != null && function == null && id == null) {
         returned = findByAgent(agent);
      }
      // function
      else if (agent == null && function != null && id != null) {
         returned = findByFunctionId(function, id);
      } else if (agent == null && function != null && id == null) {
         returned = findByFunction(function);
      }
      // id
      else if (agent == null && function == null && id != null) {
         returned = findById(id);
      }

      return correctList(returned);
   }

   protected List correctList(List returned) {
      for (Iterator i=returned.iterator();i.hasNext();) {
         Authorization authz = (Authorization)i.next();
         if (authz.getAgent() == null) {
            i.remove();
         }
      }
      return returned;
   }

   /**
    * @jira OSP-323 PostgreSQL Table Creation
    */
   protected List findById(Id id) {
      getHibernateTemplate().setCacheQueries(true);
      return getHibernateTemplate().findByNamedQuery("byId",
         new Object[]{id.getValue()});
   }

   /**
    * @jira OSP-323 PostgreSQL Table Creation
    */
   protected List findByFunction(String function) {
      getHibernateTemplate().setCacheQueries(true);
      return getHibernateTemplate().findByNamedQuery("byFunction",
         new Object[]{function});
   }

   /**
    * @jira OSP-323 PostgreSQL Table Creation
    */
   protected List findByFunctionId(String function, Id id) {
      getHibernateTemplate().setCacheQueries(true);
      return getHibernateTemplate().findByNamedQuery("byFunctionAndId",
         new Object[]{function, id.getValue()});
   }

   /**
    * @jira OSP-323 PostgreSQL Table Creation
    */
   protected List findByAgent(Agent agent) {
      getHibernateTemplate().setCacheQueries(true);
      return getHibernateTemplate().findByNamedQuery("byAgent",
         new Object[]{agent.getId().getValue()});
   }

   /**
    * @jira OSP-323 PostgreSQL Table Creation
    */
   protected List findByAgentId(Agent agent, Id id) {
      getHibernateTemplate().setCacheQueries(true);
      return getHibernateTemplate().findByNamedQuery("byAgentAndId",
         new Object[]{agent.getId().getValue(), id.getValue()});
   }

   /**
    * @jira OSP-323 PostgreSQL Table Creation
    */
   protected List findByAgentFunction(Agent agent, String function) {
      getHibernateTemplate().setCacheQueries(true);
      return getHibernateTemplate().findByNamedQuery("byAgentAndFunction",
         new Object[]{agent.getId().getValue(), function});
   }


   /**
    * @param agent
    * @param function
    * @param id
    */
   public void createAuthorization(Agent agent, String function, Id id) {
      Authorization auth = getAuthorization(agent, function, id);
      if (auth == null) {
         auth = new Authorization(agent, function, id);
      }

      getHibernateTemplate().saveOrUpdate(auth);
   }

   public void deleteAuthorization(Agent agent, String function, Id id) {
      Authorization auth = getAuthorization(agent, function, id);
      if (auth != null) {
         getHibernateTemplate().delete(auth);
      }
   }

   public void deleteAuthorizations(Id qualifier) {
      getHibernateTemplate().deleteAll(findById(qualifier));
   }

   public void pushAuthzGroups(Collection authzGroups) {
      // does nothing... this impl does not care about groups
   }

   public void pushAuthzGroups(String siteId) {
      // does nothing... this impl does not care about groups
   }

   public AuthenticationManager getAuthManager() {
      return authManager;
   }

   public void setAuthManager(AuthenticationManager authManager) {
      this.authManager = authManager;
   }
}

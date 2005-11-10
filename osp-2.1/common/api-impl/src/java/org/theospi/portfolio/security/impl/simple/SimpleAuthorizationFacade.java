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

 * $Header: /opt/CVS/osp2.x/common/api-impl/src/java/org/theospi/portfolio/security/impl/simple/SimpleAuthorizationFacade.java,v 1.1.1.1 2005/07/14 15:53:42 jellis Exp $

 * $Revision$

 * $Date$

 */
package org.theospi.portfolio.security.impl.simple;

import org.springframework.orm.hibernate.HibernateObjectRetrievalFailureException;
import org.springframework.orm.hibernate.support.HibernateDaoSupport;
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

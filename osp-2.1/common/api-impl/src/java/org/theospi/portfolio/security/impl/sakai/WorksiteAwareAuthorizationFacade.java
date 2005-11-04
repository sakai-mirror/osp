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

 * $Header: /opt/CVS/osp2.x/common/api-impl/src/java/org/theospi/portfolio/security/impl/sakai/WorksiteAwareAuthorizationFacade.java,v 1.2 2005/07/18 16:57:26 jellis Exp $

 * $Revision$

 * $Date$

 */
package org.theospi.portfolio.security.impl.sakai;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.metaobj.security.impl.sakai.SecurityBase;
import org.sakaiproject.metaobj.shared.mgt.AgentManager;
import org.sakaiproject.metaobj.shared.model.Agent;
import org.sakaiproject.metaobj.shared.model.Id;
import org.theospi.portfolio.security.Authorization;
import org.theospi.portfolio.security.impl.simple.SimpleAuthorizationFacade;

import java.util.*;

public class WorksiteAwareAuthorizationFacade extends SimpleAuthorizationFacade {
   protected final transient Log logger = LogFactory.getLog(getClass());

   private AgentManager agentManager = null;
   private SecurityBase sakaiSecurityBase;

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

   protected Authorization getAuthorization(Agent agent, String function, Id id, boolean includeRoles) {
      if (includeRoles){
         Set roles = getAgentRoles(agent);

         for (Iterator i=roles.iterator();i.hasNext();) {
            Agent roleAgent = (Agent)i.next();
            if (roleAgent != null) {
               Authorization authz = getAuthorization(roleAgent, function, id);
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
      agentRoles.addAll(agent.getWorksiteRoles());
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
}

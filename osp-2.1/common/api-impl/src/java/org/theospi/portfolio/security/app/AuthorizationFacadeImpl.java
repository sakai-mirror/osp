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
 * $Header: /opt/CVS/osp2.x/common/api-impl/src/java/org/theospi/portfolio/security/app/AuthorizationFacadeImpl.java,v 1.2 2005/07/21 23:05:31 jellis Exp $
 * $Revision$
 * $Date$
 */
package org.theospi.portfolio.security.app;

import org.theospi.portfolio.security.AuthorizationFacade;
import org.theospi.portfolio.security.AuthorizationFailedException;
import org.sakaiproject.metaobj.shared.model.Id;
import org.sakaiproject.metaobj.shared.model.Agent;
import org.sakaiproject.metaobj.security.AuthenticationManager;

import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: May 19, 2004
 * Time: 4:31:35 PM
 * To change this template use File | Settings | File Templates.
 */
public class AuthorizationFacadeImpl implements AuthorizationFacade, AppAuthFacade {

   protected final org.apache.commons.logging.Log logger = org.apache.commons.logging.LogFactory
     .getLog(getClass());

   private AuthenticationManager authManager = null;
   private AuthorizationFacade explicitAuthz = null;

   private Map authorizorMap=new HashMap();

   /**
    * order needs to be maintained here.
    */
   private List applicationAuthorizers = new ArrayList();

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
      return isAuthorized(authManager.getAgent(), function, id);
   }

   /**
    * Builds and caches an ordered list of all ApplicationAuthorizors that consume a given function.
    * @param function - function
    * @return List - of pertinent ApplicationAuthorizors
    */
   protected synchronized List registerFunction(String function) {
      if (logger.isDebugEnabled()) {
         logger.debug("registerFunction("+function+")");
      }

      List result=new ArrayList();

      for (Iterator i = getApplicationAuthorizers().iterator(); i.hasNext();) {
         OrderedAuthorizer appAuth = (OrderedAuthorizer)i.next();
         if (appAuth.getAuthorizer().getFunctions().contains(function)) {
            if (logger.isDebugEnabled()) {
               logger.debug("registerFunction: adding "+appAuth.getClass().getName()+")");
            }
            result.add(appAuth.getAuthorizer());
         }
      }
      authorizorMap.put(function,result);
      return result;
   }

   /**
    * @param agent
    * @param function
    * @param id
    * @return
    */
   public boolean isAuthorized(Agent agent, String function, Id id) {

      if (logger.isDebugEnabled()) {
         logger.debug("isAuthorized("+agent+","+function+","+id+")");
      }

      List appAuthz=(List) authorizorMap.get(function);

      if (appAuthz==null) {
         synchronized(authorizorMap) {
            appAuthz=(List) authorizorMap.get(function);
            if (appAuthz==null) {
               appAuthz=registerFunction(function);
            }
         }
      }

      for (Iterator i = appAuthz.iterator(); i.hasNext();) {
         ApplicationAuthorizer appAuth =
            (ApplicationAuthorizer) i.next();

         if (logger.isDebugEnabled()) {
            logger.debug("isAuthorized() is calling: "+appAuth.getClass().getName());
         }
         Boolean auth = appAuth.isAuthorized(getExplicitAuthz(), agent, function, id);

         if (auth != null) {
            return auth.booleanValue();
         }
      }

      // fall through to explicit authorization,. no application is aware
      // of this request.
      return getExplicitAuthz().isAuthorized(agent, function, id);
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
      return getExplicitAuthz().getAuthorizations(agent, function, id);
   }

   /**
    * @param agent
    * @param function
    * @param id
    */
   public void createAuthorization(Agent agent, String function, Id id) {
      getExplicitAuthz().createAuthorization(agent, function, id);
   }

   public void deleteAuthorization(Agent agent, String function, Id id) {
      getExplicitAuthz().deleteAuthorization(agent, function, id);
   }

   public void deleteAuthorizations(Id qualifier) {
      getExplicitAuthz().deleteAuthorizations(qualifier);
   }

   public AuthenticationManager getAuthManager() {
      return authManager;
   }

   public void setAuthManager(AuthenticationManager authManager) {
      this.authManager = authManager;
   }

   public AuthorizationFacade getExplicitAuthz() {
      return explicitAuthz;
   }

   public void setExplicitAuthz(AuthorizationFacade explicitAuthz) {
      this.explicitAuthz = explicitAuthz;
   }

   public List getApplicationAuthorizers() {
      return applicationAuthorizers;
   }

   public void setApplicationAuthorizers(List applicationAuthorizers) {
      this.applicationAuthorizers = applicationAuthorizers;
   }

   public void addAppAuthorizers(List appAuthorizers) {
      SortedSet sorted = new TreeSet();
      sorted.addAll(getApplicationAuthorizers());
      sorted.addAll(appAuthorizers);
      setApplicationAuthorizers(new ArrayList(sorted));
   }

}

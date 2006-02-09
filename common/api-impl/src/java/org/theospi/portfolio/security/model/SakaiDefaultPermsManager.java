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
package org.theospi.portfolio.security.model;

import org.sakaiproject.api.kernel.function.FunctionManager;
import org.sakaiproject.api.kernel.session.cover.SessionManager;
import org.sakaiproject.exception.IdUnusedException;
import org.sakaiproject.exception.PermissionException;
import org.sakaiproject.service.legacy.authzGroup.AuthzGroup;
import org.sakaiproject.service.legacy.authzGroup.AuthzGroupService;
import org.sakaiproject.service.legacy.authzGroup.Role;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Feb 8, 2006
 * Time: 4:04:47 PM
 * To change this template use File | Settings | File Templates.
 */
public class SakaiDefaultPermsManager {

   private Map defaultPermissions;
   private List functions;
   private FunctionManager functionManager;
   private AuthzGroupService authzGroupService;
   private String prefix;

   public void init() {
      // need to register functions... set defaults on the ones that are not there
      org.sakaiproject.api.kernel.session.Session sakaiSession = SessionManager.getCurrentSession();
      String userId = sakaiSession.getUserId();

      try {
         sakaiSession.setUserId("admin");
         sakaiSession.setUserEid("admin");
         List currentFunctions = getFunctionManager().getRegisteredFunctions(getPrefix());

         for (Iterator i=getFunctions().iterator();i.hasNext();) {
            String function = (String) i.next();
            if (currentFunctions.contains(function)) {
               i.remove();
            }
            else {
               getFunctionManager().registerFunction(function);
            }
         }

         // set the defaults for anything in functions
         for (Iterator i=getDefaultPermissions().entrySet().iterator();i.hasNext();){
            Map.Entry entry = (Map.Entry) i.next();
            processRealm((String)entry.getKey(), (Map)entry.getValue());
         }
   } finally {
      sakaiSession.setUserEid(userId);
      sakaiSession.setUserId(userId);
   }

   }

   protected void processRealm(String realm, Map defaultPerms) {
      try {
         AuthzGroup group = getAuthzGroupService().getAuthzGroup(realm);
         for (Iterator i=defaultPerms.entrySet().iterator();i.hasNext();) {
            Map.Entry entry = (Map.Entry) i.next();
            Role role = group.getRole((String) entry.getKey());
            setupRole(role, (List)entry.getValue());
         }
         getAuthzGroupService().save(group);
      }
      catch (IdUnusedException e) {
         throw new RuntimeException(e);
      }
      catch (PermissionException e) {
         throw new RuntimeException(e);
      }
   }

   protected void setupRole(Role role, List functions) {
      for (Iterator i=functions.iterator();i.hasNext();) {
         String func = (String) i.next();
         if (getFunctions().contains(func)) {
            role.allowFunction(func);
         }
      }
   }

   public Map getDefaultPermissions() {
      return defaultPermissions;
   }

   public void setDefaultPermissions(Map defaultPermissions) {
      this.defaultPermissions = defaultPermissions;
   }

   public List getFunctions() {
      return functions;
   }

   public void setFunctions(List functions) {
      this.functions = functions;
   }

   public FunctionManager getFunctionManager() {
      return functionManager;
   }

   public void setFunctionManager(FunctionManager functionManager) {
      this.functionManager = functionManager;
   }

   public String getPrefix() {
      return prefix;
   }

   public void setPrefix(String prefix) {
      this.prefix = prefix;
   }

   public AuthzGroupService getAuthzGroupService() {
      return authzGroupService;
   }

   public void setAuthzGroupService(AuthzGroupService authzGroupService) {
      this.authzGroupService = authzGroupService;
   }
}

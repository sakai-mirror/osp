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
package org.theospi.portfolio.security.control;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.metaobj.utils.mvc.impl.servlet.AbstractFormController;
import org.sakaiproject.metaobj.utils.mvc.intf.FormController;
import org.sakaiproject.metaobj.utils.mvc.intf.LoadObjectController;
import org.sakaiproject.tool.cover.ToolManager;

import org.springframework.validation.Errors;
import org.springframework.web.servlet.ModelAndView;
import org.theospi.portfolio.security.mgt.PermissionManager;
import org.theospi.portfolio.security.model.PermissionsEdit;

import java.util.Hashtable;
import java.util.Map;

public class PermissionsController extends AbstractFormController implements FormController, LoadObjectController {
   protected final transient Log logger = LogFactory.getLog(getClass());

   private PermissionManager permissionManager;

   /**
    * Create a map of all data the form requries.
    * Useful for building up drop down lists, etc.
    *
    * @param request
    * @param command
    * @param errors
    * @return Map
    */
   public Map referenceData(Map request, Object command, Errors errors) {
      Map model = new Hashtable();

      PermissionsEdit edit = (PermissionsEdit)command;
      model.put("toolFunctions", getPermissionManager().getAppFunctions(edit));
      model.put("roles", getPermissionManager().getWorksiteRoles(edit));

      if (request.get("message") != null) {
         model.put("message", request.get("message"));
      }

      return model;
   }

   public ModelAndView processCancel(Map request, Map session, Map application,
                                     Object command, Errors errors) throws Exception {
      return new ModelAndView("helperDone");
   }

   public Object fillBackingObject(Object incomingModel, Map request, Map session, Map application) throws Exception {
      PermissionsEdit edit = (PermissionsEdit)incomingModel;
      edit.setSiteId(ToolManager.getCurrentPlacement().getContext());
      return getPermissionManager().fillPermissions(edit);
   }

   public ModelAndView handleRequest(Object requestModel, Map request, Map session, Map application, Errors errors) {
      PermissionsEdit edit = (PermissionsEdit)requestModel;
      getPermissionManager().updatePermissions(edit);
      return new ModelAndView("helperDone");
   }

   public PermissionManager getPermissionManager() {
      return permissionManager;
   }

   public void setPermissionManager(PermissionManager permissionManager) {
      this.permissionManager = permissionManager;
   }
}

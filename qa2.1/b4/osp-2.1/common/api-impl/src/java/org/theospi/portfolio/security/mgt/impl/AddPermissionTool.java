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
package org.theospi.portfolio.security.mgt.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.theospi.portfolio.security.mgt.PermissionManager;

import java.util.Map;

public class AddPermissionTool {
   protected final transient Log logger = LogFactory.getLog(getClass());

   private PermissionManager manager;
   private Map additionalTools;

   public void init() {
      manager.addTools(getAdditionalTools());
   }

   public Map getAdditionalTools() {
      return additionalTools;
   }

   public void setAdditionalTools(Map additionalTools) {
      this.additionalTools = additionalTools;
   }

   public PermissionManager getManager() {
      return manager;
   }

   public void setManager(PermissionManager manager) {
      this.manager = manager;
   }
}

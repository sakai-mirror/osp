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
package org.theospi.portfolio.security.mgt;

import org.sakaiproject.site.api.ToolConfiguration;
import org.theospi.portfolio.security.model.PermissionsEdit;

import java.util.List;

public interface ToolPermissionManager {

   /**
    * Get a list of functions that this tool is interested in setting.
    * This list should be in some reasonable order (read to delete, etc).
    * @param edit contains information about the permissions edit such as
    * qualifier, etc.
    * @return list of strings that name the functions in some reasonable order
    */
   public List getFunctions(PermissionsEdit edit);

   /**
    * This method is called to see if the qualifier being edited
    * has some parent qualifiers that imply permissions for this qualifier.
    * One example might be a directory that has implied permissions of
    * the parent directory.  Since the permissions are implied, the
    * set permissions screen will not allow these permissions to be turned off.
    * @param edit contains information about the permissions edit such as
    * qualifier, etc.
    * @return list of Id objects that are parents of the passed in qualifier.
    */
   public List getReadOnlyQualifiers(PermissionsEdit edit);

   public void duplicatePermissions(ToolConfiguration fromTool, ToolConfiguration toTool);

}

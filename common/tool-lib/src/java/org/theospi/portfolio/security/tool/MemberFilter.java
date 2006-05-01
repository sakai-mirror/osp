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
package org.theospi.portfolio.security.tool;

import org.sakaiproject.authz.api.Member;

import java.util.List;
import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Nov 18, 2005
 * Time: 6:11:54 PM
 * To change this template use File | Settings | File Templates.
 */
public class MemberFilter {

   private List roleList;
   private List groupList;

   public MemberFilter(AudienceTool tool) {
      roleList = new ArrayList();

      List selectedRoles = tool.getSelectedRolesFilter();
      if (selectedRoles != null) {
         roleList.addAll(selectedRoles);
         roleList.remove("");
      }
   }

   public boolean includeMember(DecoratedMember user) {
      if (roleList.size() == 0) {
         return true;
      }
      if (roleList.contains(user.getRole().getId().getValue())) {
         return true;
      }

      return false;
   }

   public List getRoleList() {
      return roleList;
   }

   public void setRoleList(List roleList) {
      this.roleList = roleList;
   }

   public List getGroupList() {
      return groupList;
   }

   public void setGroupList(List groupList) {
      this.groupList = groupList;
   }
}

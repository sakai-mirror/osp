/*
 * Copyright (c) 2005 Your Corporation. All Rights Reserved.
 */
package org.theospi.portfolio.security.tool;

import org.sakaiproject.service.legacy.authzGroup.Member;

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

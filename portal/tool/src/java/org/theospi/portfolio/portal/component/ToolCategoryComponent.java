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
package org.theospi.portfolio.portal.component;

import org.theospi.portfolio.portal.model.ToolCategory;
import org.theospi.portfolio.portal.model.SiteType;
import org.sakaiproject.authz.api.Role;

import javax.faces.component.UINamingContainer;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Feb 16, 2006
 * Time: 9:33:04 PM
 * To change this template use File | Settings | File Templates.
 */
public class ToolCategoryComponent extends UINamingContainer {

   public static final String COMPONENT_TYPE = "org.theospi.portfolio.portal.component.ToolCategoryComponent";
   private String context;
   private ToolCategory toolCategory;
   private SiteType siteType;
   private String siteId;
   private Role currentRole;

   public ToolCategory getToolCategory() {
      return toolCategory;
   }

   public void setToolCategory(ToolCategory toolCategory) {
      this.toolCategory = toolCategory;
   }

   public SiteType getSiteType() {
      return siteType;
   }

   public void setSiteType(SiteType siteType) {
      this.siteType = siteType;
   }

   public String getContext() {
      return context;
   }

   public void setContext(String context) {
      this.context = context;
   }

   public String getSiteId() {
      return siteId;
   }

   public void setSiteId(String siteId) {
      this.siteId = siteId;
   }

   public Role getCurrentRole() {
      return currentRole;
   }

   public void setCurrentRole(Role currentRole) {
      this.currentRole = currentRole;
   }
}

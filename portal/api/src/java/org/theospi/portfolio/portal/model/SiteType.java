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
package org.theospi.portfolio.portal.model;

import java.util.Map;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Feb 11, 2006
 * Time: 2:31:03 PM
 * To change this template use File | Settings | File Templates.
 */
public class SiteType implements Comparable {

   private String key;
   private String skin;
   private int order;
   private int firstCategory = 0;
   private int lastCategory = 0;
   private List toolCategories;
   public static final SiteType OTHER = new SiteType("org.theospi.portfolio.portal.other", Integer.MAX_VALUE);
   public static final SiteType MY_WORKSPACE = new SiteType("org.theospi.portfolio.portal.myWorkspace", 0);
   public static final SiteType GATEWAY = new SiteType("org.theospi.portfolio.portal.gateway", 0);

   public SiteType() {
   }

   public SiteType(String key, int order) {
      this.key = key;
      this.order = order;
   }

   public String getKey() {
      return key;
   }

   public void setKey(String key) {
      this.key = key;
   }

   public int getOrder() {
      return order;
   }

   public void setOrder(int order) {
      this.order = order;
   }

   public String getSkin() {
      return skin;
   }

   public void setSkin(String skin) {
      this.skin = skin;
   }

   public int compareTo(Object o) {
      Integer order = new Integer(getOrder());
      Integer other = new Integer(((SiteType)o).getOrder());
      return order.compareTo(other);
   }

   public List getToolCategories() {
      return toolCategories;
   }

   public void setToolCategories(List toolCategories) {
      this.toolCategories = toolCategories;
   }

   public int getFirstCategory() {
      return firstCategory;
   }

   public void setFirstCategory(int firstCategory) {
      this.firstCategory = firstCategory;
   }

   public int getLastCategory() {
      return lastCategory;
   }

   public void setLastCategory(int lastCategory) {
      this.lastCategory = lastCategory;
   }

}

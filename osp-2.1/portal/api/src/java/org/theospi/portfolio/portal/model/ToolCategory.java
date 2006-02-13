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

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Feb 11, 2006
 * Time: 8:24:08 AM
 * To change this template use File | Settings | File Templates.
 */
public class ToolCategory implements Comparable {

   public static final String UNCATEGORIZED_KEY = "org.theospi.portfolio.portal.model.ToolCategory.uncategorized";

   public static final ToolCategory UNCATEGORIZED = new ToolCategory(UNCATEGORIZED_KEY);

   private String key;
   private String description;
   private int order;

   public ToolCategory() {
   }

   protected ToolCategory(String key) {
      this.key = key;
   }

   public String getKey() {
      return key;
   }

   public void setKey(String key) {
      this.key = key;
   }

   public String getDescription() {
      return description;
   }

   public void setDescription(String description) {
      this.description = description;
   }

   public int getOrder() {
      return order;
   }

   public void setOrder(int order) {
      this.order = order;
   }

   public int compareTo(Object o) {
      Integer order = new Integer(getOrder());
      Integer other = new Integer(((ToolCategory)o).getOrder());
      return order.compareTo(other);
   }

}

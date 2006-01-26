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
package org.theospi.portfolio.warehouse.impl;

import org.theospi.portfolio.warehouse.intf.ChildWarehouseTask;
import org.theospi.portfolio.warehouse.intf.PropertyAccess;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Nov 30, 2005
 * Time: 5:10:37 PM
 * To change this template use File | Settings | File Templates.
 */
public class ChildFieldWrapper {

   private ChildWarehouseTask task;
   private PropertyAccess propertyAccess;

   public ChildWarehouseTask getTask() {
      return task;
   }

   public void setTask(ChildWarehouseTask task) {
      this.task = task;
   }

   public PropertyAccess getPropertyAccess() {
      return propertyAccess;
   }

   public void setPropertyAccess(PropertyAccess propertyAccess) {
      this.propertyAccess = propertyAccess;
   }
}

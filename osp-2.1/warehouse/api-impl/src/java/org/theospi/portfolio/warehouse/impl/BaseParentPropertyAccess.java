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

import org.theospi.portfolio.warehouse.intf.ParentPropertyAccess;
import org.theospi.portfolio.warehouse.intf.PropertyAccess;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Dec 2, 2005
 * Time: 4:49:36 PM
 * To change this template use File | Settings | File Templates.
 */
public class BaseParentPropertyAccess implements ParentPropertyAccess {

   private PropertyAccess base = null;

   public Object getPropertyValue(Object parent, Object source) throws Exception {
      return base.getPropertyValue(parent);
   }

   public PropertyAccess getBase() {
      return base;
   }

   public void setBase(PropertyAccess base) {
      this.base = base;
   }
}

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

import org.theospi.portfolio.warehouse.intf.PropertyAccess;

import java.lang.reflect.Method;
import java.beans.PropertyDescriptor;
import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.IntrospectionException;
import java.util.Iterator;
import java.util.Map;
import java.util.Hashtable;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Nov 30, 2005
 * Time: 5:34:24 PM
 * To change this template use File | Settings | File Templates.
 */
public class BeanPropertyAccess implements PropertyAccess {

   private Map gettorMap = new Hashtable();
   private String propertyName;

   public Object getPropertyValue(Object source) throws Exception {
      Method objectMethodGetProperty = getPropertyGettor(source);
      if(objectMethodGetProperty == null)
         throw new NullPointerException(source.getClass().getName() + 
               " has no get for property \"" + propertyName + "\"");
      return objectMethodGetProperty.invoke(source, new Object[]{});
   }

   public String getPropertyName() {
      return propertyName;
   }

   public void setPropertyName(String propertyName) {
      this.propertyName = propertyName;
   }

   public Method getPropertyGettor(Object source) throws IntrospectionException {
      Method propertyGettor = (Method) gettorMap.get(source.getClass());
      if (propertyGettor == null) {
         BeanInfo info = Introspector.getBeanInfo(source.getClass());

         PropertyDescriptor[] descriptors = info.getPropertyDescriptors();

         for (int i=0;i<descriptors.length;i++) {
            if (descriptors[i].getName().equals(getPropertyName())) {
               propertyGettor = descriptors[i].getReadMethod();
               gettorMap.put(source.getClass(), propertyGettor);
               break;
            }
         }
      }
      return propertyGettor;
   }

}

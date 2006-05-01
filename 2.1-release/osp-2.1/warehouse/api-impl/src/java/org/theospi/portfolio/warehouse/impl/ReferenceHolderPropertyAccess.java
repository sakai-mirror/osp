/**********************************************************************************
* $URL: https://source.sakaiproject.org/svn/trunk/osp/osp-2.1/warehouse/api-impl/src/java/org/theospi/portfolio/warehouse/impl/EntityPropertyAccess.java $
* $Id: EntityPropertyAccess.java 5557 2006-01-26 06:02:52Z john.ellis@rsmart.com $
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

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.Hashtable;
import java.util.Map;

import org.theospi.portfolio.warehouse.intf.PropertyAccess;
import org.sakaiproject.metaobj.shared.mgt.ReferenceHolder;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Nov 30, 2005
 * Time: 5:48:56 PM
 * To change this template use File | Settings | File Templates.
 */
public class ReferenceHolderPropertyAccess implements PropertyAccess {

   private Map gettorMap = new Hashtable();
   private String propertyName;

   public Object getPropertyValue(Object source) throws Exception {
      Method objectMethodGetProperty = getPropertyGettor(source);
      if(objectMethodGetProperty == null)
         throw new NullPointerException(source.getClass().getName() + 
               " has no get for property \"" + propertyName + "\"");
      Object value = objectMethodGetProperty.invoke(source, new Object[]{});

      ReferenceHolder refHolder = null;
      
      try {
         refHolder = (ReferenceHolder)value;
      } catch(ClassCastException e) {
         throw new Exception("The source could not be cast into an ReferenceHolder for property \"" + propertyName +  "\"", e);
      }
      
      return refHolder.getBase().getId();
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

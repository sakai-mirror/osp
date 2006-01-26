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
package org.theospi.portfolio.warehouse.sakai.resource;

import org.theospi.portfolio.warehouse.intf.PropertyAccess;
import org.sakaiproject.service.legacy.content.ContentResource;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Dec 19, 2005
 * Time: 10:42:06 PM
 * To change this template use File | Settings | File Templates.
 */
public class ResourceTypePropertyAccess implements PropertyAccess {

   private boolean subType = false;

   public Object getPropertyValue(Object source) throws Exception {
      ContentResource resource = (ContentResource) source;

      String propName = resource.getProperties().getNamePropStructObjType();
      String saType = resource.getProperties().getProperty(propName);
      if (saType != null) {
         if (subType) {
            return saType;
         }
         else {
            return "form";
         }
      }
      else {
         return "fileArtifact";
      }
   }

   public boolean isSubType() {
      return subType;
   }

   public void setSubType(boolean subType) {
      this.subType = subType;
   }
}

/**********************************************************************************
* $URL: https://source.sakaiproject.org/svn/trunk/sakai/legacy/component/src/java/org/sakaiproject/component/framework/email/TestEmailService.java $
* $Id: TestEmailService.java 3831 2005-11-14 20:17:24Z ggolden@umich.edu $
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
package org.theospi.portfolio.shared.tool;

import org.sakaiproject.service.legacy.content.ContentResourceFilter;
import org.sakaiproject.service.legacy.content.ContentResource;
import org.sakaiproject.service.legacy.entity.ResourceProperties;

import java.util.ArrayList;
import java.util.List;

/**
 * This class implements the typical form type filter.
 * This will be a registered bean with the component manager that
 * application components can extend to control the list of forms.
 */
public class BaseFormResourceFilter implements ContentResourceFilter {

   private boolean viewAll = true;
   private List formTypes = new ArrayList();

   public boolean allowSelect(ContentResource resource) {
      String formType = resource.getProperties().getProperty(ResourceProperties.PROP_STRUCTOBJ_TYPE);
      
      if (getFormTypes().contains(formType)) {
         return true;
      }
      else { 
         return false;
      }
   }

   public boolean allowView(ContentResource contentResource) {
      if (isViewAll()) {
         return true;
      }

      return allowSelect(contentResource);
   }

   public List getFormTypes() {
      return formTypes;
   }

   /**
    * The list of form types to allow.  The passed in content resource
    * will be tested to see if the resouce's type is included in the
    * list.
    * @param formTypes
    */
   public void setFormTypes(List formTypes) {
      this.formTypes = formTypes;
   }

   public boolean isViewAll() {
      return viewAll;
   }

   /**
    * boolean to indicate if all resources should be viewable.
    *
    * If this is false, then the viewable resources will be based on the
    * mime types and extention set in the other properties.
    * @param viewAll
    */
   public void setViewAll(boolean viewAll) {
      this.viewAll = viewAll;
   }
}

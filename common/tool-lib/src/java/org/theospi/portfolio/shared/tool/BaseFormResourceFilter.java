/**********************************************************************************
* $URL: https://source.sakaiproject.org/svn/trunk/sakai/legacy/component/src/java/org/sakaiproject/component/legacy/content/BaseExtensionResourceFilter.java $
* $Id: BaseExtensionResourceFilter.java 4255 2005-11-29 23:58:17Z john.ellis@rsmart.com $
***********************************************************************************
*
* Copyright (c) 2003, 2004 The Regents of the University of Michigan, Trustees of Indiana University,
*                  Board of Trustees of the Leland Stanford, Jr., University, and The MIT Corporation
*
* Licensed under the Educational Community License Version 1.0 (the "License");
* By obtaining, using and/or copying this Original Work, you agree that you have read,
* understand, and will comply with the terms and conditions of the Educational Community License.
* You may obtain a copy of the License at:
*
*      http://cvs.sakaiproject.org/licenses/license_1_0.html
*
* THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
* INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE
* AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
* DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
* FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
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

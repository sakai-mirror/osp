/**********************************************************************************
* $URL$
* $Id$
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

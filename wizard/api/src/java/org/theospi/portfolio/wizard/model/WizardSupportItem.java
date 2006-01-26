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
package org.theospi.portfolio.wizard.model;

import org.sakaiproject.metaobj.shared.model.Id;
import org.sakaiproject.metaobj.shared.model.IdentifiableObject;

public class WizardSupportItem extends IdentifiableObject {
   
   private Wizard wizard;
   private Id item;
   private String genericType;
   private String contentType;
  
   public WizardSupportItem() {}
   
   public WizardSupportItem(Id id, Id itemId, String genericType, String contentType, Wizard wizard) {
      setId(id);
      this.item = itemId;
      this.genericType = genericType;
      this.contentType = contentType;
      this.wizard = wizard;
   }
   
   public String getContentType() {
      return contentType;
   }
   public void setContentType(String contentType) {
      this.contentType = contentType;
   }
   public String getGenericType() {
      return genericType;
   }
   public void setGenericType(String genericType) {
      this.genericType = genericType;
   }
   public Id getItem() {
      return item;
   }
   public void setItem(Id item) {
      this.item = item;
   }
   public Wizard getWizard() {
      return wizard;
   }
   public void setWizard(Wizard wizard) {
      this.wizard = wizard;
   }

   public boolean equals(Object in) {
      if (in == null && this == null) return true;
      if (in == null && this != null) return false;
      if (this == null && in != null) return false;
      if (!this.getClass().isAssignableFrom(in.getClass())) return false;
      if (this.getContentType().equals(((WizardSupportItem) in).getContentType()) &&
            this.getGenericType().equals(((WizardSupportItem) in).getGenericType()) &&
            this.getItem().getValue().equals(((WizardSupportItem) in).getItem().getValue())) return true;
      if (this.getId() == null || ((WizardSupportItem) in).getId() == null) return false;
      return this.getId().equals(((WizardSupportItem) in).getId());
   }
   

}

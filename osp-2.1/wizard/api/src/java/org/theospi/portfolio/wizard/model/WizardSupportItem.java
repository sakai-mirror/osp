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

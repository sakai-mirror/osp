package org.theospi.portfolio.workflow.model;

import java.util.HashSet;
import java.util.Set;

import org.sakaiproject.metaobj.shared.model.IdentifiableObject;
import org.theospi.portfolio.matrix.model.WizardPageDefinition;

public class Workflow extends IdentifiableObject {

   private String title;
   private Set items = new HashSet();
   private boolean newObject = false;
   private WizardPageDefinition wizardPageDefinition;
   
   public Workflow() {;}
   
   public Workflow(String title, WizardPageDefinition wizardPageDefinition) {
      this.title = title;
      this.wizardPageDefinition = wizardPageDefinition;
   }
   
   /**
    * @return Returns the title.
    */
   public String getTitle() {
      return title;
   }
   /**
    * @param title The title to set.
    */
   public void setTitle(String title) {
      this.title = title;
   }
   /**
    * @return Returns the items.
    */
   public Set getItems() {
      return items;
   }
   /**
    * @param items The items to set.
    */
   public void setItems(Set items) {
      this.items = items;
   }
   /**
    * @return Returns the newObject.
    */
   public boolean isNewObject() {
      return newObject;
   }
   /**
    * @param newObject The newObject to set.
    */
   public void setNewObject(boolean newObject) {
      this.newObject = newObject;
   }
   
   public void add(WorkflowItem item) {
      item.setWorkflow(this);
      getItems().add(item);
   }

   /* (non-Javadoc)
    * @see org.sakaiproject.metaobj.shared.model.IdentifiableObject#equals(java.lang.Object)
    */
   public boolean equals(Object in) {
      // TODO Auto-generated method stub
      //return super.equals(in);
      
      if (this == in) return true;
      if (in == null && this == null) return true;
      if (in == null && this != null) return false;
      if (this == null && in != null) return false;
      if (this.getId() == null && ((Workflow)in).getId() != null) return false;
      if (this.getId() != null && ((Workflow)in).getId() == null) return false;
      if (this.getId() == null && ((Workflow)in).getId() == null && 
            !this.getTitle().equals(((Workflow)in).getTitle())) return false;
      return this.getId().equals(((Workflow)in).getId());
      
   }

   /**
    * @return Returns the wizardPageDefinition.
    */
   public WizardPageDefinition getWizardPageDefinition() {
      return wizardPageDefinition;
   }

   /**
    * @param wizardPageDefinition The wizardPageDefinition to set.
    */
   public void setWizardPageDefinition(WizardPageDefinition wizardPageDefinition) {
      this.wizardPageDefinition = wizardPageDefinition;
   }
   
   
   
}

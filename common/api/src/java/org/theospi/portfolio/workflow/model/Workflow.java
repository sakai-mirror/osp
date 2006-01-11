package org.theospi.portfolio.workflow.model;

import java.util.List;

import org.sakaiproject.metaobj.shared.model.IdentifiableObject;

public class Workflow extends IdentifiableObject {

   private String title;
   private List items;
   private boolean newObject = false;
   
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
   public List getItems() {
      return items;
   }
   /**
    * @param items The items to set.
    */
   public void setItems(List items) {
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
   
}

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
package org.theospi.portfolio.workflow.model;

import java.util.HashSet;
import java.util.Set;

import org.sakaiproject.metaobj.shared.model.IdentifiableObject;
import org.theospi.portfolio.shared.model.ObjectWithWorkflow;

public class Workflow extends IdentifiableObject {

   private String title;
   private Set items = new HashSet();
   private boolean newObject = false;
   private ObjectWithWorkflow parentObject;
   
   public Workflow() {;}
   
   public Workflow(String title, ObjectWithWorkflow parentObject) {
      this.title = title;
      this.parentObject = parentObject;
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
    * @return Returns the parentObject.
    */
   public ObjectWithWorkflow getParentObject() {
      return parentObject;
   }

   /**
    * @param parentObject The parentObject to set.
    */
   public void setParentObject(ObjectWithWorkflow parentObject) {
      this.parentObject = parentObject;
   }
   
   
   
}

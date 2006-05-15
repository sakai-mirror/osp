/**********************************************************************************
* $URL:https://source.sakaiproject.org/svn/osp/trunk/matrix/api/src/java/org/theospi/portfolio/matrix/model/WizardPage.java $
* $Id:WizardPage.java 9134 2006-05-08 20:28:42Z chmaurer@iupui.edu $
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
package org.theospi.portfolio.matrix.model;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.sakaiproject.metaobj.shared.model.Agent;
import org.sakaiproject.metaobj.shared.model.Id;
import org.sakaiproject.metaobj.shared.model.IdentifiableObject;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Jan 11, 2006
 * Time: 4:14:37 PM
 * To change this template use File | Settings | File Templates.
 */
public class WizardPage extends IdentifiableObject {

   private Set attachments = new HashSet();
   private String status;
   private WizardPageDefinition pageDefinition;
   private Set pageForms = new HashSet();
   private Date modified;
   
   private Agent owner;
   
   public final static String TYPE = "wizard_page_type";
   public final static String PROCESS_TYPE_KEY = "page_id";

   /**
    * Set of class Attachment
    * @return Returns Set of Attachments
    */
   public Set getAttachments() {
      return attachments;
   }

   /**
    * @param attachments A Set of class Attachment.
    */
   public void setAttachments(Set attachments) {
      this.attachments = attachments;
   }

   /**
    * @return Returns the status.
    */
   public String getStatus() {
      return status.toUpperCase();
   }

   /**
    * @param status The status to set.
    */
   public void setStatus(String status) {
      this.status = status.toUpperCase();
   }

   /* (non-Javadoc)
    * @see java.lang.Object#equals(java.lang.Object)
    */
   public boolean equals(Object in) {
      if (this == in) {
         return true;
      }
      if (in == null && this == null) {
         return true;
      }
      if (in == null && this != null) {
         return false;
      }
      if (this == null && in != null) {
         return false;
      }
      if (!this.getClass().isAssignableFrom(in.getClass())) {
         return false;
      }
      if (this.getId() == null && ((IdentifiableObject) in).getId() == null) {
         return false;
      }
      if (this.getId() == null || ((IdentifiableObject) in).getId() == null) {
         return false;
      }
      return this.getId().equals(((IdentifiableObject) in).getId());
   }

   /* (non-Javadoc)
    * @see java.lang.Object#hashCode()
    */
   public int hashCode() {
      //TODO need better hashcode
      Id id = this.getId();
      if (id == null) return 0;
      return id.getValue().hashCode();
   }

   public WizardPageDefinition getPageDefinition() {
      return pageDefinition;
   }

   public void setPageDefinition(WizardPageDefinition pageDefinition) {
      this.pageDefinition = pageDefinition;
   }

   /**
    * @return Returns the Set of class WizardPageForm.
    */
   public Set getPageForms() {
      return pageForms;
   }

   /**
    * @param pageForms A set of class WizardPageForm.
    */
   public void setPageForms(Set pageForms) {
      this.pageForms = pageForms;
   }

   /**
    * @return Returns the modified.
    */
   public Date getModified() {
      return modified;
   }

   /**
    * @param modified The modified to set.
    */
   public void setModified(Date modified) {
      this.modified = modified;
   }

   public Agent getOwner() {
      return owner;
   }

   public void setOwner(Agent owner) {
      this.owner = owner;
   }

}

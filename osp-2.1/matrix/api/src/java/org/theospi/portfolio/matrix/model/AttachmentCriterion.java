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
package org.theospi.portfolio.matrix.model;

import org.sakaiproject.metaobj.shared.model.Id;

/**
 * @author rpembry
 */
public class AttachmentCriterion {
   private Id id;
   private Criterion criterion;
   private Attachment attachment;

   /**
    * @return Returns the criterion.
    */
   public Criterion getCriterion() {
      return criterion;
   }

   /**
    * @param criterion The criterion to set.
    */
   public void setCriterion(Criterion criterion) {
      this.criterion = criterion;
   }

   /**
    * @return Returns the id.
    */
   public Id getId() {
      return id;
   }

   /**
    * @param id The id to set.
    */
   public void setId(Id id) {
      this.id = id;
   }
      
   /**
    * @return Returns the attachment.
    */
   public Attachment getAttachment() {
      return attachment;
   }
   /**
    * @param attachment The attachment to set.
    */
   public void setAttachment(Attachment attachment) {
      this.attachment = attachment;
   }
   
   /* (non-Javadoc)
    * @see java.lang.Object#equals(java.lang.Object)
    */
   public boolean equals(Object other) {
      if (other == this) return true;
      if (other == null || !(other instanceof AttachmentCriterion)) return false;
      //TODO need better equals method
      
      if (this.getId() != null && ((AttachmentCriterion) other).getId() != null) {
         return (this.getId().equals(((AttachmentCriterion) other).getId()));   
      }
      else {
         return (this.getCriterion().equals(((AttachmentCriterion) other).getCriterion()) &&
            this.getAttachment().equals(((AttachmentCriterion) other).getAttachment()));
      }
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

}

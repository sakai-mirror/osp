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
public class WizardPageForm {
   private Id id;
   private Id artifactId;
   private WizardPage wizardPage;
   private String formType;

   /**
    * @return Returns the artifactId.
    */
   public Id getArtifactId() {
      return artifactId;
   }

   /**
    * @param artifactId The artifactId to set.
    */
   public void setArtifactId(Id artifactId) {
      this.artifactId = artifactId;
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
    * @return Returns the wizardPage that contains this Attachment
    */

   public WizardPage getWizardPage() {
      return wizardPage;
   }

   /**
    * @param wizardPage The parent wizardPage for this Attachment
    */
   public void setWizardPage(WizardPage wizardPage) {
      this.wizardPage = wizardPage;
   }
      
   /* (non-Javadoc)
    * @see java.lang.Object#equals(java.lang.Object)
    */
   public boolean equals(Object other) {
      if (other == this) return true;
      if (other == null || !(other instanceof WizardPageForm)) return false;
      //TODO need better equals method
      return (this.getId().equals(((WizardPageForm) other).getId()));

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
/*      
   public String toString() {
      return "<Cell id:" + this.wizardPage.getId() + ", artifactId:" + this.getArtifactId() + "]>";
   }
*/

   /**
    * @return Returns the formType.
    */
   public String getFormType() {
      return formType;
   }

   /**
    * @param formType The formType to set.
    */
   public void setFormType(String formType) {
      this.formType = formType;
   }
}

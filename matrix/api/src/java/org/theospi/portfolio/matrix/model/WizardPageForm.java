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

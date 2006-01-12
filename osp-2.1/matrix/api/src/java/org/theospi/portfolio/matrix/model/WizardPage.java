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

import org.sakaiproject.metaobj.shared.model.IdentifiableObject;
import org.sakaiproject.metaobj.shared.model.Id;

import java.util.Set;
import java.util.HashSet;

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

   /**
    * @return Returns Set of Attachments
    */
   public Set getAttachments() {
      return attachments;
   }

   /**
    * @param attachments A Set of Attachments to set.
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
   public boolean equals(Object other) {
      if (other == this) return true;
      if (other == null || !(other instanceof WizardPage)) return false;
      //TODO need better equals method
      if (this.getId() == null) return false;
      return (this.getId().equals(((WizardPage) other).getId()));

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
    * @return Returns the pageForms.
    */
   public Set getPageForms() {
      return pageForms;
   }

   /**
    * @param pageForms The pageForms to set.
    */
   public void setPageForms(Set pageForms) {
      this.pageForms = pageForms;
   }

}

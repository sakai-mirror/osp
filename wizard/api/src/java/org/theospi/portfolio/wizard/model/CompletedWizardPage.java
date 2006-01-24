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
package org.theospi.portfolio.wizard.model;

import org.sakaiproject.metaobj.shared.model.IdentifiableObject;
import org.theospi.portfolio.matrix.model.WizardPage;

import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Jan 21, 2006
 * Time: 3:20:34 PM
 * To change this template use File | Settings | File Templates.
 */
public class CompletedWizardPage extends IdentifiableObject {

   private CompletedWizardCategory category;
   private WizardPageSequence wizardPageDefinition;
   private WizardPage wizardPage;
   private Date created;
   private Date lastVisited;

   public CompletedWizardPage() {
   }

   public CompletedWizardPage(WizardPageSequence wizardPageDefinition, CompletedWizardCategory category) {
      this.wizardPageDefinition = wizardPageDefinition;
      this.category = category;
      setCreated(new Date());
      setWizardPage(new WizardPage());
      getWizardPage().setPageDefinition(wizardPageDefinition.getWizardPageDefinition());
      getWizardPage().setStatus(wizardPageDefinition.getWizardPageDefinition().getInitialStatus());
   }

   public CompletedWizardCategory getCategory() {
      return category;
   }

   public void setCategory(CompletedWizardCategory category) {
      this.category = category;
   }

   public WizardPageSequence getWizardPageDefinition() {
      return wizardPageDefinition;
   }

   public void setWizardPageDefinition(WizardPageSequence wizardPageDefinition) {
      this.wizardPageDefinition = wizardPageDefinition;
   }

   public WizardPage getWizardPage() {
      return wizardPage;
   }

   public void setWizardPage(WizardPage wizardPage) {
      this.wizardPage = wizardPage;
   }

   public Date getCreated() {
      return created;
   }

   public void setCreated(Date created) {
      this.created = created;
   }

   public Date getLastVisited() {
      return lastVisited;
   }

   public void setLastVisited(Date lastVisited) {
      this.lastVisited = lastVisited;
   }

}

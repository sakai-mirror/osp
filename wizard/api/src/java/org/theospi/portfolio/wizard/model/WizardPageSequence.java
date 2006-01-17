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
import org.theospi.portfolio.matrix.model.WizardPageDefinition;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Jan 12, 2006
 * Time: 4:38:47 PM
 * To change this template use File | Settings | File Templates.
 */
public class WizardPageSequence extends IdentifiableObject {

   private WizardPageDefinition wizardPageDefinition;
   private int sequence;
   private WizardCategory category;
   private String title = null;

   public WizardPageSequence() {

   }

   public WizardPageSequence(WizardPageDefinition wizardPageDefinition) {
      this.wizardPageDefinition = wizardPageDefinition;
   }

   public WizardPageDefinition getWizardPageDefinition() {
      return wizardPageDefinition;
   }

   public void setWizardPageDefinition(WizardPageDefinition wizardPageDefinition) {
      this.wizardPageDefinition = wizardPageDefinition;
   }

   public int getSequence() {
      return sequence;
   }

   public void setSequence(int sequence) {
      this.sequence = sequence;
   }

   public WizardCategory getCategory() {
      return category;
   }

   public void setCategory(WizardCategory category) {
      this.category = category;
   }

   public String getTitle() {
      return title;
   }

   public void setTitle(String title) {
      this.title = title;
   }
}

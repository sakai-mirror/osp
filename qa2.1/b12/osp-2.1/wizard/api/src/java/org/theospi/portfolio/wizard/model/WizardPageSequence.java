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

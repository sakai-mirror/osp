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
import org.sakaiproject.metaobj.shared.model.Agent;

import java.util.Date;
import java.util.Collection;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Jan 21, 2006
 * Time: 3:16:08 PM
 * To change this template use File | Settings | File Templates.
 */
public class CompletedWizard extends IdentifiableObject {

   private Wizard wizard;
   private CompletedWizardCategory rootCategory;
   private Agent owner;
   private Date created;
   private Date lastVisited;

   public CompletedWizard() {
   }

   public CompletedWizard(Wizard wizard, Agent owner) {
      this.wizard = wizard;
      this.owner = owner;
      setCreated(new Date());
      setLastVisited(new Date());
      setRootCategory(new CompletedWizardCategory(this, wizard.getRootCategory()));
      getRootCategory().setExpanded(true); // root should alway be expanded
   }

   public Wizard getWizard() {
      return wizard;
   }

   public void setWizard(Wizard wizard) {
      this.wizard = wizard;
   }

   public Agent getOwner() {
      return owner;
   }

   public void setOwner(Agent owner) {
      this.owner = owner;
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

   public CompletedWizardCategory getRootCategory() {
      return rootCategory;
   }

   public void setRootCategory(CompletedWizardCategory rootCategory) {
      this.rootCategory = rootCategory;
   }

}

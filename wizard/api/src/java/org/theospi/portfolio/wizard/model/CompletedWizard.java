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

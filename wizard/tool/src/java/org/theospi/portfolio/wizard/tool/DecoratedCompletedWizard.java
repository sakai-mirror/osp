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
package org.theospi.portfolio.wizard.tool;

import org.theospi.portfolio.wizard.model.CompletedWizard;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Jan 23, 2006
 * Time: 5:51:07 PM
 * To change this template use File | Settings | File Templates.
 */
public class DecoratedCompletedWizard {

   private WizardTool parent;
   private DecoratedWizard wizard;
   private CompletedWizard base;
   private DecoratedCompletedCategory rootCategory;

   public DecoratedCompletedWizard() {
   }

   public DecoratedCompletedWizard(WizardTool parent, DecoratedWizard wizard, CompletedWizard base) {
      this.parent = parent;
      this.wizard = wizard;
      this.base = base;
      setRootCategory(new DecoratedCompletedCategory(
            parent, wizard.getRootCategory(), base.getRootCategory()));
   }

   public WizardTool getParent() {
      return parent;
   }

   public void setParent(WizardTool parent) {
      this.parent = parent;
   }

   public DecoratedWizard getWizard() {
      return wizard;
   }

   public void setWizard(DecoratedWizard wizard) {
      this.wizard = wizard;
   }

   public CompletedWizard getBase() {
      return base;
   }

   public void setBase(CompletedWizard base) {
      this.base = base;
   }

   public DecoratedCompletedCategory getRootCategory() {
      return rootCategory;
   }

   public void setRootCategory(DecoratedCompletedCategory rootCategory) {
      this.rootCategory = rootCategory;
   }
}

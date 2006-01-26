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

import java.util.Collection;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Jan 23, 2006
 * Time: 3:07:26 PM
 * To change this template use File | Settings | File Templates.
 */
public class CompletedWizardCategory extends IdentifiableObject {

   private WizardCategory category;
   private CompletedWizard wizard;
   private List childPages;
   private List childCategories;
   private int sequence;
   private CompletedWizardCategory parentCategory;

   private boolean expanded = false;

   public CompletedWizardCategory() {
   }

   public CompletedWizardCategory(CompletedWizard wizard, WizardCategory category) {
      this.wizard = wizard;
      this.category = category;
      setSequence(category.getSequence());
      setChildPages(initChildPages());
      setChildCategories(initChildCategories());
   }

   protected List initChildCategories() {
      List categories = new ArrayList();

      for (Iterator i=category.getChildCategories().iterator();i.hasNext();) {
         WizardCategory category = (WizardCategory) i.next();
         CompletedWizardCategory completed = new CompletedWizardCategory(wizard, category);
         completed.setParentCategory(this);
         categories.add(completed);
      }

      return categories;
   }

   protected List initChildPages() {
      List pages = new ArrayList();

      for (Iterator i=category.getChildPages().iterator();i.hasNext();) {
         WizardPageSequence page = (WizardPageSequence) i.next();
         CompletedWizardPage completedPage = new CompletedWizardPage(page, this);
         pages.add(completedPage);
      }

      return pages;
   }

   public WizardCategory getCategory() {
      return category;
   }

   public void setCategory(WizardCategory category) {
      this.category = category;
   }

   public CompletedWizard getWizard() {
      return wizard;
   }

   public void setWizard(CompletedWizard wizard) {
      this.wizard = wizard;
   }

   public boolean isExpanded() {
      return expanded;
   }

   public void setExpanded(boolean expanded) {
      this.expanded = expanded;
   }

   public List getChildPages() {
      return childPages;
   }

   public void setChildPages(List childPages) {
      this.childPages = childPages;
   }

   public List getChildCategories() {
      return childCategories;
   }

   public void setChildCategories(List childCategories) {
      this.childCategories = childCategories;
   }

   public int getSequence() {
      return sequence;
   }

   public void setSequence(int sequence) {
      this.sequence = sequence;
   }

   public CompletedWizardCategory getParentCategory() {
      return parentCategory;
   }

   public void setParentCategory(CompletedWizardCategory parentCategory) {
      this.parentCategory = parentCategory;
   }
}

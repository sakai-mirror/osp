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

import org.theospi.portfolio.wizard.model.Wizard;
import org.theospi.portfolio.wizard.model.WizardCategory;
import org.theospi.portfolio.wizard.model.WizardPageSequence;
import org.theospi.portfolio.matrix.model.WizardPageDefinition;

import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Jan 13, 2006
 * Time: 11:44:25 AM
 * To change this template use File | Settings | File Templates.
 */
public class DecoratedCategory extends DecoratedCategoryChild {

   private WizardCategory base;
   private WizardTool parent;
   private List categoryPageList;
   private boolean selected;

   private DecoratedCategory parentCategory = null;

   public DecoratedCategory(WizardCategory base, WizardTool tool) {
      super(0);
      this.base = base;
      this.parent = tool;
   }

   public DecoratedCategory(DecoratedCategory parentCategory, WizardCategory base, WizardTool tool, int indent) {
      super(indent);
      this.parentCategory = parentCategory;
      this.base = base;
      this.parent = tool;
   }

   public WizardCategory getBase() {
      return base;
   }

   public void setBase(WizardCategory base) {
      this.base = base;
   }

   public WizardTool getParent() {
      return parent;
   }

   public void setParent(WizardTool parent) {
      this.parent = parent;
   }

   public String processActionNewPage() {
      WizardPageSequence wizardPage = null;
            //new WizardPageSequence(new WizardPageDefinition());
      wizardPage.setCategory(getBase());

      getBase().getChildPages().add(wizardPage);
      resequencePages();
      return null;
   }

   protected void resequencePages() {
      int index = 0;
      for (Iterator i=getBase().getChildPages().iterator();i.hasNext();) {
         WizardPageSequence page = (WizardPageSequence) i.next();
         page.setSequence(index);
         index++;
      }
      categoryPageList = null;
   }

   protected void resequenceCategories() {
      int index = 0;
      for (Iterator i=getBase().getChildCategories().iterator();i.hasNext();) {
         WizardCategory category = (WizardCategory) i.next();
         category.setSequence(index);
         index++;
      }
      categoryPageList = null;
   }

   public List getCategoryPageList() {
      if (categoryPageList == null) {
         categoryPageList = new ArrayList();
         addCategoriesPages(categoryPageList);
      }
      return categoryPageList;
   }

   protected List addCategoriesPages(List categoryPages) {
      for (Iterator i=getBase().getChildCategories().iterator();i.hasNext();) {
         WizardCategory category = (WizardCategory) i.next();
         categoryPages.add(new DecoratedCategory(this, category, getParent(), getIndent()+1));
      }

      for (Iterator i=getBase().getChildPages().iterator();i.hasNext();) {
         WizardPageSequence page = (WizardPageSequence) i.next();
         categoryPages.add(new DecoratedWizardPage(this, page, getParent(), getIndent()+1));
      }
      return categoryPages;
   }

   public void setCategoryPageList(List categoryPageList) {
      this.categoryPageList = categoryPageList;
   }

   public DecoratedCategory getParentCategory() {
      return parentCategory;
   }

   public void setParentCategory(DecoratedCategory parentCategory) {
      this.parentCategory = parentCategory;
   }

   public String processActionSave() {
      List parentCategories = getParentCategory().getBase().getChildCategories();

      if (!parentCategories.contains(getBase())) {
         parentCategories.add(getBase());
         getBase().setParentCategory(getParentCategory().getBase());
      }

      getParentCategory().resequenceCategories();

      return "editWizardPages";
   }

   public String getTitle() {
      return getBase().getTitle();
   }

   public String processActionEdit() {
      getParent().setCurrentCategory(this);
      return "editWizardCategory";
   }

   public String processActionDelete() {
      return null;
   }

   public String moveUp() {
      return null;
   }

   public String moveDown() {
      return null;
   }

   public boolean isSelected() {
      return selected;
   }

   public void setSelected(boolean selected) {
      this.selected = selected;
   }

   public boolean isFirst() {
      return getBase().getSequence() == 0 && getIndent() == 1;      
   }

   public boolean isLast() {
      return false;
   }

}

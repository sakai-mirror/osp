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
import org.theospi.portfolio.matrix.WizardPageHelper;
import org.sakaiproject.api.kernel.session.ToolSession;
import org.sakaiproject.api.kernel.session.cover.SessionManager;

import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Jan 13, 2006
 * Time: 11:44:25 AM
 * To change this template use File | Settings | File Templates.
 */
public class DecoratedCategory extends DecoratedCategoryChild {

   public static final String NEW_PAGE = "org.theospi.portfolio.wizard.tool.DecoratedCategory.newPage";

   private WizardCategory base;
   private List categoryPageList;
   private boolean selected;

   private DecoratedCategory parentCategory = null;

   public DecoratedCategory(WizardCategory base, WizardTool tool) {
      super(tool, 0);
      this.base = base;
   }

   public DecoratedCategory(DecoratedCategory parentCategory, WizardCategory base, WizardTool tool, int indent) {
      super(tool, indent);
      this.parentCategory = parentCategory;
      this.base = base;
   }

   public WizardCategory getBase() {
      return base;
   }

   public void setBase(WizardCategory base) {
      this.base = base;
   }

   public String processActionNewPage() {
      WizardPageSequence wizardPage =
            new WizardPageSequence(new WizardPageDefinition());
      wizardPage.getWizardPageDefinition().setSiteId(getParent().getWorksite().getId());
      wizardPage.getWizardPageDefinition().setNewId(getParent().getIdManager().createId());
      wizardPage.setCategory(getBase());

      getParent().getCurrent().getRootCategory().setCategoryPageList(null);

      return new DecoratedWizardPage(this, wizardPage, getParent(), getIndent() + 1).processActionEdit(true);
   }

   protected void resequencePages() {
      int index = 0;
      for (Iterator i=getBase().getChildPages().iterator();i.hasNext();) {
         WizardPageSequence page = (WizardPageSequence) i.next();
         page.setSequence(index);
         index++;
      }
      getParent().getCurrent().getRootCategory().setCategoryPageList(null);
   }

   protected void resequenceCategories() {
      int index = 0;
      for (Iterator i=getBase().getChildCategories().iterator();i.hasNext();) {
         WizardCategory category = (WizardCategory) i.next();
         category.setSequence(index);
         index++;
      }
      getParent().getCurrent().getRootCategory().setCategoryPageList(null);
   }

   public List getCategoryPageList() {
      if (categoryPageList == null) {
         ToolSession session = SessionManager.getCurrentToolSession();
         if (session.getAttribute(NEW_PAGE) != null &&
               session.getAttribute(WizardPageHelper.CANCELED) == null) {
            WizardPageSequence page = (WizardPageSequence) session.getAttribute(NEW_PAGE);
            page.setSequence(page.getCategory().getChildPages().size());
            page.getCategory().getChildPages().add(page);
            session.removeAttribute(NEW_PAGE);
         }
         else if (session.getAttribute(WizardPageHelper.CANCELED) != null) {
            session.removeAttribute(NEW_PAGE);
            session.removeAttribute(WizardPageHelper.CANCELED);
         }

         categoryPageList = new ArrayList();
         addCategoriesPages(categoryPageList);
      }
      return categoryPageList;
   }

   protected List addCategoriesPages(List categoryPages) {
      if (getParent().getCurrent().getBase().getType().equals(Wizard.WIZARD_TYPE_HIERARCHICAL)) {
         for (Iterator i=getBase().getChildCategories().iterator();i.hasNext();) {
            WizardCategory category = (WizardCategory) i.next();
            DecoratedCategory decoratedCategory = new DecoratedCategory(this, category, getParent(), getIndent()+1);
            categoryPages.add(decoratedCategory);
            decoratedCategory.addCategoriesPages(categoryPages);
         }
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
      DecoratedCategory parentCategory = getParentCategory();
      parentCategory.getBase().getChildCategories().remove(getBase());
      parentCategory.resequenceCategories();
      getParent().getDeletedItems().add(getBase());
      return null;
   }

   public String moveUp() {
      if (getBase().getSequence() != 0) {
         Collections.swap(getBase().getParentCategory().getChildCategories(),
               getBase().getSequence(), getBase().getSequence() - 1);
         getParentCategory().resequenceCategories();
      }
      return null;
   }

   public String moveDown() {
      if (getBase().getSequence() < getBase().getParentCategory().getChildCategories().size() - 1) {
         Collections.swap(getBase().getParentCategory().getChildCategories(),
               getBase().getSequence(), getBase().getSequence() + 1);
         getParentCategory().resequenceCategories();
      }
      return null;
   }

   public boolean isSelected() {
      return selected;
   }

   public void setSelected(boolean selected) {
      this.selected = selected;
   }

   public boolean isFirst() {
      return getBase().getSequence() == 0;
   }

   public boolean isLast() {
      return getBase().getSequence() >= getBase().getParentCategory().getChildCategories().size() - 1;
   }

   public String processActionNewCategory() {
      WizardCategory wizardCategory = new WizardCategory(getBase().getWizard());
      getParent().setCurrentCategory(
            new DecoratedCategory(this, wizardCategory, getParent(), getIndent() + 1));
      return "editWizardCategory";
   }

   public boolean isCategory() {
      return true;
   }

   public boolean isContainerForMove() {
      if (getParent().getMoveCategoryChild() == null) {
         return false;
      }
      DecoratedCategoryChild child = getParent().getMoveCategoryChild();
      if (child instanceof DecoratedCategory) {
         DecoratedCategory category = (DecoratedCategory) child;
         return category.getParentCategory() != this && category != this;
      }
      else if (child instanceof DecoratedWizardPage){
         DecoratedWizardPage page = (DecoratedWizardPage) child;
         return page.getCategory() != this;
      }
      return false;
   }

   public String processActionMoveTo() {
      DecoratedCategoryChild child = getParent().getMoveCategoryChild();
      child.setMoveTarget(false);
      if (child instanceof DecoratedCategory) {
         DecoratedCategory category = (DecoratedCategory) child;
         DecoratedCategory oldParent = category.getParentCategory();
         oldParent.getBase().getChildCategories().remove(category.getBase());
         getBase().getChildCategories().add(category.getBase());
         category.getBase().setParentCategory(getBase());
         oldParent.resequenceCategories();
         resequenceCategories();
      }
      else if (child instanceof DecoratedWizardPage) {
         DecoratedWizardPage page = (DecoratedWizardPage) child;
         DecoratedCategory oldParent = page.getCategory();
         oldParent.getBase().getChildPages().remove(page.getBase());
         getBase().getChildPages().add(page.getBase());
         page.getBase().setCategory(getBase());
         oldParent.resequencePages();
         resequencePages();
      }
      child.setMoveTarget(false);
      getParent().setMoveCategoryChild(null);
      return null;
   }
}

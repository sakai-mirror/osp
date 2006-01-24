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

import org.theospi.portfolio.wizard.model.*;

import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Jan 24, 2006
 * Time: 9:06:23 AM
 * To change this template use File | Settings | File Templates.
 */
public class DecoratedCompletedCategory {

   private WizardTool parent;
   private DecoratedCategory category;
   private CompletedWizardCategory base;

   private List categoryPageList = null;

   public DecoratedCompletedCategory() {
   }

   public DecoratedCompletedCategory(WizardTool parent, DecoratedCategory category, CompletedWizardCategory base) {
      this.parent = parent;
      this.category = category;
      this.base = base;
   }

   public WizardTool getParent() {
      return parent;
   }

   public void setParent(WizardTool parent) {
      this.parent = parent;
   }

   public DecoratedCategory getCategory() {
      return category;
   }

   public void setCategory(DecoratedCategory category) {
      this.category = category;
   }

   public CompletedWizardCategory getBase() {
      return base;
   }

   public void setBase(CompletedWizardCategory base) {
      this.base = base;
   }

   public List getCategoryPageList() {
      if (categoryPageList == null) {
         categoryPageList = new ArrayList();
         addCategoriesPages(categoryPageList);
      }
      return categoryPageList;
   }

   public void setCategoryPageList(List categoryPageList) {
      this.categoryPageList = categoryPageList;
   }

   protected List addCategoriesPages(List categoryPages) {
      if (getParent().getCurrent().getBase().getType().equals(Wizard.WIZARD_TYPE_HIERARCHICAL)) {
         for (Iterator i=getBase().getChildCategories().iterator();i.hasNext();) {
            CompletedWizardCategory category = (CompletedWizardCategory) i.next();
            DecoratedCategory decoratedCategory = new DecoratedCategory(
                  this.getCategory(), category.getCategory(), getParent(), getCategory().getIndent()+1);
            DecoratedCompletedCategory completed = new DecoratedCompletedCategory(getParent(), decoratedCategory, category);
            categoryPages.add(completed);
            if (category.isExpanded()) {
               completed.addCategoriesPages(categoryPages);
            }
         }
      }

      for (Iterator i=getBase().getChildPages().iterator();i.hasNext();) {
         CompletedWizardPage page = (CompletedWizardPage) i.next();
         DecoratedWizardPage decoratedPage = new DecoratedWizardPage(this.getCategory(),
            page.getWizardPageDefinition(), getParent(), getCategory().getIndent()+1);
         DecoratedCompletedPage completedPage = new DecoratedCompletedPage(getParent(), decoratedPage, page);
         categoryPages.add(completedPage);
      }
      return categoryPages;
   }

   public DecoratedCategoryChild getCategoryChild() {
      return (DecoratedCategoryChild)category;
   }

   public String processActionExpandToggle() {
      getBase().setExpanded(!getBase().isExpanded());
      getParent().getCurrent().getRunningWizard().getRootCategory().setCategoryPageList(null);
      return null;
   }
}

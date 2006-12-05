/**********************************************************************************
* $URL:https://source.sakaiproject.org/svn/osp/trunk/wizard/tool/src/java/org/theospi/portfolio/wizard/tool/DecoratedCompletedCategory.java $
* $Id:DecoratedCompletedCategory.java 9134 2006-05-08 20:28:42Z chmaurer@iupui.edu $
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
package org.theospi.portfolio.wizard.tool;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.theospi.portfolio.wizard.WizardFunctionConstants;
import org.theospi.portfolio.wizard.model.CompletedWizardCategory;
import org.theospi.portfolio.wizard.model.CompletedWizardPage;
import org.theospi.portfolio.wizard.model.Wizard;

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
      if (getParent().getCurrent().getBase().getType().equals(WizardFunctionConstants.WIZARD_TYPE_HIERARCHICAL)) {
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

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
package org.theospi.portfolio.wizard.tool;

import org.theospi.portfolio.wizard.model.CompletedWizardCategory;
import org.theospi.portfolio.wizard.model.CompletedWizardPage;
import org.theospi.portfolio.matrix.WizardPageHelper;
import org.theospi.portfolio.matrix.model.WizardPage;
import org.sakaiproject.api.kernel.session.ToolSession;
import org.sakaiproject.api.kernel.session.cover.SessionManager;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Jan 24, 2006
 * Time: 9:06:32 AM
 * To change this template use File | Settings | File Templates.
 */
public class DecoratedCompletedPage {

   private WizardTool parent;
   private DecoratedWizardPage page;
   private CompletedWizardPage base;

   public DecoratedCompletedPage() {
   }

   public DecoratedCompletedPage(WizardTool parent, DecoratedWizardPage page, CompletedWizardPage base) {
      this.parent = parent;
      this.page = page;
      this.base = base;
   }

   public WizardTool getParent() {
      return parent;
   }

   public void setParent(WizardTool parent) {
      this.parent = parent;
   }

   public DecoratedWizardPage getPage() {
      return page;
   }

   public void setPage(DecoratedWizardPage page) {
      this.page = page;
   }

   public CompletedWizardPage getBase() {
      return base;
   }

   public void setBase(CompletedWizardPage base) {
      this.base = base;
   }

   public DecoratedCategoryChild getCategoryChild() {
      return (DecoratedCategoryChild)page;
   }

   public String processActionEdit() {
      ExternalContext context = FacesContext.getCurrentInstance().getExternalContext();
      ToolSession session = SessionManager.getCurrentToolSession();
      WizardPage page = getParent().getMatrixManager().getWizardPage(getBase().getWizardPage().getId());
      session.setAttribute(WizardPageHelper.WIZARD_PAGE, page);
      session.setAttribute("readOnlyMatrix", "true");

      try {
         context.redirect("osp.wizard.page.helper/wizardPage.osp");
      }
      catch (IOException e) {
         throw new RuntimeException("Failed to redirect to helper", e);
      }

      return null;
   }
}

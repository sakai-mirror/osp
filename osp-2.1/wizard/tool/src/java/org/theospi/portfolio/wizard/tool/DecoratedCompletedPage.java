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

import org.theospi.portfolio.wizard.model.CompletedWizardCategory;
import org.theospi.portfolio.wizard.model.CompletedWizardPage;
import org.theospi.portfolio.matrix.WizardPageHelper;
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
      session.setAttribute(WizardPageHelper.WIZARD_PAGE, getBase().getWizardPage());

      try {
         context.redirect("osp.wizard.page.helper/wizardPage.osp");
      }
      catch (IOException e) {
         throw new RuntimeException("Failed to redirect to helper", e);
      }

      return null;
   }
}

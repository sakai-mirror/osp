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

import org.theospi.portfolio.wizard.model.WizardPageSequence;
import org.theospi.portfolio.matrix.WizardPageHelper;
import org.sakaiproject.api.kernel.session.ToolSession;
import org.sakaiproject.api.kernel.session.cover.SessionManager;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import java.util.Collections;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Jan 13, 2006
 * Time: 11:44:37 AM
 * To change this template use File | Settings | File Templates.
 */
public class DecoratedWizardPage extends DecoratedCategoryChild {

   private DecoratedCategory category = null;
   private WizardPageSequence base;
   private boolean selected = false;

   public DecoratedWizardPage(DecoratedCategory category, WizardPageSequence base, WizardTool parent, int indent) {
      super(parent, indent);
      this.base = base;
      this.category = category;
   }

   public WizardPageSequence getBase() {
      return base;
   }

   public void setBase(WizardPageSequence base) {
      this.base = base;
   }

   public String getTitle() {
      return getBase().getWizardPageDefinition().getTitle();
   }

   public boolean isSelected() {
      return selected;
   }

   public void setSelected(boolean selected) {
      this.selected = selected;
   }

   public String moveUp() {
      if (getBase().getSequence() != 0) {
         Collections.swap(getBase().getCategory().getChildPages(),
               getBase().getSequence(), getBase().getSequence() - 1);
         getCategory().resequencePages();
      }
      return null;
   }

   public String moveDown() {
      if (getBase().getSequence() < getBase().getCategory().getChildPages().size() - 1) {
         Collections.swap(getBase().getCategory().getChildPages(),
               getBase().getSequence(), getBase().getSequence() + 1);
         getCategory().resequencePages();
      }
      return null;
   }

   public boolean isFirst() {
      return getBase().getSequence() == 0;
   }

   public boolean isLast() {
      return getBase().getSequence() >= getBase().getCategory().getChildPages().size() - 1;
   }
   public DecoratedCategory getCategory() {
      return category;
   }

   public void setCategory(DecoratedCategory category) {
      this.category = category;
   }

   public String processActionEdit() {
      return processActionEdit(false);
   }

   public String processActionDelete() {
      DecoratedCategory parentCategory = getCategory();
      parentCategory.getBase().getChildPages().remove(getBase());
      parentCategory.resequencePages();
      getParent().getDeletedItems().add(getBase());
      return null;
   }

   public String processActionEdit(boolean isNew) {
      ExternalContext context = FacesContext.getCurrentInstance().getExternalContext();
      ToolSession session = SessionManager.getCurrentToolSession();
      session.setAttribute(WizardPageHelper.WIZARD_PAGE, getBase().getWizardPageDefinition());

      if (isNew) {
         session.setAttribute(DecoratedCategory.NEW_PAGE, getBase());
      }

      try {
         context.redirect("osp.wizard.page.def.helper/wizardPageDefinition.osp");
      }
      catch (IOException e) {
         throw new RuntimeException("Failed to redirect to helper", e);
      }

      return null;
   }
}

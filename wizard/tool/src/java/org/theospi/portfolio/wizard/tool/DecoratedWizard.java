/**********************************************************************************
* $URL: https://source.sakaiproject.org/svn/trunk/sakai/legacy/component/src/java/org/sakaiproject/component/framework/email/TestEmailService.java $
* $Id: TestEmailService.java 3831 2005-11-14 20:17:24Z ggolden@umich.edu $
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

import org.sakaiproject.api.kernel.session.ToolSession;
import org.sakaiproject.api.kernel.session.cover.SessionManager;
import org.theospi.portfolio.style.StyleHelper;
import org.theospi.portfolio.style.model.Style;
import org.theospi.portfolio.wizard.model.Wizard;
import org.theospi.portfolio.guidance.model.GuidanceItem;
import org.theospi.portfolio.wizard.mgt.WizardManager;
import org.theospi.portfolio.guidance.model.Guidance;
import org.theospi.portfolio.guidance.model.GuidanceItem;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.io.IOException;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import java.util.List;
import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Nov 14, 2005
 * Time: 4:52:55 PM
 * To change this template use File | Settings | File Templates.
 */
public class DecoratedWizard implements DecoratedListInterface {
   private Wizard base;
   private WizardTool parent;
   private DecoratedCategory rootCategory = null;
   private DecoratedWizard next;
   private DecoratedWizard prev;
   private boolean newWizard = false;

   private DecoratedCompletedWizard runningWizard;

   public DecoratedWizard(WizardTool tool, Wizard base) {
      this.base = base;
      this.parent = tool;
      rootCategory = new DecoratedCategory(base.getRootCategory(), tool);
   }
   public DecoratedWizard(WizardTool tool, Wizard base, boolean newWizard) {
       this.newWizard = newWizard;
       this.base = base;
       this.parent = tool;
       rootCategory = new DecoratedCategory(base.getRootCategory(), tool);
   }
   public Wizard getBase() {
      return base;
   }

   public void setBase(Wizard base) {
      this.base = base;
   }
   
   public String getDescription() {
      return getConcatDescription();
   }
   
   public String getConcatDescription() {
      String s = getBase().getDescription();
      if(s.length() > 100)
         s = s.substring(0, 100) + "...";
      return s;
   }
   
   public boolean getExposeAsTool() {
      if (base.getExposeAsTool() == null)
         return false;
      else
         return base.getExposeAsTool().booleanValue();
   }
   
   public void setExposeAsTool(boolean exposeAsTool) {
      base.setExposeAsTool(new Boolean(exposeAsTool));
   }
   
   public boolean getCanPublish() {
      return parent.getCanPublish(base);
   }

   public boolean getCanDelete() {
      return parent.getCanDelete(base);
   }
   
   public boolean getCanEdit() {
      return parent.getCanEdit(base);
   }
   
   public boolean getCanExport() {
      return parent.getCanExport(base);
   }
   
   public String getCurrentExportLink() {

	      try {
	         return "repository/" + "manager=org.theospi.portfolio.wizard.mgt.WizardManager&" +
	               WizardManager.WIZARD_PARAM_ID + "=" +
	               URLEncoder.encode(getBase().getId().getValue(), "UTF-8") + "/" +
	               URLEncoder.encode(getBase().getName() + " Wizard.zip", "UTF-8");
	      }
	      catch (UnsupportedEncodingException e) {
	         throw new RuntimeException(e);
	      }
	   }

   public String processActionEdit() {
      return parent.processActionEdit(base);
   }

   public String processActionDelete() {
      return parent.processActionDelete(base);
   }

   public String processActionConfirmDelete() {
      return parent.processActionConfirmDelete(base);
   }
   
   public String processActionPublish() {
      return parent.processActionPublish(base);
   }
   
   public String getStyleName() {
      ToolSession session = SessionManager.getCurrentToolSession();
      if (session.getAttribute(StyleHelper.CURRENT_STYLE) != null) {
         Style style = (Style)session.getAttribute(StyleHelper.CURRENT_STYLE);
         base.setStyle(style);
      }
      else if (session.getAttribute(StyleHelper.UNSELECTED_STYLE) != null) {
         base.setStyle(null);
         session.removeAttribute(StyleHelper.UNSELECTED_STYLE);
         return "";
      }
      
      if (base.getStyle() != null)
         return base.getStyle().getName();
      return "";
   }
   
   public String processActionSelectStyle() {      
      getParent().clearInterface();
      ExternalContext context = FacesContext.getCurrentInstance().getExternalContext();
      ToolSession session = SessionManager.getCurrentToolSession();
      session.removeAttribute(StyleHelper.CURRENT_STYLE);
      session.removeAttribute(StyleHelper.CURRENT_STYLE_ID);
      
      session.setAttribute(StyleHelper.STYLE_SELECTABLE, "true");
      
      Wizard wizard = getBase();
      
      if (wizard.getStyle() != null)
         session.setAttribute(StyleHelper.CURRENT_STYLE_ID, wizard.getStyle().getId().getValue());
      
      try {
         context.redirect("osp.style.helper/listStyle");
      }
      catch (IOException e) {
         throw new RuntimeException("Failed to redirect to helper", e);
      }
      return null;
   }

   public WizardTool getParent() {
      return parent;
   }

   public void setParent(WizardTool parent) {
      this.parent = parent;
   }

   public DecoratedCategory getRootCategory() {
      return rootCategory;
   }

   public void setRootCategory(DecoratedCategory rootCategory) {
      this.rootCategory = rootCategory;
   }

   public boolean isFirst() {
      return getPrev() == null;
   }

   public boolean isLast() {
      return getNext() == null;
   }

   public String moveUp() {
      return switchSeq(getPrev());
   }

   public String moveDown() {
      return switchSeq(getNext());
   }

   protected String switchSeq(DecoratedWizard other) {
      int otherSeq = other.getBase().getSequence();
      int thisSeq = getBase().getSequence();
      other.getBase().setSequence(thisSeq);
      getBase().setSequence(otherSeq);
      getParent().getWizardManager().saveWizard(getBase());
      getParent().getWizardManager().saveWizard(other.getBase());
      return null;
   }

   public DecoratedWizard getNext() {
      return next;
   }

   public void setNext(DecoratedWizard next) {
      this.next = next;
   }

   public DecoratedWizard getPrev() {
      return prev;
   }

   public void setPrev(DecoratedWizard prev) {
      this.prev = prev;
   }
   
   public boolean isOwner() {
      String userId = SessionManager.getCurrentSessionUserId();
      if(userId != null)
         return userId.equals(getBase().getOwner().getId().getValue());
      return false;
   }

   public String processActionRunWizard() {
      getParent().clearInterface();
      ExternalContext context = FacesContext.getCurrentInstance().getExternalContext();
      //ToolSession session = SessionManager.getCurrentToolSession();
      
      getParent().setCurrent(this);
      setRunningWizard(new DecoratedCompletedWizard(getParent(), this,
         parent.getWizardManager().getCompletedWizard(getBase(), getParent().getCurrentUserId())));

      //return "runWizard";
      
      try {
         context.redirect("osp.wizard.run.helper/runWizardGuidance");
      }
      catch (IOException e) {
         throw new RuntimeException("Failed to redirect to helper", e);
      }
      return null;
   }
   
   public String processActionEditInstructions()
   {
      parent.processActionGuidanceHelper(getBase(), 1);
      return null;
   }
   
   public String processActionEditRationale()
   {
      parent.processActionGuidanceHelper(getBase(), 2);
      return null;
   }
   
   public String processActionEditExamples()
   {
      parent.processActionGuidanceHelper(getBase(), 4);
      return null;
   }

   public DecoratedCompletedWizard getRunningWizard() {
      return runningWizard;
   }

   public void setRunningWizard(DecoratedCompletedWizard runningWizard) {
      this.runningWizard = runningWizard;
   }

   public GuidanceItem getInstruction() {
      if(getBase().getGuidance() == null)
            return null;
      return getBase().getGuidance().getInstruction();
   }

   public GuidanceItem getExample() {
      if(getBase().getGuidance() == null)
         return null;
      return getBase().getGuidance().getExample();
   }

   public GuidanceItem getRationale() {
      if(getBase().getGuidance() == null)
         return null;
      return getBase().getGuidance().getRationale();
   }

   public boolean isGuidanceAvailable() {
      return getBase().getGuidance() != null;
   }
   
   protected String limitString(String s, int max)
   {
      if(s == null)
         return "";
      if(s.length() > max)
         s = s.substring(0,max) + "...";
      return s;
   }

   public String getGuidanceInstructions() {
      Guidance guidance = getBase().getGuidance();
      if(guidance == null)
           return null;
      GuidanceItem item = guidance.getInstruction();
      if(item == null)
         return null;
      return limitString(item.getText(), 100);
   }

   public List getGuidanceInstructionsAttachments() {
      Guidance guidance = getBase().getGuidance();
      if(guidance == null)
         return new ArrayList();
      GuidanceItem item = guidance.getInstruction();
      if(item == null)
         return new ArrayList();
      return item.getAttachments();
   }

   public String getGuidanceRationale() {
      Guidance guidance = getBase().getGuidance();
      if(guidance == null)
           return "";
      GuidanceItem item = guidance.getRationale();
      if(item == null)
         return "";
      return limitString(item.getText(), 100);
   }

   public List getGuidanceRationaleAttachments() {
      Guidance guidance = getBase().getGuidance();
      if(guidance == null)
           return new ArrayList();
      GuidanceItem item = guidance.getRationale();
      if(item == null)
         return new ArrayList();
      return item.getAttachments();
   }

   public String getGuidanceExamples() {
      Guidance guidance = getBase().getGuidance();
      if(guidance == null)
           return "";
      GuidanceItem item = guidance.getExample();
      if(item == null)
         return "";
      return limitString(item.getText(), 100);
   }

   public List getGuidanceExamplesAttachments() {
      Guidance guidance = getBase().getGuidance();
      if(guidance == null)
           return new ArrayList();
      GuidanceItem item = guidance.getExample();
      if(item == null)
         return new ArrayList();
      return item.getAttachments();
   }

   public List getEvaluators() {
       return parent.getEvaluators(getBase());
   }

   public boolean isNewWizard() {
       return newWizard;
   }

    public void setNewWizard(boolean newWizard) {
        this.newWizard = newWizard;
    }
    
    public DecoratedCategory getCategory()
    {
       return null;
    }

    public String getIndentString() {
       return "";
    }

    public String getTitle() {
       return getBase().getName();
    }

    public boolean isMoveTarget() {
       return false;
    }

    public boolean getHasChildren() {
       return false;
    }
    public boolean isWizard() {
       return true;
    }
    
    public String getDeleteMessage() {
       return getParent().getMessageFromBundle("delete_wizard_message", new Object[]{
             base.getName()});
    }
}

package org.theospi.portfolio.wizard.tool;

import java.util.ArrayList;
import java.util.List;

import org.sakaiproject.api.kernel.session.ToolSession;
import org.sakaiproject.api.kernel.session.cover.SessionManager;
import org.sakaiproject.service.legacy.entity.Reference;
import org.sakaiproject.service.legacy.filepicker.FilePickerHelper;
import org.theospi.portfolio.guidance.model.Guidance;
import org.theospi.portfolio.guidance.model.GuidanceItem;
import org.theospi.portfolio.guidance.model.GuidanceItemAttachment;
import org.theospi.portfolio.guidance.tool.GuidanceTool;
import org.theospi.portfolio.wizard.model.Wizard;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Nov 14, 2005
 * Time: 4:52:55 PM
 * To change this template use File | Settings | File Templates.
 */
public class DecoratedWizard {
   private Wizard base;
   private WizardTool tool;

   public DecoratedWizard(WizardTool tool, Wizard base) {
      this.base = base;
      this.tool = tool;
   }

   public Wizard getBase() {
      return base;
   }

   public void setBase(Wizard base) {
      this.base = base;
   }
/*
   public List getStyle() {
      ToolSession session = SessionManager.getCurrentToolSession();
      if (session.getAttribute(FilePickerHelper.FILE_PICKER_CANCEL) == null &&
         session.getAttribute(FilePickerHelper.FILE_PICKER_ATTACHMENTS) != null) {

         List refs = (List)session.getAttribute(FilePickerHelper.FILE_PICKER_ATTACHMENTS);
         List newAttachments = new ArrayList();

         session.removeAttribute(FilePickerHelper.FILE_PICKER_ATTACHMENTS);
         //session.removeAttribute(GuidanceTool.ATTACHMENT_TYPE);
         //base.setStyle();
         //base.getAttachments().addAll(newAttachments);
      }

      return base.getStyle();
   }
*/
   public String processActionEdit() {
      return tool.processActionEdit(base);
   }

   public String processActionDelete() {
      return tool.processActionDelete(base);
   }
}

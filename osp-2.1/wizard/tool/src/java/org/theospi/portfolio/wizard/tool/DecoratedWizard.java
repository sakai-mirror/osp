package org.theospi.portfolio.wizard.tool;

import java.util.ArrayList;
import java.util.List;

import org.sakaiproject.api.kernel.session.ToolSession;
import org.sakaiproject.api.kernel.session.cover.SessionManager;
import org.sakaiproject.service.legacy.entity.Reference;
import org.sakaiproject.service.legacy.filepicker.FilePickerHelper;
import org.theospi.portfolio.wizard.model.Wizard;
import org.theospi.portfolio.wizard.model.WizardStyleItem;

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
   
   public boolean getExposeAsTool() {
      if (base.getExposeAsTool() == null)
         return false;
      else
         return base.getExposeAsTool().booleanValue();
   }
   
   public void setExposeAsTool(boolean exposeAsTool) {
      base.setExposeAsTool(new Boolean(exposeAsTool));
   }

   public List getWizardStyleItems() {
      ToolSession session = SessionManager.getCurrentToolSession();
      if (session.getAttribute(FilePickerHelper.FILE_PICKER_CANCEL) == null &&
         session.getAttribute(FilePickerHelper.FILE_PICKER_ATTACHMENTS) != null) {

         List refs = (List)session.getAttribute(FilePickerHelper.FILE_PICKER_ATTACHMENTS);
         List newStyles = new ArrayList();
         for(int i=0; i<refs.size(); i++) {
            Reference ref = (Reference) refs.get(i);
            Reference fullRef = tool.decorateReference(ref.getReference());
            WizardStyleItem wsItem = new WizardStyleItem(base, ref, fullRef);
            if (base.getWizardStyleItems().contains(wsItem)) {
               wsItem =
                  (WizardStyleItem) base.getWizardStyleItems().get(base.getWizardStyleItems().indexOf(wsItem));
            }

            newStyles.add(wsItem);
         }
         base.setWizardStyleItems(newStyles);
         session.removeAttribute(FilePickerHelper.FILE_PICKER_ATTACHMENTS);
      }

      return base.getWizardStyleItems();
   }

   public String processActionEdit() {
      return tool.processActionEdit(base);
   }

   public String processActionDelete() {
      return tool.processActionDelete(base);
   }
}

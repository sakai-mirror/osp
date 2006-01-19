package org.theospi.portfolio.wizard.tool;

import org.sakaiproject.api.kernel.session.ToolSession;
import org.sakaiproject.api.kernel.session.cover.SessionManager;
import org.sakaiproject.service.legacy.entity.Reference;
import org.sakaiproject.service.legacy.filepicker.FilePickerHelper;
import org.theospi.portfolio.wizard.model.Wizard;
import org.theospi.portfolio.wizard.model.WizardStyleItem;
import org.theospi.portfolio.wizard.model.WizardCategory;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Nov 14, 2005
 * Time: 4:52:55 PM
 * To change this template use File | Settings | File Templates.
 */
public class DecoratedWizard {
   private Wizard base;
   private WizardTool parent;
   private DecoratedCategory rootCategory = null;
   private DecoratedWizard next;
   private DecoratedWizard prev;

   public DecoratedWizard(WizardTool tool, Wizard base) {
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
         base.getWizardStyleItems().clear();
         for(int i=0; i<refs.size(); i++) {
            Reference ref = (Reference) refs.get(i);
            Reference fullRef = parent.decorateReference(ref.getReference());
            WizardStyleItem wsItem = new WizardStyleItem(base, ref, fullRef);
            base.getWizardStyleItems().add(wsItem);
         }
         session.removeAttribute(FilePickerHelper.FILE_PICKER_ATTACHMENTS);
      }

      return base.getWizardStyleItems();
   }

   public String processActionEdit() {
      return parent.processActionEdit(base);
   }

   public String processActionDelete() {
      return parent.processActionDelete(base);
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

}

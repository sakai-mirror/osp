package org.theospi.portfolio.guidance.tool;

import org.theospi.portfolio.guidance.mgt.GuidanceManager;
import org.theospi.portfolio.guidance.model.Guidance;
import org.theospi.portfolio.guidance.model.GuidanceItem;
import org.theospi.portfolio.guidance.model.GuidanceItemAttachment;
import org.theospi.portfolio.shared.mgt.ContentEntityWrapper;
import org.sakaiproject.api.kernel.component.cover.ComponentManager;
import org.sakaiproject.api.kernel.session.ToolSession;
import org.sakaiproject.api.kernel.session.cover.SessionManager;
import org.sakaiproject.service.legacy.filepicker.FilePickerHelper;
import org.sakaiproject.service.legacy.resource.cover.EntityManager;
import org.sakaiproject.service.legacy.entity.Reference;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import java.io.IOException;
import java.util.List;
import java.util.Iterator;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Nov 11, 2005
 * Time: 3:33:43 PM
 * To change this template use File | Settings | File Templates.
 */
public class GuidanceTool {

   private DecoratedGuidance current = null;

   private GuidanceManager guidanceManager;
   private String guidanceInstructions = "Add guidance for a wizard.";
   public static final String ATTACHMENT_TYPE = "org.theospi.portfolio.guidance.tool.GuidanceTool.attachmentType";

   public GuidanceManager getGuidanceManager() {
      return guidanceManager;
   }

   public void setGuidanceManager(GuidanceManager guidanceManager) {
      this.guidanceManager = guidanceManager;
   }

   public String getGuidanceInstructions() {
      return guidanceInstructions;
   }

   public void setGuidanceInstructions(String guidanceInstructions) {
      this.guidanceInstructions = guidanceInstructions;
   }

   public DecoratedGuidance getCurrent() {
      if (current == null) {
         current = new DecoratedGuidance(this,
            getGuidanceManager().createNew("Worksite", "blah", null, "blahblah"));
      }
      return current;
   }

   public String processActionManageAttachments(String type) {
      ExternalContext context = FacesContext.getCurrentInstance().getExternalContext();
      ToolSession session = SessionManager.getCurrentToolSession();
      session.setAttribute(FilePickerHelper.FILE_PICKER_ATTACH_LINKS, new Boolean(true).toString());
      session.setAttribute(GuidanceTool.ATTACHMENT_TYPE, type);
      GuidanceItem item = current.getBase().getItem(type);

      List attachments = item.getAttachments();
      List attachmentRefs = EntityManager.newReferenceList();

      for (Iterator i=attachments.iterator();i.hasNext();) {
         GuidanceItemAttachment attachment = (GuidanceItemAttachment)i.next();
         attachmentRefs.add(attachment.getBaseReference());
      }
      session.setAttribute(FilePickerHelper.FILE_PICKER_ATTACHMENTS, attachmentRefs);

      try {
         context.redirect("sakai.filepicker.helper/tool");
      }
      catch (IOException e) {
         throw new RuntimeException("Failed to redirect to helper", e);
      }
      return null;
   }

   public String processActionSave() {
      return "tool";
   }

   public Reference decorateReference(String reference) {
      return getGuidanceManager().decorateReference(current.getBase(), reference);
   }
}

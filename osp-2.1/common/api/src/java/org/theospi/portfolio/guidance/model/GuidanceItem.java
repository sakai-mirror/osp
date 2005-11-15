package org.theospi.portfolio.guidance.model;

import org.sakaiproject.metaobj.shared.model.IdentifiableObject;

import java.util.Set;
import java.util.List;
import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Nov 11, 2005
 * Time: 12:06:34 PM
 * To change this template use File | Settings | File Templates.
 */
public class GuidanceItem extends IdentifiableObject {

   private String type;
   private String text = "";
   private Guidance guidance;
   private List attachments;

   public GuidanceItem() {
   }

   public GuidanceItem(Guidance guidance, String type) {
      this.type = type;
      this.guidance = guidance;
      attachments = new ArrayList();
   }

   public String getType() {
      return type;
   }

   public void setType(String type) {
      this.type = type;
   }

   public String getText() {
      return text;
   }

   public void setText(String text) {
      this.text = text;
   }

   public Guidance getGuidance() {
      return guidance;
   }

   public void setGuidance(Guidance guidance) {
      this.guidance = guidance;
   }

   public List getAttachments() {
      return attachments;
   }

   public void setAttachments(List attachments) {
      this.attachments = attachments;
   }
}

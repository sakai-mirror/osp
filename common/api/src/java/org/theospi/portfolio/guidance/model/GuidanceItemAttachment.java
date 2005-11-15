package org.theospi.portfolio.guidance.model;

import org.sakaiproject.metaobj.shared.model.IdentifiableObject;
import org.sakaiproject.metaobj.shared.model.Artifact;
import org.sakaiproject.service.legacy.content.ContentResource;
import org.sakaiproject.service.legacy.entity.Reference;
import org.theospi.portfolio.shared.mgt.ContentEntityWrapper;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Nov 11, 2005
 * Time: 12:38:19 PM
 * To change this template use File | Settings | File Templates.
 */
public class GuidanceItemAttachment extends IdentifiableObject {

   private GuidanceItem item;
   private Reference baseReference;
   private Reference fullReference;

   public GuidanceItemAttachment() {
   }

   public GuidanceItemAttachment(GuidanceItem item, Reference baseReference, Reference fullReference) {
      this.item = item;
      this.baseReference = baseReference;
      this.fullReference = fullReference;
   }

   public GuidanceItem getItem() {
      return item;
   }

   public void setItem(GuidanceItem item) {
      this.item = item;
   }

   public Reference getBaseReference() {
      return baseReference;
   }

   public void setBaseReference(Reference baseReference) {
      this.baseReference = baseReference;
   }

   public Reference getFullReference() {
      return fullReference;
   }

   public void setFullReference(Reference fullReference) {
      this.fullReference = fullReference;
   }

   public String getDisplayName() {
      ContentResource resource = (ContentResource)baseReference.getEntity();

      String displayNameProp = resource.getProperties().getNamePropDisplayName();
      return resource.getProperties().getProperty(displayNameProp);
   }

}

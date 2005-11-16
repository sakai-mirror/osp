package org.theospi.portfolio.guidance.model;

import org.sakaiproject.metaobj.shared.model.IdentifiableObject;
import org.sakaiproject.metaobj.shared.model.Artifact;
import org.sakaiproject.service.legacy.content.ContentResource;
import org.sakaiproject.service.legacy.entity.Reference;
import org.theospi.portfolio.shared.mgt.ContentEntityWrapper;
import org.theospi.portfolio.shared.mgt.ReferenceHolder;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Nov 11, 2005
 * Time: 12:38:19 PM
 * To change this template use File | Settings | File Templates.
 */
public class GuidanceItemAttachment extends IdentifiableObject {

   private GuidanceItem item;
   private ReferenceHolder baseReference;
   private ReferenceHolder fullReference;

   public GuidanceItemAttachment() {
   }

   public GuidanceItemAttachment(GuidanceItem item, Reference baseReference, Reference fullReference) {
      this.item = item;
      this.baseReference = new ReferenceHolder(baseReference);
      this.fullReference = new ReferenceHolder(fullReference);
   }

   public GuidanceItem getItem() {
      return item;
   }

   public void setItem(GuidanceItem item) {
      this.item = item;
   }

   public ReferenceHolder getBaseReference() {
      return baseReference;
   }

   public void setBaseReference(ReferenceHolder baseReference) {
      this.baseReference = baseReference;
   }

   public void setBaseReference(Reference baseReference) {
      this.baseReference = new ReferenceHolder(baseReference);
   }

   public ReferenceHolder getFullReference() {
      return fullReference;
   }

   public void setFullReference(Reference fullReference) {
      this.fullReference = new ReferenceHolder(fullReference);
   }

   public void setFullReference(ReferenceHolder fullReference) {
      this.fullReference = fullReference;
   }

   public String getDisplayName() {
      ContentResource resource = (ContentResource)baseReference.getBase().getEntity();

      String displayNameProp = resource.getProperties().getNamePropDisplayName();
      return resource.getProperties().getProperty(displayNameProp);
   }

   public boolean equals(Object o) {
      if (this == o) {
         return true;
      }
      if (!(o instanceof GuidanceItemAttachment)) {
         return false;
      }

      final GuidanceItemAttachment guidanceItemAttachment = (GuidanceItemAttachment) o;

      if (fullReference != null ? !fullReference.equals(guidanceItemAttachment.fullReference) : guidanceItemAttachment.fullReference != null) {
         return false;
      }
      if (item != null ? !item.equals(guidanceItemAttachment.item) : guidanceItemAttachment.item != null) {
         return false;
      }

      return true;
   }

   public int hashCode() {
      int result = 0;
      result = 29 * result + (item != null ? item.hashCode() : 0);
      result = 29 * result + (fullReference != null ? fullReference.hashCode() : 0);
      return result;
   }

}

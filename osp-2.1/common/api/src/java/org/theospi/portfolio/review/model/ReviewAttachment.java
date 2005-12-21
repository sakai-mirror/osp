package org.theospi.portfolio.review.model;

import org.sakaiproject.metaobj.shared.model.IdentifiableObject;
import org.sakaiproject.service.legacy.content.ContentResource;
import org.sakaiproject.service.legacy.entity.Reference;
import org.theospi.portfolio.shared.mgt.ReferenceHolder;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Nov 11, 2005
 * Time: 12:38:19 PM
 * To change this template use File | Settings | File Templates.
 */
public class ReviewAttachment extends IdentifiableObject {

   private Review review;
   private ReferenceHolder baseReference;
   private ReferenceHolder fullReference;

   public ReviewAttachment() {
   }

   public ReviewAttachment(Review review, Reference baseReference, Reference fullReference) {
      this.review = review;
      this.baseReference = new ReferenceHolder(baseReference);
      this.fullReference = new ReferenceHolder(fullReference);
   }

   public Review getReview() {
      return review;
   }

   public void setReview(Review review) {
      this.review = review;
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
      if (!(o instanceof ReviewAttachment)) {
         return false;
      }

      final ReviewAttachment reviewAttachment = (ReviewAttachment) o;

      if (fullReference != null ? !fullReference.equals(reviewAttachment.fullReference) : reviewAttachment.fullReference != null) {
         return false;
      }
      if (review != null ? !review.equals(reviewAttachment.review) : reviewAttachment.review != null) {
         return false;
      }

      return true;
   }

   public int hashCode() {
      int result = 0;
      result = 29 * result + (review != null ? review.hashCode() : 0);
      result = 29 * result + (fullReference != null ? fullReference.hashCode() : 0);
      return result;
   }

}

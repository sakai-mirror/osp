package org.theospi.portfolio.review.model;

import java.util.Date;

import org.sakaiproject.metaobj.shared.model.Agent;
import org.sakaiproject.metaobj.shared.model.Id;
import org.sakaiproject.metaobj.shared.model.IdentifiableObject;

public class Review extends IdentifiableObject {
   
   public static final byte VISABILITY_UNKNOWN = 0;
   public static final byte VISABILITY_PRIVATE = 1;
   public static final byte VISABILITY_SHARED = 2;
   public static final byte VISABILITY_PUBLIC = 3;

   //private ReviewDevice reviewDevice = new ReviewDevice();
   private String siteId;
   private String parent;
   private Id reviewContent;
   private Agent creator = null;
   private String title = null;
   private Date created = null;
   private byte visibility = 0;
   private ReviewAttachment reviewAttachment;
   
   private boolean newObject = false;
   
   private Id securityQualifier;
   private String securityViewFunction;
   private String securityEditFunction;
   
   public Review() {}
   
   public Review(Id id, Agent owner, String description, String siteId, Id securityQualifier,
         String securityViewFunction, String securityEditFunction) {
      this.siteId = siteId;
      this.creator = owner;
      this.securityQualifier = securityQualifier;
      this.securityViewFunction = securityViewFunction;
      this.securityEditFunction = securityEditFunction;
      setId(id);
      newObject = true;

   }
   
   
   /**
    * @return Returns the created.
    */
   public Date getCreated() {
      return created;
   }
   /**
    * @param created The created to set.
    */
   public void setCreated(Date created) {
      this.created = created;
   }
   /**
    * @return Returns the creator.
    */
   public Agent getCreator() {
      return creator;
   }
   /**
    * @param creator The creator to set.
    */
   public void setCreator(Agent creator) {
      this.creator = creator;
   }
   /**
    * @return Returns the title.
    */
   public String getTitle() {
      return title;
   }
   /**
    * @param title The title to set.
    */
   public void setTitle(String title) {
      this.title = title;
   }
   /**
    * @return Returns the visibility.
    */
   public byte getVisibility() {
      return visibility;
   }
   /**
    * @param visibility The visibility to set.
    */
   public void setVisibility(byte visibility) {
      this.visibility = visibility;
   }
   /**
    * @return Returns the reviewContent.
    */
   public Id getReviewContent() {
      return reviewContent;
   }
   /**
    * @param reviewContent The reviewContent to set.
    */
   public void setReviewContent(Id reviewContent) {
      this.reviewContent = reviewContent;
   }
   /**
    * @return Returns the reviewDevice.
    */
   public String getParent() {
      return parent;
   }
   /**
    * @param reviewDevice The reviewDevice to set.
    */
   public void setParent(String parent) {
      this.parent = parent;
   }
   /**
    * @return Returns the newObject.
    */
   public boolean isNewObject() {
      return newObject;
   }
   /**
    * @param newObject The newObject to set.
    */
   public void setNewObject(boolean newObject) {
      this.newObject = newObject;
   }
   /**
    * @return Returns the securityEditFunction.
    */
   public String getSecurityEditFunction() {
      return securityEditFunction;
   }
   /**
    * @param securityEditFunction The securityEditFunction to set.
    */
   public void setSecurityEditFunction(String securityEditFunction) {
      this.securityEditFunction = securityEditFunction;
   }
   /**
    * @return Returns the securityQualifier.
    */
   public Id getSecurityQualifier() {
      return securityQualifier;
   }
   /**
    * @param securityQualifier The securityQualifier to set.
    */
   public void setSecurityQualifier(Id securityQualifier) {
      this.securityQualifier = securityQualifier;
   }
   /**
    * @return Returns the securityViewFunction.
    */
   public String getSecurityViewFunction() {
      return securityViewFunction;
   }
   /**
    * @param securityViewFunction The securityViewFunction to set.
    */
   public void setSecurityViewFunction(String securityViewFunction) {
      this.securityViewFunction = securityViewFunction;
   }


   /**
    * @return Returns the siteId.
    */
   public String getSiteId() {
      return siteId;
   }


   /**
    * @param siteId The siteId to set.
    */
   public void setSiteId(String siteId) {
      this.siteId = siteId;
   }

   /**
    * @return Returns the reviewAttachment.
    */
   public ReviewAttachment getReviewAttachment() {
      return reviewAttachment;
   }

   /**
    * @param reviewAttachment The reviewAttachment to set.
    */
   public void setReviewAttachment(ReviewAttachment reviewAttachment) {
      this.reviewAttachment = reviewAttachment;
   }

}

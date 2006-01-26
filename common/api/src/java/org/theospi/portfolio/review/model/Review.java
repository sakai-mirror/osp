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
package org.theospi.portfolio.review.model;

import java.util.Date;

import org.sakaiproject.metaobj.shared.model.Agent;
import org.sakaiproject.metaobj.shared.model.Id;
import org.sakaiproject.metaobj.shared.model.IdentifiableObject;

public class Review extends IdentifiableObject {
   
   public static final int VISABILITY_UNKNOWN = 0;
   public static final int VISABILITY_PRIVATE = 1;
   public static final int VISABILITY_SHARED = 2;
   public static final int VISABILITY_PUBLIC = 3;
   
   public static final int REFLECTION_TYPE = 0;
   public static final int EVALUATION_TYPE = 1;
   public static final int REVIEW_TYPE = 2;

   //private ReviewDevice reviewDevice = new ReviewDevice();
   private String siteId;
   private String parent;
   private String deviceId;
   private int type;
   private Id reviewContent;
   private Agent creator = null;
   private String title = null;
   private Date created = null;
   private int visibility = 0;
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
   public int getVisibility() {
      return visibility;
   }
   /**
    * @param visibility The visibility to set.
    */
   public void setVisibility(int visibility) {
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

   /**
    * @return Returns the deviceId.
    */
   public String getDeviceId() {
      return deviceId;
   }

   /**
    * @param deviceId The deviceId to set.
    */
   public void setDeviceId(String deviceId) {
      this.deviceId = deviceId;
   }

   /**
    * @return Returns the type.
    */
   public int getType() {
      return type;
   }

   /**
    * @param type The type to set.
    */
   public void setType(int type) {
      this.type = type;
   }

}

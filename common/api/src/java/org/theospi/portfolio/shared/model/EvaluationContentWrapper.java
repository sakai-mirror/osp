/**********************************************************************************
* $URL$
* $Id$
***********************************************************************************
*
* Copyright (c) 2006 The Sakai Foundation.
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

package org.theospi.portfolio.shared.model;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.sakaiproject.metaobj.shared.model.Agent;
import org.sakaiproject.metaobj.shared.model.Id;
import org.sakaiproject.user.api.User;
import org.sakaiproject.user.api.UserNotDefinedException;
import org.sakaiproject.user.cover.UserDirectoryService;

public abstract class EvaluationContentWrapper {

   private Id id;
   private String title;
   private User owner;
   private Date submittedDate;
   private String evalType;
   private String url;
   private Set urlParams = new HashSet();
   
   public EvaluationContentWrapper() {;}
   
   public EvaluationContentWrapper(Id id, String title, Agent owner, 
         Date submittedDate) throws UserNotDefinedException {
      this.id = id;
      this.title = title;
      this.submittedDate = submittedDate;
      
      this.owner = UserDirectoryService.getUser(owner.getId().getValue());
   }
   
   public EvaluationContentWrapper(Id id, String title, Agent owner, 
         Date submittedDate, String type) throws UserNotDefinedException {
      
      this.id = id;
      this.title = title;
      this.submittedDate = submittedDate;
      
      this.owner = UserDirectoryService.getUser(owner.getId().getValue());
      this.evalType = type;
   }
   
   public EvaluationContentWrapper(Id id, String title, User owner, Date submittedDate) {
      this.id = id;
      this.title = title;
      this.owner = owner;
      this.submittedDate = submittedDate;
   }
   
   protected class ParamBean {
      
      private String key;
      private String value;
      
      public ParamBean(String key, String value) {
         this.key = key;
         this.value = value;
      }
      
      /**
       * @return Returns the key.
       */
      public String getKey() {
         return key;
      }

      /**
       * @param key The key to set.
       */
      public void setKey(String key) {
         this.key = key;
      }

      /**
       * @return Returns the value.
       */
      public String getValue() {
         return value;
      }

      /**
       * @param value The value to set.
       */
      public void setValue(String value) {
         this.value = value;
      }
      
   }
   
   /**
    * @return Returns the owner.
    */
   public User getOwner() {
      return owner;
   }
   /**
    * @param owner The owner to set.
    */
   public void setOwner(User owner) {
      this.owner = owner;
   }
   /**
    * @return Returns the submittedDate.
    */
   public Date getSubmittedDate() {
      return submittedDate;
   }
   /**
    * @param submittedDate The submittedDate to set.
    */
   public void setSubmittedDate(Date submittedDate) {
      this.submittedDate = submittedDate;
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
    * @return Returns the id.
    */
   public Id getId() {
      return id;
   }
   /**
    * @param id The id to set.
    */
   public void setId(Id id) {
      this.id = id;
   }

   /**
    * @return Returns the evalType.
    */
   public String getEvalType() {
      return evalType;
   }

   /**
    * @param evalType The evalType to set.
    */
   public void setEvalType(String evalType) {
      this.evalType = evalType;
   }

   /**
    * @return Returns the url.
    */
   public String getUrl() {
      return url;
   }

   /**
    * @param url The url to set.
    */
   public void setUrl(String url) {
      this.url = url;
   }

   /**
    * @return Returns the urlParams.
    */
   public Set getUrlParams() {
      return urlParams;
   }

   /**
    * @param urlParams The urlParams to set.
    */
   public void setUrlParams(Set urlParams) {
      this.urlParams = urlParams;
   }
}

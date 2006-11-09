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

/**
 * 
 */
package org.theospi.portfolio.shared.model;

import org.sakaiproject.metaobj.shared.model.Agent;
import org.sakaiproject.metaobj.shared.model.Id;
import org.sakaiproject.site.api.Site;
import org.sakaiproject.user.api.User;
import org.sakaiproject.user.api.UserNotDefinedException;
import org.sakaiproject.user.cover.UserDirectoryService;


public class SortableListObject {

   private Id id;
   private String title;
   private String description;
   private User owner;
   private Site site;
   private String type;
   
   public SortableListObject() {}
   
   public SortableListObject(Id id, String title, String description, Agent owner,
         Site site, String type) throws UserNotDefinedException {
      this.id = id;
      this.title = title;
      this.description = description;
      this.owner = UserDirectoryService.getUser(owner.getId().getValue());
      this.site = site;
      this.type = type;
   }
   
   /**
    * @return the id
    */
   public Id getId() {
      return id;
   }

   /**
    * @param id the id to set
    */
   public void setId(Id id) {
      this.id = id;
   }

   /**
    * @return the description
    */
   public String getDescription() {
      return description;
   }
   /**
    * @param description the description to set
    */
   public void setDescription(String description) {
      this.description = description;
   }
   /**
    * @return the owner
    */
   public User getOwner() {
      return owner;
   }
   /**
    * @param owner the owner to set
    */
   public void setOwner(User owner) {
      this.owner = owner;
   }
   /**
    * @return the site
    */
   public Site getSite() {
      return site;
   }
   /**
    * @param site the site to set
    */
   public void setSite(Site site) {
      this.site = site;
   }
   /**
    * @return the title
    */
   public String getTitle() {
      return title;
   }
   /**
    * @param title the title to set
    */
   public void setTitle(String title) {
      this.title = title;
   }

   /**
    * @return the type
    */
   public String getType() {
      return type;
   }

   /**
    * @param type the type to set
    */
   public void setType(String type) {
      this.type = type;
   }
   
   
}

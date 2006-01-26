/**********************************************************************************
* $URL$
* $Id$
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
package org.theospi.portfolio.matrix.model;


import org.sakaiproject.metaobj.shared.model.Agent;
import org.sakaiproject.metaobj.shared.model.Id;

import java.util.Date;

/**
 * @author chmaurer
 * 
 * @deprecated ReviewerItem is now deprecated.
 */
public class ReviewerItem {




   private Id id;
   private Date created;
   private String status;
   private Date modified;
   private Agent reviewer;
   private String comments;
   private String grade;
   private Cell cell;

   /**
    * @return
    */
   public Date getCreated() {
      return created;
   }

   /**
    * @return
    */
   public Agent getReviewer() {
      return reviewer;
   }

   /**
    * @return
    */
   public String getStatus() {
      return status;
   }

   /**
    * @return
    */
   public Date getModified() {
      return modified;
   }

   /**
    * @param date
    */
   public void setCreated(Date date) {
      created = date;
   }

   /**
    * @param agent
    */
   public void setReviewer(Agent agent) {
      reviewer = agent;
   }

   /**
    * @param string
    */
   public void setStatus(String string) {
      status = string;
   }

   /**
    * @param date
    */
   public void setModified(Date date) {
      modified = date;
   }

   /**
    * @return
    */
   public Id getId() {
      return id;
   }

   /**
    * @param id
    */
   public void setId(Id id) {
      this.id = id;
   }



   /**
    * @return
    */
   public String getComments() {
      return comments;
   }

   /**
    * @param string
    */
   public void setComments(String string) {
      comments = string;
   }

   /**
    * @return
    */
   public String getGrade() {
      return grade;
   }

   /**
    * @param string
    */
   public void setGrade(String string) {
      grade = string;
   }

   /**
    * @return Returns the wizardPage.
    */
   public Cell getCell() {
      return cell;
   }
   /**
    * @param cell The wizardPage to set.
    */
   public void setCell(Cell cell) {
      this.cell = cell;
   }
   
   public boolean equals(Object obj) {
      // TODO Auto-generated method stub
      return this.id.getValue().equals(((ReviewerItem)obj).getId().getValue());
      //return super.equals(obj);
   }
}


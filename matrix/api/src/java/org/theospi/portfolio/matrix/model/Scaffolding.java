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
import org.sakaiproject.metaobj.shared.model.IdentifiableObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * I. Communication
 * A. Writing
 * B. Public Speaking
 * II. Critical Thinking
 * A. Logic
 * B. Analysis
 * III. Integration & Application of Knowledge
 * IV. Values & Ethics
 *
 * @author apple
 */
public class Scaffolding extends IdentifiableObject implements Serializable {
   private Id id;
   private List levels = new ArrayList();
   private List criteria = new ArrayList();
   private Set scaffoldingCells = new HashSet();
   private Id ownerId;
   private String title;
   private String columnLabel;
   private String rowLabel;
   private String readyColor;
   private String pendingColor;
   private String completedColor;
   private String lockedColor;
   
   private String description;
   private String documentRoot;
   private Id privacyXsdId;
   private Id worksiteId;
   
   private boolean published = false;
   private Agent publishedBy;
   private Date publishedDate;
   
   transient private boolean validate;
   transient private String xsdName;
   
   private int workflowOption;
   
   public static final int NO_PROGRESSION = 0;
   public static final int HORIZONTAL_PROGRESSION = 1;
   public static final int VERTICAL_PROGRESSION = 2;
   public static final int OPEN_PROGRESSION = 3;
   public static final int MANUAL_PROGRESSION = 4;

   public Scaffolding() {}
   
   public Scaffolding (String columnLabel, String rowLabel, String readyColor,
         String pendingColor, String completedColor, String lockedColor) {
      this.columnLabel = columnLabel;
      this.rowLabel = rowLabel;
      this.readyColor = readyColor;
      this.pendingColor = pendingColor;
      this.completedColor = completedColor;
      this.lockedColor = lockedColor;
   }
   
   /* (non-Javadoc)
    * @see java.lang.Object#equals(java.lang.Object)
    */
   public boolean equals(Object other) {
      //TODO need better equals
      if (other == this) return true;
      if (other == null || !(other instanceof Scaffolding)) return false;
      return (this.getId().equals(((Scaffolding) other).getId()));

   }

   /* (non-Javadoc)
    * @see java.lang.Object#hashCode()
    */
   public int hashCode() {
      //TODO need better hashcode
      Id id = this.getId();
      if (id == null) return 0;
      return id.getValue().hashCode();
   }

   /**
    * Typical levels might be Beginner, Intermediate, Advanced
    */
   public List getLevels() {
      return levels;
   }

   /**
    * @return List of Criteria
    */
   public List getCriteria() {
      return criteria;
   }


   /**
    * @return Returns the owner.
    */
   public Id getOwnerId() {
      return ownerId;
   }

   /**
    * @param owner The owner to set.
    */
   public void setOwnerId(Id ownerId) {
      this.ownerId = ownerId;
   }

   /**
    * @param criteria The criteria to set.
    */
   public void setCriteria(List criteria) {
      this.criteria = criteria;
   }

   /**
    * @param levels The levels to set.
    */
   public void setLevels(List levels) {
      this.levels = levels;
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

   public void add(Criterion criterion) {
      this.getCriteria().add(criterion);
   }

   public void add(Level level) {
      this.getLevels().add(level);
   }
   
   public void add(ScaffoldingCell scaffoldingCell) {
      this.getScaffoldingCells().add(scaffoldingCell);
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
    * @return Returns the expectations.
    */
   public Set getScaffoldingCells() {
      return scaffoldingCells;
   }
   /**
    * @param expectations The expectations to set.
    */
   public void setScaffoldingCells(Set scaffoldingCells) {
      this.scaffoldingCells = scaffoldingCells;
   }
   /**
    * @return Returns the privacyDocRoot.
    */
   public String getDocumentRoot() {
      return documentRoot;
   }
   /**
    * @param privacyDocRoot The privacyDocRoot to set.
    */
   public void setDocumentRoot(String documentRoot) {
      this.documentRoot = documentRoot;
   }
   /**
    * @return Returns the privacyXsdId.
    */
   public Id getPrivacyXsdId() {
      return privacyXsdId;
   }
   /**
    * @param privacyXsdId The privacyXsdId to set.
    */
   public void setPrivacyXsdId(Id privacyXsdId) {
      this.privacyXsdId = privacyXsdId;
   }
   /**
    * @return Returns the description.
    */
   public String getDescription() {
      return description;
   }
   /**
    * @param description The description to set.
    */
   public void setDescription(String description) {
      this.description = description;
   }
  
   /**
    * @return Returns the worksiteId.
    */
   public Id getWorksiteId() {
      return worksiteId;
   }
   /**
    * @param worksiteId The worksiteId to set.
    */
   public void setWorksiteId(Id worksiteId) {
      this.worksiteId = worksiteId;
   }

   /*
    * 
    *  This commented method was replaced with a the lighter weight method below it
    * 
   public void setXsdNode(Node xsdNode) {
      this.xsdName = xsdNode.getDisplayName();
   }
   */

   public void setXsdNodeName(String name) {
       this.xsdName = name;
    }

   
   /**
    * @return Returns the xsdName.
    */
   public String getXsdName() {
      return xsdName;
   }
   /**
    * @param xsdName The xsdName to set.
    */
   public void setXsdName(String xsdName) {
      this.xsdName = xsdName;
   }
   /**
    * @return Returns the validate.
    */
   public boolean isValidate() {
      return validate;
   }
   /**
    * @param validate The validate to set.
    */
   public void setValidate(boolean validate) {
      this.validate = validate;
   }
   public boolean isPublished() {
      return published;
   }
   public void setPublished(boolean published) {
      this.published = published;
   }
   public Agent getPublishedBy() {
      return publishedBy;
   }
   public void setPublishedBy(Agent publishedBy) {
      this.publishedBy = publishedBy;
   }
   public Date getPublishedDate() {
      return publishedDate;
   }
   public void setPublishedDate(Date publishedDate) {
      this.publishedDate = publishedDate;
   }

   public String getColumnLabel() {
      return columnLabel;
   }

   public void setColumnLabel(String columnLabel) {
      this.columnLabel = columnLabel;
   }

   public String getRowLabel() {
      return rowLabel;
   }

   public void setRowLabel(String rowLabel) {
      this.rowLabel = rowLabel;
   }

   public String getCompletedColor() {
      return completedColor;
   }

   public void setCompletedColor(String completedColor) {
      this.completedColor = completedColor;
   }

   public String getLockedColor() {
      return lockedColor;
   }

   public void setLockedColor(String lockedColor) {
      this.lockedColor = lockedColor;
   }

   public String getPendingColor() {
      return pendingColor;
   }

   public void setPendingColor(String pendingColor) {
      this.pendingColor = pendingColor;
   }

   public String getReadyColor() {
      return readyColor;
   }

   public void setReadyColor(String readyColor) {
      this.readyColor = readyColor;
   }
   
   /**
    * @return Returns the workflowOption.
    */
   public int getWorkflowOption() {
      return workflowOption;
   }

   /**
    * @param workflowOption The workflowOption to set.
    */
   public void setWorkflowOption(int workflowOption) {
      this.workflowOption = workflowOption;
   }
}

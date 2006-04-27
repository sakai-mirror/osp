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
package org.theospi.portfolio.wizard.model;

import java.util.Date;

import org.sakaiproject.metaobj.shared.model.Agent;
import org.sakaiproject.metaobj.shared.model.Id;
import org.theospi.portfolio.guidance.model.Guidance;
import org.theospi.portfolio.shared.model.ObjectWithWorkflow;
import org.theospi.portfolio.style.model.Style;

/**
 * The super class has the evaluation, reflection and review
 * 
 */

public class Wizard extends ObjectWithWorkflow {

   public final static String WIZARD_TYPE_SEQUENTIAL = "org.theospi.portfolio.wizard.model.Wizard.sequential";
   public final static String WIZARD_TYPE_HIERARCHICAL = "org.theospi.portfolio.wizard.model.Wizard.hierarchical";

   public final static String ROOT_TITLE = "root";
   
   private String name;
   private String description;
   private String keywords;
   private Date created;
   private Date modified;
   private transient Agent owner;
   private Id guidanceId;
   //private Set supportItems = new HashSet();
   private boolean published = false;
   private String type = WIZARD_TYPE_SEQUENTIAL;
   //private List wizardStyleItems = new ArrayList();
   private String exposedPageId;
   private transient Boolean exposeAsTool = null;
   
   private String siteId;
   private String toolId;
   private WizardCategory rootCategory;
   private int sequence = 0;
   private Style style;
   private transient Id styleId;

   private transient Guidance guidance;
   
   private boolean newObject = false;
   
   public Wizard() {
   }

   public Wizard(Id id, Agent owner, String siteId, String toolId) {
      setId(id);
      this.owner = owner;
      this.siteId = siteId;
      this.toolId = toolId;
      newObject = true;
      rootCategory = new WizardCategory(this);
      rootCategory.setTitle(ROOT_TITLE);
   }
   
   public String getType() {
      return type;
   }
   public void setType(String type) {
      this.type = type;
   }
   
   public Date getCreated() {
      return created;
   }
   public void setCreated(Date created) {
      this.created = created;
   }
   public String getDescription() {
      return description;
   }
   public void setDescription(String description) {
      this.description = description;
   }

   public Date getModified() {
      return modified;
   }
   public void setModified(Date modified) {
      this.modified = modified;
   }
   public String getName() {
      return name;
   }
   public void setName(String name) {
      this.name = name;
   }
   public Agent getOwner() {
      return owner;
   }
   public void setOwner(Agent owner) {
      this.owner = owner;
   }
   public boolean isNewObject() {
      return newObject;
   }
   public void setNewObject(boolean newObject) {
      this.newObject = newObject;
   }
   public String getSiteId() {
      return siteId;
   }
   public void setSiteId(String siteId) {
      this.siteId = siteId;
   }
   public String getKeywords() {
      return keywords;
   }
   public void setKeywords(String keywords) {
      this.keywords = keywords;
   }
   public Guidance getGuidance() {
      return guidance;
   }
   public void setGuidance(Guidance guidance) {
      this.guidance = guidance;
   }

   public Id getGuidanceId() {
      return guidanceId;
   }

   public void setGuidanceId(Id guidanceId) {
      this.guidanceId = guidanceId;
   }

   public boolean isPublished() {
      return published;
   }

   public void setPublished(boolean published) {
      this.published = published;
   }

   public Boolean getExposeAsTool() {
      return exposeAsTool;
   }

   public void setExposeAsTool(Boolean exposeAsTool) {
      this.exposeAsTool = exposeAsTool;
   }

   public String getExposedPageId() {
      return exposedPageId;
   }

   public void setExposedPageId(String exposedPageId) {
      this.exposedPageId = exposedPageId;
   }

   public String getToolId() {
      return toolId;
   }

   public void setToolId(String toolId) {
      this.toolId = toolId;
   }

   public WizardCategory getRootCategory() {
      return rootCategory;
   }

   public void setRootCategory(WizardCategory rootCategory) {
      this.rootCategory = rootCategory;
   }

   public int getSequence() {
      return sequence;
   }

   public void setSequence(int sequence) {
      this.sequence = sequence;
   }

   public Style getStyle() {
      return style;
   }

   public void setStyle(Style style) {
      this.style = style;
   }

   public Id getStyleId() {
      return styleId;
   }

   public void setStyleId(Id styleId) {
      this.styleId = styleId;
   }
   public boolean getHasPages()
   {
      return rootCategory.getHasPages();
   }
}

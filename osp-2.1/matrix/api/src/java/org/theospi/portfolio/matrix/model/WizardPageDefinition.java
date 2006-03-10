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

import org.sakaiproject.metaobj.shared.model.Id;
import org.theospi.portfolio.guidance.model.Guidance;
import org.theospi.portfolio.shared.model.ObjectWithWorkflow;
import org.theospi.portfolio.style.model.Style;

import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Jan 11, 2006
 * Time: 4:14:09 PM
 * To change this template use File | Settings | File Templates.
 */
public class WizardPageDefinition extends ObjectWithWorkflow {

   private String title;
   private String description;
   private String initialStatus = "";
   private Collection evaluators = new HashSet();
   transient private boolean validate;
   private Set pages = new HashSet();
   transient private Id guidanceId;
   private Guidance guidance;
   transient private Id deleteGuidanceId;
   
   private String siteId;
   private String toolId;
   private Style style;
   
   transient private Id styleId;

   private List additionalForms = new ArrayList();

   /**
    * @return Returns the initialStatus.
    */
   public String getInitialStatus() {
      return initialStatus.toUpperCase();
   }
   /**
    * @param initialStatus The initialStatus to set.
    */
   public void setInitialStatus(String initialStatus) {
      this.initialStatus = initialStatus.toUpperCase();
   }

   /**
    * @return Returns the evaluators.
    */
   public Collection getEvaluators() {
      return evaluators;
   }
   /**
    * @param reviewers The evaluators to set.
    */
   public void setEvaluators(Collection evaluators) {
      this.evaluators = evaluators;
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
   public Set getPages() {
      return pages;
   }
   public void setPages(Set pages) {
      this.pages = pages;
   }

   /**
    * This is the transient property.
    * @return Returns the guidanceId.
    */
   public Id getGuidanceId() {
      return guidanceId;
   }
   /**
    * This is the transient property.  This will not save to the database.
    * Use setGuidance to save the guidance to the database.
    * @param guidanceId The guidanceId to set.
    */
   public void setGuidanceId(Id guidanceId) {
      this.guidanceId = guidanceId;
   }
   /**
    * @return Returns the guidance.
    */
   public Guidance getGuidance() {
      return guidance;
   }
   /**
    * @param guidance The guidance to set.
    */
   public void setGuidance(Guidance guidance) {
      this.guidance = guidance;
   }

   /**
    * @return Returns the deleteGuidanceId.
    */
   public Id getDeleteGuidanceId() {
      return deleteGuidanceId;
   }
   /**
    * @param deleteGuidanceId The deleteGuidanceId to set.
    */
   public void setDeleteGuidanceId(Id deleteGuidanceId) {
      this.deleteGuidanceId = deleteGuidanceId;
   }
   /**
    * List of Strings of the form Ids
    * @return Returns the additionalForms.
    */
   public List getAdditionalForms() {
      return additionalForms;
   }
   /**
    * @param additionalForms The additionalForms to set.
    */
   public void setAdditionalForms(List additionalForms) {
      this.additionalForms = additionalForms;
   }

   public String getTitle() {
      return title;
   }

   public void setTitle(String title) {
      this.title = title;
   }

   public String getDescription() {
      return description;
   }

   public void setDescription(String description) {
      this.description = description;
   }

   public String getSiteId() {
      return siteId;
   }

   public void setSiteId(String siteId) {
      this.siteId = siteId;
   }
   public String getToolId() {
      return toolId;
   }
   public void setToolId(String toolId) {
      this.toolId = toolId;
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

}

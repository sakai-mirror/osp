
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

import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.sakaiproject.metaobj.shared.model.Id;
import org.sakaiproject.metaobj.shared.model.IdentifiableObject;
import org.theospi.portfolio.guidance.model.Guidance;

/**
 * @author chmaurer
 */
public class ScaffoldingCell extends IdentifiableObject implements Serializable {
   private Criterion rootCriterion;
   private Level level;
   private Scaffolding scaffolding;
   private Set cells = new HashSet();
   private WizardPageDefinition wizardPageDefinition;

   public ScaffoldingCell() {
      this.wizardPageDefinition = new WizardPageDefinition();
   }

   public ScaffoldingCell(Criterion criterion, Level level, String initialStatus, Scaffolding scaffolding) {
      this.rootCriterion = criterion;
      this.level = level;
      this.wizardPageDefinition = new WizardPageDefinition();
      wizardPageDefinition.setInitialStatus(initialStatus);
      wizardPageDefinition.setSiteId(scaffolding.getWorksiteId().getValue());
      this.scaffolding = scaffolding;
   }
   
   public boolean equals(Object other) {
      if (other == this) return true;
      if (other == null || !(other instanceof ScaffoldingCell)) return false;
      //TODO need better equals method
      if (this.getId() == null) return false;
      return (this.getId().equals(((ScaffoldingCell) other).getId()));

   }
   
   /**
    * @return String Returns the title.
    */
   public String getTitle() {
      return wizardPageDefinition.getTitle();
   }
   /**
    * @param title String The title to set.
    */
   public void setTitle(String title) {
      wizardPageDefinition.setTitle(title);
   }
   
   /**
    * @return Returns the level.
    */
   public Level getLevel() {
      return level;
   }
   /**
    * @param level The level to set.
    */
   public void setLevel(Level level) {
      this.level = level;
   }
   /**
    * @return Returns the rootCriterion.
    */
   public Criterion getRootCriterion() {
      return rootCriterion;
   }
   /**
    * @param rootCriterion The rootCriterion to set.
    */
   public void setRootCriterion(Criterion rootCriterion) {
      this.rootCriterion = rootCriterion;
   }
   /**
    * @return Returns the scaffolding.
    */
   public Scaffolding getScaffolding() {
      return scaffolding;
   }
   /**
    * @param scaffolding The scaffolding to set.
    */
   public void setScaffolding(Scaffolding scaffolding) {
      this.scaffolding = scaffolding;
   }
   /**
    * @return Returns the initialStatus.
    */
   public String getInitialStatus() {
      return wizardPageDefinition.getInitialStatus();
   }
   /**
    * @param initialStatus The initialStatus to set.
    */
   public void setInitialStatus(String initialStatus) {
      wizardPageDefinition.setInitialStatus(initialStatus);
   }

   /**
    * @return Returns the evaluators.
    */
   public Collection getEvaluators() {
      return wizardPageDefinition.getEvaluators();
   }
   /**
    * @param evaluators The evaluators to set.
    */
   public void setEvaluators(Collection evaluators) {
      wizardPageDefinition.setEvaluators(evaluators);
   }
   /**
    * @return Returns the validate.
    */
   public boolean isValidate() {
      return wizardPageDefinition.isValidate();
   }
   /**
    * @param validate The validate to set.
    */
   public void setValidate(boolean validate) {
      wizardPageDefinition.setValidate(validate);
   }
   public Set getCells() {
      return cells;
   }
   public void setCells(Set cells) {
      this.cells = cells;
   }
   public Id getEvaluationDevice() {
      return wizardPageDefinition.getEvaluationDevice();
   }
   public void setEvaluationDevice(Id evaluationDevice) {
      wizardPageDefinition.setEvaluationDevice(evaluationDevice);
   }
   public String getEvaluationDeviceType() {
      return wizardPageDefinition.getEvaluationDeviceType();
   }
   public void setEvaluationDeviceType(String evaluationDeviceType) {
      wizardPageDefinition.setEvaluationDeviceType(evaluationDeviceType);
   }
   public Id getReflectionDevice() {
      return wizardPageDefinition.getReflectionDevice();
   }
   public void setReflectionDevice(Id reflectionDevice) {
      wizardPageDefinition.setReflectionDevice(reflectionDevice);
   }
   public String getReflectionDeviceType() {
      return wizardPageDefinition.getReflectionDeviceType();
   }
   public void setReflectionDeviceType(String reflectionDeviceType) {
      wizardPageDefinition.setReflectionDeviceType(reflectionDeviceType);
   }
   
   
   /**
    * This is for getting the feedback/comments form id
    * @param reviewDevice
    */
   public Id getReviewDevice() {
      return wizardPageDefinition.getReviewDevice();
   }
   /**
    * This is for setting the feedback/comments form id
    * @param reviewDevice
    */
   public void setReviewDevice(Id reviewDevice) {
      wizardPageDefinition.setReviewDevice(reviewDevice);
   }
   public String getReviewDeviceType() {
      return wizardPageDefinition.getReviewDeviceType();
   }
   public void setReviewDeviceType(String reviewDeviceType) {
      wizardPageDefinition.setReviewDeviceType(reviewDeviceType);
   }

   /**
    * @return Returns the guidanceId.
    */
   public Id getGuidanceId() {
      return wizardPageDefinition.getGuidanceId();
   }
   /**
    * @param guidanceId The guidanceId to set.
    */
   public void setGuidanceId(Id guidanceId) {
      wizardPageDefinition.setGuidanceId(guidanceId);
   }
   /**
    * @return Returns the guidance.
    */
   public Guidance getGuidance() {
      return wizardPageDefinition.getGuidance();
   }
   /**
    * @param guidance The guidance to set.
    */
   public void setGuidance(Guidance guidance) {
      wizardPageDefinition.setGuidance(guidance);
   }

   /**
    * @return Returns the deleteGuidanceId.
    */
   public Id getDeleteGuidanceId() {
      return wizardPageDefinition.getDeleteGuidanceId();
   }
   /**
    * @param deleteGuidanceId The deleteGuidanceId to set.
    */
   public void setDeleteGuidanceId(Id deleteGuidanceId) {
      wizardPageDefinition.setDeleteGuidanceId(deleteGuidanceId);
   }
   /**
    * @return Returns the additionalForms.
    */
   public List getAdditionalForms() {
      return wizardPageDefinition.getAdditionalForms();
   }
   /**
    * @param additionalForms The additionalForms to set.
    */
   public void setAdditionalForms(List additionalForms) {
      wizardPageDefinition.setAdditionalForms(additionalForms);
   }

   public WizardPageDefinition getWizardPageDefinition() {
      return wizardPageDefinition;
   }

   public void setWizardPageDefinition(WizardPageDefinition wizardPageDefinition) {
      this.wizardPageDefinition = wizardPageDefinition;
   }
   
   public boolean isSuppressItems(){
	   return wizardPageDefinition.isSuppressItems();
   }
   
   public void setSuppressItems(boolean suppressItems){
	   wizardPageDefinition.setSuppressItems(suppressItems);
   }
}

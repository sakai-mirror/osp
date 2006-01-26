/*
 * The Open Source Portfolio Initiative Software is Licensed under the Educational Community License Version 1.0:
 *
 * This Educational Community License (the "License") applies to any original work of authorship
 * (the "Original Work") whose owner (the "Licensor") has placed the following notice immediately
 * following the copyright notice for the Original Work:
 *
 * Copyright (c) 2004 Trustees of Indiana University and r-smart Corporation
 *
 * This Original Work, including software, source code, documents, or other related items, is being
 * provided by the copyright holder(s) subject to the terms of the Educational Community License.
 * By obtaining, using and/or copying this Original Work, you agree that you have read, understand,
 * and will comply with the following terms and conditions of the Educational Community License:
 *
 * Permission to use, copy, modify, merge, publish, distribute, and sublicense this Original Work and
 * its documentation, with or without modification, for any purpose, and without fee or royalty to the
 * copyright holder(s) is hereby granted, provided that you include the following on ALL copies of the
 * Original Work or portions thereof, including modifications or derivatives, that you make:
 *
 * - The full text of the Educational Community License in a location viewable to users of the
 * redistributed or derivative work.
 *
 * - Any pre-existing intellectual property disclaimers, notices, or terms and conditions.
 *
 * - Notice of any changes or modifications to the Original Work, including the date the changes were made.
 *
 * - Any modifications of the Original Work must be distributed in such a manner as to avoid any confusion
 *  with the Original Work of the copyright holders.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
 * SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 * The name and trademarks of copyright holder(s) may NOT be used in advertising or publicity pertaining
 * to the Original or Derivative Works without specific, written prior permission. Title to copyright
 * in the Original Work and any associated documentation will at all times remain with the copyright holders.
 *
 * $Header: /opt/CVS/osp2.x/matrix/api/src/java/org/theospi/portfolio/matrix/model/ScaffoldingCell.java,v 1.1 2005/07/14 20:41:24 rpembry Exp $
 * $Revision$
 * $Date$
 */


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
    * @return Returns the reviewers.
    */
   public Collection getReviewers() {
      return wizardPageDefinition.getReviewers();
   }
   /**
    * @param reviewers The reviewers to set.
    */
   public void setReviewers(Collection reviewers) {
      wizardPageDefinition.setReviewers(reviewers);
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
   public Id getReviewDevice() {
      return wizardPageDefinition.getReviewDevice();
   }
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
}

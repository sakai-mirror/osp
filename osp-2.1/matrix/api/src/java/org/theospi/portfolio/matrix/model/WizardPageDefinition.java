/**********************************************************************************
* $URL$
* $Id$
***********************************************************************************
*
* Copyright (c) 2003, 2004 The Regents of the University of Michigan, Trustees of Indiana University,
*                  Board of Trustees of the Leland Stanford, Jr., University, and The MIT Corporation
*
* Licensed under the Educational Community License Version 1.0 (the "License");
* By obtaining, using and/or copying this Original Work, you agree that you have read,
* understand, and will comply with the terms and conditions of the Educational Community License.
* You may obtain a copy of the License at:
*
*      http://cvs.sakaiproject.org/licenses/license_1_0.html
*
* THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
* INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE
* AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
* DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
* FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*
**********************************************************************************/
package org.theospi.portfolio.matrix.model;

import org.sakaiproject.metaobj.shared.model.IdentifiableObject;
import org.sakaiproject.metaobj.shared.model.Id;
import org.theospi.portfolio.guidance.model.Guidance;
import org.theospi.portfolio.workflow.model.Workflow;

import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Jan 11, 2006
 * Time: 4:14:09 PM
 * To change this template use File | Settings | File Templates.
 */
public class WizardPageDefinition extends IdentifiableObject {

   private String initialStatus = "";
   private Collection reviewers = new HashSet();
   transient private boolean validate;
   private Set pages = new HashSet();
   transient private Id guidanceId;
   private Guidance guidance;
   transient private Id deleteGuidanceId;
   private Workflow submitWorkflow;
   transient private Id submitWorkflowId;

   private Id reflectionDevice;
   private String reflectionDeviceType;
   private Id evaluationDevice;
   private String evaluationDeviceType;
   private Id reviewDevice;
   private String reviewDeviceType;

   private List additionalForms = new ArrayList();

   public WizardPageDefinition() {;}

   public boolean equals(Object other) {
      if (other == this) return true;
      if (other == null || !(other instanceof WizardPageDefinition)) return false;
      //TODO need better equals method
      if (this.getId() == null) return false;
      return (this.getId().equals(((WizardPageDefinition) other).getId()));

   }

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
    * @return Returns the reviewers.
    */
   public Collection getReviewers() {
      return reviewers;
   }
   /**
    * @param reviewers The reviewers to set.
    */
   public void setReviewers(Collection reviewers) {
      this.reviewers = reviewers;
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
   public Id getEvaluationDevice() {
      return evaluationDevice;
   }
   public void setEvaluationDevice(Id evaluationDevice) {
      this.evaluationDevice = evaluationDevice;
   }
   public String getEvaluationDeviceType() {
      return evaluationDeviceType;
   }
   public void setEvaluationDeviceType(String evaluationDeviceType) {
      this.evaluationDeviceType = evaluationDeviceType;
   }
   public Id getReflectionDevice() {
      return reflectionDevice;
   }
   public void setReflectionDevice(Id reflectionDevice) {
      this.reflectionDevice = reflectionDevice;
   }
   public String getReflectionDeviceType() {
      return reflectionDeviceType;
   }
   public void setReflectionDeviceType(String reflectionDeviceType) {
      this.reflectionDeviceType = reflectionDeviceType;
   }
   public Id getReviewDevice() {
      return reviewDevice;
   }
   public void setReviewDevice(Id reviewDevice) {
      this.reviewDevice = reviewDevice;
   }
   public String getReviewDeviceType() {
      return reviewDeviceType;
   }
   public void setReviewDeviceType(String reviewDeviceType) {
      this.reviewDeviceType = reviewDeviceType;
   }

   /**
    * @return Returns the guidanceId.
    */
   public Id getGuidanceId() {
      return guidanceId;
   }
   /**
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
    * @return Returns the submitWorkflow.
    */
   public Workflow getSubmitWorkflow() {
      return submitWorkflow;
   }
   /**
    * @param submitWorkflow The submitWorkflow to set.
    */
   public void setSubmitWorkflow(Workflow submitWorkflow) {
      this.submitWorkflow = submitWorkflow;
   }
   /**
    * @return Returns the submitWorkflowId.
    */
   public Id getSubmitWorkflowId() {
      return submitWorkflowId;
   }
   /**
    * @param submitWorkflowId The submitWorkflowId to set.
    */
   public void setSubmitWorkflowId(Id submitWorkflowId) {
      this.submitWorkflowId = submitWorkflowId;
   }
   /**
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

}

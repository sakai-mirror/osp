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
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.sakaiproject.metaobj.shared.model.Id;
import org.sakaiproject.metaobj.shared.model.IdentifiableObject;

/**
 * @author chmaurer
 */
public class ScaffoldingCell extends IdentifiableObject implements Serializable {
   private Id id;
   private Criterion rootCriterion;
   private Level level;
   private String expectationHeader = "";
   private List expectations = new ArrayList();
   private Scaffolding scaffolding;
   private String initialStatus = "";
   private boolean gradableReflection;
   private Collection reviewers = new HashSet();
   transient private boolean validate;
   private Set cells = new HashSet();

   public ScaffoldingCell() {;}
   public ScaffoldingCell(Criterion criterion, Level level, String initialStatus, Scaffolding scaffolding) {
      this.rootCriterion = criterion;
      this.level = level;
      this.initialStatus = initialStatus;
      this.scaffolding = scaffolding;
   }
   
   public void add(Expectation expectation) {
      this.getExpectations().add(expectation);
   }
   
   public boolean equals(Object other) {
      if (other == this) return true;
      if (other == null || !(other instanceof ScaffoldingCell)) return false;
      //TODO need better equals method
      if (this.getId() == null) return false;
      return (this.getId().equals(((ScaffoldingCell) other).getId()));

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
    * @return Returns the expectations.
    */
   public List getExpectations() {
      return expectations;
   }
   /**
    * @param expectations The expectations to set.
    */
   public void setExpectations(List expectations) {
      this.expectations = expectations;
   }
   /**
    * @return Returns the gradableReflection.
    */
   public boolean isGradableReflection() {
      return gradableReflection;
   }
   /**
    * @param gradableReflection The gradableReflection to set.
    */
   public void setGradableReflection(boolean gradableReflection) {
      this.gradableReflection = gradableReflection;
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
    * @return Returns the expectationHeader.
    */
   public String getExpectationHeader() {
      return expectationHeader;
   }
   /**
    * @param expectationHeader The expectationHeader to set.
    */
   public void setExpectationHeader(String expectationHeader) {
      this.expectationHeader = expectationHeader;
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
   public Set getCells() {
      return cells;
   }
   public void setCells(Set cells) {
      this.cells = cells;
   }
}

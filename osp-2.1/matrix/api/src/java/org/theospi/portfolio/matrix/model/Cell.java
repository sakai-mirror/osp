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
 * $Header: /opt/CVS/osp2.x/matrix/api/src/java/org/theospi/portfolio/matrix/model/Cell.java,v 1.1 2005/07/14 20:41:24 rpembry Exp $
 * $Revision$
 * $Date$
 */
/*
 * Created on May 21, 2004
 *
 */
package org.theospi.portfolio.matrix.model;



import java.util.HashSet;
import java.util.Set;

import org.sakaiproject.metaobj.shared.model.Id;

/**
 * @author rpembry
 */
public class Cell {

   private Id id;
   private Matrix matrix;
   private Set attachments = new HashSet();
   private Reflection reflection;
   private Set reviewerItems = new HashSet();
   private String status;
   private ScaffoldingCell scaffoldingCell;
   private Set cellForms = new HashSet();

   public Reflection getReflection() {
      return reflection;
   }

   public void setReflection(Reflection reflection) {
      this.reflection = reflection;
   }

   /**
    * @return Returns Set of Attachments
    */
   public Set getAttachments() {
      return attachments;
   }

   /**
    * @param attachments A Set of Attachments to set.
    */
   public void setAttachments(Set attachments) {
      this.attachments = attachments;
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
    * @return Returns the status.
    */
   public String getStatus() {
      return status.toUpperCase();
   }

   /**
    * @param status The status to set.
    */
   public void setStatus(String status) {
      this.status = status.toUpperCase();
   }

   /**
    * @return Returns the matrix.
    */
   public Matrix getMatrix() {
      return matrix;
   }

   /**
    * @param matrix The matrix to set.
    */
   public void setMatrix(Matrix matrix) {
      this.matrix = matrix;
   }


   /* (non-Javadoc)
    * @see java.lang.Object#equals(java.lang.Object)
    */
   public boolean equals(Object other) {
      if (other == this) return true;
      if (other == null || !(other instanceof Cell)) return false;
      //TODO need better equals method
      if (this.getId() == null) return false;
      return (this.getId().equals(((Cell) other).getId()));

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
    * @return
    */
   public ReviewerItem getReviewerItem() {
      if (reviewerItems.size() > 0)
         return (ReviewerItem)reviewerItems.toArray()[0];
      return null;
   }

   /**
    * @param item
    */
   public void setReviewerItem(ReviewerItem reviewerItem) {
      if (reviewerItem != null) {
         this.reviewerItems.add(reviewerItem);
      }
   }


   /**
    * @return Returns the scaffoldingCell.
    */
   public ScaffoldingCell getScaffoldingCell() {
      return scaffoldingCell;
   }
   /**
    * @param scaffoldingCell The scaffoldingCell to set.
    */
   public void setScaffoldingCell(ScaffoldingCell scaffoldingCell) {
      this.scaffoldingCell = scaffoldingCell;
   }
   /**
    * @return Returns the reviewerItems.
    */
   public Set getReviewerItems() {
      return reviewerItems;
   }
   /**
    * @param reviewerItems The reviewerItems to set.
    */
   public void setReviewerItems(Set reviewerItems) {
      this.reviewerItems = reviewerItems;
   }

   /**
    * @return Returns the cellForms.
    */
   public Set getCellForms() {
      return cellForms;
   }

   /**
    * @param cellForms The cellForms to set.
    */
   public void setCellForms(Set cellForms) {
      this.cellForms = cellForms;
   }
}

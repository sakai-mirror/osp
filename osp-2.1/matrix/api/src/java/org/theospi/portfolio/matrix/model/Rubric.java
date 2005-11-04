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
 * $Header: /opt/CVS/osp2.x/matrix/api/src/java/org/theospi/portfolio/matrix/model/Rubric.java,v 1.1 2005/07/14 20:41:24 rpembry Exp $
 * $Revision$
 * $Date$
 */
/*
 * Created on May 23, 2004
 */
package org.theospi.portfolio.matrix.model;

import java.io.Serializable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.metaobj.shared.model.Id;
import org.sakaiproject.metaobj.shared.model.IdentifiableObject;
import org.sakaiproject.metaobj.shared.model.MimeType;

/**
 * @author apple
 */
public class Rubric extends IdentifiableObject implements Serializable {
   private final Log logger = LogFactory.getLog(getClass());

   private Id id;
   private Criterion criterion;
   private Level level;
   private int quantity;
   private Id type;
   private MimeType mimeType;
   private Scaffolding scaffolding;

   public String toString() {
      return "<(Rubric) Criterion: " + criterion + ",Level: " + level + ",Type: " + type + ",MimeType: " + mimeType + ",Quantity: " + quantity + "[" + id + "]>";
   }

   public Rubric() {
   }

   /**
    * @return Returns the criterion.
    */
   public Criterion getCriterion() {
      return criterion;
   }

   /**
    * @param criterion The criterion to set.
    */
   public void setCriterion(Criterion criterion) {
      this.criterion = criterion;
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
    * @return Returns the mimeType.
    */
   public MimeType getMimeType() {
      return mimeType;
   }

   /**
    * @param mimeType The mimeType to set.
    */
   public void setMimeType(MimeType mimeType) {
      this.mimeType = mimeType;
   }

   /**
    * @return Returns the quantity.
    */
   public int getQuantity() {
      return quantity;
   }

   /**
    * @param quantity The quantity to set.
    */
   public void setQuantity(int quantity) {
      this.quantity = quantity;
   }

   /**
    * @return Returns the type.
    */
   public Id getType() {
      return type;
   }

   /**
    * @param type The type to set.
    */
   public void setType(Id type) {
      this.type = type;
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

   /* (non-Javadoc)
    * @see java.lang.Object#equals(java.lang.Object)
    */
   public boolean equals(Object other) {
      if (other == this) return true;
      if (other == null || !(other instanceof Rubric)) return false;

      Rubric otherRubric = (Rubric) other;

      if (otherRubric.getQuantity() != this.getQuantity()) return false;
      if (!(otherRubric.getCriterion().equals(this.getCriterion()))) return false;
      if (!(otherRubric.getLevel().equals(this.getLevel()))) return false;
      if (!(otherRubric.getMimeType().equals(this.getMimeType()))) return false;
      if (!(otherRubric.getScaffolding().equals(this.getScaffolding()))) return false;

      return true;

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
}

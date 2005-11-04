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
 * $Header: /opt/CVS/osp2.x/matrix/api/src/java/org/theospi/portfolio/matrix/model/Matrix.java,v 1.1 2005/07/14 20:41:24 rpembry Exp $
 * $Revision$
 * $Date$
 */
/*
 * Created on May 21, 2004
 *
 */
package org.theospi.portfolio.matrix.model;


import org.sakaiproject.metaobj.shared.mgt.ReadableObjectHome;
import org.sakaiproject.metaobj.shared.model.Agent;
import org.sakaiproject.metaobj.shared.model.Artifact;
import org.sakaiproject.metaobj.shared.model.Id;

import java.util.HashSet;
import java.util.Set;

/**
 * @author rpembry
 */
public class Matrix implements Artifact {

   private Id id;
   private Agent owner;
   private MatrixTool matrixTool;
   private Set cells = new HashSet();
   private ReadableObjectHome home;


   /**
    * @return Returns the cells.
    */
   public Set getCells() {
      return cells;
   }

   /**
    * @param cells The cells to set.
    */
   public void setCells(Set cells) {
      this.cells = cells;
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
    * @return Returns the scaffoldId.
    */
   public MatrixTool getMatrixTool() {
      return matrixTool;
   }

   /**
    * @param scaffoldId The scaffoldId to set.
    */
   public void setMatrixTool(MatrixTool matrixTool) {
      this.matrixTool = matrixTool;
   }

   public void add(Cell cell) {
      this.getCells().add(cell);
      cell.setMatrix(this);
   }

   /* (non-Javadoc)
    * @see java.lang.Object#equals(java.lang.Object)
    */
   public boolean equals(Object other) {
      if (other == this) return true;
      if (other == null || !(other instanceof Matrix)) return false;
      //TODO need better equals method
      return (this.getId().equals(((Matrix) other).getId()));

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

/* (non-Javadoc)
 * @see org.theospi.portfolio.shared.model.Artifact#getOwner()
 */
   public Agent getOwner() {
      return owner;
   }
   
   public void setOwner(Agent owner) {
      this.owner = owner;
   }

/* (non-Javadoc)
 * @see org.theospi.portfolio.shared.model.Artifact#getHome()
 */
   public ReadableObjectHome getHome() {
      return home;
   }

/* (non-Javadoc)
 * @see org.theospi.portfolio.shared.model.Artifact#setHome(org.theospi.portfolio.shared.mgt.ReadableObjectHome)
 */
   public void setHome(ReadableObjectHome home) {
      this.home = home;

   }

/* (non-Javadoc)
 * @see org.theospi.portfolio.shared.model.Artifact#getDisplayName()
 */
   public String getDisplayName() {
      return matrixTool.getScaffolding().getTitle();
   }
}

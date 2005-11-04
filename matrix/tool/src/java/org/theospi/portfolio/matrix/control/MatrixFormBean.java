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
 * $Header: /opt/CVS/osp2.x/matrix/tool/src/java/org/theospi/portfolio/matrix/control/MatrixFormBean.java,v 1.1 2005/07/15 21:10:34 rpembry Exp $
 * $Revision$
 * $Date$
 */
/*
 * Created on May 24, 2004
 *
 */
package org.theospi.portfolio.matrix.control;

import org.sakaiproject.metaobj.shared.model.Id;
import org.theospi.portfolio.matrix.model.Criterion;
import org.theospi.portfolio.matrix.model.Matrix;

import java.util.List;

/**
 * @author apple
 */
public class MatrixFormBean {

   private String action;
   private Id nodeId;
   private Id cellId;
   private Matrix matrix;
   private List criteria;
   private String[] selectedCriteria;


   /**
    * @return Returns the action.
    */
   public String getAction() {
      return action;
   }

   /**
    * @param action The action to set.
    */
   public void setAction(String action) {
      this.action = action;
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

   /**
    * @return Returns the nodeId.
    */
   public Id getNodeId() {
      return nodeId;
   }

   /**
    * @param nodeId The nodeId to set.
    */
   public void setNodeId(Id nodeId) {
      this.nodeId = nodeId;
   }

   /**
    * @return Returns the criteria.
    */
   public List getCriteria() {
      return criteria;
   }

   /**
    * @param criteria The criteria to set.
    */
   public void setCriteria(List criteria) {
      this.criteria = criteria;
   }

   /**
    * @return Returns the cellId.
    */
   public Id getCellId() {
      return cellId;
   }

   /**
    * @param cellId The cellId to set.
    */
   public void setCellId(Id cellId) {
      this.cellId = cellId;
   }

   /**
    * @return Returns the selectedCriteria.
    */
   public String[] getSelectedCriteria() {
      return selectedCriteria;
   }

   /**
    * @param selectedCriteria The selectedCriteria to set.
    */
   public void setSelectedCriteria(String[] selectedCriteria) {
      this.selectedCriteria = selectedCriteria;
   }

   /**
    * @param list
    */
   public void setSelectedCriteria(List list) {
      if (list == null) return;
      int size = list.size();
      if (size == 0) return;
      String[] result = new String[size];
      for (int i = 0; i < size; i++) {
         result[i] = ((Criterion) list.get(i)).getId().getValue();
      }
      setSelectedCriteria(result);
   }

}

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
 * $Header: /opt/CVS/osp2.x/matrix/tool/src/java/org/theospi/portfolio/matrix/control/ScaffoldingCellBean.java,v 1.1 2005/07/15 21:10:34 rpembry Exp $
 * $Revision$
 * $Date$
 */


package org.theospi.portfolio.matrix.control;

import java.util.List;

/**
 * @author chmaurer
 */
public class ScaffoldingCellBean {
    private int levelIndex;
    private int rootCriterionIndex;
    private String status;
    private List rubrics;
    private List expectations;
    private boolean gradableReflection;
    
    

    /**
     * @return Returns the levelIndex.
     */
    public int getLevelIndex() {
        return levelIndex;
    }
    /**
     * @param levelIndex The levelIndex to set.
     */
    public void setLevelIndex(int levelIndex) {
        this.levelIndex = levelIndex;
    }
    /**
     * @return Returns the rootCriterionIndex.
     */
    public int getRootCriterionIndex() {
        return rootCriterionIndex;
    }
    /**
     * @param rootCriterionIndex The rootCriterionIndex to set.
     */
    public void setRootCriterionIndex(int rootCriterionIndex) {
        this.rootCriterionIndex = rootCriterionIndex;
    }
    /**
     * @return Returns the rubrics.
     */
    public List getRubrics() {
        return rubrics;
    }
    /**
     * @param rubrics The rubrics to set.
     */
    public void setRubrics(List rubrics) {
        this.rubrics = rubrics;
    }
    /**
     * @return Returns the status.
     */
    public String getStatus() {
        return status;
    }
    /**
     * @param status The status to set.
     */
    public void setStatus(String status) {
        this.status = status;
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
}

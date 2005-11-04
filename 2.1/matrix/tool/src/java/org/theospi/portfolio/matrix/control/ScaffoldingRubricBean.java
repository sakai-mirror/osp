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
 * $Header: /opt/CVS/osp2.x/matrix/tool/src/java/org/theospi/portfolio/matrix/control/ScaffoldingRubricBean.java,v 1.1 2005/07/15 21:10:34 rpembry Exp $
 * $Revision$
 * $Date$
 */


package org.theospi.portfolio.matrix.control;


/**
 * @author chmaurer
 */
public class ScaffoldingRubricBean {
    
    private int criterionIndex;
    private String type;
    private String primaryMimeType;
    private String subMimeType;
    private int quantity;
    
    

    /**
     * @return Returns the criterionIndex.
     */
    public int getCriterionIndex() {
        return criterionIndex;
    }
    /**
     * @param criterionIndex The criterionIndex to set.
     */
    public void setCriterionIndex(int criterionIndex) {
        this.criterionIndex = criterionIndex;
    }

    /**
     * @return Returns the primaryMimeType.
     */
    public String getPrimaryMimeType() {
        return primaryMimeType;
    }
    /**
     * @param primaryMimeType The primaryMimeType to set.
     */
    public void setPrimaryMimeType(String primaryMimeType) {
        this.primaryMimeType = primaryMimeType;
    }
    /**
     * @return Returns the subMimeType.
     */
    public String getSubMimeType() {
        return subMimeType;
    }
    /**
     * @param subMimeType The subMimeType to set.
     */
    public void setSubMimeType(String subMimeType) {
        this.subMimeType = subMimeType;
    }
    /**
     * @return Returns the type.
     */
    public String getType() {
        return type;
    }
    /**
     * @param type The type to set.
     */
    public void setType(String type) {
        this.type = type;
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
}

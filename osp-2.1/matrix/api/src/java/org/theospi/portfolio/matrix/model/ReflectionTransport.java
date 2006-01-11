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
 * $Header: /opt/CVS/osp2.x/matrix/api/src/java/org/theospi/portfolio/matrix/model/ReflectionTransport.java,v 1.1 2005/07/14 20:41:24 rpembry Exp $
 * $Revision$
 * $Date$
 */
package org.theospi.portfolio.matrix.model;

import org.sakaiproject.metaobj.shared.model.Id;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * User: apple
 * Date: Jun 9, 2004
 * Time: 4:44:54 PM
 * @deprecated ReflectionTransport is now deprecated.
 */
public class ReflectionTransport {
   private Id id;
   private List reflectionItems = new ArrayList();
   private String growthStatement = "";
   private Cell cell;
   private String[] selectedExpectations = {""};
   private int currentVirtualPage = 0;
   private List activePages;
   private int effectivePage = 0;
   private boolean usingWizard;
   private List cellArtifacts = new ArrayList();
   private String direction;
   private int totalPages = 0;
   private int currentPage = 0;
   
   public ReflectionTransport(Reflection reflection) {
      this.id = reflection.getId();
      this.growthStatement = reflection.getGrowthStatement();
      reflectionItems = new ArrayList();
      for (Iterator i = reflection.getReflectionItems().iterator(); i.hasNext();) {
         ReflectionItemTransport rt = new ReflectionItemTransport((ReflectionItem)i.next());
         reflectionItems.add(rt);
      }
      this.cell = reflection.getCell();
   }
   
   public ReflectionTransport copy(Reflection reflection) {
      this.id = reflection.getId();
      this.growthStatement = reflection.getGrowthStatement();
      this.reflectionItems = new ArrayList();
      for (Iterator i = reflection.getReflectionItems().iterator(); i.hasNext();) {
         ReflectionItem ri = (ReflectionItem)i.next();
         ReflectionItemTransport item = new ReflectionItemTransport(ri);
         item.reflection = this;
         this.reflectionItems.add(item);
      }
      this.cell = reflection.getCell();
      cell.getStatus();
      return this;
   }

   public Id getId() {
      return id;
   }

   public void setId(Id id) {
      this.id = id;
   }

   public String getGrowthStatement() {
      return growthStatement;
   }

   public void setGrowthStatement(String growthStatement) {
      this.growthStatement = growthStatement;
   }

   public Cell getCell() {
      return cell;
   }

   public void setCell(Cell cell) {
      this.cell = cell;
   }

   public List getReflectionItems() {
      return reflectionItems;
   }

   public void setReflectionItems(List reflectionItems) {
      this.reflectionItems = reflectionItems;
   }
   
   

   /* (non-Javadoc)
    * @see java.lang.Object#equals(java.lang.Object)
    */
   public boolean equals(Object obj) {
      // TODO Auto-generated method stub
      if (this.id == null && ((ReflectionTransport)obj).getId() == null) return true;
      return this.id.getValue().equals(((ReflectionTransport)obj).getId().getValue());
      //return super.equals(obj);
   }
   
   public String[] getSelectedExpectations() {
      return selectedExpectations;
   }
   public void setSelectedExpectations(String[] selectedExpectations) {
      this.selectedExpectations = selectedExpectations;
   }
   public List getActivePages() {
      return activePages;
   }

   public void setActivePages(List activePages) {
      this.activePages = activePages;
   }
   public int getCurrentVirtualPage() {
      if (activePages != null && activePages.size() >= currentVirtualPage)
         return (Integer.parseInt((String)activePages.get(currentVirtualPage)));
      else
         return 0;
   }

   public void setCurrentVirtualPage(int currentVirtualPage) {
      this.currentVirtualPage = currentVirtualPage;
   }
   public int getEffectivePage() {
      return effectivePage;
   }

   public void setEffectivePage(int effectivePage) {
      this.effectivePage = effectivePage;
      if (effectivePage == 2) this.effectivePage += currentVirtualPage;
   }
   
   public int getVirtualPageCount() {
      return activePages.size();
   }
   
   public String getDirection() {
      return direction;
   }

   public void setDirection(String direction) {
      this.direction = direction;
   }

   public boolean isUsingWizard() {
      return usingWizard;
   }

   public void setUsingWizard(boolean usingWizard) {
      this.usingWizard = usingWizard;
   } 
   
   public List getCellArtifacts() {
       return cellArtifacts;
   }
   
   public void setCellArtifacts(List cellArtifacts) {
       this.cellArtifacts = cellArtifacts;
   }
   
   public int getCurrentPage() {
      return currentPage;
   }
   public void setCurrentPage(int currentPage) {
      this.currentPage = currentPage;
   }
   public int getTotalPages() {
      return totalPages;
   }

   public void setTotalPages(int totalPages) {
      this.totalPages = totalPages;
   }
   
   //TODO next,previous,bof, and eof need to take filter into account

   public void previous() {
      if (!bof()) {
         this.setCurrentVirtualPage(this.currentVirtualPage - 1);
      }
   }

   public void next() {
      if (!eof()) {
         this.setCurrentVirtualPage(this.currentVirtualPage + 1);
      }
   }

   public boolean bof() {
      return (currentVirtualPage == 0);
   }


   public boolean eof() {
      return (currentVirtualPage >= this.getVirtualPageCount() - 1);
   }
}

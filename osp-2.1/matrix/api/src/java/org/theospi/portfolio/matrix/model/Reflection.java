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
 * $Header: /opt/CVS/osp2.x/matrix/api/src/java/org/theospi/portfolio/matrix/model/Reflection.java,v 1.1 2005/07/14 20:41:24 rpembry Exp $
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
 */
public class Reflection {
   private Id id;
   private List reflectionItems = new ArrayList();
   private String growthStatement = "";
   private Cell cell;
   
   public Reflection() {}
   
   public Reflection (ReflectionTransport rt) {
      this.id = rt.getId();
      this.growthStatement = rt.getGrowthStatement();
      reflectionItems = new ArrayList();
      for (Iterator i = rt.getReflectionItems().iterator(); i.hasNext();) {
         ReflectionItemTransport rit = (ReflectionItemTransport)i.next();
         ReflectionItem item = new ReflectionItem(rit);
         item.reflection = this;
         this.reflectionItems.add(item);
      }
      this.cell = rt.getCell();
      cell.getStatus();
   }
   
   public Reflection copy(ReflectionTransport rt) {
      this.id = rt.getId();
      this.growthStatement = rt.getGrowthStatement();

      for (Iterator i = rt.getReflectionItems().iterator(); i.hasNext();) {
         ReflectionItemTransport rit = (ReflectionItemTransport)i.next();
         ReflectionItem item = new ReflectionItem();
         if (rit.getId()==null) {
            item = new ReflectionItem(rit);
            reflectionItems.add(item);
         }
         else {
            item = getItemById(rit.getId());
            item.copy(rit);
         }
         item.reflection = this;
      }
      this.cell = rt.getCell();
      cell.getStatus();
      return this;
   }
   
   private ReflectionItem getItemById(Id itemId) {
      for (Iterator i = getReflectionItems().iterator(); i.hasNext();) {
         ReflectionItem item = (ReflectionItem)i.next();
         if (item.getId().getValue().equals(itemId.getValue()))
            return item;
      }
      return null;
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
      if (this.id == null && ((Reflection)obj).getId() == null) return true;
      return this.id.getValue().equals(((Reflection)obj).getId().getValue());
      //return super.equals(obj);
   }
   

}

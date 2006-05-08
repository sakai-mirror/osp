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
 * $Header: /opt/CVS/osp/src/portfolio/org/theospi/portfolio/list/model/ListConfig.java,v 1.3 2004/11/02 22:08:31 jellis Exp $
 * $Revision: 5901 $
 * $Date$
 */
package org.theospi.portfolio.list.model;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.metaobj.shared.model.Id;
import org.sakaiproject.metaobj.shared.model.Agent;

import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ListConfig {
   protected final transient Log logger = LogFactory.getLog(getClass());

   private Id id;
   private Id toolId;
   private Agent owner;
   private String title;
   private List selectedColumns = new ArrayList();
   private int height;
   private int rows;

   // not persisted, used on UI
   private List columns;

   public int getHeight() {
      return height;
   }

   public void setHeight(int height) {
      this.height = height;
   }

   public int getRows() {
      return rows;
   }

   public void setRows(int rows) {
      this.rows = rows;
   }

   public List getColumns() {
      return columns;
   }

   public void setColumns(List columns) {
      this.columns = columns;
   }

   public String getTitle() {
      return title;
   }

   public void setTitle(String title) {
      this.title = title;
   }

   public Id getToolId() {
      return toolId;
   }

   public void setToolId(Id toolId) {
      this.toolId = toolId;
   }

   public Agent getOwner() {
      return owner;
   }

   public void setOwner(Agent owner) {
      this.owner = owner;
   }

   public List getSelectedColumns() {
      return selectedColumns;
   }

   public void setSelectedColumns(List selectedColumns) {
      this.selectedColumns = selectedColumns;
   }

   public Map getSelected() {
      return new ColumnMap(getSelectedColumns());
   }

   public Id getId() {
      return id;
   }

   public void setId(Id id) {
      this.id = id;
   }

   private class ColumnMap extends HashMap {

      private List selectedColumns;
      private final Column FAKE_COLUMN = new Column("empty", false);

      /**
       * Constructs an empty <tt>HashMap</tt> with the default initial capacity
       * (16) and the default load factor (0.75).
       */
      public ColumnMap(List selectedColumns) {
         this.selectedColumns = selectedColumns;
      }

      public Object get(Object indexString) {
         int index = Integer.parseInt(indexString.toString());

         if (selectedColumns.size() > index) {
            return new Column((String)selectedColumns.get(index),
               true);
         }
         else {
            return FAKE_COLUMN;
         }
      }

   }

}

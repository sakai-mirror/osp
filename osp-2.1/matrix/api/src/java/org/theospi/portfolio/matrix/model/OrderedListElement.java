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
 * $Header: /opt/CVS/osp2.x/matrix/api/src/java/org/theospi/portfolio/matrix/model/OrderedListElement.java,v 1.1 2005/07/14 20:41:24 rpembry Exp $
 * $Revision$
 * $Date$
 */
/*
 * Created on Apr 24, 2004
 */
package org.theospi.portfolio.matrix.model;

/**
 * @author rpembry
 */
public class OrderedListElement {

   protected final org.apache.commons.logging.Log logger =
      org.apache.commons.logging.LogFactory.getLog(getClass());

   private String description = "";
   private int level;
   private int maxLevel;
   private OrderedList container;
   private Object data;
   private String displayString;
   private boolean hasChild = false;


   private OrderedListElement() {
      ;
   }

   public OrderedListElement(OrderedList container) {
      this.container = container;
      this.maxLevel = container.getMaxIndent();
   }

   /**
    * @return Returns the description.
    */
   public String getDescription() {
      return description;
   }

   /**
    * @param description The description to set.
    */
   public void setDescription(String description) {
      this.description = description;
   }

   /**
    * @return Returns the level.
    */
   public int getLevel() {
      return level;
   }

   /**
    * @param newLevel
    */
   //This is private on purpose; use indent or outdent to manage level
   private void setLevel(int newLevel) {
      setLevel(newLevel, true);
   }

   public OrderedList getContainer() {
      return container;
   }

   /**
    * @param level The level to set.
    */
   // This is package protected on purpose; use indent or outdent to manage level
   void setLevel(int level, boolean coordinate) {
      if (logger.isDebugEnabled()) {
         logger.debug("setLevel: " + level);
         logger.debug("maxLevel: " + maxLevel);
      }
      if (coordinate) { // coordinate with container regarding promotions, etc.
         if (level < 0) return;
         if (level > this.maxLevel) return;
         OrderedListElement parent = container.getPredecessor(this);
         if (parent == null) return; //can't promote root element
         if (logger.isDebugEnabled()) logger.debug("Parent level: " + parent.getLevel());
         if (level - parent.getLevel() > 1) return; //can't indent more than one level deeper than parent

      }
      if (logger.isDebugEnabled())
         logger.debug("changing level");
      this.level = level;
   }

   public Object getLabel() {
      return container.getLabel(this);
   }

   public void indent() {
      this.setLevel(level + 1);
   }


   public void outdent() {
      this.setLevel(level - 1);
   }

   public String getDisplayString() {
      return this.container.render(this);
   }

   public void setDisplayString() {
      displayString = this.container.render(this);
   }

   /**
    * @return
    */
   public Object getData() {
      return data;
   }

   /**
    * @param object
    */
   public void setData(Object data) {
      this.data = data;
   }
    /**
     * @return Returns the hasChild.
     */
    public boolean isHasChild() {
        return hasChild;
    }
    /**
     * @param hasChild The hasChild to set.
     */
    public void setHasChild(boolean hasChild) {
        this.hasChild = hasChild;
    }

    public void setHasChild() {
       int level = getLevel();
       if (level == container.maxIndent) {
           hasChild=false;
       }
       else
       {
           OrderedListElement result = container.getSuccessor(this);
           if (result == null)
               hasChild = false;
           else if (result.getLevel() > level)
               hasChild = true;
           else
               hasChild = false;
       }
   }
}

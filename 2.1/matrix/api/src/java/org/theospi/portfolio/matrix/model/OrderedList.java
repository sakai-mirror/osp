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
 * $Header: /opt/CVS/osp2.x/matrix/api/src/java/org/theospi/portfolio/matrix/model/OrderedList.java,v 1.1 2005/07/14 20:41:24 rpembry Exp $
 * $Revision$
 * $Date$
 */
/*
 * Created on Apr 24, 2004
 */
package org.theospi.portfolio.matrix.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;


/**
 * @author rpembry
 *         <p/>
 *         May use a list of Classes instead of Objects for more efficient implementation
 */
public class OrderedList extends AbstractOrderedList {
   List data = new ArrayList();
   List levels = null;
   int maxIndent;
   int curIndent = 0;
   OrderedListElementRenderer renderer;
   boolean fullyContainedLabels;
   int offset = 0;

   public OrderedList() {
      ;
   }

   public OrderedList(List levelIteratorsList) {
      setLevels(levelIteratorsList);
   }

   // This provides a setter as well as a constructor to satisfy Spring IoC
   // For future optimizations, don't change this once it is set
   public void setLevels(List levels) {
      if (this.levels != null) throw new ScaffoldModelException("Levels may only be set once");
      this.levels = levels;
      this.maxIndent = levels.size() - 1;
   }

   public int getMaxIndent() {
      return this.maxIndent;
   }

   public OrderedListElement append() {
      OrderedListElement newElement = new OrderedListElement(this);
      if (data.size() > 1)
         curIndent = ((OrderedListElement) data.get(data.size() - 1)).getLevel();
      data.add(newElement);
      newElement.setLevel(curIndent, false);
      return newElement;
   }

   public OrderedListElement insertRow(int row) {
      OrderedListElement newElement = new OrderedListElement(this);
      if (data.size() > 1)
         curIndent = ((OrderedListElement) data.get(row)).getLevel();
      data.add(row, newElement);
      newElement.setLevel(curIndent, false);
      return newElement;
   }

   /* (non-Javadoc)
    * @see java.util.List#size()
    */
   public int size() {
      return data.size();
   }

   /* (non-Javadoc)
    * @see java.util.List#get(int)
    */
   public Object get(int index) {
      return data.get(index);
   }

   /* (non-Javadoc)
    * @see java.util.List#add(int, java.lang.Object)
    */
   public void add(int index, Object element) {
      // TODO Auto-generated method stub

   }

   /* (non-Javadoc)
    * @see java.util.List#indexOf(java.lang.Object)
    */
   public int indexOf(Object o) {
      return data.indexOf(o);
   }

   /* (non-Javadoc)
    * @see java.util.List#add(java.lang.Object)
    */
   public boolean add(Object o) {
      // TODO Auto-generated method stub
      return false;
   }

   /* (non-Javadoc)
    * @see java.util.List#contains(java.lang.Object)
    */
   public boolean contains(Object o) {
      return data.contains(o);
   }

   /* (non-Javadoc)
    * @see java.util.List#remove(java.lang.Object)
    */
   public boolean remove(Object o) {
      // TODO Auto-generated method stub
      return false;
   }

   /* (non-Javadoc)
    * @see java.util.List#iterator()
    */
   public Iterator iterator() {
      return data.iterator(); //TODO should I do this?
   }

   /* (non-Javadoc)
    * @see java.util.List#set(int, java.lang.Object)
    */
   public Object set(int index, Object element) {
      // TODO Auto-generated method stub
      return null;
   }

   /**
    * @param element
    * @return
    */
   public Object getLabel(OrderedListElement element) {
      return lookupLabel(data.indexOf(element));
   }

   /**
    * @param i
    * @return
    */
   private Object lookupLabel(int i) {
//TODO very inefficient approach (O(n^2))
      //TODO consider caching this as a temporary improvement
      return generateLabels().get(i);
   }

   private List generateLabels() {

      ArrayList result = new ArrayList();
      Stack generators = new Stack();

      int oldLevel = -1;
      int curLevel;
      OrderedListElement curElement;
      Iterator iter = data.iterator();
      IdGenerator generator;

      while (iter.hasNext()) {
         curElement = (OrderedListElement) iter.next();
         curLevel = curElement.getLevel();
         if (curLevel > oldLevel) {
            generators.push(makeGenerator(curLevel));
         } else
            while (curLevel < oldLevel) {
               generators.pop();
               oldLevel--;
            }

         generator = (IdGenerator) generators.peek();
         //If the offset has been set, keep getting the next item
         for (int i = 0; i < offset; i++) {
            generator.next();
         }
         result.add(generator.next());
         oldLevel = curLevel;

      }

      return result;
   }

   private IdGenerator makeGenerator(int level) {
      IdGenerator result = (IdGenerator) levels.get(level);
      result.reset();
      return result;
   }

   /**
    * @param element
    * @return
    */
   public OrderedListElement getPredecessor(OrderedListElement element) {
      int index = data.indexOf(element);
      if (index > 0) return (OrderedListElement) data.get(index - 1);
      return null;
   }

   /**
    * @param element
    * @return
    */
   public String render(OrderedListElement element) {
      return renderer.render(element);
   }

   /**
    * @return Returns the renderer.
    */
   public OrderedListElementRenderer getRenderer() {
      return renderer;
   }

   /**
    * @param renderer The renderer to set.
    */
   public void setRenderer(OrderedListElementRenderer renderer) {
      this.renderer = renderer;
   }

   /**
    * Traverses through predecessors, looking for the first one whose
    * level is lower than the passed element's level.
    *
    * @param element
    * @return parent of element
    */
   public OrderedListElement getParent(OrderedListElement element) {
      if (element == null) return null;
      int level = element.getLevel();
      if (level == 0) return null;

      OrderedListElement result = element;
      do {
         result = getPredecessor(result);
         if (result == null) return null;
         if (result.getLevel() < level) return result;
      } while (true);
   }

   public void clear() {
      data.clear();
      this.curIndent = 0;
   }
  
   public OrderedListElement getSuccessor(OrderedListElement element) {
      int index = data.indexOf(element);
      if (index < data.size()-1) return (OrderedListElement) data.get(index + 1);
      return null;
   }

   /**
    * @return
    */
   public boolean isFullyContainedLabels() {
      return fullyContainedLabels;
   }

   /**
    * @param b
    */
   public void setFullyContainedLabels(boolean b) {
      fullyContainedLabels = b;
   }

   /**
    * @return Returns the offset.
    */
   public int getOffset() {
      return offset;
   }

   /**
    * @param offset The offset to set.
    */
   public void setOffset(int offset) {
      this.offset = offset;
   }

}
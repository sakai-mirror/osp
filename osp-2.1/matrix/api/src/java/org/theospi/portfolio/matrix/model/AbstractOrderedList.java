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
 * $Header: /opt/CVS/osp2.x/matrix/api/src/java/org/theospi/portfolio/matrix/model/AbstractOrderedList.java,v 1.1 2005/07/14 20:41:24 rpembry Exp $
 * $Revision$
 * $Date$
 */
/*
 * Created on Apr 24, 2004
 */
package org.theospi.portfolio.matrix.model;

import java.util.Collection;
import java.util.List;
import java.util.ListIterator;

/**
 * @author rpembry
 */
public abstract class AbstractOrderedList implements List {


   /* (non-Javadoc)
    * @see java.util.Collection#clear()
    */
   public void clear() {
      throw new ScaffoldModelException("UNIMPLEMENTED");
   }

   /* (non-Javadoc)
    * @see java.util.Collection#isEmpty()
    */
   public boolean isEmpty() {
      return size() == 0;
   }

   /* (non-Javadoc)
    * @see java.util.Collection#toArray()
    */
   public Object[] toArray() {
      throw new ScaffoldModelException("UNIMPLEMENTED");
   }

   /* (non-Javadoc)
   * @see java.util.List#remove(int)
   */
   public Object remove(int index) {
      throw new ScaffoldModelException("UNIMPLEMENTED");
   }

   /* (non-Javadoc)
    * @see java.util.List#lastIndexOf(java.lang.Object)
    */
   public int lastIndexOf(Object o) {
      throw new ScaffoldModelException("UNIMPLEMENTED");
   }

   /* (non-Javadoc)
   * @see java.util.List#addAll(int, java.util.Collection)
   */
   public boolean addAll(int index, Collection c) {
      throw new ScaffoldModelException("UNIMPLEMENTED");
   }

   /* (non-Javadoc)
    * @see java.util.Collection#addAll(java.util.Collection)
    */
   public boolean addAll(Collection c) {
      throw new ScaffoldModelException("UNIMPLEMENTED");
   }

   /* (non-Javadoc)
    * @see java.util.Collection#containsAll(java.util.Collection)
    */
   public boolean containsAll(Collection c) {
      throw new ScaffoldModelException("UNIMPLEMENTED");
   }

   /* (non-Javadoc)
    * @see java.util.Collection#removeAll(java.util.Collection)
    */
   public boolean removeAll(Collection c) {
      throw new ScaffoldModelException("UNIMPLEMENTED");
   }

   /* (non-Javadoc)
    * @see java.util.Collection#retainAll(java.util.Collection)
    */
   public boolean retainAll(Collection c) {
      throw new ScaffoldModelException("UNIMPLEMENTED");
   }

   /* (non-Javadoc)
    * @see java.util.List#subList(int, int)
    */
   public List subList(int fromIndex, int toIndex) {
      throw new ScaffoldModelException("UNIMPLEMENTED");
   }

   /* (non-Javadoc)
    * @see java.util.List#listIterator()
    */
   public ListIterator listIterator() {
      throw new ScaffoldModelException("UNIMPLEMENTED");
   }

   /* (non-Javadoc)
    * @see java.util.List#listIterator(int)
    */
   public ListIterator listIterator(int index) {
      throw new ScaffoldModelException("UNIMPLEMENTED");
   }

   /* (non-Javadoc)
    * @see java.util.Collection#toArray(java.lang.Object[])
    */
   public Object[] toArray(Object[] a) {
      throw new ScaffoldModelException("UNIMPLEMENTED");
   }
}

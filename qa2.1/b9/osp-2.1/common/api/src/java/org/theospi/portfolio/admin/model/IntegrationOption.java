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
 * $Header: /root/osp/src/portfolio/org/theospi/portfolio/admin/model/IntegrationOption.java,v 1.2 2004/12/02 23:59:11 jellis Exp $
 * $Revision: 1.2 $
 * $Date: 2004/12/02 23:59:11 $
 */
package org.theospi.portfolio.admin.model;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class IntegrationOption implements Cloneable {
   protected final transient Log logger = LogFactory.getLog(getClass());

   private String label;
   private boolean include = true;

   public IntegrationOption() {
   }

   public IntegrationOption(boolean include, String label) {
      this.include = include;
      this.label = label;
   }

   public IntegrationOption(IntegrationOption copy) {
      this.include = copy.include;
      this.label = copy.label;
   }

   public boolean isInclude() {
      return include;
   }

   public void setInclude(boolean include) {
      this.include = include;
   }

   public String getLabel() {
      return label;
   }

   public void setLabel(String label) {
      this.label = label;
   }

   /**
    * Creates and returns a copy of this object.  The precise meaning
    * of "copy" may depend on the class of the object. The general
    * intent is that, for any object <tt>x</tt>, the expression:
    * <blockquote>
    * <pre>
    * x.clone() != x</pre></blockquote>
    * will be true, and that the expression:
    * <blockquote>
    * <pre>
    * x.clone().getClass() == x.getClass()</pre></blockquote>
    * will be <tt>true</tt>, but these are not absolute requirements.
    * While it is typically the case that:
    * <blockquote>
    * <pre>
    * x.clone().equals(x)</pre></blockquote>
    * will be <tt>true</tt>, this is not an absolute requirement.
    * <p/>
    * By convention, the returned object should be obtained by calling
    * <tt>super.clone</tt>.  If a class and all of its superclasses (except
    * <tt>Object</tt>) obey this convention, it will be the case that
    * <tt>x.clone().getClass() == x.getClass()</tt>.
    * <p/>
    * By convention, the object returned by this method should be independent
    * of this object (which is being cloned).  To achieve this independence,
    * it may be necessary to modify one or more fields of the object returned
    * by <tt>super.clone</tt> before returning it.  Typically, this means
    * copying any mutable objects that comprise the internal "deep structure"
    * of the object being cloned and replacing the references to these
    * objects with references to the copies.  If a class contains only
    * primitive fields or references to immutable objects, then it is usually
    * the case that no fields in the object returned by <tt>super.clone</tt>
    * need to be modified.
    * <p/>
    * The method <tt>clone</tt> for class <tt>Object</tt> performs a
    * specific cloning operation. First, if the class of this object does
    * not implement the interface <tt>Cloneable</tt>, then a
    * <tt>CloneNotSupportedException</tt> is thrown. Note that all arrays
    * are considered to implement the interface <tt>Cloneable</tt>.
    * Otherwise, this method creates a new instance of the class of this
    * object and initializes all its fields with exactly the contents of
    * the corresponding fields of this object, as if by assignment; the
    * contents of the fields are not themselves cloned. Thus, this method
    * performs a "shallow copy" of this object, not a "deep copy" operation.
    * <p/>
    * The class <tt>Object</tt> does not itself implement the interface
    * <tt>Cloneable</tt>, so calling the <tt>clone</tt> method on an object
    * whose class is <tt>Object</tt> will result in throwing an
    * exception at run time.
    *
    * @return a clone of this instance.
    * @throws CloneNotSupportedException if the object's class does not
    *                                    support the <code>Cloneable</code> interface. Subclasses
    *                                    that override the <code>clone</code> method can also
    *                                    throw this exception to indicate that an instance cannot
    *                                    be cloned.
    * @see Cloneable
    */
   public Object clone() throws CloneNotSupportedException {
      return super.clone();
   }
}

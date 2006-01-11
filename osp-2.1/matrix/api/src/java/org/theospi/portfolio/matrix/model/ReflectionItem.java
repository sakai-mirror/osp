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
 * $Header: /opt/CVS/osp2.x/matrix/api/src/java/org/theospi/portfolio/matrix/model/ReflectionItem.java,v 1.1 2005/07/14 20:41:24 rpembry Exp $
 * $Revision$
 * $Date$
 */
/*
 * Created on Jun 1, 2004
 */
package org.theospi.portfolio.matrix.model;

import org.sakaiproject.metaobj.shared.model.Id;

/**
 * @author chmaurer
 * @deprecated ReflectionItem is now deprecated.
 */
public class ReflectionItem {

   Id id;
   //Integer expectation;
   Expectation expectation;
   String evidence = "";
   String connect = "";
   Reflection reflection;
   
   public ReflectionItem () {}
   
   public ReflectionItem (ReflectionItemTransport rit) {
      this.id = rit.getId();
      this.connect = rit.getConnect();
      this.evidence = rit.getEvidence();
      this.expectation = rit.getExpectation();      
   }
   
   public ReflectionItem copy(ReflectionItemTransport rit) {
      this.id = rit.getId();
      this.connect = rit.getConnect();
      this.evidence = rit.getEvidence();
      this.expectation = rit.getExpectation();
      return this;
   }

   public Reflection getReflection() {
      return reflection;
   }

   public void setReflection(Reflection reflection) {
      this.reflection = reflection;
   }

   public Expectation getExpectation() {
      return expectation;
   }

   public void setExpectation(Expectation expectation) {
      this.expectation = expectation;
   }

   public String getConnect() {
      return connect;
   }

   public void setConnect(String connect) {
      this.connect = connect;
   }

   public String getEvidence() {
      return evidence;
   }

   public void setEvidence(String evidence) {
      this.evidence = evidence;
   }

   public Id getId() {
      return id;
   }

   public void setId(Id id) {
      this.id = id;
   }

   String naturalKey() {
      return "" + expectation + connect + evidence;
   }

   public boolean equals(Object o) {
      if (this == o) return true;
      if (!(o instanceof ReflectionItem)) return false;

      final ReflectionItem reflection = (ReflectionItem) o;
      return reflection.naturalKey().equals(this.naturalKey());

   }

   public int hashCode() {
      return naturalKey().hashCode();
   }
}

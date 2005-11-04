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
 * $Header: /opt/CVS/osp2.x/glossary/api/src/java/org/theospi/portfolio/help/model/GlossaryEntry.java,v 1.1 2005/07/08 01:18:46 jellis Exp $
 * $Revision$
 * $Date$
 */
package org.theospi.portfolio.help.model;



public class GlossaryEntry extends GlossaryBase {
   private String term;
   private String description;
   private String worksiteId;

   private GlossaryDescription longDescriptionObject = new GlossaryDescription();

   public GlossaryEntry(){}

   public GlossaryEntry(String term, String description){
      this.term = term;
      this.description = description;
   }

   public String getTerm() {
      return term;
   }

   public void setTerm(String term) {
      this.term = term.trim();
   }

   public String getDescription() {
      return description;
   }

   public void setDescription(String description) {
      this.description = description;
   }

   public String getWorksiteId() {
      return worksiteId;
   }

   public void setWorksiteId(String worksiteId) {
      this.worksiteId = worksiteId;
   }

   public String getLongDescription() {
      return longDescriptionObject.getLongDescription();
   }

   public void setLongDescriptionObject(GlossaryDescription longDescriptionObject) {
      this.longDescriptionObject = longDescriptionObject;
   }

   public GlossaryDescription getLongDescriptionObject() {
      return longDescriptionObject;
   }

   public void setLongDescription(String longDescription) {
      this.longDescriptionObject.setLongDescription(longDescription);
   }

   /**
    * Returns a string representation of the object. In general, the
    * <code>toString</code> method returns a string that
    * "textually represents" this object. The result should
    * be a concise but informative representation that is easy for a
    * person to read.
    * It is recommended that all subclasses override this method.
    * <p/>
    * The <code>toString</code> method for class <code>Object</code>
    * returns a string consisting of the name of the class of which the
    * object is an instance, the at-sign character `<code>@</code>', and
    * the unsigned hexadecimal representation of the hash code of the
    * object. In other words, this method returns a string equal to the
    * value of:
    * <blockquote>
    * <pre>
    * getClass().getName() + '@' + Integer.toHexString(hashCode())
    * </pre></blockquote>
    *
    * @return a string representation of the object.
    */
   public String toString() {
      return getTerm();
   }
   
}

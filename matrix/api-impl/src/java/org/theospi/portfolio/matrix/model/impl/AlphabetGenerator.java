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
 * $Header: /opt/CVS/osp2.x/matrix/api-impl/src/java/org/theospi/portfolio/matrix/model/impl/AlphabetGenerator.java,v 1.1 2005/07/15 17:42:04 rpembry Exp $
 * $Revision$
 * $Date$
 */
/*
 * Created on Apr 24, 2004
 */
package org.theospi.portfolio.matrix.model.impl;

import org.theospi.portfolio.matrix.model.IdGenerator;
import org.theospi.portfolio.matrix.model.ScaffoldModelException;

/**
 * @author rpembry
 *         A,B,C,...,Z,AA,BB,CC,..ZZ,...
 */
public class AlphabetGenerator implements IdGenerator {
   int i = 0;

   // TODO these three should be controlled by Spring
   boolean useUpperCase = true;
   String alphabet;
   int len;

   /* (non-Javadoc)
    * @see edu.iu.uits.osp.scaffolding.model.ResetableIterator#reset()
    */
   public void reset() {
      i = 0;
   }

   /* (non-Javadoc)
    * @see java.util.Iterator#remove()
    */
   public void remove() {
      throw new ScaffoldModelException("remove is unsupported");
   }

   /* (non-Javadoc)
    * @see java.util.Iterator#hasNext()
    */
   public boolean hasNext() {
      return true;
   }

   /* (non-Javadoc)
    * @see java.util.Iterator#next()
    */
   public Object next() {
      String result = makealphabetequence(i++);
      if (this.isUseUpperCase()) return result; else return result.toLowerCase();
   }

   private String makealphabetequence(int n) {
      char letter = alphabet.charAt(n % len);
      StringBuffer result = new StringBuffer();
      result.append(letter);
      int digits = n / len;
      for (int i = 0; i < digits; i++) {
         result.append(letter);
      }
      return result.toString();
   }


   /**
    * @return Returns the alphabet.
    */
   public String getalphabet() {
      return alphabet;
   }

   /**
    * @param alphabet The alphabet to set.
    */
   public void setalphabet(String alphabet) {
      this.alphabet = alphabet;
      this.len = alphabet.length();
   }

   /**
    * @return Returns the useUpperCase.
    */
   public boolean isUseUpperCase() {
      return useUpperCase;
   }

   /**
    * @param useUpperCase The useUpperCase to set.
    */
   public void setUseUpperCase(boolean useUpperCase) {
      this.useUpperCase = useUpperCase;
   }
}
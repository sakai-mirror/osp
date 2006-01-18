/**********************************************************************************
* $URL$
* $Id$
***********************************************************************************
*
* Copyright (c) 2003, 2004 The Regents of the University of Michigan, Trustees of Indiana University,
*                  Board of Trustees of the Leland Stanford, Jr., University, and The MIT Corporation
*
* Licensed under the Educational Community License Version 1.0 (the "License");
* By obtaining, using and/or copying this Original Work, you agree that you have read,
* understand, and will comply with the terms and conditions of the Educational Community License.
* You may obtain a copy of the License at:
*
*      http://cvs.sakaiproject.org/licenses/license_1_0.html
*
* THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
* INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE
* AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
* DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
* FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*
**********************************************************************************/
package org.theospi.portfolio.wizard.tool;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Jan 17, 2006
 * Time: 4:18:11 PM
 * To change this template use File | Settings | File Templates.
 */
public abstract class DecoratedCategoryChild {

   private static final String INDENT_CHAR = "&nbsp;&nbsp;&nbsp;";

   private String indentString;
   private int indent;

   public DecoratedCategoryChild(int indent) {
      this.indent = indent;
      this.indentString = "";
      for (int i=0;i<indent - 1;i++) {
         this.indentString += INDENT_CHAR;
      }
   }

   public String getIndentString() {
      return indentString;
   }

   public void setIndentString(String indentString) {
      this.indentString = indentString;
   }

   public int getIndent() {
      return indent;
   }

   public void setIndent(int indent) {
      this.indent = indent;
   }

   public abstract String getTitle();

   public abstract boolean isSelected();
   public abstract void setSelected(boolean selected);

   public abstract String processActionEdit();
   public abstract String processActionDelete();

   public abstract String moveUp();
   public abstract String moveDown();

   public abstract boolean isFirst();
   public abstract boolean isLast();
}

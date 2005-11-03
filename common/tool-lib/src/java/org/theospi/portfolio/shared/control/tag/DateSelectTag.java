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
 * $Header: /opt/CVS/osp2.x/common/tool-lib/src/java/org/theospi/portfolio/shared/control/tag/DateSelectTag.java,v 1.1 2005/08/17 19:16:44 chmaurer Exp $
 * $Revision$
 * $Date$
 */
package org.theospi.portfolio.shared.control.tag;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.taglibs.standard.lang.support.ExpressionEvaluatorManager;
import org.sakaiproject.metaobj.shared.mgt.PortalParamManager;
import org.sakaiproject.service.framework.component.cover.ComponentManager;

import javax.servlet.jsp.JspException;
import java.io.IOException;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Calendar;

public class DateSelectTag extends DateSelectPopupTag {
   protected final transient Log logger = LogFactory.getLog(getClass());

   private String earliestYear = String.valueOf(Calendar.getInstance().get(Calendar.YEAR) - 5);
   private String latestYear = String.valueOf(Calendar.getInstance().get(Calendar.YEAR) + 5);
   private Date selected;
   private String selectedDate;

   public int doStartTag() throws JspException {
      //EL support for setting selectedDate attribute
      if (selectedDate != null && selectedDate.length() > 0){
         selected = (Date) ExpressionEvaluatorManager.evaluate(
                 "selectedDate",                // attribute name
                 selectedDate,              // expression
                 java.util.Date.class,  // expected type
                 this,                 // this tag handler
                 pageContext);         // the page context
      }

      try {
         StringBuffer buffer = new StringBuffer();
         buffer.append("<select name=\"" + getMonthSelectId() + "\" id=\"" + getMonthSelectId() + "\" onchange=\"blur();\">\n");
         buffer.append("<option value=\"\">\n");
         for (int i=1; i<13; i++){
            buffer.append("<option value=\"" + i + "\"");
            if (getMonthSelected() == i){
               buffer.append("selected=\"selected\"");
            }
            buffer.append(">" + getMonthName(i) + "</option>\n");
         }
         buffer.append("</select>");

         buffer.append("<select name=\"" + getDaySelectId() + "\" id=\"" + getDaySelectId() + "\" onchange=\"blur();\">\n");
         buffer.append("<option value=\"\">\n");
         for (int i=1; i<31; i++){
            buffer.append("<option value=\"" + i + "\"");
            if (getDaySelected() == i){
               buffer.append("selected=\"selected\"");
            }
            buffer.append(">" + i + "</option>\n");
         }
         buffer.append("</select>");

         buffer.append("<select name=\"" + getYearSelectId() + "\" id=\"" + getYearSelectId() + "\" onchange=\"blur();\">\n");
         buffer.append("<option value=\"\">\n");
         for (int i=Integer.parseInt(earliestYear); i<Integer.parseInt(latestYear)+1; i++){
            buffer.append("<option value=\"" + i + "\"");
            if (getYearSelected() == i){
               buffer.append("selected=\"selected\"");
            }
            buffer.append(">" + i + "</option>\n");
         }
         buffer.append("</select>");

         pageContext.getOut().write(buffer.toString());
         super.doStartTag();
      } catch (IOException e) {
         logger.error("", e);
         throw new JspException(e);
      }

      return EVAL_BODY_INCLUDE;
   }

   protected String getMonthName(int month) throws JspException {
      switch (month) {
         case 1 : return "JAN";
         case 2 : return "FEB";
         case 3 : return "MAR";
         case 4 : return "APR";
         case 5 : return "MAY";
         case 6 : return "JUN";
         case 7 : return "JUL";
         case 8 : return "AUG";
         case 9 : return "SEP";
         case 10 : return "OCT";
         case 11 : return "NOV";
         case 12 : return "DEC";
      }
      throw new JspException(month + " is not a valid month");
   }

   protected PortalParamManager getPortalParamManager() {
      return (PortalParamManager)
            ComponentManager.getInstance().get(PortalParamManager.class.getName());
   }

   protected Calendar getCalendar(){
      Calendar calendar = new GregorianCalendar();
      calendar.setTime(selected);
      return calendar;
   }

   protected int getMonthSelected(){
      if (selected == null) return -1;
      return getCalendar().get(Calendar.MONTH)+1; // Calendar indexes months starting at 0
   }

   protected int getYearSelected(){
      if (selected == null) return -1;
      return getCalendar().get(Calendar.YEAR);
   }

   protected int getDaySelected(){
      if (selected == null) return -1;
      return getCalendar().get(Calendar.DAY_OF_MONTH);
   }

   public String getEarliestYear() {
      return earliestYear;
   }

   public void setEarliestYear(String earliestYear) {
      this.earliestYear = earliestYear;
   }

   public String getLatestYear() {
      return latestYear;
   }

   public void setLatestYear(String lastestYear) {
      this.latestYear = lastestYear;
   }

   public String getSelectedDate() {
      return selectedDate;
   }

   public void setSelectedDate(String selectedDate) {
      this.selectedDate = selectedDate;
   }
}

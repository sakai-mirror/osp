/**********************************************************************************
* $URL$
* $Id$
***********************************************************************************
*
* Copyright (c) 2005, 2006 The Sakai Foundation.
*
* Licensed under the Educational Community License, Version 1.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*      http://www.opensource.org/licenses/ecl1.php
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*
**********************************************************************************/
package org.theospi.portfolio.shared.control.tag;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.taglibs.standard.lang.support.ExpressionEvaluatorManager;
import org.sakaiproject.metaobj.shared.mgt.PortalParamManager;
import org.sakaiproject.component.cover.ComponentManager;

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

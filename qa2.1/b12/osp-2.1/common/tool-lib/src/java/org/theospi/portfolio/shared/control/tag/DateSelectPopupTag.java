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
import org.sakaiproject.service.framework.component.cover.ComponentManager;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTagSupport;
import java.io.IOException;

public class DateSelectPopupTag extends BodyTagSupport {
   protected final transient Log logger = LogFactory.getLog(getClass());

   private String yearSelectId;
   private String daySelectId;
   private String monthSelectId;

   public int doStartTag() throws JspException {
      try {
         daySelectId = (String) ExpressionEvaluatorManager.evaluate("daySelectId", // attribute name
               daySelectId, // expression
               java.lang.String.class, // expected type
               this, // this tag handler
               pageContext);         // the page context
         monthSelectId = (String) ExpressionEvaluatorManager.evaluate("monthSelectId", // attribute name
               monthSelectId, // expression
               java.lang.String.class, // expected type
               this, // this tag handler
               pageContext);         // the page context
         yearSelectId = (String) ExpressionEvaluatorManager.evaluate("yearSelectId", // attribute name
               yearSelectId, // expression
               java.lang.String.class, // expected type
               this, // this tag handler
               pageContext);         // the page context

         pageContext.getOut().write("<script type=\"text/javascript\" src=\"/library/calendar/sakai-calendar.js\"></script>\n" +
               "<script type=\"text/javascript\" src=\"/osp-common-tool/js/eport.js\"></script>\n" +
               "<script type=\"text/javascript\">osp_dateselectionwidgetpopup('" +
               getYearSelectId() + "', '" + getMonthSelectId() + "', '" + getDaySelectId() + "');</script>\n");
      } catch (IOException e) {
         logger.error("", e);
         throw new JspException(e);
      }

      return EVAL_BODY_INCLUDE;
   }

   protected PortalParamManager getPortalParamManager() {
      return (PortalParamManager)
            ComponentManager.getInstance().get(PortalParamManager.class.getName());
   }

   public String getYearSelectId() {
      return yearSelectId;
   }

   public void setYearSelectId(String yearSelectId) {
      this.yearSelectId = yearSelectId;
   }

   public String getDaySelectId() {
      return daySelectId;
   }

   public void setDaySelectId(String daySelectId) {
      this.daySelectId = daySelectId;
   }

   public String getMonthSelectId() {
      return monthSelectId;
   }

   public void setMonthSelectId(String monthSelectId) {
      this.monthSelectId = monthSelectId;
   }
}

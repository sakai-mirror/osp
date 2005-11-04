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
 * $Header: /opt/CVS/osp2.x/common/tool-lib/src/java/org/theospi/portfolio/shared/control/tag/DateSelectPopupTag.java,v 1.1 2005/08/17 19:16:44 chmaurer Exp $
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

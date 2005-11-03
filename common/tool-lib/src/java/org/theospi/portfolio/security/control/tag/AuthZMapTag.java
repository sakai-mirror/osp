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
 * $Header: /opt/CVS/osp2.x/common/tool-lib/src/java/org/theospi/portfolio/security/control/tag/AuthZMapTag.java,v 1.1 2005/07/18 23:46:59 jellis Exp $
 * $Revision$
 * $Date$
 */
package org.theospi.portfolio.security.control.tag;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.taglibs.standard.lang.support.ExpressionEvaluatorManager;
import org.theospi.portfolio.security.AuthorizationFacade;
import org.theospi.portfolio.security.model.AuthZMap;
import org.sakaiproject.service.framework.portal.cover.PortalService;
import org.sakaiproject.service.framework.component.cover.ComponentManager;
import org.sakaiproject.metaobj.shared.model.Id;
import org.sakaiproject.metaobj.shared.mgt.IdManager;

import javax.servlet.jsp.tagext.TagSupport;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.JspException;
import java.util.Map;

public class AuthZMapTag extends TagSupport {

   private Id qualifier;
   private String qualifierExpression;
   private boolean useSite;
   private String prefix;
   private String var;
   private int scope;

   protected final transient Log logger = LogFactory.getLog(getClass());

   public AuthZMapTag() {
      init();
   }

   /**
    * Default processing of the start tag, returning SKIP_BODY.
    *
    * @return SKIP_BODY
    * @throws javax.servlet.jsp.JspException if an error occurs while processing this tag
    * @see javax.servlet.jsp.tagext.Tag#doStartTag()
    */

   public int doStartTag() throws JspException {
      Map authz = new AuthZMap(getAuthzFacade(), getPrefix(), evaluateQualifier());

      pageContext.setAttribute(getVar(), authz, getScope());

      return super.doStartTag();
   }

   /**
    * Release state.
    *
    * @see javax.servlet.jsp.tagext.Tag#release()
    */

   public void release() {
      super.release();
      init();
   }

   protected AuthorizationFacade getAuthzFacade() {
      return (AuthorizationFacade)ComponentManager.getInstance().get("authzManager");
   }

   public String getPrefix() {
      return prefix;
   }

   public void setPrefix(String prefix) {
      this.prefix = prefix;
   }

   public Id evaluateQualifier() throws JspException {
      if (isUseSite()) {
         qualifier = getIdManager().getId(PortalService.getCurrentSiteId());
      }
      else if (qualifierExpression == null){
         qualifier = getIdManager().getId(PortalService.getCurrentToolId());
      }
      else {
         qualifier = (Id)ExpressionEvaluatorManager.evaluate(
              "qualifier", qualifierExpression,
             Id.class, this, pageContext);
      }
      return qualifier;
   }

   public void setQualifier(String qualifierExpression) {
      this.qualifierExpression = qualifierExpression;
   }

   public int getScope() {
      return scope;
   }

   public String getVar() {
      return var;
   }

   public void setVar(String var) {
      this.var = var;
   }

   public void setScope(String scope) {
      if (scope.equalsIgnoreCase("page"))
         this.scope = PageContext.PAGE_SCOPE;
      else if (scope.equalsIgnoreCase("request"))
         this.scope = PageContext.REQUEST_SCOPE;
      else if (scope.equalsIgnoreCase("session"))
         this.scope = PageContext.SESSION_SCOPE;
      else if (scope.equalsIgnoreCase("application"))
         this.scope = PageContext.APPLICATION_SCOPE;

      // TODO: Add error handling?  Needs direction from spec.
   }

   // initializes internal state
   protected void init() {
      var = "can";
      scope = PageContext.PAGE_SCOPE;
      setUseSite(false);
   }

   protected IdManager getIdManager() {
      return (IdManager)ComponentManager.getInstance().get("idManager");
   }

   public boolean isUseSite() {
      return useSite;
   }

   public void setUseSite(boolean useSite) {
      this.useSite = useSite;
   }

}

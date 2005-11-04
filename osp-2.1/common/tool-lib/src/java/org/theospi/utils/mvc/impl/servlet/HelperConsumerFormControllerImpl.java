package org.theospi.utils.mvc.impl.servlet;

import org.sakaiproject.metaobj.utils.mvc.impl.servlet.FormControllerImpl;
import org.sakaiproject.api.kernel.tool.ToolManager;
import org.sakaiproject.api.kernel.session.SessionManager;
import org.sakaiproject.api.kernel.session.ToolSession;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Jul 22, 2005
 * Time: 4:14:12 PM
 * To change this template use File | Settings | File Templates.
 */
public class HelperConsumerFormControllerImpl extends FormControllerImpl {

   private static String TOOL_RETURNING_ATTRIBUTE = "theospi.toolReturning";

   private SessionManager sessionManager;

   protected boolean isFormSubmission(HttpServletRequest request) {
      ToolSession currentSession = sessionManager.getCurrentToolSession();

      if (currentSession.getAttribute(TOOL_RETURNING_ATTRIBUTE) != null) {
         currentSession.removeAttribute(TOOL_RETURNING_ATTRIBUTE);
         return true;
      }
      else {
         currentSession.setAttribute(TOOL_RETURNING_ATTRIBUTE, "true");
         return false;
      }
   }

   public SessionManager getSessionManager() {
      return sessionManager;
   }

   public void setSessionManager(SessionManager sessionManager) {
      this.sessionManager = sessionManager;
   }

}

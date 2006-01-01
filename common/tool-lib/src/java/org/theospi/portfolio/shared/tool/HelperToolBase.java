package org.theospi.portfolio.shared.tool;

import org.sakaiproject.api.kernel.session.ToolSession;
import org.sakaiproject.api.kernel.session.cover.SessionManager;
import org.sakaiproject.api.kernel.tool.Tool;
import org.sakaiproject.api.kernel.tool.cover.ToolManager;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Nov 16, 2005
 * Time: 2:57:48 PM
 * To change this template use File | Settings | File Templates.
 */
public class HelperToolBase extends ToolBase {

   protected Object getAttribute(String attributeName) {
      ToolSession session = SessionManager.getCurrentToolSession();
      return session.getAttribute(attributeName);
   }

   protected Object getAttributeOrDefault(String attributeName) {
      ToolSession session = SessionManager.getCurrentToolSession();
      Object returned = session.getAttribute(attributeName);
      if (returned == null) {
         return attributeName;
      }
      return returned;
   }

   protected String returnToCaller() {
      ExternalContext context = FacesContext.getCurrentInstance().getExternalContext();
      Tool tool = ToolManager.getCurrentTool();
      String url = (String) SessionManager.getCurrentToolSession().getAttribute(
            tool.getId() + Tool.HELPER_DONE_URL);
      SessionManager.getCurrentToolSession().removeAttribute(tool.getId() + Tool.HELPER_DONE_URL);
      try {
         context.redirect(url);
      }
      catch (IOException e) {
         throw new RuntimeException("Failed to redirect to caller", e);
      }
      return null;
   }

   protected void removeAttribute(String attrib) {
      removeAttributes(new String[]{attrib});
   }

   protected void removeAttributes(String[] attribs) {
      ToolSession session = SessionManager.getCurrentToolSession();

      for (int i=0;i<attribs.length;i++) {
         session.removeAttribute(attribs[i]);
      }
   }

   protected void setAttribute(String attributeName, Object value) {
      ToolSession session = SessionManager.getCurrentToolSession();
      session.setAttribute(attributeName, value);      
   }

}

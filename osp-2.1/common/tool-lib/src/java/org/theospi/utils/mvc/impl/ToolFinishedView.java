package org.theospi.utils.mvc.impl;

import org.sakaiproject.metaobj.shared.control.RedirectView;
import org.sakaiproject.api.kernel.session.ToolSession;
import org.sakaiproject.api.kernel.session.cover.SessionManager;
import org.sakaiproject.api.kernel.tool.Tool;
import org.sakaiproject.api.kernel.tool.cover.ToolManager;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;
import java.util.Hashtable;
import java.util.Iterator;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Jul 15, 2005
 * Time: 2:06:07 PM
 * To change this template use File | Settings | File Templates.
 */
public class ToolFinishedView extends HelperView {

   public void render(Map model, HttpServletRequest request, HttpServletResponse response) throws Exception {
      ToolSession toolSession = SessionManager.getCurrentToolSession();
      Tool tool = ToolManager.getCurrentTool();

      String url = (String) toolSession.getAttribute(
            tool.getId() + Tool.HELPER_DONE_URL);

      toolSession.removeAttribute(tool.getId() + Tool.HELPER_DONE_URL);

      setUrl(url);

      if (getModelPrefix() == null) {
         setModelPrefix("");
      }

      super.render(model, request, response);
   }


}

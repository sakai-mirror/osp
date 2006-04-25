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
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Jul 15, 2005
 * Time: 2:06:07 PM
 * To change this template use File | Settings | File Templates.
 */
public class ToolFinishedView extends HelperView {

   /** the alternate next view */
   public static final String ALTERNATE_DONE_URL = "altDoneURL";
   
   /** the set of alternate views */
   public static final String ALTERNATE_DONE_URL_SET = "altDoneURLSet";
   
   public void render(Map model, HttpServletRequest request, HttpServletResponse response) throws Exception {
      ToolSession toolSession = SessionManager.getCurrentToolSession();
      Tool tool = ToolManager.getCurrentTool();

      String url = (String) toolSession.getAttribute(
            tool.getId() + Tool.HELPER_DONE_URL);
      
      toolSession.removeAttribute(tool.getId() + Tool.HELPER_DONE_URL);
      
      String path = "";
      Object altObj = toolSession.getAttribute(ALTERNATE_DONE_URL);
      Object pathObj = toolSession.getAttribute(tool.getId() + "thetoolPath");

      if(altObj != null) {
         url = (String) toolSession.getAttribute((String)altObj);

         if(pathObj != null) {
            path = (String) pathObj;
         }
         
         if(!url.startsWith("/"))
            url = path + "/" + url;
      }
      if(toolSession.getAttribute(ALTERNATE_DONE_URL_SET) != null) {
         List views = (List) toolSession.getAttribute(ALTERNATE_DONE_URL_SET);
         
         for(Iterator i = views.iterator(); i.hasNext();) {
            toolSession.removeAttribute((String)i.next());
         }
         toolSession.removeAttribute(ALTERNATE_DONE_URL_SET);
      }
      toolSession.removeAttribute(tool.getId() + "thetoolPath");

      
      setUrl(url);

      if (getModelPrefix() == null) {
         setModelPrefix("");
      }

      super.render(model, request, response);
   }


}

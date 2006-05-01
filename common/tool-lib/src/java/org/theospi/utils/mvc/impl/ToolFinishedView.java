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
import org.sakaiproject.tool.api.ToolSession;
import org.sakaiproject.tool.cover.SessionManager;
import org.sakaiproject.tool.api.Tool;
import org.sakaiproject.tool.cover.ToolManager;

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
   public static final String ALTERNATE_DONE_URL_MAP = "altDoneURLSet";
   
   public void render(Map model, HttpServletRequest request, HttpServletResponse response) throws Exception {
      ToolSession toolSession = SessionManager.getCurrentToolSession();
      Tool tool = ToolManager.getCurrentTool();

      String url = (String) toolSession.getAttribute(
            tool.getId() + Tool.HELPER_DONE_URL);
      
      toolSession.removeAttribute(tool.getId() + Tool.HELPER_DONE_URL);
      
      String pathObj = (String)toolSession.getAttribute(tool.getId() + "thetoolPath");
      toolSession.removeAttribute(tool.getId() + "thetoolPath");
      
      if(toolSession.getAttribute(ALTERNATE_DONE_URL_MAP) != null) {
         String path = "";
         Object altObj = toolSession.getAttribute(ALTERNATE_DONE_URL);
         Map urlMap = (Map)toolSession.getAttribute(ALTERNATE_DONE_URL_MAP);
   
         if(altObj != null) {
            url = (String) urlMap.get((String)altObj);
   
            if(pathObj != null) {
               path = (String) pathObj;
            }
            
            if(!url.startsWith("/"))
               url = path + "/" + url;
         }

         toolSession.removeAttribute(ALTERNATE_DONE_URL_MAP);
         toolSession.removeAttribute(ALTERNATE_DONE_URL);
      }

      
      setUrl(url);

      if (getModelPrefix() == null) {
         setModelPrefix("");
      }

      super.render(model, request, response);
   }


}

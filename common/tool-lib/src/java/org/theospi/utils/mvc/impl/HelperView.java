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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;
import java.util.Iterator;
import java.io.UnsupportedEncodingException;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Dec 31, 2005
 * Time: 11:25:24 AM
 * To change this template use File | Settings | File Templates.
 */
public class HelperView extends RedirectView {

   private String modelPrefix;

   public void render(Map model, HttpServletRequest request,
                      HttpServletResponse response) throws Exception {
      // take model entries and register them as helper params
      ToolSession toolSession = SessionManager.getCurrentToolSession();

      for (Iterator i=model.entrySet().iterator();i.hasNext();) {
         Map.Entry entry = (Map.Entry) i.next();
         toolSession.setAttribute(createName(entry.getKey()),
            entry.getValue());
      }

      super.render(model, request, response);
   }

   protected String createName(Object key) {
      return getModelPrefix() + key;
   }

   public String getModelPrefix() {
      return modelPrefix;
   }

   public void setModelPrefix(String modelPrefix) {
      this.modelPrefix = modelPrefix;
   }

}

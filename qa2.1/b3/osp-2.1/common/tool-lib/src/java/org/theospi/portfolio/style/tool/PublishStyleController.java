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
package org.theospi.portfolio.style.tool;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.validation.Errors;
import org.springframework.web.servlet.ModelAndView;
import org.theospi.portfolio.style.StyleFunctionConstants;
import org.theospi.portfolio.style.model.Style;
import org.sakaiproject.metaobj.shared.model.Id;
import org.sakaiproject.metaobj.shared.model.PersistenceException;
import org.sakaiproject.metaobj.utils.mvc.intf.LoadObjectController;

import java.util.Map;

public class PublishStyleController extends ListStyleController implements LoadObjectController {
   protected final Log logger = LogFactory.getLog(getClass());

   public ModelAndView handleRequest(Object requestModel, Map request, Map session, Map application, Errors errors) {
      //Style style = (Style) requestModel;
      Id styleId = getIdManager().getId((String)request.get("style_id"));
      Style style = getStyleManager().getStyle(styleId);

      String publishTo = (String)request.get("publishTo");
      if (publishTo.equals("global")) {
         style.setGlobalState(Style.STATE_PUBLISHED);
         style.setSiteId(null);
         doSave(style, StyleFunctionConstants.GLOBAL_PUBLISH_STYLE, errors);
      }
      else {
         style.setGlobalState(Style.STATE_WAITING_APPROVAL);
         doSave(style, StyleFunctionConstants.SUGGEST_GLOBAL_PUBLISH_STYLE, errors);
      }

      request.put("newStyleId", style.getId().getValue());

      return super.handleRequest(requestModel, request, session, application, errors);
   }
   
   protected void doSave(Style style, String function, Errors errors) {
      checkPermission(function);
      try{
         getStyleManager().storeStyle(style);
      } catch (PersistenceException e){
         errors.rejectValue(e.getField(), e.getErrorCode(), e.getErrorInfo(),
               e.getDefaultMessage());
      }
   }
   
   public Object fillBackingObject(Object incomingModel, Map request, Map session, Map application) throws Exception {
      //Style style = (Style) incomingModel;
      Id styleId = getIdManager().getId((String)request.get("style_id"));
      String publishTo = (String)request.get("publishTo");
      String function = "";
      if (publishTo.equals("global")) {
         function = StyleFunctionConstants.GLOBAL_PUBLISH_STYLE; 
      }
      else if (publishTo.equals("site")){
         function = StyleFunctionConstants.PUBLISH_STYLE;
      }
      else {
         function = StyleFunctionConstants.SUGGEST_GLOBAL_PUBLISH_STYLE;
      }
      getAuthzManager().checkPermission(function, styleId);
      return getStyleManager().getStyle(styleId);
   }
}

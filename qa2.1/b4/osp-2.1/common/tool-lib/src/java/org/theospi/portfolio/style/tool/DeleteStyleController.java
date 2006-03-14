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
import org.sakaiproject.metaobj.shared.model.Id;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.validation.Errors;
import org.springframework.web.servlet.ModelAndView;
import org.theospi.portfolio.style.StyleFunctionConstants;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Apr 22, 2004
 * Time: 9:58:31 AM
 * To change this template use File | Settings | File Templates.
 */
public class DeleteStyleController extends ListStyleController {
   protected final Log logger = LogFactory.getLog(getClass());

   public ModelAndView handleRequest(Object requestModel, Map request, Map session, Map application, Errors errors) {
      Id id = getIdManager().getId((String)request.get("style_id"));
      getAuthzManager().checkPermission(StyleFunctionConstants.DELETE_STYLE, id);
      Map model = new HashMap();
      try {
         getStyleManager().deleteStyle(id);
      }
      catch (DataIntegrityViolationException e) {
         logger.warn("Failed to delete Style");
         model.put("styleError", "cant_delete_style");
      }
      return new ModelAndView("success", model);
   }

}

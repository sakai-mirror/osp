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
package org.theospi.portfolio.presentation.control;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.validation.Errors;
import org.springframework.web.servlet.ModelAndView;
import org.theospi.portfolio.presentation.model.PresentationTemplate;
import org.theospi.portfolio.presentation.PresentationFunctionConstants;

import java.util.Map;
import java.util.Hashtable;

public class CopyTemplateController extends DeleteTemplateController {
   protected final transient Log logger = LogFactory.getLog(getClass());

   public ModelAndView handleRequest(Object requestModel, Map request, Map session, Map application, Errors errors) {
      PresentationTemplate template = (PresentationTemplate) requestModel;
      getAuthzManager().checkPermission(PresentationFunctionConstants.COPY_TEMPLATE, template.getId());
      PresentationTemplate newTemplate = getPresentationManager().copyTemplate(template.getId());

      Map model = new Hashtable();
      model.put("newPresentationTemplateId", newTemplate.getId().getValue());

      return new ModelAndView("success", model);
   }
}

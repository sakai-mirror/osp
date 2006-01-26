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
import org.sakaiproject.metaobj.utils.mvc.intf.LoadObjectController;

import java.util.Map;

public class PublishTemplateController extends ListTemplateController implements LoadObjectController {
   protected final Log logger = LogFactory.getLog(getClass());

   public ModelAndView handleRequest(Object requestModel, Map request, Map session, Map application, Errors errors) {
      PresentationTemplate template = (PresentationTemplate) requestModel;
      template = getPresentationManager().getPresentationTemplate(template.getId());

      template.setPublished(true);
      
      getPresentationManager().storeTemplate(template);

      request.put("newPresentationTemplateId", template.getId().getValue());

      return super.handleRequest(requestModel, request, session, application, errors);
   }

   public Object fillBackingObject(Object incomingModel, Map request, Map session, Map application) throws Exception {
      PresentationTemplate template = (PresentationTemplate) incomingModel;
      getAuthzManager().checkPermission(PresentationFunctionConstants.PUBLISH_TEMPLATE, template.getId());
      return getPresentationManager().getPresentationTemplate(template.getId());
   }
}

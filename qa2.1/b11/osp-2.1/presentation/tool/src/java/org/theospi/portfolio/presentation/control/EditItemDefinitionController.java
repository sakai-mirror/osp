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

import org.springframework.validation.Errors;
import org.springframework.web.servlet.ModelAndView;
import org.theospi.portfolio.presentation.model.PresentationItemDefinition;
import org.theospi.portfolio.presentation.model.PresentationTemplate;
import org.sakaiproject.metaobj.utils.mvc.intf.Controller;

import java.util.Hashtable;
import java.util.Map;
import java.util.Iterator;

public class EditItemDefinitionController extends AbstractPresentationController implements Controller {
   public ModelAndView handleRequest(Object requestModel, Map request, Map session, Map application, Errors errors) {
      PresentationItemDefinition itemDef = (PresentationItemDefinition) requestModel;
      PresentationTemplate template = getActiveTemplate(session);

      for (Iterator i=template.getItemDefinitions().iterator(); i.hasNext(); ){
         PresentationItemDefinition nextItemDef = (PresentationItemDefinition) i.next();
         if (itemDef.getId().equals(nextItemDef.getId())){
            template.getItem().setAction(null); // clear the action
            template.setItem(nextItemDef);
            break;
         }
      }

      Hashtable params = new Hashtable();
      params.put("_target2", "true");
      params.put("editItem", "true");
      params.put("formSubmission", "true");
      params.put("item.action", "none");
      return new ModelAndView("success", params);
   }


}


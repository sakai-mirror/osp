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
import org.theospi.portfolio.presentation.model.TemplateFileRef;
import org.theospi.portfolio.presentation.model.PresentationTemplate;
import org.sakaiproject.metaobj.utils.mvc.intf.Controller;

import java.util.Hashtable;
import java.util.Map;
import java.util.Iterator;

public class EditTemplateFileController extends AbstractPresentationController implements Controller {
   protected final transient Log logger = LogFactory.getLog(getClass());

   public ModelAndView handleRequest(Object requestModel, Map request, Map session, Map application, Errors errors) {
      TemplateFileRef file = (TemplateFileRef) requestModel;
      PresentationTemplate template = getActiveTemplate(session);

      for (Iterator i=template.getFiles().iterator(); i.hasNext(); ){
         TemplateFileRef nextFile = (TemplateFileRef) i.next();
         if (file.getId().equals(nextFile.getId())){
            template.getFileRef().setAction(null); // clear the action
            template.setFileRef(nextFile);
            break;
         }
      }

      Hashtable params = new Hashtable();
      params.put("_target3", "true");
      params.put("editFile", "true");
      params.put("formSubmission", "true");
      params.put("fileRef.action", "none");
      return new ModelAndView("success", params);
   }

}

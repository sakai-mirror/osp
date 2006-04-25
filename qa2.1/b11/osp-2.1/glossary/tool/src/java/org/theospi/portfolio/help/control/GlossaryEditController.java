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
package org.theospi.portfolio.help.control;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.theospi.portfolio.help.model.GlossaryEntry;
import org.springframework.validation.Errors;
import org.springframework.web.servlet.ModelAndView;
import org.sakaiproject.metaobj.utils.mvc.intf.LoadObjectController;
import org.sakaiproject.metaobj.shared.model.PersistenceException;

import java.util.Map;

public class GlossaryEditController extends HelpController implements LoadObjectController {
   protected final transient Log logger = LogFactory.getLog(getClass());

   public Object fillBackingObject(Object incomingModel, Map request,
                                   Map session, Map application) throws Exception {
      GlossaryEntry entry = (GlossaryEntry)incomingModel;

      if (entry.getId() == null) {
         return entry;
      }
      else {
         return getHelpManager().getGlossary().load(entry.getId());
      }
   }

   public ModelAndView handleRequest(Object requestModel, Map request, Map session, Map application, Errors errors) {
      GlossaryEntry entry = (GlossaryEntry)requestModel;
      getHelpManager().removeFromSession(entry);

      try {
         if (entry.getId() == null) {
	         entry = getHelpManager().addEntry(entry);
	      }
	      else {
	         getHelpManager().updateEntry(entry);
	      }
      } catch (PersistenceException e){
         errors.rejectValue(e.getField(), e.getErrorCode(), e.getErrorInfo(),
               e.getDefaultMessage());
      }

      if (entry != null && entry.getId() != null) {
         return new ModelAndView("success", "newTermId", entry.getId().getValue());
      }
      else {
         return new ModelAndView("success");
      }

   }
}

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
import org.sakaiproject.metaobj.utils.mvc.intf.Controller;
import org.springframework.validation.Errors;
import org.springframework.web.servlet.ModelAndView;
import org.theospi.portfolio.help.model.GlossaryEntry;
import org.theospi.portfolio.help.model.HelpManager;

import java.util.Map;

public class GlossaryController implements Controller {
   private HelpManager helpManager;

   protected final Log logger = LogFactory.getLog(getClass());

   public ModelAndView handleRequest(Object requestModel, Map request, Map session, Map application, Errors errors) {
      GlossaryEntry entry = (GlossaryEntry)requestModel;
      entry = getHelpManager().getGlossary().load(entry.getId());
      return new ModelAndView("success", "entry", entry);
   }

   public HelpManager getHelpManager() {
      return helpManager;
   }

   public void setHelpManager(HelpManager helpManager) {
      this.helpManager = helpManager;
   }

}

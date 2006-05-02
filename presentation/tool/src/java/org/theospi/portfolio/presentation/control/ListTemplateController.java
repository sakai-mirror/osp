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
import org.sakaiproject.metaobj.shared.model.Agent;
import org.sakaiproject.metaobj.utils.mvc.intf.ListScrollIndexer;
import org.sakaiproject.metaobj.utils.mvc.intf.ListScroll;
import org.sakaiproject.tool.cover.ToolManager;


import java.util.*;

public class ListTemplateController extends AbstractPresentationController {

   protected final Log logger = LogFactory.getLog(getClass());
   private ListScrollIndexer listScrollIndexer;

   public ModelAndView handleRequest(Object requestModel, Map request, Map session,
                                     Map application, Errors errors) {

      Hashtable model = new Hashtable();
      Agent agent = getAuthManager().getAgent();
      List templates = new ArrayList(
         getPresentationManager().findTemplatesByOwner(agent, ToolManager.getCurrentPlacement().getContext()));
      templates.addAll(getPresentationManager().findPublishedTemplates(ToolManager.getCurrentPlacement().getContext()));
      model.put("templateCount", String.valueOf(templates.size()));

      if (request.get("newPresentationTemplateId") != null) {
         request.put(ListScroll.ENSURE_VISIBLE_TAG, "" + getPresentationIndex(templates,
            (String)request.get("newPresentationTemplateId")));
      }

      templates = getListScrollIndexer().indexList(request, model, templates);
      model.put("templates", templates);

      model.put("osp_agent", agent);
      String worksiteId = getWorksiteManager().getCurrentWorksiteId().getValue();
      model.put("worksite", getWorksiteManager().getSite(worksiteId));
      model.put("tool", getWorksiteManager().getTool(ToolManager.getCurrentPlacement().getToolId()));
      model.put("isMaintainer", isMaintainer());
      return new ModelAndView("success", model);
   }

   protected int getPresentationIndex(List templates, String templateId) {
      if (templateId == null) {
         return 0;
      }

      for (int i=0;i<templates.size();i++){
         PresentationTemplate current = (PresentationTemplate)templates.get(i);
         if (current.getId().getValue().equals(templateId)) {
            return i;
         }
      }
      return 0;
   }

   public ListScrollIndexer getListScrollIndexer() {
      return listScrollIndexer;
   }

   public void setListScrollIndexer(ListScrollIndexer listScrollIndexer) {
      this.listScrollIndexer = listScrollIndexer;
   }

}

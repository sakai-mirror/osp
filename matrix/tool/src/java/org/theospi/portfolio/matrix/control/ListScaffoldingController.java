/**********************************************************************************
* $URL$
* $Id$
***********************************************************************************
*
* Copyright (c) 2006 The Sakai Foundation.
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

package org.theospi.portfolio.matrix.control;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.metaobj.shared.model.Agent;
import org.sakaiproject.metaobj.utils.mvc.intf.ListScrollIndexer;
import org.sakaiproject.tool.cover.ToolManager;

import org.springframework.validation.Errors;
import org.springframework.web.servlet.ModelAndView;

public class ListScaffoldingController extends AbstractMatrixController {

   protected final Log logger = LogFactory.getLog(getClass());
   private ListScrollIndexer listScrollIndexer;

   public ModelAndView handleRequest(Object requestModel, Map request, Map session, Map application, Errors errors) {
      Hashtable model = new Hashtable();
      Agent currentAgent = getAuthManager().getAgent();
      String currentToolId = ToolManager.getCurrentPlacement().getToolId();
      String worksiteId = getWorksiteManager().getCurrentWorksiteId().getValue();

      List scaffolding = new ArrayList(getMatrixManager().findScaffolding(
            worksiteId, currentToolId, currentAgent.getId().getValue()));
      
      // When selecting a matrix the user should start with a fresh user
      session.remove(ViewMatrixController.VIEW_USER);

      model.put("scaffolding",
         getListScrollIndexer().indexList(request, model, scaffolding));

      model.put("worksite", getWorksiteManager().getSite(worksiteId));
      model.put("tool", getWorksiteManager().getTool(currentToolId));
      model.put("isMaintainer", isMaintainer());
      model.put("osp_agent", currentAgent);
      return new ModelAndView("success", model);
   }

   public ListScrollIndexer getListScrollIndexer() {
      return listScrollIndexer;
   }

   public void setListScrollIndexer(ListScrollIndexer listScrollIndexer) {
      this.listScrollIndexer = listScrollIndexer;
   }
}

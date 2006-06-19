/**********************************************************************************
* $URL:https://source.sakaiproject.org/svn/osp/trunk/presentation/tool/src/java/org/theospi/portfolio/presentation/control/ListPresentationController.java $
* $Id:ListPresentationController.java 9134 2006-05-08 20:28:42Z chmaurer@iupui.edu $
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

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.component.api.ServerConfigurationService;
import org.sakaiproject.metaobj.shared.model.Agent;
import org.sakaiproject.metaobj.utils.mvc.intf.ListScrollIndexer;
import org.sakaiproject.tool.cover.ToolManager;
import org.sakaiproject.tool.cover.SessionManager;
import org.sakaiproject.tool.api.Tool;
import org.springframework.validation.Errors;
import org.springframework.web.servlet.ModelAndView;

public class ListPresentationController extends AbstractPresentationController {

   protected final Log logger = LogFactory.getLog(getClass());
   private ListScrollIndexer listScrollIndexer;
   private ServerConfigurationService serverConfigurationService;

   public ModelAndView handleRequest(Object requestModel, Map request, Map session, Map application, Errors errors) {
      Hashtable model = new Hashtable();
      Agent currentAgent = getAuthManager().getAgent();
      String currentToolId = ToolManager.getCurrentPlacement().getId();
      String worksiteId = getWorksiteManager().getCurrentWorksiteId().getValue();

      List presentations = new ArrayList(getPresentationManager().findPresentationsByViewer(currentAgent,
         currentToolId));

      model.put("presentations",
         getListScrollIndexer().indexList(request, model, presentations));

      String baseUrl = getServerConfigurationService().getServerUrl();

      String url =  baseUrl + "/osp-presentation-tool/viewPresentation.osp?";
      url += Tool.PLACEMENT_ID + "=" + SessionManager.getCurrentToolSession().getPlacementId();
      model.put("baseUrl", url);
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
    public ServerConfigurationService getServerConfigurationService() {
      return serverConfigurationService;
   }

   public void setServerConfigurationService(
         ServerConfigurationService serverConfigurationService) {
      this.serverConfigurationService = serverConfigurationService;
   }

}

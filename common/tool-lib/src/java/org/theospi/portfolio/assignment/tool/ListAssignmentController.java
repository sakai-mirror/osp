/********************************************************************************** $
* $Id: $
***********************************************************************************
*
* Copyright (c) 2007 The Sakai Foundation.
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
package org.theospi.portfolio.assignment.tool;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.theospi.utils.mvc.impl.servlet.AbstractFormController;
import org.sakaiproject.metaobj.utils.mvc.intf.Controller;
import org.sakaiproject.metaobj.shared.mgt.IdManager;
import org.sakaiproject.metaobj.shared.mgt.AgentManager;
import org.sakaiproject.metaobj.security.AuthenticationManager;
import org.sakaiproject.metaobj.security.AuthorizationFailedException;
import org.sakaiproject.metaobj.shared.model.Agent;
import org.sakaiproject.metaobj.utils.mvc.intf.ListScroll;
import org.sakaiproject.metaobj.utils.mvc.intf.ListScrollIndexer;
import org.sakaiproject.metaobj.worksite.mgt.WorksiteManager;
import org.sakaiproject.tool.cover.ToolManager;
import org.sakaiproject.assignment.api.AssignmentService;
import org.sakaiproject.assignment.api.Assignment;

import org.springframework.validation.Errors;
import org.springframework.web.servlet.ModelAndView;

public class ListAssignmentController extends AbstractFormController implements Controller {

   protected final Log logger = LogFactory.getLog(getClass());
   private ListScrollIndexer listScrollIndexer;
   private AgentManager agentManager;
   private AuthenticationManager authManager;
   private IdManager idManager;
   private WorksiteManager worksiteManager;
	private AssignmentService assignmentService;

   public ModelAndView handleRequest(Object requestModel, Map request, Map session,
                                     Map application, Errors errors) {

      Hashtable model = new Hashtable();
      Agent agent = getAuthManager().getAgent();
      List assignments = new ArrayList();
      String goBack = (String)request.get("goBack");
		
		if ( goBack != null && !goBack.equals("") )
		{
         return new ModelAndView("done", model);
		}
		
		else
		{
			String context = ToolManager.getCurrentPlacement().getContext();
   		assignments = assignmentService.getListAssignmentsForContext(context); 
         assignments = getListScrollIndexer().indexList(request, model, assignments);
         model.put("assignments", assignments);
   
         model.put("osp_agent", agent);
         String worksiteId = getWorksiteManager().getCurrentWorksiteId().getValue();
         model.put("worksite", getWorksiteManager().getSite(worksiteId));
         model.put("tool", getWorksiteManager().getTool(ToolManager.getCurrentPlacement().getId()));
         
         return new ModelAndView("success", model);
		}

   }

   public ListScrollIndexer getListScrollIndexer() {
      return listScrollIndexer;
   }

   public void setListScrollIndexer(ListScrollIndexer listScrollIndexer) {
      this.listScrollIndexer = listScrollIndexer;
   }

   public WorksiteManager getWorksiteManager() {
      return worksiteManager;
   }

   public void setWorksiteManager(WorksiteManager worksiteManager) {
      this.worksiteManager = worksiteManager;
   }

   public AuthenticationManager getAuthManager() {
      return authManager;
   }

   public void setAuthManager(AuthenticationManager authManager) {
      this.authManager = authManager;
   }
	
   public AgentManager getAgentManager() {
      return agentManager;
   }

   public void setAgentManager(AgentManager agentManager) {
      this.agentManager = agentManager;
   }

   public IdManager getIdManager() {
      return idManager;
   }

   public void setIdManager(IdManager idManager) {
      this.idManager = idManager;
   }

   public AssignmentService getAssignmentService() {
      return assignmentService;
   }

   public void setAssignmentService(AssignmentService assignmentService) {
      this.assignmentService = assignmentService;
   }
}

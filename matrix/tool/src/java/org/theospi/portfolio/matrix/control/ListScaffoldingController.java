/**********************************************************************************
* $URL:https://source.sakaiproject.org/svn/osp/trunk/matrix/tool/src/java/org/theospi/portfolio/matrix/control/ListScaffoldingController.java $
* $Id:ListScaffoldingController.java 9134 2006-05-08 20:28:42Z chmaurer@iupui.edu $
***********************************************************************************
*
* Copyright (c) 2006, 2007 The Sakai Foundation.
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
import java.util.Iterator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.metaobj.shared.model.Agent;
import org.sakaiproject.metaobj.utils.mvc.intf.ListScrollIndexer;
import org.sakaiproject.metaobj.shared.mgt.IdManager;
import org.sakaiproject.metaobj.shared.model.Id;
import org.sakaiproject.tool.cover.ToolManager;
import org.springframework.validation.Errors;
import org.springframework.web.servlet.ModelAndView;
import org.sakaiproject.site.api.Site;
import org.sakaiproject.site.api.SiteService;

public class ListScaffoldingController extends AbstractMatrixController {

   protected final Log logger = LogFactory.getLog(getClass());
   private ListScrollIndexer listScrollIndexer;
   private SiteService siteService;
	private IdManager idManager;

   public ModelAndView handleRequest(Object requestModel, Map request, Map session, Map application, Errors errors) {
      Hashtable<String, Object> model = new Hashtable<String, Object>();
      Agent currentAgent = getAuthManager().getAgent();
      String currentToolId = ToolManager.getCurrentPlacement().getId();
      String worksiteId = getWorksiteManager().getCurrentWorksiteId().getValue();
		List scaffolding = null;

		if ( isOnWorkspaceTab() )
		{
			scaffolding = getMatrixManager().findAvailableScaffolding(getUserWorksites(), currentAgent);
		}
		else
		{
			scaffolding = getMatrixManager().findAvailableScaffolding(worksiteId, currentAgent);
		}
      
      // When selecting a matrix the user should start with a fresh user
      session.remove(ViewMatrixController.VIEW_USER);

      model.put("scaffolding",
         getListScrollIndexer().indexList(request, model, scaffolding));

      model.put("worksite", getWorksiteManager().getSite(worksiteId));
      model.put("tool", getWorksiteManager().getTool(currentToolId));
      model.put("isMaintainer", isMaintainer());
      model.put("osp_agent", currentAgent);
		model.put("myworkspace", isOnWorkspaceTab() );
      
      model.put("useExperimentalMatrix", getMatrixManager().isUseExperimentalMatrix());
      
      return new ModelAndView("success", model);
   }

   public ListScrollIndexer getListScrollIndexer() {
      return listScrollIndexer;
   }

   public void setListScrollIndexer(ListScrollIndexer listScrollIndexer) {
      this.listScrollIndexer = listScrollIndexer;
   }
	
   public SiteService getSiteService() {
      return siteService;
   }

   public void setSiteService(SiteService siteService) {
      this.siteService = siteService;
   }
	
   /**
    * @return the idManager
    */
   public IdManager getIdManager() {
      return idManager;
   }
   /**
    * @param idManager the idManager to set
    */
   public void setIdManager(IdManager idManager) {
      this.idManager = idManager;
   }
	
   /**
    * See if the current tab is the workspace tab.
    * @return true if we are currently on the "My Workspace" tab.
    */
   private boolean isOnWorkspaceTab()
   {
      return siteService.isUserSite(ToolManager.getCurrentPlacement().getContext());
   }
	
	/**
	 ** Return list of worksite Ids for current user
	 **/
	private List getUserWorksites()
	{		 
		List siteList = siteService.getSites(SiteService.SelectionType.ACCESS,
														 null, null, null, 
														 SiteService.SortType.TITLE_ASC, null);
      List siteStrIds = new ArrayList(siteList.size());
		for (Iterator it = siteList.iterator(); it.hasNext();) 
		{
			Site site = (Site) it.next();
         String siteId = site.getId();
			siteStrIds.add( idManager.getId(siteId) );
		}
		
		return siteStrIds;
	}
}

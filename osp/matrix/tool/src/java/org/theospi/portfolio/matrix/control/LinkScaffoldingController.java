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
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.assignment.taggable.api.TaggableActivity;
import org.sakaiproject.assignment.taggable.api.TaggingManager;
import org.sakaiproject.assignment.taggable.api.TaggingProvider;
import org.sakaiproject.exception.IdUnusedException;
import org.sakaiproject.gmt.api.GmtService;
import org.sakaiproject.metaobj.shared.model.Agent;
import org.sakaiproject.metaobj.utils.mvc.intf.ListScrollIndexer;
import org.sakaiproject.metaobj.shared.mgt.IdManager;
import org.sakaiproject.tool.api.SessionManager;
import org.sakaiproject.tool.cover.ToolManager;
import org.springframework.validation.Errors;
import org.springframework.web.servlet.ModelAndView;
import org.sakaiproject.site.api.Site;
import org.sakaiproject.site.api.SiteService;
import org.theospi.portfolio.matrix.model.Criterion;
import org.theospi.portfolio.matrix.model.Level;
import org.theospi.portfolio.matrix.model.Scaffolding;
import org.theospi.portfolio.matrix.model.ScaffoldingCell;
import org.theospi.portfolio.tagging.api.MatrixTaggingProvider;

public class LinkScaffoldingController extends AbstractMatrixController {

	protected final Log logger = LogFactory.getLog(getClass());
	private ListScrollIndexer listScrollIndexer;
	private SiteService siteService;
	private IdManager idManager;
	private SessionManager sessionManager;
	private TaggingManager taggingManager;
	private TaggingProvider matrixTaggingProvider;
	private GmtService gmtService;

	public ModelAndView handleRequest(Object requestModel, Map request, Map session, Map application, Errors errors) {
		Hashtable<String, Object> model = new Hashtable<String, Object>();
		Agent currentAgent = getAuthManager().getAgent();
		//String currentToolId = ToolManager.getCurrentPlacement().getId();
		String worksiteId = getWorksiteManager().getCurrentWorksiteId().getValue();
		
		TaggableActivity currentActivity = getCurrentActivity();

		String selectedSite = (String)request.get("selectedSite");
		if (selectedSite == null) selectedSite = worksiteId;

		List<Site> sites = getAvailableSites(currentActivity);

		List<Scaffolding> scaffolding = getMatrixManager().findAvailableScaffolding(selectedSite, currentAgent);
		List<MatrixGridBean> grids = getScaffoldingGrids(scaffolding);

		// When selecting a matrix the user should start with a fresh user
		session.remove(ViewMatrixController.VIEW_USER);

		model.put("grids", getListScrollIndexer().indexList(request, model, grids));
		model.put("currentActivity", currentActivity);

		//model.put("worksite", getWorksiteManager().getSite(worksiteId));
		//model.put("tool", getWorksiteManager().getTool(currentToolId));
		//model.put("isMaintainer", isMaintainer());
		//model.put("osp_agent", currentAgent);
		//model.put("myworkspace", isOnWorkspaceTab() );
		model.put("sites", sites);
		model.put("selectedSite", selectedSite);

		//model.put("useExperimentalMatrix", getMatrixManager().isUseExperimentalMatrix());

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

	private List<MatrixGridBean> getScaffoldingGrids(List<Scaffolding> scaffoldingList) {
		List<MatrixGridBean> beanList = new ArrayList<MatrixGridBean>(scaffoldingList.size());
		for (Scaffolding scaffolding : scaffoldingList) {
			MatrixGridBean grid = new MatrixGridBean();

			List<Level> levels = scaffolding.getLevels();
			List<Criterion> criteria = scaffolding.getCriteria();
			List<List<ScaffoldingCell>> matrixContents = new ArrayList<List<ScaffoldingCell>>();
			Criterion criterion = new Criterion();
			Level level = new Level();
			List<ScaffoldingCell> row = new ArrayList<ScaffoldingCell>();

			Set<ScaffoldingCell> cells = getMatrixManager().getScaffoldingCells(scaffolding.getId());

			for (Iterator<Criterion> criteriaIterator = criteria.iterator(); criteriaIterator.hasNext();) {
				row = new ArrayList<ScaffoldingCell>();
				criterion = (Criterion) criteriaIterator.next();
				for (Iterator<Level> levelsIterator = levels.iterator(); levelsIterator.hasNext();) {
					level = (Level) levelsIterator.next();
					ScaffoldingCell scaffoldingCell = getScaffoldingCell(cells, criterion, level);

					row.add(scaffoldingCell);
				}
				matrixContents.add(row);
			}

			grid.setScaffolding(scaffolding);
			grid.setColumnLabels(levels);
			grid.setRowLabels(criteria);
			grid.setMatrixContents(matrixContents);
			beanList.add(grid);
		}
		return beanList;
	}

	/**
	 * Lookup the cell from the cells list that matches the passed criterion and level
	 * @param cells
	 * @param criterion
	 * @param level
	 * @return
	 */
	private ScaffoldingCell getScaffoldingCell(Set<ScaffoldingCell> cells, Criterion criterion, Level level) {
		for (Iterator<ScaffoldingCell> iter=cells.iterator(); iter.hasNext();) {
			ScaffoldingCell scaffoldingCell = (ScaffoldingCell) iter.next();
			if (scaffoldingCell.getRootCriterion().getId().getValue().equals(criterion.getId().getValue()) && 
					scaffoldingCell.getLevel().getId().getValue().equals(level.getId().getValue())) {
				return scaffoldingCell;
			}
		}
		return null;
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
	 * Find the sites that are associated to this site
	 * @param sites Pass an empty list to fill up
	 * @param siteIds Pass an empty list to fill up
	 */
	protected List<Site> getAvailableSites(TaggableActivity currentActivity) {
		String fromContext = currentActivity.getContext();

		List<String> contexts = getGmtService().getAssociatedFrom(fromContext);
		List<Site> sites = new ArrayList<Site>(contexts.size());

		for (String toContext : contexts) {
			if (getGmtService().allowModifyLinks(fromContext, toContext)) {
				try {
					Site site = getSiteService().getSite(toContext);
					sites.add(site);
				} catch (IdUnusedException iue) {
					logger.error(iue.getMessage(), iue);
				}
			}
		}
		return sites;
	}

	protected TaggableActivity getCurrentActivity() {
		String activityRef = (String) getSessionManager()
		.getCurrentToolSession().getAttribute(MatrixTaggingProvider.ACTIVITY_REF);
		TaggableActivity activity = 
			getTaggingManager().getActivity(activityRef, getMatrixTaggingProvider());
		return activity;
	}

	protected TaggingProvider getMatrixTaggingProvider() {
		if (matrixTaggingProvider == null) {
			matrixTaggingProvider = getTaggingManager().findProviderById(
					MatrixTaggingProvider.PROVIDER_ID);
		}
		return matrixTaggingProvider;
	}

	public SessionManager getSessionManager() {
		return sessionManager;
	}

	public void setSessionManager(SessionManager sessionManager) {
		this.sessionManager = sessionManager;
	}

	public TaggingManager getTaggingManager() {
		return taggingManager;
	}

	public void setTaggingManager(TaggingManager taggingManager) {
		this.taggingManager = taggingManager;
	}

	public void setMatrixTaggingProvider(TaggingProvider matrixTaggingProvider) {
		this.matrixTaggingProvider = matrixTaggingProvider;
	}

	public GmtService getGmtService() {
		return gmtService;
	}

	public void setGmtService(GmtService gmtService) {
		this.gmtService = gmtService;
	}
}

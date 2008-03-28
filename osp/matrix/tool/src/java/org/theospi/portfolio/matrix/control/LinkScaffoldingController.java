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
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.taggable.api.Link;
import org.sakaiproject.taggable.api.LinkManager;
import org.sakaiproject.taggable.api.TaggableActivity;
import org.sakaiproject.taggable.api.TaggingManager;
import org.sakaiproject.taggable.api.TaggingProvider;
import org.sakaiproject.entity.api.Reference;
import org.sakaiproject.entity.cover.EntityManager;
import org.sakaiproject.exception.IdUnusedException;
import org.sakaiproject.exception.PermissionException;
import org.sakaiproject.metaobj.shared.model.Agent;
import org.sakaiproject.metaobj.shared.model.Id;
import org.sakaiproject.metaobj.utils.mvc.intf.ListScrollIndexer;
import org.sakaiproject.metaobj.shared.mgt.IdManager;
import org.sakaiproject.tool.api.SessionManager;
import org.sakaiproject.tool.api.ToolSession;
import org.springframework.validation.Errors;
import org.springframework.web.servlet.ModelAndView;
import org.sakaiproject.site.api.Site;
import org.sakaiproject.site.api.SiteService;
import org.sakaiproject.siteassociation.api.SiteAssocManager;
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
	private LinkManager linkManager;
	private TaggingProvider matrixTaggingProvider;
	private SiteAssocManager siteAssocManager;

	public ModelAndView handleRequest(Object requestModel, Map request, Map session, Map application, Errors errors) {
		Hashtable<String, Object> model = new Hashtable<String, Object>();
		
		TaggableActivity currentActivity = getCurrentActivity();
		
		//FIXME change this to be a real permission
		boolean canLink = true;
		//boolean canLink = getAuthzManager().checkPermission(function, id);
		
		if (canLink) {
			String clickedCell = (String)request.get("page_id");
			if (clickedCell != null) {
				ScaffoldingCell sCell = getMatrixManager().getScaffoldingCell(idManager.getId(clickedCell));
				try
				{
					if (linkManager.getLink(currentActivity.getReference(), sCell.getWizardPageDefinition().getReference()) == null) {
						taggingManager.addLink(currentActivity.getReference(), sCell.getWizardPageDefinition().getReference(), null, null, true);
					}
				}
				catch (PermissionException e)
				{
					logger.error("link could not be added", e);
				}
			}
		}
		
		List<Site> sites = getAvailableSites(currentActivity);
		
		if (canLink) {
			//only do this for the instructor that has perms
			addGridsToModel(request, model, currentActivity.getReference());
		}
		else {
			//only do this for the student who does not have perms
			addLinkedCellsToModel(request, model, sites, currentActivity.getReference());
		}
		
		model.put("currentActivity", currentActivity);
		model.put("sites", sites);
		
		return new ModelAndView("success", model);
	}

	/**
	 * Adds all of the matrices and links to the model under the key "grids"
	 * @param request
	 * @param model
	 */
	private void addGridsToModel(Map request, Hashtable<String, Object> model, String activityRef) {
		Agent currentAgent = getAuthManager().getAgent();
		
		String selectedSite = (String)request.get("selectedSite");
		
		String worksiteId = getWorksiteManager().getCurrentWorksiteId().getValue();
		if (selectedSite == null) selectedSite = worksiteId;
		
		List<Scaffolding> scaffolding = getMatrixManager().findAvailableScaffolding(selectedSite, currentAgent);
		List<MatrixGridBean> grids = getScaffoldingGrids(scaffolding, selectedSite, activityRef);
		model.put("grids", getListScrollIndexer().indexList(request, model, grids));
		model.put("selectedSite", selectedSite);
	}
	
	/**
	 * Adds all of the linked cells to the model under the key "linkedCells"
	 * @param model
	 * @param sites
	 * @param activityRef
	 */
	private void addLinkedCellsToModel(Map request, Hashtable<String, Object> model, List<Site> sites, String activityRef) {
		List<Link> links = new ArrayList<Link>();
		for (Site site : sites) {
			try
			{
				links.addAll(getLinkManager().getLinks(activityRef, false, site.getId()));
			}
			catch (PermissionException e)
			{
				logger.warn("unable to get links for activity " + activityRef + " and site " + site.getId(), e);
			}
		}
		List<Id> idList = new ArrayList<Id>();
		//Map
		for (Link link : links) {
			Reference ref = EntityManager.newReference(link.getTagCriteriaRef());
			idList.add(getIdManager().getId(ref.getId()));
		}
		List<ScaffoldingCell> sCells = getMatrixManager().getScaffoldingCells(idList);
		List<LinkableScaffoldingCell> linkedCells = new ArrayList<LinkableScaffoldingCell>();
		for (ScaffoldingCell sCell : sCells) {
			LinkableScaffoldingCell lsc = new LinkableScaffoldingCell(sCell, lookupLink(links, activityRef, sCell));
			linkedCells.add(lsc);
		}
		model.put("linkedCells", getListScrollIndexer().indexList(request, model, linkedCells));
	}
	
	/**
	 * Lookup the link to the cell and activity in the given list of links
	 * @param links
	 * @param activityRef
	 * @param sCell
	 * @return
	 */
	private Link lookupLink(List<Link> links, String activityRef, ScaffoldingCell sCell) {
		for (Link link : links) {
			if (link.getActivityRef().equals(activityRef) && link.getTagCriteriaRef().equals(sCell.getWizardPageDefinition().getReference())) {
				return link;
			}
		}
		return null;
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

	private List<MatrixGridBean> getScaffoldingGrids(List<Scaffolding> scaffoldingList, String context, String activityRef) {
		List<MatrixGridBean> beanList = new ArrayList<MatrixGridBean>(scaffoldingList.size());
		
		List<Link> links = new ArrayList<Link>();
		try
		{
			links = linkManager.getLinks(activityRef, true, context);
		}
		catch (PermissionException e)
		{
			logger.warn("no permission to get links");
		}
		
		for (Scaffolding scaffolding : scaffoldingList) {
			MatrixGridBean grid = new MatrixGridBean();

			List<Level> levels = scaffolding.getLevels();
			List<Criterion> criteria = scaffolding.getCriteria();
			List<List<LinkableScaffoldingCell>> matrixContents = new ArrayList<List<LinkableScaffoldingCell>>();
			Criterion criterion = new Criterion();
			Level level = new Level();
			List<LinkableScaffoldingCell> row = new ArrayList<LinkableScaffoldingCell>();

			Set<ScaffoldingCell> cells = getMatrixManager().getScaffoldingCells(scaffolding.getId());

			for (Iterator<Criterion> criteriaIterator = criteria.iterator(); criteriaIterator.hasNext();) {
				row = new ArrayList<LinkableScaffoldingCell>();
				criterion = (Criterion) criteriaIterator.next();
				for (Iterator<Level> levelsIterator = levels.iterator(); levelsIterator.hasNext();) {
					level = (Level) levelsIterator.next();
					ScaffoldingCell scaffoldingCell = getScaffoldingCell(cells, criterion, level);
					Link link = linkManager.lookupLink(links, scaffoldingCell.getWizardPageDefinition().getReference());
					LinkableScaffoldingCell linkableCell = new LinkableScaffoldingCell(scaffoldingCell, link);
					
					linkableCell.setLink(link);
					row.add(linkableCell);
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
	 * Find the sites that are associated to this site
	 * @param sites Pass an empty list to fill up
	 * @param siteIds Pass an empty list to fill up
	 */
	protected List<Site> getAvailableSites(TaggableActivity currentActivity) {
		String fromContext = currentActivity.getContext();

		Set<String> contexts = new HashSet<String>(getSiteAssocManager().getAssociatedFrom(fromContext));
		contexts.add(fromContext);
		List<Site> sites = new ArrayList<Site>(contexts.size());

		for (String toContext : contexts) {
			try {
				Site site = getSiteService().getSite(toContext);
				sites.add(site);
			} catch (IdUnusedException iue) {
				logger.error(iue.getMessage(), iue);
			}
		}
		return sites;
	}

	protected TaggableActivity getCurrentActivity() {
		ToolSession toolSession = getSessionManager().getCurrentToolSession();
		String activityRef = (String) toolSession.getAttribute(MatrixTaggingProvider.ACTIVITY_REF);
		TaggableActivity activity = 
			getTaggingManager().getActivity(activityRef, getMatrixTaggingProvider());
		toolSession.removeAttribute(MatrixTaggingProvider.ACTIVITY_REF);
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

	public LinkManager getLinkManager()
	{
		return linkManager;
	}

	public void setLinkManager(LinkManager linkManager)
	{
		this.linkManager = linkManager;
	}

	public void setMatrixTaggingProvider(TaggingProvider matrixTaggingProvider) {
		this.matrixTaggingProvider = matrixTaggingProvider;
	}

	public SiteAssocManager getSiteAssocManager() {
		return siteAssocManager;
	}

	public void setSiteAssocManager(SiteAssocManager siteAssocManager) {
		this.siteAssocManager = siteAssocManager;
	}
	
	public class LinkableScaffoldingCell {
		private ScaffoldingCell scaffoldingCell;
		private Link link;

		public LinkableScaffoldingCell(ScaffoldingCell scaffoldingCell, Link link) {
			this.scaffoldingCell = scaffoldingCell;
			this.link = link;
		}
		
		public ScaffoldingCell getScaffoldingCell()
		{
			return scaffoldingCell;
		}

		public void setScaffoldingCell(ScaffoldingCell scaffoldingCell)
		{
			this.scaffoldingCell = scaffoldingCell;
		}

		public Link getLink()
		{
			return link;
		}

		public void setLink(Link link)
		{
			this.link = link;
		}		
	}
}

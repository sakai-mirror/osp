/**********************************************************************************
 * $URL$
 * $Id$
 ***********************************************************************************
 *
 * Copyright (c) 2008 The Sakai Foundation.
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
package org.theospi.portfolio.matrix.jsf;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.component.api.ServerConfigurationService;
import org.sakaiproject.entity.api.Reference;
import org.sakaiproject.entity.api.EntityManager;
import org.sakaiproject.exception.IdUnusedException;
import org.sakaiproject.exception.PermissionException;
import org.sakaiproject.metaobj.security.AuthenticationManager;
import org.sakaiproject.metaobj.shared.mgt.AgentManager;
import org.sakaiproject.metaobj.shared.mgt.IdManager;
import org.sakaiproject.metaobj.shared.model.Id;
import org.sakaiproject.metaobj.worksite.mgt.WorksiteManager;
import org.sakaiproject.site.api.Site;
import org.sakaiproject.site.api.SiteService;
import org.sakaiproject.siteassociation.api.SiteAssocManager;
import org.sakaiproject.taggable.api.Link;
import org.sakaiproject.taggable.api.LinkManager;
import org.sakaiproject.taggable.api.TaggableActivity;
import org.sakaiproject.taggable.api.TaggableActivityProducer;
import org.sakaiproject.taggable.api.TaggingManager;
import org.sakaiproject.taggable.api.TaggingProvider;
import org.sakaiproject.tool.api.SessionManager;
import org.sakaiproject.tool.api.ToolManager;
import org.sakaiproject.tool.api.ToolSession;
import org.sakaiproject.util.ResourceLoader;
import org.sakaiproject.util.Validator;
import org.theospi.portfolio.matrix.MatrixManager;
import org.theospi.portfolio.matrix.control.MatrixGridBean;
import org.theospi.portfolio.matrix.model.Criterion;
import org.theospi.portfolio.matrix.model.Level;
import org.theospi.portfolio.matrix.model.Scaffolding;
import org.theospi.portfolio.matrix.model.ScaffoldingCell;
import org.theospi.portfolio.security.AuthorizationFacade;
import org.theospi.portfolio.shared.tool.HelperToolBase;
import org.theospi.portfolio.tagging.api.MatrixTaggingProvider;

public class MatrixLinkTool extends HelperToolBase
{

	protected final Log logger = LogFactory.getLog(getClass());
	//private ListScrollIndexer listScrollIndexer;
	
	private MatrixManager matrixManager;
	private AuthenticationManager authManager = null;
	private IdManager idManager = null;
	private AuthorizationFacade authzManager = null;
	private WorksiteManager worksiteManager = null;
	private AgentManager agentManager = null;

	private SiteService siteService;
	private SessionManager sessionManager;
	private TaggingManager taggingManager;
	private LinkManager linkManager;
	private TaggingProvider matrixTaggingProvider;
	private SiteAssocManager siteAssocManager;
	
	private ToolManager toolManager;
	private ServerConfigurationService serverConfigurationService;
	private EntityManager entityManager;
	
	private TaggableActivity currentActivity;
	private String selectedSiteId;
	private List<Id> addLinkList = new ArrayList<Id>();
	private List<Id> removeLinkList = new ArrayList<Id>();
	private int gridSize = 0;
	
	private static ResourceLoader myResources = new ResourceLoader("org.theospi.portfolio.matrix.bundle.Messages");
	
	public static final String PROP_ALLOW_UNLINK_OVERRIDE = "osp.cellunlink.override";
	
	/**
	 * Clear all variables that were used
	 */
	private void resetVariables() {
		currentActivity = null;
		selectedSiteId = null;
		addLinkList.clear();
		removeLinkList.clear();
	}
	
	public String processActionBack() {
		//process add list
		for (Id addId : addLinkList) {
			ScaffoldingCell sCell = getMatrixManager().getScaffoldingCell(addId);
			try
			{
				if (linkManager.getLink(getCurrentActivity().getReference(), sCell.getWizardPageDefinition().getReference()) == null) {
					taggingManager.addLink(getCurrentActivity().getReference(), sCell.getWizardPageDefinition().getReference(), null, null, true);
				}
			}
			catch (PermissionException e)
			{
				logger.error("link could not be added", e);
			}
		}
		addLinkList.clear();

		//process remove list
		for (Id removeId : removeLinkList) {
			ScaffoldingCell sCell = getMatrixManager().getScaffoldingCell(removeId);
			try
			{
				Link link = linkManager.getLink(getCurrentActivity().getReference(), sCell.getWizardPageDefinition().getReference());
				if ( link != null) {
					linkManager.removeLink(link);
				}
			}
			catch (PermissionException e)
			{
				logger.error("link could not be added", e);
			}
		}
		removeLinkList.clear();
		
		resetVariables();
		return returnToCaller();
	}
	
	public String processActionCancel() {
		resetVariables();
		return returnToCaller();
	}
	
	
	public List<MatrixGridBean> getGrids() {
		List<MatrixGridBean> grids = new ArrayList<MatrixGridBean>();
		if (selectedSiteId != null) {
			List<Scaffolding> scaffolding = getMatrixManager().findPublishedScaffolding(selectedSiteId);
			grids = getScaffoldingGrids(scaffolding, selectedSiteId, getCurrentActivity());
		}
		setGridSize(grids.size());
		return grids;
	}
	
	private List<MatrixGridBean> getScaffoldingGrids(List<Scaffolding> scaffoldingList, String context, TaggableActivity activity) {
		List<MatrixGridBean> beanList = new ArrayList<MatrixGridBean>(scaffoldingList.size());
		
		List<Link> links = new ArrayList<Link>();
		try
		{
			links = linkManager.getLinks(activity.getReference(), true, context);
		}
		catch (PermissionException e)
		{
			logger.warn("no permission to get links");
		}
		
		//Map<Id, Integer> submissionCounts = getMatrixManager().getSubmissionCountByScaffolding(scaffoldingList);
		TaggableActivityProducer producer = activity.getProducer();
		boolean hasSubmissions = producer.hasSubmissions(activity, getMatrixTaggingProvider(), true, null);
		
		boolean canOverride = getCanOverride();
		
		for (Scaffolding scaffolding : scaffoldingList) {
			MatrixGridBean grid = new MatrixGridBean();
			
			//fixme I hate to have to do this, but I was getting hibernate lazy init errors
			scaffolding = getMatrixManager().loadScaffolding(scaffolding.getId());

			List<Level> levels = scaffolding.getLevels();
			List<Criterion> criteria = scaffolding.getCriteria();
			List<List<DecoratedScaffoldingCell>> matrixContents = new ArrayList<List<DecoratedScaffoldingCell>>();

			List<DecoratedScaffoldingCell> row = new ArrayList<DecoratedScaffoldingCell>();

			Set<ScaffoldingCell> cells = getMatrixManager().getScaffoldingCells(scaffolding.getId());
			for (Criterion criterion : criteria){
				row = new ArrayList<DecoratedScaffoldingCell>();
				for (Level level : levels) {
					ScaffoldingCell scaffoldingCell = getScaffoldingCell(cells, criterion, level);
					//WizardReference reference = new WizardReference(WizardReference.REF_DEF, scaffoldingCell.getWizardPageDefinition().getId()
					//		.toString());
					Reference actRef = getEntityManager().newReference(activity.getReference());
					Reference wizRef = getEntityManager().newReference(scaffoldingCell.getWizardPageDefinition().getReference());
					Link link = linkManager.lookupLink(links, scaffoldingCell.getWizardPageDefinition().getReference());
					boolean linkDisabled = (link != null && hasSubmissions) || actRef.getId().equals(wizRef.getId());
					DecoratedScaffoldingCell linkableCell = new DecoratedScaffoldingCell(this, scaffoldingCell, link, linkDisabled, canOverride);
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
	 */
	public List<SelectItem> getAvailableSites() {
		String fromContext = getCurrentActivity().getContext();

		Set<String> contexts = new HashSet<String>(getSiteAssocManager().getAssociatedFrom(fromContext));

		List<SelectItem> sites = new ArrayList<SelectItem>(contexts.size());

		for (String toContext : contexts) {
			try {
				Site site = getSiteService().getSite(toContext);
				//sites.add(site);
				sites.add((SelectItem)createSelect(site.getId(), site.getTitle()));
			} catch (IdUnusedException iue) {
				logger.error(iue.getMessage(), iue);
			}
		}
		return sites;
	}
	
	protected TaggableActivity getCurrentActivity() {
		if (currentActivity == null) {
			ToolSession toolSession = getSessionManager().getCurrentToolSession();
			String activityRef = (String) toolSession.getAttribute(MatrixTaggingProvider.ACTIVITY_REF);
			currentActivity = getTaggingManager().getActivity(activityRef, getMatrixTaggingProvider());
			toolSession.removeAttribute(MatrixTaggingProvider.ACTIVITY_REF);
		}
		return currentActivity;
	}

	protected TaggingProvider getMatrixTaggingProvider() {
		if (matrixTaggingProvider == null) {
			matrixTaggingProvider = getTaggingManager().findProviderById(
					MatrixTaggingProvider.PROVIDER_ID);
		}
		return matrixTaggingProvider;
	}
	
	public String getViewTitle() {
		return getMessageFromBundle("matrix_links_title", new Object[] {getCurrentActivity().getTitle()});
	}
	
	public void changeSite(ValueChangeEvent vce) {
		
		String siteId = ((String)vce.getNewValue());
		
	}
	
	public String getFrameId() {
		ToolSession toolSession = getSessionManager().getCurrentToolSession();
		String placementId = toolSession.getPlacementId();
		String id = Validator.escapeJavascript(placementId);
		String script = "<script type=\"text/javascript\" language=\"JavaScript\">iframeId = '" + id + "';</script>";
		
		return script;
	}
	
	//NOTE: This method is context-aware, checking properties of the current tool
	public boolean getCanOverride() {

		//Leave override off by default if not configured
		boolean canOverride = serverConfigurationService.getBoolean(PROP_ALLOW_UNLINK_OVERRIDE, false);
		try {
			String siteWide = siteService.findTool(toolManager.getCurrentPlacement().getId())
				.getContainingPage().getContainingSite().getProperties()
				.getProperty(PROP_ALLOW_UNLINK_OVERRIDE);

			//We want to allow sites to turn free-form back on if off system-wide, or off if on by default
			//But be specific about the property values
			if ("true".equalsIgnoreCase(siteWide) || "1".equals(siteWide))
				canOverride = true;
			else if ("false".equalsIgnoreCase(siteWide) || "0".equals(siteWide))
				canOverride = false;
		}
		catch (Exception e) {
			if (logger.isDebugEnabled())
				logger.debug("Error retrieving site properties for tool placement: " + toolManager.getCurrentPlacement());
		}

		return canOverride;
	}
	
	public String getUnlinkOverrideConfirmationText() {
		TaggableActivity act = getCurrentActivity();
		
		String[] args = {act.getTypeName()};
		String text = myResources.getFormattedMessage("unlink_override_confirm", args);
		return text;
	}
	
	
	public MatrixManager getMatrixManager()
	{
		return matrixManager;
	}
	public void setMatrixManager(MatrixManager matrixManager)
	{
		this.matrixManager = matrixManager;
	}
	public AuthenticationManager getAuthManager()
	{
		return authManager;
	}
	public void setAuthManager(AuthenticationManager authManager)
	{
		this.authManager = authManager;
	}
	public IdManager getIdManager()
	{
		return idManager;
	}
	public void setIdManager(IdManager idManager)
	{
		this.idManager = idManager;
	}
	public AuthorizationFacade getAuthzManager()
	{
		return authzManager;
	}
	public void setAuthzManager(AuthorizationFacade authzManager)
	{
		this.authzManager = authzManager;
	}
	public WorksiteManager getWorksiteManager()
	{
		return worksiteManager;
	}
	public void setWorksiteManager(WorksiteManager worksiteManager)
	{
		this.worksiteManager = worksiteManager;
	}
	public AgentManager getAgentManager()
	{
		return agentManager;
	}
	public void setAgentManager(AgentManager agentManager)
	{
		this.agentManager = agentManager;
	}
	public SiteService getSiteService()
	{
		return siteService;
	}
	public void setSiteService(SiteService siteService)
	{
		this.siteService = siteService;
	}
	public SessionManager getSessionManager()
	{
		return sessionManager;
	}
	public void setSessionManager(SessionManager sessionManager)
	{
		this.sessionManager = sessionManager;
	}
	public TaggingManager getTaggingManager()
	{
		return taggingManager;
	}
	public void setTaggingManager(TaggingManager taggingManager)
	{
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
	public void setMatrixTaggingProvider(TaggingProvider matrixTaggingProvider)
	{
		this.matrixTaggingProvider = matrixTaggingProvider;
	}
	public SiteAssocManager getSiteAssocManager()
	{
		return siteAssocManager;
	}
	public void setSiteAssocManager(SiteAssocManager siteAssocManager)
	{
		this.siteAssocManager = siteAssocManager;
	}

	public ToolManager getToolManager() {
		return toolManager;
	}

	public void setToolManager(ToolManager toolManager) {
		this.toolManager = toolManager;
	}

	public ServerConfigurationService getServerConfigurationService() {
		return serverConfigurationService;
	}

	public void setServerConfigurationService(
			ServerConfigurationService serverConfigurationService) {
		this.serverConfigurationService = serverConfigurationService;
	}

	public void setEntityManager(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

	public EntityManager getEntityManager() {
		return entityManager;
	}

	public class DecoratedScaffoldingCell {
		private ScaffoldingCell scaffoldingCell;
		private Link link;
		private boolean linked = false;
		private boolean disabled = false;
		private MatrixLinkTool parentTool;
		private boolean canOverride = false;

		public DecoratedScaffoldingCell(MatrixLinkTool parentTool, ScaffoldingCell scaffoldingCell, Link link, boolean disabled, boolean canOverride) {
			this.scaffoldingCell = scaffoldingCell;
			this.link = link;
			this.linked = link != null;
			this.parentTool = parentTool;
			this.disabled = disabled;
			this.canOverride = canOverride;
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
		
		public boolean isLinked() {
			return linked;
		}
		
		public void setLinked(boolean linked) {
			this.linked = linked;
		}
		
		public void processActionClickCell(boolean linked) {
			if (linked && link == null) {
				parentTool.addLinkList.add(getScaffoldingCell().getId());				
			}
			else if (linked && link != null) {
				//make sure it's not in the list to remove
				parentTool.removeLinkList.remove(getScaffoldingCell().getId());
			}
			else if (!linked && link == null){
				//make sure it's not in the list to add
				parentTool.addLinkList.remove(getScaffoldingCell().getId());
			}
			else if (!linked && link != null){
				parentTool.removeLinkList.add(getScaffoldingCell().getId());
			}
		}

		public String processActionCellInfo() {
			ExternalContext context = FacesContext.getCurrentInstance().getExternalContext();
			ToolSession session = getSessionManager().getCurrentToolSession();

			session.setAttribute("sCell_id", scaffoldingCell.getId().getValue());

			try {
				context.redirect("osp.matrix.cell.info.helper/viewCellInformation.osp");
			}
			catch (IOException e) {
				throw new RuntimeException("Failed to redirect to helper", e);
			}
			return null;
		}
		
		public void checkBoxChanged(ValueChangeEvent vce) {
			boolean tmpLinked = ((Boolean)vce.getNewValue()).booleanValue();
			if (tmpLinked && link == null) {
				parentTool.addLinkList.add(getScaffoldingCell().getId());				
			}
			else if (tmpLinked && link != null) {
				//make sure it's not in the list to remove
				parentTool.removeLinkList.remove(getScaffoldingCell().getId());
			}
			else if (!tmpLinked && link == null){
				//make sure it's not in the list to add
				parentTool.addLinkList.remove(getScaffoldingCell().getId());
			}
			else if (!tmpLinked && link != null){
				parentTool.removeLinkList.add(getScaffoldingCell().getId());
			}
		}

		public boolean isDisabled()
		{
			return disabled;
		}

		public void setDisabled(boolean disabled)
		{
			this.disabled = disabled;
		}

		public boolean isCanOverride() {
			return canOverride;
		}

		public void setCanOverride(boolean canOverride) {
			this.canOverride = canOverride;
		}
	}

	public String getSelectedSiteId()
	{
		return selectedSiteId;
	}

	public void setSelectedSiteId(String selectedSiteId)
	{
		this.selectedSiteId = selectedSiteId;
	}

	public void setCurrentActivity(TaggableActivity currentActivity)
	{
		this.currentActivity = currentActivity;
	}
	
	public int getGridSize() {
		return gridSize;
	}

	public void setGridSize(int gridSize) {
		this.gridSize = gridSize;
	}
}

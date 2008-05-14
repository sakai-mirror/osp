package org.theospi.portfolio.tagging.impl;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.exception.IdUnusedException;
import org.sakaiproject.exception.PermissionException;
import org.sakaiproject.site.api.Site;
import org.sakaiproject.site.api.ToolConfiguration;
import org.sakaiproject.site.cover.SiteService;
import org.sakaiproject.siteassociation.api.SiteAssocManager;
import org.sakaiproject.taggable.api.Link;
import org.sakaiproject.taggable.api.LinkManager;
import org.sakaiproject.taggable.api.Tag;
import org.sakaiproject.taggable.api.TagList;
import org.sakaiproject.taggable.api.TaggableActivity;
import org.sakaiproject.taggable.api.TaggableItem;
import org.sakaiproject.taggable.api.TaggingHelperInfo;
import org.sakaiproject.taggable.api.TaggingManager;
import org.sakaiproject.util.ResourceLoader;
import org.theospi.portfolio.tagging.api.MatrixTaggingProvider;

public class MatrixTaggingProviderImpl implements MatrixTaggingProvider {

	private static final Log logger = LogFactory.getLog(MatrixTaggingProviderImpl.class);
	
	private static ResourceLoader messages = new ResourceLoader("org.theospi.portfolio.matrix.bundle.Messages");
	
	protected TaggingManager taggingManager;
	protected LinkManager linkManager;
	protected SiteAssocManager siteAssocManager;
	
	protected static final String LINK_HELPER = "osp.matrix.link";
	
	
	
	public void init() {
		logger.info("init()");

		// register as a tagging provider
		getTaggingManager().registerProvider(this);
	}
	
	public String getId() {
		return MatrixTaggingProvider.PROVIDER_ID;
	}

	public String getName() {
		return messages.getString("provider_name");
	}

	private String getPlacementId(String context) {
		String placement = null;
		try
		{
			Site site = SiteService.getSite(context);
			ToolConfiguration toolConfig = site.getToolForCommonId("osp.matrix");
			placement = toolConfig.getId();
		}
		catch (IdUnusedException e)
		{
			logger.warn("unable to get site from context: " + context, e);
		}
		
        return placement;
	}

	public boolean allowViewTags(String context) {
		// TODO Auto-generated method stub
		//CWM need something like this for instructor/student view differentiation
		return true;
	}
	
	protected boolean allowTagActivities(String activityContext) {
		// TODO Auto-generated method stub
		return true;
	}

	public TaggingHelperInfo getActivityHelperInfo(String activityRef) {
		TaggingHelperInfo helperInfo = null;
		String context = taggingManager.getContext(activityRef);
		if (allowTagActivities(context)
				&& (taggingManager.getActivity(activityRef, this) != null)) {
			Map<String, String> parameterMap = new HashMap<String, String>();
			parameterMap.put(ACTIVITY_REF, activityRef);
			String text = messages.getString("act_helper_text");
			String title = messages.getString("act_helper_title");
			helperInfo = taggingManager.createTaggingHelperInfoObject(LINK_HELPER, text, title,
					parameterMap, this, getPlacementId(context));
		}
		return helperInfo;
	}


	public TaggingHelperInfo getItemHelperInfo(String itemRef) {
		// TODO Auto-generated method stub
		return null;
	}

	public TaggingHelperInfo getItemsHelperInfo(String activityRef) {
		// TODO Auto-generated method stub
		return null;
	}

	public TagList getTags(TaggableActivity activity) {
		TagList tagList = taggingManager.createTagList();
		String activityContext = activity.getContext();
		for (String toContext : getSiteAssocManager().getAssociatedFrom(activityContext)) {
			try {
				for (Link link : linkManager.getLinks(activity
						.getReference(), true, toContext)) {
					Tag tag = taggingManager.createTag(link);
					tagList.add(tag);
				}
			} catch (PermissionException pe) {
				logger.error(pe.getMessage(), pe);
			}
		}
		return tagList;
	}

	public void removeTags(TaggableActivity activity)
			throws PermissionException {

		getTaggingManager().removeLinks(activity);
		
	}

	public void removeTags(TaggableItem item) throws PermissionException {
		// TODO Auto-generated method stub
		
	}

	public void transferCopyTags(TaggableActivity fromActivity,
			TaggableActivity toActivity) throws PermissionException {
		// TODO Auto-generated method stub
		
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

	public SiteAssocManager getSiteAssocManager()
	{
		return siteAssocManager;
	}

	public void setSiteAssocManager(SiteAssocManager siteAssocManager)
	{
		this.siteAssocManager = siteAssocManager;
	}
}
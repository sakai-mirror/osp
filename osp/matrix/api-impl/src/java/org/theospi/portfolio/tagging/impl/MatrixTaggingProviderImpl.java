package org.theospi.portfolio.tagging.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.taggable.api.Tag;
import org.sakaiproject.taggable.api.TagList;
import org.sakaiproject.taggable.api.TaggableActivity;
import org.sakaiproject.taggable.api.TaggableItem;
import org.sakaiproject.taggable.api.TaggingHelperInfo;
import org.sakaiproject.taggable.api.TaggingManager;
import org.sakaiproject.exception.PermissionException;
import org.sakaiproject.gmt.api.GmtService;
import org.sakaiproject.gmt.tagging.impl.GmtTagListImpl;
import org.sakaiproject.gmt.tagging.impl.GmtTaggingHelperInfoImpl;
import org.sakaiproject.util.ResourceLoader;
import org.theospi.portfolio.tagging.api.MatrixTaggingProvider;

public class MatrixTaggingProviderImpl implements MatrixTaggingProvider {

	private static final Log logger = LogFactory.getLog(MatrixTaggingProviderImpl.class);
	
	private static ResourceLoader messages = new ResourceLoader("org.theospi.portfolio.matrix.bundle.Messages");
	
	protected GmtService gmtService;
	protected TaggingManager taggingManager;
	
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



	public boolean allowViewTags(String context) {
		// TODO Auto-generated method stub
		return true;
	}
	
	protected boolean allowTagActivities(String activityContext) {
		// TODO Auto-generated method stub
		return true;
	}

	public TaggingHelperInfo getActivityHelperInfo(String activityRef) {
		TaggingHelperInfo helperInfo = null;
		if (allowTagActivities(taggingManager.getContext(activityRef))
				&& (taggingManager.getActivity(activityRef, this) != null)) {
			Map<String, String> parameterMap = new HashMap<String, String>();
			parameterMap.put(ACTIVITY_REF, activityRef);
			String text = messages.getString("act_helper_text");
			String title = messages.getString("act_helper_title");
			helperInfo = new GmtTaggingHelperInfoImpl(LINK_HELPER, text, title,
					parameterMap, this);
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
		// TODO Auto-generated method stub
		return new GmtTagListImpl();
		//return (TagList) new ArrayList<Tag>();
		//return null;
	}

	public void removeTags(TaggableActivity activity)
			throws PermissionException {
		// TODO Auto-generated method stub
		
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

	public GmtService getGmtService() {
		return gmtService;
	}

	public void setGmtService(GmtService gmtService) {
		this.gmtService = gmtService;
	}
	
	

}

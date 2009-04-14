/**********************************************************************************
 * $URL$
 * $Id$
 ***********************************************************************************
 *
 * Copyright (c) 2007, 2008 The Sakai Foundation
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.osedu.org/licenses/ECL-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 **********************************************************************************/

package org.theospi.portfolio.wizard.taggable.impl;

import org.sakaiproject.component.cover.ServerConfigurationService;
import org.sakaiproject.exception.IdUnusedException;
import org.sakaiproject.site.cover.SiteService;
import org.sakaiproject.taggable.api.TaggableActivity;
import org.sakaiproject.taggable.api.TaggableItem;
import org.theospi.portfolio.matrix.WizardPageHelper;
import org.theospi.portfolio.matrix.model.WizardPage;
import org.theospi.portfolio.matrix.model.WizardPageDefinition;

public class WizardItemImpl implements TaggableItem {

	TaggableActivity activity;

	WizardPage page;

	WizardReference reference;

	public WizardItemImpl(WizardPage page, TaggableActivity activity) {
		this.page = page;
		this.activity = activity;

		reference = new WizardReference(WizardReference.REF_PAGE, page.getId()
				.toString());
	}

	public Object getObject() {
		return page;
	}

	public TaggableActivity getActivity() {
		return activity;
	}

	public String getContent() {
		return "";
	}

	public String getReference() {
		return reference.toString();
	}

	public String getTitle() {
		return page.getOwner().getDisplayName() + " - "
				+ page.getPageDefinition().getTitle();
	}

	public String getUserId() {
		return page.getOwner().getId().getValue();
	}
	
	public String getItemDetailPrivateUrl(){
		return getItemDetailUrl();
	}

	public String getItemDetailUrl()
	{
		String url = null;
		try
		{
			String placement = SiteService.getSite(page.getPageDefinition().getSiteId()).getToolForCommonId("osp.matrix").getId();

			//pick one to start with
			String view = "viewCell.osp";
			if (page.getPageDefinition().getType().equals(WizardPageDefinition.WPD_WIZARD_HIER_TYPE))
				view="wizardPage.osp";
			else if (page.getPageDefinition().getType().equals(WizardPageDefinition.WPD_WIZARD_SEQ_TYPE))
				view="sequentialWizardPage.osp";

			url = ServerConfigurationService.getServerUrl() + "/portal/tool/" + 
				placement + "/osp.wizard.page.helper/" + view;

		}
		catch (IdUnusedException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return url;
	}
	
	public String getItemDetailUrlParams() {
		return "?session.readOnlyMatrix=true" +
			"&page_id=" + page.getId().getValue() + 
			"&session." + WizardPageHelper.WIZARD_PAGE + "=" +
			"&panel=Main" + 
			"&TB_iframe=true";
	}
	
	public boolean getUseDecoration() {
		return false;
	}

	public String getIconUrl()
	{
		String url = ServerConfigurationService.getServerUrl() + "/library/image/silk/wand.png";
		
		if (page.getPageDefinition().getType().equals(WizardPageDefinition.WPD_MATRIX_TYPE))
			url = ServerConfigurationService.getServerUrl() + "/library/image/silk/table.png";
		return url;
	}
	
	public boolean equals(Object obj)
	{
		if (!(obj instanceof WizardItemImpl))
			return false;
		else if (!((TaggableItem) obj).getReference().equals(this.getReference()))
			return false;
		
		return true;
	}
	
	public int hashCode()
	{
		return this.getReference().hashCode();
	}
}

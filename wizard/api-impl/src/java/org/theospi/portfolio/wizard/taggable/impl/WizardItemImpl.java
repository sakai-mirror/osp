/**********************************************************************************
 * $URL$
 * $Id$
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

package org.theospi.portfolio.wizard.taggable.impl;

import org.sakaiproject.assignment.taggable.api.TaggableActivity;
import org.sakaiproject.assignment.taggable.api.TaggableItem;
import org.theospi.portfolio.matrix.model.WizardPage;

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
}

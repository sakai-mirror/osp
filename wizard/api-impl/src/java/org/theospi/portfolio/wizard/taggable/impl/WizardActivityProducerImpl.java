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

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.assignment.taggable.api.TaggableActivity;
import org.sakaiproject.assignment.taggable.api.TaggableItem;
import org.sakaiproject.assignment.taggable.api.TaggingManager;
import org.sakaiproject.metaobj.shared.mgt.IdManager;
import org.sakaiproject.util.ResourceLoader;
import org.theospi.portfolio.matrix.MatrixManager;
import org.theospi.portfolio.matrix.model.WizardPage;
import org.theospi.portfolio.matrix.model.WizardPageDefinition;
import org.theospi.portfolio.wizard.mgt.WizardManager;
import org.theospi.portfolio.wizard.taggable.api.WizardActivityProducer;

public class WizardActivityProducerImpl implements WizardActivityProducer {

	MatrixManager matrixManager;

	WizardManager wizardManager;

	IdManager idManager;

	TaggingManager taggingManager;

	private static final Log logger = LogFactory
			.getLog(WizardActivityProducerImpl.class);

	protected static ResourceLoader messages = new ResourceLoader(
			"org.theospi.portfolio.wizard.bundle.Messages");

	public void init() {
		logger.info(this + ".init()");

		taggingManager.registerProducer(this);
	}

	public IdManager getIdManager() {
		return idManager;
	}

	public void setIdManager(IdManager idManager) {
		this.idManager = idManager;
	}

	public WizardManager getWizardManager() {
		return wizardManager;
	}

	public void setWizardManager(WizardManager wizardManager) {
		this.wizardManager = wizardManager;
	}

	public MatrixManager getMatrixManager() {
		return matrixManager;
	}

	public void setMatrixManager(MatrixManager matrixManager) {
		this.matrixManager = matrixManager;
	}

	public TaggingManager getTaggingManager() {
		return taggingManager;
	}

	public void setTaggingManager(TaggingManager taggingManager) {
		this.taggingManager = taggingManager;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * Each reference to an activity or item produced by this service must start
	 * with {@link WizardActivityProducer#REF_ROOT}.
	 */
	public boolean checkReference(String ref) {
		return (WizardReference.getReference(ref) != null ? true : false);
	}

	public List<TaggableActivity> getActivities(String context) {
		List<TaggableActivity> activities = new ArrayList<TaggableActivity>();
		for (WizardPageDefinition def : wizardManager.findWizardPageDefs(
				context, true)) {
			activities.add(getActivity(def));
		}
		return activities;
	}

	public TaggableActivity getActivity(String activityRef) {
		TaggableActivity activity = null;
		WizardReference reference = WizardReference.getReference(activityRef);
		if (reference != null) {
			WizardPageDefinition def = wizardManager.getWizardPageDefinition(
					idManager.getId(reference.getId()), true);
			activity = getActivity(def);
		}
		return activity;
	}

	public String getContext(String ref) {
		String context = null;
		WizardReference reference = WizardReference.getReference(ref);
		if (reference != null) {
			if (WizardReference.REF_DEF.equals(reference.getType())) {
				context = wizardManager.getWizardPageDefinition(
						idManager.getId(reference.getId())).getSiteId();
			} else {
				context = matrixManager.getWizardPage(
						idManager.getId(reference.getId())).getPageDefinition()
						.getSiteId();
			}
		}
		return context;
	}

	public TaggableItem getItem(String itemRef) {
		TaggableItem item = null;
		WizardReference reference = WizardReference.getReference(itemRef);
		if (reference != null) {
			WizardPage page = matrixManager.getWizardPage(idManager
					.getId(reference.getId()));
			if (page != null) {
				item = getItem(page);
			}
		}
		return item;
	}

	public String getName() {
		return messages.getString("service_name");
	}

	public String getType() {
		return WizardActivityProducer.TYPE_NAME;
	}

	public TaggableActivity getActivity(WizardPageDefinition wizardPageDef) {
		TaggableActivity activity = null;
		if (wizardPageDef != null && wizardPageDef.getId() != null) {
			activity = new WizardActivityImpl(wizardPageDef, this);
		}
		return activity;
	}

	public TaggableItem getItem(WizardPage wizardPage) {
		return new WizardItemImpl(wizardPage, getActivity(wizardPage
				.getPageDefinition()));
	}
}

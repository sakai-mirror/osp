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
import org.sakaiproject.assignment.taggable.api.TaggableActivityProducer;
import org.theospi.portfolio.matrix.model.WizardPageDefinition;

public class WizardActivityImpl implements TaggableActivity {

	TaggableActivityProducer producer;

	WizardPageDefinition def;

	WizardReference reference;

	public WizardActivityImpl(WizardPageDefinition def,
			TaggableActivityProducer producer) {
		this.def = def;
		this.producer = producer;
		reference = new WizardReference(WizardReference.REF_DEF, def.getId()
				.toString());
	}

	public Object getObject() {
		return def;
	}

	public String getContext() {
		return def.getSiteId();
	}

	public String getDescription() {
		return def.getDescription();
	}

	public TaggableActivityProducer getProducer() {
		return producer;
	}

	public String getReference() {
		return reference.toString();
	}

	public String getTitle() {
		return def.getTitle();
	}
}

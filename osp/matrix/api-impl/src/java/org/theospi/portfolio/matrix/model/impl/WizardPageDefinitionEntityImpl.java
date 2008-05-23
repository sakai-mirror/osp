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
package org.theospi.portfolio.matrix.model.impl;

import java.util.Stack;

import org.sakaiproject.entity.api.ResourceProperties;
import org.sakaiproject.taggable.api.TagList;
import org.sakaiproject.util.BaseResourcePropertiesEdit;
import org.theospi.portfolio.matrix.WizardPageDefinitionEntity;
import org.theospi.portfolio.matrix.model.WizardPageDefinition;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class WizardPageDefinitionEntityImpl implements WizardPageDefinitionEntity
{

	private WizardPageDefinition wpd;
	private String parentTitle;
	
	public WizardPageDefinitionEntityImpl() {
		
	}
	
	public WizardPageDefinitionEntityImpl(WizardPageDefinition wpd, String parentTitle)
	{
		this.wpd = wpd;
		this.parentTitle = parentTitle;
	}
	
	public ResourceProperties getProperties()
	{
		ResourceProperties rp = new BaseResourcePropertiesEdit();
		rp.addProperty(TagList.PARENT, parentTitle);
		rp.addProperty(TagList.CRITERIA, wpd.getTitle());

		return rp;
	}

	public String getReference(String rootProperty)
	{
		// TODO Auto-generated method stub
		return null;
	}

	public String getUrl()
	{
		// TODO Auto-generated method stub
		return null;
	}

	public String getUrl(String rootProperty)
	{
		// TODO Auto-generated method stub
		return null;
	}

	public Element toXml(Document doc, Stack stack)
	{
		// TODO Auto-generated method stub
		return null;
	}

	public String getId()
	{
		// TODO Auto-generated method stub
		return null;
	}

	public String getReference()
	{
		return wpd.getReference();
	}

	public WizardPageDefinition getWpd()
	{
		return wpd;
	}

	public void setWpd(WizardPageDefinition wpd)
	{
		this.wpd = wpd;
	}

	public String getParentTitle()
	{
		return parentTitle;
	}

	public void setParentTitle(String parentTitle)
	{
		this.parentTitle = parentTitle;
	}

}

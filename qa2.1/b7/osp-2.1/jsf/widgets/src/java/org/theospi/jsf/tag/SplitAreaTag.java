/**********************************************************************************
* $URL: https://source.sakaiproject.org/svn/trunk/sakai/legacy/component/src/java/org/sakaiproject/component/framework/email/TestEmailService.java $
* $Id: TestEmailService.java 3831 2005-11-14 20:17:24Z ggolden@umich.edu $
***********************************************************************************
*
* Copyright (c) 2005, 2006 The Sakai Foundation.
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
package org.theospi.jsf.tag;

import javax.faces.component.UIComponent;
import javax.faces.webapp.UIComponentTag;

import org.sakaiproject.jsf.util.TagUtil;


public class SplitAreaTag extends UIComponentTag
{
	private String direction = null;
	private String width = null;
	private String height = null;

	public String getComponentType()
	{
		return "org.theospi.SplitArea";
	}

	public String getRendererType()
	{
		return "org.theospi.SplitArea";
	}

	public String getDirection()		{	return direction;	}
	public String getWidth()			{	return width;		}
	public String getHeight()			{	return height;		}

	/**
	 * 
	 * @param component		places the attributes in the component
	 */
	protected void setProperties(UIComponent component)
	{
		super.setProperties(component);
		TagUtil.setString(component, "direction", direction);
		TagUtil.setString(component, "width", width);
		TagUtil.setString(component, "height", height);
	}

	public void setDirection(String string)			{	direction = string;		}
	public void setWidth(String string)				{	width = string;			}
	public void setHeight(String string) 			{	height = string;		}
}




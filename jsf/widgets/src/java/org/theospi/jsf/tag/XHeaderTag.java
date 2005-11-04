/**********************************************************************************
* $URL: https://source.sakaiproject.org/svn/trunk/sakai/jsf/widgets/src/java/org/sakaiproject/jsf/tag/InstructionMessageTag.java $
* $Id: XHeaderTag.java,v 1.3 2005/09/29 20:15:52 andersjb Exp $
***********************************************************************************
*
* Copyright (c) 2003, 2004 The Regents of the University of Michigan, Trustees of Indiana University,
*                  Board of Trustees of the Leland Stanford, Jr., University, and The MIT Corporation
* 
* Licensed under the Educational Community License Version 1.0 (the "License");
* By obtaining, using and/or copying this Original Work, you agree that you have read,
* understand, and will comply with the terms and conditions of the Educational Community License.
* You may obtain a copy of the License at:
* 
*      http://cvs.sakaiproject.org/licenses/license_1_0.html
* 
* THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
* INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE
* AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
* DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING 
* FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*
**********************************************************************************/

package org.theospi.jsf.tag;

import javax.faces.component.UIComponent;
import javax.faces.webapp.UIComponentTag;

import org.sakaiproject.jsf.util.TagUtil;


public class XHeaderTag extends UIComponentTag
{
	private String title = null;
	private String expandable = null;
	private String initiallyexpanded = null;
	private String italicize = null;
	private String small = null;
	private String expandclass = null;

	public String getComponentType()
	{
		return "org.theospi.XHeader";
	}

	public String getRendererType()
	{
		return "org.theospi.XHeader";
	}

	public String getTitle()			{	return title;				}
	public String getExpandable()		{	return expandable;			}
	public String getInitiallyexpanded(){	return initiallyexpanded;	}
	public String getItalicize()		{	return italicize;			}
	public String getSmall()			{	return small;				}
	public String getExpandclass()		{	return expandclass;			}

	/**
	 * 
	 * @param component		places the attributes in the component
	 */
	protected void setProperties(UIComponent component)
	{
		super.setProperties(component);
		TagUtil.setString(component, "title", title);
		TagUtil.setString(component, "expandable", expandable);
		TagUtil.setString(component, "initiallyexpanded", initiallyexpanded);
		TagUtil.setString(component, "italicize", italicize);
		TagUtil.setString(component, "small", small);
		TagUtil.setString(component, "expandclass", expandclass);
	}

	public void setTitle(String string)				{	title = string;		}
	public void setExpandable(String string)		{	expandable = string;		}
	public void setInitiallyexpanded(String string) {	initiallyexpanded = string;		}
	public void setItalicize(String string)			{	italicize = string;		}
	public void setSmall(String string)				{	small = string;		}
	public void setExpandclass(String string)		{	expandclass = string;		}
}




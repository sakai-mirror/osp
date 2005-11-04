/**********************************************************************************
* $URL: https://source.sakaiproject.org/svn/trunk/sakai/jsf/widgets/src/java/org/sakaiproject/jsf/tag/InstructionMessageTag.java $
* $Id: SplitSectionTag.java,v 1.1 2005/09/30 20:35:06 andersjb Exp $
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


public class SplitSectionTag extends UIComponentTag
{
	private String cssclass = null;
	private String valign = null;
	private String align = null;
	private String size = null;

	public String getComponentType()
	{
		return "org.theospi.SplitSection";
	}

	public String getRendererType()
	{
		return "org.theospi.SplitSection";
	}

	public String getCssclass()	{	return cssclass;	}
	public String getValign()	{	return valign;		}
	public String getAlign()	{	return align;		}
	public String getSize()		{	return size;		}

	/**
	 * 
	 * @param component		places the attributes in the component
	 */
	protected void setProperties(UIComponent component)
	{
		super.setProperties(component);
		TagUtil.setString(component, "cssclass", cssclass);
		TagUtil.setString(component, "valign", valign);
		TagUtil.setString(component, "align", align);
		TagUtil.setString(component, "size", size);
	}

	public void setCssclass(String string)			{	cssclass = string;	}
	public void setValign(String string)			{	valign = string;	}
	public void setAlign(String string)				{	align = string;		}
	public void setSize(String string)				{	size = string;		}
}




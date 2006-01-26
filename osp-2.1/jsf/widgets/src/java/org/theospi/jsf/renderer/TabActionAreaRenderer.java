
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
package org.theospi.jsf.renderer;


import java.io.IOException;
import java.util.Iterator;

import javax.faces.component.UIColumn;
import javax.faces.component.UIComponent;
import javax.faces.component.UIOutput;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.render.Renderer;

import org.sakaiproject.jsf.util.RendererUtil;
import org.theospi.jsf.util.TagUtil;
import org.theospi.jsf.util.OspxTagHelper;

/**
 * This creates a split content area.  It divides a space in two.
 * This creates a table of size width x height.  It the uses
 * the divider position to give the first cell a height or width
 * based on if the direction is vertical or horizontal, respectively.
 * <br><br>
 * This class depends on the splitdivider tag to create the actual divide
 * The second class makes the second cell.
 * 
 * @author andersjb
 *
 */

public class TabActionAreaRenderer extends Renderer
{
	public boolean supportsComponentType(UIComponent component)
	{
		return (component instanceof UIOutput);
	}

	/**
	 * This renders html for the beginning of the tag.
	 * 
	 * @param context
	 * @param component
	 * @throws IOException
	 */
	public void encodeBegin(FacesContext context, UIComponent component) throws IOException
	{
		ResponseWriter writer = context.getResponseWriter();
		
		String directionStr = (String) RendererUtil.getAttribute(context, component, "direction");
		String width = (String) RendererUtil.getAttribute(context, component, "width");
		String height = (String) RendererUtil.getAttribute(context, component, "height");

		//checks for vertical, its abbr., and the y axis
		
		writer.write("<table");
		TagUtil.writeAttr(writer, "width", width);
		TagUtil.writeAttr(writer, "height", height);
		TagUtil.writeAttr(writer, "cellspacing", "0");
		TagUtil.writeAttr(writer, "cellpadding", "0");
		writer.write(">");
		
		if(!OspxTagHelper.isVertical(directionStr))
			writer.write("<tr>");
	}


	/**
	 * @param context FacesContext for the request we are processing
	 * @param component UIComponent to be rendered
	 * @exception IOException if an input/output error occurs while rendering
	 * @exception NullPointerException if <code>context</code> or <code>component</code> is null
	 */
	public void encodeEnd(FacesContext context, UIComponent component) throws IOException
	{
		String directionStr = (String) RendererUtil.getAttribute(context, component, "direction");
		ResponseWriter writer = context.getResponseWriter();

		if(!OspxTagHelper.isVertical(directionStr))
			writer.write("</tr>");
			
		writer.write("</table>");
	}
}




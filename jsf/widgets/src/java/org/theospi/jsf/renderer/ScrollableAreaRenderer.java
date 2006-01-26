
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

import javax.faces.component.UIComponent;
import javax.faces.component.UIOutput;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.render.Renderer;

import org.sakaiproject.jsf.util.RendererUtil;

import org.theospi.jsf.util.TagUtil;

/**
 * This class renders a scrollable area.  It does this by
 * making a div and then making it "auto" overflow which places
 * scroll bars at the right and/or bottom.  If the content is 
 * small enough to fit into the div then no scroll bars are rendered.
 * <br><br>
 * There must be a height defined in this tag or by a surrounding tag.
 * If there is not then there must be a parent tag that has a height defined.
 * If there is not either, then the div will resize the height to 
 * the total size of the content thus making this tag moot.
 * 
 * @author andersjb
 * 
 */
public class ScrollableAreaRenderer extends Renderer
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
		
		
		writer.write("<div ");

		String cssClass = (String) RendererUtil.getAttribute(context, component, "cssclass");
		String id = (String) RendererUtil.getAttribute(context, component, "id");
		String width = (String) RendererUtil.getAttribute(context, component, "width");
		String height = (String) RendererUtil.getAttribute(context, component, "height");

		TagUtil.writeAttr(writer, "class", cssClass);
		TagUtil.writeAttr(writer, "id", id);
		
		//	set the div tag to have scroll bars when the innerHTML is larger than the div size
		writer.write("style=\"overflow:auto;");
		
		if(width != null) {
			writer.write("width:");
			writer.write(width);
			writer.write(";");
		}
		if(height != null) {
			writer.write("height:");
			writer.write(height);
			writer.write(";");
		}
		writer.write("\">");
	}

	/**
	 * @param context FacesContext for the request we are processing
	 * @param component UIComponent to be rendered
	 * @exception IOException if an input/output error occurs while rendering
	 * @exception NullPointerException if <code>context</code> or <code>component</code> is null
	 */
	public void encodeEnd(FacesContext context, UIComponent component) throws IOException
	{
		ResponseWriter writer = context.getResponseWriter();

		writer.write("</div>");
	}
}




/**********************************************************************************
* $URL: https://source.sakaiproject.org/svn/trunk/sakai/jsf/widgets/src/java/org/sakaiproject/jsf/renderer/InstructionMessageRenderer.java $
* $Id: ScrollableAreaRenderer.java,v 1.4 2005/09/30 19:16:00 andersjb Exp $
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




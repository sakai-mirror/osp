/**********************************************************************************
* $URL: https://source.sakaiproject.org/svn/trunk/sakai/jsf/widgets/src/java/org/sakaiproject/jsf/renderer/InstructionMessageRenderer.java $
* $Id: SplitSectionRenderer.java,v 1.1 2005/09/30 19:10:04 andersjb Exp $
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
import java.util.Iterator;

import javax.faces.component.UIColumn;
import javax.faces.component.UIComponent;
import javax.faces.component.UIOutput;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.render.Renderer;

import org.sakaiproject.jsf.util.RendererUtil;
import org.theospi.jsf.component.SplitAreaComponent;
import org.theospi.jsf.util.TagUtil;
import org.theospi.jsf.util.OspxTagHelper;


public class SplitSectionRenderer extends Renderer
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
	    if (!component.isRendered())
	    {
	      return;
	    }
	    
		ResponseWriter writer = context.getResponseWriter();
		
		String cssclass = (String) RendererUtil.getAttribute(context, component, "cssclass");
		String valign = (String) RendererUtil.getAttribute(context, component, "valign");
		String align = (String) RendererUtil.getAttribute(context, component, "align");
		String size = (String) RendererUtil.getAttribute(context, component, "size");
		String id = (String) RendererUtil.getAttribute(context, component, "id");

       	UIComponent parent = component.getParent();
       	
        if (parent == null || !(parent instanceof SplitAreaComponent)) {
        	return;
        }

		String directionStr = (String) RendererUtil.getAttribute(context, parent, "direction");
		
		boolean vertical = OspxTagHelper.isVertical(directionStr);
        
		if(vertical)
			writer.write("<tr>");
		writer.write("<td");
		
		TagUtil.writeAttr(writer, "class", cssclass);
		TagUtil.writeAttr(writer, "id", id);
		TagUtil.writeAttr(writer, "align", align);
		TagUtil.writeAttr(writer, "valign", valign);
		
		if(vertical)
			TagUtil.writeAttr(writer, "height", size);
		else
			TagUtil.writeAttr(writer, "width", size);
			
		writer.write(">");

		
	}


	/**
	 * @param context FacesContext for the request we are processing
	 * @param component UIComponent to be rendered
	 * @exception IOException if an input/output error occurs while rendering
	 * @exception NullPointerException if <code>context</code> or <code>component</code> is null
	 */
	public void encodeEnd(FacesContext context, UIComponent component) throws IOException
	{
	    if (!component.isRendered())
	    {
	      return;
	    }
	    
		ResponseWriter writer = context.getResponseWriter();

		String directionStr = (String) RendererUtil.getAttribute(
										context, component.getParent(), "direction");
		
		
		writer.write("</td>");
		if(OspxTagHelper.isVertical(directionStr))
			writer.write("</tr>");
	}
}




/**********************************************************************************
* $URL: https://source.sakaiproject.org/svn/trunk/sakai/jsf/widgets/src/java/org/sakaiproject/jsf/renderer/InstructionMessageRenderer.java $
* $Id: SplitAreaRenderer.java,v 1.5 2005/10/12 15:51:45 andersjb Exp $
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
import org.theospi.jsf.component.TabComponent;
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

public class TabRenderer extends Renderer
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
		
		String title = (String) RendererUtil.getAttribute(context, component, "title");
      String directionStr = (String) RendererUtil.getAttribute(context, component.getParent(), "direction");
      String selected = (String) RendererUtil.getAttribute(context, component, "selected");
      TabComponent tab = (TabComponent) component;
      
      if (selected.equalsIgnoreCase("true"))
         tab.setSelected("true");
		
		//checks for vertical, its abbr., and the y axis
		if(!OspxTagHelper.isVertical(directionStr))
			writer.write("<td>");
      
      writer.write("<div class=\"osp_tab\">");
      
      writer.write(title);
      writer.write("<div>");
	}


	/**
	 * @param context FacesContext for the request we are processing
	 * @param component UIComponent to be rendered
	 * @exception IOException if an input/output error occurs while rendering
	 * @exception NullPointerException if <code>context</code> or <code>component</code> is null
	 */
	public void encodeEnd(FacesContext context, UIComponent component) throws IOException
	{
		String directionStr = (String) RendererUtil.getAttribute(context, component.getParent(), "direction");
		ResponseWriter writer = context.getResponseWriter();

		if(!OspxTagHelper.isVertical(directionStr))
			writer.write("</td>");
			
	}

   public void encodeChildren(FacesContext context, UIComponent component) throws IOException {
      //Do nothing
   }

   public boolean getRendersChildren() {
      //Set to false so the content can be rendered in the correct place by the TabArea
      return true;
   }
   
   
}




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
import org.theospi.jsf.component.WizardStepComponent;
import org.theospi.jsf.util.ConfigurationResource;
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

public class TabAreaRenderer extends Renderer
{
   
   private static final String RESOURCE_PATH;
   private static final String CURSOR;
   private static final String CSS_LOC;

   static {
     ConfigurationResource cr = new ConfigurationResource();
     RESOURCE_PATH = "/" + cr.get("resources");
     CURSOR = cr.get("picker_style");
     CSS_LOC = RESOURCE_PATH + "/" + cr.get("cssFile");
   }
   
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
		String height = (String) RendererUtil.getAttribute(context, component, "height");
		String width = (String) RendererUtil.getAttribute(context, component, "width");
		
		//checks for vertical, its abbr., and the y axis
		
        TagUtil.writeExternalCSSDependencies(context, writer, "osp.jsf.css", CSS_LOC);
		writer.write("<table border=\"0\" ");
		TagUtil.writeAttr(writer, "height", height);
		TagUtil.writeAttr(writer, "width", width);
		
		//the tab cell needs to be small, it will be expanded
		writer.write("><tr><td width=\"1%\">");
		
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

		writer.write("</td>");
      if(OspxTagHelper.isVertical(directionStr)) {
		 writer.write("<td width=\"*\">");
         encodeTabContent(context, component);
         writer.write("</td>");
      }
      else {
         writer.write("</tr><tr><td>");
         encodeTabContent(context, component);
         writer.write("</td>");
      }
      
		writer.write("</tr></table>");
      
	}
   
   protected void encodeTabContent(FacesContext context, UIComponent component) throws IOException {
      Iterator iter = component.getChildren().iterator();
      
      while (iter.hasNext())
      {
        UIComponent step = (UIComponent) iter.next();
        if (!(step instanceof TabComponent) || !step.isRendered())
        {
          continue;
        }
        TabComponent tab = (TabComponent) step;
        if (tab.getSelected().equalsIgnoreCase("true")) {
           for (Iterator i = tab.getChildren().iterator(); i.hasNext();) {
              UIComponent tab_content = (UIComponent) i.next();
              RendererUtil.encodeRecursive(context, tab_content);
           }
        }
      }  
   }

   public boolean getRendersChildren() {
      return false;
   }  
   
   
}




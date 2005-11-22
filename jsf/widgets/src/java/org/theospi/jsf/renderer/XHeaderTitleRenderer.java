/**********************************************************************************
* $URL: https://source.sakaiproject.org/svn/trunk/sakai/jsf/widgets/src/java/org/sakaiproject/jsf/renderer/InstructionMessageRenderer.java $
* $Id: XHeaderTitleRenderer.java,v 1.6 2005/10/13 21:13:01 chmaurer Exp $
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
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.render.Renderer;
import javax.servlet.http.HttpServletRequest;

import org.theospi.jsf.util.ConfigurationResource;
import org.sakaiproject.jsf.util.RendererUtil;
import org.theospi.jsf.component.XHeaderComponent;
import org.theospi.jsf.component.XHeaderDrawerComponent;
import org.theospi.jsf.component.XHeaderTitleComponent;
import org.theospi.jsf.util.TagUtil;
import org.theospi.jsf.util.OspxTagHelper;


public class XHeaderTitleRenderer extends Renderer
{
   private static final String RESOURCE_PATH;
   private static final String BARIMG_RIGHT;
   private static final String BARIMG_DOWN;
   private static final String CURSOR;
   private static final String JS_LOC;
   private static final String CSS_LOC;

   static {
     ConfigurationResource cr = new ConfigurationResource();
     RESOURCE_PATH = "/" + cr.get("resources");
     BARIMG_RIGHT = RESOURCE_PATH + "/" +cr.get("xheaderRight");
     BARIMG_DOWN = RESOURCE_PATH + "/" +cr.get("xheaderDown");
     CURSOR = cr.get("picker_style");
     JS_LOC = RESOURCE_PATH + "/" + cr.get("xheaderScript");
     CSS_LOC = RESOURCE_PATH + "/" + cr.get("cssFile");
   }
   
	public boolean supportsComponentType(UIComponent component)
	{
		return (component instanceof UIOutput);
	}
   

	public void encodeBegin(FacesContext context, UIComponent inComponent) throws IOException
	{
		if(!(inComponent instanceof XHeaderTitleComponent))
			throw new IOException("the xheadertitle was expecting an xheadertitlecomponent");
		
		ResponseWriter writer = context.getResponseWriter();
      
      TagUtil.writeExternalCSSDependencies(context, writer, "osp.jsf.css", CSS_LOC);
      TagUtil.writeExternalJSDependencies(context, writer, "osp.jsf.xheader.js", JS_LOC);
      
		XHeaderTitleComponent component = (XHeaderTitleComponent)inComponent;
		
		String id = (String) RendererUtil.getAttribute(context, component, "id");
      String cssclass = (String) RendererUtil.getAttribute(context, component, "cssclass");
      String value = (String) RendererUtil.getAttribute(context, component, "value");

		if(cssclass == null)
			cssclass = "xheader";
		
      writer.write("<div");
		
		TagUtil.writeAttr(writer, "class", cssclass);
		TagUtil.writeAttr(writer, "id", id);

		writer.write(">");
      
      XHeaderComponent parent = (XHeaderComponent)component.getParent();
		//if(component.getDrawerComponent() != null) {
      XHeaderDrawerComponent drawer = parent.getDrawerComponent(); 
      if (drawer != null) {
         String divId = "div" + drawer.getClientId(context);
         drawer.setDivId(divId);
			writer.write("<span onclick=\"showHideDiv('" + divId + "', '" + RESOURCE_PATH + "');" +
               "refeshChildren" + drawer.getDivId().hashCode() + "();\">");
				
         writer.startElement("img", component);
         writer.writeAttribute("style", "position:relative; float:left; margin-right:10px; left:3px; top:2px;", "style");
         writer.writeAttribute("id", "img" + divId, "id");
         String initiallyexpandedStr = (String) RendererUtil.getAttribute(context, drawer, "initiallyexpanded");
         if (initiallyexpandedStr == null) initiallyexpandedStr = "false";
         if (OspxTagHelper.parseBoolean(initiallyexpandedStr))
            writer.writeAttribute("src", BARIMG_RIGHT, "src");
         else         
            writer.writeAttribute("src", BARIMG_DOWN, "src");
         writer.endElement("img");

         if (value != null) {
            writer.write(value);
         }

         writer.write("</span>");
		}
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
   
     /**
      * This component renders its children
      * @return true
      */
     public boolean getRendersChildren()
     {
       return false;
     }
}




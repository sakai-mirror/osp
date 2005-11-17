/**********************************************************************************
* $URL: https://source.sakaiproject.org/svn/trunk/sakai/jsf/widgets/src/java/org/sakaiproject/jsf/renderer/InstructionMessageRenderer.java $
* $Id: XHeaderDrawerRenderer.java,v 1.3 2005/10/13 17:28:14 chmaurer Exp $
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

import org.sakaiproject.jsf.util.RendererUtil;
import org.theospi.jsf.util.TagUtil;
import org.theospi.jsf.util.OspxTagHelper;
import org.theospi.jsf.component.XHeaderDrawerComponent;


public class XHeaderDrawerRenderer extends Renderer
{
	public boolean supportsComponentType(UIComponent component)
	{
		return (component instanceof UIOutput);
	}

	public void encodeBegin(FacesContext context, UIComponent inComponent) throws IOException
	{
		if(!(inComponent instanceof XHeaderDrawerComponent))
			throw new IOException("the xheaderdrawer was expecting an xheaderdrawercomponent");
		
		ResponseWriter writer = context.getResponseWriter();
		XHeaderDrawerComponent component = (XHeaderDrawerComponent)inComponent;
		
		String initiallyexpandedStr = (String) RendererUtil.getAttribute(context, component, "initiallyexpanded");
		String cssclass = (String) RendererUtil.getAttribute(context, component, "cssclass");
        
		if(initiallyexpandedStr == null)
			initiallyexpandedStr = "false";
		
		boolean initiallyexpanded = OspxTagHelper.parseBoolean(initiallyexpandedStr);
		
		writer.write("<div");
		TagUtil.writeAttr(writer, "class", cssclass);
		TagUtil.writeAttr(writer, "id", component.getDivId());
		if(!initiallyexpanded)
			TagUtil.writeAttr(writer, "style", "display:none;padding:3%");
      else
         TagUtil.writeAttr(writer, "style", "display:block;;padding:3%");
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
		ResponseWriter writer = context.getResponseWriter();

      writer.write("</div>");
	}
}




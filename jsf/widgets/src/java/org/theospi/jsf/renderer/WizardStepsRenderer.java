/**********************************************************************************
* $URL: https://source.sakaiproject.org/svn/trunk/sakai/jsf/widgets/src/java/org/sakaiproject/jsf/renderer/InstructionMessageRenderer.java $
* $Id$
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

import javax.faces.component.UIComponent;
import javax.faces.component.UIOutput;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.render.Renderer;

import org.sakaiproject.jsf.util.RendererUtil;
import org.theospi.jsf.component.WizardStepComponent;
import org.theospi.jsf.util.ConfigurationResource;
import org.theospi.jsf.util.TagUtil;


public class WizardStepsRenderer extends Renderer
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
      TagUtil.writeExternalCSSDependencies(context, writer, "osp.jsf.css", CSS_LOC);
		writer.write("<table><tr>");
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
		writer.write("</tr></table>");
	}

	/**
	 * This class renders its own children and this is the function to do just that
	 * @param context FacesContext for the request we are processing
	 * @param component UIComponent to be rendered
	 * @exception IOException if an input/output error occurs while rendering
	 */
   public void encodeChildren(FacesContext context, UIComponent component) throws IOException {
      Iterator iter = component.getChildren().iterator();
      int i=1;
      while (iter.hasNext())
      {
        UIComponent step = (UIComponent) iter.next();
        if (!(step instanceof WizardStepComponent) || !step.isRendered())
        {
          continue;
        }

        RendererUtil.setAttribute(context, step, "stepNumber", String.valueOf(i));
        RendererUtil.encodeRecursive(context, step);
        i++;
      }      
   }

	/**
	 * This class renders its own children
	 * 
	 * @param context
	 * @param component
	 * @throws IOException
	 */
   public boolean getRendersChildren()
   {
   	return true;
   }
}




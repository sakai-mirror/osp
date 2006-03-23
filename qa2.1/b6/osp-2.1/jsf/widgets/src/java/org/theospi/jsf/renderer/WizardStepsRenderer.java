
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




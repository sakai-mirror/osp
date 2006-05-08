/**********************************************************************************
* $URL$
* $Id$
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
package org.theospi.jsf.util;

import java.io.Writer;
import java.io.IOException;
import java.util.Iterator;

import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.component.UIComponent;
import javax.servlet.http.HttpServletRequest;

public class TagUtil {
	
	public static void writeAttr(Writer inWriter, String inAttr, String inAttrValue)
		throws IOException
	{
		if(inWriter == null || inAttr == null || inAttrValue == null)
			return;

		inWriter.write(" ");
		inWriter.write(inAttr);
		inWriter.write("=\"");
		inWriter.write(inAttrValue);
		inWriter.write("\" ");
	}
   
   /**
    * @param context FacesContext for the request we are processing
    * @param writer ResponseWriter to be used
    * @param key key to use to look up the value in the request
    * @param path path to the file
    * @exception IOException if an input/output error occurs while rendering
    */
   public static void writeExternalJSDependencies(FacesContext context, 
         ResponseWriter writer, String key, String path) throws IOException
   {
      HttpServletRequest req = (HttpServletRequest) context.getExternalContext().getRequest();
      String jsInclude= (String) req.getAttribute(key);
      
      if (jsInclude == null || jsInclude.length() == 0)
      {
         // include default stylesheet
         jsInclude = "<script type=\"text/javascript\" src=\"" + path + "\"></script>\n";
         req.setAttribute(key, jsInclude);
         writer.write(jsInclude);
      }
   }
   
   /**
    * @param context FacesContext for the request we are processing
    * @param writer ResponseWriter to be used
    * @param key key to use to look up the value in the request
    * @param path path to the file
    * @exception IOException if an input/output error occurs while rendering
    */
   public static void writeExternalCSSDependencies(FacesContext context, 
         ResponseWriter writer, String key, String path) throws IOException
   {
      HttpServletRequest req = (HttpServletRequest) context.getExternalContext().getRequest();
      String cssInclude = (String) req.getAttribute(key);
      
      if (cssInclude == null || cssInclude.length() == 0)
      {
         // include default stylesheet
         cssInclude = "<link href=\"" + path + "\" type=\"text/css\" rel=\"stylesheet\" media=\"all\" />\n";
         req.setAttribute(key, cssInclude);
         writer.write(cssInclude);
      }
   }

   public static void renderChildren(FacesContext facesContext, UIComponent component)
           throws IOException
   {
       if (component.getChildCount() > 0)
       {
           for (Iterator it = component.getChildren().iterator(); it.hasNext(); )
           {
               UIComponent child = (UIComponent)it.next();
               renderChild(facesContext, child);
           }
       }
   }

   public static void renderChild(FacesContext facesContext, UIComponent child)
           throws IOException
   {
       if (!child.isRendered())
       {
           return;
       }

       child.encodeBegin(facesContext);
       if (child.getRendersChildren())
       {
           child.encodeChildren(facesContext);
       }
       else
       {
           renderChildren(facesContext, child);
       }
       child.encodeEnd(facesContext);
   }

}
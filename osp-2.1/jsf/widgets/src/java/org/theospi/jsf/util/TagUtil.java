/**********************************************************************************
* $URL: https://source.sakaiproject.org/svn/trunk/sakai/jsf/widgets/src/java/org/sakaiproject/jsf/tag/InstructionMessageTag.java $
* $Id: TagUtil.java,v 1.2 2005/10/13 21:13:01 chmaurer Exp $
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

package org.theospi.jsf.util;

import java.io.Writer;
import java.io.IOException;

import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
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
}
/**********************************************************************************
* $URL$
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
package org.theospi.jsf.tag;

import org.sakaiproject.jsf.util.TagUtil;
import org.theospi.jsf.component.XmlDocumentComponent;
import org.theospi.jsf.impl.XmlDocumentHandler;
import org.xml.sax.SAXException;

import javax.faces.webapp.UIComponentTag;
import javax.faces.component.UIComponent;
import javax.faces.component.UIViewRoot;
import javax.faces.component.UIOutput;
import javax.faces.context.FacesContext;
import javax.servlet.jsp.JspException;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Dec 29, 2005
 * Time: 2:22:50 PM
 * To change this template use File | Settings | File Templates.
 */
public class XmlDocumentTag extends UIComponentTag {

   private String factory;
   private String xmlFile;

   private String var;

   public String getComponentType()
   {
      return "org.theospi.XmlDocument";
   }

   public String getRendererType()
   {
      return "org.theospi.XmlDocument";
   }

   /**
    *
    * @param component		places the attributes in the component
    */
   protected void setProperties(UIComponent component)
   {
      super.setProperties(component);
      TagUtil.setObject(component, "factory", factory);
      TagUtil.setObject(component, "xmlFile", xmlFile);
      TagUtil.setString(component, "var", var);
   }

   public String getFactory() {
      return factory;
   }

   public void setFactory(String factory) {
      this.factory = factory;
   }

   public String getXmlFile() {
      return xmlFile;
   }

   public void setXmlFile(String xmlFile) {
      this.xmlFile = xmlFile;
   }

   public String getVar() {
      return var;
   }

   public void setVar(String var) {
      this.var = var;
   }

   protected UIComponent findComponent(FacesContext context) throws JspException {
      XmlDocumentComponent docComponent = (XmlDocumentComponent) super.findComponent(context);
      if (docComponent.getXmlRootComponent() != null) {
         docComponent.getChildren().remove(docComponent.getXmlRootComponent());
      }

      UIViewRoot root = context.getViewRoot();
      UIOutput base = (UIOutput) context.getApplication().createComponent("javax.faces.Output");
      base.setId(root.createUniqueId());
      docComponent.getChildren().add(base);
      XmlDocumentHandler handler = new XmlDocumentHandler(
         context, docComponent.getFactory(), base);
      try {
         SAXParserFactory parserFactory = SAXParserFactory.newInstance();
         parserFactory.setNamespaceAware(true);
         InputStream xmlFile = docComponent.getXmlFile();
         parserFactory.newSAXParser().parse(xmlFile, handler);
         xmlFile.close();
      }
      catch (SAXException e) {
         throw new JspException(e);
      }
      catch (ParserConfigurationException e) {
         throw new JspException(e);
      }
      catch (IOException e) {
         throw new JspException(e);
      }

      docComponent.setXmlRootComponent(base);
      return docComponent;
   }

}

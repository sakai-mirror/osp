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
   private String xmlFileId;

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
      TagUtil.setString(component, "xmlFileId", xmlFileId);
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

   public String getXmlFileId() {
      return xmlFileId;
   }

   public void setXmlFileId(String xmlFileId) {
      this.xmlFileId = xmlFileId;
   }

   protected UIComponent findComponent(FacesContext context) throws JspException {
       XmlDocumentComponent docComponent = (XmlDocumentComponent) super.findComponent(context);
      if (docComponent.getXmlFile() == null){
        return docComponent;
      }
      if (docComponent.getXmlRootComponent() != null && docComponent.getOldXmlFileId() != null) {
         String lastId = docComponent.getOldXmlFileId();
         String newId = docComponent.getXmlFileId();
         if (lastId.equals(newId)) {
            return docComponent;
         }
      }

      if (docComponent.getXmlRootComponent() != null) {
         docComponent.getChildren().remove(docComponent.getXmlRootComponent());
      }

      docComponent.setOldXmlFileId(docComponent.getXmlFileId());
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

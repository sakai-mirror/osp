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
package org.theospi.jsf.impl;

import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.InputSource;
import org.theospi.jsf.intf.XmlTagFactory;
import org.theospi.jsf.intf.XmlTagHandler;
import org.theospi.jsf.intf.ComponentWrapper;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import java.io.InputStream;
import java.io.IOException;
import java.util.Stack;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Dec 29, 2005
 * Time: 1:52:58 PM
 * To change this template use File | Settings | File Templates.
 */
public class XmlDocumentHandler extends DefaultHandler {

   private XmlTagFactory factory;
   private FacesContext context;
   private UIComponent rootView;
   private Stack components = new Stack();
   private XmlTagHandler defaultHandler;

   public XmlDocumentHandler(FacesContext context, XmlTagFactory factory, UIComponent rootView) {
      this.context = context;
      this.factory = factory;
      this.rootView = rootView;
      this.defaultHandler = factory.getDefaultHandler();
   }

   public void startElement(String uri, String localName, String qName,
                            Attributes attributes) throws SAXException {
      try {
         ComponentWrapper wrapper = defaultHandler.startElement(context, getCurrentComponent(),
               uri, localName, qName, attributes);
         components.push(wrapper);
      }
      catch (IOException e) {
         throw new SAXException(e);
      }
   }

   public void characters(char ch[], int start, int length) throws SAXException {
      ComponentWrapper component = (ComponentWrapper) components.peek();
      XmlTagHandler handler = component.getHandler();
      if (handler == null) {
         handler = defaultHandler;
      }
      try {
         handler.characters(context, component,  ch,  start,  length);
      }
      catch (IOException e) {
         throw new SAXException(e);
      }
   }

   public void endElement(String uri, String localName, String qName) throws SAXException {
      ComponentWrapper component = (ComponentWrapper) components.pop();
      XmlTagHandler handler = component.getHandler();
      if (handler == null) {
         handler = defaultHandler;
      }
      try {
         handler.endElement(context, component,  uri, localName, qName);
         if (components.empty()) {
            handler.endDocument(context, component);
         }
      }
      catch (IOException e) {
         throw new SAXException(e);
      }
   }

   public InputSource resolveEntity(String publicId, String systemId) throws IOException, SAXException {
      return super.resolveEntity(publicId, systemId);
   }

   protected XmlTagHandler getCurrentHandler() {
      ComponentWrapper component = getCurrentComponent();

      if (component == null) {
         return defaultHandler;
      }
      return component.getHandler();
   }

   public ComponentWrapper getCurrentComponent() {
      ComponentWrapper component = null;
      if (!components.isEmpty()) {
         component = (ComponentWrapper) components.peek();
      }
      else {
         component = new ComponentWrapper(null, rootView, null);
      }
      return component;
   }

   public XmlTagFactory getFactory() {
      return factory;
   }

   public void setFactory(XmlTagFactory factory) {
      this.factory = factory;
   }

   public FacesContext getContext() {
      return context;
   }

   public void setContext(FacesContext context) {
      this.context = context;
   }

}

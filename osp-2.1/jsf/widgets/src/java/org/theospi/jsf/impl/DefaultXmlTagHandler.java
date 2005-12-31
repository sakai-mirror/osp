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

import org.theospi.jsf.intf.XmlTagHandler;
import org.theospi.jsf.intf.XmlTagFactory;
import org.theospi.jsf.intf.ComponentWrapper;
import org.xml.sax.Attributes;

import javax.faces.component.UINamingContainer;
import javax.faces.component.UIComponent;
import javax.faces.component.html.HtmlOutputText;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import java.io.IOException;
import java.io.StringWriter;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Dec 29, 2005
 * Time: 10:33:50 AM
 * To change this template use File | Settings | File Templates.
 */
public class DefaultXmlTagHandler implements XmlTagHandler {

   private XmlTagFactory factory;

   public DefaultXmlTagHandler(XmlTagFactory factory) {
      this.factory = factory;
   }

   public ComponentWrapper startElement(FacesContext context, ComponentWrapper parent, String uri,
                                        String localName, String qName, Attributes attributes) throws IOException {

      XmlTagHandler handler = getFactory().getHandler(uri, localName, qName);
      if (handler != null) {
         return handler.startElement(context, parent, uri, localName, qName, attributes);
      }
      else {
         StringWriter buffer = new StringWriter();
         ResponseWriter writer = context.getResponseWriter().cloneWithWriter(buffer);
         writer.startElement(qName, null);
         if (attributes != null) {
            for (int i=0;i < attributes.getLength();i++) {
               writer.writeAttribute(attributes.getQName(i),
                     attributes.getValue(i), null);
            }
         }
         writer.writeText("", null);
         createOutput(buffer, parent.getComponent());
         return new ComponentWrapper(parent, parent.getComponent(), null);
      }
   }

   public void characters(FacesContext context, ComponentWrapper current, char[] ch, int start, int length)
         throws IOException {
      if (current == null || current.getHandler() == null) {
         writeCharsToVerbatim(context, current, ch, start, length);
      }
      else {
         current.getHandler().characters(context, current, ch, start,  length);
      }
   }

   public void endElement(FacesContext context, ComponentWrapper current,
                          String uri, String localName, String qName) throws IOException {
      StringWriter buffer = new StringWriter();
      ResponseWriter writer = context.getResponseWriter().cloneWithWriter(buffer);
      writer.endElement(qName);
      createOutput(buffer, current.getComponent());
   }

   protected void writeCharsToVerbatim(FacesContext context, ComponentWrapper current,
                                       char[] ch, int start, int length) throws IOException {
      StringWriter buffer = new StringWriter();
      ResponseWriter writer = context.getResponseWriter().cloneWithWriter(buffer);
      writer.write(ch, start, length);
      createOutput(buffer, current.getComponent());
   }

   protected void createOutput(String text, UIComponent parent) {
      HtmlOutputText outputComponent = new HtmlOutputText();
      outputComponent.setEscape(false);
      outputComponent.setValue(text);
      parent.getChildren().add(outputComponent);
   }

   protected void createOutput(StringWriter buffer, UIComponent parent) {
      createOutput(buffer.getBuffer().toString(), parent);
   }

   public XmlTagFactory getFactory() {
      return factory;
   }

   public void setFactory(XmlTagFactory factory) {
      this.factory = factory;
   }

}

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
package org.theospi.jsf.renderer;

import org.theospi.jsf.component.XmlDocumentComponent;
import org.theospi.jsf.impl.XmlDocumentHandler;
import org.theospi.jsf.util.TagUtil;
import org.xml.sax.SAXException;

import javax.faces.component.UIComponent;
import javax.faces.component.UINamingContainer;
import javax.faces.component.UIComponentBase;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import javax.faces.render.Renderer;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Dec 29, 2005
 * Time: 2:28:02 PM
 * To change this template use File | Settings | File Templates.
 */
public class XmlDocumentRenderer extends Renderer {

   public boolean supportsComponentType(UIComponent component) {
      return (component instanceof XmlDocumentComponent);
   }

   public boolean getRendersChildren() {
      return true;
   }

   public void encodeChildren(FacesContext context, UIComponent component) throws IOException {
      super.encodeChildren(context, component);
      XmlDocumentComponent docComponent = (XmlDocumentComponent) component;
      UIComponent layoutRoot = docComponent.getXmlRootComponent();
      if (layoutRoot == null) {
         UIInput base = new UIInput();
         XmlDocumentHandler handler = new XmlDocumentHandler(
            context, docComponent.getFactory(), base);
         component.getChildren().add(base);
         try {
            SAXParserFactory parserFactory = SAXParserFactory.newInstance();
            parserFactory.setNamespaceAware(true);
            parserFactory.newSAXParser().parse(docComponent.getXmlFile(), handler);
         }
         catch (SAXException e) {
            throw new RuntimeException(e);
         }
         catch (ParserConfigurationException e) {
            throw new RuntimeException(e);
         }

         docComponent.setXmlRootComponent(base);
         layoutRoot = base;
      }

      TagUtil.renderChild(context, layoutRoot);
   }

}

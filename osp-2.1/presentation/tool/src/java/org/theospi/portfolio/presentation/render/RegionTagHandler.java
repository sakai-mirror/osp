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
package org.theospi.portfolio.presentation.render;

import org.theospi.jsf.impl.DefaultXmlTagHandler;
import org.theospi.jsf.intf.XmlTagFactory;
import org.theospi.jsf.intf.ComponentWrapper;
import org.theospi.jsf.intf.XmlDocumentContainer;
import org.theospi.portfolio.presentation.component.SequenceComponent;
import org.xml.sax.Attributes;

import javax.faces.context.FacesContext;
import javax.faces.component.UIInput;
import javax.faces.component.UIOutput;
import javax.faces.component.UIViewRoot;
import javax.faces.component.UIComponent;
import javax.faces.component.html.HtmlInputText;
import javax.faces.el.ValueBinding;
import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Dec 29, 2005
 * Time: 3:10:07 PM
 * To change this template use File | Settings | File Templates.
 */
public class RegionTagHandler extends LayoutPageHandlerBase {

   public RegionTagHandler(XmlTagFactory factory) {
      super(factory);
   }

   public ComponentWrapper startElement(FacesContext context, ComponentWrapper parent, String uri, String localName,
                                        String qName, Attributes attributes) throws IOException {
      UIViewRoot root = context.getViewRoot();
      UIOutput container = (UIOutput) context.getApplication().createComponent(UIOutput.COMPONENT_TYPE);
      container.setId(root.createUniqueId());

      createOutput(context, "starting region: " + attributes.getValue("id"), container);
      parent.getComponent().getChildren().add(container);

      XmlDocumentContainer parentContainer = getParentContainer(parent.getComponent());
      String mapVar = parentContainer.getVariableName();
      String regionId = attributes.getValue("id");

      if (parentContainer instanceof SequenceComponent) {
         XmlDocumentContainer parentParentContainer = getParentContainer(((UIComponent)parentContainer).getParent());
         String globalMapVar = parentParentContainer.getVariableName();

         ValueBinding vb = context.getApplication().createValueBinding("#{"+globalMapVar+ "." + regionId + "}");
         ((SequenceComponent)parentContainer).addRegion(vb);
      }

      HtmlInputText input = (HtmlInputText) context.getApplication().createComponent(HtmlInputText.COMPONENT_TYPE);
      input.setId(root.createUniqueId());
      ValueBinding vbValue = context.getApplication().createValueBinding("#{"+mapVar+ "." + regionId + ".item.value}");
      input.setValueBinding("value", vbValue);
      parent.getComponent().getChildren().add(input);

      return new ComponentWrapper(parent, container, this);
   }

   public void characters(FacesContext context, ComponentWrapper current, char[] ch, int start, int length) throws IOException {
   }

   public void endElement(FacesContext context, ComponentWrapper current, String uri, String localName, String qName) throws IOException {
      this.createOutput(context, "ending region", current.getComponent());
   }
}

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
package example.xml;

import org.theospi.jsf.impl.DefaultXmlTagHandler;
import org.theospi.jsf.intf.XmlTagFactory;
import org.theospi.jsf.intf.ComponentWrapper;
import org.xml.sax.Attributes;

import javax.faces.context.FacesContext;
import javax.faces.component.UINamingContainer;
import javax.faces.component.UIViewRoot;
import javax.faces.component.UIData;
import javax.faces.component.UIColumn;
import javax.faces.component.html.HtmlOutputLink;
import javax.faces.component.html.HtmlOutputText;
import javax.faces.component.html.HtmlCommandButton;
import javax.faces.component.html.HtmlInputText;
import javax.faces.el.ValueBinding;
import javax.faces.event.ActionEvent;
import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Dec 30, 2005
 * Time: 1:25:13 PM
 * To change this template use File | Settings | File Templates.
 */
public class SequenceTagHandler extends DefaultXmlTagHandler {

   public SequenceTagHandler(XmlTagFactory factory) {
      super(factory);
   }

   public ComponentWrapper startElement(FacesContext context, ComponentWrapper parent, String uri, String localName, String qName, Attributes attributes) throws IOException {
      UIViewRoot root = context.getViewRoot();
      UIData container = new UIData();
      parent.getComponent().getChildren().add(container);
      ValueBinding vb = context.getApplication().createValueBinding("#{testBean.subBeans}");
      container.setValueBinding("value", vb);
      container.setVar("testSubBean");
      container.setId(root.createUniqueId());
      UIColumn column = new UIColumn();
      column.setId(root.createUniqueId());
      container.getChildren().add(column);
      HtmlOutputLink testLink = new HtmlOutputLink();
      testLink.setValue("http://www.javasoft.com");
      HtmlOutputText text = new HtmlOutputText();
      text.setValue("test");
      testLink.getChildren().add(text);
      HtmlCommandButton button = new HtmlCommandButton();
      button.setId(root.createUniqueId());
      button.setActionListener(
            context.getApplication().createMethodBinding("#{testSubBean.processTestButton}",
                  new Class[]{ActionEvent.class}));
      button.setValue("test me");
      HtmlInputText input = new HtmlInputText();
      input.setValueBinding("value", context.getApplication().createValueBinding("#{testSubBean.index}"));
      input.setId(root.createUniqueId());
      column.getChildren().add(input);
      column.getChildren().add(button);
      HtmlOutputText testVerbatim = new HtmlOutputText();
      testVerbatim.setEscape(false);
      testVerbatim.setValue("<some>");
      column.getChildren().add(testVerbatim);
      column.getChildren().add(testLink);
      HtmlOutputText testVerbatim2 = new HtmlOutputText();
      testVerbatim2.setEscape(false);
      testVerbatim2.setValue("</some>");
      column.getChildren().add(testVerbatim2);
      return new ComponentWrapper(parent, column, this);
   }

   public void characters(FacesContext context, ComponentWrapper current, char[] ch, int start, int length) throws IOException {
      writeCharsToVerbatim(context, current, ch, start, length);
   }

   public void endElement(FacesContext context, ComponentWrapper current, String uri, String localName, String qName) throws IOException {
   }
}

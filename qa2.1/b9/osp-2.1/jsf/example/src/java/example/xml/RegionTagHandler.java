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
package example.xml;

import org.theospi.jsf.impl.DefaultXmlTagHandler;
import org.theospi.jsf.intf.XmlTagFactory;
import org.theospi.jsf.intf.ComponentWrapper;
import org.xml.sax.Attributes;

import javax.faces.context.FacesContext;
import javax.faces.component.UIInput;
import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Dec 29, 2005
 * Time: 3:10:07 PM
 * To change this template use File | Settings | File Templates.
 */
public class RegionTagHandler extends DefaultXmlTagHandler {

   public RegionTagHandler(XmlTagFactory factory) {
      super(factory);
   }

   public ComponentWrapper startElement(FacesContext context, ComponentWrapper parent, String uri, String localName, String qName, Attributes attributes) throws IOException {
      UIInput container = new UIInput();
      createOutput(context, "starting region: " + attributes.getValue("id"), container);
      parent.getComponent().getChildren().add(container);
      return new ComponentWrapper(parent, container, this);
   }

   public void characters(FacesContext context, ComponentWrapper current, char[] ch, int start, int length) throws IOException {
   }

   public void endElement(FacesContext context, ComponentWrapper current, String uri, String localName, String qName) throws IOException {
      this.createOutput(context, "ending region", current.getComponent());
   }
}

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
package org.theospi.jsf.intf;

import org.xml.sax.Attributes;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Dec 29, 2005
 * Time: 10:08:47 AM
 * To change this template use File | Settings | File Templates.
 */
public interface XmlTagHandler {

   public void setFactory(XmlTagFactory factory);

   public ComponentWrapper startElement(FacesContext context, ComponentWrapper parent, String uri,
                                        String localName, String qName, Attributes attributes) throws IOException;

   public void characters(FacesContext context, ComponentWrapper current,
                          char[] ch, int start, int length) throws IOException;

   public void endElement(FacesContext context, ComponentWrapper current,
                          String uri, String localName, String qName) throws IOException;

   public void endDocument(FacesContext context, ComponentWrapper current) throws IOException;

}

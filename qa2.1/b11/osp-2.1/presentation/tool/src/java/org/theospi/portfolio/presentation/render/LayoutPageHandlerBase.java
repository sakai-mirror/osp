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
package org.theospi.portfolio.presentation.render;

import org.theospi.jsf.impl.DefaultXmlTagHandler;
import org.theospi.jsf.intf.XmlTagFactory;
import org.theospi.jsf.intf.XmlDocumentContainer;

import javax.faces.component.UIComponent;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Jan 1, 2006
 * Time: 6:17:19 PM
 * To change this template use File | Settings | File Templates.
 */
public class LayoutPageHandlerBase extends DefaultXmlTagHandler {

   public LayoutPageHandlerBase(XmlTagFactory factory) {
      super(factory);
   }

   public XmlDocumentContainer getParentContainer(UIComponent current) {
      UIComponent parent = current;

      while (!(parent instanceof XmlDocumentContainer) &&
            parent != null) {
         parent = parent.getParent();
      }

      return (XmlDocumentContainer) parent;
   }

}

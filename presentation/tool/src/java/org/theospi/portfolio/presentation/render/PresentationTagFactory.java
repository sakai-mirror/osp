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

import org.theospi.jsf.impl.DefaultXmlTagFactory;
import org.theospi.jsf.impl.DefaultXmlTagHandler;
import org.theospi.jsf.intf.XmlTagHandler;
import org.sakaiproject.api.kernel.component.cover.ComponentManager;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Dec 31, 2005
 * Time: 11:15:05 AM
 * To change this template use File | Settings | File Templates.
 */
public class PresentationTagFactory extends DefaultXmlTagFactory {

   private final static String OSP_NS_URI = "http://www.osportfolio.org/OspML";
   private XmlTagHandler regionTagHandler;
   private XmlTagHandler sequenceTagHandler;
   private XmlTagHandler textTypeTagHandler;

   public XmlTagHandler getHandler(String uri, String localName, String qName) {
      if (OSP_NS_URI.equals(uri)) {
         if ("region".equals(localName)) {
            return regionTagHandler;
         }
         else if ("sequence".equals(localName)) {
            return sequenceTagHandler;
         }
         else if ("texttype".equals(localName)) {
            return textTypeTagHandler;
         }
      }
      return super.getHandler(uri, localName, qName);
   }

   public XmlTagHandler getRegionTagHandler() {
      return regionTagHandler;
   }

   public void setRegionTagHandler(XmlTagHandler regionTagHandler) {
      this.regionTagHandler = regionTagHandler;
   }

   public XmlTagHandler getSequenceTagHandler() {
      return sequenceTagHandler;
   }

   public void setSequenceTagHandler(XmlTagHandler sequenceTagHandler) {
      this.sequenceTagHandler = sequenceTagHandler;
   }

   public XmlTagHandler getTextTypeTagHandler() {
      return textTypeTagHandler;
   }

   public void setTextTypeTagHandler(XmlTagHandler textTypeTagHandler) {
      this.textTypeTagHandler = textTypeTagHandler;
   }

   public void init() {
      ComponentManager.loadComponent("org.theospi.jsf.intf.XmlTagFactory.freeFormPresentation", this);
      setDefaultHandler(new DefaultXmlTagHandler(this));
      setRegionTagHandler(new RegionTagHandler(this));
      setSequenceTagHandler(new SequenceTagHandler(this));
      setTextTypeTagHandler(new TextTypeTagHandler(this));
   }

}

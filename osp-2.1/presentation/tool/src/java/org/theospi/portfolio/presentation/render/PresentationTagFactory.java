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

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
package org.theospi.jsf.intf;

import javax.faces.component.UIComponent;
import javax.faces.component.UINamingContainer;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Dec 29, 2005
 * Time: 10:42:14 AM
 * To change this template use File | Settings | File Templates.
 */
public class ComponentWrapper {
   private UIComponent component;
   private XmlTagHandler handler;
   private ComponentWrapper parent;

   public ComponentWrapper(ComponentWrapper parent, UIComponent component, XmlTagHandler handler) {
      this.parent = parent;
      this.component = component;
      this.handler = handler;
   }

   public UIComponent getComponent() {
      return component;
   }

   public void setComponent(UIComponent component) {
      this.component = component;
   }

   public XmlTagHandler getHandler() {
      return handler;
   }

   public void setHandler(XmlTagHandler handler) {
      this.handler = handler;
   }

   public ComponentWrapper getParent() {
      return parent;
   }

   public void setParent(ComponentWrapper parent) {
      this.parent = parent;
   }
}

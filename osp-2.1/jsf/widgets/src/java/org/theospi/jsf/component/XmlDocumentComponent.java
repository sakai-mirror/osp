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
package org.theospi.jsf.component;

import org.theospi.jsf.intf.XmlTagFactory;
import org.theospi.jsf.intf.XmlDocumentContainer;

import javax.faces.component.UIOutput;
import javax.faces.component.UIComponent;
import javax.faces.component.UIComponentBase;
import javax.faces.el.ValueBinding;
import javax.faces.context.FacesContext;
import javax.faces.event.FacesEvent;
import javax.faces.event.AbortProcessingException;
import java.io.InputStream;
import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Dec 29, 2005
 * Time: 2:28:53 PM
 * To change this template use File | Settings | File Templates.
 */
public class XmlDocumentComponent extends UIComponentBase implements XmlDocumentContainer {

   private XmlTagFactory factory;
   private InputStream xmlFile;
   private UIComponent xmlRootComponent;
   private String var;

   public XmlDocumentComponent() {
      super();
      this.setRendererType("org.theospi.XmlDocument");
   }

   public String getFamily() {
      return "org.theospi.xml";
   }

   public XmlTagFactory getFactory() {
      if (factory != null) return factory;
      ValueBinding vb = getValueBinding("factory");
      factory = (XmlTagFactory) vb.getValue(getFacesContext());
      return factory;
   }

   public void setFactory(XmlTagFactory factory) {
      this.factory = factory;
   }

   public InputStream getXmlFile() {
      ValueBinding vb = getValueBinding("xmlFile");
      return (InputStream) vb.getValue(getFacesContext());
   }

   public void setXmlFile(InputStream xmlFile) {
      this.xmlFile = xmlFile;
   }

   public UIComponent getXmlRootComponent() {
      return xmlRootComponent;
   }

   public void setXmlRootComponent(UIComponent xmlRootComponent) {
      this.xmlRootComponent = xmlRootComponent;
   }

   public String getVariableName() {
      return getVar();
   }

   public String getVar() {
      return var;
   }

   public void setVar(String var) {
      this.var = var;
   }

   public void broadcast(FacesEvent event) throws AbortProcessingException {
      super.broadcast(event);
   }

   public Object saveState(FacesContext context) {
      return super.saveState(context);
   }

   public void restoreState(FacesContext context, Object state) {
      super.restoreState(context, state);
   }

   public void setTransient(boolean transientFlag) {
      super.setTransient(transientFlag);
   }
}

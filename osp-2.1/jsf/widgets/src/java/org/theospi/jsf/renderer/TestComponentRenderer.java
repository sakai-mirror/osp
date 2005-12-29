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

import org.theospi.jsf.component.TestComponent;

import javax.faces.render.Renderer;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.component.*;
import javax.faces.component.html.HtmlOutputLink;
import javax.faces.component.html.HtmlOutputText;
import javax.faces.component.html.HtmlCommandButton;
import javax.faces.component.html.HtmlInputText;
import javax.faces.event.ActionEvent;
import javax.faces.el.ValueBinding;
import java.io.IOException;
import java.util.Iterator;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Dec 28, 2005
 * Time: 11:51:38 AM
 * To change this template use File | Settings | File Templates.
 */
public class TestComponentRenderer extends Renderer {

   public void decode(FacesContext context, UIComponent component) {
      super.decode(context, component);
   }

   public void encodeBegin(FacesContext context, UIComponent component) throws IOException {
      super.encodeBegin(context, component);
      ResponseWriter writer = context.getResponseWriter();
      writer.write("<b>JDE Test</b>");
   }

   public boolean getRendersChildren() {
      return true;
   }

   public void encodeChildren(FacesContext context, UIComponent component) throws IOException {
      super.encodeChildren(context, component);
      TestComponent testComponent = (TestComponent) component;
      UIComponent layoutRoot = testComponent.getLayoutRoot();
      if (layoutRoot == null) {
         UIViewRoot root = context.getViewRoot();
         UIData container = new UIData();
         ValueBinding vb = context.getApplication().createValueBinding("#{testBean.subBeans}");
         container.setValueBinding("value", vb);
         container.setVar("testSubBean");
         container.setId(root.createUniqueId());
         UIColumn column = new UIColumn();
         column.setId(root.createUniqueId());
         container.getChildren().add(column);
         root.getChildren().add(container);
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
         column.getChildren().add(testLink);
         renderChild(context, container);
         testComponent.setLayoutRoot(container);
      }
      else {
         renderChild(context, layoutRoot);
      }
   }

   public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
      ResponseWriter writer = context.getResponseWriter();

      writer.write("<b>End JDE Test</b>");
      super.encodeEnd(context, component);
   }

   public static void renderChildren(FacesContext facesContext, UIComponent component)
           throws IOException
   {
       if (component.getChildCount() > 0)
       {
           for (Iterator it = component.getChildren().iterator(); it.hasNext(); )
           {
               UIComponent child = (UIComponent)it.next();
               renderChild(facesContext, child);
           }
       }
   }

   public static void renderChild(FacesContext facesContext, UIComponent child)
           throws IOException
   {
       if (!child.isRendered())
       {
           return;
       }

       child.encodeBegin(facesContext);
       if (child.getRendersChildren())
       {
           child.encodeChildren(facesContext);
       }
       else
       {
           renderChildren(facesContext, child);
       }
       child.encodeEnd(facesContext);
   }



}

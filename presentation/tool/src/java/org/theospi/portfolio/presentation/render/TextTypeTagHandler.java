/**********************************************************************************
* $URL:https://source.sakaiproject.org/svn/osp/trunk/presentation/tool/src/java/org/theospi/portfolio/presentation/render/TextTypeTagHandler.java $
* $Id:TextTypeTagHandler.java 9134 2006-05-08 20:28:42Z chmaurer@iupui.edu $
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

import java.io.IOException;
import java.util.ArrayList;

import javax.faces.component.UIComponent;
import javax.faces.component.UIViewRoot;
import javax.faces.component.html.HtmlInputText;
import javax.faces.context.FacesContext;
import javax.faces.el.ValueBinding;

import org.sakaiproject.jsf.component.InputRichTextComponent;
import org.theospi.jsf.intf.ComponentWrapper;
import org.theospi.jsf.intf.XmlDocumentContainer;
import org.theospi.jsf.intf.XmlTagFactory;
import org.theospi.portfolio.presentation.component.RegionComponent;
import org.theospi.portfolio.presentation.component.SequenceComponent;
import org.theospi.portfolio.presentation.model.PresentationPageRegion;
import org.theospi.portfolio.presentation.tool.DecoratedRegion;
import org.theospi.portfolio.presentation.tool.RegionMap;
import org.xml.sax.Attributes;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Jan 4, 2006
 * Time: 6:59:22 PM
 * To change this template use File | Settings | File Templates.
 */
public class TextTypeTagHandler extends LayoutPageHandlerBase {

   public TextTypeTagHandler(XmlTagFactory factory) {
      super(factory);
   }

   public ComponentWrapper startElement(FacesContext context, ComponentWrapper parent, String uri,
                                        String localName, String qName, Attributes attributes) throws IOException {
      UIViewRoot root = context.getViewRoot();
      XmlDocumentContainer parentContainer = getParentContainer(parent.getComponent());
      String mapVar = parentContainer.getVariableName();
      RegionComponent parentRegion = (RegionComponent) parent.getComponent();

      boolean richEdit = false;

      if (attributes.getValue("isRichText") != null) {
         richEdit = new Boolean(attributes.getValue("isRichText")).booleanValue();
      }

      UIComponent input;

      if (richEdit) {
         input = createRichTextRegion(context, root, mapVar, parentRegion.getRegionId(),  parent);
      }
      else {
         input = createTextRegion(context, root, mapVar, parentRegion.getRegionId(),  parent);
      }

      ValueBinding vbValue = context.getApplication().createValueBinding(
            "#{"+mapVar+ "." + parentRegion.getRegionId() + ".item.value}");
      input.setValueBinding("value", vbValue);

      boolean sequenceParent = false;
      if (parentContainer instanceof SequenceComponent) {
         sequenceParent = true;
         XmlDocumentContainer parentParentContainer = getParentContainer(((UIComponent)parentContainer).getParent());
         mapVar = parentParentContainer.getVariableName();
      }

      ValueBinding vbRegion = context.getApplication().createValueBinding(
            "#{"+mapVar+ "." + parentRegion.getRegionId() + "}");
      if (vbRegion.getValue(context) == null) {
         // need to add default region
         ValueBinding vbRegionMap = context.getApplication().createValueBinding(
               "#{"+mapVar + "}");
         RegionMap map = (RegionMap) vbRegionMap.getValue(context);
         PresentationPageRegion region = new PresentationPageRegion();
         region.setRegionId(parentRegion.getRegionId());
         region.setType(richEdit?"richtext":"text");
         region.setItems(new ArrayList());
         region.setHelpText(attributes.getValue("helpText"));
         map.getPage().getRegions().add(region);
         if (!sequenceParent) {
            region.addBlank();
         }
         DecoratedRegion decoratedRegion = new DecoratedRegion(map, region);
         map.put(parentRegion.getRegionId(), decoratedRegion);
      }
      return new ComponentWrapper(parent, parent.getComponent(), this);
   }

   public void characters(FacesContext context, ComponentWrapper current, char[] ch, int start, int length) throws IOException {
   }

   public void endElement(FacesContext context, ComponentWrapper current, String uri, String localName, String qName) throws IOException {
   }

   protected UIComponent createRichTextRegion(FacesContext context, UIViewRoot root, String mapVar,
                                   String regionId, ComponentWrapper parent) {
      InputRichTextComponent input = (InputRichTextComponent) context.getApplication().createComponent(
         "org.sakaiproject.InputRichText");
      input.setId(root.createUniqueId());
      ValueBinding attachedFiles = context.getApplication().createValueBinding("#{freeForm.attachableItems}");
      input.setValueBinding("attachedFiles", attachedFiles);
      parent.getComponent().getChildren().add(input);
      return input;
   }

   protected UIComponent createTextRegion(FacesContext context, UIViewRoot root, String mapVar,
                                   String regionId, ComponentWrapper parent) {
      HtmlInputText input = (HtmlInputText) context.getApplication().createComponent(HtmlInputText.COMPONENT_TYPE);
      input.setId(root.createUniqueId());
      parent.getComponent().getChildren().add(input);
      return input;
   }

}
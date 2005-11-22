/*
 * Copyright (c) 2005 Your Corporation. All Rights Reserved.
 */
package org.theospi.jsf.tag;

import org.sakaiproject.jsf.tag.InputRichTextTag;
import org.sakaiproject.jsf.util.TagUtil;
import org.theospi.jsf.component.LinkListInputRichTextComponent;

import javax.faces.component.UIComponent;
import javax.faces.application.Application;
import javax.faces.context.FacesContext;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Nov 21, 2005
 * Time: 10:25:52 AM
 * To change this template use File | Settings | File Templates.
 */
public class LinkListInputRichTextTag extends InputRichTextTag {

   private String attachedFiles;

   public String getRendererType() {
     return "org.theospi.LinkListInputRichText";
   }

   public String getComponentType() {
      return "org.theospi.LinkListInputRichText";
   }

   protected void setProperties(UIComponent component) {
      super.setProperties(component);

      LinkListInputRichTextComponent inputRichTextComponent = (LinkListInputRichTextComponent)component;
      Application application =  FacesContext.getCurrentInstance().getApplication();
      inputRichTextComponent.setValueBinding("attachedFiles",
            application.createValueBinding(attachedFiles));
   }

   public String getAttachedFiles() {
      return attachedFiles;
   }

   public void setAttachedFiles(String attachedFiles) {
      this.attachedFiles = attachedFiles;
   }
}

/*
 * Copyright (c) 2005 Your Corporation. All Rights Reserved.
 */
package org.theospi.jsf.component;

import org.sakaiproject.jsf.component.InputRichTextComponent;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Nov 21, 2005
 * Time: 11:00:38 AM
 * To change this template use File | Settings | File Templates.
 */
public class LinkListInputRichTextComponent extends InputRichTextComponent {

   private Object attachedFiles;

   public LinkListInputRichTextComponent() {
      super();
      this.setRendererType("org.theospi.LinkListInputRichText");
   }

   public String getFamily() {
      return "org.sakaiproject.InputRichText";
   }

   public Object getAttachedFiles() {
      return attachedFiles;
   }

   public void setAttachedFiles(Object attachedFiles) {
      this.attachedFiles = attachedFiles;
   }
}

package org.theospi.utils.mvc.impl;

import org.sakaiproject.metaobj.utils.mvc.intf.TypedPropertyEditor;
import org.sakaiproject.service.legacy.entity.Reference;
import org.sakaiproject.service.legacy.resource.cover.EntityManager;

import java.beans.PropertyEditorSupport;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Aug 8, 2005
 * Time: 11:10:20 AM
 * To change this template use File | Settings | File Templates.
 */
public class RefCustomEditor  extends PropertyEditorSupport implements TypedPropertyEditor {

   public Class getType() {
      return Reference.class;
   }

   public String getAsText() {
      Object value = getValue();
      if (value instanceof Reference && value != null) {
         return ((Reference)value).getReference();
      }
      else {
         return "";
      }
   }

   public void setAsText(String text) throws IllegalArgumentException {
      if (text == null || text.equals("")) {
         setValue(null);
      }
      else {
         setValue(EntityManager.newReference(text));
      }
   }
}

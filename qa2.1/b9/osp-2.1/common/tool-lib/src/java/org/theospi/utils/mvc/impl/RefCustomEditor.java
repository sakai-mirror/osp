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

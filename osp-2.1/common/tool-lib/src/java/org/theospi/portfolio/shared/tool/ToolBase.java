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
package org.theospi.portfolio.shared.tool;

import javax.faces.model.SelectItem;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
//import java.util.ResourceBundle;
import java.util.Locale;
import java.text.MessageFormat;

import org.sakaiproject.util.java.ResourceLoader;


/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Dec 12, 2005
 * Time: 1:30:16 PM
 * To change this template use File | Settings | File Templates.
 */
public class ToolBase {
   //private ResourceBundle toolBundle;
   private ResourceLoader toolBundle;

   public Object createSelect(Object id, String description) {
      SelectItem item = new SelectItem(id, description);
      return item;
   }

   public String getMessageFromBundle(String key, Object[] args) {
      return MessageFormat.format(getMessageFromBundle(key), args);
   }

   public FacesMessage getFacesMessageFromBundle(String key, Object[] args) {
      return new FacesMessage(getMessageFromBundle(key, args));
   }

   public String getMessageFromBundle(String key) {
      if (toolBundle == null) {
         String bundle = FacesContext.getCurrentInstance().getApplication().getMessageBundle();
         toolBundle = new ResourceLoader(bundle);
      /*   Locale requestLocale = FacesContext.getCurrentInstance().getExternalContext().getRequestLocale();
         if (requestLocale != null) {
            toolBundle = ResourceBundle.getBundle(
                  bundle, requestLocale);
         }
         else {
            toolBundle = ResourceBundle.getBundle(bundle);
         }*/
      }
      return toolBundle.getString(key);
   }
}

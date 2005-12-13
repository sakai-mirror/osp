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
package org.theospi.portfolio.shared.tool;

import javax.faces.model.SelectItem;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import java.util.ResourceBundle;
import java.util.Locale;
import java.text.MessageFormat;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Dec 12, 2005
 * Time: 1:30:16 PM
 * To change this template use File | Settings | File Templates.
 */
public class ToolBase {
   private ResourceBundle toolBundle;

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
         Locale requestLocale = FacesContext.getCurrentInstance().getExternalContext().getRequestLocale();
         String bundle = FacesContext.getCurrentInstance().getApplication().getMessageBundle();
         if (requestLocale != null) {
            toolBundle = ResourceBundle.getBundle(
                  bundle, requestLocale);
         }
         else {
            toolBundle = ResourceBundle.getBundle(bundle);
         }
      }
      return toolBundle.getString(key);
   }
}

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
package org.theospi.portfolio.presentation.model.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.theospi.portfolio.presentation.PresentationManager;
import org.theospi.portfolio.presentation.model.PresentationItem;
import org.sakaiproject.metaobj.shared.mgt.HomeFactory;
import org.sakaiproject.metaobj.shared.mgt.IdManager;
import org.sakaiproject.metaobj.utils.mvc.intf.TypedPropertyEditor;

import java.beans.PropertyEditorSupport;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: May 19, 2004
 * Time: 2:23:28 PM
 * To change this template use File | Settings | File Templates.
 */
public class PresentationItemCustomEditor extends PropertyEditorSupport implements TypedPropertyEditor {
   protected final Log logger = LogFactory.getLog(this.getClass());
   private PresentationManager presentationManager;
   private IdManager idManager = null;
   private HomeFactory homeFactory;


   public void setAsText(String text) throws IllegalArgumentException {
      if (text == null || text.length() == 0 || text.indexOf(".") == -1) {
         setValue(null);
      } else {
         String[] items = text.split(",");
         Collection presentationItems = new HashSet();
         for (int i = 0; i < items.length; i++) {
            PresentationItem item = new PresentationItem();
            String[] values = items[i].split("\\.");
            if (values.length != 2) continue;
            item.setDefinition(getPresentationManager().getPresentationItemDefinition(getIdManager().getId(values[0])));
            item.setArtifactId(getIdManager().getId(values[1]));
            presentationItems.add(item);
         }
         setValue(presentationItems);
      }
   }

   public String getAsText() {
      StringBuffer buffer = new StringBuffer();
      for (Iterator i = ((Collection) getValue()).iterator(); i.hasNext();) {
         PresentationItem item = (PresentationItem) i.next();
         buffer.append(item.getDefinition().getId().getValue() + "." +
            item.getArtifactId().getValue());
      }
      return buffer.toString();
   }

   public HomeFactory getHomeFactory() {
      return homeFactory;
   }

   public void setHomeFactory(HomeFactory homeFactory) {
      this.homeFactory = homeFactory;
   }

   public Class getType() {
      return Set.class;
   }

   public PresentationManager getPresentationManager() {
      return presentationManager;
   }

   public void setPresentationManager(PresentationManager presentationManager) {
      this.presentationManager = presentationManager;
   }

   public IdManager getIdManager() {
      return idManager;
   }

   public void setIdManager(IdManager idManager) {
      this.idManager = idManager;
   }

}

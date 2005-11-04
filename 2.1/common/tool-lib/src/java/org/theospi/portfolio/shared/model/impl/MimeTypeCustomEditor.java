/*
 * The Open Source Portfolio Initiative Software is Licensed under the Educational Community License Version 1.0:
 *
 * This Educational Community License (the "License") applies to any original work of authorship
 * (the "Original Work") whose owner (the "Licensor") has placed the following notice immediately
 * following the copyright notice for the Original Work:
 *
 * Copyright (c) 2004 Trustees of Indiana University and r-smart Corporation
 *
 * This Original Work, including software, source code, documents, or other related items, is being
 * provided by the copyright holder(s) subject to the terms of the Educational Community License.
 * By obtaining, using and/or copying this Original Work, you agree that you have read, understand,
 * and will comply with the following terms and conditions of the Educational Community License:
 *
 * Permission to use, copy, modify, merge, publish, distribute, and sublicense this Original Work and
 * its documentation, with or without modification, for any purpose, and without fee or royalty to the
 * copyright holder(s) is hereby granted, provided that you include the following on ALL copies of the
 * Original Work or portions thereof, including modifications or derivatives, that you make:
 *
 * - The full text of the Educational Community License in a location viewable to users of the
 * redistributed or derivative work.
 *
 * - Any pre-existing intellectual property disclaimers, notices, or terms and conditions.
 *
 * - Notice of any changes or modifications to the Original Work, including the date the changes were made.
 *
 * - Any modifications of the Original Work must be distributed in such a manner as to avoid any confusion
 *  with the Original Work of the copyright holders.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
 * SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 * The name and trademarks of copyright holder(s) may NOT be used in advertising or publicity pertaining
 * to the Original or Derivative Works without specific, written prior permission. Title to copyright
 * in the Original Work and any associated documentation will at all times remain with the copyright holders.
 *
 * $Header: /opt/CVS/osp2.x/common/tool-lib/src/java/org/theospi/portfolio/shared/model/impl/MimeTypeCustomEditor.java,v 1.1 2005/07/21 23:05:31 jellis Exp $
 * $Revision$
 * $Date$
 */
package org.theospi.portfolio.shared.model.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.metaobj.utils.mvc.intf.TypedPropertyEditor;
import org.theospi.portfolio.shared.model.ItemDefinitionMimeType;

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
public class MimeTypeCustomEditor extends PropertyEditorSupport implements TypedPropertyEditor {
   protected final Log logger = LogFactory.getLog(this.getClass());

   public void setAsText(String text) throws IllegalArgumentException {
      if (text == null || text.length() == 0) {
         setValue(null);
      } else {
         String[] items = text.split(",");
         Collection mimeTypes = new HashSet();
         for (int i = 0; i < items.length; i++) {
            ItemDefinitionMimeType item = new ItemDefinitionMimeType();
            String[] values = items[i].split("/");
            if (values.length < 1) return;
            item.setPrimary(values[0]);
            if (values.length == 2) {
               item.setSecondary(values[1]);
            }
            mimeTypes.add(item);
         }
         setValue(mimeTypes);
      }
   }

   public String getAsText() {
      StringBuffer buffer = new StringBuffer();
      for (Iterator i = ((Collection) getValue()).iterator(); i.hasNext();) {
         ItemDefinitionMimeType item = (ItemDefinitionMimeType) i.next();
         buffer.append(item.getPrimary());
         if (item.getSecondary() != null) {
            buffer.append("/" + item.getSecondary());
         }
      }
      return buffer.toString();
   }

   public Class getType() {
      return Set.class;
   }


}

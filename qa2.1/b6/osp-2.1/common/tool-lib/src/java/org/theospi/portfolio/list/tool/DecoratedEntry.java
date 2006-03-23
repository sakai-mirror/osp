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
 * $Header: /opt/CVS/osp/src/portfolio/org/theospi/portfolio/list/tool/DecoratedEntry.java,v 1.4 2005/08/29 18:24:53 jellis Exp $
 * $Revision$
 * $Date$
 */
package org.theospi.portfolio.list.tool;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.beanutils.PropertyUtils;
import org.theospi.portfolio.list.intf.ListService;
import org.theospi.portfolio.shared.model.OspException;

import javax.faces.context.FacesContext;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.lang.reflect.InvocationTargetException;

public class DecoratedEntry {
   protected final transient Log logger = LogFactory.getLog(getClass());

   private Object entry;
   private ListService service;
   private ListTool parent;

   public DecoratedEntry(Object entry, ListService service, ListTool parent) {
      this.entry = entry;
      this.service = service;
      this.parent = parent;
   }

   public Object getEntry() {
      return entry;
   }

   public void setEntry(Object entry) {
      this.entry = entry;
   }

   public String getEntryLink() {
      return getService().getEntryLink(getEntry());
   }

   public ListService getService() {
      return service;
   }

   public void setService(ListService service) {
      this.service = service;
   }

   public String getRedirectUrl() {
      String link = getService().getEntryLink(getEntry());

      return link;
   }

   public Map getColumnValues() {
      return new ColumnValuesMap();
   }

   public boolean isNewWindow() {
      return getService().isNewWindow(getEntry());
   }

   private class ColumnValuesMap extends HashMap {

      /**
       * Returns the value associated with the given key
       *
       * @return the value associated with the given key
       */
      public Object get(Object key) {
         int index = Integer.parseInt(key.toString());

         List current = parent.getCurrentConfig().getSelectedColumns();

         if (current.size() <= index) {
            return null;
         }

         String name = (String)current.get(index);

         try {
            return PropertyUtils.getNestedProperty(entry, name);
         } catch (IllegalAccessException e) {
            logger.error("", e);
            throw new OspException(e);
         } catch (InvocationTargetException e) {
            logger.error("", e);
            throw new OspException(e);
         } catch (NoSuchMethodException e) {
            logger.error("", e);
            throw new OspException(e);
         }
      } //-- get
   }
}

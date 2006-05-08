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
 * $Header: /opt/CVS/osp/src/portfolio/org/theospi/portfolio/list/tool/ListTool.java,v 1.5 2005/02/04 21:47:05 jellis Exp $
 * $Revision: 6699 $
 * $Date$
 */
package org.theospi.portfolio.list.tool;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.theospi.portfolio.list.intf.ListService;
import org.theospi.portfolio.list.model.ListConfig;
import org.theospi.portfolio.list.intf.ListItemUtils;
import org.theospi.portfolio.list.intf.DecoratedListItem;
import org.theospi.portfolio.shared.tool.ToolBase;

import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

public class ListTool extends ToolBase implements ListItemUtils {
   protected final transient Log logger = LogFactory.getLog(getClass());

   private ListService listService;
   private DecoratedEntry currentEntry;

   private ListConfig currentConfig;


   public ListTool() {
      logger.debug("ListTool()");
   }

    public String formatMessage(String key, Object[] args) {
        return getMessageFromBundle(key, args);
    }

    public List getEntries() {
      List entries = getListService().getList();
      List returned = new ArrayList();

      int count = 0;
      for (Iterator i=entries.iterator();i.hasNext();) {
          Object listItem = i.next();
          if (listItem instanceof DecoratedListItem)
          {
               ((DecoratedListItem)listItem).setListItemUtils(this);
          }
          returned.add(new DecoratedEntry(listItem, getListService(), this));
           count++;
         if (getCurrentConfig().getRows() > 0 &&
            count == getCurrentConfig().getRows()) {
            return returned;
         }
      }

      return returned;
   }

   public List getDisplayColumns() {
      return getListService().getCurrentDisplayColumns();
   }

   public ListService getListService() {
      return listService;
   }

   public void setListService(ListService listService) {
      this.listService = listService;
      setCurrentConfig(getListService().getCurrentConfig());
   }

   public String processActionOptions() {
      setCurrentConfig(getListService().getCurrentConfig());

      return "options";
   }

   public String processMain() {
      return "main";
   }

   public String processActionOptionsSave() {
      getListService().saveOptions(getCurrentConfig());
      return "main";
   }

   public DecoratedEntry getCurrentEntry() {
      return currentEntry;
   }

   public void setCurrentEntry(DecoratedEntry currentEntry) {
      this.currentEntry = currentEntry;
   }

   public ListConfig getCurrentConfig() {
      return currentConfig;
   }

   public void setCurrentConfig(ListConfig currentConfig) {
      this.currentConfig = currentConfig;
   }

}

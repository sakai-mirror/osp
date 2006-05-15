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
*Ê Ê Ê http://www.opensource.org/licenses/ecl1.php
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*
**********************************************************************************/

package org.theospi.portfolio.list.tool;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.theospi.portfolio.list.intf.DecoratedListItem;
import org.theospi.portfolio.list.intf.ListItemUtils;
import org.theospi.portfolio.list.intf.ListService;
import org.theospi.portfolio.list.model.ListConfig;
import org.theospi.portfolio.shared.tool.ToolBase;

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

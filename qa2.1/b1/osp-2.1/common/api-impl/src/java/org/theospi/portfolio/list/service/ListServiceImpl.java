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
 * $Header: /opt/CVS/osp/src/portfolio/org/theospi/portfolio/list/service/ListServiceImpl.java,v 1.6 2005/08/29 18:24:53 jellis Exp $
 * $Revision$
 * $Date$
 */
package org.theospi.portfolio.list.service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.api.kernel.tool.Placement;
import org.sakaiproject.api.kernel.tool.ToolManager;
import org.sakaiproject.metaobj.security.AuthenticationManager;
import org.sakaiproject.metaobj.shared.mgt.IdManager;
import org.sakaiproject.metaobj.shared.model.Agent;
import org.sakaiproject.metaobj.shared.model.Id;
import org.springframework.orm.hibernate.support.HibernateDaoSupport;
import org.theospi.portfolio.list.intf.CustomLinkListGenerator;
import org.theospi.portfolio.list.intf.ListGenerator;
import org.theospi.portfolio.list.intf.ListService;
import org.theospi.portfolio.list.model.Column;
import org.theospi.portfolio.list.model.ListConfig;

import java.util.*;

public class ListServiceImpl  extends HibernateDaoSupport implements ListService {
   protected final transient Log logger = LogFactory.getLog(getClass());
   public final static String LIST_GEN_ID_TAG = "listGenId";

   private Map listGenerators;
   private IdManager idManager;
   private AuthenticationManager authnManager;
   private ToolManager toolManager;

   public List getCurrentDisplayColumns() {
      return getCurrentGenerator().getColumns();
   }

   public String getEntryLink(Object entry) {
      if (getCurrentGenerator() instanceof CustomLinkListGenerator) {
         String uri = ((CustomLinkListGenerator)getCurrentGenerator()).getCustomLink(entry);
         if (uri != null) {
            return uri;
         }
      }

      return null;
   }

   public List getList() {
      return getCurrentGenerator().getObjects();
   }

   protected Placement getCurrentTool() {
      return getToolManager().getCurrentPlacement();
   }

   protected ListGenerator getCurrentGenerator() {
      Placement current = getCurrentTool();

      String generatorName = current.getPlacementConfig().getProperty(LIST_GEN_ID_TAG);

      if (generatorName == null) {
         generatorName = current.getTool().getMutableConfig().getProperty(LIST_GEN_ID_TAG);
      }

      return getListGenerator(generatorName);
   }

   public ListGenerator getListGenerator(String generatorName) {
      return (ListGenerator)getListGenerators().get(generatorName);
   }

   public ListConfig getCurrentConfig() {
      ListGenerator listGen = getCurrentGenerator();
      ListConfig currentConfig = loadCurrentConfig();

      if (currentConfig == null) {
         currentConfig = initConfig(listGen);
      }

      List columns = new ArrayList();
      List columnStringList = listGen.getColumns();
      List selected = currentConfig.getSelectedColumns();

      for (Iterator i=columnStringList.iterator();i.hasNext();) {
         String name = (String)i.next();
         Column column = new Column(name, selected.contains(name));
         columns.add(column);
      }

      currentConfig.setColumns(columns);
      return currentConfig;
   }

   private ListConfig initConfig(ListGenerator listGen) {
      ListConfig currentConfig = new ListConfig();
      currentConfig.setSelectedColumns(listGen.getDefaultColumns());
      currentConfig.setTitle(getCurrentTool().getTitle());
      currentConfig.setToolId(getIdManager().getId(
         getCurrentTool().getId()));
      currentConfig.setOwner(getAuthnManager().getAgent());

      return currentConfig;
   }

   public void saveOptions(ListConfig currentConfig) {
      List newSelected = new ArrayList();

      for (Iterator i = currentConfig.getColumns().iterator();i.hasNext();) {
         Column col = (Column)i.next();
         if (col.isSelected()) {
            newSelected.add(col.getName());
         }
      }

      currentConfig.setSelectedColumns(newSelected);
      getHibernateTemplate().saveOrUpdate(currentConfig);
   }

   public boolean isNewWindow(Object entry) {
      return getCurrentGenerator().isNewWindow(entry);
   }

   protected ListConfig loadCurrentConfig() {
      Agent currentAgent = getAuthnManager().getAgent();
      Id toolId = getIdManager().getId(getCurrentTool().getId());

      Collection configs =
         getHibernateTemplate().find("from ListConfig where owner_id=? and tool_id=?",
            new Object[]{currentAgent.getId().getValue(), toolId.getValue()});

      if (configs.size() >= 1) {
         return (ListConfig)configs.iterator().next();
      }
      else {
         return null;
      }
   }

   public Map getListGenerators() {
      return listGenerators;
   }

   public void setListGenerators(Map listGenerators) {
      this.listGenerators = listGenerators;
   }

   public AuthenticationManager getAuthnManager() {
      return authnManager;
   }

   public void setAuthnManager(AuthenticationManager authnManager) {
      this.authnManager = authnManager;
   }

   public IdManager getIdManager() {
      return idManager;
   }

   public void setIdManager(IdManager idManager) {
      this.idManager = idManager;
   }

   public ToolManager getToolManager() {
      return toolManager;
   }

   public void setToolManager(ToolManager toolManager) {
      this.toolManager = toolManager;
   }

}

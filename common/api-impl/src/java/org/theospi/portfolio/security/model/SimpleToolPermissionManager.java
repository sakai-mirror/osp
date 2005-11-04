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
 * $Header: /opt/CVS/osp2.x/common/api-impl/src/java/org/theospi/portfolio/security/model/SimpleToolPermissionManager.java,v 1.1.1.1 2005/07/14 15:53:42 jellis Exp $
 * $Revision$
 * $Date$
 */
package org.theospi.portfolio.security.model;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.metaobj.shared.mgt.AgentManager;
import org.sakaiproject.metaobj.shared.mgt.IdManager;
import org.sakaiproject.metaobj.shared.model.Agent;
import org.sakaiproject.metaobj.shared.model.Id;
import org.sakaiproject.service.legacy.site.ToolConfiguration;
import org.theospi.portfolio.security.mgt.PermissionManager;
import org.theospi.portfolio.security.mgt.ToolPermissionManager;
import org.theospi.portfolio.worksite.intf.ToolEventListener;
import org.theospi.portfolio.worksite.model.SiteTool;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class SimpleToolPermissionManager implements ToolEventListener, ToolPermissionManager {
   protected final transient Log logger = LogFactory.getLog(getClass());

   private Map defaultPermissions;
   private AgentManager agentManager;
   private PermissionManager permissionManager;
   private String permissionEditName;
   private IdManager idManager;
   private List functions = new ArrayList();

   /**
    * sets up the default perms for a tool.  Use's the tool id as the qualifier.
    * Assumes that if no perms exist for the tool, the perms should be set to the defaults.
    * @param toolConfig
    */
   public void toolSiteChanged(ToolConfiguration toolConfig) {
      Id toolId = getIdManager().getId(toolConfig.getId());
      PermissionsEdit edit = new PermissionsEdit();
      edit.setQualifier(toolId);
      edit.setName(getPermissionEditName());
      edit.setSiteId(toolConfig.getContainingPage().getContainingSite().getId());
      getPermissionManager().fillPermissions(edit);
      if (edit.getPermissions() == null || edit.getPermissions().size() == 0){
         createDefaultPermissions(edit.getSiteId(), toolId);
      }
   }

   public void toolRemoved(SiteTool siteTool) {
      // todo remove all authz
   }

   protected void createDefaultPermissions(String worksiteId, Id qualifier) {
      PermissionsEdit edit = setupPermissions(worksiteId, qualifier);
      edit.setName(getPermissionEditName());
      getPermissionManager().updatePermissions(edit);
   }

   protected PermissionsEdit setupPermissions(String worksiteId, Id qualifier) {

      List permissions = new ArrayList();
      PermissionsEdit edit = new PermissionsEdit();
      edit.setQualifier(qualifier);
      edit.setSiteId(worksiteId);
      for (Iterator i=getDefaultPermissions().entrySet().iterator();i.hasNext();) {
         Map.Entry entry = (Map.Entry)i.next();
         String agentName = (String)entry.getKey();
         List functions = (List)entry.getValue();
         processFunctions(permissions, agentName, functions, worksiteId);
      }

      edit.setPermissions(permissions);
      return edit;
   }

   protected void processFunctions(List permissions, String roleName, List functions, String worksiteId) {
      Agent agent = getAgentManager().getWorksiteRole(roleName, worksiteId);

      for (Iterator i=functions.iterator();i.hasNext();) {
         Permission permission = new Permission();
         permission.setAgent(agent);
         permission.setFunction((String)i.next());
         permissions.add(permission);
      }
   }

   public Map getDefaultPermissions() {
      return defaultPermissions;
   }

   public void setDefaultPermissions(Map defaultPermissions) {
      this.defaultPermissions = defaultPermissions;
   }

   public AgentManager getAgentManager() {
      return agentManager;
   }

   public void setAgentManager(AgentManager agentManager) {
      this.agentManager = agentManager;
   }

   public PermissionManager getPermissionManager() {
      return permissionManager;
   }

   public void setPermissionManager(PermissionManager permissionManager) {
      this.permissionManager = permissionManager;
   }

   public String getPermissionEditName() {
      return permissionEditName;
   }

   public void setPermissionEditName(String permissionEditName) {
      this.permissionEditName = permissionEditName;
   }

   public IdManager getIdManager() {
      return idManager;
   }

   public void setIdManager(IdManager idManager) {
      this.idManager = idManager;
   }

   public List getFunctions(PermissionsEdit edit) {
      return functions;
   }

   public List getReadOnlyQualifiers(PermissionsEdit edit) {
      return new ArrayList();
   }

   public void duplicatePermissions(ToolConfiguration fromTool, ToolConfiguration toTool) {
      getPermissionManager().duplicatePermissions(
         getIdManager().getId(fromTool.getId()),
         getIdManager().getId(toTool.getId()),
         toTool.getContainingPage().getContainingSite());
   }

   public void setFunctions(List functions) {
      this.functions = functions;
   }

}

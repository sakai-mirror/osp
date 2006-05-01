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
package org.theospi.portfolio.style.tool;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.theospi.portfolio.shared.model.Node;
import org.theospi.portfolio.style.mgt.StyleManager;
import org.sakaiproject.metaobj.security.AuthenticationManager;
import org.theospi.portfolio.security.AuthorizationFacade;
import org.sakaiproject.metaobj.shared.mgt.AgentManager;
import org.sakaiproject.metaobj.shared.mgt.IdManager;
import org.sakaiproject.metaobj.shared.model.Id;
import org.sakaiproject.metaobj.worksite.mgt.WorksiteManager;
import org.sakaiproject.metaobj.utils.mvc.intf.Controller;
import org.sakaiproject.tool.cover.ToolManager;
import org.theospi.utils.mvc.impl.servlet.AbstractFormController;

import java.util.Collection;

abstract public class AbstractStyleController extends AbstractFormController implements Controller {
   private AgentManager agentManager;
   private AuthenticationManager authManager;
   protected final Log logger = LogFactory.getLog(getClass());
   private IdManager idManager;
   private Collection mimeTypes;
   private AuthorizationFacade authzManager;
   private WorksiteManager worksiteManager;
   private StyleManager styleManager;

   
   /**
    *
    * @return true is current agent is a maintainer in the current site
    */
   protected Boolean isMaintainer(){
      return new Boolean(getAuthzManager().isAuthorized(WorksiteManager.WORKSITE_MAINTAIN,
            getIdManager().getId(ToolManager.getCurrentPlacement().getContext())));
   }

   protected Node loadNode(Id nodeId) {
      return getStyleManager().getNode(nodeId);
   }

   public WorksiteManager getWorksiteManager() {
      return worksiteManager;
   }

   public void setWorksiteManager(WorksiteManager worksiteManager) {
      this.worksiteManager = worksiteManager;
   }

   public AuthenticationManager getAuthManager() {
      return authManager;
   }

   public void setAuthManager(AuthenticationManager authManager) {
      this.authManager = authManager;
   }

   public AgentManager getAgentManager() {
      return agentManager;
   }

   public void setAgentManager(AgentManager agentManager) {
      this.agentManager = agentManager;
   }

   public IdManager getIdManager() {
      return idManager;
   }

   public void setIdManager(IdManager idManager) {
      this.idManager = idManager;
   }

   public Collection getMimeTypes() {
      return mimeTypes;
   }

   public void setMimeTypes(Collection mimeTypes) {
      this.mimeTypes = mimeTypes;
   }

   public AuthorizationFacade getAuthzManager() {
      return authzManager;
   }

   public void setAuthzManager(AuthorizationFacade authzManager) {
      this.authzManager = authzManager;
   }

   public StyleManager getStyleManager() {
      return styleManager;
   }

   public void setStyleManager(StyleManager styleManager) {
      this.styleManager = styleManager;
   }
}

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
package org.theospi.portfolio.presentation.control;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.theospi.portfolio.presentation.PresentationManager;
import org.theospi.portfolio.presentation.model.PresentationTemplate;
//import org.theospi.portfolio.repository.mgt.FileArtifactFinder;
//import org.theospi.portfolio.repository.RepositoryManager;
import org.theospi.portfolio.shared.model.Node;
import org.sakaiproject.metaobj.security.AuthenticationManager;
import org.theospi.portfolio.security.AuthorizationFacade;
import org.sakaiproject.metaobj.shared.mgt.AgentManager;
import org.sakaiproject.metaobj.shared.mgt.HomeFactory;
import org.sakaiproject.metaobj.shared.mgt.IdManager;
import org.sakaiproject.metaobj.shared.model.Agent;
import org.sakaiproject.metaobj.shared.model.Id;
import org.sakaiproject.metaobj.worksite.mgt.WorksiteManager;
import org.sakaiproject.metaobj.utils.mvc.intf.Controller;
import org.theospi.utils.mvc.impl.servlet.AbstractFormController;
import org.sakaiproject.service.framework.component.cover.ComponentManager;
import org.sakaiproject.service.framework.portal.cover.PortalService;

import java.util.Collection;
import java.util.Map;

abstract public class AbstractPresentationController extends AbstractFormController implements Controller {
   private AgentManager agentManager;
   private PresentationManager presentationManager;
   private AuthenticationManager authManager;
   private HomeFactory homeFactory;
   protected final Log logger = LogFactory.getLog(getClass());
   //private FileArtifactFinder fileArtifactFinder;
   private IdManager idManager;
   private Collection mimeTypes;
   private AuthorizationFacade authzManager;
   private WorksiteManager worksiteManager;
   private AddTemplateController addTemplateController;

   protected PresentationTemplate getActiveTemplate(Map session){
      //AddTemplateController addTemplateController = (AddTemplateController)ComponentManager.getInstance().get("addTemplateController");
      return (PresentationTemplate) session.get(getAddTemplateController().getFormAttributeName());
   }
   
   /**
    *
    * @return true is current agent is a maintainer in the current site
    */
   protected Boolean isMaintainer(){
      return new Boolean(getAuthzManager().isAuthorized(WorksiteManager.WORKSITE_MAINTAIN,
            getIdManager().getId(PortalService.getCurrentSiteId())));
   }

   protected Node loadNode(Id nodeId) {
      return getPresentationManager().getNode(nodeId);
   }

   public WorksiteManager getWorksiteManager() {
      return worksiteManager;
   }

   public void setWorksiteManager(WorksiteManager worksiteManager) {
      this.worksiteManager = worksiteManager;
   }

   public PresentationManager getPresentationManager() {
      return presentationManager;
   }

   protected Collection getHomes() {
      return getHomeFactory().getHomes().entrySet();
   }

   public void setPresentationManager(PresentationManager presentationManager) {
      this.presentationManager = presentationManager;
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

   public HomeFactory getHomeFactory() {
      return homeFactory;
   }

   public void setHomeFactory(HomeFactory homeFactory) {
      this.homeFactory = homeFactory;
   }

   //public FileArtifactFinder getFileArtifactFinder() {
   //   return fileArtifactFinder;
   //}

   //public void setFileArtifactFinder(FileArtifactFinder fileFinder) {
   //   this.fileArtifactFinder = fileFinder;
   //}

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

   public AddTemplateController getAddTemplateController() {
      return addTemplateController;
   }

   public void setAddTemplateController(AddTemplateController addTemplateController) {
      this.addTemplateController = addTemplateController;
   }
}

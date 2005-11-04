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
 * $Header: /opt/CVS/osp2.x/presentation/tool/src/java/org/theospi/portfolio/presentation/control/AbstractPresentationController.java,v 1.2 2005/08/19 21:30:54 chmaurer Exp $
 * $Revision$
 * $Date$
 */
/*
 * $Header: /opt/CVS/osp2.x/presentation/tool/src/java/org/theospi/portfolio/presentation/control/AbstractPresentationController.java,v 1.2 2005/08/19 21:30:54 chmaurer Exp $
 * $Revision$
 * $Date$
 */
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

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
package org.theospi.portfolio.matrix.control;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.api.kernel.tool.ToolManager;
import org.sakaiproject.metaobj.security.AuthenticationManager;
import org.theospi.portfolio.security.AuthorizationFacade;
import org.sakaiproject.metaobj.shared.mgt.AgentManager;
import org.sakaiproject.metaobj.shared.mgt.IdManager;
import org.sakaiproject.metaobj.shared.model.Id;
import org.sakaiproject.metaobj.utils.mvc.intf.CustomCommandController;
import org.sakaiproject.metaobj.utils.mvc.intf.FormController;
import org.sakaiproject.metaobj.utils.mvc.intf.ListScrollIndexer;
import org.sakaiproject.metaobj.utils.mvc.intf.LoadObjectController;
import org.sakaiproject.metaobj.worksite.mgt.WorksiteManager;
import org.sakaiproject.service.framework.portal.cover.PortalService;
import org.springframework.validation.Errors;
import org.springframework.web.servlet.ModelAndView;
import org.theospi.portfolio.matrix.MatrixFunctionConstants;
import org.theospi.portfolio.matrix.MatrixManager;
import org.theospi.portfolio.matrix.model.ReviewerItem;

import java.util.*;

/**
 * @author chmaurer
 */
public class ListReviewerItemController implements FormController, LoadObjectController, CustomCommandController {

   protected final Log logger = LogFactory.getLog(getClass());
   private MatrixManager matrixManager = null;
   private IdManager idManager = null;
   private AuthenticationManager authManager = null;
   private AuthorizationFacade authzManager = null;
   private WorksiteManager worksiteManager = null;
   private AgentManager agentManager = null;
   private ListScrollIndexer listScrollIndexer;
   private ToolManager toolManager;

   /* (non-Javadoc)
    * @see org.theospi.utils.mvc.intf.LoadObjectController#fillBackingObject(java.lang.Object, java.util.Map, java.util.Map, java.util.Map)
    */
   public Object fillBackingObject(Object incomingModel, Map request, Map session, Map application) throws Exception {
      List list = (List) incomingModel;
      list = matrixManager.getEvaluatableCells(authManager.getAgent(), worksiteManager.getCurrentWorksiteId());

      list = getListScrollIndexer().indexList(request, request, list);

      return list;
   }

   /* (non-Javadoc)
    * @see org.theospi.utils.mvc.intf.Controller#formBackingObject(java.util.Map, java.util.Map, java.util.Map)
    */
   public Object formBackingObject(Map request, Map session, Map application) {
      return new ArrayList();
   }

   /* (non-Javadoc)
    * @see org.theospi.utils.mvc.intf.Controller#handleRequest(java.lang.Object, java.util.Map, java.util.Map, java.util.Map, org.springframework.validation.Errors)
    */
   public ModelAndView handleRequest(Object requestModel, Map request, Map session, Map application, Errors errors) {
      Map model = new HashMap();

      Id id = idManager.getId((String) request.get("reviewItem"));
      String newStatus = (String) request.get("newStatus");

      boolean unlocking = newStatus.equals(MatrixFunctionConstants.WAITING_STATUS);

      if (id.getValue() == null) {
         return new ModelAndView("nothing");
      }

      ReviewerItem ri = matrixManager.getReviewerItem(id);

      if (ri.getStatus().equals(MatrixFunctionConstants.CHECKED_OUT_STATUS) && !unlocking) {
         // already checked out... make sure they are the reviewer
         if (ri.getReviewer() != null &&
               !ri.getReviewer().getId().equals(authManager.getAgent().getId())) {
            return new ModelAndView("nothing", "errorMessage",
                  "Review is already checked out.");
         }
      }
      else if (unlocking && !ri.getReviewer().getId().equals(authManager.getAgent().getId())) {
         authzManager.checkPermission(MatrixFunctionConstants.UNLOCK_EVAL_MATRIX,
               getIdManager().getId(PortalService.getCurrentToolId()));
      }
      else if (!unlocking) { // must be trying to check out
         authzManager.checkPermission(MatrixFunctionConstants.EVALUATE_MATRIX,
               ri.getCell().getScaffoldingCell().getId());
      }

      if (unlocking) {
         ri.setStatus(MatrixFunctionConstants.WAITING_STATUS);
         ri.setReviewer(null);
      }
      else {

         ri.setStatus(MatrixFunctionConstants.CHECKED_OUT_STATUS);

         ri.setReviewer(authManager.getAgent());

      }

      ri.setModified(new Date(System.currentTimeMillis()));
      matrixManager.store(ri);
      model.put("reviewerItem_id", ri.getId());

      if (unlocking) {
         return new ModelAndView("nothing");
      }
      else {
         return new ModelAndView("success", model);
      }
   }

   public Map referenceData(Map request, Object command, Errors errors) {
      Map model = new HashMap();
      String worksiteId = getWorksiteManager().getCurrentWorksiteId().getValue();
      model.put("worksite", getWorksiteManager().getSite(worksiteId));
      model.put("tool", getToolManager().getCurrentPlacement());
      model.put("isMaintainer", isMaintainer());
      model.put("currentUser", authManager.getAgent());

      return model;
   }

   private Boolean isMaintainer() {
      return new Boolean(getAuthzManager().isAuthorized(WorksiteManager.WORKSITE_MAINTAIN,
            getIdManager().getId(PortalService.getCurrentSiteId())));
   }

   /**
    * @return
    */
   public MatrixManager getMatrixManager() {
      return matrixManager;
   }

   /**
    * @param manager
    */
   public void setMatrixManager(MatrixManager manager) {
      matrixManager = manager;
   }

   /**
    * @return
    */
   public IdManager getIdManager() {
      return idManager;
   }

   /**
    * @param manager
    */
   public void setIdManager(IdManager manager) {
      idManager = manager;
   }

   /**
    * @return
    */
   public AuthenticationManager getAuthManager() {
      return authManager;
   }

   /**
    * @param manager
    */
   public void setAuthManager(AuthenticationManager manager) {
      authManager = manager;
   }

   /**
    * @return Returns the agentManager.
    */

   public AgentManager getAgentManager() {
      return agentManager;
   }

   /**
    * @param agentManager The agentManager to set.
    */
   public void setAgentManager(AgentManager agentManager) {
      this.agentManager = agentManager;
   }

   /**
    * @return Returns the authzManager.
    */
   public AuthorizationFacade getAuthzManager() {
      return authzManager;
   }

   /**
    * @param authzManager The authzManager to set.
    */
   public void setAuthzManager(AuthorizationFacade authzManager) {
      this.authzManager = authzManager;
   }

   /**
    * @return Returns the worksiteManager.
    */
   public WorksiteManager getWorksiteManager() {
      return worksiteManager;
   }

   /**
    * @param worksiteManager The worksiteManager to set.
    */
   public void setWorksiteManager(WorksiteManager worksiteManager) {
      this.worksiteManager = worksiteManager;
   }

   public ListScrollIndexer getListScrollIndexer() {
      return listScrollIndexer;
   }

   public void setListScrollIndexer(ListScrollIndexer listScrollIndexer) {
      this.listScrollIndexer = listScrollIndexer;
   }

   public ToolManager getToolManager() {
      return toolManager;
   }

   public void setToolManager(ToolManager toolManager) {
      this.toolManager = toolManager;
   }

}


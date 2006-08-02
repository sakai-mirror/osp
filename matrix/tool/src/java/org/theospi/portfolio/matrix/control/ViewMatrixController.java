/**********************************************************************************
* $URL:https://source.sakaiproject.org/svn/osp/trunk/matrix/tool/src/java/org/theospi/portfolio/matrix/control/ViewMatrixController.java $
* $Id:ViewMatrixController.java 9134 2006-05-08 20:28:42Z chmaurer@iupui.edu $
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.authz.api.Member;
import org.sakaiproject.exception.IdUnusedException;
import org.sakaiproject.metaobj.security.FunctionConstants;
import org.sakaiproject.metaobj.shared.model.Agent;
import org.sakaiproject.metaobj.shared.model.Id;
import org.sakaiproject.metaobj.utils.mvc.intf.FormController;
import org.sakaiproject.metaobj.utils.mvc.intf.LoadObjectController;
import org.sakaiproject.site.api.Group;
import org.sakaiproject.site.api.Site;
import org.sakaiproject.site.api.ToolConfiguration;
import org.sakaiproject.site.cover.SiteService;
import org.sakaiproject.tool.api.Placement;
import org.sakaiproject.tool.api.ToolManager;
import org.sakaiproject.tool.api.ToolSession;
import org.sakaiproject.tool.cover.SessionManager;
import org.sakaiproject.user.api.UserNotDefinedException;
import org.sakaiproject.user.cover.UserDirectoryService;
import org.springframework.validation.Errors;
import org.springframework.web.servlet.ModelAndView;
import org.theospi.portfolio.matrix.MatrixFunctionConstants;
import org.theospi.portfolio.matrix.MatrixManager;
import org.theospi.portfolio.matrix.model.Cell;
import org.theospi.portfolio.matrix.model.Criterion;
import org.theospi.portfolio.matrix.model.Level;
import org.theospi.portfolio.matrix.model.Matrix;
import org.theospi.portfolio.matrix.model.Scaffolding;
import org.theospi.portfolio.matrix.model.ScaffoldingCell;

public class ViewMatrixController extends AbstractMatrixController implements FormController, LoadObjectController {

   protected final Log logger = LogFactory.getLog(getClass());
   
   public static final String VIEW_USER = "view_user";
   
   private ToolManager toolManager;

   public Object fillBackingObject(Object incomingModel, Map request, Map session, Map application) throws Exception {

      MatrixGridBean grid = (MatrixGridBean)incomingModel;
      String strScaffoldingId = (String)request.get("scaffolding_id");
      
      if (strScaffoldingId == null) {
         Placement placement = getToolManager().getCurrentPlacement();
         strScaffoldingId = placement.getPlacementConfig().getProperty(
               MatrixManager.EXPOSED_MATRIX_KEY);
      }
      
      Id scaffoldingId = getIdManager().getId(strScaffoldingId);
      Scaffolding scaffolding = getMatrixManager().getScaffolding(scaffoldingId);

      Agent currentAgent = getAuthManager().getAgent();
      boolean createAuthz = false;

      String userRequest = (String)request.get(VIEW_USER);
      String userSession = (String)session.get(VIEW_USER);
      if (userRequest != null) {
         currentAgent = getAgentManager().getAgent(getIdManager().getId(userRequest));
         createAuthz = true;
      } else if(userSession != null) {
         userRequest = userSession;
         currentAgent = getAgentManager().getAgent(getIdManager().getId(userSession));
         // The authorize was already created by this point
      }
      session.put(VIEW_USER, userRequest);

      Matrix matrix = getMatrixManager().getMatrix(scaffoldingId, currentAgent.getId());
      if (matrix == null) {
         if (currentAgent != null && !currentAgent.equals("")) {
            //Don't create a matrix unless the scaffolding has been published 
            // and the user has permission to use a matrix.
            if (scaffolding.isPublished()) {
               matrix = getMatrixManager().createMatrix(currentAgent, scaffolding);
            }
            else {
               grid.setScaffolding(scaffolding);
               return incomingModel;
            }
         }
      }
      scaffolding = matrix.getScaffolding();
      if (createAuthz) {
         getAuthzManager().createAuthorization(getAuthManager().getAgent(), 
                 FunctionConstants.READ_MATRIX, matrix.getId());
      }

      List levels = scaffolding.getLevels();
      List criteria = scaffolding.getCriteria();
      List matrixContents = new ArrayList();
      Criterion criterion = new Criterion();
      Level level = new Level();
      List row = new ArrayList();
      
      Set cells = matrix.getCells();
       
      for (Iterator criteriaIterator = criteria.iterator(); criteriaIterator.hasNext();) {
         row = new ArrayList();
         criterion = (Criterion) criteriaIterator.next();
         for (Iterator levelsIterator = levels.iterator(); levelsIterator.hasNext();) {
            level = (Level) levelsIterator.next();
            CellFormBean cellBean = new CellFormBean();

            Cell cell = getCell(cells, criterion, level);
            if (cell == null) {
               cell = new Cell();
               cell.setMatrix(matrix);
               ScaffoldingCell scaffoldingCell = getMatrixManager().getScaffoldingCell(criterion, level);
               cell.setScaffoldingCell(scaffoldingCell);
               cell.setStatus(scaffoldingCell.getInitialStatus());
               getMatrixManager().storeCell(cell);
            }
            List nodeList = new ArrayList(getMatrixManager().getPageContents(cell.getWizardPage()));
            nodeList.addAll(getMatrixManager().getPageForms(cell.getWizardPage()));
            cellBean.setCell(cell);
            cellBean.setNodes(nodeList);
            row.add(cellBean);
         }
         matrixContents.add(row);
      }


      grid.setMatrixId(matrix.getId());
      grid.setMatrixOwner(matrix.getOwner());
      grid.setScaffolding(scaffolding);
      grid.setColumnLabels(levels);
      grid.setRowLabels(criteria);
      grid.setMatrixContents(matrixContents);

      return incomingModel;
   }

   /* (non-Javadoc)
    * @see org.theospi.utils.mvc.intf.FormController#referenceData(java.util.Map, java.lang.Object, org.springframework.validation.Errors)
    */
   public Map referenceData(Map request, Object command, Errors errors) {
      Map model = new HashMap();
      String worksiteId = getWorksiteManager().getCurrentWorksiteId().getValue();

      model.put("worksite", getWorksiteManager().getSite(worksiteId));
      model.put("tool", getToolManager().getCurrentPlacement());
      model.put("isMaintainer", isMaintainer());      

      List userList = new ArrayList(getUserList(worksiteId));
      Collections.sort(userList);
      model.put("members", userList);
      
      MatrixGridBean grid = (MatrixGridBean) command;      
      Agent owner = grid.getMatrixOwner();
      Boolean readOnly = Boolean.valueOf(false);

      if ((owner != null && !owner.equals(getAuthManager().getAgent())) ||
           !getAuthzManager().isAuthorized(MatrixFunctionConstants.USE_SCAFFOLDING,getWorksiteManager().getCurrentWorksiteId()))
         readOnly = Boolean.valueOf(true);

      model.put("matrixOwner", owner);      
      model.put("readOnlyMatrix", readOnly);
      
      if (getCurrentSitePageId().equals(
            grid.getScaffolding().getExposedPageId())) {
         model.put("isExposedPage", Boolean.valueOf(true));
      }
      
      return model;
   }
   
   /**
    * Extract the site page id from the current request.
    * 
    * @return The site page id implied from the current request.
    */
   protected String getCurrentSitePageId()
   {
      ToolSession ts = SessionManager.getCurrentToolSession();
      if (ts != null)
      {
         ToolConfiguration tool = SiteService.findTool(ts.getPlacementId());
         if (tool != null)
         {
            return tool.getPageId();
         }
      }

      return null;

   } // getCurrentSitePageId
   
   private Set getUserList(String worksiteId) {
      Set members = new HashSet();
      Set users = new HashSet();
      
      try {
         Site site = SiteService.getSite(worksiteId);
         if (site.hasGroups()) {
            String currentUser = SessionManager.getCurrentSessionUserId();
            Collection groups = site.getGroupsWithMember(currentUser);
            for (Iterator iter = groups.iterator(); iter.hasNext();) {
               Group group = (Group) iter.next();
               members.addAll(group.getMembers());
            }
         }
         else {
            members.addAll(site.getMembers());
         }
         
         for (Iterator memb = members.iterator(); memb.hasNext();) {
            try {
               Member member = (Member) memb.next();
               users.add(UserDirectoryService.getUser(member.getUserId()));
            } catch (UserNotDefinedException e) {
               logger.error("Unable to find user: " + e.getId(), e);
            }            
         }
      } catch (IdUnusedException e) {
         logger.error("", e);
      }
      return users;
   }
   
   private Cell getCell(Collection cells, Criterion criterion, Level level) {
      for (Iterator iter=cells.iterator(); iter.hasNext();) {
         Cell cell = (Cell) iter.next();
         if (cell.getScaffoldingCell().getRootCriterion().getId().getValue().equals(criterion.getId().getValue()) && 
               cell.getScaffoldingCell().getLevel().getId().getValue().equals(level.getId().getValue())) {
            return cell;
         }
      }
      return null;
   }

   
   public ModelAndView handleRequest(Object requestModel, Map request, Map session, Map application, Errors errors) {
      Map model = new HashMap();
      //model.put("view_user", request.get("view_user"));
      return new ModelAndView("success", model);
   }

   

   public ToolManager getToolManager() {
      return toolManager;
   }

   public void setToolManager(ToolManager toolManager) {
      this.toolManager = toolManager;
   }
}

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
import org.sakaiproject.metaobj.security.FunctionConstants;
import org.sakaiproject.metaobj.shared.model.Agent;
import org.sakaiproject.metaobj.shared.model.Id;
import org.sakaiproject.metaobj.utils.mvc.intf.FormController;
import org.sakaiproject.metaobj.utils.mvc.intf.LoadObjectController;
import org.sakaiproject.service.framework.portal.cover.PortalService;
import org.sakaiproject.service.legacy.authzGroup.Member;
import org.sakaiproject.service.legacy.site.Group;
import org.sakaiproject.service.legacy.site.Site;
import org.sakaiproject.service.legacy.site.cover.SiteService;
import org.sakaiproject.service.legacy.user.cover.UserDirectoryService;
import org.sakaiproject.api.kernel.session.cover.SessionManager;
import org.sakaiproject.api.kernel.tool.Placement;
import org.sakaiproject.api.kernel.tool.ToolManager;
import org.sakaiproject.exception.IdUnusedException;
import org.springframework.validation.Errors;
import org.springframework.web.servlet.ModelAndView;
import org.theospi.portfolio.matrix.MatrixManager;
import org.theospi.portfolio.matrix.model.Cell;
import org.theospi.portfolio.matrix.model.Criterion;
import org.theospi.portfolio.matrix.model.Level;
import org.theospi.portfolio.matrix.model.Matrix;
import org.theospi.portfolio.matrix.model.Scaffolding;
import org.theospi.portfolio.matrix.model.ScaffoldingCell;


import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ViewMatrixController extends AbstractMatrixController implements FormController, LoadObjectController {

   protected final Log logger = LogFactory.getLog(getClass());
   
   private ToolManager toolManager;

   public Object fillBackingObject(Object incomingModel, Map request, Map session, Map application) throws Exception {

      MatrixGridBean grid = (MatrixGridBean)incomingModel;
      String strScaffoldingId = (String)request.get("scaffolding_id");
      
      
      if (strScaffoldingId == null) {
         Placement placement = getToolManager().getCurrentPlacement();
         strScaffoldingId = placement.getPlacementConfig().getProperty(
               MatrixManager.EXPOSED_WIZARD_KEY);
      }
      
      Id scaffoldingId = getIdManager().getId(strScaffoldingId);
      Scaffolding scaffolding = getMatrixManager().getScaffolding(scaffoldingId);

      Agent currentAgent = getAuthManager().getAgent();
      boolean createAuthz = false;
      
      String user = (String)request.get("view_user");
      if (user != null) {
          currentAgent = getAgentManager().getAgent(getIdManager().getId(user));
          createAuthz = true;
      }

      Matrix matrix = getMatrixManager().getMatrix(scaffoldingId, currentAgent.getId());
      if (matrix == null) {
         if (currentAgent != null && !currentAgent.equals("")) {
            //Don't create a matrix unless the scaffolding has been published
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
      Boolean readOnly = new Boolean(false);

      if (owner != null && !owner.equals(getAuthManager().getAgent()))
         readOnly = new Boolean(true);
      
      model.put("matrixOwner", owner);      
      model.put("readOnlyMatrix", readOnly);
      
      return model;
   }
   
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
            Member member = (Member) memb.next();
            users.add(UserDirectoryService.getUser(member.getUserId()));
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

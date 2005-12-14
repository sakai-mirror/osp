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
 * $Header: /opt/CVS/osp2.x/matrix/tool/src/java/org/theospi/portfolio/matrix/control/ViewMatrixController.java,v 1.6 2005/09/01 18:00:56 chmaurer Exp $
 * $Revision$
 * $Date$
 */
package org.theospi.portfolio.matrix.control;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.metaobj.security.AuthenticationManager;
import org.sakaiproject.metaobj.security.FunctionConstants;
import org.sakaiproject.metaobj.shared.mgt.IdManager;
import org.sakaiproject.metaobj.shared.mgt.AgentManager;
import org.sakaiproject.metaobj.shared.model.Agent;
import org.sakaiproject.metaobj.shared.model.Id;
import org.sakaiproject.metaobj.utils.mvc.intf.FormController;
import org.sakaiproject.metaobj.utils.mvc.intf.LoadObjectController;
import org.sakaiproject.metaobj.worksite.mgt.WorksiteManager;
import org.sakaiproject.service.framework.portal.cover.PortalService;
import org.sakaiproject.service.legacy.security.cover.SecurityService;
import org.sakaiproject.service.legacy.site.ToolConfiguration;
import org.sakaiproject.service.legacy.site.cover.SiteService;
import org.sakaiproject.api.kernel.tool.ToolManager;
import org.springframework.validation.Errors;
import org.springframework.web.servlet.ModelAndView;
import org.theospi.portfolio.matrix.MatrixManager;
import org.theospi.portfolio.matrix.model.Cell;
import org.theospi.portfolio.matrix.model.Criterion;
import org.theospi.portfolio.matrix.model.Level;
import org.theospi.portfolio.matrix.model.Matrix;
import org.theospi.portfolio.matrix.model.MatrixTool;
import org.theospi.portfolio.matrix.model.Scaffolding;
import org.theospi.portfolio.matrix.model.ScaffoldingCell;
import org.theospi.portfolio.security.AuthorizationFacade;


import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ViewMatrixController implements FormController, LoadObjectController {

   protected final Log logger = LogFactory.getLog(getClass());
   private MatrixManager matrixManager;
   private AuthenticationManager authManager = null;
   private IdManager idManager = null;
   private AuthorizationFacade authzManager = null;
   private WorksiteManager worksiteManager = null;
   private AgentManager agentManager = null;
   private ToolManager toolManager;

   public Object fillBackingObject(Object incomingModel, Map request, Map session, Map application) throws Exception {

      MatrixGridBean grid = (MatrixGridBean)incomingModel;
      Scaffolding scaffolding = null;

      Agent currentAgent = getAuthManager().getAgent();
      boolean createAuthz = false;

      String user = (String)request.get("view_user");
      if (user != null) {
          currentAgent = getAgentManager().getAgent(idManager.getId(user));
          createAuthz = true;
      }

      Id toolId = idManager.getId(PortalService.getCurrentToolId());

      Matrix matrix = new Matrix();
      matrix = matrixManager.getMatrix(toolId, currentAgent.getId());
      if (matrix == null) {
         MatrixTool matrixTool = matrixManager.getMatrixTool(toolId);
         if (matrixTool == null) {
            return incomingModel;
         }
         if (currentAgent != null && !currentAgent.equals("")) {
            //Don't create a matrix unless the scaffolding has been published
            if (matrixTool.getScaffolding().isPublished()) {
               matrix = matrixManager.createMatrix(currentAgent, matrixTool);
            }
            else {
               grid.setScaffolding(matrixTool.getScaffolding());
               return incomingModel;
            }
         }
      }
      scaffolding = matrix.getMatrixTool().getScaffolding();
      if (createAuthz) {
         getAuthzManager().createAuthorization(getAuthManager().getAgent(), 
                 FunctionConstants.READ_MATRIX, matrix.getId());
     }

      List levels = scaffolding.getLevels();
      List criteria = scaffolding.getRootCriteria();
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
               ScaffoldingCell scaffoldingCell = matrixManager.getScaffoldingCell(criterion, level);
               cell.setScaffoldingCell(scaffoldingCell);
               cell.setStatus(scaffoldingCell.getInitialStatus());
               matrixManager.storeCell(cell);
            }
            List nodeList = new ArrayList(matrixManager.getCellContents(cell));
            cellBean.setCell(cell);
            cellBean.setAttachments(nodeList);
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

      ToolConfiguration tool = getWorksiteManager().getTool(getToolManager().getCurrentPlacement().getId());
      String courses = tool.getConfig().getProperty("theospi.courseCompareList");
      if (courses == null || courses.equals(""))
         courses = worksiteId;

      String[] realms = courses.split(",");
      Set users = new HashSet();
      for (int i=0; i<realms.length; i++) {
         String realm = "/site/" + realms[i];
//       TODO - maybe use this one: SECURE_VIEW_ROSTER?
         if (SecurityService.unlock(SiteService.SECURE_UPDATE_SITE, realm)) {
            users.addAll(SecurityService.unlockUsers(SiteService.SITE_VISIT, realm));
         }
      }

      List userList = new ArrayList(users);
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
   
   private Boolean isMaintainer(){
      return new Boolean(getAuthzManager().isAuthorized(WorksiteManager.WORKSITE_MAINTAIN,
            getIdManager().getId(PortalService.getCurrentSiteId())));
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
   public AuthorizationFacade getAuthzManager() {
      return authzManager;
   }

   /**
    * @param facade
    */
   public void setAuthzManager(AuthorizationFacade facade) {
      authzManager = facade;
   }

   /**
    * @return Returns the matrixManager.
    */
   public MatrixManager getMatrixManager() {
      return matrixManager;
   }
   /**
    * @param matrixManager The matrixManager to set.
    */
   public void setMatrixManager(MatrixManager matrixManager) {
      this.matrixManager = matrixManager;
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

   public ToolManager getToolManager() {
      return toolManager;
   }

   public void setToolManager(ToolManager toolManager) {
      this.toolManager = toolManager;
   }
}

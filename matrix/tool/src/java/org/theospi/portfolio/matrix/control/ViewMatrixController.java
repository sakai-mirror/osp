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
import java.util.Comparator;
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
import org.sakaiproject.assignment.cover.AssignmentService;
import org.sakaiproject.assignment.api.AssignmentSubmission;
import org.sakaiproject.assignment.api.Assignment;
import org.sakaiproject.component.cover.ServerConfigurationService;
import org.sakaiproject.user.api.User;

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
import org.theospi.portfolio.matrix.model.WizardPage;
import org.theospi.portfolio.assignment.AssignmentHelper;

public class ViewMatrixController extends AbstractMatrixController implements FormController, LoadObjectController {

   protected final Log logger = LogFactory.getLog(getClass());
   
   public static final String VIEW_USER = "view_user";
   
   public static final String GROUP_FILTER = "group_filter";
   
   public static final String GROUP_FILTER_BUTTON = "filter";
      
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
      // Check for invalid scaffolding (could happen if scaffolding is deleted)
      if (scaffolding == null )
      {
         logger.warn("Unable to find scaffolding: " + scaffoldingId );
         return incomingModel;
      }

      Agent currentAgent = getAuthManager().getAgent();
      boolean createAuthz = false;

      String filterButton = (String)request.get(GROUP_FILTER_BUTTON);

      String groupFilterRequest = (String)request.get(GROUP_FILTER);
      String groupFilterSession = (String)session.get(GROUP_FILTER);
      if (groupFilterRequest != null && filterButton != null) {
    	  //TODO: Check that this user can filter on this group
      }
      else if (groupFilterSession != null) {
    	  groupFilterRequest = groupFilterSession;
    	  //TODO: Check if there is a better way to shuttle this to referenceData without modding the bean
          request.put(GROUP_FILTER, groupFilterRequest);
      }
      session.put(GROUP_FILTER, groupFilterRequest);

      
      //TODO: Check to make sure that the session user is in the filtered group
		//If the user is, apply filter and select the user
	    //If not, apply and select the active user
        //For right now, we're resetting when filtering
      
      String userRequest = (String)request.get(VIEW_USER);
      String userSession = (String)session.get(VIEW_USER);
      if (groupFilterRequest != null && filterButton != null) {
    	  userRequest = null;
      }
      else {
	      if (userRequest != null) {
	         currentAgent = getAgentManager().getAgent(getIdManager().getId(userRequest));
	         createAuthz = true;
	      } else if(userSession != null) {
	         userRequest = userSession;
	         currentAgent = getAgentManager().getAgent(getIdManager().getId(userSession));
	         // The authorize was already created by this point
	      }
      }
      session.put(VIEW_USER, userRequest);
      
      Matrix matrix = getMatrixManager().getMatrix(scaffoldingId, currentAgent.getId());
      if (matrix == null) {
         if (currentAgent != null && !currentAgent.equals("")) {
            //Don't create a matrix unless the scaffolding has been published 
            // and the user has permission to use a matrix.
            if (scaffolding.isPublished() || scaffolding.isPreview()) {
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

      List<Level> levels = scaffolding.getLevels();
      List<Criterion> criteria = scaffolding.getCriteria();
      List matrixContents = new ArrayList();
      Criterion criterion = new Criterion();
      Level level = new Level();
      List row = new ArrayList();
      
      List<Cell> cells = getMatrixManager().getCells(matrix);
       
      for (Iterator<Criterion> criteriaIterator = criteria.iterator(); criteriaIterator.hasNext();) {
         row = new ArrayList();
         criterion = (Criterion) criteriaIterator.next();
         for (Iterator<Level> levelsIterator = levels.iterator(); levelsIterator.hasNext();) {
            level = (Level) levelsIterator.next();
            CellFormBean cellBean = new CellFormBean();

            Cell cell = getCell(cells, criterion, level);
            if (cell == null) {
               cell = new Cell();
               cell.getWizardPage().setOwner(matrix.getOwner());
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
            cellBean.setAssignments(getAssignments(cell.getWizardPage(), matrix.getOwner()));
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
      MatrixGridBean grid = (MatrixGridBean) command;      
      Agent owner = grid.getMatrixOwner();
      Boolean readOnly = Boolean.valueOf(false);
      String worksiteId = grid.getScaffolding().getWorksiteId().getValue();

      String filteredGroup = (String) request.get(GROUP_FILTER);
      boolean allowAllGroups = ServerConfigurationService.getBoolean(MatrixFunctionConstants.PROP_GROUPS_ALLOW_ALL_GLOBAL, false)
      			|| grid.getScaffolding().getReviewerGroupAccess() == Scaffolding.UNRESTRICTED_GROUP_ACCESS;
      List<Group> groupList = new ArrayList<Group>(getGroupList(worksiteId, allowAllGroups));
      //Collections.sort(groupList);
      //TODO: Figure out why ClassCastExceptions fire if we do this the obvious way...  The User list sorts fine
      Collections.sort(groupList, new Comparator<Group>() {
    	  public int compare(Group arg0, Group arg1) {
    		  return arg0.getTitle().toLowerCase().compareTo(arg1.getTitle().toLowerCase());
    	  }});
      
      List userList = new ArrayList(getUserList(worksiteId, filteredGroup, allowAllGroups, groupList));
		
      Collections.sort(userList);
      model.put("members", userList);
      model.put("userGroups", groupList);
      //TODO: Address why the fn:length() function can't be loaded or another handy way to pull collection size via EL
      model.put("userGroupsCount", groupList.size());
      model.put("hasGroups", hasGroups(worksiteId));
      model.put("filteredGroup", request.get(GROUP_FILTER));
      //TODO: Clean this up for efficiency.. We're going back to the SiteService too much
      
      if ((owner != null && !owner.equals(getAuthManager().getAgent())) ||
           !getAuthzManager().isAuthorized(MatrixFunctionConstants.USE_SCAFFOLDING,getWorksiteManager().getCurrentWorksiteId()))
         readOnly = Boolean.valueOf(true);

      model.put("worksite", worksiteId );
      model.put("tool", getToolManager().getCurrentPlacement());
      model.put("isMaintainer", isMaintainer());      

      model.put("matrixOwner", owner);      
      model.put("readOnlyMatrix", readOnly);
      
      if (grid.getScaffolding() != null &&
          getCurrentSitePageId().equals(grid.getScaffolding().getExposedPageId())) 
      {
         model.put("isExposedPage", Boolean.valueOf(true));
      }
      
      return model;
   }
   
	/**
	 ** Return true if matrix owner has submitted assignments associated with this cell
	 **/
	protected List getAssignments(WizardPage wizPage, Agent owner) {
      ArrayList submissions = new ArrayList();
      
		try {
			User user = UserDirectoryService.getUser(owner.getId().getValue());
			ArrayList assignments = 
				AssignmentHelper.getSelectedAssignments(wizPage.getPageDefinition().getAttachments());
			
			for ( Iterator it=assignments.iterator(); it.hasNext(); ) {
				Assignment assign = (Assignment)it.next();
				AssignmentSubmission assignSubmission = AssignmentService.getSubmission( assign.getId(),
																												 user );
				if (assignSubmission != null && assignSubmission.getSubmitted())
					submissions.add(assignSubmission);
			}
		}
		catch ( Exception e ) {
			logger.warn(".getAssignments: " +  e.toString());
		}
		
		return submissions;
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
    
    private Set getGroupList(String worksiteId, boolean allowAllGroups) {
    	try {
			Site site = SiteService.getSite(worksiteId);
			return getGroupList(site, allowAllGroups);
		} catch (IdUnusedException e) {
			logger.error("", e);
		}
		return new HashSet();    	
    }
    
    private Set getGroupList(Site site, boolean allowAllGroups) {
    	Set groups = new HashSet();
		if (site.hasGroups()) {
            String currentUser = SessionManager.getCurrentSessionUserId();
            if (allowAllGroups) {
            	groups.addAll(site.getGroups());
            }
            else {
            	groups.addAll(site.getGroupsWithMember(currentUser));
            }
		}
		return groups;
    }
    
    private boolean hasGroups(String worksiteId) {
		try {
			Site site = SiteService.getSite(worksiteId);
			return site.hasGroups();
		} catch (IdUnusedException e) {
			logger.error("", e);
		}
		return false;
	}

	private Set getUserList(String worksiteId, String filterGroupId, boolean allowAllGroups, List<Group> groups) {
		Set members = new HashSet();
		Set users = new HashSet();

		try {
			Site site = SiteService.getSite(worksiteId);
			if (site.hasGroups()) {
				String currentUser = SessionManager.getCurrentSessionUserId();
				
				if (allowAllGroups && (filterGroupId == null || filterGroupId.equals(""))) {
					members.addAll(site.getMembers());
				}
				else {
					for (Iterator iter = groups.iterator(); iter.hasNext();) {
						Group group = (Group) iter.next();
						// TODO: Determine if Java loop invariants are optimized out
						if (filterGroupId == null || filterGroupId.equals("")
								|| filterGroupId.equals(group.getId())) {
							members.addAll(group.getMembers());
						}
					}
				}
			} else {
				members.addAll(site.getMembers());
			}

			for (Iterator memb = members.iterator(); memb.hasNext();) {
				try {
					Member member = (Member) memb.next();
					users.add(UserDirectoryService.getUser(member.getUserId()));
				} catch (UserNotDefinedException e) {
					logger.warn("Unable to find user: " + e.getId() + " "
							+ e.toString());
				}
			}
		} catch (IdUnusedException e) {
			logger.error("", e);
		}
		return users;
	}
   
   private Cell getCell(Collection<Cell> cells, Criterion criterion, Level level) {
      for (Iterator<Cell> iter=cells.iterator(); iter.hasNext();) {
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

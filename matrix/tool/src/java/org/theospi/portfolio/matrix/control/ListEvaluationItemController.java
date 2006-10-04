/**********************************************************************************
* $URL:https://source.sakaiproject.org/svn/osp/trunk/matrix/tool/src/java/org/theospi/portfolio/matrix/control/ListReviewerItemController.java $
* $Id:ListReviewerItemController.java 9134 2006-05-08 20:28:42Z chmaurer@iupui.edu $
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
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.metaobj.security.AuthenticationManager;
import org.sakaiproject.metaobj.shared.mgt.AgentManager;
import org.sakaiproject.metaobj.shared.mgt.IdManager;
import org.sakaiproject.metaobj.utils.mvc.intf.CustomCommandController;
import org.sakaiproject.metaobj.utils.mvc.intf.FormController;
import org.sakaiproject.metaobj.utils.mvc.intf.ListScrollIndexer;
import org.sakaiproject.metaobj.utils.mvc.intf.LoadObjectController;
import org.sakaiproject.metaobj.worksite.mgt.WorksiteManager;
import org.sakaiproject.tool.api.ToolManager;
import org.springframework.validation.Errors;
import org.springframework.web.servlet.ModelAndView;
import org.theospi.portfolio.matrix.MatrixManager;
import org.theospi.portfolio.matrix.model.EvaluationContentComparator;
import org.theospi.portfolio.security.AuthorizationFacade;
import org.theospi.portfolio.shared.model.EvaluationContentWrapper;

/**
 * @author chmaurer
 */
public class ListEvaluationItemController implements FormController, LoadObjectController, CustomCommandController {

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
      //List list = (List) incomingModel;
      //list = matrixManager.getEvaluatableItems(authManager.getAgent(), worksiteManager.getCurrentWorksiteId());
      //Set sortedSet = new TreeSet(new EvaluationContentComparator(
      //      EvaluationContentComparator.SORT_TITLE, true));
      
      String sortColumn = (String)request.get("sortByColumn");
      if (sortColumn == null)
         sortColumn = EvaluationContentComparator.SORT_TITLE;
      String strAsc = (String)request.get("direction");
      boolean asc = true;
      if (strAsc != null)
         asc = strAsc.equalsIgnoreCase("asc");

      List list = matrixManager.getEvaluatableItems(authManager.getAgent(), worksiteManager.getCurrentWorksiteId());
      Collections.sort(list, new EvaluationContentComparator(
            sortColumn, asc));
      list = getListScrollIndexer().indexList(request, request, list);

      return list; /* goes into 'reviewerItems'  */
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
      
      String action = (String)request.get("action");
      String view = "success";

      Map model = new HashMap();
      
      if("open".equals(action)) {
         String id = (String)request.get("id");
         List list = (List)requestModel;
         
         if(id != null) {
            for(Iterator i = list.iterator(); i.hasNext(); ) {
               EvaluationContentWrapper wrapper = (EvaluationContentWrapper)i.next();
               
               if(id.equals(wrapper.getId().getValue())) {
                  view = wrapper.getUrl();
                  
                  for(Iterator params = wrapper.getUrlParams().iterator(); params.hasNext(); ) {
                     EvaluationContentWrapper.ParamBean param = (EvaluationContentWrapper.ParamBean)params.next();
                     
                     model.put(param.getKey(), param.getValue());
                  }
                  session.put("is_eval_page_id", id);
               }
            }
         }
      }
      
      return new ModelAndView(view, model);
   }

   public Map referenceData(Map request, Object command, Errors errors) {
      Map model = new HashMap();
      String worksiteId = getWorksiteManager().getCurrentWorksiteId().getValue();
      model.put("worksite", getWorksiteManager().getSite(worksiteId));
      model.put("tool", getToolManager().getCurrentPlacement());
      model.put("isMaintainer", isMaintainer());
      model.put("currentUser", authManager.getAgent());
      
      String asc = (String)request.get("direction");
      //Boolean asc = new Boolean(true);
      if (asc == null)
         asc = "asc";
      
      model.put("direction", asc);
      
      String sortColumn = (String)request.get("sortByColumn");
      if (sortColumn == null)
         sortColumn = EvaluationContentComparator.SORT_TITLE;
      model.put("sortByColumn", sortColumn);
      
      return model;
   }

   private Boolean isMaintainer() {
      return new Boolean(getAuthzManager().isAuthorized(WorksiteManager.WORKSITE_MAINTAIN,
            getIdManager().getId(getToolManager().getCurrentPlacement().getContext())));
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


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
import org.sakaiproject.entity.api.ResourceProperties;
import org.sakaiproject.exception.IdUnusedException;
import org.sakaiproject.exception.IdUsedException;
import org.sakaiproject.exception.InUseException;
import org.sakaiproject.exception.PermissionException;
import org.sakaiproject.metaobj.security.AuthenticationManager;
import org.sakaiproject.metaobj.shared.mgt.AgentManager;
import org.sakaiproject.metaobj.shared.mgt.IdManager;
import org.sakaiproject.metaobj.utils.mvc.intf.CustomCommandController;
import org.sakaiproject.metaobj.utils.mvc.intf.FormController;
import org.sakaiproject.metaobj.utils.mvc.intf.ListScrollIndexer;
import org.sakaiproject.metaobj.utils.mvc.intf.LoadObjectController;
import org.sakaiproject.metaobj.worksite.mgt.WorksiteManager;
import org.sakaiproject.site.cover.SiteService;
import org.sakaiproject.tool.api.ToolManager;
import org.sakaiproject.user.api.Preferences;
import org.sakaiproject.user.api.PreferencesEdit;
import org.sakaiproject.user.cover.PreferencesService;
import org.springframework.validation.Errors;
import org.springframework.web.servlet.ModelAndView;
import org.theospi.portfolio.matrix.MatrixManager;
import org.theospi.portfolio.matrix.WizardPageHelper;
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
  
   private final static String EVAL_PLACEMENT_PREF = "org.theospi.portfolio.evaluation.placement.";
   private final static String CURRENT_SITE_EVALS = "org.theospi.portfolio.evaluation.currentSite";
   private final static String ALL_EVALS = "org.theospi.portfolio.evaluation.allSites";
   private final static String EVAL_SITE_FETCH = "org.theospi.portfolio.evaluation.siteEvals";
   
   /* (non-Javadoc)
    * @see org.theospi.utils.mvc.intf.LoadObjectController#fillBackingObject(java.lang.Object, java.util.Map, java.util.Map, java.util.Map)
    */
   public Object fillBackingObject(Object incomingModel, Map request, Map session, Map application) throws Exception {
      
      List list = new ArrayList();
      
      String evalType = (String)request.get("evalTypeKey");
      if (evalType != null)
         setUserEvalProperty(evalType);
      
      if (ALL_EVALS.equals(getUserEvalProperty()))         
         list = matrixManager.getEvaluatableItems(authManager.getAgent());
      else
         list = matrixManager.getEvaluatableItems(authManager.getAgent(), worksiteManager.getCurrentWorksiteId());
      
      list = purgeNullOwners(list);
      
      String sortColumn = (String)request.get("sortByColumn");
      if (sortColumn == null)
         sortColumn = EvaluationContentComparator.SORT_TITLE;
      String strAsc = (String)request.get("direction");
      boolean asc = true;
      if (strAsc != null)
         asc = strAsc.equalsIgnoreCase("asc");

      
      Collections.sort(list, new EvaluationContentComparator(
            sortColumn, asc));
      list = getListScrollIndexer().indexList(request, request, list);

      return list; /* goes into 'reviewerItems'  */
   }
   
   protected List purgeNullOwners(List list) {
      List parsedList = new ArrayList(list.size());
      
      for (Iterator i = list.iterator(); i.hasNext();) {
         EvaluationContentWrapper ecw = (EvaluationContentWrapper) i.next();
         if (ecw.getOwner() != null)
            parsedList.add(ecw);
      }
      
      return parsedList;
      
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
               
               if(id.equals(wrapper.getId().getValue() + "_" + wrapper.getOwner().getId())) {
                  view = wrapper.getUrl();
                  if (view == null) break;
                  for(Iterator params = wrapper.getUrlParams().iterator(); params.hasNext(); ) {
                     EvaluationContentWrapper.ParamBean param = (EvaluationContentWrapper.ParamBean)params.next();
                     
                     model.put(param.getKey(), param.getValue());
                  }
                  
                  //Clear out the hier page if there is one & clear the set of seq pages
                  session.remove(WizardPageHelper.WIZARD_PAGE);
                  session.remove(WizardPageHelper.SEQUENTIAL_WIZARD_PAGES);
                  
                  session.put("is_eval_page_id", wrapper.getId().getValue());
                  break;
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
      model.put("evalType", getUserEvalProperty());
      model.put("currentSiteEvalsKey", CURRENT_SITE_EVALS);
      model.put("allEvalsKey", ALL_EVALS);
      
      boolean userSite = SiteService.isUserSite(getWorksiteManager().getCurrentWorksiteId().getValue());
      model.put("isUserSite", userSite);
      
      return model;
   }
   
   private String getUserEvalProperty() {
      String prop = CURRENT_SITE_EVALS;
      
      //If the site is a my workapace site, default to all sites
      //Site site = getWorksiteManager().getSite(getWorksiteManager().getCurrentWorksiteId().getValue());
      boolean userSite = SiteService.isUserSite(getWorksiteManager().getCurrentWorksiteId().getValue());
      if (userSite) prop = ALL_EVALS;
      
      try {
         Preferences userPreferences = PreferencesService.getPreferences(authManager.getAgent().getId().getValue());
         ResourceProperties evalPrefs = userPreferences.getProperties(EVAL_PLACEMENT_PREF + getToolManager().getCurrentPlacement().getId());
         String tmpProp = evalPrefs.getProperty(EVAL_SITE_FETCH);
         if (tmpProp != null) prop = tmpProp;
      }
      catch (Exception e) {
         logger.debug("Couldn't get user prefs for the eval tool.  Using defaults.");
      }
      return prop;
   }

   private void setUserEvalProperty(String evalType) {
      PreferencesEdit prefEdit = null;
      try {
         prefEdit = (PreferencesEdit) PreferencesService.add(authManager.getAgent().getId().getValue());
      } catch (PermissionException e) {
         logger.warn("Problem saving preferences for site evals in setUserEvalProperty().", e);
      } catch (IdUsedException e) {
         // Preferences already exist, just edit
         try {
            prefEdit = (PreferencesEdit) PreferencesService.edit(authManager.getAgent().getId().getValue());
         } catch (PermissionException e1) {
            logger.warn("Problem saving preferences for site evals in setUserEvalProperty().", e1);
         } catch (InUseException e1) {
            logger.warn("Problem saving preferences for site evals in setUserEvalProperty().", e1);
         } catch (IdUnusedException e1) {
            // This should be safe to ignore since we got here because it existed
            logger.warn("Problem saving preferences for site evals in setUserEvalProperty().", e1);
         }
      }
      if (prefEdit != null) {
         ResourceProperties propEdit = prefEdit.getPropertiesEdit(EVAL_PLACEMENT_PREF + getToolManager().getCurrentPlacement().getId());
         if (evalType.equals(CURRENT_SITE_EVALS))
            propEdit.removeProperty(EVAL_SITE_FETCH);
         else
            propEdit.addProperty(EVAL_SITE_FETCH, evalType);
         try {
            PreferencesService.commit(prefEdit);
         }
         catch (Exception e) {
            logger.warn("Problem saving preferences for site evals in setUserEvalProperty().", e);
         }
      }
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


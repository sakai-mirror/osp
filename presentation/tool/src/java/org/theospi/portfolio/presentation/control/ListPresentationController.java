/**********************************************************************************
* $URL:https://source.sakaiproject.org/svn/osp/trunk/presentation/tool/src/java/org/theospi/portfolio/presentation/control/ListPresentationController.java $
* $Id:ListPresentationController.java 9134 2006-05-08 20:28:42Z chmaurer@iupui.edu $
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

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.component.api.ServerConfigurationService;
import org.sakaiproject.entity.api.ResourceProperties;
import org.sakaiproject.exception.IdUnusedException;
import org.sakaiproject.exception.IdUsedException;
import org.sakaiproject.exception.InUseException;
import org.sakaiproject.exception.PermissionException;
import org.sakaiproject.metaobj.shared.model.Agent;
import org.sakaiproject.metaobj.utils.mvc.intf.ListScrollIndexer;
import org.sakaiproject.site.cover.SiteService;
import org.sakaiproject.tool.cover.ToolManager;
import org.sakaiproject.tool.cover.SessionManager;
import org.sakaiproject.tool.api.Tool;
import org.sakaiproject.user.api.Preferences;
import org.sakaiproject.user.api.PreferencesEdit;
import org.sakaiproject.user.cover.PreferencesService;
import org.springframework.validation.Errors;
import org.springframework.web.servlet.ModelAndView;

public class ListPresentationController extends AbstractPresentationController {

   protected final Log logger = LogFactory.getLog(getClass());
   private ListScrollIndexer listScrollIndexer;
   private ServerConfigurationService serverConfigurationService;
   
   private final static String HIDDEN_PRES_PLACEMENT_PREF = "org.theospi.portfolio.presentation.placement.";
   private final static String HIDDEN_PRES_PREF = "org.theospi.portfolio.presentation.hidden.";

   public ModelAndView handleRequest(Object requestModel, Map request, Map session, Map application, Errors errors) {
      Hashtable<String, Object> model = new Hashtable<String, Object>();
      Agent currentAgent = getAuthManager().getAgent();
      String currentToolId = ToolManager.getCurrentPlacement().getId();
      String worksiteId = getWorksiteManager().getCurrentWorksiteId().getValue();

      boolean showHidden = getUserPresHiddenProperty();
      String showHiddenKey = (String)request.get("showHiddenKey");
      if (showHiddenKey != null)
         showHidden = setUserPresHiddenProperty(showHiddenKey);
      
      List presentations = null;
		if ( isOnWorkspaceTab() )
      {
         presentations = new ArrayList(getPresentationManager().findPresentationsByViewer(currentAgent, showHidden));
      }
      else
      {
         presentations = new ArrayList(getPresentationManager().findPresentationsByViewer(currentAgent,
                                                                                          currentToolId, showHidden));
      }

      model.put("presentations",
         getListScrollIndexer().indexList(request, model, presentations));

      String baseUrl = getServerConfigurationService().getServerUrl();

      String url =  baseUrl + "/osp-presentation-tool/viewPresentation.osp?";
      url += Tool.PLACEMENT_ID + "=" + SessionManager.getCurrentToolSession().getPlacementId();
      model.put("baseUrl", url);
      model.put("worksite", getWorksiteManager().getSite(worksiteId));
      model.put("tool", getWorksiteManager().getTool(currentToolId));
      model.put("isMaintainer", isMaintainer());
      model.put("osp_agent", currentAgent);
      model.put("showHidden", showHidden);
      model.put("myworkspace", isOnWorkspaceTab() );
      return new ModelAndView("success", model);
   }
   
   /**
    * 
    * @return
    */
   protected boolean getUserPresHiddenProperty() {
      boolean prop = true;
      
      try {
         Preferences userPreferences = PreferencesService.getPreferences(getAuthManager().getAgent().getId().getValue());
         ResourceProperties evalPrefs = userPreferences.getProperties(HIDDEN_PRES_PLACEMENT_PREF + ToolManager.getCurrentPlacement().getId());
         String tmpProp = evalPrefs.getProperty(HIDDEN_PRES_PREF);
         if (tmpProp != null) prop = Boolean.getBoolean(tmpProp);
      }
      catch (Exception e) {
         logger.debug("Couldn't get user prefs for showing hidden presentations.  Using defaults.");
      }
      return prop;
   }
   
   /**
    * 
    * @param evalType
    */
   protected boolean setUserPresHiddenProperty(String hiddenValue) {
      PreferencesEdit prefEdit = null;
      try {
         prefEdit = (PreferencesEdit) PreferencesService.add(getAuthManager().getAgent().getId().getValue());
      } catch (PermissionException e) {
         logger.warn("Problem saving preferences for site hidden presentations in setUserPresHiddenProperty().", e);
      } catch (IdUsedException e) {
         // Preferences already exist, just edit
         try {
            prefEdit = (PreferencesEdit) PreferencesService.edit(getAuthManager().getAgent().getId().getValue());
         } catch (PermissionException e1) {
            logger.warn("Problem saving preferences for site hidden presentations in setUserPresHiddenProperty().", e1);
         } catch (InUseException e1) {
            logger.warn("Problem saving preferences for site hidden presentations in setUserPresHiddenProperty().", e1);
         } catch (IdUnusedException e1) {
            // This should be safe to ignore since we got here because it existed
            logger.warn("Problem saving preferences for site hidden presentations in setUserPresHiddenProperty().", e1);
         }
      }
      if (prefEdit != null) {
         ResourceProperties propEdit = prefEdit.getPropertiesEdit(HIDDEN_PRES_PLACEMENT_PREF + ToolManager.getCurrentPlacement().getId());
         if (hiddenValue.equals(Boolean.toString(Boolean.TRUE)))
            propEdit.removeProperty(HIDDEN_PRES_PREF);
         else
            propEdit.addProperty(HIDDEN_PRES_PREF, hiddenValue);
         try {
            PreferencesService.commit(prefEdit);
         }
         catch (Exception e) {
            logger.warn("Problem saving preferences for site hidden presentations in setUserPresHiddenProperty().", e);
         }
      }
      return hiddenValue.equals(Boolean.toString(Boolean.TRUE));

   }

   /**
    * See if the current tab is the workspace tab.
    * @return true if we are currently on the "My Workspace" tab.
    */
   private boolean isOnWorkspaceTab()
   {
      return SiteService.isUserSite(ToolManager.getCurrentPlacement().getContext());
   }
	
   public ListScrollIndexer getListScrollIndexer() {
      return listScrollIndexer;
   }

   public void setListScrollIndexer(ListScrollIndexer listScrollIndexer) {
      this.listScrollIndexer = listScrollIndexer;
   }
    public ServerConfigurationService getServerConfigurationService() {
      return serverConfigurationService;
   }

   public void setServerConfigurationService(
         ServerConfigurationService serverConfigurationService) {
      this.serverConfigurationService = serverConfigurationService;
   }
	
}

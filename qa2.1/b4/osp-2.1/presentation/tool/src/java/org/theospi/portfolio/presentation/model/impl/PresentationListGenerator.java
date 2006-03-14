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
package org.theospi.portfolio.presentation.model.impl;

import org.theospi.portfolio.list.intf.ActionableListGenerator;
import org.theospi.portfolio.list.intf.CustomLinkListGenerator;
import org.theospi.portfolio.presentation.PresentationManager;
import org.theospi.portfolio.presentation.model.Presentation;
import org.theospi.portfolio.presentation.model.PresentationTemplate;
import org.sakaiproject.metaobj.worksite.mgt.WorksiteManager;
import org.sakaiproject.metaobj.security.AuthenticationManager;
import org.sakaiproject.metaobj.shared.control.servlet.SakaiComponentDispatchServlet;
import org.sakaiproject.service.legacy.site.ToolConfiguration;
import org.sakaiproject.service.framework.session.SessionState;
import org.sakaiproject.service.framework.session.cover.UsageSessionService;

import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;

public class PresentationListGenerator implements ActionableListGenerator, CustomLinkListGenerator {
   private PresentationManager presentationManager;
   private static final String TOOL_ID_PARAM = "toolId";
   private static final String PRESENTATION_ID_PARAM = "presentationId";

   private WorksiteManager worksiteManager;
   private List columns;
   private List defaultColumns;
   private AuthenticationManager authnManager;

   public WorksiteManager getWorksiteManager() {
      return worksiteManager;
   }

   public AuthenticationManager getAuthnManager() {
      return authnManager;
   }

   public void setAuthnManager(AuthenticationManager authnManager) {
      this.authnManager = authnManager;
   }

   public void setWorksiteManager(WorksiteManager worksiteManager) {
      this.worksiteManager = worksiteManager;
   }

   public PresentationManager getPresentationManager() {
      return presentationManager;
   }

   public void setPresentationManager(PresentationManager presentationManager) {
      this.presentationManager = presentationManager;
   }

   public List getColumns() {
      return columns;
   }

   public void setColumns(List columns) {
      this.columns = columns;
   }

   public List getDefaultColumns() {
      return defaultColumns;
   }

   public void setDefaultColumns(List defaultColumns) {
      this.defaultColumns = defaultColumns;
   }

   public List getObjects() {
      return new ArrayList(getPresentationManager().findPresentationsByViewer(getAuthnManager().getAgent()));
   }

   public Map getToolParams(Object entry) {
      Map params = new HashMap();
      Presentation presentation = (Presentation) entry;
      params.put(PRESENTATION_ID_PARAM, presentation.getId());
      params.put(TOOL_ID_PARAM, presentation.getToolConfiguration().getId());
      return params;
   }

   public ToolConfiguration getToolInfo(Map request) {
      String toolId = (String) request.get(TOOL_ID_PARAM);
      if (toolId != null && toolId.length() > 0 ){
         return getWorksiteManager().getTool(toolId);
      }
      return null;
   }

   public boolean isNewWindow(Object entry) {
      return !internalWindow((Presentation)entry);
   }

   public void setToolState(String toolId, Map request) {
      SessionState sessionState = UsageSessionService.getSessionState(toolId);
      sessionState.setAttribute(SakaiComponentDispatchServlet.TOOL_STATE_VIEW_KEY,
            "viewPresentation.osp");
      Map requestParams = new HashMap();
      requestParams.put("id", request.get(PRESENTATION_ID_PARAM));
      sessionState.setAttribute(SakaiComponentDispatchServlet.TOOL_STATE_VIEW_REQUEST_PARAMS_KEY,
            requestParams);
   }

   /**
    * Create a custom link for enty if it needs
    * to customize, otherwise, null to use the usual entry
    *
    * @param entry
    * @return link to use or null to use normal redirect link
    */
   public String getCustomLink(Object entry) {
      Presentation pres = (Presentation)entry;
      if (!internalWindow(pres)) {
         return pres.getExternalUri();
      }
      return null;
   }

   protected boolean internalWindow(Presentation pres) {
      PresentationTemplate template = pres.getTemplate();

      if (!template.isIncludeHeaderAndFooter()) {
         return false;
      }

      WorksiteManager manager = getWorksiteManager();

      return manager.isUserInSite(pres.getTemplate().getSiteId());
   }
}

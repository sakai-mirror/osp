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
import org.theospi.portfolio.list.impl.BaseListGenerator;
import org.theospi.portfolio.presentation.PresentationManager;
import org.theospi.portfolio.presentation.model.Presentation;
import org.theospi.portfolio.presentation.model.PresentationTemplate;
import org.sakaiproject.metaobj.worksite.mgt.WorksiteManager;
import org.sakaiproject.metaobj.security.AuthenticationManager;
import org.sakaiproject.site.api.ToolConfiguration;

import java.util.*;

public class PresentationListGenerator extends BaseListGenerator implements ActionableListGenerator, CustomLinkListGenerator {
   private PresentationManager presentationManager;
   private static final String TOOL_ID_PARAM = "toolId";
   private static final String PRESENTATION_ID_PARAM = "presentationId";

   private WorksiteManager worksiteManager;
   private List columns;
   private List defaultColumns;
   private AuthenticationManager authnManager;

   public void init(){
       super.init();
   }
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

      List presentations = new ArrayList();
      List tempPresentationList = new ArrayList(getPresentationManager().findPresentationsByViewer(getAuthnManager().getAgent()));
      {
        if (tempPresentationList.size() > 0)
        {
          Iterator iter = tempPresentationList.iterator();

          while (iter.hasNext())
          {
            presentations.add(new DecoratedPresentation((Presentation) iter.next(), worksiteManager));
          }
        }
      }
      return presentations;

      // return new ArrayList(getPresentationManager().findPresentationsByViewer(getAuthnManager().getAgent()));
   }

   public boolean isNewWindow(Object entry) {
      return !internalWindow((DecoratedPresentation)entry);
   }

   /**
    * Create a custom link for enty if it needs
    * to customize, otherwise, null to use the usual entry
    *
    * @param entry
    * @return link to use or null to use normal redirect link
    */
   public String getCustomLink(Object entry) {
      DecoratedPresentation pres = (DecoratedPresentation)entry;
      if (!internalWindow(pres)) {
         return pres.getExternalUri();
      }
      return null;
   }

   protected boolean internalWindow(DecoratedPresentation pres) {
      PresentationTemplate template = pres.getTemplate();

      if (!template.isIncludeHeaderAndFooter()) {
         return false;
      }

      WorksiteManager manager = getWorksiteManager();

      return manager.isUserInSite(pres.getTemplate().getSiteId());
   }
      public Map getToolParams(Object entry) {
      Map params = new HashMap();
      Presentation presentation = (Presentation) entry;
      params.put(PRESENTATION_ID_PARAM, presentation.getId());
      params.put(TOOL_ID_PARAM, presentation.getToolId());
      return params;
        }

    public ToolConfiguration getToolInfo(Map request) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public void setToolState(String toolId, Map request) {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}

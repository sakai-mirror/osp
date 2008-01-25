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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.sakaiproject.exception.IdUnusedException;
import org.sakaiproject.metaobj.security.AuthenticationManager;
import org.sakaiproject.metaobj.shared.model.Agent;
import org.sakaiproject.metaobj.worksite.mgt.WorksiteManager;
import org.sakaiproject.site.api.Site;
import org.sakaiproject.site.api.ToolConfiguration;
import org.sakaiproject.site.cover.SiteService;
import org.sakaiproject.user.api.UserNotDefinedException;
import org.theospi.portfolio.list.impl.BaseListGenerator;
import org.theospi.portfolio.list.intf.ActionableListGenerator;
import org.theospi.portfolio.list.intf.CustomLinkListGenerator;
import org.theospi.portfolio.presentation.PresentationFunctionConstants;
import org.theospi.portfolio.presentation.PresentationManager;
import org.theospi.portfolio.presentation.model.Presentation;
import org.theospi.portfolio.presentation.model.PresentationTemplate;
import org.theospi.portfolio.security.AuthorizationFacade;

public class PresentationListGenerator extends BaseListGenerator implements ActionableListGenerator, CustomLinkListGenerator {
   private PresentationManager presentationManager;
   private static final String TOOL_ID_PARAM = "toolId";
   private static final String PRESENTATION_ID_PARAM = "presentationId";

   private WorksiteManager worksiteManager;
   private AuthenticationManager authnManager;
   private AuthorizationFacade authzManager;

   public void init(){
      logger.info("init()"); 
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

   public List getObjects() {

      List<DecoratedPresentation> presentations = new ArrayList<DecoratedPresentation>();
      Agent viewer = getAuthnManager().getAgent();
      List tempPresentationList = new ArrayList(getPresentationManager().findPresentationsByViewer(viewer));
      {
        if (tempPresentationList.size() > 0)
        {
          Iterator iter = tempPresentationList.iterator();

          while (iter.hasNext())
          {
             Presentation pres = (Presentation) iter.next();
             
             try {
                if (!getAuthzManager().isAuthorized(viewer, 
                      PresentationFunctionConstants.HIDE_PRESENTATION, pres.getId())) {
                   Site site = SiteService.getSite(pres.getSiteId());
                   DecoratedPresentation decoPres = new DecoratedPresentation(pres, site);
                   presentations.add(decoPres);
                }
             }
             catch (IdUnusedException e) {
                logger.warn("Site with id " + pres.getSiteId() + " does not exist.");
            } catch (UserNotDefinedException e) {
               logger.warn("User with id " + pres.getOwner().getId() + " does not exist.");
            }
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
      DecoratedPresentation decoPres = (DecoratedPresentation)entry;
      if (!internalWindow(decoPres)) {
         return decoPres.getPresentation().getExternalUri();
      }
      return null;
   }

   protected boolean internalWindow(DecoratedPresentation pres) {
      PresentationTemplate template = pres.getPresentation().getTemplate();

      if (!template.isIncludeHeaderAndFooter()) {
         return false;
      }

      WorksiteManager manager = getWorksiteManager();

      return manager.isUserInSite(pres.getPresentation().getTemplate().getSiteId());
   }
      public Map getToolParams(Object entry) {
      Map<String, Object> params = new HashMap<String, Object>();
      DecoratedPresentation presentation = (DecoratedPresentation) entry;
      params.put(PRESENTATION_ID_PARAM, presentation.getId());
      params.put(TOOL_ID_PARAM, presentation.getPresentation().getToolId());
      return params;
        }

    public ToolConfiguration getToolInfo(Map request) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public void setToolState(String toolId, Map request) {
        //To change body of implemented methods use File | Settings | File Templates.
    }
   /**
    * @return the authzManager
    */
   public AuthorizationFacade getAuthzManager() {
      return authzManager;
   }
   /**
    * @param authzManager the authzManager to set
    */
   public void setAuthzManager(AuthorizationFacade authzManager) {
      this.authzManager = authzManager;
   }
}

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
package org.theospi.portfolio.wizard;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.sakaiproject.component.cover.ServerConfigurationService;
import org.sakaiproject.metaobj.security.AuthenticationManager;
import org.sakaiproject.metaobj.shared.mgt.IdManager;
import org.sakaiproject.metaobj.worksite.mgt.WorksiteManager;
import org.sakaiproject.site.api.Site;
import org.sakaiproject.site.api.ToolConfiguration;
import org.sakaiproject.tool.cover.SessionManager;
import org.sakaiproject.user.api.UserNotDefinedException;
import org.theospi.portfolio.list.impl.BaseListGenerator;
import org.theospi.portfolio.list.intf.ActionableListGenerator;
import org.theospi.portfolio.list.intf.CustomLinkListGenerator;
import org.theospi.portfolio.matrix.MatrixFunctionConstants;
import org.theospi.portfolio.matrix.MatrixManager;
import org.theospi.portfolio.matrix.model.Scaffolding;
import org.theospi.portfolio.security.AuthorizationFacade;
import org.theospi.portfolio.shared.model.SortableListObject;
import org.theospi.portfolio.wizard.mgt.WizardManager;
import org.theospi.portfolio.wizard.model.Wizard;

public class WizardListGenerator extends BaseListGenerator implements ActionableListGenerator, CustomLinkListGenerator {
   
   private static final String SITE_ID_PARAM = "selectedSiteId";
   private static final String WIZARD_ID_PARAM = "wizardId";
   private static final String MATRIX_ID_PARAM = "matrixId";

   private WizardManager wizardManager;
   private MatrixManager matrixManager;
   private WorksiteManager worksiteManager;
   private AuthenticationManager authnManager;
   private IdManager idManager;
   private AuthorizationFacade authzManager;
   private List siteTypes;
   private List displayTypes;

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

   public List getObjects() {

      List wizards = new ArrayList();
      List userSites = getWorksiteManager().getUserSites(null, getSiteTypes());
      List siteIds = new ArrayList(userSites.size());
      List siteStrIds = new ArrayList(userSites.size());
      Map siteMap = new HashMap();
      
      for (Iterator i = userSites.iterator(); i.hasNext();) {
         Site site = (Site) i.next();
         String siteId = site.getId();
         siteIds.add(siteId);
         siteStrIds.add(idManager.getId(siteId));
         siteMap.put(siteId, site);
      }
      
      List tempWizardList = new ArrayList();
      if (getDisplayTypes().contains("wizards")) tempWizardList = getWizardManager().findPublishedWizards(siteIds);
      List tempMatrixList = new ArrayList();
      if (getDisplayTypes().contains("matrices")) tempMatrixList = getMatrixManager().findPublishedScaffolding(siteStrIds);
      
      //Need to make sure the current user can actually have one of their own here, 
      // so only check if they can "use"
      List objects = new ArrayList();
      
      objects.addAll(verifyWizards(tempWizardList, siteMap));
      
      objects.addAll(verifyMatrices(tempMatrixList, siteMap));
      

      return objects;
   }
   
   protected List verifyWizards(List allWizards, Map siteMap) {
      List retWizards = new ArrayList();
      for (Iterator i = allWizards.iterator(); i.hasNext();) {
         Wizard wizard = (Wizard)i.next();
         
         //make sure that the target site gets tested
         getAuthzManager().pushAuthzGroups(wizard.getSiteId());
         
         if (getAuthzManager().isAuthorized(WizardFunctionConstants.VIEW_WIZARD, 
               idManager.getId(wizard.getSiteId()))) {
            Site site = (Site)siteMap.get(wizard.getSiteId());
            SortableListObject wiz;
            try {
               wiz = new SortableListObject(wizard.getId(), 
                     wizard.getName(), wizard.getDescription(), 
                     wizard.getOwner(), site, wizard.getType());
               retWizards.add(wiz);
            } catch (UserNotDefinedException e) {
               logger.warn("User with id " + wizard.getOwner().getId() + " does not exist.");
            }
            
         }
      }
      return retWizards;
   }

   protected List verifyMatrices(List allMatrices, Map siteMap) {
      List retMatrices = new ArrayList();
      for (Iterator i = allMatrices.iterator(); i.hasNext();) {
         Scaffolding scaffolding = (Scaffolding)i.next();
         
         //make sure that the target site gets tested
         getAuthzManager().pushAuthzGroups(scaffolding.getWorksiteId().getValue());
         
         if (getAuthzManager().isAuthorized(MatrixFunctionConstants.USE_SCAFFOLDING, 
               scaffolding.getWorksiteId())) {
            Site site = (Site)siteMap.get(scaffolding.getWorksiteId().getValue());
            SortableListObject scaff;
            try {
               scaff = new SortableListObject(scaffolding.getId(), 
                     scaffolding.getTitle(), scaffolding.getDescription(), 
                     scaffolding.getOwner(), site, MatrixFunctionConstants.SCAFFOLDING_PREFIX);
               retMatrices.add(scaff);
            } catch (UserNotDefinedException e) {
               logger.warn("User with id " + scaffolding.getOwner().getId() + " does not exist.");
            }
            
         }
      }
      return retMatrices;
   }
   
   public boolean isNewWindow(Object entry) {
      return false;
   }

   /**
    * Create a custom link for enty if it needs
    * to customize, otherwise, null to use the usual entry
    *
    * @param entry
    * @return link to use or null to use normal redirect link
    */
   public String getCustomLink(Object entry) {
      SortableListObject obj = (SortableListObject)entry;
      String urlEnd = "";
      String placement = "";
      
      if (obj.getType().equals(WizardFunctionConstants.WIZARD_TYPE_HIERARCHICAL) || 
            obj.getType().equals(WizardFunctionConstants.WIZARD_TYPE_SEQUENTIAL)) {
         urlEnd = "/osp.wizard.run.helper/runWizardGuidance?session.CURRENT_WIZARD_ID=" + 
            obj.getId().getValue() + "&session.WIZARD_USER_ID=" + SessionManager.getCurrentSessionUserId() +
            "&session.WIZARD_RESET_CURRENT=true";
      
         ToolConfiguration toolConfig = obj.getSite().getToolForCommonId("osp.wizard");
         placement = toolConfig.getId();
      }
      else if (obj.getType().equals(MatrixFunctionConstants.SCAFFOLDING_PREFIX)) {
         urlEnd = "/viewMatrix.osp?1=1&scaffolding_id=" + obj.getId().getValue();
         
         ToolConfiguration toolConfig = obj.getSite().getToolForCommonId("osp.matrix");
         placement = toolConfig.getId();
      }
      
      String url = ServerConfigurationService.getPortalUrl() + "/directtool/" + placement + urlEnd;
      
      return url;
   }
   
   public Map getToolParams(Object entry) {
      Map params = new HashMap();
      SortableListObject obj = (SortableListObject) entry;      
      
      if (obj.getType().equals(WizardFunctionConstants.WIZARD_PREFIX)) {
         params.put(WIZARD_ID_PARAM, obj.getId());
      }
      else if (obj.getType().equals(MatrixFunctionConstants.SCAFFOLDING_PREFIX)) {
         params.put(MATRIX_ID_PARAM, obj.getId());
      }
      
      params.put(SITE_ID_PARAM, idManager.getId(obj.getSite().getId()));
      return params;
   }

    public ToolConfiguration getToolInfo(Map request) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public void setToolState(String toolId, Map request) {
        //To change body of implemented methods use File | Settings | File Templates.
    }
   /**
    * @return the wizardManager
    */
   public WizardManager getWizardManager() {
      return wizardManager;
   }
   /**
    * @param wizardManager the wizardManager to set
    */
   public void setWizardManager(WizardManager wizardManager) {
      this.wizardManager = wizardManager;
   }
   /**
    * @return the siteTypes
    */
   public List getSiteTypes() {
      return siteTypes;
   }
   /**
    * @param siteTypes the siteTypes to set
    */
   public void setSiteTypes(List siteTypes) {
      this.siteTypes = siteTypes;
   }
   /**
    * @return the idManager
    */
   public IdManager getIdManager() {
      return idManager;
   }
   /**
    * @param idManager the idManager to set
    */
   public void setIdManager(IdManager idManager) {
      this.idManager = idManager;
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
   /**
    * @return the displayTypes
    */
   public List getDisplayTypes() {
      return displayTypes;
   }
   /**
    * @param displayTypes the displayTypes to set
    */
   public void setDisplayTypes(List displayTypes) {
      this.displayTypes = displayTypes;
   }
   /**
    * @return the matrixManager
    */
   public MatrixManager getMatrixManager() {
      return matrixManager;
   }
   /**
    * @param matrixManager the matrixManager to set
    */
   public void setMatrixManager(MatrixManager matrixManager) {
      this.matrixManager = matrixManager;
   }
}

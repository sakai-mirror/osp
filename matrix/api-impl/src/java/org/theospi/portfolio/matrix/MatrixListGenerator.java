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
package org.theospi.portfolio.matrix;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.sakaiproject.component.cover.ServerConfigurationService;
import org.sakaiproject.metaobj.security.AuthenticationManager;
import org.sakaiproject.metaobj.shared.mgt.IdManager;
import org.sakaiproject.metaobj.shared.model.Id;
import org.sakaiproject.metaobj.worksite.mgt.WorksiteManager;
import org.sakaiproject.site.api.Site;
import org.sakaiproject.site.api.ToolConfiguration;
import org.sakaiproject.user.api.UserNotDefinedException;
import org.theospi.portfolio.list.impl.BaseListGenerator;
import org.theospi.portfolio.list.intf.ActionableListGenerator;
import org.theospi.portfolio.list.intf.CustomLinkListGenerator;
import org.theospi.portfolio.matrix.model.Scaffolding;
import org.theospi.portfolio.security.AuthorizationFacade;
import org.theospi.portfolio.shared.model.SortableListObject;

public class MatrixListGenerator extends BaseListGenerator implements ActionableListGenerator, CustomLinkListGenerator {
   private MatrixManager matrixManager;
   private static final String SITE_ID_PARAM = "selectedSiteId";
   private static final String MATRIX_ID_PARAM = "matrixId";

   private WorksiteManager worksiteManager;
   private AuthenticationManager authnManager;
   private IdManager idManager;
   private AuthorizationFacade authzManager;
   private List siteTypes;

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

      List matrices = new ArrayList();
      List userSites = getWorksiteManager().getUserSites(null, getSiteTypes());
      List siteIds = new ArrayList(userSites.size());
      Map siteMap = new HashMap();
      
      for (Iterator i = userSites.iterator(); i.hasNext();) {
         Site site = (Site) i.next();
         Id siteId = getIdManager().getId(site.getId());
         siteIds.add(siteId);
         siteMap.put(siteId, site);
      }
      
      List tempMatrixList = new ArrayList(getMatrixManager().findPublishedScaffolding(siteIds));
      
      //Need to make sure the current user can actually have one of their own here, 
      // so only check if they can "use"
      
      for (Iterator i = tempMatrixList.iterator(); i.hasNext();) {
         Scaffolding scaffolding = (Scaffolding)i.next();
         
         //make sure that the target site gets tested
         getAuthzManager().pushAuthzGroups(scaffolding.getWorksiteId().getValue());
         
         if (getAuthzManager().isAuthorized(MatrixFunctionConstants.USE_SCAFFOLDING, 
               scaffolding.getWorksiteId())) {
            Site site = (Site)siteMap.get(scaffolding.getWorksiteId());
            SortableListObject scaff;
            try {
               scaff = new SortableListObject(scaffolding.getId(), 
                     scaffolding.getTitle(), scaffolding.getDescription(), 
                     scaffolding.getOwner(), site, MatrixFunctionConstants.SCAFFOLDING_PREFIX);
               matrices.add(scaff);
            } catch (UserNotDefinedException e) {
               logger.warn("User with id " + scaffolding.getOwner().getId() + " does not exist.");
            }
            
         }
      }

      return matrices;
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
      
      String urlEnd = "/viewMatrix.osp?1=1&scaffolding_id=" + obj.getId().getValue();
      
      ToolConfiguration toolConfig = obj.getSite().getToolForCommonId("osp.matrix");
      String placement = toolConfig.getId();
      
      //String url = ServerConfigurationService.getPortalUrl() + "/site/" + toolConfig.getSiteId() + "/page/" + toolConfig.getPageId() + "/tool/" + placement + urlEnd;
      String url = ServerConfigurationService.getPortalUrl() + "/directtool/" + placement + urlEnd;
      
      return url;
      //<osp:url value="viewMatrix.osp"/>&scaffolding_id=<c:out value="${scaffold.id.value}" />
      //http://localhost:8080/portal/tool/bce2c347-d9c1-4fcd-8052-f8aaf9432e5e/viewMatrix.osp?1=1&scaffolding_id=A45C4EFD67AB6E514F0127D025163E5D
      //http://localhost:8080/portal/tool/bce2c347-d9c1-4fcd-8052-f8aaf9432e5e/viewMatrix.osp?1=1&scaffolding_id=A45C4EFD67AB6E514F0127D025163E5D
   }
   
   public Map getToolParams(Object entry) {
      Map params = new HashMap();
      SortableListObject obj = (SortableListObject) entry;
      params.put(MATRIX_ID_PARAM, obj.getId());
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
}

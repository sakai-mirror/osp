
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
package org.theospi.portfolio.matrix.control;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.List;

import org.sakaiproject.component.cover.ServerConfigurationService;
import org.sakaiproject.content.api.ContentHostingService;
import org.sakaiproject.entity.api.EntityManager;
import org.sakaiproject.metaobj.security.AuthenticationManager;
import org.sakaiproject.metaobj.shared.model.Id;
import org.sakaiproject.metaobj.utils.mvc.intf.CustomCommandController;
import org.sakaiproject.metaobj.utils.mvc.intf.FormController;
import org.sakaiproject.metaobj.worksite.mgt.WorksiteManager;
import org.sakaiproject.tool.api.SessionManager;
import org.springframework.validation.Errors;
import org.springframework.web.servlet.ModelAndView;
import org.theospi.portfolio.matrix.MatrixFunctionConstants;
import org.theospi.portfolio.matrix.model.Scaffolding;
import org.theospi.portfolio.matrix.model.ScaffoldingCell;
import org.theospi.portfolio.matrix.model.Matrix;
import org.theospi.portfolio.matrix.model.WizardPage;
import org.theospi.portfolio.matrix.model.Cell;
import org.theospi.portfolio.review.mgt.ReviewManager;


/**
 * @author chmaurer
 */
public class AddScaffoldingController extends BaseScaffoldingController 
   implements FormController, CustomCommandController {

   private WorksiteManager worksiteManager = null;
   private AuthenticationManager authManager = null;
   private SessionManager sessionManager;
   private ContentHostingService contentHosting;
   private EntityManager entityManager;
   private ReviewManager reviewManager;


   /* (non-Javadoc)
    * @see org.theospi.utils.mvc.intf.FormController#referenceData(java.util.Map, java.lang.Object, org.springframework.validation.Errors)
    */
   public Map referenceData(Map request, Object command, Errors errors) {
      Map model = new HashMap();

      model.put("isInSession", EditedScaffoldingStorage.STORED_SCAFFOLDING_FLAG);
      
      Scaffolding scaffolding = null;
      if ( command instanceof Scaffolding )
         scaffolding = (Scaffolding)command;
      
      if ( scaffolding != null )
         model.put("isMatrixUsed", scaffolding.isPublished() && isMatrixUsed( scaffolding.getId() ) );
      else
         model.put("isMatrixUsed", false );
      
      model.put("ignoreReviewerGroups", ServerConfigurationService.getBoolean(MatrixFunctionConstants.PROP_GROUPS_ALLOW_ALL_GLOBAL, false));
      
      return model;
   }
   

   /* (non-Javadoc)
    * @see org.theospi.utils.mvc.intf.Controller#handleRequest(java.lang.Object, java.util.Map, java.util.Map, java.util.Map, org.springframework.validation.Errors)
    */
   public ModelAndView handleRequest(Object requestModel, Map request, Map session, Map application, Errors errors) {
      String action = (String) request.get("action");
      if (action == null) action = (String) request.get("submitAction");
      String generateAction = (String)request.get("generateAction");
      String cancelAction = (String)request.get("cancelAction");
      
      Id worksiteId = worksiteManager.getCurrentWorksiteId();
      Map model = new HashMap();
      
      EditedScaffoldingStorage sessionBean = (EditedScaffoldingStorage)session.get(
            EditedScaffoldingStorage.EDITED_SCAFFOLDING_STORAGE_SESSION_KEY);
      Scaffolding scaffolding = sessionBean.getScaffolding();
      scaffolding.setWorksiteId(worksiteId);
      
      scaffolding.setOwner(authManager.getAgent());
      
      if (generateAction != null) {
         if (scaffolding.isPublished()) {                              
            return new ModelAndView("editScaffoldingConfirm");             
         }           
         
         scaffolding = saveScaffolding(scaffolding);
         session.remove(EditedScaffoldingStorage.STORED_SCAFFOLDING_FLAG);
         session.remove(EditedScaffoldingStorage.EDITED_SCAFFOLDING_STORAGE_SESSION_KEY);
         model.put("scaffolding_id", scaffolding.getId());
         return new ModelAndView("view", model);
      }
      if (cancelAction != null) {
         return new ModelAndView("return");
      }
      
      if (action != null) {
         if (action.equals("forward")) {
            String forwardView = (String)request.get("dest");
            model.put("label", request.get("label"));
            model.put("finalDest", request.get("finalDest"));
            model.put("displayText", request.get("displayText"));
            String params = (String)request.get("params");
            model.put("params", params);
            if (!params.equals("")) {
               String[] paramsList = params.split(":");
               for (int i=0; i<paramsList.length; i++) {
                  String[] pair = paramsList[i].split("=");
                  String val = null;
                  if (pair.length>1)
                     val = pair[1];
                  model.put(pair[0], val);
               }
            }
            //matrixManager.storeScaffolding(scaffolding);
            
            //touchAllCells(scaffolding);
            sessionBean.setScaffolding(scaffolding);
            model.put("scaffolding_id", scaffolding.getId());
            
            return new ModelAndView(forwardView, model);
            
         }
      }
      return new ModelAndView("success");
   }
/*
   private void touchAllScaffolding(Scaffolding scaffolding) {
	  scaffolding.getLevels().size();
 	  scaffolding.getCriteria().size();
 	 for (Iterator iter = scaffolding.getScaffoldingCells().iterator(); iter.hasNext();) {
         ScaffoldingCell sCell = (ScaffoldingCell) iter.next();
         sCell.getCells().size();
         //sCell.getExpectations().size();
      }
   }
*/
   protected void touchAllCells(Scaffolding scaffolding) {
      for (Iterator iter = scaffolding.getScaffoldingCells().iterator(); iter.hasNext();) {
         ScaffoldingCell sCell = (ScaffoldingCell) iter.next();
         sCell.getCells().size();
      }
      
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
    * @return Returns the authManager.
    */
   public AuthenticationManager getAuthManager() {
      return authManager;
   }
   /**
    * @param authManager The authManager to set.
    */
   public void setAuthManager(AuthenticationManager authManager) {
      this.authManager = authManager;
   }

   public SessionManager getSessionManager() {
      return sessionManager;
   }

   public void setSessionManager(SessionManager sessionManager) {
      this.sessionManager = sessionManager;
   }


   public ContentHostingService getContentHosting() {
      return contentHosting;
   }


   public void setContentHosting(ContentHostingService contentHosting) {
      this.contentHosting = contentHosting;
   }

   public EntityManager getEntityManager() {
      return entityManager;
   }

   public void setEntityManager(EntityManager entityManager) {
      this.entityManager = entityManager;
   }
   
   /**
    * @return Returns the reviewManager.
    */
   public ReviewManager getReviewManager() {
      return reviewManager;
   }

   /**
    * @param reviewManager The reviewManager to set.
    */
   public void setReviewManager(ReviewManager reviewManager) {
      this.reviewManager = reviewManager;
   }

   /**
    ** Determine if any matrix with the specified scaffoldingId has been 'used'
    ** (e.g. containing reflections and/or added form items)
    **/
   private boolean isMatrixUsed( Id scaffoldingId ) 
   {
      List matrices = getMatrixManager().getMatrices(scaffoldingId);
   
      for (Iterator matrixIt = matrices.iterator(); matrixIt.hasNext();) 
      {
         Matrix matrix = (Matrix)matrixIt.next();
         Set cells = matrix.getCells();
       
         for (Iterator cellIt=cells.iterator(); cellIt.hasNext();) 
         {
            Cell cell = (Cell)cellIt.next();
            WizardPage wizardPage = cell.getWizardPage();
				String pageId = wizardPage.getId().getValue();
            if ( wizardPage.getReflections() != null && wizardPage.getReflections().size() > 0 )
               return true;
            if ( wizardPage.getPageForms() != null && wizardPage.getPageForms().size() > 0 )
               return true;
            if ( wizardPage.getAttachments() != null && wizardPage.getAttachments().size() > 0 )
               return true;
				if ( reviewManager.getReviewsByParent(pageId) != null && reviewManager.getReviewsByParent(pageId).size() > 0 )
					return true;
				// note: wizardPage.[get|set]Feedback() does not appear to be used
         }
      }
      
      return false;
   }
}

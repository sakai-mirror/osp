/**********************************************************************************
* $URL:https://source.sakaiproject.org/svn/osp/trunk/presentation/tool/src/java/org/theospi/portfolio/presentation/control/DeletePresentationController.java $
* $Id:DeletePresentationController.java 9134 2006-05-08 20:28:42Z chmaurer@iupui.edu $
***********************************************************************************
*
 * Copyright (c) 2008 Sakai Foundation
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.osedu.org/licenses/ECL-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*
**********************************************************************************/
package org.theospi.portfolio.presentation.control;

import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.HashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.metaobj.utils.mvc.intf.Controller;
import org.sakaiproject.metaobj.shared.model.Agent;
import org.theospi.portfolio.security.Authorization;
import org.springframework.validation.Errors;
import org.springframework.web.servlet.ModelAndView;
import org.theospi.portfolio.presentation.model.Presentation;
import org.theospi.portfolio.security.AudienceSelectionHelper;

import org.sakaiproject.tool.api.Tool;
import org.sakaiproject.component.api.ServerConfigurationService;
import org.sakaiproject.tool.cover.SessionManager;
import org.sakaiproject.tool.api.Session;

/**
 **/
public class SharePresentationController extends AbstractPresentationController implements Controller {
   protected final Log logger = LogFactory.getLog(getClass());

   private ServerConfigurationService serverConfigurationService;
   
   private final String SHARE_PUBLIC  = "pres_share_public";
   private final String SHARE_SELECT  = "pres_share_select";
   
   private final String SHARE_LIST_ATTRIBUTE   = "org.theospi.portfolio.presentation.control.SharePresentationController.shareList";
   private final String SHARE_PUBLIC_ATTRIBUTE = "org.theospi.portfolio.presentation.control.SharePresentationController.public";
   
   public ModelAndView handleRequest(Object requestModel, Map request, Map session, Map application, Errors errors) {
      Map model = new HashMap();
      Presentation presentation = (Presentation) requestModel;
      presentation = getPresentationManager().getPresentation(presentation.getId());
      
      // Check if cancel request (TBD: change this to an UNDO request)
      String cancel = (String) request.get("undo");
      if (cancel != null)
      {
         cleanSessionAttributes(presentation);
         return new ModelAndView("undo");
      }
      
      // determine share status
      if ( getIsPublic(request, presentation) )
         model.put(SHARE_PUBLIC, "true");
      else
         model.put(SHARE_PUBLIC, "false");
         
      List revisedShareList = getRevisedShareList( request, presentation );
      if ( revisedShareList.size() > 0 )
         model.put(SHARE_SELECT, "true");
      else
         model.put(SHARE_SELECT, "false");
      
      model.put("presentation", presentation);
      model.put("publicUrl", getPublicUrl(presentation));
      model.put("shareList", revisedShareList );
      
      // If save not requested, then display share page
      if ( request.get("save") == null )
         return new ModelAndView("share", model);
         
      // Otherwise, save presentation share status
      if ( request.get(SHARE_PUBLIC) != null )
         presentation.setIsPublic(true);
      else
         presentation.setIsPublic(false);
         
      getPresentationManager().storePresentation(presentation);
      
      // Save updated share list
      saveRevisedShareList( revisedShareList, presentation );
      cleanSessionAttributes(presentation);
      
      return new ModelAndView("success");
   }
   
   /** 
    ** Get session-based state of isPublic flag for this presentation
    **/
   private boolean getIsPublic( Map request, Presentation presentation ) {
      Session session = SessionManager.getCurrentSession();
      boolean isPublic = false;
      
      // if no session attribute, then pull status from presentation
      if ( session.getAttribute(SHARE_PUBLIC_ATTRIBUTE+presentation.getId().getValue()) == null ) {
         isPublic = presentation.getIsPublic();
      }
      
      // otherwise, get revised status from form
      else {
         if ( request.get(SHARE_PUBLIC) == null )
            isPublic = false;
         else
            isPublic = true;
      }
      
      session.setAttribute(SHARE_PUBLIC_ATTRIBUTE+presentation.getId().getValue(), String.valueOf(isPublic));
      return isPublic;
   }
   
   /**
    ** Get authorized share list from the database for this portfolio, return a list of Agent objects
    **/
   private List getShareList( Presentation presentation ) {
      List authzList = getAuthzManager().getAuthorizations(null, 
                                                           AudienceSelectionHelper.AUDIENCE_FUNCTION_PORTFOLIO, 
                                                           presentation.getId() );
               
      ArrayList shareList = new ArrayList(authzList.size());                                            
      for (Iterator it=authzList.iterator(); it.hasNext(); ) {
         Agent member = ((Authorization)it.next()).getAgent();
         shareList.add( member );
      }
      
      return shareList;
   }
    
   /**
    ** get session-based share list and remove selected-for-removal members 
    **/
   private List getRevisedShareList( Map request, Presentation presentation ) {
   
      Session session = SessionManager.getCurrentSession();
      
      List shareList = (List)session.getAttribute(SHARE_LIST_ATTRIBUTE+presentation.getId().getValue());
      if ( shareList == null )
         shareList = getShareList( presentation );
   
      List revisedShareList = new ArrayList();
      for (Iterator it=shareList.iterator(); it.hasNext(); ) {
         Agent member = (Agent)it.next();
         if ( request.get(member.getId().getValue()) == null )
              revisedShareList.add( member );
      }
      
      session.setAttribute(SHARE_LIST_ATTRIBUTE+presentation.getId().getValue(), revisedShareList);
      return revisedShareList;
   }
   
   /** Compare orignal and revised shared list of users (or roles); deleting/adding as necessary
    ** to authorization list in the database.
    **/
   private void saveRevisedShareList( List revisedShareList, Presentation presentation ) {
   
      List origShareList = getShareList( presentation );
      
      // Setup hashmap of revisedShareList
      HashMap revisedHash = new HashMap( revisedShareList.size() );
      for (Iterator it=revisedShareList.iterator(); it.hasNext(); ) {
         Agent member = (Agent)it.next();
         revisedHash.put( member.getId().getValue(), member );
      }
      
      // Setup hashmap of origShareList
      HashMap originalHash = new HashMap( origShareList.size() );
      for (Iterator it=origShareList.iterator(); it.hasNext(); ) {
         Agent member = (Agent)it.next();
         originalHash.put( member.getId().getValue(), member );
         
         // Check for deletions from original shareList
         if ( ! revisedHash.containsKey(member.getId().getValue()) )
            getAuthzManager().deleteAuthorization(member,  
                                                  AudienceSelectionHelper.AUDIENCE_FUNCTION_PORTFOLIO,
                                                  presentation.getId() );
      }
      
      // tbd check for additions
   }
   
   /** Construct public url for given portfolio presentation
    **/
   private String getPublicUrl( Presentation presentation ) {
      String baseUrl = getServerConfigurationService().getServerUrl();
      String url =  baseUrl + "/osp-presentation-tool/viewPresentation.osp?id=" + presentation.getId().getValue();
      url += "&" + Tool.PLACEMENT_ID + "=" + SessionManager.getCurrentToolSession().getPlacementId();
      return url;
   }
   
   private void cleanSessionAttributes( Presentation presentation ) {
      Session session = SessionManager.getCurrentSession();
      session.removeAttribute(SHARE_LIST_ATTRIBUTE+presentation.getId().getValue());
      session.removeAttribute(SHARE_PUBLIC_ATTRIBUTE+presentation.getId().getValue());
   }
   
   public ServerConfigurationService getServerConfigurationService() {
      return serverConfigurationService;
   }

   public void setServerConfigurationService(
         ServerConfigurationService serverConfigurationService) {
      this.serverConfigurationService = serverConfigurationService;
   }

}

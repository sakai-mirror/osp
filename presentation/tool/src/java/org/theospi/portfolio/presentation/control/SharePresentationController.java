/**********************************************************************************
* $URL:https://source.sakaiproject.org/svn/osp/trunk/presentation/tool/src/java/org/theospi/portfolio/presentation/control/DeletePresentationController.java $
* $Id:DeletePresentationController.java 9134 2006-05-08 20:28:42Z chmaurer@iupui.edu $
***********************************************************************************
*
 * Copyright (c) 2008, 2009 The Sakai Foundation
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
import java.util.Set;
import java.util.HashMap;
import java.util.Collections;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.metaobj.utils.mvc.intf.Controller;
import org.sakaiproject.metaobj.shared.model.Agent;
import org.theospi.portfolio.security.Authorization;
import org.springframework.validation.Errors;
import org.springframework.web.servlet.ModelAndView;
import org.theospi.portfolio.presentation.model.Presentation;
import org.theospi.portfolio.security.AudienceSelectionHelper;
import org.theospi.portfolio.presentation.intf.FreeFormHelper;

import org.sakaiproject.tool.api.ToolSession;
import org.sakaiproject.tool.api.Tool;
import org.sakaiproject.component.api.ServerConfigurationService;
import org.sakaiproject.tool.cover.SessionManager;
import org.sakaiproject.tool.api.Session;
import org.sakaiproject.entity.api.Reference;
import org.sakaiproject.exception.IdUnusedException;
import org.sakaiproject.entity.cover.EntityManager;
import org.sakaiproject.site.cover.SiteService;
import org.sakaiproject.user.api.User;
import org.sakaiproject.user.api.UserDirectoryService;
import org.sakaiproject.authz.api.Member;
import org.sakaiproject.authz.api.Role;
import org.sakaiproject.site.api.Site;
import org.sakaiproject.email.api.EmailService; 
import org.sakaiproject.util.ResourceLoader;
import org.sakaiproject.entity.api.Reference;

/**
 **/
public class SharePresentationController extends AbstractPresentationController implements Controller {
   protected final Log logger = LogFactory.getLog(getClass());

   private ServerConfigurationService serverConfigurationService;
   private UserAgentComparator userAgentComparator = new UserAgentComparator();
   private EmailService emailService;
   private UserDirectoryService userDirectoryService;
	
   private ResourceLoader rl = new ResourceLoader("org.theospi.portfolio.presentation.bundle.Messages");
   
   private final String SHARE_PUBLIC  = "pres_share_public";
   private final String SHARE_SELECT  = "pres_share_select";
   
   public final static String SHARE_LIST_ATTRIBUTE   = "org.theospi.portfolio.presentation.control.SharePresentationController.shareList";
   public final static String SHARE_PUBLIC_ATTRIBUTE = "org.theospi.portfolio.presentation.control.SharePresentationController.public";
   
   public ModelAndView handleRequest(Object requestModel, Map request, Map session, Map application, Errors errors) {
      Map model = new HashMap();
      Presentation presentation = (Presentation) requestModel;
      
      // Get presentation from tool session if returning from free-form helper
      if (presentation == null || presentation.getId() == null )
      {
         ToolSession toolSession = SessionManager.getCurrentToolSession();
         presentation = (Presentation)toolSession.getAttribute(FreeFormHelper.FREE_FORM_PREFIX + "presentation");
      }
      
      // otherwise, load presentation specified by given id
      else
      {
         presentation = getPresentationManager().getPresentation(presentation.getId());
      }

      // Check if request to edit free-form content
      if ( presentation.getIsFreeFormType() && 
           request.get("freeFormContent")!= null && 
           request.get("freeFormContent").equals("true") )
      {
         ToolSession toolSession = SessionManager.getCurrentToolSession();
         toolSession.setAttribute(FreeFormHelper.FREE_FORM_PREFIX + "presentation", presentation);
         return new ModelAndView("freeFormPresentationRedirect");
      }
      
      // Check if Undo request 
      if (request.get("undo") != null)
      {
         cleanSessionAttributes(presentation);
         model.put("actionUndo", true );
      }
      
      // determine share status
      if ( getIsPublic(request, presentation) )
         model.put(SHARE_PUBLIC, "true");
      else
         model.put(SHARE_PUBLIC, "false");
         
      List revisedShareList = getRevisedShareList( request, presentation );
      if ( revisedShareList.size() > 0 || request.get(SHARE_SELECT)!=null )
         model.put(SHARE_SELECT, "true");
      else
         model.put(SHARE_SELECT, "false");
      
      model.put("presentation", presentation);
      model.put("publicUrl", getPublicUrl(presentation));
      model.put("shareList", revisedShareList );
      
      // If save not requested, then display share page
      if ( request.get("save") == null && request.get("notify") == null )
         return new ModelAndView("share", model);
         
      // Otherwise, save presentation share status
      if ( request.get(SHARE_PUBLIC) != null )
         presentation.setIsPublic(true);
      else
         presentation.setIsPublic(false);
         
      getPresentationManager().storePresentation(presentation);
      
      // Save updated share list
      saveRevisedShareList( revisedShareList, presentation );

      // Notify users if requested
      if ( request.get("notify") != null )
         notifyViewers(presentation, revisedShareList);
      
      model.put("actionSave", true );
      return new ModelAndView("save", model);
   }
   
   public void notifyViewers( Presentation presentation, List revisedShareList ) { 
        User user = userDirectoryService.getCurrentUser();

        String url = getPublicUrl( presentation );

        //  TBD: email template service
        String subject = rl.getFormattedMessage("email.notify.subject", new Object[]{user.getDisplayName()});
		  
        StringBuffer message = new StringBuffer();
		  message.append( rl.getFormattedMessage("email.notify.msg1", 
                                                new Object[]{getServerConfigurationService().getString("ui.service","Sakai")}) );
        message.append("\n\n");
		  message.append( rl.getFormattedMessage("email.notify.msg2", 
                                                new Object[]{user.getDisplayName(), user.getEmail()}) );
        message.append("\n\n");
		  message.append( rl.getFormattedMessage("email.notify.msg3", 
                                                new Object[]{presentation.getName()}) );
        message.append("\n\n");
		  message.append( rl.getFormattedMessage("email.notify.msg4", 
                                                new Object[]{url}) );
        message.append("\n\n");
		  
        String emailFrom = getServerConfigurationService().getString("setup.request", 
																							"postmaster@".concat(getServerConfigurationService().getServerName()));
        
        for (Iterator it=revisedShareList.iterator(); it.hasNext(); ) 
        {
           try
           {
              Agent shareMember = (Agent)it.next();
              if ( shareMember.isRole() )
              {
					  Reference r = EntityManager.newReference( shareMember.getRole() );
                 Site site = SiteService.getSite( r.getId() );
                 Set members = site.getMembers();
                 for (Iterator j = members.iterator(); j.hasNext();) 
                 {
                    Member member = (Member) j.next();
                    if ( shareMember.getRole().contains( member.getRole().getId() )) 
                    {
                       String emailTo = userDirectoryService.getUser(member.getUserId()).getEmail();
                       if ( SharePresentationMoreController.emailPattern.matcher(emailTo).matches() )
                          emailService.send(emailFrom, emailTo, subject, message.toString(), emailTo, null, null);
                    }
                 }
              }
              else 
              {
                 String emailTo = userDirectoryService.getUser(shareMember.getId().getValue()).getEmail();
                 if ( SharePresentationMoreController.emailPattern.matcher(emailTo).matches() )
                    emailService.send(emailFrom, emailTo, subject, message.toString(), emailTo, null, null);
              }
           }
           catch ( Exception e ) 
           {
              logger.warn(e.toString());
           }
        }
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
         Agent agent = ((Authorization)it.next()).getAgent();
         if ( agent.isRole() ) {
            String worksiteName = getSiteFromRoleMember(agent.getId().getValue());
            agent = new AgentWrapper( agent, worksiteName );
         }
         shareList.add( agent );
      }
      
      return shareList;
   }

   /**
    ** Parse role id and return Site title
    **/
    private String getSiteFromRoleMember( String roleMember ) {
       Reference ref = EntityManager.newReference( roleMember );
       String siteId = ref.getContainer();
       String siteTitle = "";
       try {
          siteTitle = SiteService.getSite(siteId).getTitle();
       }
       catch (IdUnusedException e) {
          logger.warn(e.toString());
       }
            
       return siteTitle;
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
      
      Collections.sort(revisedShareList, userAgentComparator);
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
      
      // Setup hashmap of origShareList and check for deletions
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
      
      // Check for additions to original shareList
      for (Iterator it=revisedShareList.iterator(); it.hasNext(); ) {
         Agent member = (Agent)it.next();
         if ( ! originalHash.containsKey(member.getId().getValue()) )
            getAuthzManager().createAuthorization(member,  
                                                  AudienceSelectionHelper.AUDIENCE_FUNCTION_PORTFOLIO,
                                                  presentation.getId() );
      }
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

   public EmailService getEmailService() {
      return emailService;
   }

   public void setEmailService( EmailService emailService ) {
      this.emailService = emailService;
   }
   
   public UserDirectoryService getUserDirectoryService() {
      return userDirectoryService;
   }

   public void setUserDirectoryService( UserDirectoryService userDirectoryService) {
      this.userDirectoryService = userDirectoryService;
   }
   
}

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
import java.util.Set;
import java.util.HashMap;
import java.util.Comparator;
import java.util.Collections;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.metaobj.utils.mvc.intf.Controller;
import org.sakaiproject.metaobj.shared.model.Id;
import org.sakaiproject.metaobj.shared.model.Agent;
import org.sakaiproject.metaobj.shared.mgt.AgentManager;
import org.theospi.portfolio.security.Authorization;
import org.springframework.validation.Errors;
import org.springframework.web.servlet.ModelAndView;
import org.theospi.portfolio.presentation.model.Presentation;
import org.theospi.portfolio.security.AudienceSelectionHelper;

import org.sakaiproject.authz.api.Member;
import org.sakaiproject.authz.api.Role;
import org.sakaiproject.site.api.Site;
import org.sakaiproject.site.api.SiteService;
import org.sakaiproject.component.api.ServerConfigurationService;
import org.sakaiproject.tool.cover.SessionManager;
import org.sakaiproject.tool.api.Session;

/**
 **/
public class SharePresentationMoreController extends AbstractPresentationController implements Controller {
   protected final Log logger = LogFactory.getLog(getClass());
   private ServerConfigurationService serverConfigurationService;
   private SiteService siteService;
   
   private UserAgentComparator userAgentComparator = new UserAgentComparator();
   private RoleAgentComparator roleAgentComparator = new RoleAgentComparator();
   
   private final String SHARE_PUBLIC  = "pres_share_public";
   private final String SHARE_SELECT  = "pres_share_select";
   
   private final String SHARE_LIST_ATTRIBUTE   = "org.theospi.portfolio.presentation.control.SharePresentationController.shareList";
   private final String SHARE_PUBLIC_ATTRIBUTE = "org.theospi.portfolio.presentation.control.SharePresentationController.public";
   
   private final String SHAREBY_KEY    = "shareBy";
   private final String SHAREBY_BROWSE = "share_browse";
   private final String SHAREBY_GROUP = "share_group";
   private final String SHAREBY_SEARCH = "share_search";
   private final String SHAREBY_EMAIL  = "share_email";
   private final String SHAREBY_ROLE   = "share_role";
   private final String SHAREBY_ALLROLE= "share_allrole";
   
   public ModelAndView handleRequest(Object requestModel, Map request, Map session, Map application, Errors errors) {
      // Get specified portfolio/presentation      
      Map model = new HashMap();
      Presentation presentation = (Presentation) requestModel;
      presentation = getPresentationManager().getPresentation(presentation.getId());
      model.put("id", presentation.getId().getValue());
      
      // Check if request to return to previous page
      if ( request.get("back") != null )
         return new ModelAndView("back", model);
         
      model.put("presentation", presentation);
      model.put("hasGroups", getHasGroups(presentation));
      model.put("guestEnabled", getGuestUserEnabled());
      
      String shareBy = (String)request.get(SHAREBY_KEY);
      if ( shareBy==null || shareBy.equals("") )
         shareBy = SHAREBY_BROWSE;
      model.put(SHAREBY_KEY, shareBy);

      // Update list of Shared-with Users         
      List shareList = getShareList(presentation);
      
      boolean isUpdated = false;
      if (shareBy.equals(SHAREBY_SEARCH) ) // tbd
      {
      }
      else if (shareBy.equals(SHAREBY_EMAIL) ) // tbd
      {
         
      }
      else if ( shareBy.equals(SHAREBY_BROWSE) || shareBy.equals(SHAREBY_GROUP) )
      {
         if ( shareBy.equals(SHAREBY_GROUP) ) ; // tbd add groups
         
         List availList = getAvailableUserList(shareBy, presentation, shareList);
         isUpdated = updateAvailList( shareBy, request, presentation, shareList, availList );
         model.put("availList", availList );
      }
      else if ( shareBy.equals(SHAREBY_ROLE) || shareBy.equals(SHAREBY_ALLROLE) )
      {
         List availList = getAvailableRoleList(shareBy, presentation, shareList);
         isUpdated = updateAvailList( shareBy, request, presentation, shareList, availList );
         model.put("availList", availList );
      }
      
      model.put("isUpdated", String.valueOf(isUpdated) );
      return new ModelAndView("share", model);
   }

   /**
    ** get session-based share list
    **/
   private List getShareList( Presentation presentation ) {
      Session session = SessionManager.getCurrentSession();
      List shareList = (List)session.getAttribute(SharePresentationController.SHARE_LIST_ATTRIBUTE+presentation.getId().getValue());
      return shareList;
   }
   
   /**
    ** set session-based share list
    **/
   private void setShareList( Presentation presentation, List shareList ) {
      Session session = SessionManager.getCurrentSession();
      session.setAttribute(SharePresentationController.SHARE_LIST_ATTRIBUTE+presentation.getId().getValue(), shareList);
   }
   
   /** 
    ** Check if adding user by email is enabled/disabled
    **/   
   private String getGuestUserEnabled() {
      if ( getServerConfigurationService().getBoolean("notifyNewUserEmail",true) )
         return String.valueOf(true);
      else
         return String.valueOf(false);
   }

   /**
    ** Check if presentation's worksite has groups defined and return String.valueOf(boolean)
    **/
   private String getHasGroups( Presentation presentation ) {
      try {
         Site site = getSiteService().getSite(presentation.getSiteId());
         return String.valueOf( site.hasGroups() );
      }
      catch (Exception e) {
         logger.warn(e.toString());
      }
      return String.valueOf(false);
   }
    
   /**
    ** Check for share list changes from form submission and update shareList and availList if necessary
    **
    ** @return true if update was necessary, otherwise false
    **/
   private boolean updateAvailList( String shareBy, Map request, Presentation presentation, List shareList, List availList ) {
      boolean mods = false;
      ArrayList selectedList = new ArrayList();
      ArrayList newAvailList = new ArrayList();
      
      if ( shareBy.equals(SHAREBY_SEARCH) )
      {
      }
      else if ( shareBy.equals(SHAREBY_EMAIL) )
      {
      }
      
      else if (shareBy.equals(SHAREBY_BROWSE) || shareBy.equals(SHAREBY_GROUP))
      {
         for (Iterator it = availList.iterator(); it.hasNext();) {
            Agent availItem = (Agent) it.next();
            if ( request.get(availItem.getId().getValue()) != null )
            {
               mods = true;
               selectedList.add( availItem );
            }
            else {
               newAvailList.add( availItem );
            }
         }
      }
      
      else // (shareBy.equals(SHAREBY_ROLE) || shareBy.equals(SHAREBY_ALLROLE))
      {
         for (Iterator it = availList.iterator(); it.hasNext();) {
            AgentWrapper availItem = (AgentWrapper) it.next();
            if ( request.get(availItem.getId().getValue()) != null )
            {
               mods = true;
               selectedList.add( availItem );
            }
            else {
               newAvailList.add( availItem );
            }
         }
      }
      
      if ( mods ) {
         // Add selected items to shareList and save
         shareList.addAll(selectedList);
         setShareList(presentation, shareList);
         
         // Delete selected items from availList
         availList.clear();
         availList.addAll(newAvailList);
      }
      
      return mods;
   }
   
   private List getFilteredMembersList( String shareBy, Presentation presentation ) {
      Set members = null;
      Site site = null;
      List memberList = new ArrayList();
      
      try {
         site = getSiteService().getSite(presentation.getSiteId());
      } 
      catch (Exception e) {
         logger.warn(e.toString());
         return memberList;
      }

      /* TBD      
      if ( selectedGroupFilter != null && !selectedGroupFilter.equals("") )
         members = getGroupMembers();
      else
      */
      members = site.getMembers(); // slow
      
      for (Iterator it = members.iterator(); it.hasNext();) {
         String userId = ((Member)it.next()).getUserId();
         
         // Check for a null agent since the site.getMembers() will return member records for deleted users
         Agent agent = getAgentManager().getAgent(userId);
         if (agent != null && agent.getId() != null) 
            memberList.add(agent);
      }
      
      return memberList;
   }

   private List getAvailableUserList( String shareBy, Presentation presentation, List shareList ) {
      ArrayList availableUserList = new ArrayList();

      ArrayList userMemberList = new ArrayList();
      userMemberList.addAll(getFilteredMembersList(shareBy, presentation));

      for (Iterator it1 = userMemberList.iterator(); it1.hasNext();) {
         Agent availableItem = (Agent)it1.next();
         boolean matchFound = false;
         
         for (Iterator it2 = shareList.iterator(); it2.hasNext();) {
            Agent selectedItem = (Agent) it2.next();
            if (selectedItem.getId().getValue().equals(availableItem.getId().getValue())) {
               matchFound = true;
               break;
            }
         }
         if (!matchFound){
            availableUserList.add(availableItem);
         }
      }
      
      Collections.sort(availableUserList, userAgentComparator);
      return availableUserList;
   }
   
   private List getAvailableRoleList( String shareBy, Presentation presentation, List shareList ) {
      ArrayList availableRoleList = new ArrayList();
      ArrayList roleMemberList = new ArrayList();
      
      if ( shareBy.equals(SHAREBY_ROLE) )
         roleMemberList.addAll(getRoles(false, presentation));
      else // (shareBy.equals(SHAREBY_ALLROLE)
         roleMemberList.addAll(getRoles(true, presentation));
      
      for (Iterator it1 = roleMemberList.iterator(); it1.hasNext();) {
         AgentWrapper availableItem = (AgentWrapper)it1.next();
         boolean matchFound = false;
         
         for (Iterator it2 = shareList.iterator(); it2.hasNext();) {
            Agent selectedItem = (Agent) it2.next();
            if (selectedItem.getId().getValue().equals(availableItem.getId().getValue())) {
               matchFound = true;
               break;
            }
         }
         if (!matchFound){
            availableRoleList.add(availableItem);
         }
      }
      
      Collections.sort(availableRoleList, roleAgentComparator);
      return availableRoleList;
   }
   
    /**
     ** Return list of roles for this or all worksites
     **/
   public List getRoles( boolean showAllSiteRoles, Presentation presentation ) {
        List roleList = new ArrayList();
        
        if ( !showAllSiteRoles ) {
           Site site = null;
           Set roles = null;
           
           try {
              site = getSiteService().getSite(presentation.getSiteId());
              roles = site.getRoles();
           }
           catch (Exception e) {
              logger.warn(e.toString());
              return roleList;
           }
           
           for (Iterator i = roles.iterator(); i.hasNext();) {
              Role role = (Role) i.next();
              Agent agent = getAgentManager().getWorksiteRole(role.getId(), site.getId());
              AgentWrapper roleAgent = new AgentWrapper( agent, site.getTitle() );
              roleList.add(roleAgent);
           }
        }
        
        else {
           List siteList = getSiteService().getSites(SiteService.SelectionType.ACCESS,
                                                     null, null, null, 
                                                     SiteService.SortType.TITLE_ASC, null);
                                                
           for (Iterator siteIt = siteList.iterator(); siteIt.hasNext();) {
              Site site = (Site)siteIt.next();
              Set roles = site.getRoles();

              for (Iterator roleIt = roles.iterator(); roleIt.hasNext();) {
                 Role role = (Role) roleIt.next();
                 Agent agent = getAgentManager().getWorksiteRole(role.getId(), site.getId());
                 AgentWrapper roleAgent = new AgentWrapper( agent, site.getTitle() );
                 roleList.add(roleAgent);
              }
           }
        }

        return roleList;
    }

   public ServerConfigurationService getServerConfigurationService() {
      return serverConfigurationService;
   }

   public void setServerConfigurationService(ServerConfigurationService serverConfigurationService) {
      this.serverConfigurationService = serverConfigurationService;
   }

   public SiteService getSiteService() {
      return siteService;
   }

   public void setSiteService( SiteService siteService) {
      this.siteService = siteService;
   }
   
   /**
    ** Comparator for sorting user-based Agent objects
    ** (tbd: localize sorting of names)
    **/
   public class UserAgentComparator implements Comparator<Agent> {
      
      public int compare(Agent o1, Agent o2) {
         String n1 = o1.getDisplayName();
         String n2 = o2.getDisplayName();
         int i1 = n1.lastIndexOf(" ");
         int i2 = n2.lastIndexOf(" ");
         if (i1 > 0)
            n1 = n1.substring(i1 + 1) + " " + n1.substring(0, i1);
         if (i2 > 0)
            n2 = n2.substring(i2 + 1) + " " + n2.substring(0, i2);
         
         return n1.compareToIgnoreCase(n2);
      }
   }
    
   /** 
    ** Comparator for sorting role-based Agent objects
    **/
	public class RoleAgentComparator implements Comparator<AgentWrapper> {
		public int compare(AgentWrapper o1, AgentWrapper o2) {
			return o1.getDisplayName().compareTo( o2.getDisplayName() );
		}
	}
}

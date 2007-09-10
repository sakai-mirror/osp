/**********************************************************************************
 * $URL:https://source.sakaiproject.org/svn/osp/trunk/common/tool-lib/src/java/org/theospi/portfolio/security/tool/AudienceTool.java $
 * $Id:AudienceTool.java 9134 2006-05-08 20:28:42Z chmaurer@iupui.edu $
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
package org.theospi.portfolio.security.tool;

import org.sakaiproject.util.ResourceLoader;
import org.sakaiproject.authz.api.Member;
import org.sakaiproject.authz.api.Role;
import org.sakaiproject.component.cover.ServerConfigurationService;
import org.sakaiproject.email.cover.EmailService;
import org.sakaiproject.exception.IdUnusedException;
import org.sakaiproject.metaobj.shared.mgt.AgentManager;
import org.sakaiproject.metaobj.shared.mgt.IdManager;
import org.sakaiproject.metaobj.shared.model.Agent;
import org.sakaiproject.metaobj.shared.model.Id;
import org.sakaiproject.site.api.Group;
import org.sakaiproject.site.api.Site;
import org.sakaiproject.site.api.SiteService;
import org.sakaiproject.tool.api.Placement;
import org.sakaiproject.tool.api.ToolManager;
import org.sakaiproject.tool.api.ToolSession;
import org.sakaiproject.tool.cover.SessionManager;
import org.sakaiproject.user.cover.UserDirectoryService;
import org.sakaiproject.entity.api.Reference;
import org.sakaiproject.entity.cover.EntityManager;

import org.theospi.portfolio.security.AudienceSelectionHelper;
import org.theospi.portfolio.security.Authorization;
import org.theospi.portfolio.security.AuthorizationFacade;
import org.theospi.portfolio.shared.model.AgentImplOsp;
import org.theospi.portfolio.shared.model.OspException;
import org.theospi.portfolio.shared.tool.HelperToolBase;
import org.theospi.portfolio.shared.tool.PagingList;

import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import java.util.*;
import java.util.regex.Pattern;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Nov 16, 2005
 * Time: 2:54:19 PM
 * To change this template use File | Settings | File Templates.
 */
public class AudienceTool extends HelperToolBase {

    private AuthorizationFacade authzManager;
    private IdManager idManager;
    private SiteService siteService;
    private ToolManager toolManager;
    private AgentManager agentManager;
    private int maxRoleMemberList;

    private List selectedMembers = null;
    private List originalMembers = null;
    private List selectedRoles = null;
    private String searchUsers;
    private String searchEmails;
    private Site site;

    /**
     * ***********************************
     */
    private String[] availableRoleArray;
    private List availableRoleList;

    private String[] selectedRoleArray;
    private List selectedRoleList;
    
    private String[] availableUserArray;
    private List availableUserList;

    private String[] selectedUserArray;
    private List selectedUserList;


    /**
     * **********************************
     */

    private String PRESENTATION_VIEWERS = "PRESENTATION_VIEWERS";

    private List selectedRolesFilter = null;
    private List selectedGroupsFilter = null;
    private PagingList browseUsers = null;
    private String stepString = "2";
    private String function;
    private Id qualifier;
    
    /** This accepts email addresses */
    private static final Pattern emailPattern = Pattern.compile(
          "^" +
             "(?>" +
                "\\.?[a-zA-Z\\d!#$%&'*+\\-/=?^_`{|}~]+" +
             ")+" + 
          "@" + 
             "(" +
                "(" +
                   "(?!-)[a-zA-Z\\d\\-]+(?<!-)\\." +
                ")+" +
                "[a-zA-Z]{2,}" +
             "|" +
                "(?!\\.)" +
                "(" +
                   "\\.?" +
                   "(" +
                      "25[0-5]" +
                   "|" +
                      "2[0-4]\\d" +
                   "|" +
                      "[01]?\\d?\\d" +
                   ")" +
                "){4}" +
             ")" +
          "$"
          );
    
    private List roleMemberList = null;
    private List groupMemberList = null;
    private String LIST_SEPERATOR = "__________________";
    private boolean publicAudience = false;



    /*************************************************************************/

    protected List getMembersList() {
        Set members = getSite().getMembers();
        List memberList = new ArrayList();
        for (Iterator i = members.iterator(); i.hasNext();) {
            Member member = (Member) i.next();

            Agent agent = getAgentManager().getAgent((member.getUserId()));
            //Check for a null agent since the site.getMembers() will return member records for deleted users
            if (agent != null && agent.getId() != null) {
               DecoratedMember decoratedMember = new DecoratedMember(this, agent);
               memberList.add(new SelectItem(decoratedMember.getBase().getId().getValue(), decoratedMember.getBase().getDisplayName(), "member"));
            }
        }

        return memberList;
    }

    public boolean isMaxList() {

        if (getMembersList().size() > getMaxRoleMemberList()) {
            return true;
        } else {
            return false;
        }
    }

    public int getMaxRoleMemberList() {
       return maxRoleMemberList;
    }

    public void setMaxRoleMemberList(int maxRoleMemberList) {
        this.maxRoleMemberList = maxRoleMemberList;
    }

    public List getSelectedMembers() {
        if (getAttribute(PRESENTATION_VIEWERS) == null) {
            selectedMembers = fillMemberList();
            setAttribute(PRESENTATION_VIEWERS, selectedMembers);
        }

        if (selectedMembers == null)
            selectedMembers = new ArrayList();
        return selectedMembers;
    }

    protected List fillMemberList() {
        List returned = new ArrayList();

        originalMembers = new ArrayList();

        String id = (String) getAttribute(AudienceSelectionHelper.AUDIENCE_QUALIFIER);
        setQualifier(getIdManager().getId(id));
        setFunction((String) getAttribute(AudienceSelectionHelper.AUDIENCE_FUNCTION));

        List authzs = getAuthzManager().getAuthorizations(null, getFunction(), getQualifier());

        for (Iterator i = authzs.iterator(); i.hasNext();) {
            Authorization authz = (Authorization) i.next();
            returned.add(new DecoratedMember(this, authz.getAgent()));
            originalMembers.add(authz.getAgent());
        }

        return returned;
    }

    public String getFunction() {
        return function;
    }

    public void setFunction(String function) {
        this.function = function;
    }

    public Id getQualifier() {
        return qualifier;
    }

    public void setQualifier(Id qualifier) {
        this.qualifier = qualifier;
    }

    public void setSelectedMembers(List selectedMembers) {
        this.selectedMembers = selectedMembers;
    }

    public String processActionCancel() {
        ToolSession session = SessionManager.getCurrentToolSession();
        session.setAttribute("target", getCancelTarget());
        clearAudienceSelectionVariables();
        processActionClearFilter();
        return returnToCaller();
    }

    public String processActionSaveNotify() {
        ToolSession session = SessionManager.getCurrentToolSession();
        session.setAttribute("target", getSaveNotifyTarget());
        save();
        clearAudienceSelectionVariables();
        processActionClearFilter();
        return returnToCaller();
    }

    public String processActionBack() {
        ToolSession session = SessionManager.getCurrentToolSession();
        session.setAttribute("target", getBackTarget());
        clearAudienceSelectionVariables();
        processActionClearFilter();
        return returnToCaller();
    }

    public AuthorizationFacade getAuthzManager() {
        return authzManager;
    }

    public void setAuthzManager(AuthorizationFacade authzManager) {
        this.authzManager = authzManager;
    }

    public IdManager getIdManager() {
        return idManager;
    }

    public String getCancelTarget() {
        return (String) getAttribute(AudienceSelectionHelper.AUDIENCE_CANCEL_TARGET);
    }

    public String getSaveTarget() {
        return (String) getAttribute(AudienceSelectionHelper.AUDIENCE_SAVE_TARGET);
    }

    public String getSaveNotifyTarget() {
        return (String) getAttribute(AudienceSelectionHelper.AUDIENCE_SAVE_NOTIFY_TARGET);
    }

    public String getBackTarget() {
        return (String) getAttribute(AudienceSelectionHelper.AUDIENCE_BACK_TARGET);
    }

    public void setIdManager(IdManager idManager) {
        this.idManager = idManager;
    }

    public String getAudienceFunction() {
        return (String) getAttributeOrDefault(AudienceSelectionHelper.AUDIENCE_FUNCTION);
    }
    
    public String getPublicURL() {
        return (String) getAttributeOrDefault(AudienceSelectionHelper.AUDIENCE_PUBLIC_URL);
    }

    public boolean isGuestUserEnabled() {
        if ( ServerConfigurationService.getBoolean("notifyNewUserEmail",true) )
           return true;
        else
           return false;
    }

    public boolean isPortfolioAudience() {
        if ( getAudienceFunction().equals(AudienceSelectionHelper.AUDIENCE_FUNCTION_PORTFOLIO) )
           return true;
        else
           return false;
    }

    public boolean isWizardAudience() {
        if ( getAudienceFunction().equals(AudienceSelectionHelper.AUDIENCE_FUNCTION_WIZARD) )
           return true;
        else
           return false;
    }

    public boolean isMatrixAudience() {
        if ( getAudienceFunction().equals(AudienceSelectionHelper.AUDIENCE_FUNCTION_MATRIX) )
           return true;
        else
           return false;
    }

    public boolean isWorksiteLimited() {
       if ( isPortfolioAudience() )
           return false;
        else
           return true;
    }

    public boolean isPublicAudience() {
        if (getAttribute(AudienceSelectionHelper.AUDIENCE_PUBLIC_FLAG) != null) {
            publicAudience =
                    "true".equalsIgnoreCase((String) getAttribute(AudienceSelectionHelper.AUDIENCE_PUBLIC_FLAG));
            removeAttribute(AudienceSelectionHelper.AUDIENCE_PUBLIC_FLAG);
        }
        return publicAudience;
    }

    public void setPublicAudience(boolean publicAudience) {
        this.publicAudience = publicAudience;
        setAttribute(AudienceSelectionHelper.AUDIENCE_PUBLIC_FLAG, publicAudience ? "true" : "false");
    }

    public String getSearchEmails() {
        return searchEmails;
    }

    public void setSearchEmails(String searchEmails) {
        this.searchEmails = searchEmails;
    }

    /**
     ** Return current site for this portfolio/matrix/wizard
     **/   
    public Site getSite() {
        if (site == null) {
            String currentSiteId = (String) getAttribute(AudienceSelectionHelper.AUDIENCE_SITE);
            try {
                site = getSiteService().getSite(currentSiteId);
            }
            catch (IdUnusedException e) {
                throw new RuntimeException(e);
            }
        }
        return site;
    }

    public ToolManager getToolManager() {
        return toolManager;
    }

    public void setToolManager(ToolManager toolManager) {
        this.toolManager = toolManager;
    }

    public SiteService getSiteService() {
        return siteService;
    }

    public void setSiteService(SiteService siteService) {
        this.siteService = siteService;
    }

    public List getSelectedRoles() {
        return selectedRoles;
    }

    public void setSelectedRoles(List selectedRoles) {
        this.selectedRoles = selectedRoles;
    }

    /**
     * This checks that the parameter does not contain the 
     * invalidEmailInIdAccountString string from sakai.properties
     * 
     * @param id String email address
     * @return boolean 
     */
    protected boolean isDomainAllowed(String email)
    {
       String invalidEmailInIdAccountString = ServerConfigurationService.getString("invalidEmailInIdAccountString", null);
       
       if(invalidEmailInIdAccountString != null) {
          String[] invalidDomains = invalidEmailInIdAccountString.split(",");
          
          for(int i = 0; i < invalidDomains.length; i++) {
             String domain = invalidDomains[i].trim();
             
             if(email.toLowerCase().indexOf(domain.toLowerCase()) != -1) {
                return false;
             }
          }
       }
       return true;
    }

    public String processActionAddEmailUser() {
        boolean worksiteLimited = ! isPortfolioAudience();
        
        String emailOrUser = getSearchEmails();
        
        if ( ! findByEmailOrUserName(emailOrUser, isGuestUserEnabled(), worksiteLimited) ) {
           if ( isGuestUserEnabled() )
              FacesContext.getCurrentInstance().addMessage(null,
                                                           getFacesMessageFromBundle("email_user_not_found", (new Object[]{emailOrUser})));
           else
              FacesContext.getCurrentInstance().addMessage(null,
                                                           getFacesMessageFromBundle("user_not_found", (new Object[]{emailOrUser})));
        } 
        else {
            setSearchEmails("");
        }
        return "tool";
    }

    public String processActionAddGroup() {
        for (Iterator i = getSelectedRoles().iterator(); i.hasNext();) {
            String roleId = (String) i.next();
            Agent role = getAgentManager().getAgent(getIdManager().getId(roleId));
            addAgent(role, "role_exists");
        }
        getSelectedRoles().clear();
        return "tool";
    }

    public AgentManager getAgentManager() {
        return agentManager;
    }

    public void setAgentManager(AgentManager agentManager) {
        this.agentManager = agentManager;
    }

    /**
     * @param displayName - for a guest user, this is the email address
     * 
     */
    protected boolean findByEmailOrUserName(String displayName, boolean allowGuest, boolean worksiteLimited) {
        List userList = getAgentManager().findByProperty(AgentManager.TYPE_EID, displayName);
        
        // if guest users not allowed and user was not found, return false
        if ( ! allowGuest && userList == null) 
           return false;
           
        // if guest users allowed, users not limited by worksite, and user not found
        if (allowGuest && !worksiteLimited && userList == null) {
           AgentImplOsp viewer = new AgentImplOsp();
           if(validateEmail(displayName) && isDomainAllowed(displayName)) {
               viewer.setDisplayName(displayName);
               viewer.setRole(Agent.ROLE_GUEST);
               viewer.setId(getIdManager().getId(viewer.getDisplayName()));
               userList = new ArrayList();
               userList.add(viewer);
           }
        }

        boolean found = false;

        for (Iterator i = userList.iterator(); i.hasNext();) {
            found = true;
            Agent agent = (Agent) i.next();
            if (worksiteLimited && !checkWorksiteMember(agent)) {
               return false;
            }
            if (agent instanceof AgentImplOsp) {
                agent = createGuestUser(agent);
                if (agent != null) 
                    notifyNewUserEmail(agent);
            }
            addAgent(agent, "user_exists");
        }

        return found;
    }

    protected void addAgent(Agent agent, String key) {
        DecoratedMember decoratedAgent = new DecoratedMember(this, agent);
        if (!getSelectedMembers().contains(decoratedAgent)) {
            getSelectedMembers().add(decoratedAgent);
        } else {
            FacesContext.getCurrentInstance().addMessage(null,
                    getFacesMessageFromBundle(key, (new Object[]{agent.getDisplayName()})));
        }
    }

    protected boolean checkWorksiteMember(Agent agent) {
        List roles = agent.getWorksiteRoles(getSite().getId());
        return (roles != null && roles.size() > 0);
    }
    

    public String processActionSave() {
        ToolSession session = SessionManager.getCurrentToolSession();
        session.setAttribute("target", getSaveTarget());
        save();
        clearAudienceSelectionVariables();
        processActionClearFilter();
        return returnToCaller();
    }

    protected void save() {
        List added = new ArrayList();

        for (Iterator i = getSelectedMembers().iterator(); i.hasNext();) {
            DecoratedMember member = (DecoratedMember) i.next();

            if (originalMembers.contains(member.getBase())) {
                originalMembers.remove(member.getBase());
            } else {
                added.add(member.getBase());
            }
        }
        setSelectedMembers(null);
        addMembers(added);
        removeMembers(originalMembers);


    }

    protected void addMembers(List added) {
        for (Iterator i = added.iterator(); i.hasNext();) {
            Agent agent = (Agent) i.next();

            getAuthzManager().createAuthorization(agent,
                    getFunction(), getQualifier());
        }
    }

    protected void removeMembers(List added) {
        for (Iterator i = added.iterator(); i.hasNext();) {
            Agent agent = (Agent) i.next();

            getAuthzManager().deleteAuthorization(agent,
                    getFunction(), getQualifier());
        }
    }

   /** Format role name, optionally including site title
    **/
   private String formatRole( Site site, String roleName ) {
   
      if ( site == null || site.equals(getSite()) ) {
         return roleName;
      }
      else {
         StringBuilder buf = new StringBuilder( roleName );
         buf.append(" (");
         buf.append( site.getTitle() );
         buf.append(")");
         return buf.toString();
      }
   
   }
   
    /**
     ** Return list of roles for this site, or for all sites user can access
     **/
    public List getRoles() {
        List returned = new ArrayList();
        
        if ( isWorksiteLimited() ) {
           Site site = getSite();
           Set roles = site.getRoles();
           
           for (Iterator i = roles.iterator(); i.hasNext();) {
              Role role = (Role) i.next();
              Agent roleAgent = getAgentManager().getWorksiteRole(role.getId(), site.getId());
              returned.add(new SelectItem(roleAgent.getId().getValue(), 
                                          role.getId(), 
                                          "role"));
           }
        }
        
        else {
           List siteList = siteService.getSites(SiteService.SelectionType.ACCESS,
                                                null, null, null, 
                                                SiteService.SortType.TITLE_ASC, null);
                                                
           for (Iterator siteIt = siteList.iterator(); siteIt.hasNext();) {
              Site site = (Site)siteIt.next();
              Set roles = site.getRoles();

              for (Iterator roleIt = roles.iterator(); roleIt.hasNext();) {
                 Role role = (Role) roleIt.next();
                 Agent roleAgent = getAgentManager().getWorksiteRole(role.getId(), site.getId());
                 returned.add(new SelectItem(roleAgent.getId().getValue(), 
                                             formatRole( site, role.getId() ),
                                             "role"));
              }
           }
        
        }

        return returned;
    }

    public String[] getAvailableUserArray() {
        return availableUserArray;
    }

    public void setAvailableUserArray(String[] availableUserArray) {
        this.availableUserArray = availableUserArray;
    }

    public void setAvailableUserList(List availableUserList) {
        this.availableUserList = availableUserList;
    }

    public List getAvailableUserList() {

            availableUserList = new ArrayList();

            List userMemberList = new ArrayList();
            userMemberList.addAll(getMembersList());

            for (Iterator idx = userMemberList.iterator(); idx.hasNext();) {
                SelectItem availableItem = (SelectItem) idx.next();
                boolean matchFound = false;
                for (Iterator jdx = getSelectedUserList().iterator(); jdx.hasNext();) {
                    SelectItem selecteItem = (SelectItem) jdx.next();
                    if (selecteItem.getValue().toString().equals(availableItem.getValue().toString())) {
                        matchFound = true;
                        break;
                    }

                }
                if (!matchFound){
                    availableUserList.add(availableItem);
                }
            }

        return availableUserList;
    }

    public String[] getAvailableRoleArray() {
        return availableRoleArray;
    }

    public void setAvailableRoleArray(String[] availableRoleArray) {
        this.availableRoleArray = availableRoleArray;
    }

    public void setAvailableRoleList(List availableRoleList) {
        this.availableRoleList = availableRoleList;
    }

    public List getAvailableRoleList() {

            availableRoleList = new ArrayList();

            List roleMemberList = new ArrayList();
            roleMemberList.addAll(getRoles());

            for (Iterator idx = roleMemberList.iterator(); idx.hasNext();) {
                SelectItem availableItem = (SelectItem) idx.next();
                boolean matchFound = false;
                for (Iterator jdx = getSelectedRoleList().iterator(); jdx.hasNext();) {
                    SelectItem selectedItem = (SelectItem) jdx.next();
                    if (selectedItem.getValue().toString().equals(availableItem.getValue().toString())) {
                        matchFound = true;
                        break;
                    }

                }
                if (!matchFound){
                    availableRoleList.add(availableItem);
                }
            }

        return availableRoleList;
    }

   /**
    * Get array of selected users 
    */
    public String[] getSelectedUserArray() {
        return selectedUserArray;
    }

   /**
    * Set array of selected users 
    */
    public void setSelectedUserArray(String[] selectedUserArray) {
        this.selectedUserArray = selectedUserArray;
    }

   /**
     * Get list of selected users 
     */
    public List getSelectedUserList() {

            selectedUserList = new ArrayList();
            for (Iterator i = getSelectedMembers().iterator(); i.hasNext();) {
                DecoratedMember decoratedMember = (DecoratedMember) i.next();
                if ( ! decoratedMember.getBase().isRole() && decoratedMember.getBase().getId() != null ) 
                   selectedUserList.add(new SelectItem(decoratedMember.getBase().getId().getValue(), decoratedMember.getBase().getDisplayName(), "member"));
            }

        return selectedUserList;
    }

   /**
     * Set list of selected users 
     */
    public void setSelectedUserList(List selectedUserList) {
        this.selectedUserList = selectedUserList;
    }


   /**
    * Get array of selected roles 
    */
    public String[] getSelectedRoleArray() {
        return selectedRoleArray;
    }

   /**
    * Set array of selected roles 
    */
    public void setSelectedRoleArray(String[] selectedRoleArray) {
        this.selectedRoleArray = selectedRoleArray;
    }

   /**
    ** Parse role id and return Site id
    **/
    private Site getSiteFromRoleMember( String roleMember ) {
       Reference ref = EntityManager.newReference( roleMember );
       String siteId = ref.getContainer();
       Site site = null;
       try {
          site = getSiteService().getSite(siteId);
       }
       catch (IdUnusedException e) {
          // tbd - log warning
       }
            
       return site;
    }
    
   /**
     * Get list of selected roles 
     */
    public List getSelectedRoleList() {

       selectedRoleList = new ArrayList();
       for (Iterator i = getSelectedMembers().iterator(); i.hasNext();) {
           DecoratedMember decoratedMember = (DecoratedMember) i.next();
           if (decoratedMember.getBase().isRole()) {
              String roleName = null;
              
              if ( isWorksiteLimited() ) {
                 roleName = decoratedMember.getBase().getDisplayName();
              }
              else {
                 Site site = getSiteFromRoleMember( decoratedMember.getBase().getId().getValue() );
                 roleName = formatRole( site, decoratedMember.getBase().getDisplayName() ); 
              }
                             
              selectedRoleList.add(new SelectItem(decoratedMember.getBase().getId().getValue(), 
                                                  roleName,
                                                  "role"));
           }
       }

       return selectedRoleList;
    }

   /**
     * Set list of selected roles
     */
    public void setSelectedRoleList(List selectedRoleList) {
        this.selectedRoleList = selectedRoleList;
    }

   /** Action to add to list of users
    **/
    public String processActionAddUser() {
        String[] selected = getAvailableUserArray();
        if (selected.length < 1) {
            //put in a message that they need to select something from the list to add
            return "main";
        }

        for (int i = 0; i < selected.length; i++) {
            SelectItem addItem = removeItems(selected[i], getAvailableUserList());
            addAgent(getAgentManager().getAgent(addItem.getValue().toString()), "user_exists");
            getSelectedUserList().add(addItem);
        }

        setSelectedUserList(sortList(getSelectedUserList(), false));
        
        return "main";
    }

   /** Action to remove from list of users
    **/
    public String processActionRemoveUser() {
        String[] selected = getSelectedUserArray();
        if (selected.length < 1) {
            //put in a message that they need to select something from the lsit to add
            return "main";
        }

        for (int i = 0; i < selected.length; i++) {
            for (Iterator idx = getSelectedMembers().iterator(); idx.hasNext();) {
               DecoratedMember member = (DecoratedMember) idx.next();
               if (member.getBase().getId().toString().equals(selected[i])) 
                   idx.remove();
               
               getAvailableUserList().add(removeItems(selected[i], getSelectedUserList()));
           }
        }

        setAvailableUserList(sortList(getAvailableUserList(), true));

        return "main";
    }


   /** Action to add to list of roles
    **/
    public String processActionAddRole() {
        String[] selected = getAvailableRoleArray();
        if (selected.length < 1) {
            //put in a message that they need to select something from the list to add
            return "main";
        }

        for (int i = 0; i < selected.length; i++) {
            SelectItem addItem = removeItems(selected[i], getAvailableRoleList());
            addAgent(getAgentManager().getAgent(getIdManager().getId(addItem.getValue().toString())), "role_exists");
            getSelectedRoleList().add(addItem);
        }

        setSelectedRoleList(sortList(getSelectedRoleList(), false));
            
        return "main";
    }

   /** Action to remove from list of roles
    **/
    public String processActionRemoveRole() {
        String[] selected = getSelectedRoleArray();
        if (selected.length < 1) {
            //put in a message that they need to select something from the lsit to add
            return "main";
        }

        for (int i = 0; i < selected.length; i++) {
            for (Iterator idx = getSelectedMembers().iterator(); idx.hasNext();) {
               DecoratedMember member = (DecoratedMember) idx.next();
               if (member.getBase().getId().toString().equals(selected[i]))
                   idx.remove();
               
               getAvailableRoleList().add(removeItems(selected[i], getSelectedRoleList()));
           }
        }

        setAvailableRoleList(sortList(getAvailableRoleList(), true));

        return "main";
    }

    private SelectItem removeItems(String value, List items) {

        SelectItem result = null;
        for (int i = 0; i < items.size(); i++) {
            SelectItem item = (SelectItem) items.get(i);
            if (value.equals(item.getValue())) {
                result = (SelectItem) items.remove(i);
                break;
            }
        }

        return result;
    }

    protected void clearAudienceSelectionVariables() {
        ToolSession session = SessionManager.getCurrentToolSession();
        session.removeAttribute(AudienceSelectionHelper.AUDIENCE_FUNCTION);
        session.removeAttribute(AudienceSelectionHelper.AUDIENCE_QUALIFIER);
        session.removeAttribute(AudienceSelectionHelper.AUDIENCE_BACK_TARGET);
        session.removeAttribute(AudienceSelectionHelper.AUDIENCE_CANCEL_TARGET);
        session.removeAttribute(AudienceSelectionHelper.AUDIENCE_PUBLIC_URL);
        session.removeAttribute(AudienceSelectionHelper.AUDIENCE_SAVE_NOTIFY_TARGET);
        session.removeAttribute(AudienceSelectionHelper.AUDIENCE_SAVE_TARGET);
        session.removeAttribute(PRESENTATION_VIEWERS);
    }

    public String getStepString() {
        return stepString;
    }

   protected boolean validateEmail(String displayName)
   {
      if (!emailPattern.matcher(displayName).matches()) {
         return false;
      }

      return true;
   }
   
    protected Agent createGuestUser(Agent viewer) {
        AgentImplOsp guest = (AgentImplOsp) viewer;
        guest.setRole(Agent.ROLE_GUEST);
        return getAgentManager().createAgent(guest);
    }

    private void notifyNewUserEmail(Agent guest) {
        String from = ServerConfigurationService.getString("setup.request", null);
        if (from == null) {

            from = "postmaster@".concat(ServerConfigurationService.getServerName());
        }
        String productionSiteName = ServerConfigurationService.getString("ui.service", "");
        String productionSiteUrl = ServerConfigurationService.getPortalUrl();

        String to = guest.getDisplayName();
        String headerTo = to;
        String replyTo = to;
        String message_subject = getMessageFromBundle("email.guestusernoti", new Object[]{productionSiteName});
        String content = "";

        if (from != null && to != null) {
            StringBuilder buf = new StringBuilder();
            buf.setLength(0);

            // email body
            buf.append(to + ":\n\n");
            AgentImplOsp impl = (AgentImplOsp) guest;
            buf.append(getMessageFromBundle("email.addedto", new Object[]{productionSiteName, productionSiteUrl}) + "\n\n");
            buf.append(getMessageFromBundle("email.simpleby", new Object[]{UserDirectoryService.getCurrentUser().getDisplayName()}) + "\n\n");
            buf.append(getMessageFromBundle("email.userid", new Object[]{to}) + "\n\n");
            buf.append(getMessageFromBundle("email.password", new Object[]{impl.getPassword()}) + "\n\n");

            content = buf.toString();
            EmailService.send(from, to, message_subject, content, headerTo, replyTo, null);
        }
    }

    private List sortList(List sortList, boolean seperator) {
        List roleList = new ArrayList();
        List groupList = new ArrayList();
        List memberList = new ArrayList();
        for (Iterator i = sortList.iterator(); i.hasNext();) {
            SelectItem item = (SelectItem) i.next();
            if (item.getDescription().equals("role")) {
                roleList.add(item);
            } else if (item.getDescription().equals("group")) {
                groupList.add(item);
            } else if (item.getDescription().equals("member")) {
                memberList.add(item);
            }
        }
        roleList.addAll(groupList);
        if (seperator) {
            roleList.add(new SelectItem("null", LIST_SEPERATOR, ""));
        }
        roleList.addAll(memberList);
        return roleList;

    }
     public String getBrowseMessage() {
      String message = "";


         message = getMessageFromBundle("browseUserInstruction1", new Object[]{
               new Integer(getMaxRoleMemberList())});


      return message;
   }
   
   /*------------- browse.jsp control ------------------------*/
    
    public String getSearchUsers() {
        return searchUsers;
    }

    public void setSearchUsers(String searchUsers) {
        this.searchUsers = searchUsers;
    }

    public void processActionRemoveBrowseMember() {
        for (Iterator i = getSelectedMembers().iterator(); i.hasNext();) {
            DecoratedMember member = (DecoratedMember) i.next();
            if (member.isSelected()) {
                i.remove();
            }
        }
    }
    
    public boolean getHasGroups() {
        return getSite().hasGroups();
    }

    public List getGroups() {
        List returned = new ArrayList();
        Collection groups = getSite().getGroups();

        for (Iterator i = groups.iterator(); i.hasNext();) {
            Group group = (Group) i.next();
            returned.add(new SelectItem(group.getId(), group.getTitle(), "group"));
        }

        return returned;
    }

    public List getSelectedRolesFilter() {
        if (selectedRolesFilter == null) {
            selectedRolesFilter = new ArrayList();
        }
        return selectedRolesFilter;
    }

    public void setSelectedRolesFilter(List selectedRolesFilter) {
        this.selectedRolesFilter = selectedRolesFilter;
    }


    public List getSelectedGroupsFilter() {
        if (selectedGroupsFilter == null) {
            selectedGroupsFilter = new ArrayList();
        }
        return selectedGroupsFilter;
    }

    public void setSelectedGroupsFilter(List selectedGroupsFilter) {
        this.selectedGroupsFilter = selectedGroupsFilter;
    }

    public PagingList getBrowseUsers() {
        if (browseUsers == null) {
            processActionApplyFilter();
        }
        return browseUsers;
    }

    public void setBrowseUsers(PagingList browseUsers) {
        this.browseUsers = browseUsers;
    }

    public void processActionAddBrowseSelected() {
        for (Iterator i = getBrowseUsers().getWholeList().iterator(); i.hasNext();) {
            DecoratedMember member = (DecoratedMember) i.next();
            if (member.isSelected()) {
                Agent user = member.getBase();
                addAgent(user, "user_exists");
                member.setSelected(false);
            }
        }
        if (getSelectedRoles() != null) {
            getSelectedRoles().clear();
        }
    }

    public void processActionClearFilter() {

        getSelectedGroupsFilter().clear();
        getSelectedRolesFilter().clear();
        setSearchEmails("");
        setSearchUsers("");
        if (selectedMembers != null) {
            selectedMembers.clear();
        }
        if (selectedRoles != null) {
            selectedRoles.clear();
        }
        if (availableUserList !=null){
            availableUserList.clear();
        }
        if (selectedUserList != null){
            selectedUserList.clear();
        }
        processActionApplyFilter();
    }

    public void processActionApplyFilter() {
        Set members = getGroupMembers();
        List siteUsers = new ArrayList();
        MemberFilter filter = new MemberFilter(this);

        for (Iterator i = members.iterator(); i.hasNext();) {
            Member user = (Member) i.next();
            DecoratedMember decoratedMember =
                    new DecoratedMember(this, getAgentManager().getAgent(user.getUserId()));
            if (filter.includeMember(decoratedMember)) {
                siteUsers.add(decoratedMember);
            }
        }

        setBrowseUsers(new PagingList(siteUsers));
    }

    protected Set getGroupMembers() {
        Set members = new HashSet();
        List filterGroups = new ArrayList();
        filterGroups.addAll(getSelectedGroupsFilter());
        filterGroups.remove("");

        if (!getHasGroups() || filterGroups.size() == 0) {
            return getSite().getMembers();
        }

        for (Iterator i = filterGroups.iterator(); i.hasNext();) {
            String groupId = (String) i.next();
            Group group = getSite().getGroup(groupId);
            members.addAll(group.getMembers());
        }

        return members;
    }

}

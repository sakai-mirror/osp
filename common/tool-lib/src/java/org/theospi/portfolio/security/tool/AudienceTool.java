/**********************************************************************************
 * $URL:https://source.sakaiproject.org/svn/osp/trunk/common/tool-lib/src/java/org/theospi/portfolio/security/tool/AudienceTool.java $
 * $Id:AudienceTool.java 9134 2006-05-08 20:28:42Z chmaurer@iupui.edu $
 ***********************************************************************************
 *
 * Copyright 2005, 2006, 2007, 2008 Sakai Foundation
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at
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
import javax.faces.event.ValueChangeEvent;
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

    private List selectedMembers = null;
    private List originalMembers = null;
	 private List selectedRoles = null;
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
    private List availableUserList = null;

    private String[] selectedUserArray;
    private List selectedUserList;

    private String selectedGroupFilter = null;

    /**
     * **********************************
     */

    private static String TOOL_JSF = "tool";
    private String PRESENTATION_VIEWERS = "PRESENTATION_VIEWERS";
    private String stepString = "2";
    private String function;
    private Id qualifier;
    private boolean publicAudience = false;
    private boolean showAllSiteRoles = false;
	 
    private SelectItemComparator selectItemComparator = new SelectItemComparator();

    private MemberSort memberSort = new MemberSort();

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
    
 
    /*************************************************************************/

    private List getFilteredMembersList() {
       Set members = null;
       if ( !isPortfolioAudience() )
          members = getEvaluateUsers();
       else if ( selectedGroupFilter != null && !selectedGroupFilter.equals("") )
          members = getGroupMembers();
       else
          members = getSite().getMembers();
          
        List memberList = new ArrayList();
        for (Iterator i = members.iterator(); i.hasNext();) {
           String userId = null;
           if ( !isPortfolioAudience() )
              userId = (String)i.next();
           else
              userId = ((Member)i.next()).getUserId();

            Agent agent = getAgentManager().getAgent(userId);
            //Check for a null agent since the site.getMembers() will return member records for deleted users
            if (agent != null && agent.getId() != null) {
               DecoratedMember decoratedMember = new DecoratedMember(this, agent);
               memberList.add(new SelectItem(decoratedMember.getBase().getId().getValue(), decoratedMember.getBase().getDisplayName(), "member"));
            }
        }
        
        return memberList;
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

    private List getSelectedMembers() {
        if (getAttribute(PRESENTATION_VIEWERS) == null) {
            selectedMembers = fillMemberList();
            setAttribute(PRESENTATION_VIEWERS, selectedMembers);
        }

        if (selectedMembers == null)
            selectedMembers = new ArrayList();
        return selectedMembers;
    }

    private void setSelectedMembers(List selectedMembers) {
        this.selectedMembers = selectedMembers;
    }
	 
    private Id getQualifier() {
        return qualifier;
    }

    private void setQualifier(Id qualifier) {
        this.qualifier = qualifier;
    }

    private String getFunction() {
        return function;
    }

    private void setFunction(String function) {
        this.function = function;
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

    private String getCancelTarget() {
        return (String) getAttribute(AudienceSelectionHelper.AUDIENCE_CANCEL_TARGET);
    }

    private String getSaveTarget() {
        return (String) getAttribute(AudienceSelectionHelper.AUDIENCE_SAVE_TARGET);
    }

    private String getSaveNotifyTarget() {
        return (String) getAttribute(AudienceSelectionHelper.AUDIENCE_SAVE_NOTIFY_TARGET);
    }

    private String getBackTarget() {
        return (String) getAttribute(AudienceSelectionHelper.AUDIENCE_BACK_TARGET);
    }
    private String getAudienceFunction() {
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
        String currentSiteId = (String) getAttribute(AudienceSelectionHelper.AUDIENCE_SITE);
        if ( site == null || ! currentSiteId.equals(site.getId()) ) {
            try {
                site = getSiteService().getSite(currentSiteId);
            }
            catch (IdUnusedException e) {
                throw new RuntimeException(e);
            }
        }
        return site;
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
        return TOOL_JSF;
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

        if (userList == null) {
           return false;
        }
       
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
     ** Return list of roles for portfolio, wizard or matrix
     **/
    public List getRoles() {
        List returned = new ArrayList();
        
        // Matrix/Wizard evaluators selection needs roles only from this site
        if ( ! isPortfolioAudience() ) {
           Site site = getSite();
           Set roles = site.getRoles();
           
           for (Iterator i = roles.iterator(); i.hasNext();) {
              Role role = (Role) i.next();
              if ( isWizardAudience() && !role.isAllowed(AudienceSelectionHelper.AUDIENCE_FUNCTION_WIZARD) )
                 continue;
              else if ( isMatrixAudience() && !role.isAllowed(AudienceSelectionHelper.AUDIENCE_FUNCTION_MATRIX) )
                 continue;
              Agent roleAgent = getAgentManager().getWorksiteRole(role.getId(), site.getId());
              returned.add(new SelectItem(roleAgent.getId().getValue(), 
                                          role.getId(), 
                                          "role"));
           }
        }
        
        // Looping through all user sites for roles can be a perfomance hit,
        // so isShowAllSiteRoles() is false by default
        else if ( !isShowAllSiteRoles() ) {
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
        
        // Portfolio needs roles from all sites user can access
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

    public List getAvailableUserList() {

        availableUserList = new ArrayList();

        List userMemberList = new ArrayList();
        userMemberList.addAll(getFilteredMembersList());

        for (Iterator idx = userMemberList.iterator(); idx.hasNext();) {
            SelectItem availableItem = (SelectItem) idx.next();
            boolean matchFound = false;
            for (Iterator jdx = getSelectedUserList().iterator(); jdx.hasNext();) {
                SelectItem selectedItem = (SelectItem) jdx.next();
                if (selectedItem.getValue().toString().equals(availableItem.getValue().toString())) {
                    matchFound = true;
                    break;
                }
            }
            if (!matchFound){
                availableUserList.add(availableItem);
            }
        }

        Collections.sort(availableUserList, memberSort);
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

        Collections.sort(availableRoleList, selectItemComparator);
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

        Collections.sort(selectedUserList, memberSort);
        return selectedUserList;
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
              
              if ( !isPortfolioAudience() ) {
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

       Collections.sort(selectedRoleList, selectItemComparator);
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

        return "main";
    }

    /** Return whether or not this portfolio is in a my workspace site
     **/
    private boolean isUserSite() {
       return siteService.isUserSite(getSite().getId());
    }

    /** Only render 'show all site roles' option for portfolios outside of my workspace sites
     **/
    public boolean isRenderShowAllSiteRoles() {
       return isPortfolioAudience() && !isUserSite();
    }

    /** Return value of showAllSiteRoles (always true for my workspace)
     **/
    public boolean isShowAllSiteRoles() {
       return isUserSite() || showAllSiteRoles;
    }

   /** Action to show roles from all sites
    **/
    public String processActionShowAllSiteRoles() {
        showAllSiteRoles = true;
        return "main";
    }

   /** Action to hide roles from all sites (use only this site)
    **/
    public String processActionHideAllSiteRoles() {
        showAllSiteRoles = false;
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

    protected boolean validateEmail(String displayName) {
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

    public boolean getHasGroups() {
        return getSite().hasGroups();
    }

    public List getGroups() {
        List groupsList = new ArrayList();
        Collection groups = getSite().getGroups();

        for (Iterator i = groups.iterator(); i.hasNext();) {
            Group group = (Group) i.next();
            groupsList.add(new SelectItem(group.getId(), group.getTitle(), "group"));
        }

        Collections.sort(groupsList, selectItemComparator);
        return groupsList;
    }

    public String getSelectedGroupFilter() {
        return selectedGroupFilter;
    }
	 
    public void setSelectedGroupFilter(String selectedGroupFilter) {
        this.selectedGroupFilter = selectedGroupFilter;
    }

    public void processActionClearFilter() {
       selectedGroupFilter = null;
    }

    public void processActionApplyFilter() {
		 // getFilteredMembersList() will apply filter
		 return; 
    }

    protected Set<Member> getGroupMembers() {
        if (!getHasGroups() || selectedGroupFilter == null || selectedGroupFilter.equals("")) {
            return getSite().getMembers();
        }

        Set members = new HashSet();
        members.addAll( getSite().getGroup(selectedGroupFilter).getMembers() );
        return members;
    }
    
    protected Set<String> getEvaluateUsers() {
       Set evalUsers = new HashSet();
       Site site = getSite();
       Set roles = site.getRoles();
           
       for (Iterator i = roles.iterator(); i.hasNext();) {
          Role role = (Role) i.next();
          // check if role has evaluate permission
          if ( isWizardAudience() && role.isAllowed(AudienceSelectionHelper.AUDIENCE_FUNCTION_WIZARD) )
             evalUsers.addAll( site.getUsersHasRole(role.getId()) );
          if ( isMatrixAudience() && role.isAllowed(AudienceSelectionHelper.AUDIENCE_FUNCTION_MATRIX) )
             evalUsers.addAll( site.getUsersHasRole(role.getId()) );
       }
       
       return evalUsers;
    }
    
    /**
     * Context (AudienceSelectionHelper.CONTEXT) is used to describe the page/tool
     * that is being used in this helper.  Context is the main title (ex. matrix or wizard name)
     * and context 2 is used for the subtitle (ex. matrix cell or wizard page).  If left
     * blank, then nothing displays on the page.  
     * @return
     */
    public String getPageContext(){
    	String context = (String) getAttribute(AudienceSelectionHelper.CONTEXT);
    	return context != null ? context : "";
    }
    
    /**
     * Context2 (AudienceSelectionHelper.CONTEXT2) is used to describe the page/tool
     * that is being used in this helper.  Context is the main title (ex. matrix or wizard name)
     * and Context2 is used for the subtitle (ex. matrix cell or wizard page).  If left
     * blank, then nothing displays on the page.  
     * @return
     */
    public String getPageContext2(){
    	String context2 = (String) getAttribute(AudienceSelectionHelper.CONTEXT2);
    	return context2 != null ? context2 : "";
    }
	 
	 
	// Spring Injection methods
    public AuthorizationFacade getAuthzManager() {
        return authzManager;
    }

    public void setAuthzManager(AuthorizationFacade authzManager) {
        this.authzManager = authzManager;
    }

    public IdManager getIdManager() {
        return idManager;
    }

    public void setIdManager(IdManager idManager) {
        this.idManager = idManager;
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
	 
    public AgentManager getAgentManager() {
        return agentManager;
    }

    public void setAgentManager(AgentManager agentManager) {
        this.agentManager = agentManager;
    }
	 
   /** 
    ** Comparator for sorting SelectItem objects
    **/
	public class SelectItemComparator implements Comparator {
		public int compare(Object o1, Object o2) {
			return ((SelectItem)o1).getLabel().compareTo( ((SelectItem)o2).getLabel() );
		}
	}
   
   /**
    ** Sort SelectList of member names
    ** (tbd: localize sorting of names)
    **/
   public class MemberSort implements Comparator<SelectItem> {
      
      public int compare(SelectItem o1, SelectItem o2) {
         String n1 = o1.getLabel();
         String n2 = o2.getLabel();
         int i1 = n1.lastIndexOf(" ");
         int i2 = n2.lastIndexOf(" ");
         if (i1 > 0)
            n1 = n1.substring(i1 + 1) + " " + n1.substring(0, i1);
         if (i2 > 0)
            n2 = n2.substring(i2 + 1) + " " + n2.substring(0, i2);
         
         return n1.compareToIgnoreCase(n2);
      }
   }
    
}

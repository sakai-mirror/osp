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
import org.theospi.portfolio.security.AudienceSelectionHelper;
import org.theospi.portfolio.security.Authorization;
import org.theospi.portfolio.security.AuthorizationFacade;
import org.theospi.portfolio.shared.model.AgentImplOsp;
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
    private String[] availableRoleMember;
    private List availableRoleMemberList;

    private String[] selectedRoleMember;
    private List selectedRoleMemberList;

    private List availableRoleList;
    private List availableMemberList;

    private List selectedRoleList = new ArrayList();
    private List selectedMemberList = new ArrayList();


    private boolean maxList;

    /**
     * **********************************
     */

    private List selectedRolesFilter;
    private List selectedGroupsFilter;
    private PagingList browseUsers = null;
    private String stepString = "2";
    private String function;
    private Id qualifier;
    private static final Pattern emailPattern = Pattern.compile(".*@.*");
    private List roleMemberList = null;
    private List groupMemberList = null;
    private String LIST_SEPERATOR = "__________________";
    private boolean publicAudience = false;




    /*************************************************************************/




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
        if (getAttribute(AudienceSelectionHelper.AUDIENCE_FUNCTION) != null) {
            selectedMembers = fillMemberList();
        }
        setAttribute("PRESENTATION_VIEWERS", selectedMembers);

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

        removeAttributes(
                new String[]{AudienceSelectionHelper.AUDIENCE_FUNCTION, AudienceSelectionHelper.AUDIENCE_QUALIFIER});

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

    public String getInstructions() {
        return (String) getAttributeOrDefault(AudienceSelectionHelper.AUDIENCE_INSTRUCTIONS);
    }

    public String getPublicInstructions() {
        return (String) getAttributeOrDefault(AudienceSelectionHelper.AUDIENCE_PUBLIC_INSTRUCTIONS);
    }

    public String getPublicURL() {
        return (String) getAttributeOrDefault(AudienceSelectionHelper.AUDIENCE_PUBLIC_URL);
    }

    public String getFilterTitle() {
        return (String) getAttributeOrDefault(AudienceSelectionHelper.AUDIENCE_FILTER_INSTRUCTIONS);
    }

    public String getGlobalTitle() {
        return (String) getAttributeOrDefault(AudienceSelectionHelper.AUDIENCE_GLOBAL_TITLE);
    }

    public String getIndividualTitle() {
        return (String) getAttributeOrDefault(AudienceSelectionHelper.AUDIENCE_INDIVIDUAL_TITLE);
    }

    public String getGroupTitle() {
        return (String) getAttributeOrDefault(AudienceSelectionHelper.AUDIENCE_GROUP_TITLE);
    }

    public String getPublicTitle() {
        return (String) getAttribute(AudienceSelectionHelper.AUDIENCE_PUBLIC_TITLE);
    }

    public String getSelectedTitle() {
        return (String) getAttributeOrDefault(AudienceSelectionHelper.AUDIENCE_SELECTED_TITLE);
    }

    public boolean isPortfolioWizard() {
        return getAttribute(AudienceSelectionHelper.AUDIENCE_PORTFOLIO_WIZARD) != null;
    }

    public boolean isPublicCapable() {
        return getAttribute(AudienceSelectionHelper.AUDIENCE_PUBLIC_TITLE) != null;
    }

    public boolean isEmailCapable() {
        return getAttribute(AudienceSelectionHelper.AUDIENCE_GUEST_EMAIL) != null;
    }

    public boolean isPublicAudience() {
        if (getAttribute(AudienceSelectionHelper.AUDIENCE_PUBLIC_FLAG) != null) {
            publicAudience =
                    "true".equalsIgnoreCase((String) getAttribute(AudienceSelectionHelper.AUDIENCE_PUBLIC_FLAG));
            removeAttribute(AudienceSelectionHelper.AUDIENCE_PUBLIC_FLAG);
        }
        return publicAudience;
    }

    public boolean isWorksiteLimited() {
        if (getAttribute(AudienceSelectionHelper.AUDIENCE_WORKSITE_LIMITED) != null) {
            return "true".equalsIgnoreCase((String) getAttribute(AudienceSelectionHelper.AUDIENCE_WORKSITE_LIMITED));
        }
        return false;
    }


    public void setPublicAudience(boolean publicAudience) {
        this.publicAudience = publicAudience;
        setAttribute(AudienceSelectionHelper.AUDIENCE_PUBLIC_FLAG, publicAudience ? "true" : "false");
    }

    public String getSearchUsers() {
        return searchUsers;
    }

    public void setSearchUsers(String searchUsers) {
        this.searchUsers = searchUsers;
    }

    public String getSearchEmails() {
        return searchEmails;
    }

    public void setSearchEmails(String searchEmails) {
        this.searchEmails = searchEmails;
    }


    public List getSiteGroups() {
        List returned = new ArrayList();
        Collection groups = getSite().getGroups();

        for (Iterator i = groups.iterator(); i.hasNext();) {
            Group group = (Group) i.next();
            returned.add(new SelectItem(group.getId(), group.getDescription(), "group"));
        }

        return returned;
    }

    public Site getSite() {
        if (site == null) {
            Placement placement = getToolManager().getCurrentPlacement();
            String currentSiteId = placement.getContext();
            try {
                site = getSiteService().getSite(currentSiteId);
            }
            catch (IdUnusedException e) {
                throw new RuntimeException(e);
            }
        }
        return site;
    }

    public void setSite(Site site) {
        this.site = site;
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

    public void processActionRemove() {
        for (Iterator i = getSelectedMembers().iterator(); i.hasNext();) {
            DecoratedMember member = (DecoratedMember) i.next();
            if (member.isSelected()) {
                i.remove();
            }
        }
    }

    /*public String processActionAddUser() {
        boolean worksiteLimited = isWorksiteLimited();
        if (!findByEmailOrDisplayName(getSearchUsers(), false, worksiteLimited)) {
            FacesContext.getCurrentInstance().addMessage(null,
                    getFacesMessageFromBundle(worksiteLimited ? "worksite_user_not_found" : "user_not_found",
                            (new Object[]{getSearchUsers()})));
        } else {
            setSearchUsers("");
        }
        return "tool";
    } */

    public String processActionAddEmail() {
        boolean worksiteLimited = isWorksiteLimited();

        if (!findByEmailOrDisplayName(getSearchEmails(), true, worksiteLimited)) {
            if (worksiteLimited) {
                FacesContext.getCurrentInstance().addMessage(null,
                        getFacesMessageFromBundle("worksite_user_not_found", (new Object[]{getSearchUsers()})));
            }


        } else {
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
     * @
     */
    protected boolean findByEmailOrDisplayName(String displayName, boolean includeEmail, boolean worksiteLimited) {
        List retVal = new ArrayList();
        List guestUsers = null;
        AgentImplOsp viewer = new AgentImplOsp();
        if (includeEmail) {
            guestUsers = getAgentManager().findByProperty("email", displayName);
            if (!worksiteLimited && guestUsers == null) {
                viewer.setDisplayName(displayName);
                viewer.setRole(Agent.ROLE_GUEST);
                viewer.setId(getIdManager().getId(viewer.getDisplayName()));
                guestUsers = new ArrayList();
                guestUsers.add(viewer);
            }

        }

        if (guestUsers == null || guestUsers.size() == 0) {
            guestUsers = getAgentManager().findByProperty("displayName", displayName);
        }

        if (guestUsers != null) {
            retVal.addAll(guestUsers);
        }

        boolean found = false;

        for (Iterator i = retVal.iterator(); i.hasNext();) {
            found = true;
            Agent agent = (Agent) i.next();
            if (worksiteLimited) {
                if (!checkWorksiteMember(agent)) {
                    return false;
                }
            }
            if (agent instanceof AgentImplOsp) {
                agent = createGuestUser(agent);
                if (agent != null) {
                    notifyNewUserEmail(agent);
                }
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

    public void save() {
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
        if (availableRoleMemberList !=null){
            availableRoleMemberList.clear();
        }
        if (selectedRoleMemberList != null){
            selectedRoleMemberList.clear();
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

    public List getMembersList() {
        Set members = getSite().getMembers();
        List memberList = new ArrayList();
        for (Iterator i = members.iterator(); i.hasNext();) {
            Member member = (Member) i.next();

            DecoratedMember decoratedMember = new DecoratedMember(this, getAgentManager().getAgent((member.getUserId())));
            memberList.add(new SelectItem(decoratedMember.getBase().getId().getValue(), decoratedMember.getBase().getDisplayName(), "member"));
        }

        return memberList;
    }

    public List getRoles() {
        List returned = new ArrayList();
        Set roles = getSite().getRoles();
        String siteId = getSite().getId();

        for (Iterator i = roles.iterator(); i.hasNext();) {
            Role role = (Role) i.next();
            Agent roleAgent = getAgentManager().getWorksiteRole(role.getId(), siteId);
            returned.add(new SelectItem(roleAgent.getId().getValue(), role.getId(), "role"));
        }

        return returned;
    }

    public boolean getHasGroups() {
        return getSite().hasGroups();
    }

    public List getGroups() {
        List siteGroups = getSiteGroups();
        return siteGroups;
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

    public String[] getAvailableRoleMember() {
        return availableRoleMember;
    }

    public void setAvailableRoleMember(String[] availableRoleMember) {
        this.availableRoleMember = availableRoleMember;
    }

    public void setAvailableRoleMemberList(List availableRoleMemberList) {
        this.availableRoleMemberList = availableRoleMemberList;
    }

    public List getAvailableRoleMemberList() {

            availableRoleMemberList = new ArrayList();

            List roleMemberList = new ArrayList();
            roleMemberList.addAll(getRoles());

            roleMemberList.add(new SelectItem("null", LIST_SEPERATOR, ""));
            if (!isMaxList()) {
                roleMemberList.addAll(getMembersList());
            }

            for (Iterator idx = roleMemberList.iterator(); idx.hasNext();) {
                SelectItem availableItem = (SelectItem) idx.next();
                boolean matchFound = false;
                for (Iterator jdx = getSelectedRoleMemberList().iterator(); jdx.hasNext();) {
                    SelectItem selecteItem = (SelectItem) jdx.next();
                    if (selecteItem.getValue().toString().equals(availableItem.getValue().toString())) {
                        matchFound = true;
                        break;

                    }

                }
                if (!matchFound){
                    availableRoleMemberList.add(availableItem);
                }
            }

        return availableRoleMemberList;
    }


    public String[] getSelectedRoleMember() {
        return selectedRoleMember;
    }

    public void setSelectedRoleMember(String[] selectedRoleMember) {
        this.selectedRoleMember = selectedRoleMember;
    }

    public List getSelectedRoleMemberList() {

            selectedRoleMemberList = new ArrayList();
            for (Iterator i = getSelectedMembers().iterator(); i.hasNext();) {
                DecoratedMember decoratedMember = (DecoratedMember) i.next();
                String description = "member";
                if (decoratedMember.getBase().isRole()) {
                    description = "role";
                }
                selectedRoleMemberList.add(new SelectItem(decoratedMember.getBase().getId().getValue(), decoratedMember.getBase().getDisplayName(), description));
            }

        return selectedRoleMemberList;
    }

    public void setSelectedRoleMemberList(List selectedRoleMemberList) {
        this.selectedRoleMemberList = selectedRoleMemberList;
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

    public String processActionAdd() {
        String[] selected = getAvailableRoleMember();
        if (selected.length < 1) {
            //put in a message that they need to select something from the lsit to add
            return "main";
        }

        for (int i = 0; i < selected.length; i++) {

            SelectItem addItem = removeItems(selected[i], getAvailableRoleMemberList());
            if (addItem.getDescription().equals("role")){
               addAgent(getAgentManager().getAgent(getIdManager().getId(addItem.getValue().toString())), "role_exists");
            } else if (addItem.getDescription().equals("member")){
                addAgent(getAgentManager().getAgent(addItem.getValue().toString()), "user_exists");
            }
            getSelectedRoleMemberList().add(addItem);
            setSelectedRoleMemberList(sortList(getSelectedRoleMemberList(), false));
        }

        return "main";
    }

    public String processActionRemoveSelected() {
        String[] selected = getSelectedRoleMember();
        if (selected.length < 1) {
            //put in a message that they need to select something from the lsit to add
            return "main";
        }

        for (int i = 0; i < selected.length; i++) {
            for (Iterator idx = getSelectedMembers().iterator(); idx.hasNext();) {
            DecoratedMember member = (DecoratedMember) idx.next();
            if (member.getBase().getId().toString().equals(selected[i])) {
                idx.remove();
            }
            getAvailableRoleMemberList().add(removeItems(selected[i], getSelectedRoleMemberList()));
            setAvailableRoleMemberList(sortList(getAvailableRoleMemberList(), true));


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

    /*public void processActionAddSelected() {
        for (Iterator i = getAvailableRoleMemberList().iterator(); i.hasNext();) {
            Member user = (Member) i.next();
            DecoratedMember member = new DecoratedMember(this, getAgentManager().getAgent(user.getUserId()));
            if (member.isSelected()) {
                Agent agentUser = member.getBase();
                addAgent(agentUser, "user_exists");
                member.setSelected(false);
            }
        }
    }*/

    protected void clearAudienceSelectionVariables() {
        ToolSession session = SessionManager.getCurrentToolSession();
        session.removeAttribute(AudienceSelectionHelper.AUDIENCE_FUNCTION);
        session.removeAttribute(AudienceSelectionHelper.AUDIENCE_QUALIFIER);
        session.removeAttribute(AudienceSelectionHelper.AUDIENCE_INSTRUCTIONS);
        session.removeAttribute(AudienceSelectionHelper.AUDIENCE_GLOBAL_TITLE);
        session.removeAttribute(AudienceSelectionHelper.AUDIENCE_INDIVIDUAL_TITLE);
        session.removeAttribute(AudienceSelectionHelper.AUDIENCE_GROUP_TITLE);
        session.removeAttribute(AudienceSelectionHelper.AUDIENCE_PUBLIC_TITLE);
        session.removeAttribute(AudienceSelectionHelper.AUDIENCE_SELECTED_TITLE);
        session.removeAttribute(AudienceSelectionHelper.AUDIENCE_FILTER_INSTRUCTIONS);
        session.removeAttribute(AudienceSelectionHelper.AUDIENCE_GUEST_EMAIL);
        session.removeAttribute(AudienceSelectionHelper.AUDIENCE_WORKSITE_LIMITED);
        session.removeAttribute(AudienceSelectionHelper.AUDIENCE_BACK_TARGET);
        session.removeAttribute(AudienceSelectionHelper.AUDIENCE_CANCEL_TARGET);
        session.removeAttribute(AudienceSelectionHelper.AUDIENCE_PUBLIC_URL);
        session.removeAttribute(AudienceSelectionHelper.AUDIENCE_SAVE_NOTIFY_TARGET);
        session.removeAttribute(AudienceSelectionHelper.AUDIENCE_SAVE_TARGET);
        session.removeAttribute(AudienceSelectionHelper.AUDIENCE_PORTFOLIO_WIZARD);
    }

    public String getStepString() {
        return stepString;
    }

    /*   protected boolean validateEmail(String displayName) {

    if (!emailPattern.matcher(displayName).matches()) {
       //errors.rejectValue("displayName", "Invalid email address",
       //      new Object[0], "Invalid email address");
       return false;
    }

    try {
       InternetAddress.parse(displayName, true);
    }
    catch (AddressException e) {
       //errors.rejectValue("displayName", "Invalid email address",
       //      new Object[0], "Invalid email address");
       return false;
    }

    return true;
 }   */
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
            StringBuffer buf = new StringBuffer();
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
}

package org.theospi.portfolio.security.tool;

import org.theospi.portfolio.shared.tool.HelperToolBase;
import org.theospi.portfolio.shared.tool.PagingList;
import org.theospi.portfolio.security.AuthorizationFacade;
import org.theospi.portfolio.security.Authorization;
import org.theospi.portfolio.security.AudienceSelectionHelper;
import org.sakaiproject.metaobj.shared.mgt.IdManager;
import org.sakaiproject.metaobj.shared.mgt.AgentManager;
import org.sakaiproject.metaobj.shared.model.Id;
import org.sakaiproject.metaobj.shared.model.Agent;
import org.sakaiproject.service.legacy.authzGroup.Role;
import org.sakaiproject.service.legacy.authzGroup.Member;
import org.sakaiproject.service.legacy.site.SiteService;
import org.sakaiproject.service.legacy.site.Site;
import org.sakaiproject.service.legacy.site.Group;
import org.sakaiproject.service.legacy.user.User;
import org.sakaiproject.api.kernel.tool.Placement;
import org.sakaiproject.api.kernel.tool.ToolManager;
import org.sakaiproject.exception.IdUnusedException;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import java.util.*;

import com.sun.faces.util.MessageFactory;

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
   private String searchUsers;
   private String searchEmails;
   private Site site;

   private List selectedRolesFilter;
   private List selectedGroupsFilter;
   private PagingList browseUsers = null;

   private String function;
   private Id qualifier;

   private boolean publicAudience = false;

   public List getSelectedMembers() {
      if (getAttribute(AudienceSelectionHelper.AUDIENCE_FUNCTION) != null) {
         selectedMembers = fillMemberList();
      }
      return selectedMembers;
   }

   protected List fillMemberList() {
      List returned = new ArrayList();

      originalMembers = new ArrayList();

      String id = (String)getAttribute(AudienceSelectionHelper.AUDIENCE_QUALIFIER);
      setQualifier(getIdManager().getId(id));
      setFunction((String)getAttribute(AudienceSelectionHelper.AUDIENCE_FUNCTION));

      List authzs = getAuthzManager().getAuthorizations(null, getFunction(), getQualifier());

      for (Iterator i=authzs.iterator();i.hasNext();) {
         Authorization authz = (Authorization)i.next();
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

   public void setIdManager(IdManager idManager) {
      this.idManager = idManager;
   }

   public String getInstructions() {
      return (String) getAttributeOrDefault(AudienceSelectionHelper.AUDIENCE_INSTRUCTIONS);
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

   public boolean isPublicCapable() {
      return getAttribute(AudienceSelectionHelper.AUDIENCE_PUBLIC_TITLE) != null;
   }

   public boolean isPublicAudience() {
      if (getAttribute(AudienceSelectionHelper.AUDIENCE_PUBLIC_FLAG) != null) {
         publicAudience =
            "true".equalsIgnoreCase((String)getAttribute(AudienceSelectionHelper.AUDIENCE_PUBLIC_FLAG));
         removeAttribute(AudienceSelectionHelper.AUDIENCE_PUBLIC_FLAG);
      }
      return publicAudience;
   }

   public boolean isWorksiteLimited() {
      if (getAttribute(AudienceSelectionHelper.AUDIENCE_WORKSITE_LIMITED) != null) {
         return "true".equalsIgnoreCase((String)getAttribute(AudienceSelectionHelper.AUDIENCE_WORKSITE_LIMITED));
      }
      return false;
   }

   public void setPublicAudience(boolean publicAudience) {
      this.publicAudience = publicAudience;
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

   public List getSiteRoles() {
      List returned = new ArrayList();
      Set roles = getSite().getRoles();
      String siteId = getSite().getId();

      for (Iterator i=roles.iterator();i.hasNext();) {
         Role role = (Role)i.next();
         Agent roleAgent = getAgentManager().getWorksiteRole(role.getId(), siteId);
         returned.add(createSelect(roleAgent.getId().getValue(), role.getId()));
      }

      return returned;
   }

   public List getSiteGroups() {
      List returned = new ArrayList();
      Collection groups = getSite().getGroups();

      for (Iterator i=groups.iterator();i.hasNext();) {
         Group group = (Group)i.next();
         returned.add(createSelect(group.getId(), group.getDescription()));
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

   public void processActionRemove(ActionEvent event) {
      for (Iterator i=getSelectedMembers().iterator();i.hasNext();) {
         DecoratedMember member = (DecoratedMember)i.next();
         if (member.isSelected()) {
            i.remove();
         }
      }
   }

   public String processActionAddUser() {
      boolean worksiteLimited = isWorksiteLimited();
      if (!findByEmailOrDisplayName(getSearchUsers(), false, worksiteLimited)) {
         FacesContext.getCurrentInstance().addMessage(null,
             MessageFactory.getMessage(FacesContext.getCurrentInstance(),
                 worksiteLimited?"worksite_user_not_found":"user_not_found", (new Object[] { getSearchUsers() })));
      }
      else {
         setSearchUsers("");
      }
      return "tool";
   }

   public String processActionAddEmail() {
      boolean worksiteLimited = isWorksiteLimited();
      if (!findByEmailOrDisplayName(getSearchEmails(), true, worksiteLimited)) {
         if (worksiteLimited) {
            FacesContext.getCurrentInstance().addMessage(null,
                MessageFactory.getMessage(FacesContext.getCurrentInstance(),
                    "worksite_user_not_found", (new Object[] { getSearchUsers() })));
         }
      }
      else {
         setSearchEmails("");
      }
      return "tool";
   }

   public String processActionAddGroup() {
      for (Iterator i=getSelectedRoles().iterator();i.hasNext();) {
         String roleId = (String)i.next();
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
    *
    * @param displayName - for a guest user, this is the email address
    * @return
    */
   protected boolean findByEmailOrDisplayName(String displayName, boolean includeEmail, boolean worksiteLimited) {
      List retVal = new ArrayList();

      List guestUsers = null;

      if (includeEmail) {
         guestUsers = getAgentManager().findByProperty("email", displayName);
      }

      if (guestUsers == null || guestUsers.size() == 0) {
         guestUsers = getAgentManager().findByProperty("displayName", displayName);
      }

      if (guestUsers != null) {
         retVal.addAll(guestUsers);
      }

      boolean found = false;

      for (Iterator i=retVal.iterator();i.hasNext();) {
         found = true;
         Agent agent = (Agent)i.next();
         if (worksiteLimited) {
            if (!checkWorksiteMember(agent)) {
               return false;
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
      }
      else {
         FacesContext.getCurrentInstance().addMessage(null,
             MessageFactory.getMessage(FacesContext.getCurrentInstance(),
                 key, (new Object[] { agent.getDisplayName() })));
      }
   }

   protected boolean checkWorksiteMember(Agent agent) {
      List roles = agent.getWorksiteRoles(getSite().getId());
      return (roles != null && roles.size() > 0);
   }

   public String processActionSave() {
      List added = new ArrayList();

      for (Iterator i=getSelectedMembers().iterator();i.hasNext();) {
         DecoratedMember member = (DecoratedMember)i.next();

         if (originalMembers.contains(member.getBase())) {
            originalMembers.remove(member.getBase());
         }
         else {
            added.add(member.getBase());
         }
      }

      setSelectedMembers(null);
      addMembers(added);
      removeMembers(originalMembers);

      return returnToCaller();
   }

   protected void addMembers(List added) {
      for (Iterator i=added.iterator();i.hasNext();) {
         Agent agent = (Agent)i.next();

         getAuthzManager().createAuthorization(agent,
            getFunction(), getQualifier());
      }
   }

   protected void removeMembers(List added) {
      for (Iterator i=added.iterator();i.hasNext();) {
         Agent agent = (Agent)i.next();

         getAuthzManager().deleteAuthorization(agent,
            getFunction(), getQualifier());
      }
   }

   public void processActionApplyFilter(ActionEvent event) {
      Set members = getGroupMembers();
      List siteUsers = new ArrayList();
      MemberFilter filter = new MemberFilter(this);

      for (Iterator i=members.iterator();i.hasNext();) {
         Member user = (Member)i.next();
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

      for (Iterator i=filterGroups.iterator();i.hasNext();) {
         String groupId = (String)i.next();
         Group group = getSite().getGroup(groupId);
         members.addAll(group.getMembers());
      }

      return members;
   }

   public List getRoles() {
      List siteRoles = getSiteRoles();
      siteRoles.add(0, createSelect("", "<none>"));
      return siteRoles;
   }

   public boolean getHasGroups() {
      return getSite().hasGroups();
   }

   public List getGroups() {
      List siteGroups = getSiteGroups();
      siteGroups.add(0, createSelect("", "<none>"));
      return siteGroups;
   }

   public List getSelectedRolesFilter() {
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
         processActionApplyFilter(null);
      }
      return browseUsers;
   }

   public void setBrowseUsers(PagingList browseUsers) {
      this.browseUsers = browseUsers;
   }

   public void processActionAddBrowseSelected(ActionEvent event) {
      for (Iterator i=getBrowseUsers().getWholeList().iterator();i.hasNext();) {
         DecoratedMember member = (DecoratedMember)i.next();
         if (member.isSelected()) {
            Agent user = member.getBase();
            addAgent(user, "user_exists");
            member.setSelected(false);
         }
      }
      getSelectedRoles().clear();
   }

}

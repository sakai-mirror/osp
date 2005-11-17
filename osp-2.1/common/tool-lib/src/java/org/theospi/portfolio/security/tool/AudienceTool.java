package org.theospi.portfolio.security.tool;

import org.theospi.portfolio.shared.tool.HelperToolBase;
import org.theospi.portfolio.security.AuthorizationFacade;
import org.theospi.portfolio.security.Authorization;
import org.theospi.portfolio.security.AudienceSelectionHelper;
import org.sakaiproject.metaobj.shared.mgt.IdManager;
import org.sakaiproject.metaobj.shared.model.Id;
import org.sakaiproject.service.legacy.authzGroup.Role;
import org.sakaiproject.service.legacy.site.SiteService;
import org.sakaiproject.service.legacy.site.Site;
import org.sakaiproject.service.legacy.site.Group;
import org.sakaiproject.api.kernel.tool.Placement;
import org.sakaiproject.api.kernel.tool.ToolManager;
import org.sakaiproject.exception.IdUnusedException;

import java.util.*;

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

   private List selectedMembers = null;
   private List originalMembers = null;
   private List selectedRoles = null;
   private String searchUsers;
   private String searchEmails;
   private Site site;

   private boolean publicAudience = false;

   public List getSelectedMembers() {
      if (getAttribute(AudienceSelectionHelper.AUDIENCE_FUNCTION) != null) {
         selectedMembers = fillMemberList();
      }
      return selectedMembers;
   }

   protected List fillMemberList() {
      List returned = new ArrayList();

      originalMembers = getAuthzManager().getAuthorizations(null, getFunction(), getQualifier());

      for (Iterator i=originalMembers.iterator();i.hasNext();) {
         Authorization authz = (Authorization)i.next();
         returned.add(new DecoratedMember(this, authz.getAgent()));
      }

      removeAttributes(
         new String[]{AudienceSelectionHelper.AUDIENCE_FUNCTION, AudienceSelectionHelper.AUDIENCE_QUALIFIER});

      return returned;
   }

   protected Id getQualifier() {
      String id = (String)getAttribute(AudienceSelectionHelper.AUDIENCE_QUALIFIER);
      return getIdManager().getId(id);
   }

   protected String getFunction() {
      return (String)getAttribute(AudienceSelectionHelper.AUDIENCE_FUNCTION);
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

      for (Iterator i=roles.iterator();i.hasNext();) {
         Role role = (Role)i.next();
         for (int it=0;it < 15;it++){
            returned.add(createSelect(role.getId(), role.getId()));
         }
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
}

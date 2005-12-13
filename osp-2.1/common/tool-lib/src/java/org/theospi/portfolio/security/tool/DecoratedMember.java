package org.theospi.portfolio.security.tool;

import org.sakaiproject.metaobj.shared.model.Agent;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Nov 16, 2005
 * Time: 3:56:31 PM
 * To change this template use File | Settings | File Templates.
 */
public class DecoratedMember {

   private Agent base;
   private AudienceTool parent;
   private boolean selected = false;

   public DecoratedMember(AudienceTool parent, Agent base) {
      this.base = base;
      this.parent = parent;
   }

   public String getDisplayName() {
      String baseName = base.getId().getValue();
      if (base.isRole()) {
         return parent.getMessageFromBundle("decorated_role_format",
               new Object[]{base.getDisplayName()});
      }
      else {
         return parent.getMessageFromBundle("decorated_user_format",
               new Object[]{base.getDisplayName(), baseName});
      }
   }

   public boolean isSelected() {
      return selected;
   }

   public void setSelected(boolean selected) {
      this.selected = selected;
   }

   public Agent getBase() {
      return base;
   }

   public void setBase(Agent base) {
      this.base = base;
   }

   public AudienceTool getParent() {
      return parent;
   }

   public void setParent(AudienceTool parent) {
      this.parent = parent;
   }

   public Agent getRole() {
      List roles = getBase().getWorksiteRoles(getParent().getSite().getId());
      if (roles.size() > 0) {
         return (Agent)roles.get(0);
      }
      return null;
   }

   public boolean equals(Object o) {
      if (this == o) {
         return true;
      }
      if (!(o instanceof DecoratedMember)) {
         return false;
      }

      final DecoratedMember decoratedMember = (DecoratedMember) o;

      if (!base.equals(decoratedMember.base)) {
         return false;
      }

      return true;
   }

   public int hashCode() {
      return base.hashCode();
   }
}

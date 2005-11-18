package org.theospi.portfolio.security.tool;

import org.sakaiproject.metaobj.shared.model.Agent;

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
   private boolean selectedForRemoval = false;

   public DecoratedMember(AudienceTool parent, Agent base) {
      this.base = base;
      this.parent = parent;
   }

   public String getDisplayName() {

      return base.getDisplayName() + " (" + base.getId().getValue() + ")";
   }

   public boolean isSelectedForRemoval() {
      return selectedForRemoval;
   }

   public void setSelectedForRemoval(boolean selectedForRemoval) {
      this.selectedForRemoval = selectedForRemoval;
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

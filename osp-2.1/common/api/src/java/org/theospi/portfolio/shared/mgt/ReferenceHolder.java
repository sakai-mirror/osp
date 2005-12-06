package org.theospi.portfolio.shared.mgt;

import org.sakaiproject.service.legacy.entity.Reference;
import org.sakaiproject.service.legacy.resource.cover.EntityManager;

import java.io.Serializable;
import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Nov 15, 2005
 * Time: 3:13:42 PM
 * To change this template use File | Settings | File Templates.
 */
public class ReferenceHolder implements Serializable {

   private transient Reference base;

   public ReferenceHolder() {
   }

   public ReferenceHolder(Reference base) {
      this.base = base;
   }

   public Reference getBase() {
      return base;
   }

   public void setBase(Reference base) {
      this.base = base;
   }

   private void writeObject(java.io.ObjectOutputStream out)
      throws IOException {
      out.writeObject(base.getReference());
   }

   private void readObject(java.io.ObjectInputStream in)
      throws IOException, ClassNotFoundException {
      String ref = (String) in.readObject();
      setBase(EntityManager.newReference(ref));
   }

   public boolean equals(Object o) {
      if (this == o) {
         return true;
      }
      if (!(o instanceof ReferenceHolder)) {
         return false;
      }

      final ReferenceHolder referenceHolder = (ReferenceHolder) o;

      if (base != null ? !base.getReference().equals(referenceHolder.base.getReference()) : referenceHolder.base != null) {
         return false;
      }

      return true;
   }

   public int hashCode() {
      return (base != null ? base.hashCode() : 0);
   }

}

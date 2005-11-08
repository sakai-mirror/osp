package org.theospi.portfolio.shared.mgt;

import org.sakaiproject.metaobj.shared.ArtifactFinderManager;
import org.sakaiproject.metaobj.shared.ArtifactFinder;

import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Nov 7, 2005
 * Time: 6:36:26 PM
 * To change this template use File | Settings | File Templates.
 */
public class WrappedArtifactFinderManager implements ArtifactFinderManager {

   private ArtifactFinderManager base;
   private Map substitutions;

   public ArtifactFinder getArtifactFinderByType(String key) {
      ArtifactFinder finder = (ArtifactFinder) substitutions.get(key);

      if (finder != null) {
         return finder;
      }
      return base.getArtifactFinderByType(key);
   }

   public Map getFinders() {
      return base.getFinders();
   }

   public Map getSubstitutions() {
      return substitutions;
   }

   public void setSubstitutions(Map substitutions) {
      this.substitutions = substitutions;
   }

   public ArtifactFinderManager getBase() {
      return base;
   }

   public void setBase(ArtifactFinderManager base) {
      this.base = base;
   }
}

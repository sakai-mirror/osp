package org.theospi.portfolio.shared.mgt;

import java.util.Map;

import org.sakaiproject.metaobj.shared.ArtifactFinderManager;

public class AdditionalFinder {


   private Map additionalFinders;
   private ArtifactFinderManager artifactFinderManager;

   public Map getAdditionalFinders() {
      return additionalFinders;
   }

   public void setAdditionalFinders(Map additionalFinders) {
      this.additionalFinders = additionalFinders;
   }

   public ArtifactFinderManager getArtifactFinderManager() {
      return artifactFinderManager;
   }

   public void setArtifactFinderManager(ArtifactFinderManager artifactFinderManager) {
      this.artifactFinderManager = artifactFinderManager;
   }

   public void init() {
      getArtifactFinderManager().getFinders().putAll(getAdditionalFinders());
   }

}


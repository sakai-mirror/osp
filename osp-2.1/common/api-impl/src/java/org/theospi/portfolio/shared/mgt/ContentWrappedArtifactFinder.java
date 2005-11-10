package org.theospi.portfolio.shared.mgt;

import org.sakaiproject.metaobj.shared.mgt.impl.FileArtifactFinder;
import org.sakaiproject.metaobj.shared.mgt.impl.ContentResourceArtifact;
import org.sakaiproject.metaobj.shared.model.Artifact;
import org.sakaiproject.metaobj.shared.model.Id;
import org.sakaiproject.service.legacy.content.ContentResource;
import org.sakaiproject.service.legacy.entity.Entity;
import org.theospi.portfolio.shared.intf.EntityContextFinder;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Nov 7, 2005
 * Time: 4:49:49 PM
 * To change this template use File | Settings | File Templates.
 */
public class ContentWrappedArtifactFinder extends FileArtifactFinder implements EntityContextFinder {

   public Artifact load(Id artifactId) {
      return super.load(artifactId);
   }

   public Artifact loadInContext(Id artifactId, String context, String siteId, String contextId) {
      Artifact art = super.load(artifactId);

      if (art instanceof ContentResourceArtifact) {
         return wrap((ContentResourceArtifact)art, context, siteId, contextId);
      }

      return art;
   }

   protected Artifact wrap(ContentResourceArtifact contentResourceArtifact,
                           String context, String siteId, String contextId) {
      ContentResource resource = contentResourceArtifact.getBase();

      ContentResource wrapped = new ContentEntityWrapper(resource,
            buildRef(context, siteId, contextId, resource));

      contentResourceArtifact.setBase(wrapped);

      return contentResourceArtifact;
   }

   protected String buildRef(String context, String siteId, String contextId, ContentResource resource) {
      return Entity.SEPARATOR + context + Entity.SEPARATOR + siteId + Entity.SEPARATOR + contextId + resource.getReference();
   }

}

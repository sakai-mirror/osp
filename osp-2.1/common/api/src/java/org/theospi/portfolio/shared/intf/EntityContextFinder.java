package org.theospi.portfolio.shared.intf;

import org.sakaiproject.metaobj.shared.model.Artifact;
import org.sakaiproject.metaobj.shared.model.Id;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Nov 7, 2005
 * Time: 5:06:43 PM
 * To change this template use File | Settings | File Templates.
 */
public interface EntityContextFinder {

   public Artifact loadInContext(Id artifactId, String context, String siteId, String contextId);

}

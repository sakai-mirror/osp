package org.theospi.portfolio.security.model;

import org.sakaiproject.metaobj.shared.model.Id;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Aug 16, 2005
 * Time: 11:29:55 AM
 * To change this template use File | Settings | File Templates.
 */
public interface CleanupableService {

   public void cleanupTool(Id toolId);
   
}

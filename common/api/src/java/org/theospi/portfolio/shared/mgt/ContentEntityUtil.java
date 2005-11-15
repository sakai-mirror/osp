package org.theospi.portfolio.shared.mgt;

import org.sakaiproject.service.legacy.entity.Entity;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Nov 14, 2005
 * Time: 6:13:21 PM
 * To change this template use File | Settings | File Templates.
 */
public class ContentEntityUtil {

   private static ContentEntityUtil instance = new ContentEntityUtil();

   public String buildRef(String producer, String siteId, String contextId, String reference) {
      return Entity.SEPARATOR + producer +
         Entity.SEPARATOR + siteId + Entity.SEPARATOR + contextId + reference;
   }

   public static ContentEntityUtil getInstance() {
      return instance;
   }
}

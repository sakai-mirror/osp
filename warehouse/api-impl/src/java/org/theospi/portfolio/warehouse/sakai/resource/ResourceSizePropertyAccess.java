package org.theospi.portfolio.warehouse.sakai.resource;

import org.sakaiproject.content.api.ContentCollection;
import org.sakaiproject.content.cover.ContentHostingService;
import org.theospi.portfolio.warehouse.intf.PropertyAccess;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Dec 19, 2005
 * Time: 12:50:22 PM
 * To change this template use File | Settings | File Templates.
 */
public class ResourceSizePropertyAccess implements PropertyAccess {

   public Object getPropertyValue(Object source) throws Exception {

     ContentCollection collection = (ContentCollection) source;
      return ContentHostingService.getQuota(collection);
   }
}

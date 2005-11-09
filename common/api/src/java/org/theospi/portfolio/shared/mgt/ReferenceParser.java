package org.theospi.portfolio.shared.mgt;

import org.sakaiproject.service.legacy.entity.Entity;
import org.sakaiproject.service.legacy.entity.EntityProducer;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Nov 8, 2005
 * Time: 2:43:22 PM
 * To change this template use File | Settings | File Templates.
 */
public class ReferenceParser {

   private String context;
   private String id;
   private String ref;

   public ReferenceParser(String reference, EntityProducer parent) {
      // with /pres/12345/content/etc/etc.xml
      String baseRef = reference.substring(parent.getLabel().length() + 2); // lenght of 2 sperators
      int sep = baseRef.indexOf(Entity.SEPARATOR);
      id = baseRef.substring(0, sep);
      ref = baseRef.substring(sep);
      context = parent.getLabel();
   }

   public String getContext() {
      return context;
   }

   public void setContext(String context) {
      this.context = context;
   }

   public String getId() {
      return id;
   }

   public void setId(String id) {
      this.id = id;
   }

   public String getRef() {
      return ref;
   }

   public void setRef(String ref) {
      this.ref = ref;
   }

}

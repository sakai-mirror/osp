/*
 * $Header: /opt/CVS/osp2.x/common/api/src/java/org/theospi/portfolio/security/model/AuthZMap.java,v 1.1 2005/07/18 23:46:59 jellis Exp $
 * $Revision$
 * $Date$
 */
package org.theospi.portfolio.security.model;

import org.theospi.portfolio.security.AuthorizationFacade;
import org.sakaiproject.metaobj.shared.model.Id;

import java.util.HashMap;

public class AuthZMap extends HashMap {
   private AuthorizationFacade authzFacade;
   private String prefix;
   private Id qualifier;

   public AuthZMap(AuthorizationFacade authzFacade, Id qualifier){
      this.authzFacade = authzFacade;
      this.prefix = "";
      this.qualifier = qualifier;
   }

   public AuthZMap(AuthorizationFacade authzFacade, String prefix, Id qualifier){
      this.authzFacade = authzFacade;
      this.prefix = prefix;
      this.qualifier = qualifier;
   }

   public Object get(Object key){
      if (super.get(key) == null) {
         super.put(key, new Boolean(authzFacade.isAuthorized(prefix + key.toString(), qualifier)));
      }
      return super.get(key);      
   }
}

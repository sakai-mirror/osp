package org.theospi.portfolio.security;

import org.sakaiproject.service.legacy.security.SecurityAdvisor;

import java.util.Map;
import java.util.List;
import java.util.Hashtable;
import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Nov 4, 2005
 * Time: 5:02:36 PM
 * To change this template use File | Settings | File Templates.
 */
public class AllowMapSecurityAdvisor implements SecurityAdvisor {

   private Map allowedReferences;

   public AllowMapSecurityAdvisor(Map allowedReferences) {
      this.allowedReferences = allowedReferences;
   }

   public AllowMapSecurityAdvisor(String function, List references) {
      this.allowedReferences = new Hashtable();
      allowedReferences.put(function, references);
   }

   public AllowMapSecurityAdvisor(String function, String reference) {
      this.allowedReferences = new Hashtable();
      List references = new ArrayList();
      references.add(reference);
      allowedReferences.put(function, references);
   }

   public SecurityAdvice isAllowed(String userId, String function, String reference) {
      List refs = (List)allowedReferences.get(function);
      if (refs != null) {
         if (refs.contains(reference)) {
            return SecurityAdvice.ALLOWED;
         }
      }

      return SecurityAdvice.PASS;
   }
}

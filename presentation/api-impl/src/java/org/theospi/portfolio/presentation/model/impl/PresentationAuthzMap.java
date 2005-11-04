/**********************************************************************************
 *
 * $Header: /opt/CVS/osp2.x/presentation/api-impl/src/java/org/theospi/portfolio/presentation/model/impl/PresentationAuthzMap.java,v 1.1 2005/08/10 21:08:30 chmaurer Exp $
 *
 ***********************************************************************************
 * Copyright (c) 2005 the r-smart group, inc.
 **********************************************************************************/
package org.theospi.portfolio.presentation.model.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.metaobj.shared.model.Agent;
import org.theospi.portfolio.presentation.model.Presentation;
import org.theospi.portfolio.presentation.PresentationFunctionConstants;

import java.util.Hashtable;
import java.util.HashMap;

public class PresentationAuthzMap extends HashMap {
   protected final transient Log logger = LogFactory.getLog(getClass());

   private Agent currentAgent;
   private Presentation presentation;
   private boolean owner = false;

   /**
    * Constructs a new, empty hashtable with a default initial capacity (11)
    * and load factor, which is <tt>0.75</tt>.
    */
   public PresentationAuthzMap(Agent currentAgent, Presentation presentation) {
      this.currentAgent = currentAgent;
      this.presentation = presentation;
      owner = presentation.getOwner().getId().equals(currentAgent.getId());
   }

   /**
    * Returns the value to which the specified key is mapped in this hashtable.
    *
    * @param key a key in the hashtable.
    * @return the value to which the key is mapped in this hashtable;
    *         <code>null</code> if the key is not mapped to any value in
    *         this hashtable.
    * @throws NullPointerException if the key is <code>null</code>.
    * @see #put(Object, Object)
    */
   public Object get(Object key) {
      if (owner) {
         return new Boolean(true); // owner can do anything
      }

      String func = PresentationFunctionConstants.PRESENTATION_PREFIX + key.toString();

      if (func.equals(PresentationFunctionConstants.VIEW_PRESENTATION)) {
         return new Boolean(true);
      }
      else {
         return new Boolean(false);
      }
   }
}

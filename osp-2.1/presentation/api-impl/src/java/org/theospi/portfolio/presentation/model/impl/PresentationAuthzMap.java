/**********************************************************************************
* $URL$
* $Id$
***********************************************************************************
*
* Copyright (c) 2005, 2006 The Sakai Foundation.
*
* Licensed under the Educational Community License, Version 1.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*      http://www.opensource.org/licenses/ecl1.php
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*
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

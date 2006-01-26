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
   private String siteId;
   private String ref;

   public ReferenceParser(String reference, EntityProducer parent) {
      // with /pres/<siteid>/<preseId>/content/etc/etc.xml
      String baseRef = reference.substring(parent.getLabel().length() + 2); // lenght of 2 sperators
      int sep = baseRef.indexOf(Entity.SEPARATOR);
      siteId = baseRef.substring(0, sep);
      baseRef = baseRef.substring(sep + 1);

      sep = baseRef.indexOf(Entity.SEPARATOR);
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

   public String getSiteId() {
      return siteId;
   }

   public void setSiteId(String siteId) {
      this.siteId = siteId;
   }

}

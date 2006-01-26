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

import org.sakaiproject.service.legacy.site.Site;
import org.sakaiproject.service.legacy.content.ContentResource;
import org.sakaiproject.service.legacy.entity.*;
import org.sakaiproject.service.framework.config.cover.ServerConfigurationService;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Nov 7, 2005
 * Time: 1:40:10 PM
 * To change this template use File | Settings | File Templates.
 */
public abstract class OspEntityProducerBase implements EntityProducer {

   private EntityManager entityManager;
   private HttpAccess httpAccess;

   public boolean willArchiveMerge() {
      return false;
   }

   public boolean willImport() {
      return false;
   }

   public String archive(String siteId, Document doc, Stack stack, String archivePath, List attachments) {
      return null;
   }

   public String merge(String siteId, Element root, String archivePath, String fromSiteId, Map attachmentNames, Map userIdTrans, Set userListAllowImport) {
      return null;
   }

   public void importEntities(String fromContext, String toContext, List ids) {
   }

   public boolean parseEntityReference(String reference, Reference ref) {
      if (reference.startsWith(getContext())) {
         ref.set(getLabel(), null, reference, null, "");
         return true;
      }
      return false;
   }

   protected String getContext() {
      return Entity.SEPARATOR + getLabel() + Entity.SEPARATOR;
   }

   public String getEntityDescription(Reference ref) {
      return ref.getId();
   }

   public ResourceProperties getEntityResourceProperties(Reference ref) {
      ContentEntityWrapper entity = getContentEntityWrapper(ref);

      return entity.getBase().getProperties();
   }

   protected ContentEntityWrapper getContentEntityWrapper(Reference ref) {
      String wholeRef = ref.getReference();
      ReferenceParser parser = new ReferenceParser(wholeRef, this);
      ContentResource base =
         (ContentResource) getEntityManager().newReference(parser.getRef()).getEntity();
      return new ContentEntityWrapper(base, wholeRef);
   }

   public Entity getEntity(Reference ref) {
      return getContentEntityWrapper(ref);
   }

   public String getEntityUrl(Reference ref) {
      return ServerConfigurationService.getAccessUrl() + ref.getReference();
   }

   public Collection getEntityAuthzGroups(Reference ref) {
      return null;
   }

   public void syncWithSiteChange(Site site, EntityProducer.ChangeType change) {
   }

   public EntityManager getEntityManager() {
      return entityManager;
   }

   public void setEntityManager(EntityManager entityManager) {
      this.entityManager = entityManager;
   }

   public HttpAccess getHttpAccess() {
      return httpAccess;
   }

   public void setHttpAccess(HttpAccess httpAccess) {
      this.httpAccess = httpAccess;
   }

   public void destroy() {
      
   }

}

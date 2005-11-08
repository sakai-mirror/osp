package org.theospi.portfolio.shared.mgt;

import org.sakaiproject.service.legacy.site.Site;
import org.sakaiproject.service.legacy.content.ContentResource;
import org.sakaiproject.service.legacy.entity.*;
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
      ReferenceParser parser = new ReferenceParser(wholeRef);
      ContentResource base =
         (ContentResource) getEntityManager().newReference(parser.ref).getEntity();
      return new ContentEntityWrapper(base, wholeRef);
   }

   public Entity getEntity(Reference ref) {
      return getContentEntityWrapper(ref);
   }

   public String getEntityUrl(Reference ref) {
      return null;
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

   protected class ReferenceParser {
      public String context;
      public String id;
      public String ref;

      public ReferenceParser(String reference) {
         // with /pres/12345/content/etc/etc.xml
         String baseRef = reference.substring(getContext().length());
         int sep = baseRef.indexOf(Entity.SEPARATOR);
         id = baseRef.substring(0, sep);
         ref = baseRef.substring(sep);
         context = getLabel();
      }
   }
}

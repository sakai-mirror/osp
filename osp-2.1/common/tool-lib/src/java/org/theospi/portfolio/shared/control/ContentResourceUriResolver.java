package org.theospi.portfolio.shared.control;

import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import javax.xml.transform.URIResolver;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.exception.ServerOverloadException;
import org.sakaiproject.service.framework.config.ServerConfigurationService;
import org.sakaiproject.service.legacy.content.ContentResource;
import org.sakaiproject.service.legacy.entity.EntityManager;
import org.sakaiproject.service.legacy.entity.Reference;

public class ContentResourceUriResolver implements URIResolver {
   
   private EntityManager entityManager;
   private ServerConfigurationService serverConfigurationService;

   protected final Log logger = LogFactory.getLog(getClass());
   
   public Source resolve(String href, String base) throws TransformerException {
      try {
         String accessUrl = getServerConfigurationService().getAccessUrl();
         String url = href.replaceAll(accessUrl, "");
         Reference ref = getEntityManager().newReference(url);
         return new StreamSource(((ContentResource)ref.getEntity()).streamContent());
      } catch (ServerOverloadException e) {
         logger.error("", e);
      }
      return null;
   }

   public EntityManager getEntityManager() {
      return entityManager;
   }

   public void setEntityManager(EntityManager entityManager) {
      this.entityManager = entityManager;
   }

   public ServerConfigurationService getServerConfigurationService() {
      return serverConfigurationService;
   }

   public void setServerConfigurationService(
         ServerConfigurationService serverConfigurationService) {
      this.serverConfigurationService = serverConfigurationService;
   }

}

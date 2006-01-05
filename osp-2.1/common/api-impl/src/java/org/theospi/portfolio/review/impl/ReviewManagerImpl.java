package org.theospi.portfolio.review.impl;

import java.util.Date;
import java.util.List;

import org.sakaiproject.exception.IdUnusedException;
import org.sakaiproject.exception.PermissionException;
import org.sakaiproject.exception.TypeException;
import org.sakaiproject.metaobj.shared.mgt.AgentManager;
import org.sakaiproject.metaobj.shared.mgt.IdManager;
import org.sakaiproject.metaobj.shared.model.Agent;
import org.sakaiproject.metaobj.shared.model.Id;
import org.sakaiproject.service.legacy.content.ContentHostingService;
import org.sakaiproject.service.legacy.content.ContentResource;
import org.sakaiproject.service.legacy.entity.EntityManager;
import org.sakaiproject.service.legacy.entity.Reference;
import org.sakaiproject.service.legacy.security.SecurityService;
import org.springframework.orm.hibernate.support.HibernateDaoSupport;
import org.theospi.portfolio.review.mgt.ReviewManager;
import org.theospi.portfolio.review.model.Review;
import org.theospi.portfolio.security.AllowMapSecurityAdvisor;
import org.theospi.portfolio.security.AuthorizationFacade;
import org.theospi.portfolio.shared.mgt.ContentEntityUtil;
import org.theospi.portfolio.shared.model.Node;

public class ReviewManagerImpl extends HibernateDaoSupport implements ReviewManager {

   private AuthorizationFacade authorizationFacade;
   private SecurityService securityService;
   private EntityManager entityManager;
   private IdManager idManager;
   private ContentHostingService contentHosting = null;
   private AgentManager agentManager = null;
   
   public Review createNew(String owner, String description, String siteId, 
         Id securityQualifier, String securityViewFunction, String securityEditFunction) {
      Agent agent = getAgentManager().getAgent(owner);
      Review review = new Review(getIdManager().createId(), agent, description, 
            siteId, securityQualifier, securityViewFunction, securityEditFunction);

      return review;
   }

   public Review getReview(Id reviewId) {
      Review review = (Review)getHibernateTemplate().get(Review.class, reviewId);

      if (review == null) {
         return null;
      }

      if (review.getSecurityQualifier() != null) {
         getAuthorizationFacade().checkPermission(review.getSecurityViewFunction(),
               review.getSecurityQualifier());
      }

      return review;
   }
   
   public List getReviewsByParent(String parentId) {
      Object[] params = new Object[]{parentId};
      return getHibernateTemplate().find("from Review r where r.parent=? ", params);
   }
   
   public List getReviewsByParentAndType(String parentId, int type) {
      Object[] params = new Object[]{parentId, new Integer(type)};
      return getHibernateTemplate().find("from Review r where r.parent=? and r.type=? ", params);
   }

   public Review saveReview(Review review) {
      Date now = new Date(System.currentTimeMillis());
      //review.setModified(now);
      
      if (review.isNewObject()) {
         review.setCreated(now);
         getHibernateTemplate().save(review, review.getId());
         review.setNewObject(false);
      }
      else {
         getHibernateTemplate().saveOrUpdate(review);
      }    

      return review;
   }

   public void deleteReview(Review review) {
      getHibernateTemplate().delete(review);
   }

   public Reference decorateReference(Review review, String reference) {
      String fullRef = ContentEntityUtil.getInstance().buildRef(ReviewEntityProducer.REVIEW_PRODUCER,
            review.getSiteId(), review.getId().getValue(), reference);

         return getEntityManager().newReference(fullRef);
   }

   public List listReviews(String siteId) {
      return getHibernateTemplate().find("from Review where site_id=? ",
            siteId);
   }

   public Review getReview(String id) {
      return getReview(getIdManager().getId(id));
   }
   
   protected Node getNode(Id artifactId) {
      String id = getContentHosting().resolveUuid(artifactId.getValue());
      if (id == null) {
         return null;
      }

      getSecurityService().pushAdvisor(
         new AllowMapSecurityAdvisor(ContentHostingService.EVENT_RESOURCE_READ,
               getContentHosting().getReference(id)));

      try {
         ContentResource resource = getContentHosting().getResource(id);
         String ownerId = resource.getProperties().getProperty(resource.getProperties().getNamePropCreator());
         Agent owner = getAgentManager().getAgent((getIdManager().getId(ownerId)));

         return new Node(artifactId, resource, owner);
      }
      catch (PermissionException e) {
         logger.error("", e);
         throw new RuntimeException(e);
      }
      catch (IdUnusedException e) {
         logger.error("", e);
         throw new RuntimeException(e);
      }
      catch (TypeException e) {
         logger.error("", e);
         throw new RuntimeException(e);
      }
   }
   
   public Node getNode(Reference ref) {
      String nodeId = getContentHosting().getUuid(ref.getId());

      return getNode(getIdManager().getId(nodeId));
   }

   /**
    * @return Returns the authorizationFacade.
    */
   public AuthorizationFacade getAuthorizationFacade() {
      return authorizationFacade;
   }

   /**
    * @param authorizationFacade The authorizationFacade to set.
    */
   public void setAuthorizationFacade(AuthorizationFacade authorizationFacade) {
      this.authorizationFacade = authorizationFacade;
   }

   /**
    * @return Returns the entityManager.
    */
   public EntityManager getEntityManager() {
      return entityManager;
   }

   /**
    * @param entityManager The entityManager to set.
    */
   public void setEntityManager(EntityManager entityManager) {
      this.entityManager = entityManager;
   }

   /**
    * @return Returns the idManager.
    */
   public IdManager getIdManager() {
      return idManager;
   }

   /**
    * @param idManager The idManager to set.
    */
   public void setIdManager(IdManager idManager) {
      this.idManager = idManager;
   }

   /**
    * @return Returns the securityService.
    */
   public SecurityService getSecurityService() {
      return securityService;
   }

   /**
    * @param securityService The securityService to set.
    */
   public void setSecurityService(SecurityService securityService) {
      this.securityService = securityService;
   }

   /**
    * @return Returns the contentHosting.
    */
   public ContentHostingService getContentHosting() {
      return contentHosting;
   }

   /**
    * @param contentHosting The contentHosting to set.
    */
   public void setContentHosting(ContentHostingService contentHosting) {
      this.contentHosting = contentHosting;
   }

   /**
    * @return Returns the agentManager.
    */
   public AgentManager getAgentManager() {
      return agentManager;
   }

   /**
    * @param agentManager The agentManager to set.
    */
   public void setAgentManager(AgentManager agentManager) {
      this.agentManager = agentManager;
   }


}

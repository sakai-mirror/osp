/**********************************************************************************
* $URL: https://source.sakaiproject.org/svn/trunk/sakai/legacy/component/src/java/org/sakaiproject/component/framework/email/TestEmailService.java $
* $Id: TestEmailService.java 3831 2005-11-14 20:17:24Z ggolden@umich.edu $
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
package org.theospi.portfolio.review.impl;

import java.util.Iterator;
import java.util.List;

import org.sakaiproject.exception.IdUnusedException;
import org.sakaiproject.exception.PermissionException;
import org.sakaiproject.exception.TypeException;
import org.sakaiproject.metaobj.shared.mgt.AgentManager;
import org.sakaiproject.metaobj.shared.mgt.ContentEntityWrapper;
import org.sakaiproject.metaobj.shared.mgt.IdManager;
import org.sakaiproject.metaobj.shared.mgt.ContentEntityUtil;
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
import org.theospi.portfolio.shared.model.Node;

public class ReviewManagerImpl extends HibernateDaoSupport implements ReviewManager {

   private AuthorizationFacade authorizationFacade;
   private SecurityService securityService;
   private EntityManager entityManager;
   private IdManager idManager;
   private ContentHostingService contentHosting = null;
   private AgentManager agentManager = null;
   
   public Review createNew(String description, String siteId) {
      Review review = new Review(getIdManager().createId(), description, 
            siteId);

      return review;
   }

   public Review getReview(Id reviewId) {
      Review review = (Review)getHibernateTemplate().get(Review.class, reviewId);

      if (review == null) {
         return null;
      }

      return review;
   }
   
   public List getReviewsByParent(String parentId) {
      Object[] params = new Object[]{parentId};
      return getHibernateTemplate().find("from Review r where r.parent=? ", params);
   }
   
   public List getReviewsByParent(String parentId, String siteId, String producer) {
      Object[] params = new Object[]{parentId};
      return getReviewsByParent("from Review r where r.parent=? ", params, parentId, siteId, producer);
   }
   
   public List getReviewsByParentAndType(String parentId, int type, String siteId, String producer) {
      Object[] params = new Object[]{parentId, new Integer(type)};
      return getReviewsByParent("from Review r where r.parent=? and r.type=? ", params, parentId, siteId, producer);
   }
   
   protected List getReviewsByParent(String sql, Object[] params, String parentId, String siteId, String producer) {
      List reviews = getHibernateTemplate().find(sql, params);
      for (Iterator i = reviews.iterator(); i.hasNext();) {
         Review review = (Review) i.next();
         Node node = getNode(review.getReviewContent(), parentId, siteId, producer);
         review.setReviewContentNode(node);
      }
      
      return reviews;
   }

   public Review saveReview(Review review) {
      //review.setModified(now);
      
      if (review.isNewObject()) {
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

   public List listReviews(String siteId) {
      return getHibernateTemplate().find("from Review where site_id=? ",
            siteId);
   }

   public Review getReview(String id) {
      return getReview(getIdManager().getId(id));
   }
   
   protected Node getNode(Id artifactId, String parentId, String siteId, String producer) {
      Node node = getNode(artifactId);
      
      if (node == null) {
         return null;
      }
      ContentResource wrapped = new ContentEntityWrapper(node.getResource(),
            buildRef(siteId, parentId, node.getResource(), producer));

      return new Node(artifactId, wrapped, node.getTechnicalMetadata().getOwner());
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
   
 /*  
   public Node getNode2(Reference ref, String parentId, String siteId) {
      String nodeId = getContentHosting().getUuid(ref.getId());

      Node node = getNode(getIdManager().getId(nodeId), siteId);
      
      if (node == null) {
         return null;
      }
      ContentResource wrapped = new ContentEntityWrapper(node.getResource(),
            buildRef(siteId, parentId, node.getResource()));

      return new Node(artifactId, wrapped, node.getTechnicalMetadata().getOwner());
      
      
   }
   */
   protected String buildRef(String siteId, String contextId, ContentResource resource, 
         String producer) {
      return ContentEntityUtil.getInstance().buildRef(
         producer, siteId, contextId, resource.getReference());
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

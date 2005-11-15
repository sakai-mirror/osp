package org.theospi.portfolio.guidance.impl;

import org.springframework.orm.hibernate.support.HibernateDaoSupport;
import org.theospi.portfolio.guidance.mgt.GuidanceManager;
import org.theospi.portfolio.guidance.model.Guidance;
import org.theospi.portfolio.guidance.model.GuidanceItem;
import org.theospi.portfolio.guidance.model.GuidanceItemAttachment;
import org.theospi.portfolio.security.AllowMapSecurityAdvisor;
import org.theospi.portfolio.security.AuthorizationFacade;
import org.theospi.portfolio.shared.mgt.ContentEntityWrapper;
import org.theospi.portfolio.shared.mgt.ContentEntityUtil;
import org.sakaiproject.metaobj.shared.model.Id;
import org.sakaiproject.metaobj.shared.mgt.IdManager;
import org.sakaiproject.service.legacy.security.SecurityService;
import org.sakaiproject.service.legacy.content.ContentHostingService;
import org.sakaiproject.service.legacy.entity.EntityManager;
import org.sakaiproject.service.legacy.entity.Reference;

import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Nov 11, 2005
 * Time: 1:00:57 PM
 * To change this template use File | Settings | File Templates.
 */
public class GuidanceManagerImpl extends HibernateDaoSupport implements GuidanceManager {

   private AuthorizationFacade authorizationFacade;
   private SecurityService securityService;
   private EntityManager entityManager;
   private IdManager idManager;

   public Guidance createNew(String description, String siteId, Id securityQualifier, String securityFunction) {
      Guidance guidance = new Guidance(getIdManager().createId(),
         description, siteId, securityQualifier, securityFunction);

      GuidanceItem instruction = new GuidanceItem(guidance, Guidance.INSTRUCTION_TYPE);
      guidance.getItems().add(instruction);

      GuidanceItem example = new GuidanceItem(guidance, Guidance.EXAMPLE_TYPE);
      guidance.getItems().add(example);

      GuidanceItem rationale = new GuidanceItem(guidance, Guidance.RATIONALE_TYPE);
      guidance.getItems().add(rationale);

      return guidance;
   }

   public Guidance getGuidance(Id guidanceId) {
      Guidance guidance = (Guidance)getHibernateTemplate().get(Guidance.class, guidanceId);

      if (guidance == null) {
         return null;
      }

      if (guidance.getSecurityQualifier() != null) {
         getAuthorizationFacade().checkPermission(guidance.getSecurityFunction(),
            guidance.getSecurityQualifier());
      }

      // setup access to the files
      List refs = new ArrayList();
      for (Iterator i=guidance.getItems().iterator();i.hasNext();) {
         GuidanceItem item = (GuidanceItem)i.next();
         for (Iterator j=item.getAttachments().iterator();j.hasNext();) {
            GuidanceItemAttachment attachment = (GuidanceItemAttachment)j.next();
            refs.add(attachment.getBaseReference().getReference());
         }
      }
      
      getSecurityService().pushAdvisor(new AllowMapSecurityAdvisor(ContentHostingService.EVENT_RESOURCE_READ,
         refs));

      return guidance;
   }

   public Guidance saveGuidance(Guidance guidance) {
      getHibernateTemplate().saveOrUpdate(guidance);
      return guidance;
   }

   public void deleteGuidance(Guidance guidance) {
      getHibernateTemplate().delete(guidance);
   }

   public Reference decorateReference(Guidance guidance, String reference) {
      String fullRef = ContentEntityUtil.getInstance().buildRef(GuidanceEntityProducer.GUIDANCE_PRODUCER,
         guidance.getSiteId(), guidance.getId().getValue(), reference);

      return getEntityManager().newReference(fullRef);
   }


   public AuthorizationFacade getAuthorizationFacade() {
      return authorizationFacade;
   }

   public void setAuthorizationFacade(AuthorizationFacade authorizationFacade) {
      this.authorizationFacade = authorizationFacade;
   }

   public SecurityService getSecurityService() {
      return securityService;
   }

   public void setSecurityService(SecurityService securityService) {
      this.securityService = securityService;
   }

   public EntityManager getEntityManager() {
      return entityManager;
   }

   public void setEntityManager(EntityManager entityManager) {
      this.entityManager = entityManager;
   }

   public IdManager getIdManager() {
      return idManager;
   }

   public void setIdManager(IdManager idManager) {
      this.idManager = idManager;
   }
}

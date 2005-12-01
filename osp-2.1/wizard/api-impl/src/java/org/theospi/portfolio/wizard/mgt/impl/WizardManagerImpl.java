package org.theospi.portfolio.wizard.mgt.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.sakaiproject.metaobj.shared.mgt.AgentManager;
import org.sakaiproject.metaobj.shared.mgt.IdManager;
import org.sakaiproject.metaobj.shared.mgt.StructuredArtifactDefinitionManager;
import org.sakaiproject.metaobj.shared.model.Agent;
import org.sakaiproject.metaobj.shared.model.Id;
import org.sakaiproject.service.legacy.content.ContentHostingService;
import org.sakaiproject.service.legacy.entity.EntityManager;
import org.sakaiproject.service.legacy.entity.Reference;
import org.sakaiproject.service.legacy.security.SecurityService;
import org.springframework.orm.hibernate.support.HibernateDaoSupport;
import org.theospi.portfolio.security.AllowMapSecurityAdvisor;
import org.theospi.portfolio.security.AuthorizationFacade;
import org.theospi.portfolio.shared.mgt.ContentEntityUtil;
import org.theospi.portfolio.wizard.impl.WizardEntityProducer;
import org.theospi.portfolio.wizard.mgt.WizardManager;
import org.theospi.portfolio.wizard.model.Wizard;

public class WizardManagerImpl extends HibernateDaoSupport implements WizardManager {

   private AuthorizationFacade authorizationFacade;
   private SecurityService securityService;
   private EntityManager entityManager;
   private IdManager idManager;
   private StructuredArtifactDefinitionManager sadManager;
   private AgentManager agentManager;
   
   public Wizard createNew(String owner, String siteId, Id securityQualifier, String securityViewFunction, String securityEditFunction) {
      Agent agent = getAgentManager().getAgent(owner);
      Wizard wizard = new Wizard(getIdManager().createId(), agent, siteId, 
            securityQualifier, securityViewFunction, securityEditFunction);
//      (Agent owner, String siteId, Id securityQualifier,
//            String securityViewFunction, String securityEditFunction) {
      return wizard;
   }

   public Wizard getWizard(Id wizardId) {
      Wizard wizard = (Wizard)getHibernateTemplate().get(Wizard.class, wizardId);

      if (wizard == null) {
         return null;
      }

      if (wizard.getSecurityQualifier() != null) {
         getAuthorizationFacade().checkPermission(wizard.getSecurityViewFunction(),
               wizard.getSecurityQualifier());
      }

      // setup access to the files
      List refs = new ArrayList();
      //TODO put this back when it is ready
      /*
      for (Iterator i=wizard.getItems().iterator();i.hasNext();) {
         GuidanceItem item = (GuidanceItem)i.next();
         for (Iterator j=item.getAttachments().iterator();j.hasNext();) {
            GuidanceItemAttachment attachment = (GuidanceItemAttachment)j.next();
            refs.add(attachment.getBaseReference().getBase().getReference());
         }
      }
      */
      
      getSecurityService().pushAdvisor(new AllowMapSecurityAdvisor(ContentHostingService.EVENT_RESOURCE_READ,
         refs));

      return wizard;
   }

   public Wizard saveWizard(Wizard wizard) {
      Date now = new Date(System.currentTimeMillis());
      wizard.setModified(now);
      if (wizard.isNewObject()) {
         wizard.setCreated(now);
         getHibernateTemplate().save(wizard, wizard.getId());
         wizard.setNewObject(false);
      }
      else {
         getHibernateTemplate().saveOrUpdate(wizard);
      }

      return wizard;
   }

   public void deleteWizard(Wizard wizard) {
      getHibernateTemplate().delete(wizard);      
   }

   public Reference decorateReference(Wizard wizard, String reference) {
      String fullRef = ContentEntityUtil.getInstance().buildRef(WizardEntityProducer.WIZARD_PRODUCER,
            wizard.getSiteId(), wizard.getId().getValue(), reference);

      return getEntityManager().newReference(fullRef);
   }

   public List listWizardsByType(String owner, String siteId, String type) {
      Object[] params = new Object[]{owner, new Boolean(true), siteId, type};
      return getHibernateTemplate().find("from Wizard w where " +
            "(w.owner=? or w.published=?) and w.siteId=? and w.type=?", params);
   }
   
   public List listAllWizards(String owner, String siteId) {
      Object[] params = new Object[]{owner, new Boolean(true), siteId};
      return getHibernateTemplate().find("from Wizard w where " +
            "(w.owner=? or w.published=?) and w.siteId=? ", params);
   }
   
   public List findWizardsByOwner(String ownerId, String siteId) {
      Object[] params = new Object[]{ownerId, siteId};
      return getHibernateTemplate().find("from Wizard w where w.owner=? and w.siteId=? ", params);
   }
   
   public List findPublishedWizards(String siteId) {
      Object[] params = new Object[]{new Boolean(true), siteId};
      return getHibernateTemplate().find("from Wizard w where w.published=? and w.siteId=? ", params);
   }

   public Wizard getWizard(String id) {
      return getWizard(getIdManager().getId(id));
   }
   
   public List getAvailableForms(String siteId, String type) {
      return getSadManager().findHomes(
            getIdManager().getId(siteId));
      //ArtifactFinder finder = getArtifactFinderManager().getArtifactFinderByType(type);
      
   }
   

   public AuthorizationFacade getAuthorizationFacade() {
      return authorizationFacade;
   }

   public void setAuthorizationFacade(AuthorizationFacade authorizationFacade) {
      this.authorizationFacade = authorizationFacade;
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

   public SecurityService getSecurityService() {
      return securityService;
   }

   public void setSecurityService(SecurityService securityService) {
      this.securityService = securityService;
   }

   public StructuredArtifactDefinitionManager getSadManager() {
      return sadManager;
   }

   public void setSadManager(StructuredArtifactDefinitionManager sadManager) {
      this.sadManager = sadManager;
   }

   public AgentManager getAgentManager() {
      return agentManager;
   }

   public void setAgentManager(AgentManager agentManager) {
      this.agentManager = agentManager;
   }

}

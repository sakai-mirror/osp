package org.theospi.portfolio.wizard.mgt.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.sakaiproject.api.kernel.tool.cover.ToolManager;
import org.sakaiproject.exception.IdUnusedException;
import org.sakaiproject.exception.PermissionException;
import org.sakaiproject.metaobj.shared.mgt.AgentManager;
import org.sakaiproject.metaobj.shared.mgt.IdManager;
import org.sakaiproject.metaobj.shared.mgt.StructuredArtifactDefinitionManager;
import org.sakaiproject.metaobj.shared.model.Agent;
import org.sakaiproject.metaobj.shared.model.Id;
import org.sakaiproject.service.legacy.content.ContentHostingService;
import org.sakaiproject.service.legacy.entity.EntityManager;
import org.sakaiproject.service.legacy.entity.Reference;
import org.sakaiproject.service.legacy.security.SecurityService;
import org.sakaiproject.service.legacy.site.Site;
import org.sakaiproject.service.legacy.site.SitePage;
import org.sakaiproject.service.legacy.site.ToolConfiguration;
import org.sakaiproject.service.legacy.site.cover.SiteService;
import org.springframework.orm.hibernate.support.HibernateDaoSupport;
import org.theospi.portfolio.security.AllowMapSecurityAdvisor;
import org.theospi.portfolio.security.AuthorizationFacade;
import org.theospi.portfolio.shared.mgt.ContentEntityUtil;
import org.theospi.portfolio.wizard.impl.WizardEntityProducer;
import org.theospi.portfolio.wizard.mgt.WizardManager;
import org.theospi.portfolio.wizard.model.Wizard;
import org.theospi.portfolio.wizard.model.WizardStyleItem;
import org.theospi.portfolio.wizard.model.WizardCategory;
import org.theospi.portfolio.wizard.model.WizardPageSequence;
import org.theospi.portfolio.matrix.model.WizardPageDefinition;

public class WizardManagerImpl extends HibernateDaoSupport implements WizardManager {

   private AuthorizationFacade authorizationFacade;
   private SecurityService securityService;
   private EntityManager entityManager;
   private IdManager idManager;
   private StructuredArtifactDefinitionManager structuredArtifactDefinitionManager;
   private AgentManager agentManager;

   public Wizard createNew(String owner, String siteId, String toolId, 
         Id securityQualifier, String securityViewFunction, String securityEditFunction) {
      Agent agent = getAgentManager().getAgent(owner);
      Wizard wizard = new Wizard(getIdManager().createId(), agent, siteId, toolId,
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
      
      for (Iterator i=wizard.getWizardStyleItems().iterator();i.hasNext();) {
         WizardStyleItem item = (WizardStyleItem)i.next();
         refs.add(item.getBaseReference().getBase().getReference());
      }

      WizardCategory rootCategory = (WizardCategory)wizard.getRootCategory();
      loadCategory(rootCategory, refs);

      getSecurityService().pushAdvisor(new AllowMapSecurityAdvisor(ContentHostingService.EVENT_RESOURCE_READ,
         refs));

      return wizard;
   }

   protected void loadCategory(WizardCategory category, List refs) {

      for (Iterator i=category.getChildPages().iterator();i.hasNext();) {
         WizardPageSequence page = (WizardPageSequence) i.next();
         WizardPageDefinition pageDef = page.getWizardPageDefinition(); // make sure this loads
         pageDef.getTitle();
      }

      if (category.getChildCategories() != null) {
         for (Iterator i=category.getChildCategories().iterator();i.hasNext();) {
            loadCategory((WizardCategory) i.next(), refs);
         }
      }
   }

   public Wizard saveWizard(Wizard wizard) {
      Date now = new Date(System.currentTimeMillis());
      wizard.setModified(now);

      if (wizard.getExposeAsTool() != null && 
            wizard.getExposeAsTool().booleanValue() && 
            wizard.getExposedPageId() == null) {
         addTool(wizard);
      }
      else if (wizard.getExposeAsTool() != null && 
            !wizard.getExposeAsTool().booleanValue() && 
            wizard.getExposedPageId() != null) {
         removeTool(wizard);
      }
      
      if (wizard.isNewObject()) {
         wizard.setCreated(now);
         wizard.getRootCategory().setCreated(now);
         wizard.getRootCategory().setModified(now);
         wizard.getRootCategory().setWizard(null);
         getHibernateTemplate().save(wizard, wizard.getId());
         wizard.getRootCategory().setWizard(wizard);
         wizard.setNewObject(false);
      }
      else {
         getHibernateTemplate().saveOrUpdate(wizard);
      }

      return wizard;
   }
   
   private void removeTool(Wizard wizard) {
      String siteId = wizard.getSiteId();
      try {
         Site siteEdit = SiteService.getSite(siteId);
      
         SitePage page = siteEdit.getPage(wizard.getExposedPageId());
         siteEdit.removePage(page);
         SiteService.save(siteEdit);
         wizard.setExposedPageId(null);
      } catch (IdUnusedException e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
      } catch (PermissionException e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }
   }
   
   private void addTool(Wizard wizard) {
      //TODO add logging errors back
      String siteId = wizard.getSiteId();
      try {
         Site siteEdit = SiteService.getSite(siteId);
         
      
         SitePage page = siteEdit.addPage();
         
         page.setTitle(wizard.getName());
         page.setLayout(SitePage.LAYOUT_SINGLE_COL);
      
         ToolConfiguration tool = page.addTool();
         tool.setTool(ToolManager.getTool("osp.exposedwizard"));
         tool.setTitle(wizard.getName());
         tool.setLayoutHints("0,0");                           
      
         //LOG.info(this+": SiteService.commitEdit():" +siteId);
         
         SiteService.save(siteEdit);
         wizard.setExposedPageId(page.getId());
         
         
      } catch (IdUnusedException e) {
//       TODO Auto-generated catch block
         e.printStackTrace();
      } catch (PermissionException e) {
//       TODO Auto-generated catch block
         e.printStackTrace();
      }
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
   
   public Collection getAvailableForms(String siteId, String type) {
      return getStructuredArtifactDefinitionManager().findHomes(
            getIdManager().getId(siteId));      
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

   public StructuredArtifactDefinitionManager getStructuredArtifactDefinitionManager() {
      return structuredArtifactDefinitionManager;
      //TODO - Fix this soon!
      //return (StructuredArtifactDefinitionManager) 
      //   ComponentManager.getInstance().get("structuredArtifactDefinitionManager");
   }

   public void setStructuredArtifactDefinitionManager(StructuredArtifactDefinitionManager structuredArtifactDefinitionManager) {
      this.structuredArtifactDefinitionManager = structuredArtifactDefinitionManager;
   }

   public AgentManager getAgentManager() {
      return agentManager;
   }

   public void setAgentManager(AgentManager agentManager) {
      this.agentManager = agentManager;
   }
}

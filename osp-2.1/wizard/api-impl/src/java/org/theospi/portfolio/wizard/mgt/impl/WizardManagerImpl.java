package org.theospi.portfolio.wizard.mgt.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.sakaiproject.api.kernel.tool.cover.ToolManager;
import org.sakaiproject.api.kernel.tool.Placement;
import org.sakaiproject.api.kernel.function.cover.FunctionManager;
import org.sakaiproject.exception.IdUnusedException;
import org.sakaiproject.exception.PermissionException;
import org.sakaiproject.metaobj.shared.mgt.AgentManager;
import org.sakaiproject.metaobj.shared.mgt.IdManager;
import org.sakaiproject.metaobj.shared.mgt.StructuredArtifactDefinitionManager;
import org.sakaiproject.metaobj.shared.model.Agent;
import org.sakaiproject.metaobj.shared.model.Id;
import org.sakaiproject.metaobj.security.impl.sakai.AuthnManager;
import org.sakaiproject.metaobj.security.AuthorizationFacade;
import org.sakaiproject.service.legacy.content.ContentHostingService;
import org.sakaiproject.service.legacy.entity.EntityManager;
import org.sakaiproject.service.legacy.entity.Reference;
import org.sakaiproject.service.legacy.security.SecurityService;
import org.sakaiproject.service.legacy.site.Site;
import org.sakaiproject.service.legacy.site.SitePage;
import org.sakaiproject.service.legacy.site.ToolConfiguration;
import org.sakaiproject.service.legacy.site.cover.SiteService;
import org.sakaiproject.service.framework.portal.cover.PortalService;
import org.springframework.orm.hibernate.support.HibernateDaoSupport;
import org.theospi.portfolio.security.AllowMapSecurityAdvisor;
import org.theospi.portfolio.shared.mgt.ContentEntityUtil;
import org.theospi.portfolio.shared.model.OspException;
import org.theospi.portfolio.wizard.impl.WizardEntityProducer;
import org.theospi.portfolio.wizard.mgt.WizardManager;
import org.theospi.portfolio.wizard.model.*;
import org.theospi.portfolio.wizard.WizardFunctionConstants;
import org.theospi.portfolio.matrix.model.WizardPageDefinition;
import net.sf.hibernate.HibernateException;

public class WizardManagerImpl extends HibernateDaoSupport implements WizardManager {

   private AuthorizationFacade authorizationFacade;
   private SecurityService securityService;
   private EntityManager entityManager;
   private IdManager idManager;
   private StructuredArtifactDefinitionManager structuredArtifactDefinitionManager;
   private AgentManager agentManager;
   private AuthnManager authManager;

   protected void init() throws Exception {
      FunctionManager.registerFunction(WizardFunctionConstants.CREATE_WIZARD);
      FunctionManager.registerFunction(WizardFunctionConstants.EDIT_WIZARD);
      FunctionManager.registerFunction(WizardFunctionConstants.DELETE_WIZARD);
      FunctionManager.registerFunction(WizardFunctionConstants.PUBLISH_WIZARD);
      FunctionManager.registerFunction(WizardFunctionConstants.REVIEW_WIZARD);
      FunctionManager.registerFunction(WizardFunctionConstants.VIEW_WIZARD);
      FunctionManager.registerFunction(WizardFunctionConstants.EXPORT_WIZARD);
   }

   public Wizard createNew() {
      Placement placement = ToolManager.getCurrentPlacement();
      String currentSite = placement.getContext();
      String currentTool = PortalService.getCurrentToolId();
      Agent agent = getAuthManager().getAgent();
      Wizard wizard = new Wizard(getIdManager().createId(), agent, currentSite, currentTool);
      return wizard;
   }

   public Wizard getWizard(Id wizardId) {
      Wizard wizard = (Wizard)getHibernateTemplate().get(Wizard.class, wizardId);

      if (wizard == null) {
         return null;
      }

      getAuthorizationFacade().checkPermission(WizardFunctionConstants.VIEW_WIZARD,
         getIdManager().getId(wizard.getToolId()));

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
            "(w.owner=? or w.published=?) and w.siteId=? and w.type=? order by seq_num", params);
   }
   
   public List listAllWizards(String owner, String siteId) {
      Object[] params = new Object[]{owner, new Boolean(true), siteId};
      return getHibernateTemplate().find("from Wizard w where " +
            "(w.owner=? or w.published=?) and w.siteId=? order by seq_num", params);
   }
   
   public List findWizardsByOwner(String ownerId, String siteId) {
      Object[] params = new Object[]{ownerId, siteId};
      return getHibernateTemplate().find("from Wizard w where w.owner=? and w.siteId=? order by seq_num", params);
   }
   
   public List findPublishedWizards(String siteId) {
      Object[] params = new Object[]{new Boolean(true), siteId};
      return getHibernateTemplate().find("from Wizard w where w.published=? and w.siteId=? order by seq_num", params);
   }

   public Wizard getWizard(String id) {
      return getWizard(getIdManager().getId(id));
   }
   
   public Collection getAvailableForms(String siteId, String type) {
      return getStructuredArtifactDefinitionManager().findHomes(
            getIdManager().getId(siteId));      
   }

   public void deleteObjects(List deletedItems) {

      for (Iterator i=deletedItems.iterator();i.hasNext();) {
         try {
            getSession().delete(i.next());
         }
         catch (HibernateException e) {
            throw new OspException(e);
         }
      }

   }

   public CompletedWizard getCompletedWizard(Wizard wizard) {
      Agent agent = getAuthManager().getAgent();

      return getUsersWizard(wizard, agent);

   }

   public CompletedWizard saveWizard(CompletedWizard wizard) {
      getHibernateTemplate().saveOrUpdate(wizard);
      return wizard;
   }

   public CompletedWizard getUsersWizard(Wizard wizard, Agent agent) {
      List completedWizards = getHibernateTemplate().find(" CompletedWizard where wizard_id=? and owner_id=?",
            new Object[]{wizard.getId().getValue(), agent.getId().getValue()});

      if (completedWizards.size() == 0) {
         return new CompletedWizard(wizard, agent);
      }
      else {
         return (CompletedWizard)completedWizards.get(0);
      }
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

   public AuthnManager getAuthManager() {
      return authManager;
   }

   public void setAuthManager(AuthnManager authManager) {
      this.authManager = authManager;
   }
}

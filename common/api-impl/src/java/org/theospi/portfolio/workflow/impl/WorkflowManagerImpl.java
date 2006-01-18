package org.theospi.portfolio.workflow.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.sakaiproject.metaobj.shared.mgt.IdManager;
import org.sakaiproject.metaobj.shared.model.Id;
import org.sakaiproject.service.legacy.content.ContentHostingService;
import org.sakaiproject.service.legacy.entity.EntityManager;
import org.sakaiproject.service.legacy.entity.Reference;
import org.sakaiproject.service.legacy.security.SecurityService;
import org.springframework.orm.hibernate.support.HibernateDaoSupport;
import org.theospi.portfolio.security.AllowMapSecurityAdvisor;
import org.theospi.portfolio.security.AuthorizationFacade;
import org.theospi.portfolio.workflow.mgt.WorkflowManager;
import org.theospi.portfolio.workflow.model.Workflow;
import org.theospi.portfolio.workflow.model.WorkflowItem;

public class WorkflowManagerImpl extends HibernateDaoSupport implements WorkflowManager {

   private AuthorizationFacade authorizationFacade;
   private SecurityService securityService;
   private EntityManager entityManager;
   private IdManager idManager;
   
   public Workflow createNew(String description, String siteId, Id securityQualifier, String securityViewFunction, String securityEditFunction) {
      // TODO Auto-generated method stub
      return null;
   }

   public Workflow getWorkflow(Id workflowId) {
      Workflow workflow = (Workflow)getHibernateTemplate().get(Workflow.class, workflowId);

      if (workflow == null) {
         return null;
      }
/*
      if (workflow.getSecurityQualifier() != null) {
         getAuthorizationFacade().checkPermission(workflow.getSecurityViewFunction(),
               workflow.getSecurityQualifier());
      }
*/
      return workflow;
   }

   public Workflow saveWorkflow(Workflow workflow) {
      if (workflow.isNewObject()) {
         getHibernateTemplate().save(workflow, workflow.getId());
         workflow.setNewObject(false);
      }
      else {
         getHibernateTemplate().saveOrUpdate(workflow);
      }

      return workflow;
   }

   public void deleteWorkflow(Workflow workflow) {
      //for(Iterator iter = workflow.getItems().iterator(); iter.hasNext();) {
      //   WorkflowItem item = (WorkflowItem)iter.next();
      //   getHibernateTemplate().delete(item);
      //}
      workflow.getItems().clear();
      getHibernateTemplate().delete(workflow);
      //getHibernateTemplate().
      
   }

   public Reference decorateReference(Workflow workflow, String reference) {
      // TODO Auto-generated method stub
      return null;
   }

   public List listWorkflows(String siteId) {
      return getHibernateTemplate().find("from Workflow where site_id=? ",
            siteId);
   }

   public Workflow getWorkflow(String id) {
      return getWorkflow(getIdManager().getId(id));
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

}

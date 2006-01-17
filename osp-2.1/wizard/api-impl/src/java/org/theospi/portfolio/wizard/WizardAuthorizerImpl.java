/*
 * $Header: /opt/CVS/osp2.x/presentation/api-impl/src/java/org/theospi/portfolio/presentation/PresentationAuthorizerImpl.java,v 1.3 2005/09/15 17:37:27 jellis Exp $
 * $Revision: 3898 $
 * $Date: 2005-11-16 12:16:20 -0500 (Wed, 16 Nov 2005) $
 */
package org.theospi.portfolio.wizard;

import org.theospi.portfolio.security.app.ApplicationAuthorizer;
import org.theospi.portfolio.security.AuthorizationFacade;
import org.theospi.portfolio.wizard.mgt.WizardManager;
import org.theospi.portfolio.wizard.model.Wizard;
import org.theospi.portfolio.wizard.model.WizardSupportItem;
import org.sakaiproject.metaobj.shared.model.Agent;
import org.sakaiproject.metaobj.shared.model.Id;
import org.sakaiproject.metaobj.shared.mgt.IdManager;

import java.util.Iterator;
import java.util.List;

public class WizardAuthorizerImpl implements ApplicationAuthorizer{
   private WizardManager wizardManager;
   private IdManager idManager;
   private List functions;
                    
   /**
    * This method will ask the application specific functional authorizer to determine authorization.
    *
    * @param facade   this can be used to do explicit auths if necessary
    * @param agent
    * @param function
    * @param id
    * @return null if the authorizer has no opinion, true if authorized, false if explicitly not authorized.
    */
   public Boolean isAuthorized(AuthorizationFacade facade, Agent agent,
                               String function, Id id) {

      // return null if we don't know what is up...
      if (function.equals(WizardFunctionConstants.VIEW_WIZARD)) {
         return isWizardViewAuth(facade, agent, id, true);
      } else if (function.equals(WizardFunctionConstants.COMMENT_WIZARD)) {
         return isWizardCommentAuth(facade, agent, id);
      } else if (function.equals(WizardFunctionConstants.CREATE_WIZARD)) {
         return new Boolean(facade.isAuthorized(agent,function,id));
      } else if (function.equals(WizardFunctionConstants.EDIT_WIZARD)) {
         return isWizardAuth(facade, id, agent, WizardFunctionConstants.EDIT_WIZARD);
      } else if (function.equals(WizardFunctionConstants.PUBLISH_WIZARD)) {
         Wizard wizard = getWizardManager().getWizard(id);
         Id toolId = getIdManager().getId(wizard.getToolId());
         return new Boolean(facade.isAuthorized(agent,function,toolId));
      } else if (function.equals(WizardFunctionConstants.DELETE_WIZARD)) {
         return isWizardAuth(facade, id, agent, WizardFunctionConstants.DELETE_WIZARD);
      } else if (function.equals(WizardFunctionConstants.COPY_WIZARD)) {
         return isWizardAuth(facade, id, agent, WizardFunctionConstants.COPY_WIZARD);
      } else if (function.equals(WizardFunctionConstants.EXPORT_WIZARD)) {
         return isWizardAuth(facade, id, agent, WizardFunctionConstants.EXPORT_WIZARD);
      } else {
         return null;
      }
   }
   protected Boolean isWizardAuth(AuthorizationFacade facade, Id qualifier, Agent agent, String function){
      Wizard wizard = getWizardManager().getWizard(qualifier);

      if (wizard == null) {
         // must be tool id
         return new Boolean(facade.isAuthorized(function,qualifier));
      }
      
      //owner can do anything
      if (wizard.getOwner().equals(agent)){
         return new Boolean(true);
      }
      Id toolId = getIdManager().getId(wizard.getToolId());
      return new Boolean(facade.isAuthorized(function,toolId));
   }

   protected Boolean isWizardCommentAuth(AuthorizationFacade facade, Agent agent, Id id) {
      Wizard wizard = getWizardManager().getWizard(id);

      //Is there a comment form/wizard supplied?
      for (Iterator iter = wizard.getSupportItems().iterator(); iter.hasNext();) {
         WizardSupportItem wsi = (WizardSupportItem)iter.next();
         if (wsi.getGenericType().equals(WizardFunctionConstants.COMMENT_TYPE))
               return new Boolean(true);
      }
      //if (!wizard.getSupportItems()..isIncludeComments()){
      //   return new Boolean(false);
      //}

      if (wizard.getOwner().equals(agent)) {
         return new Boolean(true);
      } else {
         Id toolId = getIdManager().getId(wizard.getToolId());
         return new Boolean(facade.isAuthorized(agent, WizardFunctionConstants.COMMENT_WIZARD, toolId));
      }
   }

   protected Boolean isWizardViewAuth(AuthorizationFacade facade, Agent agent, Id id, boolean allowAnonymous) {
      Wizard wizard = getWizardManager().getWizard(id);

      return isWizardViewAuth(wizard, facade, agent, id, allowAnonymous);
   }

   protected Boolean isWizardViewAuth(Wizard wizard, AuthorizationFacade facade,
                                            Agent agent, Id id, boolean allowAnonymous) {
      if (wizard.getOwner().equals(agent)) {
         return new Boolean(true);
      } else {
         return new Boolean(facade.isAuthorized(agent, WizardFunctionConstants.VIEW_WIZARD, id));
      }
   }
/*
   protected Boolean isFileAuth(AuthorizationFacade facade, Agent agent, Id id) {
      // check if this id is attached to any pres

      if (id == null) return null;

      Collection presItems = getWizardManager().getPresentationItems(id);
      presItems.addAll(getWizardManager().getPresentationsBasedOnTemplateFileRef(id));

      if (presItems.size() == 0) {
         return null;
      }

      // does this user have access to any of the above pres
      for (Iterator i = presItems.iterator(); i.hasNext();) {
         Wizard wizard = (Wizard) i.next();

         Boolean returned = isWizardViewAuth(wizard, facade, agent, wizard.getId(), true);
         if (returned != null && returned.booleanValue()) {
            return returned;
         }
      }

      return null;
   }
*/
   public WizardManager getWizardManager() {
      return wizardManager;
   }

   public void setWizardManager(WizardManager wizardManager) {
      this.wizardManager = wizardManager;
   }

   public IdManager getIdManager() {
      return idManager;
   }

   public void setIdManager(IdManager idManager) {
      this.idManager = idManager;
   }

   public List getFunctions() {
      return functions;
   }

   public void setFunctions(List functions) {
      this.functions = functions;
   }
}

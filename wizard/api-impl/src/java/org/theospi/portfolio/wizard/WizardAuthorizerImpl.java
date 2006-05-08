/**********************************************************************************
* $URL$
* $Id$
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
package org.theospi.portfolio.wizard;

import org.sakaiproject.metaobj.shared.mgt.IdManager;
import org.sakaiproject.metaobj.shared.model.Agent;
import org.sakaiproject.metaobj.shared.model.Id;
import org.theospi.portfolio.security.AuthorizationFacade;
import org.theospi.portfolio.security.app.ApplicationAuthorizer;
import org.theospi.portfolio.wizard.mgt.WizardManager;
import org.theospi.portfolio.wizard.model.CompletedWizardPage;
import org.theospi.portfolio.wizard.model.Wizard;
import org.theospi.portfolio.wizard.model.WizardPageSequence;

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
      } else if (WizardFunctionConstants.REVIEW_WIZARD.equals(function)) {
         return isWizardAuthForReview(facade, agent, id);
      } else if (WizardFunctionConstants.EVALUATE_WIZARD.equals(function)) {
         return isWizardAuthForEval(facade, agent, id);
      } 
      else if (function.equals(WizardFunctionConstants.EDIT_WIZARDPAGE_GUIDANCE)) {
         //ScaffoldingCell sCell = getMatrixManager().getScaffoldingCellByWizardPageDef(id);
         WizardPageSequence wps = wizardManager.getWizardPageSeqByDef(id);
         Agent owner = wps.getCategory().getWizard().getOwner();
         
         return new Boolean(agent.equals(owner));
      } else if (function.equals(WizardFunctionConstants.VIEW_WIZARDPAGE_GUIDANCE)) {
         //If I can eval, review, or own it
         List pages = wizardManager.getCompletedWizardPagesByPageDef(id);
         Boolean returned = null;

         for (Iterator iter=pages.iterator(); iter.hasNext();) {
            CompletedWizardPage cwp = (CompletedWizardPage)iter.next();
            returned = Boolean.valueOf(facade.isAuthorized(agent, WizardFunctionConstants.EVALUATE_WIZARD, cwp.getId()));
            if (returned == null || !returned.booleanValue()) {
               returned = Boolean.valueOf(facade.isAuthorized(agent, WizardFunctionConstants.REVIEW_WIZARD, cwp.getId()));
            }
            if (returned == null || !returned.booleanValue()) {
               returned = Boolean.valueOf(cwp.getCategory().getWizard().getOwner().equals(agent));
            }
            if (returned == null || !returned.booleanValue()) {
               returned = Boolean.valueOf(facade.isAuthorized(agent, WizardFunctionConstants.VIEW_WIZARD,id));
            }
            if (returned.booleanValue())
               return returned;
         }
        
         WizardPageSequence wps = getWizardManager().getWizardPageSeqByDef(id);
         
         returned = Boolean.valueOf(wps.getCategory().getWizard().getOwner().equals(agent));
         if (returned.booleanValue())
            return returned;
         
         return null;
      } 
      
      else {
         return null;
      }
   }
   
   /**
    * This method checks for permission "function" of wizard "qualifier" with the given Agent.
    * @param facade AuthorizationFacade
    * @param qualifier Id
    * @param agent Agent
    * @param function String
    * @return Boolean
    */
   protected Boolean isWizardAuth(AuthorizationFacade facade, Id qualifier, Agent agent, String function) {
      Wizard wizard = getWizardManager().getWizard(qualifier);

      if (wizard == null) {
         // must be tool id
         return new Boolean(facade.isAuthorized(function,qualifier));
      }
      
      //owner can do anything
      /*
      if (wizard.getOwner().equals(agent)){
         return new Boolean(true);
      }
      */
      Id toolId = getIdManager().getId(wizard.getToolId());
      return new Boolean(facade.isAuthorized(function,toolId));
   }

   /**
    * THis handles the authority for the view permission on a wizard
    * @param facade
    * @param agent
    * @param id
    * @param allowAnonymous
    * @return
    */
   protected Boolean isWizardViewAuth(AuthorizationFacade facade, Agent agent, Id id, boolean allowAnonymous) {
      Wizard wizard = getWizardManager().getWizard(id);

      if (wizard == null) {
         return null;
      }

      return isWizardViewAuth(wizard, facade, agent, id, allowAnonymous);
   }

   /**
    * Checks an agents ability to view the given wizard.  It also check the wizard's
    * tool for permission to access the wizard as view is a tool wide permission.  
    * Anonymous is not recognized in this function yet
    * @param wizard        Wizard
    * @param facade        AuthorizationFacade
    * @param agent         Agent
    * @param id            Id
    * @param allowAnonymous boolean
    * @return Boolean
    */
   protected Boolean isWizardViewAuth(Wizard wizard, AuthorizationFacade facade,
                                            Agent agent, Id id, boolean allowAnonymous) {
      boolean isAuthorized = false;
      if (wizard != null && wizard.getOwner().equals(agent)) {
         isAuthorized = true;
      } else {
         isAuthorized = facade.isAuthorized(agent, WizardFunctionConstants.VIEW_WIZARD, id);
         
         if(!isAuthorized) {
            Id toolId = getIdManager().getId(wizard.getToolId());
            isAuthorized = facade.isAuthorized(WizardFunctionConstants.VIEW_WIZARD, toolId);
            
         }
      }
      return new Boolean(isAuthorized);
   }

   protected Boolean isWizardAuthForReview(AuthorizationFacade facade, Agent agent, Id wizardId) {
      Wizard wizard = getWizardManager().getWizard(wizardId);
      Id toolId = wizardId;
      if (wizard != null)
         toolId = getIdManager().getId(wizard.getToolId());
      
      return new Boolean(facade.isAuthorized(agent, WizardFunctionConstants.REVIEW_WIZARD, toolId));
   }
   
   protected Boolean isWizardAuthForEval(AuthorizationFacade facade, Agent agent, Id id) {
      //Wizard wizard = getWizardManager().getWizard(wizardId);
      //Id toolId = getIdManager().getId(wizard.getToolId());
      return new Boolean(facade.isAuthorized(agent, WizardFunctionConstants.EVALUATE_WIZARD, id));
   }

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

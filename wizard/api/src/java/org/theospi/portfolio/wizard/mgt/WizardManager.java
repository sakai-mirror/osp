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
package org.theospi.portfolio.wizard.mgt;

import java.util.Collection;
import java.util.List;

import org.sakaiproject.entity.api.Reference;
import org.sakaiproject.exception.ImportException;
import org.sakaiproject.exception.UnsupportedFileTypeException;
import org.sakaiproject.metaobj.shared.model.Agent;
import org.sakaiproject.metaobj.shared.model.Id;
import org.theospi.portfolio.shared.mgt.WorkflowEnabledManager;
import org.theospi.portfolio.wizard.model.CompletedWizard;
import org.theospi.portfolio.wizard.model.Wizard;
import org.theospi.portfolio.wizard.model.WizardPageSequence;

public interface WizardManager extends WorkflowEnabledManager {

   public static final int    WIZARD_NO_CHECK = 0;
   public static final int    WIZARD_OPERATE_CHECK = 10;
   public static final int    WIZARD_VIEW_CHECK = 20;
   public static final int    WIZARD_EDIT_CHECK = 30;
   public static final int    WIZARD_EXPORT_CHECK = 40;
   public static final int    WIZARD_DELETE_CHECK = 50;
   
	   public static final String WIZARD_PARAM_ID = "wizardId";
      public static final String EXPOSED_WIZARD_KEY = "osp.exposedwizard.wizard.id";
	
   /**
    * creates a new Wizard in the current site owned by the current user
    * @return Wizard
    */
   public Wizard createNew();

   /**
    * Gets a wizard given its id.  This performs a check on the operate permission
    * @param Id wizardId
    * @return Wizard
    */
   public Wizard getWizard(Id wizardId);
   
   /**
    * gets a wizard given its id.  it may perform a check on the view permission
    * if the checkAuthz is true
    * @param Id wizardId
    * @param boolean checkAuthz
    * @return
    */
   public Wizard getWizard(Id wizardId, int checkAuthz);

   /**
    * Gets a wizard given its id.  This performs a check on the view permission
    * @param String wizardId
    * @return Wizard
    */
   public Wizard getWizard(String id);
   
   /**
    * gets a wizard given its id.  it performs a check on the permission
    * specified in checkAuthz:   WIZARD_NO_CHECK, WIZARD_OPERATE_CHECK, WIZARD_VIEW_CHECK
    * WIZARD_EDIT_CHECK, WIZARD_EXPORT_CHECK, WIZARD_DELETE_CHECK
    * @param String wizardId
    * @param boolean checkAuthz
    * @return
    */
   public Wizard getWizard(String id, int checkAuthz);

   /**
    * Saves a Wizard to storage.  It returns an updated wizard
    * @param wizard
    * @return Wizard
    */
   public Wizard saveWizard(Wizard wizard);
   
   /**
    * Removes a wizard from storage
    * @param wizard
    */
   public void deleteWizard(Wizard wizard);
   
   
   public Reference decorateReference(Wizard wizard, String reference);
   
   public String getWizardEntityProducer();

   
   /**
    * Pulls all wizards, deeping loading all parts of each Wizard
    * @return List of Wizard
    */
   public List getWizardsForWarehousing();
   
   public List listAllWizards(String siteId);
   public List listAllWizardsByOwner(String owner, String siteId);
   public List listWizardsByType(String owner, String siteId, String type);
   public List findWizardsByOwner(String ownerId, String siteId);
   public List findPublishedWizards(String siteId);
   
   /**
    * 
    * @param sites A list of site Ids (Strings)
    * @return
    */
   public List findPublishedWizards(List sites);

   /**
    * changes the settings on the wizard to make it available to the users of the site
    * @param wizard
    */
   public void publishWizard(Wizard wizard);
   
   public Collection getAvailableForms(String siteId, String type);

   public void deleteObjects(List deletedItems);

   public List getCompletedWizardsByWizardId(String wizardId);
   public CompletedWizard getCompletedWizard(Id completedWizardId);
   public CompletedWizard getCompletedWizard(Wizard wizard);
   public CompletedWizard getCompletedWizard(Wizard wizard, String userId);
   public CompletedWizard getCompletedWizard(Wizard wizard, String userId, boolean create);

   /**
    * Saves a completed wizard into storage
    * @param wizard CompletedWizard
    * @return CompletedWizard
    */
   public CompletedWizard saveWizard(CompletedWizard wizard);

   public boolean importResource(Id worksite, String reference) throws UnsupportedFileTypeException, ImportException;
   
   public WizardPageSequence getWizardPageSeqByDef(Id id);
   public List getCompletedWizardPagesByPageDef(Id id);
   public CompletedWizard getCompletedWizardByPage(Id pageId);
   
   /**
    * Checks if the current user is authorized to review all the types of Reviews.
    * If the user is the owner or the user is authorized then the Reviews are read in
    * and pushed into the security advisor.
    * 
    * @param id Id of the wizard to check
    */
   public void checkWizardAccess(Id id);

   /**
    * Gets the total number of pages for the given wizard
    * @param wizard Wizard to tally the number of pages
    * @return int
    */
   public int getTotalPageCount(Wizard wizard);

   /**
    * Given a user's completed wizard this takes a look at the number of submitted 
    * pages (not in the READY state)
    * @param wizard CompletedWizard to tally the number of submitted pages
    * @return int
    */
   public int getSubmittedPageCount(CompletedWizard wizard);
   
   /**
    * This is the light weight method of getting the site id of a wizard given its id.
    * @param Id wizardId
    * @return String of the site id
    */
   public String getWizardIdSiteId(final Id wizardId);
   

   /**
    * This is the light weight method of getting the owner of a wizard given its id.
    * @param Id wizardId
    * @return Agent of the owner id
    */
   public Agent getWizardIdOwner(final Id wizardId);
}

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
package org.theospi.portfolio.wizard.mgt;

import java.util.Collection;
import java.util.List;

import org.sakaiproject.metaobj.shared.model.Id;
import org.sakaiproject.service.legacy.entity.Reference;
import org.theospi.portfolio.shared.mgt.WorkflowEnabledManager;
import org.theospi.portfolio.wizard.model.Wizard;
import org.theospi.portfolio.wizard.model.CompletedWizard;
import org.theospi.portfolio.wizard.model.WizardPageSequence;

public interface WizardManager extends WorkflowEnabledManager {

	   public static final String WIZARD_PARAM_ID = "wizardId";
      public static final String EXPOSED_WIZARD_KEY = "osp.exposedwizard.wizard.id";
	
   public Wizard createNew();

   public Wizard getWizard(Id wizardId);

   public Wizard saveWizard(Wizard wizard);
   
   public void deleteWizard(Wizard wizard);
   
   public Reference decorateReference(Wizard wizard, String reference);
   
   public String getWizardEntityProducer();

   
   /**
    * Pulls all wizards, deeping loading all parts of each Wizard
    * @return List of Wizard
    */
   public List getWizardsForWarehousing();
   
   public List listAllWizards(String owner, String siteId);
   public List listWizardsByType(String owner, String siteId, String type);
   public List findWizardsByOwner(String ownerId, String siteId);
   public List findPublishedWizards(String siteId);
   
   public Wizard getWizard(String id);
   
   public void publishWizard(Wizard wizard);
   
   public Collection getAvailableForms(String siteId, String type);

   public void deleteObjects(List deletedItems);

   public List getCompletedWizardsByWizardId(String wizardId);
   public CompletedWizard getCompletedWizard(Id completedWizardId);
   public CompletedWizard getCompletedWizard(Wizard wizard);
   public CompletedWizard getCompletedWizard(Wizard wizard, String userId);

   public CompletedWizard saveWizard(CompletedWizard wizard);

   public boolean importResource(Id worksite, String reference);
   
   public WizardPageSequence getWizardPageSeqByDef(Id id);
   public List getCompletedWizardPagesByPageDef(Id id);
   public CompletedWizard getCompletedWizardByPage(Id pageId);
   
   public void checkWizardAccess(Id id);
}

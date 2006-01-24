package org.theospi.portfolio.wizard.mgt;

import java.util.Collection;
import java.util.List;

import org.sakaiproject.metaobj.shared.model.Id;
import org.sakaiproject.metaobj.shared.model.Agent;
import org.sakaiproject.service.legacy.entity.Reference;
import org.theospi.portfolio.wizard.model.Wizard;
import org.theospi.portfolio.wizard.model.CompletedWizard;

public interface WizardManager {

   public Wizard createNew();

   public Wizard getWizard(Id wizardId);

   public Wizard saveWizard(Wizard wizard);
   
   public void deleteWizard(Wizard wizard);
   
   public Reference decorateReference(Wizard wizard, String reference);
   
   public List listAllWizards(String owner, String siteId);
   public List listWizardsByType(String owner, String siteId, String type);
   public List findWizardsByOwner(String ownerId, String siteId);
   public List findPublishedWizards(String siteId);
   
   public Wizard getWizard(String id);
   
   public Collection getAvailableForms(String siteId, String type);

   public void deleteObjects(List deletedItems);

   public CompletedWizard getCompletedWizard(Wizard wizard);

   public CompletedWizard saveWizard(CompletedWizard wizard);

}

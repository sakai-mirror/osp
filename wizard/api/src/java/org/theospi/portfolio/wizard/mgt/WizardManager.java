package org.theospi.portfolio.wizard.mgt;

import java.util.Collection;
import java.util.List;

import org.sakaiproject.metaobj.shared.model.Id;
import org.sakaiproject.service.legacy.entity.Reference;
import org.theospi.portfolio.wizard.model.Wizard;

public interface WizardManager {

   public final static String CURRENT_WIZARD = "org.theospi.portfolio.wizard.currentWizard";
   public final static String CURRENT_WIZARD_ID = "org.theospi.portfolio.wizard.currentWizardId";

   public Wizard createNew(String owner, String siteId, Id securityQualifier,
         String securityViewFunction, String securityEditFunction);

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
}

package org.theospi.portfolio.wizard.impl;

import org.theospi.portfolio.shared.mgt.OspHttpAccess;
import org.theospi.portfolio.shared.mgt.ReferenceParser;
import org.theospi.portfolio.wizard.mgt.WizardManager;
import org.sakaiproject.service.legacy.entity.Reference;
import org.sakaiproject.exception.PermissionException;
import org.sakaiproject.exception.IdUnusedException;
import org.sakaiproject.exception.ServerOverloadException;
import org.sakaiproject.exception.CopyrightException;
import org.sakaiproject.metaobj.shared.mgt.IdManager;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Nov 14, 2005
 * Time: 6:05:46 PM
 * To change this template use File | Settings | File Templates.
 */
public class WizardHttpAccess extends OspHttpAccess {

   private IdManager idManager;
   private WizardManager wizardManager;
   
   protected void checkSource(Reference ref, ReferenceParser parser)
      throws PermissionException, IdUnusedException, ServerOverloadException, CopyrightException {
      // should setup access rights, etc.
      getWizardManager().getWizard(getIdManager().getId(parser.getId()));
   }

   public IdManager getIdManager() {
      return idManager;
   }

   public void setIdManager(IdManager idManager) {
      this.idManager = idManager;
   }

   public WizardManager getWizardManager() {
      return wizardManager;
   }

   public void setWizardManager(WizardManager wizardManager) {
      this.wizardManager = wizardManager;
   }
}

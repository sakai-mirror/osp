package org.theospi.portfolio.presentation.model.impl;

import org.theospi.portfolio.shared.mgt.OspHttpAccess;
import org.theospi.portfolio.shared.mgt.OspEntityProducerBase;
import org.theospi.portfolio.shared.mgt.ReferenceParser;
import org.theospi.portfolio.presentation.PresentationManager;
import org.theospi.portfolio.presentation.model.Presentation;
import org.theospi.portfolio.security.AuthorizationFailedException;
import org.sakaiproject.service.legacy.entity.Reference;
import org.sakaiproject.service.legacy.entity.EntityManager;
import org.sakaiproject.service.legacy.content.ContentHostingService;
import org.sakaiproject.metaobj.shared.mgt.IdManager;
import org.sakaiproject.exception.PermissionException;
import org.sakaiproject.exception.IdUnusedException;
import org.sakaiproject.exception.ServerOverloadException;
import org.sakaiproject.exception.CopyrightException;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Nov 8, 2005
 * Time: 2:39:25 PM
 * To change this template use File | Settings | File Templates.
 */
public class PresentationHttpAccess extends OspHttpAccess {

   private PresentationManager presentationManager;
   private IdManager idManager;

   protected void checkSource(Reference ref, ReferenceParser parser)
      throws PermissionException, IdUnusedException, ServerOverloadException, CopyrightException{

      try {
         Presentation pres = presentationManager.getPresentation(
            getIdManager().getId(parser.getId()));
         if (pres == null) {
            throw new IdUnusedException(parser.getId());
         }
      }
      catch (AuthorizationFailedException exp) {
         throw new PermissionException(ContentHostingService.EVENT_RESOURCE_READ, ref.getReference());
      }
   }

   public PresentationManager getPresentationManager() {
      return presentationManager;
   }

   public void setPresentationManager(PresentationManager presentationManager) {
      this.presentationManager = presentationManager;
   }

   public IdManager getIdManager() {
      return idManager;
   }

   public void setIdManager(IdManager idManager) {
      this.idManager = idManager;
   }
}

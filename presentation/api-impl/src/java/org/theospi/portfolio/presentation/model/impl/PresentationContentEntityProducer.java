package org.theospi.portfolio.presentation.model.impl;

import org.theospi.portfolio.shared.mgt.OspEntityProducerBase;
import org.theospi.portfolio.presentation.PresentationManager;
import org.theospi.portfolio.presentation.model.Presentation;
import org.theospi.portfolio.security.AuthorizationFailedException;
import org.sakaiproject.service.legacy.entity.EntityManager;
import org.sakaiproject.service.legacy.entity.Reference;
import org.sakaiproject.service.legacy.content.ContentResource;
import org.sakaiproject.service.legacy.content.ContentHostingService;
import org.sakaiproject.metaobj.shared.mgt.IdManager;
import org.sakaiproject.exception.PermissionException;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Nov 7, 2005
 * Time: 6:48:14 PM
 * To change this template use File | Settings | File Templates.
 */
public class PresentationContentEntityProducer extends OspEntityProducerBase {
   protected static final String PRODUCER_NAME = "ospPresentation";

   public String getLabel() {
      return PRODUCER_NAME;
   }

   public void init() {
      getEntityManager().registerEntityProducer(this);
   }
}

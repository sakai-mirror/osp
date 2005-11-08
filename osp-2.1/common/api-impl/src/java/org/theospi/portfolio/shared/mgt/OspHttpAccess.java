package org.theospi.portfolio.shared.mgt;

import org.sakaiproject.service.legacy.entity.HttpAccess;
import org.sakaiproject.service.legacy.entity.Reference;
import org.sakaiproject.service.legacy.entity.EntityProducer;
import org.sakaiproject.service.legacy.resource.cover.EntityManager;
import org.sakaiproject.exception.PermissionException;
import org.sakaiproject.exception.IdUnusedException;
import org.sakaiproject.exception.ServerOverloadException;
import org.sakaiproject.exception.CopyrightException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Collection;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Nov 7, 2005
 * Time: 3:14:16 PM
 * To change this template use File | Settings | File Templates.
 */
public class OspHttpAccess implements HttpAccess {

   public void handleAccess(HttpServletRequest req, HttpServletResponse res,
                            Reference ref, Collection copyrightAcceptedRefs)
         throws PermissionException, IdUnusedException, ServerOverloadException, CopyrightException {

      ContentEntityWrapper wrapper = (ContentEntityWrapper)ref.getEntity();
      Reference realRef = EntityManager.newReference(wrapper.getBase().getReference());
      EntityProducer producer = realRef.getEntityProducer();
      producer.getHttpAccess().handleAccess(req, res, realRef, copyrightAcceptedRefs);
   }
}

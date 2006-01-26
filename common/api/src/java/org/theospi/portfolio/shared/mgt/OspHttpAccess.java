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
package org.theospi.portfolio.shared.mgt;

import org.sakaiproject.service.legacy.entity.HttpAccess;
import org.sakaiproject.service.legacy.entity.Reference;
import org.sakaiproject.service.legacy.entity.EntityProducer;
import org.sakaiproject.service.legacy.resource.cover.EntityManager;
import org.sakaiproject.exception.PermissionException;
import org.sakaiproject.exception.IdUnusedException;
import org.sakaiproject.exception.ServerOverloadException;
import org.sakaiproject.exception.CopyrightException;
import org.theospi.portfolio.security.AuthorizationFacade;

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
public abstract class OspHttpAccess implements HttpAccess {

   private AuthorizationFacade authzManager;

   public void handleAccess(HttpServletRequest req, HttpServletResponse res,
                            Reference ref, Collection copyrightAcceptedRefs)
         throws PermissionException, IdUnusedException, ServerOverloadException, CopyrightException {
      ReferenceParser parser =
            new ReferenceParser(ref.getReference(), ref.getEntityProducer());
      authzManager.pushAuthzGroups(parser.getSiteId());
      checkSource(ref, parser);
      ContentEntityWrapper wrapper = (ContentEntityWrapper)ref.getEntity();
      Reference realRef = EntityManager.newReference(wrapper.getBase().getReference());
      EntityProducer producer = realRef.getEntityProducer();
      producer.getHttpAccess().handleAccess(req, res, realRef, copyrightAcceptedRefs);
   }

   protected abstract void checkSource(Reference ref, ReferenceParser parser)
      throws PermissionException, IdUnusedException, ServerOverloadException, CopyrightException;

   public AuthorizationFacade getAuthzManager() {
      return authzManager;
   }

   public void setAuthzManager(AuthorizationFacade authzManager) {
      this.authzManager = authzManager;
   }
}

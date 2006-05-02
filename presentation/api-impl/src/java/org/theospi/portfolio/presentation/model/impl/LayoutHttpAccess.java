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
package org.theospi.portfolio.presentation.model.impl;

import org.sakaiproject.metaobj.shared.mgt.ReferenceParser;
import org.theospi.portfolio.presentation.PresentationManager;
import org.theospi.portfolio.presentation.model.PresentationLayout;
import org.theospi.portfolio.security.AuthorizationFailedException;
import org.theospi.portfolio.security.mgt.OspHttpAccessBase;
import org.sakaiproject.entity.api.EntityAccessOverloadException;
import org.sakaiproject.entity.api.EntityCopyrightException;
import org.sakaiproject.entity.api.EntityNotDefinedException;
import org.sakaiproject.entity.api.EntityPermissionException;
import org.sakaiproject.entity.api.Reference;
import org.sakaiproject.content.api.ContentHostingService;
import org.sakaiproject.metaobj.shared.mgt.IdManager;
import org.sakaiproject.tool.cover.SessionManager;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Jan 8, 2006
 * Time: 4:54:50 PM
 * To change this template use File | Settings | File Templates.
 */
public class LayoutHttpAccess extends OspHttpAccessBase {

   private PresentationManager presentationManager;
   private IdManager idManager;

   protected void checkSource(Reference ref, ReferenceParser parser)
         throws EntityPermissionException, EntityNotDefinedException, EntityAccessOverloadException, EntityCopyrightException {
      try {
         PresentationLayout pres = presentationManager.getPresentationLayout(
            getIdManager().getId(parser.getId()));
         if (pres == null) {
            throw new EntityNotDefinedException(parser.getId());
         }
      }
      catch (AuthorizationFailedException exp) {
         throw new EntityPermissionException(SessionManager.getCurrentSessionUserId(), 
               ContentHostingService.EVENT_RESOURCE_READ, ref.getReference());
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

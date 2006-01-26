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
package org.theospi.portfolio.reports.model.impl;

import org.theospi.portfolio.shared.mgt.OspHttpAccess;
import org.theospi.portfolio.shared.mgt.ReferenceParser;
import org.theospi.portfolio.security.impl.AllowAllSecurityAdvisor;
import org.theospi.portfolio.security.impl.AllowAllSecurityAdvisor;
import org.theospi.portfolio.reports.model.ReportsManager;
import org.sakaiproject.metaobj.shared.mgt.IdManager;
import org.sakaiproject.service.legacy.entity.Reference;
import org.sakaiproject.service.legacy.security.SecurityService;
import org.sakaiproject.exception.PermissionException;
import org.sakaiproject.exception.IdUnusedException;
import org.sakaiproject.exception.ServerOverloadException;
import org.sakaiproject.exception.CopyrightException;

/**
 * This class can check for access permissions on a particular reference
 * 
 * User: John Ellis
 * Date: Dec 24, 2005
 * Time: 12:03:18 PM
 * To change this template use File | Settings | File Templates.
 */
public class ReportsHttpAccess extends OspHttpAccess {

   private ReportsManager reportsManager;

   /**
    * Given a file reference and the reference parser (fill in what these are)
    * This method asks the ReportsManager Singleton whether or not the request
    * has access to the particular file.  This method throws an exception
    * when something isn't correct about the user, the request, or the file
    * @throws PermissionException
    * @throws IdUnusedException
    * @throws ServerOverloadException
    * @throws CopyrightException
    */
   protected void checkSource(Reference ref, ReferenceParser parser)
      throws PermissionException, IdUnusedException, ServerOverloadException, CopyrightException {
      getReportsManager().checkReportAccess(parser.getId(), parser.getRef());
   }

   /** gets the class */
   public ReportsManager getReportsManager() {
      return reportsManager;
   }

   /** sets the class, from the components.xml */
   public void setReportsManager(ReportsManager reportsManager) {
      this.reportsManager = reportsManager;
   }

}

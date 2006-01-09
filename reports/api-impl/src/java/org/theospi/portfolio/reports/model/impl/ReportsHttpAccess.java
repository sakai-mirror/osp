/**********************************************************************************
* $URL$
* $Id$
***********************************************************************************
*
* Copyright (c) 2003, 2004 The Regents of the University of Michigan, Trustees of Indiana University,
*                  Board of Trustees of the Leland Stanford, Jr., University, and The MIT Corporation
*
* Licensed under the Educational Community License Version 1.0 (the "License");
* By obtaining, using and/or copying this Original Work, you agree that you have read,
* understand, and will comply with the terms and conditions of the Educational Community License.
* You may obtain a copy of the License at:
*
*      http://cvs.sakaiproject.org/licenses/license_1_0.html
*
* THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
* INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE
* AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
* DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
* FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
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

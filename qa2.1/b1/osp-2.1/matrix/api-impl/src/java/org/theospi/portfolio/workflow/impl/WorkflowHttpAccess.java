/**********************************************************************************
* $URL: https://source.sakaiproject.org/svn/trunk/sakai/legacy/component/src/java/org/sakaiproject/component/framework/email/TestEmailService.java $
* $Id: TestEmailService.java 3831 2005-11-14 20:17:24Z ggolden@umich.edu $
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
package org.theospi.portfolio.workflow.impl;

import org.sakaiproject.metaobj.shared.mgt.HttpAccessBase;
import org.sakaiproject.metaobj.shared.mgt.ReferenceParser;
import org.theospi.portfolio.workflow.mgt.WorkflowManager;
import org.theospi.portfolio.security.mgt.OspHttpAccessBase;
import org.sakaiproject.service.legacy.entity.Reference;
import org.sakaiproject.exception.PermissionException;
import org.sakaiproject.exception.IdUnusedException;
import org.sakaiproject.exception.ServerOverloadException;
import org.sakaiproject.exception.CopyrightException;
import org.sakaiproject.metaobj.shared.mgt.IdManager;
import org.sakaiproject.metaobj.shared.mgt.HttpAccessBase;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Nov 14, 2005
 * Time: 6:05:46 PM
 * To change this template use File | Settings | File Templates.
 */
public class WorkflowHttpAccess extends OspHttpAccessBase {

   private IdManager idManager;
   private WorkflowManager workflowManager;
   
   protected void checkSource(Reference ref, ReferenceParser parser)
      throws PermissionException, IdUnusedException, ServerOverloadException, CopyrightException {
      // should setup access rights, etc.
      getWorkflowManager().getWorkflow(getIdManager().getId(parser.getId()));
   }

   public IdManager getIdManager() {
      return idManager;
   }

   public void setIdManager(IdManager idManager) {
      this.idManager = idManager;
   }

   public WorkflowManager getWorkflowManager() {
      return workflowManager;
   }

   public void setWorkflowManager(WorkflowManager workflowManager) {
      this.workflowManager = workflowManager;
   }
}

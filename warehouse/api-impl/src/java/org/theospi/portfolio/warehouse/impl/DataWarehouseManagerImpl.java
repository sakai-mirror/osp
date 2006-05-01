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
package org.theospi.portfolio.warehouse.impl;

import org.theospi.portfolio.warehouse.intf.DataWarehouseManager;
import org.theospi.portfolio.warehouse.intf.WarehouseTask;
import org.theospi.portfolio.security.impl.AllowAllSecurityAdvisor;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.sakaiproject.authz.api.SecurityService;

import java.util.List;
import java.util.Iterator;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Nov 30, 2005
 * Time: 4:48:34 PM
 * To change this template use File | Settings | File Templates.
 */
public class DataWarehouseManagerImpl implements DataWarehouseManager {

   private List tasks;
   private SecurityService securityService;

   public void registerTask(WarehouseTask task) {
      getTasks().add(task);
   }

   public void execute(JobExecutionContext jobExecutionContext)
         throws JobExecutionException {

      getSecurityService().pushAdvisor(new AllowAllSecurityAdvisor());
      for (Iterator i=getTasks().iterator();i.hasNext();) {
         WarehouseTask task = (WarehouseTask)i.next();
         task.execute();
      }
      getSecurityService().popAdvisor();
   }

   public List getTasks() {
      return tasks;
   }

   public void setTasks(List tasks) {
      this.tasks = tasks;
   }

   public SecurityService getSecurityService() {
      return securityService;
   }

   public void setSecurityService(SecurityService securityService) {
      this.securityService = securityService;
   }
}

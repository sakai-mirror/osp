/**********************************************************************************
* $URL:https://source.sakaiproject.org/svn/osp/trunk/warehouse/api/src/java/org/theospi/portfolio/warehouse/intf/ChildWarehouseTask.java $
* $Id:ChildWarehouseTask.java 9134 2006-05-08 20:28:42Z chmaurer@iupui.edu $
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
package org.theospi.portfolio.warehouse.intf;

import java.sql.Connection;
import java.util.Collection;

import org.quartz.JobExecutionException;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Nov 30, 2005
 * Time: 4:53:55 PM
 * To change this template use File | Settings | File Templates.
 */
public interface ChildWarehouseTask {

   public void execute(Object parent, Collection items, Connection connection) throws JobExecutionException;

   public void prepare(Connection connection);

}

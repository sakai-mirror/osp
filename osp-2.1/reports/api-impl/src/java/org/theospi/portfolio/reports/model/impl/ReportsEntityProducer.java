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

import org.theospi.portfolio.shared.mgt.OspEntityProducerBase;
import org.sakaiproject.service.legacy.entity.Entity;

/**
 * This class is a singleton from components.xml.
 * 
 * It munges/decorates a resource url with info so as the artifact manager
 * will ask the reports code base about access to the particular artifact
 * 
 * @see ReportsHttpAccess
 * 
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Dec 24, 2005
 * Time: 12:01:24 PM
 * To change this template use File | Settings | File Templates.
 */
public class ReportsEntityProducer extends OspEntityProducerBase {

   public static final String REPORTS_PRODUCER = "ospReports";

   public String getLabel() {
      return REPORTS_PRODUCER;
   }

   public void init() {
      getEntityManager().registerEntityProducer(this, Entity.SEPARATOR + REPORTS_PRODUCER);
   }

}

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
package org.theospi.portfolio.guidance.impl;

import org.sakaiproject.metaobj.shared.mgt.EntityProducerBase;
import org.sakaiproject.service.legacy.entity.Entity;
import org.sakaiproject.metaobj.shared.mgt.EntityProducerBase;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Nov 14, 2005
 * Time: 6:03:02 PM
 * To change this template use File | Settings | File Templates.
 */
public class GuidanceEntityProducer extends EntityProducerBase {

   public static final String GUIDANCE_PRODUCER = "ospGuidance";

   public String getLabel() {
      return GUIDANCE_PRODUCER;
   }

   public void init() {
      getEntityManager().registerEntityProducer(this, Entity.SEPARATOR + GUIDANCE_PRODUCER);
   }

}

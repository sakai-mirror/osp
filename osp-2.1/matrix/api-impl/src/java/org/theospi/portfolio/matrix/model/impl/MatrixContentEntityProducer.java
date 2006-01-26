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
package org.theospi.portfolio.matrix.model.impl;

import org.theospi.portfolio.shared.mgt.OspEntityProducerBase;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Nov 8, 2005
 * Time: 5:27:48 PM
 * To change this template use File | Settings | File Templates.
 */
public class MatrixContentEntityProducer extends OspEntityProducerBase {
   public static final String MATRIX_PRODUCER = "ospMatrix";

   public String getLabel() {
      return MATRIX_PRODUCER;
   }

   public void init() {
      getEntityManager().registerEntityProducer(this);
   }
}

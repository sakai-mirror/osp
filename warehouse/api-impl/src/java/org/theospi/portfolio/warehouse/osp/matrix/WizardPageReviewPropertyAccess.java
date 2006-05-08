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
package org.theospi.portfolio.warehouse.osp.matrix;

import org.theospi.portfolio.matrix.MatrixManager;
import org.theospi.portfolio.warehouse.intf.PropertyAccess;
import org.sakaiproject.metaobj.shared.model.IdentifiableObject;
import org.theospi.portfolio.review.mgt.ReviewManager;
import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Dec 19, 2005
 * Time: 11:12:52 PM
 * To change this template use File | Settings | File Templates.
 */
public class WizardPageReviewPropertyAccess implements PropertyAccess {

   private ReviewManager reviewManager;
   
   public Object getPropertyValue(Object source) throws Exception {
      IdentifiableObject identifiableObj = (IdentifiableObject)source;
      return reviewManager.getReviewsByParent(identifiableObj.getId().getValue());
   }
   
   public ReviewManager getReviewManager()
   {
      return reviewManager;
   }
   
   public void setReviewManager(ReviewManager reviewManager)
   {
      this.reviewManager = reviewManager;
   }
}

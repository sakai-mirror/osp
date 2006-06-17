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
package org.theospi.portfolio.review.mgt;

import java.util.List;

import org.sakaiproject.entity.api.Reference;
import org.sakaiproject.metaobj.shared.model.Id;
import org.theospi.portfolio.review.model.Review;
import org.theospi.portfolio.shared.model.Node;

public interface ReviewManager {

   public final static String CURRENT_REVIEW = "org.theospi.portfolio.review.currentReview";
   public final static String CURRENT_REVIEW_ID = "org.theospi.portfolio.review.currentReviewId";
   public final static String CANCEL_REVIEW = "org.theospi.portfolio.review.cancelReview";

   public Review createNew(String description, String siteId);

   public Review getReview(Id reviewId);

   public Review saveReview(Review review);

   public void deleteReview(Review review);

   public List listReviews(String siteId);

   public Review getReview(String id);
   
   public Node getNode(Reference ref);
   
   public List getReviewsByParent(String parentId);
   public List getReviewsByParent(String parentId, String siteId, String producer);
   public List getReviewsByParentAndType(String parentId, int type, String siteId, String producer);
}

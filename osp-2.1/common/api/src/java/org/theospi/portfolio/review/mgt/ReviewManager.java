package org.theospi.portfolio.review.mgt;

import java.util.List;

import org.sakaiproject.metaobj.shared.model.Id;
import org.sakaiproject.service.legacy.entity.Reference;
import org.theospi.portfolio.review.model.Review;
import org.theospi.portfolio.shared.model.Node;

public interface ReviewManager {

   public final static String CURRENT_REVIEW = "org.theospi.portfolio.review.currentReview";
   public final static String CURRENT_REVIEW_ID = "org.theospi.portfolio.review.currentReviewId";
   public final static String CANCEL_REVIEW = "org.theospi.portfolio.review.cancelReview";

   public Review createNew(String owner, String description, String siteId, 
         Id securityQualifier, String securityViewFunction, String securityEditFunction);

   public Review getReview(Id reviewId);

   public Review saveReview(Review review);

   public void deleteReview(Review review);

   public Reference decorateReference(Review review, String reference);

   public List listReviews(String siteId);

   public Review getReview(String id);
   
   public Node getNode(Reference ref);
   
   public List getReviewsByParent(String parentId);
   public List getReviewsByParentAndType(String parentId, int type);
}

package org.theospi.portfolio.review;

import org.theospi.portfolio.review.model.Review;
import org.theospi.portfolio.review.tool.ReviewTool;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Nov 14, 2005
 * Time: 4:52:55 PM
 * To change this template use File | Settings | File Templates.
 */
public class DecoratedReview {
   private Review base;
   private ReviewTool tool;

   public DecoratedReview(ReviewTool tool, Review base) {
      this.base = base;
      this.tool = tool;
   }

   public Review getBase() {
      return base;
   }

   public void setBase(Review base) {
      this.base = base;
   }

   public String processActionEdit() {
      return tool.processActionEdit(base);
   }

   public String processActionView() {
      return tool.processActionView(base);
   }

   public String processActionDelete() {
      return tool.processActionDelete(base);
   }  
   
}

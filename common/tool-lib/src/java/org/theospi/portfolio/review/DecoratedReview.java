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

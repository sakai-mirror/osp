package org.theospi.portfolio.review.impl;

import org.theospi.portfolio.shared.mgt.OspEntityProducerBase;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Nov 14, 2005
 * Time: 6:03:02 PM
 * To change this template use File | Settings | File Templates.
 */
public class ReviewEntityProducer extends OspEntityProducerBase {

   public static final String REVIEW_PRODUCER = "ospReview";

   public String getLabel() {
      return REVIEW_PRODUCER;
   }

   public void init() {
      getEntityManager().registerEntityProducer(this);
   }

}

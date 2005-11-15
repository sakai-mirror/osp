package org.theospi.portfolio.guidance.impl;

import org.theospi.portfolio.shared.mgt.OspEntityProducerBase;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Nov 14, 2005
 * Time: 6:03:02 PM
 * To change this template use File | Settings | File Templates.
 */
public class GuidanceEntityProducer extends OspEntityProducerBase {

   public static final String GUIDANCE_PRODUCER = "ospGuidance";

   public String getLabel() {
      return GUIDANCE_PRODUCER;
   }

   public void init() {
      getEntityManager().registerEntityProducer(this);
   }

}

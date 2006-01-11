package org.theospi.portfolio.workflow.impl;

import org.theospi.portfolio.shared.mgt.OspEntityProducerBase;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Nov 14, 2005
 * Time: 6:03:02 PM
 * To change this template use File | Settings | File Templates.
 */
public class WorkflowEntityProducer extends OspEntityProducerBase {

   public static final String WORKFLOW_PRODUCER = "ospWorkflow";

   public String getLabel() {
      return WORKFLOW_PRODUCER;
   }

   public void init() {
      getEntityManager().registerEntityProducer(this);
   }

}

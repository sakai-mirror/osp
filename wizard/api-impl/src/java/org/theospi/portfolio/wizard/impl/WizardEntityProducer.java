package org.theospi.portfolio.wizard.impl;

import org.theospi.portfolio.shared.mgt.OspEntityProducerBase;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Nov 14, 2005
 * Time: 6:03:02 PM
 * To change this template use File | Settings | File Templates.
 */
public class WizardEntityProducer extends OspEntityProducerBase {
           
   public static final String WIZARD_PRODUCER = "ospWizard";

   public String getLabel() {
      return WIZARD_PRODUCER;
   }

   public void init() {
      getEntityManager().registerEntityProducer(this);
   }

}

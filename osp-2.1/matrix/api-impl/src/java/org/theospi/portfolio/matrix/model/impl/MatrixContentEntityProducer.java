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

/**********************************************************************************
 *
 * $Header: /opt/CVS/osp/src/portfolio/org/theospi/portfolio/list/intf/CustomLinkListGenerator.java,v 1.2 2005/08/30 21:27:09 jellis Exp $
 *
 ***********************************************************************************
 * Copyright (c) 2005 the r-smart group, inc.
 **********************************************************************************/
package org.theospi.portfolio.list.intf;

public interface CustomLinkListGenerator extends ListGenerator {

   /**
    * Create a custom link for enty if it needs
    * to customize, otherwise, null to use the usual entry
    * @param entry
    * @return link to use or null to use normal redirect link
    */
   public String getCustomLink(Object entry);

}

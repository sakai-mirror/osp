package org.theospi.portfolio.list.intf;

import java.util.Map;

/*
 * forces tool into certain state
 * $Header: /opt/CVS/osp2.x/presentation/tool/src/java/org/theospi/portfolio/list/intf/ActionableListGenerator.java,v 1.1 2005/08/10 21:08:30 chmaurer Exp $
 * $Revision$
 * $Date$
 */

public interface ActionableListGenerator extends ListGenerator {
   /**
    * Store any params in request into tool state.  
    * These will be added to redirect call to load the tool state.
    * @param toolId
    * @param request
    */
   public void setToolState(String toolId, Map request);
}

/**********************************************************************************
 *
 * $Header: /opt/CVS/osp2.x/common/api-impl/src/java/org/theospi/portfolio/security/model/QualifiedSimpleToolPermissionManager.java,v 1.1.1.1 2005/07/14 15:53:42 jellis Exp $
 *
 ***********************************************************************************
 * Copyright (c) 2005 the r-smart group, inc.
 **********************************************************************************/
package org.theospi.portfolio.security.model;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.metaobj.shared.model.Id;

public class QualifiedSimpleToolPermissionManager extends SimpleToolPermissionManager {
   protected final transient Log logger = LogFactory.getLog(getClass());

   private Id qualifier;

   protected PermissionsEdit setupPermissions(String worksiteId, Id qualifier) {
      return super.setupPermissions(worksiteId, this.qualifier);
   }

   public String getQualifier() {
      return qualifier.getValue();
   }

   public void setQualifier(String qualifier) {
      this.qualifier = getIdManager().getId(qualifier);
   }

}

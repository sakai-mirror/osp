/**********************************************************************************

 *

 * $Header: /opt/CVS/osp2.x/glossary/api/src/java/org/theospi/portfolio/help/model/GlossaryDescription.java,v 1.1 2005/07/08 01:18:46 jellis Exp $

 *

 ***********************************************************************************

 * Copyright (c) 2005 the r-smart group, inc.

 **********************************************************************************/

package org.theospi.portfolio.help.model;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.metaobj.shared.model.Id;

public class GlossaryDescription extends GlossaryBase {
   protected final transient Log logger = LogFactory.getLog(getClass());

   private Id entryId;
   private String longDescription;

   public Id getEntryId() {
      return entryId;
   }

   public void setEntryId(Id entryId) {
      this.entryId = entryId;
   }

   public String getLongDescription() {
      return longDescription;
   }

   public void setLongDescription(String longDescription) {
      this.longDescription = longDescription;
   }
}


package org.theospi.portfolio.guidance.mgt;

import org.theospi.portfolio.guidance.model.Guidance;
import org.theospi.portfolio.shared.mgt.ContentEntityWrapper;
import org.sakaiproject.metaobj.shared.model.Id;
import org.sakaiproject.service.legacy.entity.Reference;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Nov 11, 2005
 * Time: 12:07:35 PM
 * To change this template use File | Settings | File Templates.
 */
public interface GuidanceManager {

   public Guidance createNew(String description, String siteId, Id securityQualifier, String securityFunction);

   public Guidance getGuidance(Id guidanceId);

   public Guidance saveGuidance(Guidance guidance);

   public void deleteGuidance(Guidance guidance);

   public Reference decorateReference(Guidance guidance, String reference);
}

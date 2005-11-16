package org.theospi.portfolio.guidance.mgt;

import org.theospi.portfolio.guidance.model.Guidance;
import org.theospi.portfolio.shared.mgt.ContentEntityWrapper;
import org.sakaiproject.metaobj.shared.model.Id;
import org.sakaiproject.service.legacy.entity.Reference;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Nov 11, 2005
 * Time: 12:07:35 PM
 * To change this template use File | Settings | File Templates.
 */
public interface GuidanceManager {

   public final static String CURRENT_GUIDANCE = "org.theospi.portfolio.guidance.currentGuidance";
   public final static String CURRENT_GUIDANCE_ID = "org.theospi.portfolio.guidance.currentGuidanceId";

   public Guidance createNew(String description, String siteId, Id securityQualifier,
                             String securityViewFunction, String securityEditFunction);

   public Guidance getGuidance(Id guidanceId);

   public Guidance saveGuidance(Guidance guidance);

   public void deleteGuidance(Guidance guidance);

   public Reference decorateReference(Guidance guidance, String reference);

   public List listGuidances(String siteId);

   public Guidance getGuidance(String id);
}

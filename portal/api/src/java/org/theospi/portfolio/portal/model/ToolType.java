package org.theospi.portfolio.portal.model;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Mar 7, 2006
 * Time: 1:32:43 PM
 * To change this template use File | Settings | File Templates.
 */
public class ToolType {

   public static final String SITE_QUALIFIER = "org.theospi.portfolio.portal.model.ToolType.site";
   public static final String PLACEMENT_QUALIFIER = "org.theospi.portfolio.portal.model.ToolType.placement";
   public static final String SAKAI_QUALIFIER = "org.theospi.portfolio.portal.model.ToolType.sakai";

   private List functions;
   private String qualifierType;

   public List getFunctions() {
      return functions;
   }

   public void setFunctions(List functions) {
      this.functions = functions;
   }

   public String getQualifierType() {
      return qualifierType;
   }

   public void setQualifierType(String qualifierType) {
      this.qualifierType = qualifierType;
   }

}

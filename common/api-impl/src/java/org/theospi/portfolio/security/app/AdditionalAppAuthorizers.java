package org.theospi.portfolio.security.app;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Jul 21, 2005
 * Time: 10:41:54 AM
 * To change this template use File | Settings | File Templates.
 */
public class AdditionalAppAuthorizers {

   private List additionalAppAuthorizers;
   private AppAuthFacade authzManager;

   public List getAdditionalAppAuthorizers() {
      return additionalAppAuthorizers;
   }

   public void setAdditionalAppAuthorizers(List additionalAppAuthorizers) {
      this.additionalAppAuthorizers = additionalAppAuthorizers;
   }

   public AppAuthFacade getAuthzManager() {
      return authzManager;
   }

   public void setAuthzManager(AppAuthFacade authzManager) {
      this.authzManager = authzManager;
   }

   public void init() {
      getAuthzManager().addAppAuthorizers(getAdditionalAppAuthorizers());
   }

}

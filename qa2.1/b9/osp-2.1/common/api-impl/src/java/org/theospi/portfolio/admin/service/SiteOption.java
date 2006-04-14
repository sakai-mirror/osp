package org.theospi.portfolio.admin.service;

import org.theospi.portfolio.admin.model.IntegrationOption;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Mar 2, 2006
 * Time: 11:41:17 AM
 * To change this template use File | Settings | File Templates.
 */
public class SiteOption extends IntegrationOption {

   private String siteId;
   private String siteType;
   private String realmTemplate;

   private String siteTitle;
   private String siteDescription;

   public String getSiteId() {
      return siteId;
   }

   public void setSiteId(String siteId) {
      this.siteId = siteId;
   }

   public String getSiteType() {
      return siteType;
   }

   public void setSiteType(String siteType) {
      this.siteType = siteType;
   }

   public String getRealmTemplate() {
      return realmTemplate;
   }

   public void setRealmTemplate(String realmTemplate) {
      this.realmTemplate = realmTemplate;
   }

   public String getSiteTitle() {
      return siteTitle;
   }

   public void setSiteTitle(String siteTitle) {
      this.siteTitle = siteTitle;
   }

   public String getSiteDescription() {
      return siteDescription;
   }

   public void setSiteDescription(String siteDescription) {
      this.siteDescription = siteDescription;
   }

}

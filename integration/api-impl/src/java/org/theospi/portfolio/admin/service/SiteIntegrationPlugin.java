package org.theospi.portfolio.admin.service;

import org.sakaiproject.exception.IdInvalidException;
import org.sakaiproject.exception.IdUnusedException;
import org.sakaiproject.exception.IdUsedException;
import org.sakaiproject.exception.PermissionException;
import org.sakaiproject.site.api.Site;
import org.sakaiproject.site.api.SiteService;
import org.theospi.portfolio.admin.model.IntegrationOption;
import org.theospi.portfolio.shared.model.OspException;


/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Mar 2, 2006
 * Time: 1:38:40 PM
 * To change this template use File | Settings | File Templates.
 */
public class SiteIntegrationPlugin extends IntegrationPluginBase {

   private SiteService siteService;

   public IntegrationOption updateOption(IntegrationOption option) {
      SiteOption siteOption = (SiteOption) option;

      try {
         Site site = getSiteService().getSite(siteOption.getSiteId());
         if (site != null) {
            return siteOption;
         }
      } catch (IdUnusedException e) {
         // no site found... this means we should go on and create it.
      }

      try {
         Site site = getSiteService().addSite(siteOption.getSiteId(), siteOption.getSiteType());

         site.setTitle(siteOption.getSiteTitle());
         site.setDescription(siteOption.getSiteDescription());
         site.setPublished(true);
         
         getSiteService().save(site);
      } catch (IdInvalidException e) {
         throw new OspException(e);
      } catch (IdUsedException e) {
         throw new OspException(e);
      } catch (PermissionException e) {
         throw new OspException(e);
      } catch (IdUnusedException e) {
         throw new OspException(e);
      }

      return siteOption;
   }

   public boolean executeOption(IntegrationOption option) {
      updateOption(option);
      return true;
   }

   public SiteService getSiteService() {
      return siteService;
   }

   public void setSiteService(SiteService siteService) {
      this.siteService = siteService;
   }
}

package org.theospi.portfolio.shared.mgt;

import java.util.Map;

import org.sakaiproject.metaobj.shared.mgt.HomeFactory;

public class AdditionalXmlHome {


   private Map additionalHomes;
   private HomeFactory xmlHomeFactory;

   public Map getAdditionalHomes() {
      return additionalHomes;
   }

   public void setAdditionalHomes(Map additionalHomes) {
      this.additionalHomes = additionalHomes;
   }

   public HomeFactory getXmlHomeFactory() {
      return xmlHomeFactory;
   }

   public void setXmlHomeFactory(HomeFactory xmlHomeFactory) {
      this.xmlHomeFactory = xmlHomeFactory;
   }

   public void init() {
      getXmlHomeFactory().getHomes().putAll(getAdditionalHomes());
   }

}


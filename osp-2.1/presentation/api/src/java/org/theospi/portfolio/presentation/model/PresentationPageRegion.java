package org.theospi.portfolio.presentation.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.sakaiproject.metaobj.shared.model.Id;
import org.sakaiproject.metaobj.shared.model.IdentifiableObject;

public class PresentationPageRegion extends IdentifiableObject implements Serializable {
   
   private Id id;
   private PresentationPage page;
   private String regionId;
   private List items = new ArrayList();
   
   public Id getId() {
      return id;
   }
   public void setId(Id id) {
      this.id = id;
   }
   public List getItems() {
      return items;
   }
   public void setItems(List items) {
      this.items = items;
   }
   public PresentationPage getPage() {
      return page;
   }
   public void setPage(PresentationPage page) {
      this.page = page;
   }
   public String getRegionId() {
      return regionId;
   }
   public void setRegionId(String regionId) {
      this.regionId = regionId;
   }
}

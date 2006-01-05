package org.theospi.portfolio.presentation.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;

import org.sakaiproject.metaobj.shared.model.Id;
import org.sakaiproject.metaobj.shared.model.IdentifiableObject;

public class PresentationPageRegion extends IdentifiableObject implements Serializable {
   
   private Id id;
   private PresentationPage page;
   private String regionId;
   private List items = new ArrayList();
   private String type = "text";
   private String helpText;

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

   public void reorderItems() {
      int index = 0;
      for (Iterator i=getItems().iterator();i.hasNext();) {
         PresentationPageItem item = (PresentationPageItem) i.next();
         item.setRegionItemSeq(index);
         index++;
      }
   }

   public void addBlank() {
      PresentationPageItem item = new PresentationPageItem();
      item.setRegion(this);
      item.setLayoutRegionId(this.getRegionId());
      item.setType(getType());
      item.setValue(getHelpText());
      getItems().add(item);
      reorderItems();
   }

   public String getType() {
      return type;
   }

   public void setType(String type) {
      this.type = type;
   }

   public String getHelpText() {
      return helpText;
   }

   public void setHelpText(String helpText) {
      this.helpText = helpText;
   }
}

package org.theospi.portfolio.presentation.model;

import org.sakaiproject.metaobj.shared.model.Id;

public class PresentationItemProperty {
   
   public final static String RENDER_STYLE = "renderStyle";
   public final static String ITEM_HEIGHT = "itemHeight";
   public final static String ITEM_WIDTH = "itemWidth";
   public final static String LINK_TARGET = "linkTarget";
   
   private Id id;
   private PresentationPageItem item;
   private String key;
   private String value;
   
   public String getKey() {
      return key;
   }
   public void setKey(String key) {
      this.key = key;
   }
   public String getValue() {
      return value;
   }
   public void setValue(String value) {
      this.value = value;
   }
   public PresentationPageItem getItem() {
      return item;
   }
   public void setItem(PresentationPageItem item) {
      this.item = item;
   }
   public Id getId() {
      return id;
   }
   public void setId(Id id) {
      this.id = id;
   }
   
   

}

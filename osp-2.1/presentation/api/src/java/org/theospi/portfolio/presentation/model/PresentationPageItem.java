package org.theospi.portfolio.presentation.model;

import java.io.Serializable;
import java.util.Set;
import java.util.Iterator;

import org.sakaiproject.metaobj.shared.model.Id;
import org.sakaiproject.metaobj.shared.model.IdentifiableObject;

public class PresentationPageItem extends IdentifiableObject implements Serializable {

  
   private Id id;
   private PresentationPageRegion region;
   private String layoutRegionId;
   private int regionItemSeq;
   private String type;
   private String value;
   private Set properties;
   
   
   public Id getId() {
      return id;
   }
   public void setId(Id id) {
      this.id = id;
   }
   public String getLayoutRegionId() {
      return layoutRegionId;
   }
   public void setLayoutRegionId(String layoutRegionId) {
      this.layoutRegionId = layoutRegionId;
   }
   public PresentationPageRegion getRegion() {
      return region;
   }
   public void setRegion(PresentationPageRegion region) {
      this.region = region;
   }
   public Set getProperties() {
      return properties;
   }
   public void setProperties(Set properties) {
      this.properties = properties;
   }
   public int getRegionItemSeq() {
      return regionItemSeq;
   }
   public void setRegionItemSeq(int regionItemSeq) {
      this.regionItemSeq = regionItemSeq;
   }
   public String getType() {
      return type;
   }
   public void setType(String type) {
      this.type = type;
   }
   public String getValue() {
      return value;
   }
   public void setValue(String value) {
      this.value = value;
   }
   
}

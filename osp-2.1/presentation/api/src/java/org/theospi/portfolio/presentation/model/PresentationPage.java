package org.theospi.portfolio.presentation.model;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import org.sakaiproject.metaobj.shared.model.Id;
import org.sakaiproject.metaobj.shared.model.IdentifiableObject;

public class PresentationPage extends IdentifiableObject implements Serializable {
   
   private Id id;
   private Presentation presentation;
   private PresentationLayout layout;
   //private Style style;
   private String style;
   private int sequence;
   private Set regions = new HashSet();
   
   
   public Id getId() {
      return id;
   }
   public void setId(Id id) {
      this.id = id;
   }
   public Set getRegions() {
      return regions;
   }
   public void setRegions(Set regions) {
      this.regions = regions;
   }
   public PresentationLayout getLayout() {
      return layout;
   }
   public void setLayout(PresentationLayout layout) {
      this.layout = layout;
   }
   public int getSequence() {
      return sequence;
   }
   public void setSequence(int sequence) {
      this.sequence = sequence;
   }
//   public Style getStyle() {
//      return style;
//   }
//   public void setStyle(Style style) {
//      this.style = style;
//   }
   
   public String getStyle() {
      return style;
   }
   public void setStyle(String style) {
      this.style = style;
   }
   
   public Presentation getPresentation() {
      return presentation;
   }
   public void setPresentation(Presentation presentation) {
      this.presentation = presentation;
   }
   
   
}

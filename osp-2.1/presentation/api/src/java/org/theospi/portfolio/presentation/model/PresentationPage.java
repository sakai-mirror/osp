package org.theospi.portfolio.presentation.model;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.sakaiproject.metaobj.shared.model.Id;
import org.sakaiproject.metaobj.shared.model.IdentifiableObject;

public class PresentationPage extends IdentifiableObject implements Serializable {
   
   private Id id;
   private String title;
   private Presentation presentation;
   private PresentationLayout layout;
   //private Style style;
   private String style;
   private String sequence;
   private Set regions = new HashSet();
   private Date created;
   private Date modified;
   private boolean navigation;
   private boolean newObject;

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
   public String getSequence() {
      return sequence;
   }
   public void setSequence(String sequence) {
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
   public String getTitle() {
      return title;
   }
   public void setTitle(String title) {
      this.title = title;
   }
   public Date getCreated() {
      return created;
   }
   public void setCreated(Date created) {
      this.created = created;
   }
   public Date getModified() {
      return modified;
   }
   public void setModified(Date modified) {
      this.modified = modified;
   }

   public boolean isNewObject() {
      return newObject;
   }

   public void setNewObject(boolean newObject) {
      this.newObject = newObject;
   }

   public boolean isNavigation() {
      return navigation;
   }

   public void setNavigation(boolean navigation) {
      this.navigation = navigation;
   }

}

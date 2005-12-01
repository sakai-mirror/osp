package org.theospi.portfolio.wizard.model;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.sakaiproject.metaobj.shared.model.Agent;
import org.sakaiproject.metaobj.shared.model.Id;
import org.sakaiproject.metaobj.shared.model.IdentifiableObject;
import org.theospi.portfolio.guidance.model.Guidance;

public class Wizard extends IdentifiableObject {

   private String name;
   private String description;
   private String keywords;
   private Date created;
   private Date modified;
   private transient Agent owner;
   private Id guidanceId;
   private Set supportItems = new HashSet();
   private boolean published = false;
   private String type = WIZARD_TYPE;
   private WizardStyleItem wizardStyleItem;
   
   private String siteId;
   private Id securityQualifier;
   private String securityViewFunction;
   private String securityEditFunction;
   
   private transient Guidance guidance;
   
   private boolean newObject = false;
   
   public final static String WIZARD_TYPE = "wizard";
   
   public Wizard() {
   }

   public Wizard(Id id, Agent owner, String siteId, Id securityQualifier,
                   String securityViewFunction, String securityEditFunction) {
      setId(id);
      this.owner = owner;
      this.siteId = siteId;
      this.securityQualifier = securityQualifier;
      this.securityViewFunction = securityViewFunction;
      this.securityEditFunction = securityEditFunction;
      newObject = true;
   }
   
   public String getType() {
      //TODO - We may want to have different types of wizards later.
      return type;
   }
   public void setType(String type) {
      this.type = type;
   }
   
   public Date getCreated() {
      return created;
   }
   public void setCreated(Date created) {
      this.created = created;
   }
   public String getDescription() {
      return description;
   }
   public void setDescription(String description) {
      this.description = description;
   }

   public Date getModified() {
      return modified;
   }
   public void setModified(Date modified) {
      this.modified = modified;
   }
   public String getName() {
      return name;
   }
   public void setName(String name) {
      this.name = name;
   }
   public Agent getOwner() {
      return owner;
   }
   public void setOwner(Agent owner) {
      this.owner = owner;
   }
   public boolean isNewObject() {
      return newObject;
   }
   public void setNewObject(boolean newObject) {
      this.newObject = newObject;
   }
   public String getSecurityEditFunction() {
      return securityEditFunction;
   }
   public void setSecurityEditFunction(String securityEditFunction) {
      this.securityEditFunction = securityEditFunction;
   }
   public Id getSecurityQualifier() {
      return securityQualifier;
   }
   public void setSecurityQualifier(Id securityQualifier) {
      this.securityQualifier = securityQualifier;
   }
   public String getSecurityViewFunction() {
      return securityViewFunction;
   }
   public void setSecurityViewFunction(String securityViewFunction) {
      this.securityViewFunction = securityViewFunction;
   }
   public String getSiteId() {
      return siteId;
   }
   public void setSiteId(String siteId) {
      this.siteId = siteId;
   }
   public String getKeywords() {
      return keywords;
   }
   public void setKeywords(String keywords) {
      this.keywords = keywords;
   }
   public Guidance getGuidance() {
      return guidance;
   }
   public void setGuidance(Guidance guidance) {
      this.guidance = guidance;
   }

   public Set getSupportItems() {
      return supportItems;
   }

   public void setSupportItems(Set supportingObjects) {
      this.supportItems = supportingObjects;
   }

   public WizardStyleItem getWizardStyleItem() {
      return wizardStyleItem;
   }

   public void setWizardStyleItem(WizardStyleItem wizardStyleItem) {
      this.wizardStyleItem = wizardStyleItem;
   }

   public Id getGuidanceId() {
      return guidanceId;
   }

   public void setGuidanceId(Id guidanceId) {
      this.guidanceId = guidanceId;
   }

   public boolean isPublished() {
      return published;
   }

   public void setPublished(boolean published) {
      this.published = published;
   }
  
  
}

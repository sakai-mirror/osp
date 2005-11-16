package org.theospi.portfolio.guidance.model;

import org.sakaiproject.metaobj.shared.model.IdentifiableObject;
import org.sakaiproject.metaobj.shared.model.Id;

import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Nov 11, 2005
 * Time: 12:06:22 PM
 * To change this template use File | Settings | File Templates.
 */
public class Guidance extends IdentifiableObject {

   public final static String INSTRUCTION_TYPE = "instruction";
   public final static String EXAMPLE_TYPE = "example";
   public final static String RATIONALE_TYPE = "rationale";

   private String description;
   private String siteId;
   private Id securityQualifier;
   private String securityViewFunction;
   private String securityEditFunction;

   private List items;

   private boolean newObject = false;

   public Guidance() {
   }

   public Guidance(Id id, String description, String siteId, Id securityQualifier,
                   String securityViewFunction, String securityEditFunction) {
      this.description = description;
      this.siteId = siteId;
      this.securityQualifier = securityQualifier;
      this.securityViewFunction = securityViewFunction;
      this.securityEditFunction = securityEditFunction;
      setId(id);
      newObject = true;
      items = new ArrayList();
   }

   public String getDescription() {
      return description;
   }

   public void setDescription(String description) {
      this.description = description;
   }

   public String getSiteId() {
      return siteId;
   }

   public void setSiteId(String siteId) {
      this.siteId = siteId;
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

   public String getSecurityEditFunction() {
      return securityEditFunction;
   }

   public void setSecurityEditFunction(String securityEditFunction) {
      this.securityEditFunction = securityEditFunction;
   }

   public List getItems() {
      return items;
   }

   public void setItems(List items) {
      this.items = items;
   }

   public GuidanceItem getItem(String type) {
      for (Iterator i=getItems().iterator();i.hasNext();) {
         GuidanceItem item = (GuidanceItem)i.next();
         if (item.getType().equals(type)) {
            return item;
         }
      }
      return null;
   }

   public GuidanceItem getInstruction() {
      return getItem(INSTRUCTION_TYPE);
   }

   public GuidanceItem getExample() {
      return getItem(EXAMPLE_TYPE);
   }

   public GuidanceItem getRationale() {
      return getItem(RATIONALE_TYPE);
   }

   public boolean isNewObject() {
      return newObject;
   }

   public void setNewObject(boolean newObject) {
      this.newObject = newObject;
   }

}



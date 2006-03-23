/**********************************************************************************
* $URL$
* $Id$
***********************************************************************************
*
* Copyright (c) 2005, 2006 The Sakai Foundation.
*
* Licensed under the Educational Community License, Version 1.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*      http://www.opensource.org/licenses/ecl1.php
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*
**********************************************************************************/
package org.theospi.portfolio.presentation.model;

import org.sakaiproject.metaobj.shared.model.Agent;
import org.sakaiproject.metaobj.shared.model.Id;
import org.sakaiproject.metaobj.shared.model.IdentifiableObject;
import org.theospi.portfolio.shared.model.Node;

import java.io.Serializable;
import java.util.*;

public class PresentationTemplate extends IdentifiableObject implements Serializable {
   private String name;
   private String description;
   private Date created;
   private Date modified;
   private transient Agent owner;
   private Id renderer;
   private Id propertyPage;
   private String documentRoot;
   private boolean active;
   private boolean includeHeaderAndFooter;
   private Set items = new TreeSet(new PresentationItemComparator());
   private Set files = new HashSet();
   private boolean includeComments = false;
   private boolean published = false;
   private String markup;
   private String toolId;
   private String siteId;
   transient private Set deletedItems = new HashSet();
   transient private boolean validate = true;
   transient private boolean newObject = false;

   /**
    * used in web form
    */
   private PresentationItemDefinition item = new PresentationItemDefinition();

   /**
    * used in web form
    */
   private TemplateFileRef fileRef = new TemplateFileRef();

   transient private String rendererName;
   transient private String propertyPageName;

   static final long serialVersionUID = -6220810277272518156l;

   public TemplateFileRef getFileRef() {
      return fileRef;
   }

   public void setFileRef(TemplateFileRef fileRef) {
      this.fileRef = fileRef;
   }

   public PresentationItemDefinition getItem() {
      return item;
   }

   public void setItem(PresentationItemDefinition item) {
      this.item = item;
   }

   public String getSiteId() {
      return siteId;
   }

   public void setSiteId(String siteId) {
      this.siteId = siteId;
   }

   public String getToolId() {
      return toolId;
   }

   public void setToolId(String toolId) {
      this.toolId = toolId;
   }

   public String getName() {
      return name;
   }

   public String getDescription() {
      return description;
   }

   public Date getCreated() {
      return created;
   }

   public Date getModified() {
      return modified;
   }

   public Agent getOwner() {
      return owner;
   }

   public Id getRenderer() {
      return renderer;
   }

   public Id getPropertyPage() {
       return propertyPage;
   }
   
   public String getDocumentRoot() {
       return documentRoot;
   }

   public boolean isActive() {
      return active;
   }

   public boolean getIncludeHeaderAndFooter() {
      return isIncludeHeaderAndFooter();
   }

   public void setName(String name) {
      this.name = name;
   }

   public void setDescription(String description) {
      this.description = description;
   }

   public void setCreated(Date created) {
      this.created = created;
   }

   public void setModified(Date modified) {
      this.modified = modified;
   }

   public void setOwner(Agent owner) {
      this.owner = owner;
   }

   public void setRenderer(Id renderer) {
      this.renderer = renderer;
   }
   
   public void setPropertyPage(Id propertyPage) {
       this.propertyPage = propertyPage;
   }
   
   public void setDocumentRoot(String documentRoot)
   {
       this.documentRoot = documentRoot;
   }

   public void setActive(boolean active) {
      this.active = active;
   }

   public boolean isIncludeHeaderAndFooter() {
      return includeHeaderAndFooter;
   }

   public void setIncludeHeaderAndFooter(boolean includeHeaderAndFooter) {
      this.includeHeaderAndFooter = includeHeaderAndFooter;
   }

   public Collection getItemDefinitions() {
      return items;
   }

   public void orderItemDefs() {
      Set ordered = getSortedItems();
      int index = 1;
      for (Iterator i=ordered.iterator();i.hasNext();) {
         PresentationItemDefinition item = (PresentationItemDefinition)i.next();
         item.setSequence(index);
         index++;
      }
   }

   public Set getItems() {
      return items;
   }

   public void setItems(Set items) {
      this.items = items;
   }

   public Set getSortedItems() {
      Set returned = new TreeSet(new PresentationItemComparator());
      returned.addAll(items);
      return returned;
   }

   public Set getFiles() {
      return files;
   }

   public void setFiles(Set files) {
      this.files = files;
   }

   public boolean isIncludeComments() {
      return includeComments;
   }

   public void setIncludeComments(boolean includeComments) {
      this.includeComments = includeComments;
   }

   public boolean isPublished() {
      return published;
   }

   public void setPublished(boolean published) {
      this.published = published;
   }

   public String getMarkup() {
      return markup;
   }

   public void setMarkup(String markup) {
      this.markup = markup;
   }

   public void setPropertyPageNode(Node propertyPageNode) {
      this.propertyPageName = propertyPageNode.getDisplayName();
   }

   public void setRendererNode(Node rendererNode) {
      this.rendererName = rendererNode.getDisplayName();
   }

   public String getPropertyPageName() {
      return propertyPageName;
   }

   public void setPropertyPageName(String propertyPageName) {
      this.propertyPageName = propertyPageName;
   }

   public String getRendererName() {
      return rendererName;
   }

   public void setRendererName(String rendererName) {
      this.rendererName = rendererName;
   }

   public void setItemSequence(String[] itemSequence) {
      int index = 0;
      Set items = getSortedItems();
      for (Iterator i=items.iterator();i.hasNext();) {
         PresentationItemDefinition item = (PresentationItemDefinition)i.next();
         if (index < itemSequence.length) {
            item.setNewSequence(Integer.valueOf(itemSequence[index]).intValue());
         }
         index++;
      }
      orderItemDefs();
   }

   public Set getDeletedItems() {
      return deletedItems;
   }

   public void setDeletedItems(Set deletedItems) {
      this.deletedItems = deletedItems;
   }

   public boolean isValidate() {
      return validate;
   }

   public void setValidate(boolean validate) {
      this.validate = validate;
   }

   public boolean isNewObject() {
      return newObject;
   }

   public void setNewObject(boolean newObject) {
      this.newObject = newObject;
   }
}

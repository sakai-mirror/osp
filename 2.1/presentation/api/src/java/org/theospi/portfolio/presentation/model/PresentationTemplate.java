/*
 * The Open Source Portfolio Initiative Software is Licensed under the Educational Community License Version 1.0:
 *
 * This Educational Community License (the "License") applies to any original work of authorship
 * (the "Original Work") whose owner (the "Licensor") has placed the following notice immediately
 * following the copyright notice for the Original Work:
 *
 * Copyright (c) 2004 Trustees of Indiana University and r-smart Corporation
 *
 * This Original Work, including software, source code, documents, or other related items, is being
 * provided by the copyright holder(s) subject to the terms of the Educational Community License.
 * By obtaining, using and/or copying this Original Work, you agree that you have read, understand,
 * and will comply with the following terms and conditions of the Educational Community License:
 *
 * Permission to use, copy, modify, merge, publish, distribute, and sublicense this Original Work and
 * its documentation, with or without modification, for any purpose, and without fee or royalty to the
 * copyright holder(s) is hereby granted, provided that you include the following on ALL copies of the
 * Original Work or portions thereof, including modifications or derivatives, that you make:
 *
 * - The full text of the Educational Community License in a location viewable to users of the
 * redistributed or derivative work.
 *
 * - Any pre-existing intellectual property disclaimers, notices, or terms and conditions.
 *
 * - Notice of any changes or modifications to the Original Work, including the date the changes were made.
 *
 * - Any modifications of the Original Work must be distributed in such a manner as to avoid any confusion
 *  with the Original Work of the copyright holders.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
 * SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 * The name and trademarks of copyright holder(s) may NOT be used in advertising or publicity pertaining
 * to the Original or Derivative Works without specific, written prior permission. Title to copyright
 * in the Original Work and any associated documentation will at all times remain with the copyright holders.
 *
 * $Header: /opt/CVS/osp2.x/presentation/api/src/java/org/theospi/portfolio/presentation/model/PresentationTemplate.java,v 1.4 2005/08/18 18:16:29 jellis Exp $
 * $Revision$
 * $Date$
 */
/*
 * $Header: /opt/CVS/osp2.x/presentation/api/src/java/org/theospi/portfolio/presentation/model/PresentationTemplate.java,v 1.4 2005/08/18 18:16:29 jellis Exp $
 * $Revision$
 * $Date$
 */
package org.theospi.portfolio.presentation.model;

import org.sakaiproject.metaobj.shared.model.Agent;
import org.sakaiproject.metaobj.shared.model.Id;
import org.theospi.portfolio.shared.model.IdentifiableObject;
import org.theospi.portfolio.shared.model.Node;
import org.theospi.portfolio.worksite.model.ToolConfigurationWrapper;
import org.sakaiproject.service.legacy.site.ToolConfiguration;

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
}

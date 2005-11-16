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
 * $Revision: 3474 $
 * $Date: 2005-11-03 18:05:53 -0500 (Thu, 03 Nov 2005) $
 */
/*
 * $Header: /opt/CVS/osp2.x/presentation/api/src/java/org/theospi/portfolio/presentation/model/PresentationTemplate.java,v 1.4 2005/08/18 18:16:29 jellis Exp $
 * $Revision: 3474 $
 * $Date: 2005-11-03 18:05:53 -0500 (Thu, 03 Nov 2005) $
 */
package org.theospi.portfolio.presentation.model;

import org.sakaiproject.metaobj.shared.model.Agent;
import org.sakaiproject.metaobj.shared.model.Id;
import org.sakaiproject.metaobj.shared.model.IdentifiableObject;

import java.io.Serializable;
import java.util.*;

public class PresentationLayout extends IdentifiableObject implements Serializable {
   private String name;
   private String description;
   private Date created;
   private Date modified;
   private transient Agent owner;
   private Id xhtmlFileId;
   private Id previewImageId;
   private String toolId;
   private String siteId;
   private boolean published = false;
   
   transient private String xhtmlFileName;
   transient private String previewImageName;
   
   
   transient private boolean validate = true;
   transient private String filePickerAction;

   static final long serialVersionUID = -6220810277272518156l;


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

   public boolean isPublished() {
      return published;
   }

   public void setPublished(boolean published) {
      this.published = published;
   }

   public boolean isValidate() {
      return validate;
   }

   public void setValidate(boolean validate) {
      this.validate = validate;
   }

   public Id getPreviewImageId() {
      return previewImageId;
   }

   public void setPreviewImageId(Id previewImageId) {
      this.previewImageId = previewImageId;
   }

   public Id getXhtmlFileId() {
      return xhtmlFileId;
   }

   public void setXhtmlFileId(Id xhtmlFileId) {
      this.xhtmlFileId = xhtmlFileId;
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

   public String getPreviewImageName() {
      return previewImageName;
   }

   public void setPreviewImageName(String previewImageName) {
      this.previewImageName = previewImageName;
   }

   public String getXhtmlFileName() {
      return xhtmlFileName;
   }

   public void setXhtmlFileName(String xhtmlFileName) {
      this.xhtmlFileName = xhtmlFileName;
   }

   public String getFilePickerAction() {
      return filePickerAction;
   }

   public void setFilePickerAction(String filePickerAction) {
      this.filePickerAction = filePickerAction;
   }
}

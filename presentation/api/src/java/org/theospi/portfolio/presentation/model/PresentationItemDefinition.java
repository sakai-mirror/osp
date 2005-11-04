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
 * $Header: /opt/CVS/osp2.x/presentation/api/src/java/org/theospi/portfolio/presentation/model/PresentationItemDefinition.java,v 1.1 2005/08/10 21:08:30 chmaurer Exp $
 * $Revision$
 * $Date$
 */
/*
 * $Header: /opt/CVS/osp2.x/presentation/api/src/java/org/theospi/portfolio/presentation/model/PresentationItemDefinition.java,v 1.1 2005/08/10 21:08:30 chmaurer Exp $
 * $Revision$
 * $Date$
 */
package org.theospi.portfolio.presentation.model;

import org.sakaiproject.metaobj.shared.model.IdentifiableObject;
import org.sakaiproject.metaobj.shared.model.MimeType;
import org.theospi.portfolio.shared.model.ItemDefinitionMimeType;
import org.apache.commons.codec.digest.DigestUtils;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;


public class PresentationItemDefinition extends IdentifiableObject implements Serializable {
   private PresentationTemplate presentationTemplate;
   /**
    * the artifact type
    */
   private String type;
   private String name;
   private String title;
   private String description;
   private boolean allowMultiple;
   private Set mimeTypes = new HashSet();
   private String externalType = null;
   private int sequence = -1;
   private transient String action;
   private transient Integer newSequence = null;

   static final long serialVersionUID = -6220810277272518156l;

   public boolean getHasMimeTypes() {
      return (type != null && type.equals("fileArtifact"));
   }

   public PresentationTemplate getPresentationTemplate() {
      return presentationTemplate;
   }

   public String getAction() {
      return action;
   }

   public void setAction(String action) {
      this.action = action;
   }

   public String getType() {
      return type;
   }

   public String getName() {
      return name;
   }

   public String getTitle() {
      return title;
   }

   public String getDescription() {
      return description;
   }

   public boolean getAllowMultiple() {
      return isAllowMultiple();
   }

   public void setType(String type) {
      this.type = type;
   }

   public void setName(String name) {
      this.name = name;
   }

   public void setTitle(String title) {
      this.title = title;
   }

   public void setDescription(String description) {
      this.description = description;
   }

   public boolean isAllowMultiple() {
      return allowMultiple;
   }

   public void setAllowMultiple(boolean allowMultiple) {
      this.allowMultiple = allowMultiple;
   }

   public void setPresentationTemplate(PresentationTemplate presentationTemplate) {
      this.presentationTemplate = presentationTemplate;
   }

   public Set getMimeTypes() {
      return mimeTypes;
   }

   public void setMimeTypes(Set mimeTypes) {
      this.mimeTypes = mimeTypes;
   }

   public String getExternalType() {
      return externalType;
   }

   public void setExternalType(String externalType) {
      this.externalType = externalType;
   }

   public int hashCode() {
      if (getId() != null){
         return getId().hashCode();
      }
      return (type != null && name != null ) ?
            DigestUtils.md5Hex(type + name).hashCode() : 0;
   }

   public boolean allowsMimeType(MimeType mimeType) {
      if (!getHasMimeTypes()) {
         return true;
      }

      if (getMimeTypes() == null || getMimeTypes().isEmpty()) {
         return true;
      }

      for (Iterator i = getMimeTypes().iterator(); i.hasNext();) {
         ItemDefinitionMimeType currentType = (ItemDefinitionMimeType) i.next();

         if (currentType.getSecondary() != null) {
            if (mimeType.getSubType().equals(currentType.getSecondary()) &&
               mimeType.getPrimaryType().equals(currentType.getPrimary())) {
               return true;
            }
         } else {
            if (mimeType.getPrimaryType().equals(currentType.getPrimary())) {
               return true;
            }
         }
      }

      return false;
   }

   public int getSequence() {
      return sequence;
   }

   public void setSequence(int sequence) {
      this.sequence = sequence;
      newSequence = null;
   }

   public int getNewSequence() {
      if (newSequence == null) {
         return sequence;
      }
      return newSequence.intValue();
   }

   public void setNewSequence(int newSequence) {
      this.newSequence = new Integer(newSequence);
   }
}

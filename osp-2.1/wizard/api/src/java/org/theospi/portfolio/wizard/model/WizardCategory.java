/**********************************************************************************
* $URL$
* $Id$
***********************************************************************************
*
* Copyright (c) 2003, 2004 The Regents of the University of Michigan, Trustees of Indiana University,
*                  Board of Trustees of the Leland Stanford, Jr., University, and The MIT Corporation
*
* Licensed under the Educational Community License Version 1.0 (the "License");
* By obtaining, using and/or copying this Original Work, you agree that you have read,
* understand, and will comply with the terms and conditions of the Educational Community License.
* You may obtain a copy of the License at:
*
*      http://cvs.sakaiproject.org/licenses/license_1_0.html
*
* THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
* INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE
* AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
* DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
* FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*
**********************************************************************************/
package org.theospi.portfolio.wizard.model;

import org.sakaiproject.metaobj.shared.model.IdentifiableObject;

import java.util.Date;
import java.util.List;
import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Jan 13, 2006
 * Time: 10:18:13 AM
 * To change this template use File | Settings | File Templates.
 */
public class WizardCategory extends IdentifiableObject {

   private String title;
   private String description;
   private String keywords;
   private Date created;
   private Date modified;
   private Wizard wizard;
   private WizardCategory parentCategory;
   private int sequence = 0;

   private List childCategories;
   private List childPages;

   public WizardCategory() {
   }

   public WizardCategory(Wizard wizard) {
      this.wizard = wizard;
      setChildCategories(new ArrayList());
      setChildPages(new ArrayList());
      setCreated(new Date());
      setModified(new Date());
   }

   public String getTitle() {
      return title;
   }

   public void setTitle(String title) {
      this.title = title;
   }

   public String getDescription() {
      return description;
   }

   public void setDescription(String description) {
      this.description = description;
   }

   public String getKeywords() {
      return keywords;
   }

   public void setKeywords(String keywords) {
      this.keywords = keywords;
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

   public List getChildCategories() {
      return childCategories;
   }

   public void setChildCategories(List childCategories) {
      this.childCategories = childCategories;
   }

   public List getChildPages() {
      return childPages;
   }

   public void setChildPages(List childPages) {
      this.childPages = childPages;
   }

   public Wizard getWizard() {
      return wizard;
   }

   public void setWizard(Wizard wizard) {
      this.wizard = wizard;
   }

   public WizardCategory getParentCategory() {
      return parentCategory;
   }

   public void setParentCategory(WizardCategory parentCategory) {
      this.parentCategory = parentCategory;
   }

   public int getSequence() {
      return sequence;
   }

   public void setSequence(int sequence) {
      this.sequence = sequence;
   }

   public boolean equals(Object in) {
      if (in == null && this == null) return true;
      if (in == null && this != null) return false;
      if (this == null && in != null) return false;
      if (!this.getClass().isAssignableFrom(in.getClass())) return false;

      if (this.getId() == null && ((IdentifiableObject) in).getId() == null ) {
         return this.getTitle().equals(((WizardCategory)in).getTitle());
      }

      if (this.getId() == null || ((IdentifiableObject) in).getId() == null ) return false;
      return this.getId().equals(((IdentifiableObject) in).getId());
   }

}

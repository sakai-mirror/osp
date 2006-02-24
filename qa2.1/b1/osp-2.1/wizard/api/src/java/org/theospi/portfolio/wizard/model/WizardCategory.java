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

}

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
package org.theospi.portfolio.wizard.tool;

import java.util.List;

import org.theospi.portfolio.review.model.Review;
import org.theospi.portfolio.wizard.model.CompletedWizard;
import org.sakaiproject.metaobj.shared.model.Agent;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Jan 23, 2006
 * Time: 5:51:07 PM
 * To change this template use File | Settings | File Templates.
 */
public class DecoratedCompletedWizard {

   private WizardTool parent;
   private DecoratedWizard wizard;
   private CompletedWizard base;
   private DecoratedCompletedCategory rootCategory;
   
   private List reflections = null;
   private List evaluations = null;
   private List reviews = null;
   

   public DecoratedCompletedWizard() {
   }

   public DecoratedCompletedWizard(WizardTool parent, DecoratedWizard wizard, CompletedWizard base) {
      this.parent = parent;
      this.wizard = wizard;
      this.base = base;
      setRootCategory(new DecoratedCompletedCategory(
            parent, wizard.getRootCategory(), base.getRootCategory()));
   }

   public WizardTool getParent() {
      return parent;
   }

   public void setParent(WizardTool parent) {
      this.parent = parent;
   }

   public DecoratedWizard getWizard() {
      return wizard;
   }

   public void setWizard(DecoratedWizard wizard) {
      this.wizard = wizard;
   }

   public CompletedWizard getBase() {
      return base;
   }

   public void setBase(CompletedWizard base) {
      this.base = base;
   }

   public DecoratedCompletedCategory getRootCategory() {
      return rootCategory;
   }

   public void setRootCategory(DecoratedCompletedCategory rootCategory) {
      this.rootCategory = rootCategory;
   }

   public String processSubmitWizard() {
      getParent().processSubmitWizard(getBase());
      return "submitted";
   }
   
   public List getReflections() {
      if(reflections == null)
         reflections = getParent().getReviewManager().getReviewsByParentAndType(
            getBase().getId().getValue(), Review.REFLECTION_TYPE, getBase().getWizard().getSiteId(),
            getParent().getWizardManager().getWizardEntityProducer());
      return reflections;
   }
   public List getEvaluations() {
      if(evaluations == null)
         evaluations = getParent().getReviewManager().getReviewsByParentAndType(
            getBase().getId().getValue(), Review.EVALUATION_TYPE, getBase().getWizard().getSiteId(),
            getParent().getWizardManager().getWizardEntityProducer());
      return evaluations;
   }
   public List getReviews() {
      if(reviews == null)
         reviews = getParent().getReviewManager().getReviewsByParentAndType(
            getBase().getId().getValue(), Review.REVIEW_TYPE, getBase().getWizard().getSiteId(),
            getParent().getWizardManager().getWizardEntityProducer());
      return reviews;
   }
   public boolean getIsReadOnly() {
      Agent completedWizardAgent = getBase().getOwner();
      Agent currentAgent = getParent().getAuthManager().getAgent();
      return !completedWizardAgent.equals(currentAgent);
   }

}

/**********************************************************************************
* $URL:https://source.sakaiproject.org/svn/osp/trunk/matrix/tool/src/java/org/theospi/portfolio/matrix/control/EditScaffoldingConfirmationController.java $
* $Id:EditScaffoldingConfirmationController.java 9134 2006-05-08 20:28:42Z chmaurer@iupui.edu $
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
package org.theospi.portfolio.matrix.control;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.metaobj.shared.model.Id;
import org.sakaiproject.metaobj.utils.mvc.intf.Controller;
import org.sakaiproject.metaobj.utils.mvc.intf.FormController;
import org.springframework.validation.Errors;
import org.springframework.web.servlet.ModelAndView;
import org.theospi.portfolio.matrix.model.Cell;
import org.theospi.portfolio.matrix.model.Matrix;
import org.theospi.portfolio.matrix.model.Scaffolding;
import org.theospi.portfolio.matrix.model.ScaffoldingCell;
import org.theospi.portfolio.matrix.model.WizardPage;
import org.theospi.portfolio.matrix.model.impl.MatrixContentEntityProducer;
import org.theospi.portfolio.review.mgt.ReviewManager;
import org.theospi.portfolio.review.model.Review;


public class EditScaffoldingConfirmationController extends BaseScaffoldingController
implements Controller, FormController {

   protected final Log logger = LogFactory.getLog(getClass());
   private ReviewManager reviewManager;

   public ModelAndView handleRequest(Object requestModel, Map request, Map session, Map application, Errors errors) {
      String viewName = "success";
      
      EditedScaffoldingStorage sessionBean = (EditedScaffoldingStorage)session.get(
            EditedScaffoldingStorage.EDITED_SCAFFOLDING_STORAGE_SESSION_KEY);
      Scaffolding scaffolding = sessionBean.getScaffolding();
      
      Id id = scaffolding.getId();
      
      Map model = new HashMap();
      model.put("scaffolding_id", id);
      
      String cancel = (String)request.get("cancel");
      String next = (String)request.get("continue");
      if (cancel != null) {
         viewName = "cancel";
      }
      else if (next != null) {
         saveScaffolding(usedCellDefaultSettingAdjustment(scaffolding));
      }
      
      session.remove(EditedScaffoldingStorage.STORED_SCAFFOLDING_FLAG);
      session.remove(EditedScaffoldingStorage.EDITED_SCAFFOLDING_STORAGE_SESSION_KEY);
      
      return new ModelAndView(viewName, model);
   }
   
   public Map referenceData(Map request, Object command, Errors errors) {
      Map model = new HashMap();
      model.put("label", "Scaffolding");
      model.put("isInSession", EditedScaffoldingStorage.STORED_SCAFFOLDING_FLAG);
      return model;
   }
   
   /**
    * If any default forms in the matrix has changed and scaffolding is published, then
    * this function loops through each cell in a matrix and sets used default to false for
    * each particular instance (reflection, custom, feedback, evaluation) and sets the old default
    * form to the cell's form, only if the useDefault flag is set to true
    * 
    * 
    * Once a student submits information to the cell, the form cannot be changed. If the program administrator is using the
	* default forms for cell A and a student submits information to cell A and then the instructor changes the default form, ensure
	* the setting at the cell level to use default settings is no longer selected.
    * 
    * @param scaffolding
    * @return
    */
   
   private Scaffolding usedCellDefaultSettingAdjustment(Scaffolding scaffolding) {
	   if(scaffolding.isPublished()){
		   Id scaffoldingId = scaffolding.getId();
		   

		   Scaffolding dbScaffolding = getMatrixManager().getScaffolding(scaffoldingId);

		   if(dbScaffolding != null){
			   boolean reflectChange = false;
				boolean customChange = false;
				boolean feedbackChange = false;
				boolean evalChange = false;

				// find out what default forms have been changed:
				if ((dbScaffolding.getReflectionDevice() == null && scaffolding
						.getReflectionDevice() != null)
						|| (dbScaffolding.getReflectionDevice() != null && scaffolding
								.getReflectionDevice() == null)
						|| (dbScaffolding.getReflectionDevice() != null
								&& scaffolding.getReflectionDevice() != null && !dbScaffolding
								.getReflectionDevice().equals(
										scaffolding.getReflectionDevice()))) {
					reflectChange = true;
				}

				if ((dbScaffolding.getAdditionalForms() != null && scaffolding
						.getAdditionalForms() == null)
						|| (dbScaffolding.getAdditionalForms() == null && scaffolding
								.getAdditionalForms() != null)
						|| (dbScaffolding.getAdditionalForms() != null
								&& scaffolding.getAdditionalForms() != null && !dbScaffolding
								.getAdditionalForms().equals(
										scaffolding.getAdditionalForms()))) {
					customChange = true;
				}

				if ((dbScaffolding.getReviewDevice() != null && scaffolding
						.getReviewDevice() == null)
						|| (dbScaffolding.getReviewDevice() == null && scaffolding
								.getReviewDevice() != null)
						|| (dbScaffolding.getReviewDevice() != null
								&& scaffolding.getReviewDevice() != null && !dbScaffolding
								.getReviewDevice().equals(
										scaffolding.getReviewDevice()))) {
					feedbackChange = true;
				}

				if ((dbScaffolding.getEvaluationDevice() == null && scaffolding
						.getEvaluationDevice() != null)
						|| (dbScaffolding.getEvaluationDevice() != null && scaffolding
								.getEvaluationDevice() == null)
						|| (dbScaffolding.getEvaluationDevice() != null
								&& scaffolding.getEvaluationDevice() != null && !dbScaffolding
								.getEvaluationDevice().equals(
										scaffolding.getEvaluationDevice()))) {
					evalChange = true;
				}



			   //only iterate through the matrix if there is a default form change:
			   if(reflectChange || customChange || feedbackChange || evalChange){
				   
				   Set<ScaffoldingCell> scaffoldIngCells = getMatrixManager().getScaffoldingCells(scaffoldingId);
				   Set<ScaffoldingCell> newScaffoldingCells = new HashSet<ScaffoldingCell>();
				   
				   for(Iterator<ScaffoldingCell> scaffCellsIt = scaffoldIngCells.iterator(); scaffCellsIt.hasNext();){
					   ScaffoldingCell sCell = scaffCellsIt.next();
					   List cells = getMatrixManager().getCellsByScaffoldingCell(sCell.getId());
					   
					   for (Iterator cellIt = cells.iterator(); cellIt.hasNext();) {
						   Cell cell = (Cell) cellIt.next();
						   WizardPage wizardPage = cell.getWizardPage();
						   
						   //inside any of these if's, change the default value to false and copy over the old default form
						   //over to the cell's form list
						   if (sCell.getWizardPageDefinition().isDefaultReflectionForm() && 
								   reflectChange){
							   List reflections = getReviewManager().getReviewsByParentAndType(
									   wizardPage.getId().getValue(), Review.REFLECTION_TYPE, wizardPage.getPageDefinition().getSiteId(), MatrixContentEntityProducer.MATRIX_PRODUCER);
							   if(reflections != null	&& reflections.size() > 0){
								   sCell.getWizardPageDefinition().setDefaultReflectionForm(false);
								   sCell.getWizardPageDefinition().setReflectionDevice(dbScaffolding.getReflectionDevice());
							   }
						   }
						   if (sCell.getWizardPageDefinition().isDefaultCustomForm() &&
								   customChange){
							   Set pageForms = getMatrixManager().getPageForms(wizardPage);
							   if(pageForms != null && pageForms.size() > 0){
								   //checkForMissingForms returns true if the ce

								   sCell.getWizardPageDefinition().setDefaultCustomForm(false);
								   sCell.getWizardPageDefinition().getAdditionalForms().clear();
								   sCell.getWizardPageDefinition().getAdditionalForms().addAll(dbScaffolding.getAdditionalForms());

							   }
						   }
						   if (sCell.getWizardPageDefinition().isDefaultFeedbackForm() &&
								   feedbackChange){
							   List feedbacks = getReviewManager().getReviewsByParentAndType(
									   wizardPage.getId().getValue(), Review.FEEDBACK_TYPE, wizardPage.getPageDefinition().getSiteId(), MatrixContentEntityProducer.MATRIX_PRODUCER);
							   if(feedbacks != null	&& feedbacks.size() > 0){
								   sCell.getWizardPageDefinition().setDefaultFeedbackForm(false);
								   sCell.getWizardPageDefinition().setReviewDevice(dbScaffolding.getReviewDevice());
							   }
						   }
						   if (sCell.getWizardPageDefinition().isDefaultEvaluationForm() &&
								   evalChange){
							   List evals = getReviewManager().getReviewsByParentAndType(
									   wizardPage.getId().getValue(), Review.EVALUATION_TYPE, wizardPage.getPageDefinition().getSiteId(), MatrixContentEntityProducer.MATRIX_PRODUCER);
							   if(evals != null	&& evals.size() > 0){
								   sCell.getWizardPageDefinition().setDefaultEvaluationForm(false);
								   sCell.getWizardPageDefinition().setEvaluationDevice(dbScaffolding.getEvaluationDevice());
							   }
						   }
						   

					   }
					   newScaffoldingCells.add(sCell);
				   }
				   
				   scaffolding.setScaffoldingCells(newScaffoldingCells);
			   }
		   }
	   }
	   
	   return scaffolding;
   }
   
public ReviewManager getReviewManager() {
	return reviewManager;
}

public void setReviewManager(ReviewManager reviewManager) {
	this.reviewManager = reviewManager;
}
   
}

/**********************************************************************************
* $URL$
* $Id$
***********************************************************************************
*
* Copyright (c) 2006 The Sakai Foundation.
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

import org.springframework.validation.Errors;
import org.springframework.web.servlet.ModelAndView;
import org.theospi.portfolio.matrix.WizardPageHelper;
import org.theospi.portfolio.matrix.model.WizardPage;

import java.util.Map;
import java.util.List;

/**
 * The steps are referenced from 1 to n.  this way we can render the step number to the interface correctly
 * 
 * User: John Ellis
 * Date: Feb 3, 2006
 * Time: 1:51:18 PM
 * To change this template use File | Settings | File Templates.
 */
public class SequentialWizardPageController extends WizardPageController {

   private static final String TOTAL_STEPS =
      "org.theospi.portfolio.matrix.control.SequentialWizardPageController.totalSteps";

   /* (non-Javadoc)
    * @see org.theospi.utils.mvc.intf.FormController#referenceData(java.util.Map, java.lang.Object, org.springframework.validation.Errors)
    */
   public Map referenceData(Map request, Object command, Errors errors) {
      Map model = super.referenceData(request, command, errors);
      if (request.get(TOTAL_STEPS) != null) {
         model.put("sequential", "true");
         model.put("currentStep", request.get(WizardPageHelper.SEQUENTIAL_WIZARD_CURRENT_STEP));
         model.put("totalSteps", request.get(TOTAL_STEPS));
      }
      return model;
   }

   public Object fillBackingObject(Object incomingModel, Map request,
                                   Map session, Map application) throws Exception {
      // get the step and get the appropriate page
      List steps = (List) session.get(WizardPageHelper.SEQUENTIAL_WIZARD_PAGES);

      if (steps != null) {
         int currentStep = getCurrentStep(session);
         request.put(TOTAL_STEPS, new Integer(steps.size()));
         if(currentStep == 0)
            currentStep = 1;
         WizardPage page = (WizardPage) steps.get(currentStep - 1);

         request.put(WizardPageHelper.SEQUENTIAL_WIZARD_CURRENT_STEP, new Integer(currentStep));

         session.put(WizardPageHelper.WIZARD_PAGE, page);
      }
      return super.fillBackingObject(incomingModel, request, session, application);
   }

   protected int getCurrentStep(Map session) {
      int currentStep = 0;
      if (session.get(WizardPageHelper.SEQUENTIAL_WIZARD_CURRENT_STEP) != null) {
         currentStep = ((Integer)session.get(WizardPageHelper.SEQUENTIAL_WIZARD_CURRENT_STEP)).intValue();
      }
      return currentStep;
   }

   public ModelAndView handleRequest(Object requestModel, Map request, Map session,
                                     Map application, Errors errors) {
      session.put(WizardPageHelper.SEQUENTIAL_WIZARD_CURRENT_STEP, getNextStep(request, session));

      return super.handleRequest(requestModel, request,
         session, application, errors);
   }

   protected Integer getNextStep(Map request, Map session) {
      int currentStep = getCurrentStep(session);

      if (request.get("_next") != null) {
         currentStep++;
      }
      else if (request.get("_back") != null) {
         currentStep--;
      }

      return new Integer(currentStep);
   }

}

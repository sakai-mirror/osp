
/**********************************************************************************
* $URL: $
* $Id: $
***********************************************************************************
*
* Copyright (c) 2008 The Sakai Foundation.
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
package org.theospi.portfolio.shared.model;

/**
 * @author chmaurer
 */
public interface WizardMatrixConstants {

	// Dependent on ordering in editWizard.jsp && addScaffolding.jsp
   public static final int FEEDBACK_OPTION_OPEN    = 0;
   public static final int FEEDBACK_OPTION_SINGLE  = 1;
   public static final int FEEDBACK_OPTION_NONE    = 2;
   
	// Dependent on ordering of <c:forTokens> in addScaffolding.jsp
   public static final int NORMAL_GROUP_ACCESS = 0;
   public static final int UNRESTRICTED_GROUP_ACCESS = 1;

}


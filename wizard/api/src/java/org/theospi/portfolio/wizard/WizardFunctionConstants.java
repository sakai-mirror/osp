
/**********************************************************************************
* $URL: https://source.sakaiproject.org/svn/trunk/sakai/legacy/component/src/java/org/sakaiproject/component/framework/email/TestEmailService.java $
* $Id: TestEmailService.java 3831 2005-11-14 20:17:24Z ggolden@umich.edu $
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
package org.theospi.portfolio.wizard;

/**
 * @author chmaurer
 */
public interface WizardFunctionConstants {
   
   public final static String COMMENT_TYPE = "comment";
   public final static String REFLECTION_TYPE = "reflection";
   public final static String EVALUATION_TYPE = "evaluation";
  
   public final static String WIZARD_PREFIX = "osp.wizard.";
   public final static String CREATE_WIZARD = WIZARD_PREFIX + "create";
   public final static String EDIT_WIZARD = WIZARD_PREFIX + "edit";
   public final static String DELETE_WIZARD = WIZARD_PREFIX + "delete";
   public final static String PUBLISH_WIZARD = WIZARD_PREFIX + "publish";
   public static final String REVIEW_WIZARD = WIZARD_PREFIX + "review";
   public static final String EVALUATE_WIZARD = WIZARD_PREFIX + "evaluate";
   public static final String VIEW_WIZARD = WIZARD_PREFIX + "view";
   public static final String COPY_WIZARD = WIZARD_PREFIX + "copy";
   public static final String EXPORT_WIZARD = WIZARD_PREFIX + "export";
}



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
package org.theospi.portfolio.matrix;

/**
 * @author chmaurer
 */
public interface MatrixFunctionConstants {
  
   public final static String READY_STATUS = "READY";
   public final static String PENDING_STATUS = "PENDING";
   public final static String COMPLETE_STATUS = "COMPLETE";
   public final static String LOCKED_STATUS = "LOCKED";
   public final static String WAITING_STATUS = "WAITING";
   public final static String CHECKED_OUT_STATUS = "CHECKED_OUT";

   public final static String SCAFFOLDING_PREFIX = "osp.matrix.scaffolding.";
   public final static String CREATE_SCAFFOLDING = SCAFFOLDING_PREFIX + "create";
   public final static String EDIT_SCAFFOLDING = SCAFFOLDING_PREFIX + "edit";
   //public static final String VIEW_MATRIX_USERS = SCAFFOLDING_PREFIX + "viewUsers";
   public final static String PUBLISH_SCAFFOLDING = SCAFFOLDING_PREFIX + "publish";
   public final static String DELETE_SCAFFOLDING = SCAFFOLDING_PREFIX + "delete";
   public final static String EXPORT_SCAFFOLDING = SCAFFOLDING_PREFIX + "export";
   public final static String USE_SCAFFOLDING = SCAFFOLDING_PREFIX + "use";
   public static final String VIEW_SCAFFOLDING_GUIDANCE = SCAFFOLDING_PREFIX + "viewScaffGuidance";
   public static final String EDIT_SCAFFOLDING_GUIDANCE = SCAFFOLDING_PREFIX + "editScaffGuidance";
      
   public final static String MATRIX_PREFIX = "osp.matrix.";
   public static final String REVIEW_MATRIX = MATRIX_PREFIX + "review";
   public static final String EVALUATE_MATRIX = MATRIX_PREFIX + "evaluate";
   public static final String VIEW_OWNER_MATRIX = MATRIX_PREFIX + "viewOwner";
   
   public static final String EVALUATE_SPECIFIC_MATRIXCELL = MATRIX_PREFIX + "evaluateSpecificMatrix";
   
}

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

import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.metaobj.shared.model.Id;
import org.springframework.validation.Errors;
import org.springframework.web.servlet.ModelAndView;
import org.theospi.portfolio.matrix.MatrixFunctionConstants;
import org.theospi.portfolio.matrix.model.Scaffolding;

public class DeleteScaffoldingController extends ListScaffoldingController {

   protected final Log logger = LogFactory.getLog(getClass());

   public ModelAndView handleRequest(Object requestModel, Map request, Map session, Map application, Errors errors) {
      Id id = getIdManager().getId((String)request.get("scaffolding_id"));
      Scaffolding scaffolding = getMatrixManager().getScaffolding(id); 
      getAuthzManager().checkPermission(MatrixFunctionConstants.DELETE_SCAFFOLDING, id);
      
      if (scaffolding.getExposedPageId() != null && !scaffolding.getExposedPageId().equals("")) {
         getMatrixManager().removeExposedMatrixTool(scaffolding);
      }
      
      getMatrixManager().deleteScaffolding(id);      
      
      return super.handleRequest(requestModel, request, session, application, errors);
   }
  
}

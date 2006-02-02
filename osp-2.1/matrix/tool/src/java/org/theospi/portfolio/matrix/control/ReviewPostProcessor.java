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
package org.theospi.portfolio.matrix.control;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.sakaiproject.api.kernel.component.cover.ComponentManager;
import org.sakaiproject.metaobj.shared.mgt.IdManager;
import org.sakaiproject.metaobj.shared.model.Id;
import org.sakaiproject.metaobj.utils.mvc.intf.Controller;
import org.springframework.validation.Errors;
import org.springframework.web.servlet.ModelAndView;
import org.theospi.portfolio.shared.mgt.WorkflowEnabledManager;

public class ReviewPostProcessor  implements Controller {
   
   //private MatrixManager matrixManager;
   private IdManager idManager = null;
   //private WorkflowEnabledManager theManager;

   public ModelAndView handleRequest(Object requestModel, Map request, Map session, Map application, Errors errors) {
      Id id = idManager.getId((String)request.get("workflowId"));
      Id objId = idManager.getId((String)request.get("objId"));
      String manager = (String)request.get("manager");
      getWorkflowEnabledManager(manager).processWorkflow(id, objId);
      return new ModelAndView("success", "page_id", objId);
   }

   public Map referenceData(Map request, Object command, Errors errors) {
      Map model = new HashMap();
      List workflows = (List)request.get("workflows");
      model.put("workflows", workflows);
      model.put("obj_id", request.get("obj_id"));
      model.put("manager", request.get("manager"));
      return model;
   }

   /**
    * @return Returns the idManager.
    */
   public IdManager getIdManager() {
      return idManager;
   }

   /**
    * @param idManager The idManager to set.
    */
   public void setIdManager(IdManager idManager) {
      this.idManager = idManager;
   }

   
         
   protected WorkflowEnabledManager getWorkflowEnabledManager(String name) {
      return (WorkflowEnabledManager)ComponentManager.get(name);
   }  

}

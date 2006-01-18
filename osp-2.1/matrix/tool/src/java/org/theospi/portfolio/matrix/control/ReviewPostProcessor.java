package org.theospi.portfolio.matrix.control;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.sakaiproject.metaobj.shared.mgt.IdManager;
import org.sakaiproject.metaobj.shared.model.Id;
import org.sakaiproject.metaobj.utils.mvc.intf.Controller;
import org.springframework.validation.Errors;
import org.springframework.web.servlet.ModelAndView;
import org.theospi.portfolio.matrix.MatrixManager;

public class ReviewPostProcessor  implements Controller {
   
   private MatrixManager matrixManager;
   private IdManager idManager = null;

   public ModelAndView handleRequest(Object requestModel, Map request, Map session, Map application, Errors errors) {
      Id id = idManager.getId((String)request.get("workflowId"));
      Id cellId = idManager.getId((String)request.get("cellId"));
      getMatrixManager().processWorkflow(id, cellId);
      return new ModelAndView("success", "cell_id", cellId);
   }

   public Map referenceData(Map request, Object command, Errors errors) {
      Map model = new HashMap();
      List workflows = (List)request.get("workflows");
      model.put("workflows", workflows);
      model.put("cell_id", request.get("cell_id"));
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

   /**
    * @return Returns the matrixManager.
    */
   public MatrixManager getMatrixManager() {
      return matrixManager;
   }

   /**
    * @param matrixManager The matrixManager to set.
    */
   public void setMatrixManager(MatrixManager matrixManager) {
      this.matrixManager = matrixManager;
   }

}

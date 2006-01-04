package org.theospi.portfolio.matrix.control;

import java.util.Map;

import org.sakaiproject.metaobj.shared.mgt.IdManager;
import org.sakaiproject.metaobj.shared.model.Id;
import org.sakaiproject.metaobj.utils.mvc.intf.Controller;
import org.springframework.validation.Errors;
import org.springframework.web.servlet.ModelAndView;
import org.theospi.portfolio.matrix.MatrixManager;
import org.theospi.portfolio.matrix.model.Cell;
import org.theospi.portfolio.review.ReviewHelper;

public class ReviewHelperController implements Controller {
   
   private MatrixManager matrixManager;
   private IdManager idManager = null;

   public ModelAndView handleRequest(Object requestModel, Map request, Map session, Map application, Errors errors) {
      String strId = (String) request.get("cell_id");
      if (strId== null) {
         strId = (String)session.get("cell_id");
         session.remove("cell_id");
         return new ModelAndView("return", "cell_id", strId);
      }
      
      Id id = getIdManager().getId(strId);
      Cell cell = matrixManager.getCell(id);
      
      session.put(ReviewHelper.REVIEW_FORM_TYPE, 
            cell.getScaffoldingCell().getReviewDevice().getValue());
      session.put(ReviewHelper.REVIEW_PARENT, 
            cell.getId().getValue());
      session.put("cell_id", cell.getId().getValue());
      return new ModelAndView("success");
      
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

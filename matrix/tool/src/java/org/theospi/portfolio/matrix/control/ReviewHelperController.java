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
import org.theospi.portfolio.review.model.Review;

public class ReviewHelperController implements Controller {
   
   private MatrixManager matrixManager;
   private IdManager idManager = null;

   public ModelAndView handleRequest(Object requestModel, Map request, Map session, Map application, Errors errors) {
      String strId = (String) request.get("cell_id");
      if (strId== null) {
         strId = (String)session.get("cell_id");
         session.remove("cell_id");
         session.remove(ReviewHelper.REVIEW_TYPE);
         session.remove(ReviewHelper.REVIEW_FORM_TYPE);
         session.remove(ReviewHelper.REVIEW_PARENT);
         session.remove(ReviewHelper.REVIEW_BUNDLE_PREFIX);
         session.remove(ReviewHelper.REVIEW_TITLE);
         session.remove(ReviewHelper.REVIEW_INSTRUCTIONS);
         session.remove(ReviewHelper.REVIEW_MANAGE_CONTENT);
         session.remove(ReviewHelper.REVIEW_FORM_LABEL);
         session.remove(ReviewHelper.REVIEW_VISIBILITY_LABEL);
         return new ModelAndView("return", "cell_id", strId);
      }
      
      Id id = getIdManager().getId(strId);
      Cell cell = matrixManager.getCell(id);
      
      session.put(ReviewHelper.REVIEW_FORM_TYPE, 
            cell.getScaffoldingCell().getReviewDevice().getValue());
      session.put(ReviewHelper.REVIEW_PARENT, 
            cell.getId().getValue());
      
      String type = (String)request.get("org_theospi_portfolio_review_type");
      int intType = Integer.parseInt(type);
      
      String bundlePrefix = ""; 
      switch (intType) {
         case Review.REVIEW_TYPE:
            bundlePrefix = "review_";
            break;
         case Review.EVALUATION_TYPE:
            bundlePrefix = "eval_";
            break;
         case Review.REFLECTION_TYPE:
            bundlePrefix = "reflection_";
            break;
      }
      
      
      session.put(ReviewHelper.REVIEW_TYPE, type);
      session.put(ReviewHelper.REVIEW_BUNDLE_PREFIX, bundlePrefix);
      //session.put(ReviewHelper.REVIEW_TITLE, request.get("org_theospi_portfolio_review_title"));
      //session.put(ReviewHelper.REVIEW_TITLE, bundlePrefix+"section_title");
      //session.put(ReviewHelper.REVIEW_INSTRUCTIONS, request.get("org_theospi_portfolio_review_instructions"));
      //session.put(ReviewHelper.REVIEW_INSTRUCTIONS, bundlePrefix+"instruction_message");
      //session.put(ReviewHelper.REVIEW_MANAGE_CONTENT, request.get("org_theospi_portfolio_review_manage_content"));
      //session.put(ReviewHelper.REVIEW_MANAGE_CONTENT, bundlePrefix+"manage_content");
      //    session.put(ReviewHelper.REVIEW_FORM_LABEL, request.get("org_theospi_portfolio_review_form_label"));
      //session.put(ReviewHelper.REVIEW_FORM_LABEL, bundlePrefix+"form_label");
//    session.put(ReviewHelper.REVIEW_VISIBILITY_LABEL, request.get("org_theospi_portfolio_review_visibility_label"));
      //session.put(ReviewHelper.REVIEW_VISIBILITY_LABEL, bundlePrefix+"visibility_label");
      
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

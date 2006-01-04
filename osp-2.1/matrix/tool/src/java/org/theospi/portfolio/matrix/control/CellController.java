/*
 * The Open Source Portfolio Initiative Software is Licensed under the Educational Community License Version 1.0:
 *
 * This Educational Community License (the "License") applies to any original work of authorship
 * (the "Original Work") whose owner (the "Licensor") has placed the following notice immediately
 * following the copyright notice for the Original Work:
 *
 * Copyright (c) 2004 Trustees of Indiana University and r-smart Corporation
 *
 * This Original Work, including software, source code, documents, or other related items, is being
 * provided by the copyright holder(s) subject to the terms of the Educational Community License.
 * By obtaining, using and/or copying this Original Work, you agree that you have read, understand,
 * and will comply with the following terms and conditions of the Educational Community License:
 *
 * Permission to use, copy, modify, merge, publish, distribute, and sublicense this Original Work and
 * its documentation, with or without modification, for any purpose, and without fee or royalty to the
 * copyright holder(s) is hereby granted, provided that you include the following on ALL copies of the
 * Original Work or portions thereof, including modifications or derivatives, that you make:
 *
 * - The full text of the Educational Community License in a location viewable to users of the
 * redistributed or derivative work.
 *
 * - Any pre-existing intellectual property disclaimers, notices, or terms and conditions.
 *
 * - Notice of any changes or modifications to the Original Work, including the date the changes were made.
 *
 * - Any modifications of the Original Work must be distributed in such a manner as to avoid any confusion
 *  with the Original Work of the copyright holders.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
 * SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 * The name and trademarks of copyright holder(s) may NOT be used in advertising or publicity pertaining
 * to the Original or Derivative Works without specific, written prior permission. Title to copyright
 * in the Original Work and any associated documentation will at all times remain with the copyright holders.
 *
 * $Header: /opt/CVS/osp2.x/matrix/tool/src/java/org/theospi/portfolio/matrix/control/CellController.java,v 1.3 2005/08/10 21:05:09 chmaurer Exp $
 * $Revision$
 * $Date$
 */
package org.theospi.portfolio.matrix.control;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.api.kernel.session.cover.SessionManager;
import org.sakaiproject.metaobj.security.AuthenticationManager;
import org.sakaiproject.metaobj.shared.mgt.IdManager;
import org.sakaiproject.metaobj.shared.model.Id;
import org.sakaiproject.metaobj.utils.mvc.intf.FormController;
import org.sakaiproject.metaobj.utils.mvc.intf.LoadObjectController;
import org.springframework.validation.Errors;
import org.springframework.web.servlet.ModelAndView;
import org.theospi.portfolio.guidance.mgt.GuidanceManager;
import org.theospi.portfolio.matrix.MatrixManager;
import org.theospi.portfolio.matrix.model.Cell;
import org.theospi.portfolio.review.ReviewHelper;
import org.theospi.portfolio.review.mgt.ReviewManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CellController implements FormController, LoadObjectController {

   protected final Log logger = LogFactory.getLog(getClass());
   private MatrixManager matrixManager;
   private AuthenticationManager authManager = null;
   private IdManager idManager = null;
   private ReviewManager reviewManager;


   public Map referenceData(Map request, Object command, Errors errors) {
      CellFormBean cell = (CellFormBean) command;
      Map model = new HashMap();
      model.put("reviewRubrics", matrixManager.getReviewRubrics());
      String cellId = cell.getCell().getId().getValue();
      model.put("reviews", getReviewManager().getReviewsByParent(cellId));
      model.put("currentUser", SessionManager.getCurrentSessionUserId());
      return model;
   }
   
   public Object fillBackingObject(Object incomingModel, Map request, Map session, Map application) throws Exception {
      CellFormBean cellBean = (CellFormBean) incomingModel;
      String strId = (String) request.get("cell_id");
      if (strId== null) {
         strId = (String)session.get("cell_id");
         session.remove("cell_id");
      }
      Id id = getIdManager().getId(strId);
      Cell cell = matrixManager.getCell(id);

      cellBean.setCell(cell);

      List nodeList = new ArrayList(matrixManager.getCellContents(cell));
      cellBean.setNodes(nodeList);
      
      return cellBean;
   }

   private String ListToString(String[] strArray) {
      String result = "";
      if (strArray != null) {
         for (int i=0; i<strArray.length; i++) {
            if (i == 0)
               result = strArray[i];
            else
               result = result.concat(",").concat(strArray[i]);
         }
      }
      return result;
   }

   public ModelAndView handleRequest(Object requestModel, Map request, Map session, Map application, Errors errors) {
      CellFormBean cellBean = (CellFormBean) requestModel;
      Cell cell = cellBean.getCell();

      String action = (String)request.get("action");
      
      if (action != null) {
         if (action.equals("Update") && cellBean.getSelectedArtifacts() != null) {
            Map map = new HashMap();
            map.put("cell_id", cell.getId());
            map.put("selectedArtifacts", ListToString(cellBean.getSelectedArtifacts()));
            map.put("cellBean", cellBean);
            return new ModelAndView("confirm", map); 
         }
         else if (action.equals("Cancel")) {
            return new ModelAndView("cancel");
         }
         else if (action.equals("guidance")) {
            session.put(GuidanceManager.CURRENT_GUIDANCE_ID, 
                  cell.getScaffoldingCell().getGuidance().getId().getValue());
            session.put("cell_id", cell.getId().getValue());
            return new ModelAndView("guidance");
         }
         else if (action.equals("review")) {
            session.put(ReviewHelper.REVIEW_FORM_TYPE, 
                  cell.getScaffoldingCell().getReviewDevice().getValue());
            session.put(ReviewHelper.REVIEW_PARENT, 
                  cell.getId().getValue());
            session.put("cell_id", cell.getId().getValue());
            return new ModelAndView("review");
         }
      }

      return new ModelAndView("success", "cellBean", cellBean);
   }
   
   /**
    * @return
    */
   public AuthenticationManager getAuthManager() {
      return authManager;
   }

   /**
    * @param manager
    */
   public void setAuthManager(AuthenticationManager manager) {
      authManager = manager;
   }

   /**
    * @return
    */
   public IdManager getIdManager() {
      return idManager;
   }

   /**
    * @param manager
    */
   public void setIdManager(IdManager manager) {
      idManager = manager;
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

   /**
    * @return Returns the reviewManager.
    */
   public ReviewManager getReviewManager() {
      return reviewManager;
   }

   /**
    * @param reviewManager The reviewManager to set.
    */
   public void setReviewManager(ReviewManager reviewManager) {
      this.reviewManager = reviewManager;
   }
}

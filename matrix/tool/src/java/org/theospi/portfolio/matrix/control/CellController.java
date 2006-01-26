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
import org.sakaiproject.metaobj.shared.mgt.StructuredArtifactDefinitionManager;
import org.sakaiproject.metaobj.shared.model.Id;
import org.sakaiproject.metaobj.shared.model.StructuredArtifactDefinitionBean;
import org.sakaiproject.metaobj.utils.mvc.intf.FormController;
import org.sakaiproject.metaobj.utils.mvc.intf.LoadObjectController;
import org.springframework.validation.Errors;
import org.springframework.web.servlet.ModelAndView;
import org.theospi.portfolio.guidance.mgt.GuidanceManager;
import org.theospi.portfolio.matrix.MatrixManager;
import org.theospi.portfolio.matrix.WizardPageHelper;
import org.theospi.portfolio.matrix.model.Cell;
import org.theospi.portfolio.review.mgt.ReviewManager;
import org.theospi.portfolio.review.model.Review;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class CellController implements FormController, LoadObjectController {

   protected final Log logger = LogFactory.getLog(getClass());
   private MatrixManager matrixManager;
   private AuthenticationManager authManager = null;
   private IdManager idManager = null;
   private ReviewManager reviewManager;
   private StructuredArtifactDefinitionManager structuredArtifactDefinitionManager;


   public Map referenceData(Map request, Object command, Errors errors) {
      CellFormBean cell = (CellFormBean) command;
      Map model = new HashMap();
      model.put("reviewRubrics", matrixManager.getReviewRubrics());
      String pageId = cell.getCell().getWizardPage().getId().getValue();
       model.put("reviews", getReviewManager().getReviewsByParentAndType(
             pageId, Review.REVIEW_TYPE));
      model.put("evaluations", getReviewManager().getReviewsByParentAndType(
             pageId, Review.EVALUATION_TYPE));
      model.put("reflections", getReviewManager().getReviewsByParentAndType(
            pageId, Review.REFLECTION_TYPE));
      
      model.put("cellFormDefs", processAdditionalForms(
            cell.getCell().getScaffoldingCell().getAdditionalForms()));
      
      model.put("cellForms", getMatrixManager().getPageForms(cell.getCell().getWizardPage()));
      
      model.put("currentUser", SessionManager.getCurrentSessionUserId());
      model.put("CURRENT_GUIDANCE_ID_KEY", "session." + GuidanceManager.CURRENT_GUIDANCE_ID);
      return model;
   }
   
   public Object fillBackingObject(Object incomingModel, Map request, Map session, Map application) throws Exception {
      session.remove(WizardPageHelper.WIZARD_PAGE); // coming from matrix cell, not helper      
      CellFormBean cellBean = (CellFormBean) incomingModel;
      String strId = (String) request.get("page_id");
      if (strId== null) {
         strId = (String)session.get("page_id");
         session.remove("page_id");
      }
      Cell cell;
      Id id = getIdManager().getId(strId);
      cell = matrixManager.getCellFromPage(id);

      cellBean.setCell(cell);

      List nodeList = new ArrayList(matrixManager.getPageContents(cell.getWizardPage()));
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

      //String action = (String)request.get("action");
      String submitAction = (String)request.get("submit");
      String matrixAction = (String)request.get("matrix");
      
      if (submitAction != null) {
         Map map = new HashMap();
         map.put("page_id", cell.getWizardPage().getId());
         map.put("selectedArtifacts", ListToString(cellBean.getSelectedArtifacts()));
         map.put("cellBean", cellBean);
         //TODO change this to use the reflection submission confirmation
         return new ModelAndView("confirm", map); 
      }
      if (matrixAction != null) {
         return new ModelAndView("cancel");
      }      

      return new ModelAndView("success", "cellBean", cellBean);
   }
   
   protected List processAdditionalForms(List formTypes) {
      List retList = new ArrayList();
      for (Iterator iter = formTypes.iterator(); iter.hasNext();) {
         String strFormDefId = (String) iter.next();
         StructuredArtifactDefinitionBean bean = 
            getStructuredArtifactDefinitionManager().loadHome(strFormDefId);
         bean.getDescription();
         //TODO use a different bean below, as the name has implications
         retList.add(new ScaffoldingCellSupportDeviceBean(strFormDefId, bean.getDescription(), strFormDefId));
      }
      return retList;
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

   /**
    * @return Returns the structuredArtifactDefinitionManager.
    */
   public StructuredArtifactDefinitionManager getStructuredArtifactDefinitionManager() {
      return structuredArtifactDefinitionManager;
   }

   /**
    * @param structuredArtifactDefinitionManager The structuredArtifactDefinitionManager to set.
    */
   public void setStructuredArtifactDefinitionManager(
         StructuredArtifactDefinitionManager structuredArtifactDefinitionManager) {
      this.structuredArtifactDefinitionManager = structuredArtifactDefinitionManager;
   }
}

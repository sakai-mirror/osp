/**********************************************************************************
* $URL:https://source.sakaiproject.org/svn/osp/trunk/matrix/tool/src/java/org/theospi/portfolio/matrix/control/CellController.java $
* $Id:CellController.java 9134 2006-05-08 20:28:42Z chmaurer@iupui.edu $
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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.content.api.FilePickerHelper;
import org.sakaiproject.content.api.ResourceEditingHelper;
import org.sakaiproject.metaobj.security.AuthenticationManager;
import org.sakaiproject.metaobj.shared.mgt.IdManager;
import org.sakaiproject.metaobj.shared.mgt.StructuredArtifactDefinitionManager;
import org.sakaiproject.metaobj.shared.model.Agent;
import org.sakaiproject.metaobj.shared.model.Id;
import org.sakaiproject.metaobj.shared.model.StructuredArtifactDefinitionBean;
import org.sakaiproject.metaobj.utils.mvc.intf.FormController;
import org.sakaiproject.metaobj.utils.mvc.intf.LoadObjectController;
import org.sakaiproject.tool.api.ToolSession;
import org.sakaiproject.tool.cover.SessionManager;
import org.springframework.validation.Errors;
import org.springframework.web.servlet.ModelAndView;
import org.theospi.portfolio.guidance.mgt.GuidanceManager;
import org.theospi.portfolio.matrix.MatrixManager;
import org.theospi.portfolio.matrix.WizardPageHelper;
import org.theospi.portfolio.matrix.model.Cell;
import org.theospi.portfolio.matrix.model.ScaffoldingCell;
import org.theospi.portfolio.matrix.model.WizardPage;
import org.theospi.portfolio.matrix.model.impl.MatrixContentEntityProducer;
import org.theospi.portfolio.review.ReviewHelper;
import org.theospi.portfolio.review.mgt.ReviewManager;
import org.theospi.portfolio.review.model.Review;
import org.theospi.portfolio.shared.model.Node;
import org.theospi.portfolio.style.model.Style;

import java.util.*;

public class CellController implements FormController, LoadObjectController {

   protected final Log logger = LogFactory.getLog(getClass());
   private MatrixManager matrixManager;
   private AuthenticationManager authManager = null;
   private IdManager idManager = null;
   private ReviewManager reviewManager;
   private StructuredArtifactDefinitionManager structuredArtifactDefinitionManager;

   public static final String WHICH_HELPER_KEY = "filepicker.helper.key";
   public static final String KEEP_HELPER_LIST = "filepicker.helper.keeplist";


   public Map referenceData(Map request, Object command, Errors errors) {
      CellFormBean cell = (CellFormBean) command;
      Map model = new HashMap();
      String pageId = cell.getCell().getWizardPage().getId().getValue();
      String siteId = 
         cell.getCell().getWizardPage().getPageDefinition().getSiteId();
      
       model.put("reviews", getReviewManager().getReviewsByParentAndType(
             pageId, Review.REVIEW_TYPE, siteId,
             getEntityProducer()));
      model.put("evaluations", getReviewManager().getReviewsByParentAndType(
             pageId, Review.EVALUATION_TYPE, siteId,
             getEntityProducer()));
      model.put("reflections", getReviewManager().getReviewsByParentAndType(
            pageId, Review.REFLECTION_TYPE, siteId,
            getEntityProducer()));
      
      model.put("cellFormDefs", processAdditionalForms(
            cell.getCell().getScaffoldingCell().getAdditionalForms()));
      
      model.put("cellForms", getMatrixManager().getPageForms(cell.getCell().getWizardPage()));
      
      model.put("currentUser", SessionManager.getCurrentSessionUserId());
      model.put("CURRENT_GUIDANCE_ID_KEY", "session." + GuidanceManager.CURRENT_GUIDANCE_ID);
      
      Boolean readOnly = new Boolean(false);
      
      if (cell.getCell().getMatrix() != null) {
         Agent owner = cell.getCell().getMatrix().getOwner();
         readOnly = isReadOnly(owner);
      }
      model.put("readOnlyMatrix", readOnly);
      
      Style style = cell.getCell().getWizardPage().getPageDefinition().getStyle();
      
      if (style != null) {
         Id fileId = style.getStyleFile();
         Node node = getMatrixManager().getNode(fileId);
         model.put("styleUrl", node.getExternalUri());
      }
      
      Style defaultStyle = getDefaultStyle(getIdManager().getId(pageId));
      if (defaultStyle != null) {
         Id fileId = defaultStyle.getStyleFile();
         Node node = getMatrixManager().getNode(fileId);
         model.put("defaultStyleUrl", node.getExternalUri());
      }
      
      model.put("pageTitleKey", "view_cell");
      
      clearSession(SessionManager.getCurrentToolSession());
      return model;
   }
   
   protected Style getDefaultStyle(Id pageId) {
      //Get the scaffolding default style
      WizardPage wp = getMatrixManager().getWizardPage(pageId);
      ScaffoldingCell sCell = getMatrixManager().getScaffoldingCellByWizardPageDef(wp.getPageDefinition().getId());
      return sCell.getScaffolding().getStyle();
   }
   
   protected String getEntityProducer() {
      return MatrixContentEntityProducer.MATRIX_PRODUCER;
   }
   
   protected Boolean isReadOnly(Agent owner) {
      if (owner != null && !owner.equals(getAuthManager().getAgent()))
         return new Boolean(true);
      return new Boolean(false);
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
      
      clearSession(SessionManager.getCurrentToolSession());
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
         //cwm change this to use the reflection submission confirmation
         return new ModelAndView("confirm", map); 
      }
      if (matrixAction != null) {
         String scaffId = "";
         if (cell.getMatrix() != null)
            scaffId = cell.getMatrix().getScaffolding().getId().getValue();
         return new ModelAndView("cancel", "scaffolding_id", scaffId);
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
         //cwm use a different bean below, as the name has implications
         retList.add(new ScaffoldingCellSupportDeviceBean(
                  strFormDefId, bean.getDecoratedDescription(), strFormDefId,
                  bean.getOwner().getName(), bean.getModified()));
      }
      return retList;
   }

   protected void clearSession(ToolSession session) {
      session.removeAttribute(FilePickerHelper.FILE_PICKER_ATTACHMENTS);
      session.removeAttribute(FilePickerHelper.FILE_PICKER_CANCEL);
      session.removeAttribute(FilePickerHelper.FILE_PICKER_RESOURCE_FILTER);
      
      session.removeAttribute(ResourceEditingHelper.CREATE_TYPE);      
      session.removeAttribute(ResourceEditingHelper.CREATE_PARENT);
      session.removeAttribute(ResourceEditingHelper.CREATE_SUB_TYPE);
      session.removeAttribute(ResourceEditingHelper.ATTACHMENT_ID);
      
      session.removeAttribute(ReviewHelper.REVIEW_TYPE);
      session.removeAttribute(ReviewHelper.REVIEW_TYPE_KEY);

      session.removeAttribute(WHICH_HELPER_KEY);
      session.removeAttribute(KEEP_HELPER_LIST);
      
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

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
import org.sakaiproject.metaobj.security.AuthenticationManager;
import org.sakaiproject.metaobj.shared.mgt.IdManager;
import org.sakaiproject.metaobj.shared.model.Id;
import org.sakaiproject.metaobj.utils.mvc.intf.FormController;
import org.sakaiproject.metaobj.utils.mvc.intf.LoadObjectController;
import org.springframework.validation.Errors;
import org.springframework.web.servlet.ModelAndView;
import org.theospi.portfolio.matrix.MatrixManager;
import org.theospi.portfolio.matrix.model.Cell;
import org.theospi.portfolio.matrix.model.Criterion;
import org.theospi.portfolio.matrix.model.OrderedList;
import org.theospi.portfolio.matrix.model.OrderedListElement;
import org.theospi.portfolio.matrix.model.RubricSatisfactionBean;
import org.theospi.portfolio.matrix.model.Rubric;
import org.theospi.portfolio.shared.model.Node;

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
   private OrderedList orderedList = null;

/*
   public Object formBackingObject(Map request, Map session, Map application) {
      return new CellFormBean();
   }   
*/
   public Map referenceData(Map request, Object command, Errors errors) {
      Map model = new HashMap();
      model.put("reviewRubrics", matrixManager.getReviewRubrics());
      return model;
   }
   
   public Object fillBackingObject(Object incomingModel, Map request, Map session, Map application) throws Exception {
      CellFormBean cellBean = (CellFormBean) incomingModel;
      Id id = getIdManager().getId((String) request.get("cell_id"));
      Cell cell = matrixManager.getCell(id);

      cellBean.setCell(cell);

      getCriteriaRequirements(cell);
      cellBean.setCriteriaRequirements(orderedList);
      List nodeList = getCellArtifacts(cell);
      cellBean.setAttachments(nodeList);
      
      return cellBean;
   }

   private int getCriterionIndex(Cell cell) {
      List scaffCriteria = cell.getScaffoldingCell().getScaffolding().getRootCriteria();
      Criterion rootCriterion = cell.getScaffoldingCell().getRootCriterion();
      int i = 0;
      for (Iterator iter = scaffCriteria.iterator(); iter.hasNext();) {
         Criterion currentCriterion = (Criterion) iter.next();
         if (currentCriterion.equals(rootCriterion)) {
            return i;
         }
         i++;
      }

      return 0;
   }

   private void getCriteriaRequirements(Cell cell) {
      List reqList = getMatrixManager().rubricSatisfaction(cell);
      List critList = getMatrixManager().getCellCriteria(cell);

      //TODO this is pretty ugly...can we do better?
      orderedList.clear();
      int lastIndent = 0;
      int currentIndent = 0;
      //orderedList
      int index = getCriterionIndex(cell);
      orderedList.setOffset(index);

      for (Iterator iter = critList.iterator(); iter.hasNext();) {
         Criterion crit = (Criterion) iter.next();
         OrderedListElement elm = orderedList.append();
         elm.setDescription(crit.getDescription());
         currentIndent = crit.getIndent().intValue();
         if (currentIndent > lastIndent) {
            elm.indent();
         } else if (currentIndent < lastIndent) {
            elm.outdent();
         }
         elm.setDisplayString();
         lastIndent = currentIndent;
         boolean subIndent = true;
         for (Iterator foo = reqList.iterator(); foo.hasNext();) {
            RubricSatisfactionBean rub = (RubricSatisfactionBean) foo.next();
            Rubric rubric = matrixManager.getRubric(rub.getRubricId());

            if (crit.equals(rubric.getCriterion())) {
               OrderedListElement sub = orderedList.append();
               StringBuffer string = new StringBuffer();
               string.append(rubric.getType().getValue() + " - ");
               string.append(rubric.getMimeType());
               string.append(" (" + rub.getActual() + "/");
               string.append(rub.getNeeded() + ")");
               sub.setDescription(string.toString());
               sub.setData(rub.getRubricId());
               if (subIndent) {
                  sub.indent();
                  subIndent = false;
                  lastIndent++;
               }
               sub.setDisplayString();
            }
         }
      }
      int size = orderedList.size();
      for (Iterator elms = orderedList.iterator(); elms.hasNext();)
      {
          orderedList.render((OrderedListElement) elms.next());
      }
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
      }
      getCriteriaRequirements(cell);
      cellBean.setCriteriaRequirements(orderedList);

      return new ModelAndView("success", "cellBean", cellBean);
   }

   public List getCellArtifacts(Cell cell) {
      List nodeList = new ArrayList();
      for (Iterator artifactIdIterator = matrixManager.getCellContents(cell).iterator();
           artifactIdIterator.hasNext();) {
         Node node = (Node)artifactIdIterator.next();
         CellArtifactBean bean = new CellArtifactBean();
         bean.setNode(node);
         List list = matrixManager.getRubricByArtifact(node.getId());
         List myList = buildList(list);
         bean.setCriteriaList(myList);
         nodeList.add(bean);
      }
      return nodeList;
   }

   private List buildList(List rubricIds) {
      List list = new ArrayList();
      for (Iterator foo = orderedList.iterator(); foo.hasNext();) {
         OrderedListElement elm = (OrderedListElement) foo.next();
         if (elm.getData() != null) {
            if (rubricIds.contains(elm.getData())) {
               String str = elm.getDisplayString();
               if (orderedList.isFullyContainedLabels())
                  list.add(elm.getDisplayString());
               else
                  list.add(buildCompleteLabel(elm));
            }
         }
      }
      return list;
   }

   private String buildCompleteLabel(OrderedListElement elm) {
      OrderedListElement parent = null;

      String completeLabel;
      parent = orderedList.getParent(elm);
      if (parent != null) {
         completeLabel = buildCompleteLabel(orderedList.getParent(elm)) + "." + elm.getLabel();
      } else {
         completeLabel = elm.getLabel().toString();
      }

      return completeLabel;
   }
   
   //TODO: 20050715 ContentHosting
   /*

   public void getNodeList(Node rootNode, List nodeList) {
      nodeList.add(rootNode);
      if (rootNode.hasChildren()) {
         for (Iterator nodeIterator = rootNode.getChildren().iterator(); nodeIterator.hasNext();) {
            Node node = (Node) nodeIterator.next();
            getNodeList(node, nodeList);
         }
      }
   }

*/
   
   
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
    * @return
    */
   public OrderedList getOrderedList() {
      return orderedList;
   }

   /**
    * @param list
    */
   public void setOrderedList(OrderedList list) {
      orderedList = list;
   }


}

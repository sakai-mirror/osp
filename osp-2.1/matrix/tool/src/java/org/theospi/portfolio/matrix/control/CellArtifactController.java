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
 * $Header: /opt/CVS/osp2.x/matrix/tool/src/java/org/theospi/portfolio/matrix/control/CellArtifactController.java,v 1.1 2005/07/15 21:10:34 rpembry Exp $
 * $Revision$
 * $Date$
 */
package org.theospi.portfolio.matrix.control;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.metaobj.security.AuthenticationManager;
import org.sakaiproject.metaobj.shared.mgt.IdManager;
import org.sakaiproject.metaobj.shared.model.Id;
import org.sakaiproject.metaobj.utils.mvc.intf.CustomCommandController;
import org.springframework.validation.Errors;
import org.springframework.web.servlet.ModelAndView;
import org.theospi.portfolio.matrix.MatrixManager;
import org.theospi.portfolio.matrix.model.Cell;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CellArtifactController implements CustomCommandController {

   protected final Log logger = LogFactory.getLog(getClass());
   private MatrixManager matrixManager;
   //private HomeFactory homeFactory = null;
   private AuthenticationManager authManager = null;
   private IdManager idManager = null;

   public Object formBackingObject(Map request, Map session, Map application) {
      Cell cell = new Cell();

      // this is an edit, load model
      Id id = getIdManager().getId((String) request.get("ID"));
      cell = matrixManager.getCell(id);


      List nodeList = new ArrayList();
      //TODO: 20050715 ContentHosting
      /*
      Node rootNode = repositoryManager.getRootNode(authManager.getAgent());
      getNodeList(rootNode, nodeList);
      */
      Map model = new HashMap();
      model.put("nodes", nodeList);
      model.put("cellId", cell.getId());

      return new ModelAndView("browse", "model", model);

   }

   public ModelAndView handleRequest(Object requestModel, Map request, Map session, Map application, Errors errors) {
      Map model = (Map) ((ModelAndView) requestModel).getModel().get("model");
      Id cellId = (Id) model.get("cellId");
      List nodeList = (List) model.get("nodes");
      Cell cell = matrixManager.getCell(cellId);

      if (request.get("action") != null) {
         /*
         if (request.get("action").equals("attach")) {
            Id artifactId = idManager.getId((String) request.get("ARTIFACT_ID"));
            //TODO remove this attachArtifactCall
            matrixManager.attachArtifact(cell, cell.getScaffoldingCell().getRootCriterion(), artifactId, null);
            cell = matrixManager.getWizardPage(cellId);
         }
         */
         return new ModelAndView("viewCell", "cell", cell);
      }

      //TODO: 20050715 ContentHosting
      /*
      Node rootNode = repositoryManager.getRootNode(authManager.getAgent());
      getNodeList(rootNode, nodeList);
      */


      return new ModelAndView("success", "nodes", nodeList);
   }

   //TODO: 20050715 ContentHosting
   /*

   private void getNodeList(Node rootNode, List nodeList) {
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
}

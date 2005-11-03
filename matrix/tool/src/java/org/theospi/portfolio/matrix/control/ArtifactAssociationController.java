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
 * $Header: /opt/CVS/osp2.x/matrix/tool/src/java/org/theospi/portfolio/matrix/control/ArtifactAssociationController.java,v 1.2 2005/07/28 01:44:56 jellis Exp $
 * $Revision$
 * $Date$
 */
/*
 * Created on May 24, 2004
 *
 */
package org.theospi.portfolio.matrix.control;

import org.sakaiproject.metaobj.security.AuthenticationManager;
import org.sakaiproject.metaobj.shared.mgt.IdManager;
import org.sakaiproject.metaobj.shared.model.ElementBean;
import org.sakaiproject.metaobj.shared.model.Id;
import org.sakaiproject.metaobj.utils.mvc.intf.LoadObjectController;
import org.sakaiproject.metaobj.utils.xml.SchemaNode;
import org.springframework.validation.Errors;
import org.springframework.web.servlet.ModelAndView;
import org.theospi.portfolio.matrix.MatrixManager;
import org.theospi.portfolio.matrix.model.Attachment;
import org.theospi.portfolio.matrix.model.Criterion;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author apple
 */
public class ArtifactAssociationController implements LoadObjectController {
   protected final org.apache.commons.logging.Log logger = org.apache.commons.logging.LogFactory
      .getLog(getClass());

   private AuthenticationManager authnManager;
   private IdManager idManager = null;
   private MatrixManager matrixManager = null;
   
   /* (non-Javadoc)
    * @see org.theospi.utils.mvc.intf.LoadObjectController#fillBackingObject(java.lang.Object, java.util.Map, java.util.Map, java.util.Map)
    */
   public Object fillBackingObject(Object incomingModel, Map request, Map session, Map application) throws Exception {
      if (logger.isDebugEnabled()) {
         logger.debug("fillBackingObject()");
      }
      MatrixFormBean bean = (MatrixFormBean) incomingModel;
      if (bean.getAction() == null) { //TODO may not need this check when Method="POST" ?
         Id cellId;
         Id nodeId;
         if (bean.getAction() == null) {

            cellId = getIdManager().getId((String) request.get("cell_id"));
            nodeId = getIdManager().getId((String) request.get("node_id"));
            bean.setCellId(cellId);
            bean.setNodeId(nodeId);
            //TODO: 20050715 ContentHosting
            /*
            Node node = repositoryManager.getNode(nodeId);
            bean.setNode(node);

            List associatedCriteria = getMatrixManager().getArtifactAssociationCriteria(cellId, nodeId);
            Iterator criteria = getMatrixManager().getCellCriteria(cellId).iterator();
            List selected = new ArrayList();
            while (criteria.hasNext()) {
               Criterion next = (Criterion) criteria.next();
               selected.add(new Object[]{next, new Boolean(contains(associatedCriteria, next))});
            }

            bean.setCriteria(selected);
            bean.setSelectedCriteria(associatedCriteria);
                        */

         }
      }

      return incomingModel;
   }


   /**
    * @param list
    * @param criterion
    * @return
    */
   private boolean contains(List list, Criterion criterion) {
      //TODO shouldn't list.contains work instead of this?
      for (Iterator iter = list.iterator(); iter.hasNext();) {
         if (criterion.equals(iter.next())) return true;
      }
      return false;
   }

   /* (non-Javadoc)
    * @see org.theospi.utils.mvc.intf.Controller#handleRequest(java.lang.Object, java.util.Map, java.util.Map, java.util.Map, org.springframework.validation.Errors)
    */
   public ModelAndView handleRequest(Object requestModel, Map request, Map session, Map application, Errors errors) {
      Map model = new HashMap();
      Attachment attachment = null;
      
      if (logger.isDebugEnabled()) {
         logger.debug("handleRequest()");
      }
      MatrixFormBean bean = (MatrixFormBean) requestModel;
      Id cellId = bean.getCellId();
    
      if ("submit".equals(bean.getAction())) {
         if (bean.getSelectedCriteria() != null) {

            PrivacyFormBean privacyBean = (PrivacyFormBean)session.get("PrivacyFormBean");
            ElementBean elem = null;
            if (privacyBean != null) {
               elem = privacyBean.getPrivacyResponse();
            }
            //attachment = getMatrixManager().attachArtifact(bean.getCellId(), bean.getSelectedCriteria(),
            //      bean.getNodeId(), elem);
            session.put("PrivacyFormBean", null);
            session.remove("PrivacyFormBean");
         } else {
            logger.debug("clearing all associations");
            getMatrixManager().detachArtifact(bean.getCellId(), bean.getNodeId());

         }

         session.put("org_theospi_matrix_attachCell", null);

      }

      return new ModelAndView("success", "cell_id", bean.getCellId().getValue());
   }
   
   protected SchemaNode loadSchema(Id xsdId, Map model) {
       //TODO: 20050715 ContentHosting
       /*
      RepositoryNode rNode = (RepositoryNode) getRepositoryManager().getNode(xsdId);
      model.put("propertyFileMetadata", rNode.getTechnicalMetadata());
      SchemaFactory schemaFactory = SchemaFactory.getInstance();
      return schemaFactory.getSchema(rNode.getStream());
      */
       throw new RuntimeException("Unimplemented: ContentHosting migration");
   }

   /**
    * @return Returns the authnManager.
    */
   public AuthenticationManager getAuthnManager() {
      return authnManager;
   }

   /**
    * @param authnManager The authnManager to set.
    */
   public void setAuthnManager(AuthenticationManager authnManager) {
      this.authnManager = authnManager;
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

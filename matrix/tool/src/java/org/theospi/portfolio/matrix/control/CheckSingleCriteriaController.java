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
 * $Header: /opt/CVS/osp2.x/matrix/tool/src/java/org/theospi/portfolio/matrix/control/CheckSingleCriteriaController.java,v 1.5 2005/10/26 23:53:01 jellis Exp $
 * $Revision$
 * $Date$
 */
package org.theospi.portfolio.matrix.control;

import java.util.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.metaobj.shared.mgt.IdManager;
import org.sakaiproject.metaobj.shared.model.ElementBean;
import org.sakaiproject.metaobj.shared.model.Id;
import org.sakaiproject.metaobj.utils.mvc.intf.Controller;
import org.sakaiproject.metaobj.utils.mvc.intf.LoadObjectController;
import org.sakaiproject.service.legacy.entity.Reference;
import org.sakaiproject.service.legacy.entity.EntityManager;
import org.sakaiproject.service.legacy.filepicker.FilePickerHelper;
import org.springframework.validation.Errors;
import org.springframework.web.servlet.ModelAndView;
import org.theospi.portfolio.matrix.MatrixManager;
import org.theospi.portfolio.matrix.model.Attachment;
import org.theospi.portfolio.matrix.model.Cell;


public class CheckSingleCriteriaController implements Controller, LoadObjectController {

   protected final Log logger = LogFactory.getLog(getClass());
   private IdManager idManager = null;
   private MatrixManager matrixManager = null;
   private EntityManager entityManager;

   public Object fillBackingObject(Object incomingModel, Map request,
                                   Map session, Map application) throws Exception {
      Object returned = request.get(AttachArtifactController.ATTACH_ARTIFACT_FORM);

      if (returned != null) {
         return returned;
      }
      else {
         CellAndNodeForm form = new CellAndNodeForm();
         form.setCell_id((String)request.get("cell_id"));
         return form;
      }
   }

   public ModelAndView handleRequest(Object requestModel, Map request, Map session, Map application, Errors errors) {
      CellAndNodeForm form = (CellAndNodeForm) requestModel;
      String viewName = "";
      Id cellId = idManager.getId(form.getCell_id());

      Cell cell = matrixManager.getCell(cellId);
      String[] criteria = {cell.getScaffoldingCell().getRootCriterion().getId().getValue()};
      
      //If there's only one, do stuff then go on to the cell view
      if (criteria != null) {

         PrivacyFormBean privacyBean = (PrivacyFormBean)session.get("PrivacyFormBean");
         ElementBean elem = null;
         if (privacyBean != null) {
            elem = privacyBean.getPrivacyResponse();
         }

         List files = (List)session.get(
               FilePickerHelper.FILE_PICKER_ATTACHMENTS);
         Set attachments = cell.getAttachments();
         List existing = new ArrayList();
         for (Iterator i = attachments.iterator();i.hasNext();) {
            Attachment attach = (Attachment)i.next();
            existing.add(attach.getArtifactId());
         }

         for (Iterator i = files.iterator();i.hasNext();) {
            Object node = i.next();
            Reference ref = null;
            if (node instanceof Reference) {
               ref = (Reference)node;
            }
            Attachment attachment = getMatrixManager().attachArtifact(
                  cellId, criteria, ref, elem);
            existing.remove(attachment.getArtifactId());
         }

         for (Iterator i = existing.iterator();i.hasNext();) {
            Id oldAttachment = (Id)i.next();
            getMatrixManager().detachArtifact(cell.getId(), oldAttachment);
         }

         session.remove(FilePickerHelper.FILE_PICKER_ATTACHMENTS);
         session.put("PrivacyFormBean", null);
         session.remove("PrivacyFormBean");
      }
      return new ModelAndView("success", "cell_id", cellId);
   }


   public IdManager getIdManager() {
      return idManager;
   }

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

   public EntityManager getEntityManager() {
      return entityManager;
   }

   public void setEntityManager(EntityManager entityManager) {
      this.entityManager = entityManager;
   }
}

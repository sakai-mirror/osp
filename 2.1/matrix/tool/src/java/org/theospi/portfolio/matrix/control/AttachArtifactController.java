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
 * $Header: /opt/CVS/osp2.x/matrix/tool/src/java/org/theospi/portfolio/matrix/control/AttachArtifactController.java,v 1.8 2005/10/26 23:53:01 jellis Exp $
 * $Revision$
 * $Date$
 */
package org.theospi.portfolio.matrix.control;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.metaobj.shared.mgt.IdManager;
import org.sakaiproject.metaobj.utils.mvc.intf.Controller;
import org.sakaiproject.metaobj.utils.mvc.intf.LoadObjectController;
import org.sakaiproject.metaobj.utils.mvc.intf.CancelableController;
import org.sakaiproject.api.kernel.session.SessionManager;
import org.sakaiproject.api.kernel.session.ToolSession;
import org.sakaiproject.service.legacy.content.ContentHostingService;
import org.sakaiproject.service.legacy.filepicker.FilePickerHelper;
import org.sakaiproject.service.legacy.entity.Reference;
import org.sakaiproject.service.legacy.entity.EntityManager;
import org.springframework.validation.Errors;
import org.springframework.web.servlet.ModelAndView;
import org.theospi.portfolio.matrix.MatrixManager;
import org.theospi.portfolio.matrix.model.Attachment;
import org.theospi.portfolio.matrix.model.Cell;

import java.util.*;

public class AttachArtifactController implements Controller, LoadObjectController, CancelableController {
   protected final transient Log logger = LogFactory.getLog(getClass());
   private SessionManager sessionManager;
   private MatrixManager matrixManager;
   private IdManager idManager;
   private ContentHostingService contentHosting;
   private EntityManager entityManager;
   public static final Object ATTACH_ARTIFACT_FORM = "attachArtifactForm";

   public Object fillBackingObject(Object incomingModel, Map request,
         Map session, Map application) throws Exception {
      CellAndNodeForm form = (CellAndNodeForm)incomingModel;
      String cell_id = (String)request.get("cell_id");
      if (cell_id != null) {
         form.setCell_id(cell_id);
         session.put(getModelName(), form);
         Cell cell = matrixManager.getCell(idManager.getId(cell_id));
         Set attachments = cell.getAttachments();
         List files = new ArrayList();
         for (Iterator iter=attachments.iterator(); iter.hasNext();) {
        	   Attachment att = (Attachment)iter.next();
        	   String id = getContentHosting().resolveUuid(att.getArtifactId().getValue());
            Reference ref = getEntityManager().newReference(contentHosting.getResource(id).getReference());
            files.add(ref);
         }
         
         session.put(FilePickerHelper.FILE_PICKER_ATTACHMENTS, files);
         
      }
      else {
         form = (CellAndNodeForm)session.get(
               getModelName());
      }
      return form;
   }

   protected String getModelName() {
      return this.getClass().getName() + ".model";
   }

   public ModelAndView handleRequest(Object requestModel, Map request, Map session,
                                     Map application, Errors errors) {
      CellAndNodeForm form = (CellAndNodeForm)requestModel;
      session.remove(getModelName());
      request.put(ATTACH_ARTIFACT_FORM, form);
      // track all the attachments here...
      return new ModelAndView("success");
   }

   public boolean isCancel(Map request) {
      ToolSession toolSession = getSessionManager().getCurrentToolSession();
      boolean returned = toolSession.getAttribute(FilePickerHelper.FILE_PICKER_CANCEL) != null;
      return returned;
   }

   public ModelAndView processCancel(Map request, Map session,
                                     Map application, Object command, Errors errors) throws Exception {
      CellAndNodeForm form = (CellAndNodeForm)command;
      session.remove(getModelName());

      Map model = new Hashtable();
      model.put("cell_id", form.getCell_id());
      model.put("readOnlyMatrix", new Boolean("false"));

      return new ModelAndView("cancel", model);
   }

   public SessionManager getSessionManager() {
      return sessionManager;
   }

   public void setSessionManager(SessionManager sessionManager) {
      this.sessionManager = sessionManager;
   }

public ContentHostingService getContentHosting() {
	return contentHosting;
}

public void setContentHosting(ContentHostingService contentHosting) {
	this.contentHosting = contentHosting;
}

public IdManager getIdManager() {
	return idManager;
}

public void setIdManager(IdManager idManager) {
	this.idManager = idManager;
}

public MatrixManager getMatrixManager() {
	return matrixManager;
}

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

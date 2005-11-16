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
 * $Header: /opt/CVS/osp2.x/presentation/tool/src/java/org/theospi/portfolio/presentation/control/AddTemplateController.java,v 1.9 2005/10/26 23:53:01 jellis Exp $
 * $Revision: 3474 $
 * $Date: 2005-11-03 18:05:53 -0500 (Thu, 03 Nov 2005) $
 */
/*
 * $Header: /opt/CVS/osp2.x/presentation/tool/src/java/org/theospi/portfolio/presentation/control/AddTemplateController.java,v 1.9 2005/10/26 23:53:01 jellis Exp $
 * $Revision: 3474 $
 * $Date: 2005-11-03 18:05:53 -0500 (Thu, 03 Nov 2005) $
 */
package org.theospi.portfolio.presentation.control;

import org.sakaiproject.service.framework.portal.cover.PortalService;
import org.sakaiproject.service.legacy.content.ContentHostingService;
import org.sakaiproject.service.legacy.filepicker.FilePickerHelper;
import org.sakaiproject.service.legacy.entity.Reference;
import org.sakaiproject.service.legacy.entity.EntityManager;
import org.springframework.validation.Errors;
import org.springframework.web.servlet.ModelAndView;
import org.theospi.portfolio.presentation.model.PresentationLayout;
import org.sakaiproject.api.kernel.session.SessionManager;
import org.sakaiproject.api.kernel.session.ToolSession;
import org.sakaiproject.exception.IdUnusedException;
import org.sakaiproject.exception.PermissionException;
import org.sakaiproject.exception.TypeException;
import org.sakaiproject.metaobj.shared.model.Id;
import org.theospi.portfolio.shared.model.Node;
import org.sakaiproject.metaobj.utils.mvc.intf.CustomCommandController;
import org.sakaiproject.metaobj.utils.mvc.intf.FormController;
import org.sakaiproject.metaobj.utils.mvc.intf.LoadObjectController;

import java.util.*;

public class AddLayoutController extends AbstractPresentationController 
      implements CustomCommandController, FormController, LoadObjectController {
   
   public static final String XHTML_FILE = "osp.presentation.layout.xhtmlFile";
   public static final String PREVIEW_IMAGE = "osp.presentation.layout.previewImage";
   protected static final String LAYOUT_SESSION_TAG =
      "osp.presentation.AddLayoutController.layout";

   private SessionManager sessionManager;
   private ContentHostingService contentHosting;
   private EntityManager entityManager;

   public Object formBackingObject(Map request, Map session, Map application) {
      PresentationLayout layout;
      if (request.get("layout_id") != null && !request.get("layout_id").equals("")) {
         Id id = getIdManager().getId((String)request.get("layout_id"));
         layout = getPresentationManager().getPresentationLayout(id);
      }
      else {
         layout = new PresentationLayout();
         layout.setOwner(getAuthManager().getAgent());
         layout.setToolId(PortalService.getCurrentToolId());
         layout.setSiteId(PortalService.getCurrentSiteId());
      }
      return layout;
   }

   public Object fillBackingObject(Object incomingModel, Map request, Map session, Map application) throws Exception {
      if (session.get(LAYOUT_SESSION_TAG) != null) {
         return session.remove(LAYOUT_SESSION_TAG);
      }
      else {
         return incomingModel;
      }
   }

   public ModelAndView handleRequest(Object requestModel, Map request,
                                     Map session, Map application, Errors errors) {
      PresentationLayout layout = (PresentationLayout) requestModel;

      if (XHTML_FILE.equals(layout.getFilePickerAction()) ||
            PREVIEW_IMAGE.equals(layout.getFilePickerAction())) {
         session.put(LAYOUT_SESSION_TAG, layout);
         //session.put(FilePickerHelper.FILE_PICKER_FROM_TEXT, request.get("filePickerFrom"));
         
         List files = new ArrayList();
         String id = "";
         if (XHTML_FILE.equals(layout.getFilePickerAction()) && layout.getXhtmlFileId() != null) {
            id = getContentHosting().resolveUuid(layout.getXhtmlFileId().getValue());
         }
         else if (XHTML_FILE.equals(layout.getFilePickerAction()) && layout.getXhtmlFileId() != null) {
            id = getContentHosting().resolveUuid(layout.getXhtmlFileId().getValue());
         }
         if (id != null && !id.equals("")) {
            Reference ref;
            try {
               ref = getEntityManager().newReference(getContentHosting().getResource(id).getReference());
               files.add(ref);              
               session.put(FilePickerHelper.FILE_PICKER_ATTACHMENTS, files);
            } catch (PermissionException e) {
               logger.error("", e);
            } catch (IdUnusedException e) {
               logger.error("", e);
            } catch (TypeException e) {
               logger.error("", e);
            }            
         }
         
         return new ModelAndView("pickLayoutFiles");
      }
/*
      String action = "";
      Object actionObj = request.get("action");
      if (actionObj instanceof String) {
         action = (String)actionObj;
      }
      else if (actionObj instanceof String[]) {
         action = ((String[])actionObj)[0];
      }
*/
      if (request.get("save") != null)
         save(layout, errors);

      return new ModelAndView("success");
   }
   
   protected void save(PresentationLayout layout, Errors errors) {
        getPresentationManager().storeLayout(layout);
   }

   public Map referenceData(Map request, Object command, Errors errors) {
      Map model = new HashMap();
      model.put("XHTML_FILE", XHTML_FILE);
      model.put("PREVIEW_IMAGE", PREVIEW_IMAGE);
      PresentationLayout layout = (PresentationLayout) command;
      
      ToolSession session = getSessionManager().getCurrentToolSession();
      if (session.getAttribute(FilePickerHelper.FILE_PICKER_CANCEL) == null &&
            session.getAttribute(FilePickerHelper.FILE_PICKER_ATTACHMENTS) != null) {
         // here is where we setup the id
         List refs = (List)session.getAttribute(FilePickerHelper.FILE_PICKER_ATTACHMENTS);
         Id nodeId = null;
         String nodeName = "";
         
         if (refs.size() == 1) {
            Reference ref = (Reference)refs.get(0);
            Node node = getPresentationManager().getNode(ref);
            nodeId = node.getId();
            nodeName = node.getDisplayName();
         }

         if (XHTML_FILE.equals(layout.getFilePickerAction())) {
            layout.setXhtmlFileId(nodeId);
            layout.setXhtmlFileName(nodeName);
         }
         else if (PREVIEW_IMAGE.equals(layout.getFilePickerAction())) {
            layout.setPreviewImageId(nodeId);
            layout.setPreviewImageName(nodeName);
         }
      }
      
      if (layout.getXhtmlFileId() != null) {
         Node xhtmlFile = getPresentationManager().getNode(layout.getXhtmlFileId());
         model.put("xhtmlFileName", xhtmlFile.getDisplayName());
      }
      if (layout.getPreviewImageId() != null) {
         Node previewFile = getPresentationManager().getNode(layout.getPreviewImageId());
         model.put("previewImageName", previewFile.getDisplayName());
      }
      
      session.removeAttribute(FilePickerHelper.FILE_PICKER_ATTACHMENTS);
      session.removeAttribute(FilePickerHelper.FILE_PICKER_CANCEL);

      return model;
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

   public EntityManager getEntityManager() {
      return entityManager;
   }

   public void setEntityManager(EntityManager entityManager) {
      this.entityManager = entityManager;
   }

}


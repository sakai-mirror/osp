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
 * $Header: /opt/CVS/osp2.x/presentation/tool/src/java/org/theospi/portfolio/presentation/control/ImportTemplateController.java,v 1.4 2005/10/26 23:53:01 jellis Exp $
 * $Revision$
 * $Date$
 */
package org.theospi.portfolio.presentation.control;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.web.servlet.ModelAndView;
import org.theospi.portfolio.presentation.model.TemplateUploadForm;
import org.theospi.portfolio.presentation.model.PresentationTemplate;
import org.theospi.portfolio.shared.model.OspException;
import org.sakaiproject.api.kernel.session.SessionManager;
import org.sakaiproject.api.kernel.session.ToolSession;
import org.sakaiproject.exception.IdUnusedException;
import org.sakaiproject.exception.PermissionException;
import org.sakaiproject.exception.TypeException;
import org.sakaiproject.metaobj.shared.model.InvalidUploadException;
import org.theospi.portfolio.shared.model.Node;
import org.sakaiproject.service.framework.portal.cover.PortalService;
import org.sakaiproject.service.legacy.content.ContentHostingService;
import org.sakaiproject.service.legacy.filepicker.FilePickerHelper;
import org.sakaiproject.service.legacy.entity.Reference;
import org.sakaiproject.service.legacy.entity.EntityManager;

import java.util.*;

public class ImportTemplateController extends AbstractPresentationController implements Validator{
   protected final transient Log logger = LogFactory.getLog(getClass());
   
   private SessionManager sessionManager;
   private ContentHostingService contentHosting = null;
   private EntityManager entityManager;

   public ModelAndView handleRequest(Object requestModel, Map request, Map session,
                                     Map application, Errors errors) {

      TemplateUploadForm templateForm = (TemplateUploadForm)requestModel;
      
      if (templateForm.getSubmitAction().equals("pickImport")) {
         if (templateForm.getUploadedTemplate() != null) {
            String id = getContentHosting().resolveUuid(templateForm.getUploadedTemplate().getValue());
            Reference ref;
            List files = new ArrayList();
            try {
               ref = getEntityManager().newReference(getContentHosting().getResource(id).getReference());
               files.add(ref);
            } catch (PermissionException e) {
               logger.error("", e);
            } catch (IdUnusedException e) {
               logger.error("", e);
            } catch (TypeException e) {
               logger.error("", e);
            }
            session.put(FilePickerHelper.FILE_PICKER_ATTACHMENTS, files);
         }
         return new ModelAndView("pickImport");
      }
      else {
         try {
            Node file = getPresentationManager().getNode(templateForm.getUploadedTemplate());
            PresentationTemplate template = getPresentationManager().uploadTemplate(
                  file.getDisplayName(),
                  PortalService.getCurrentToolId(),
                  file.getInputStream());
            Map model = new Hashtable();
            model.put("newPresentationTemplateId", template.getId().getValue());
      
            return new ModelAndView("success", model);
         } catch (InvalidUploadException e) {
            logger.warn("Failed uploading template", e);
            errors.rejectValue(e.getFieldName(), e.getMessage(), e.getMessage());
            return null;
         } catch (Exception e) {
            logger.error("Failed importing template", e);
            throw new OspException(e);
         }
      }
   }

   public Map referenceData(Map request, Object command, Errors errors) {
      TemplateUploadForm templateForm = (TemplateUploadForm)command;
      Map model = new HashMap();
      
      ToolSession session = getSessionManager().getCurrentToolSession();
      if (session.getAttribute(FilePickerHelper.FILE_PICKER_CANCEL) == null &&
            session.getAttribute(FilePickerHelper.FILE_PICKER_ATTACHMENTS) != null) {
         // here is where we setup the id
         List refs = (List)session.getAttribute(FilePickerHelper.FILE_PICKER_ATTACHMENTS);
         if (refs.size() == 1) {
            Reference ref = (Reference)refs.get(0);
            Node node = getPresentationManager().getNode(ref);
            templateForm.setUploadedTemplate(node.getId());
            model.put("name", node.getDisplayName());
         }
         else {
            templateForm.setUploadedTemplate(null);
         }
      }
      
      session.removeAttribute(FilePickerHelper.FILE_PICKER_ATTACHMENTS);
      session.removeAttribute(FilePickerHelper.FILE_PICKER_CANCEL);
      return model;
   }

   public boolean supports(Class clazz) {
      return (TemplateUploadForm.class.isAssignableFrom(clazz));
   }

   public void validate(Object obj, Errors errors) {
      TemplateUploadForm templateForm = (TemplateUploadForm) obj;
      if (templateForm.getUploadedTemplate() == null && templateForm.isValidate()){
         errors.rejectValue("uploadedTemplate", "error.required", "required");
      }
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

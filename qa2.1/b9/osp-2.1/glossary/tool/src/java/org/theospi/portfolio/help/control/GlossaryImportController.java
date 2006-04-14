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
 * $Header: /opt/CVS/osp2.x/glossary/tool/src/java/org/theospi/portfolio/help/control/GlossaryRemoveController.java,v 1.1 2005/07/08 01:18:46 jellis Exp $
 * $Revision: 3474 $
 * $Date: 2005-11-03 18:05:53 -0500 (Thu, 03 Nov 2005) $
 */
package org.theospi.portfolio.help.control;

import org.jdom.input.JDOMParseException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.api.kernel.component.cover.ComponentManager;
import org.sakaiproject.api.kernel.session.SessionManager;
import org.sakaiproject.api.kernel.session.ToolSession;
import org.sakaiproject.exception.IdUnusedException;
import org.sakaiproject.exception.PermissionException;
import org.sakaiproject.exception.TypeException;
import org.sakaiproject.exception.UnsupportedFileTypeException;
import org.sakaiproject.metaobj.shared.model.InvalidUploadException;
import org.sakaiproject.metaobj.shared.model.Id;
import org.sakaiproject.metaobj.utils.mvc.intf.CancelableController;
import org.sakaiproject.metaobj.utils.mvc.intf.FormController;
import org.sakaiproject.service.framework.portal.cover.PortalService;
import org.sakaiproject.service.legacy.content.ContentHostingService;
import org.sakaiproject.service.legacy.entity.EntityManager;
import org.sakaiproject.service.legacy.entity.Reference;
import org.sakaiproject.service.legacy.filepicker.FilePickerHelper;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.theospi.portfolio.help.model.GlossaryEntry;
import org.theospi.portfolio.help.model.GlossaryUploadForm;
import org.theospi.portfolio.shared.model.Node;
import org.theospi.portfolio.shared.model.OspException;
import org.theospi.utils.mvc.impl.servlet.AbstractFormController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;

public class GlossaryImportController extends HelpController implements Validator, CancelableController, FormController {
   protected final transient Log logger = LogFactory.getLog(getClass());

   public static final String PARAM_CANCEL = "_cancel";
   private SessionManager sessionManager;
   private ContentHostingService contentHosting = null;
   private EntityManager entityManager;

   public ModelAndView handleRequest(Object requestModel, Map request, Map session,
                                     Map application, Errors errors) {

	   GlossaryUploadForm templateForm = (GlossaryUploadForm)requestModel;
      if(templateForm == null)
    	   return new ModelAndView("success");
      
      //  if we are picking the file to import
      if (templateForm.getSubmitAction() != null && templateForm.getSubmitAction().equals("pickImport")) {
         
         // if there are files selected already, then put them into the session
         if (templateForm.getUploadedGlossary() != null && templateForm.getUploadedGlossary().length() > 0) {
            Reference ref;
            List files = new ArrayList();
            String ids[] = templateForm.getUploadedGlossary().split(",");
            
            // get a list of references of the selected files
            for(int i = 0; i < ids.length; i++) {
	            try {
		                String id = ids[i];
		                id = getContentHosting().resolveUuid(id);
		                String rid = getContentHosting().getResource(id).getReference();
		            	ref = getEntityManager().newReference(rid);
		                files.add(ref);
	            } catch (PermissionException e) {
	               logger.error("", e);
	            } catch (IdUnusedException e) {
	               logger.error("", e);
	            } catch (TypeException e) {
	               logger.error("", e);
	            }
            }
            session.put(FilePickerHelper.FILE_PICKER_ATTACHMENTS, files);
         }
         session.put(FilePickerHelper.FILE_PICKER_MAX_ATTACHMENTS, new Integer(1));
         return new ModelAndView("pickImport");
         
      } else {
         
         //  if there are files, then we want to import them
    	   if(templateForm.getUploadedGlossary().length() > 0) {
	         String ids[] = templateForm.getUploadedGlossary().split(",");
	         for(int i = 0; i < ids.length; i++) {
		        try {
	              String id = ids[i];
	              
		        	  getHelpManager().importTermsResource(id, templateForm.getReplaceExistingTerms());
                 session.put(TRANSFER_CONTROLLER_SESSION_MESSAGE, 
                                      TRANSFER_MESSAGE_IMPORT_SUCCESS);
              } catch (UnsupportedFileTypeException e) {
                 logger.error("Failed uploading glossary terms", e);
                 session.put(TRANSFER_CONTROLLER_SESSION_MESSAGE, 
                                      TRANSFER_MESSAGE_IMPORT_BAD_FILE);
              } catch (InvalidUploadException e) {
                 logger.error("Failed uploading glossary terms", e);
                 //errors.rejectValue(e.getFieldName(), e.getMessage(), e.getMessage());
                 session.put(TRANSFER_CONTROLLER_SESSION_MESSAGE, 
                                      TRANSFER_MESSAGE_IMPORT_FAILED);
              } catch (JDOMParseException e) {
                 logger.error("Failed uploading glossary terms: Couldn't parse the file", e);
                 //errors.rejectValue(e.getFieldName(), e.getMessage(), e.getMessage());
                 session.put(TRANSFER_CONTROLLER_SESSION_MESSAGE, 
                                      TRANSFER_MESSAGE_IMPORT_BAD_PARSE);
		        } catch (Exception e) {
		           logger.error("Failed importing glossary terms", e);
                 session.put(TRANSFER_CONTROLLER_SESSION_MESSAGE, 
                                      TRANSFER_MESSAGE_IMPORT_FAILED);
		        }
	         }
    	   }
        session.remove(FilePickerHelper.FILE_PICKER_ATTACHMENTS);
        session.remove(FilePickerHelper.FILE_PICKER_MAX_ATTACHMENTS);
         Map model = new Hashtable();
    	   return new ModelAndView("success", model);
      }
   }

   public Map referenceData(Map request, Object command, Errors errors) {
	  GlossaryUploadForm templateForm = (GlossaryUploadForm)command;
      Map model = new HashMap();
      
      ToolSession session = getSessionManager().getCurrentToolSession();
      if (session.getAttribute(FilePickerHelper.FILE_PICKER_CANCEL) == null &&
            session.getAttribute(FilePickerHelper.FILE_PICKER_ATTACHMENTS) != null) {
         // here is where we setup the id
         List refs = (List)session.getAttribute(FilePickerHelper.FILE_PICKER_ATTACHMENTS);
         if (refs.size() >= 1) {
        	String ids = "";
        	String names = "";
        	
        	for(Iterator iter = refs.iterator(); iter.hasNext(); ) {
	            Reference ref = (Reference)iter.next();
	    		String nodeId = getContentHosting().getUuid(ref.getId());
	
	            Node node = getHelpManager().getNode(getIdManager().getId(nodeId));
	            
	            if(ids.length() > 0)
	            	ids += ",";
	            ids += node.getId();
	            names += node.getDisplayName() + " ";
        	}
            templateForm.setUploadedGlossary(ids);
            model.put("name", names);
         }
         else {
            templateForm.setUploadedGlossary(null);
         }
      }
      
      session.removeAttribute(FilePickerHelper.FILE_PICKER_ATTACHMENTS);
      session.removeAttribute(FilePickerHelper.FILE_PICKER_CANCEL);
      session.setAttribute(FilePickerHelper.FILE_PICKER_RESOURCE_FILTER,
              ComponentManager.get("org.sakaiproject.service.legacy.content.ContentResourceFilter.glossaryStyleFile"));
      return model;
   }

   public boolean supports(Class clazz) {
      return (GlossaryUploadForm.class.isAssignableFrom(clazz));
   }

   public void validate(Object obj, Errors errors) {
	   GlossaryUploadForm templateForm = (GlossaryUploadForm) obj;
      if ((templateForm.getUploadedGlossary() == null || templateForm.getUploadedGlossary().length() == 0) && templateForm.isValidate()){
         errors.rejectValue("uploadedGlossary", "error.required", "required");
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
   
	/**
	 * Return if cancel action is specified in the request.
	 * <p>Default implementation looks for "_cancel" parameter in the request.
	 * @param request current HTTP request
	 * @see #PARAM_CANCEL
	 */
   public boolean isCancel(Map request) {
       return request.containsKey(PARAM_CANCEL);
   }

   public ModelAndView processCancel(Map request, Map session, Map application,
                                     Object command, Errors errors) throws Exception {

       return new ModelAndView("cancel");
   }
}

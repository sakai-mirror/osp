/**********************************************************************************
* $URL: https://source.sakaiproject.org/svn/trunk/sakai/legacy/component/src/java/org/sakaiproject/component/framework/email/TestEmailService.java $
* $Id: TestEmailService.java 3831 2005-11-14 20:17:24Z ggolden@umich.edu $
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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.api.kernel.session.SessionManager;
import org.sakaiproject.api.kernel.session.ToolSession;
import org.sakaiproject.metaobj.shared.model.Id;
import org.sakaiproject.metaobj.utils.mvc.intf.FormController;
import org.sakaiproject.metaobj.utils.mvc.intf.LoadObjectController;
import org.sakaiproject.service.legacy.content.ContentHostingService;
import org.sakaiproject.service.legacy.entity.EntityManager;
import org.sakaiproject.service.legacy.entity.Reference;
import org.sakaiproject.service.legacy.filepicker.FilePickerHelper;
import org.sakaiproject.service.legacy.filepicker.ResourceEditingHelper;
import org.sakaiproject.service.legacy.security.SecurityService;
import org.springframework.validation.Errors;
import org.springframework.web.servlet.ModelAndView;
import org.theospi.portfolio.matrix.model.WizardPageForm;
import org.theospi.portfolio.matrix.model.WizardPage;
import org.theospi.portfolio.security.AllowMapSecurityAdvisor;
import org.theospi.portfolio.shared.tool.BaseFormResourceFilter;

public class CellFormPickerController extends CellController implements FormController, LoadObjectController {

   protected final Log logger = LogFactory.getLog(getClass());
   private ContentHostingService contentHosting;
   private EntityManager entityManager;
   private SessionManager sessionManager;
   private SecurityService securityService = null;
   
   public static final String HELPER_CREATOR = "filepicker.helper.creator";
   public static final String HELPER_PICKER = "filepicker.helper.picker";
   
   public Map referenceData(Map request, Object command, Errors errors) {
      

      ToolSession session = getSessionManager().getCurrentToolSession();
      String pageId = (String) request.get("page_id");
      if (pageId == null) {
         pageId = (String)session.getAttribute("page_id");
      }
      WizardPage page = getMatrixManager().getWizardPage(getIdManager().getId(pageId));
      
      if (session.getAttribute(FilePickerHelper.FILE_PICKER_CANCEL) == null &&
            session.getAttribute(FilePickerHelper.FILE_PICKER_ATTACHMENTS) != null) {
         // here is where we setup the id
         List refs = (List)session.getAttribute(FilePickerHelper.FILE_PICKER_ATTACHMENTS);
         page.getPageForms().clear();
         for (Iterator iter = refs.iterator(); iter.hasNext();) {
            Reference ref = (Reference) iter.next();
            String strId = getMatrixManager().getNode(ref).getId().getValue();
            page.getPageForms().add(strId);
         }
         getMatrixManager().storePage(page);
         
         session.removeAttribute(FilePickerHelper.FILE_PICKER_ATTACHMENTS);
         session.removeAttribute(FilePickerHelper.FILE_PICKER_CANCEL);
      }
      return null;
   }

   public Object fillBackingObject(Object incomingModel, Map request,
         Map session, Map application) throws Exception {
      
      String pageId = (String) request.get("page_id");
      if (pageId == null) {
         pageId = (String)session.get("page_id");
      }
      WizardPage page = getMatrixManager().getWizardPage(getIdManager().getId(pageId));
      
      if (session.get(FilePickerHelper.FILE_PICKER_CANCEL) == null &&
            session.get(FilePickerHelper.FILE_PICKER_ATTACHMENTS) != null) {
         // here is where we setup the id
         List refs = (List)session.get(FilePickerHelper.FILE_PICKER_ATTACHMENTS);
         //if (session.get(WHICH_HELPER_KEY).equals(HELPER_PICKER))
         if (HELPER_PICKER.equals((String)session.get(WHICH_HELPER_KEY)))
            page.getPageForms().clear();
         
         for (Iterator iter = refs.iterator(); iter.hasNext();) {
            Reference ref = (Reference) iter.next();
            Id id = getMatrixManager().getNode(ref).getId();
            WizardPageForm wpf = new WizardPageForm();
            wpf.setArtifactId(id);
            wpf.setFormType(ref.getProperties().getProperty(
                  ref.getProperties().getNamePropStructObjType()));
            wpf.setWizardPage(page);
            page.getPageForms().add(wpf);
         }
         getMatrixManager().storePage(page);
         
         session.remove(FilePickerHelper.FILE_PICKER_ATTACHMENTS);
         session.remove(FilePickerHelper.FILE_PICKER_CANCEL);
         session.remove(ResourceEditingHelper.CREATE_TYPE);
         
         session.remove(ResourceEditingHelper.CREATE_PARENT);
         session.remove(ResourceEditingHelper.CREATE_SUB_TYPE);
         session.remove(ResourceEditingHelper.ATTACHMENT_ID);
         session.remove(WHICH_HELPER_KEY);
      }
      return null;
   }
   
   public ModelAndView handleRequest(Object requestModel, Map request, Map session, Map application, Errors errors) {
      String attachFormAction = (String) request.get("attachFormAction");
      String createFormAction = (String) request.get("createFormAction");
      String viewFormAction = (String) request.get("viewFormAction");
      String pageId = (String) request.get("page_id");
      if (pageId == null) {
         pageId = (String)session.get("page_id");
         session.remove("page_id");
      }
      WizardPage page = getMatrixManager().getWizardPage(getIdManager().getId(pageId));
      
      if (attachFormAction != null) {
         //session.setAttribute(TEMPLATE_PICKER, request.getParameter("pickerField"));
         //session.setAttribute("SessionPresentationTemplate", template);
         //session.setAttribute(STARTING_PAGE, request.getParameter("returnPage"));
         
         List files = new ArrayList();
         
         //String pickField = (String)request.get("formType");
         String id = "";
         for (Iterator iter = page.getPageForms().iterator(); iter.hasNext();) {
            WizardPageForm wpf = (WizardPageForm) iter.next();
            if (attachFormAction.equals(wpf.getFormType())) {
               id = getContentHosting().resolveUuid(wpf.getArtifactId().getValue());
               Reference ref = getEntityManager().newReference(getContentHosting().getReference(id));
               files.add(ref);        
            }
         }
         BaseFormResourceFilter crf = new BaseFormResourceFilter();
         
         crf.getFormTypes().add(attachFormAction);
         session.put(FilePickerHelper.FILE_PICKER_RESOURCE_FILTER, crf);
         session.put("page_id", pageId);
         session.put(FilePickerHelper.FILE_PICKER_ATTACHMENTS, files);
         session.put(WHICH_HELPER_KEY, HELPER_PICKER);
         return new ModelAndView("formPicker");
         
      }
      else if (createFormAction != null) {
         setupSessionInfo(request, session, pageId, createFormAction);
         session.put(WHICH_HELPER_KEY, HELPER_CREATOR);
         return new ModelAndView("formCreator");
      }
      else if (viewFormAction != null) {
         setupSessionInfo(request, session, pageId, viewFormAction);
         getSecurityService().pushAdvisor(new AllowMapSecurityAdvisor(
               ContentHostingService.EVENT_RESOURCE_READ,
               (String)request.get("current_form_id")));
         return new ModelAndView("formViewer");
      }
      session.remove(FilePickerHelper.FILE_PICKER_RESOURCE_FILTER);
      return new ModelAndView("page", "page_id", pageId);
   }
   
   protected void setupSessionInfo(Map request, Map session, String pageId, String formId) {
      session.put(ResourceEditingHelper.CREATE_TYPE,
            ResourceEditingHelper.CREATE_TYPE_FORM);
      session.put("page_id", pageId);
      
      
      if (request.get("current_form_id") == null) {
//       CWM fix the parent path
         session.put(ResourceEditingHelper.CREATE_PARENT, "/user/" + 
               getSessionManager().getCurrentSessionUserId() + "/");
         session.put(ResourceEditingHelper.CREATE_SUB_TYPE, formId);
         session.remove(ResourceEditingHelper.ATTACHMENT_ID);
      } else {
         session.put(ResourceEditingHelper.ATTACHMENT_ID, request.get("current_form_id"));
      }
   }

   /**
    * @return Returns the contentHosting.
    */
   public ContentHostingService getContentHosting() {
      return contentHosting;
   }

   /**
    * @param contentHosting The contentHosting to set.
    */
   public void setContentHosting(ContentHostingService contentHosting) {
      this.contentHosting = contentHosting;
   }

   /**
    * @return Returns the entityManager.
    */
   public EntityManager getEntityManager() {
      return entityManager;
   }

   /**
    * @param entityManager The entityManager to set.
    */
   public void setEntityManager(EntityManager entityManager) {
      this.entityManager = entityManager;
   }

   /**
    * @return Returns the sessionManager.
    */
   public SessionManager getSessionManager() {
      return sessionManager;
   }

   /**
    * @param sessionManager The sessionManager to set.
    */
   public void setSessionManager(SessionManager sessionManager) {
      this.sessionManager = sessionManager;
   }

   public SecurityService getSecurityService() {
      return securityService;
   }

   public void setSecurityService(SecurityService securityService) {
      this.securityService = securityService;
   }

}

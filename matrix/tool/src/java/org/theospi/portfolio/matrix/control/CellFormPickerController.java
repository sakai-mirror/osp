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
import org.sakaiproject.exception.IdUnusedException;
import org.sakaiproject.exception.PermissionException;
import org.sakaiproject.exception.TypeException;
import org.sakaiproject.metaobj.shared.mgt.IdManager;
import org.sakaiproject.metaobj.shared.model.Id;
import org.sakaiproject.metaobj.utils.mvc.intf.FormController;
import org.sakaiproject.metaobj.utils.mvc.intf.LoadObjectController;
import org.sakaiproject.service.legacy.content.ContentHostingService;
import org.sakaiproject.service.legacy.entity.EntityManager;
import org.sakaiproject.service.legacy.entity.Reference;
import org.sakaiproject.service.legacy.filepicker.FilePickerHelper;
import org.springframework.validation.Errors;
import org.springframework.web.servlet.ModelAndView;
import org.theospi.portfolio.matrix.MatrixManager;
import org.theospi.portfolio.matrix.model.WizardPageForm;
import org.theospi.portfolio.matrix.model.WizardPage;
import org.theospi.portfolio.shared.tool.BaseFormResourceFilter;

public class CellFormPickerController implements FormController, LoadObjectController {

   protected final Log logger = LogFactory.getLog(getClass());
   private ContentHostingService contentHosting;
   private EntityManager entityManager;
   private SessionManager sessionManager;
   private MatrixManager matrixManager;
   private IdManager idManager = null;
   
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
      
      //ToolSession session = getSessionManager().getCurrentToolSession();
      String pageId = (String) request.get("page_id");
      if (pageId == null) {
         pageId = (String)session.get("page_id");
      }
      WizardPage page = getMatrixManager().getWizardPage(getIdManager().getId(pageId));
      
      if (session.get(FilePickerHelper.FILE_PICKER_CANCEL) == null &&
            session.get(FilePickerHelper.FILE_PICKER_ATTACHMENTS) != null) {
         // here is where we setup the id
         List refs = (List)session.get(FilePickerHelper.FILE_PICKER_ATTACHMENTS);
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
      }
      return null;
   }
   
   public ModelAndView handleRequest(Object requestModel, Map request, Map session, Map application, Errors errors) {
      String attachFormAction = (String) request.get("attachFormAction");
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
            if (wpf.getFormType().equals(attachFormAction)) {
               id = getContentHosting().resolveUuid(wpf.getArtifactId().getValue());
               Reference ref;
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
            }
         }
         BaseFormResourceFilter crf = new BaseFormResourceFilter();
         
         crf.getFormTypes().add(attachFormAction);
         session.put(FilePickerHelper.FILE_PICKER_RESOURCE_FILTER, crf);
         session.put("page_id", pageId);
         session.put(FilePickerHelper.FILE_PICKER_ATTACHMENTS, files);
         return new ModelAndView("formPicker");
         
      }

      return new ModelAndView("page", "page_id", pageId);
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

}

/**********************************************************************************
* $URL$
* $Id$
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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.metaobj.shared.mgt.HomeFactory;
import org.sakaiproject.metaobj.shared.mgt.ReadableObjectHome;
import org.sakaiproject.metaobj.shared.model.InvalidUploadException;
import org.sakaiproject.metaobj.shared.model.OspException;
import org.sakaiproject.metaobj.utils.mvc.intf.Controller;
import org.sakaiproject.metaobj.utils.mvc.intf.FormController;
import org.sakaiproject.api.kernel.session.ToolSession;
import org.sakaiproject.api.kernel.session.SessionManager;
import org.sakaiproject.api.kernel.tool.ToolManager;
import org.sakaiproject.service.legacy.filepicker.FilePickerHelper;
import org.sakaiproject.service.legacy.entity.Reference;
import org.sakaiproject.service.legacy.site.ToolConfiguration;
import org.sakaiproject.service.legacy.site.SiteService;
import org.springframework.validation.Errors;
import org.springframework.web.servlet.ModelAndView;
import org.theospi.portfolio.matrix.MatrixManager;

import org.theospi.portfolio.matrix.model.Scaffolding;
import org.theospi.portfolio.matrix.model.ScaffoldingUploadForm;
import org.theospi.portfolio.shared.model.Node;

import java.util.Map;
import java.util.List;

public class ImportScaffoldingController implements Controller, FormController {
   protected final transient Log logger = LogFactory.getLog(getClass());

   private MatrixManager matrixManager;
   private HomeFactory homeFactory;
   private SessionManager sessionManager;
   private ToolManager toolManager;
   private SiteService siteService;

   public Map referenceData(Map request, Object command, Errors errors) {
      ScaffoldingUploadForm scaffoldingForm = (ScaffoldingUploadForm)command;

      ToolSession session = getSessionManager().getCurrentToolSession();
      if (session.getAttribute(FilePickerHelper.FILE_PICKER_CANCEL) == null &&
            session.getAttribute(FilePickerHelper.FILE_PICKER_ATTACHMENTS) != null) {

         List refs = (List)session.getAttribute(FilePickerHelper.FILE_PICKER_ATTACHMENTS);
         Reference ref = (Reference)refs.get(0);
         scaffoldingForm.setUploadedScaffolding(ref);
         Node file = getMatrixManager().getNode(ref);
         scaffoldingForm.setScaffoldingFileName(file.getDisplayName());
      }

      session.removeAttribute(FilePickerHelper.FILE_PICKER_ATTACHMENTS);
      session.removeAttribute(FilePickerHelper.FILE_PICKER_CANCEL);

      return null;
   }

   public ModelAndView handleRequest(Object requestModel, Map request, Map session,
         Map application, Errors errors) {

      ScaffoldingUploadForm scaffoldingForm = (ScaffoldingUploadForm)requestModel;

      if (scaffoldingForm.getUploadedScaffolding() == null) {
         errors.rejectValue("uploadedScaffolding", "Required", "required");
         return null;
      }

      Scaffolding scaffolding = null;

      try {
         ToolConfiguration toolConfig = getSiteService().findTool(
               getToolManager().getCurrentPlacement().getId());
         scaffolding = getMatrixManager().uploadScaffolding(
              scaffoldingForm.getUploadedScaffolding(), toolConfig);
      } catch (InvalidUploadException e) {
         logger.warn("Failed uploading template", e);
         errors.rejectValue(e.getFieldName(), e.getMessage(), e.getMessage());
         return null;
      } catch (Exception e) {
         logger.error("Failed importing scaffolding", e);
         throw new OspException(e);
      }

      return new ModelAndView("success", "scaffolding_id", scaffolding.getId());
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
    * @return Returns the homeFactory.
    */
   public HomeFactory getHomeFactory() {
      return homeFactory;
   }

   /**
    * @param homeFactory The homeFactory to set.
    */
   public void setHomeFactory(HomeFactory homeFactory) {
      this.homeFactory = homeFactory;
   }

   public SessionManager getSessionManager() {
      return sessionManager;
   }

   public void setSessionManager(SessionManager sessionManager) {
      this.sessionManager = sessionManager;
   }

   public ToolManager getToolManager() {
      return toolManager;
   }

   public void setToolManager(ToolManager toolManager) {
      this.toolManager = toolManager;
   }

   public SiteService getSiteService() {
      return siteService;
   }

   public void setSiteService(SiteService siteService) {
      this.siteService = siteService;
   }
}

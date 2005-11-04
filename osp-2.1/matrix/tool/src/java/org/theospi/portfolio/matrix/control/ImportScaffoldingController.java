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

 * $Header: /opt/CVS/osp2.x/matrix/tool/src/java/org/theospi/portfolio/matrix/control/ImportScaffoldingController.java,v 1.5 2005/10/26 23:53:01 jellis Exp $

 * $Revision$

 * $Date$

 */
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

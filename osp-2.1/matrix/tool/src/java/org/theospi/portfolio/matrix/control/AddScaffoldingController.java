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
 * $Header: /opt/CVS/osp2.x/matrix/tool/src/java/org/theospi/portfolio/matrix/control/AddScaffoldingController.java,v 1.11 2005/10/26 23:53:01 jellis Exp $
 * $Revision$
 * $Date$
 */


package org.theospi.portfolio.matrix.control;

import java.util.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.metaobj.security.AuthenticationManager;
import org.sakaiproject.metaobj.shared.model.Id;
import org.sakaiproject.metaobj.utils.mvc.intf.CustomCommandController;
import org.sakaiproject.metaobj.utils.mvc.intf.FormController;
import org.sakaiproject.metaobj.utils.xml.SchemaFactory;
import org.sakaiproject.metaobj.utils.xml.SchemaNode;
import org.sakaiproject.metaobj.worksite.mgt.WorksiteManager;
import org.sakaiproject.service.legacy.content.ContentHostingService;
import org.sakaiproject.service.legacy.filepicker.FilePickerHelper;
import org.sakaiproject.service.legacy.entity.Reference;
import org.sakaiproject.service.legacy.entity.EntityManager;
import org.sakaiproject.api.kernel.session.SessionManager;
import org.sakaiproject.api.kernel.session.ToolSession;
import org.sakaiproject.exception.IdUnusedException;
import org.sakaiproject.exception.PermissionException;
import org.sakaiproject.exception.TypeException;
import org.springframework.validation.Errors;
import org.springframework.web.servlet.ModelAndView;
import org.theospi.portfolio.matrix.model.Scaffolding;
import org.theospi.portfolio.matrix.model.ScaffoldingCell;
import org.theospi.portfolio.shared.model.Node;


/**
 * @author chmaurer
 */
public class AddScaffoldingController extends BaseScaffoldingController 
   implements FormController, CustomCommandController {

   protected final Log logger = LogFactory.getLog(getClass());
   private WorksiteManager worksiteManager = null;
   private AuthenticationManager authManager = null;
   private SessionManager sessionManager;
   private ContentHostingService contentHosting;
   private EntityManager entityManager;


   /* (non-Javadoc)
    * @see org.theospi.utils.mvc.intf.FormController#referenceData(java.util.Map, java.lang.Object, org.springframework.validation.Errors)
    */
   public Map referenceData(Map request, Object command, Errors errors) {
      Map model = new HashMap();
      Scaffolding scaffolding = (Scaffolding) command;

      ToolSession session = getSessionManager().getCurrentToolSession();
      if (session.getAttribute(FilePickerHelper.FILE_PICKER_CANCEL) == null &&
            session.getAttribute(FilePickerHelper.FILE_PICKER_ATTACHMENTS) != null) {
         // here is where we setup the id
         List refs = (List)session.getAttribute(FilePickerHelper.FILE_PICKER_ATTACHMENTS);
         if (refs.size() == 1) {
            Reference ref = (Reference)refs.get(0);
            scaffolding.setPrivacyXsdId(getMatrixManager().getNode(ref).getId());
         }
         else {
            scaffolding.setPrivacyXsdId(null);
            scaffolding.setDocumentRoot("");
         }
      }
      model.put("isInSession", EditedScaffoldingStorage.STORED_SCAFFOLDING_FLAG);
      
      session.removeAttribute(FilePickerHelper.FILE_PICKER_ATTACHMENTS);
      session.removeAttribute(FilePickerHelper.FILE_PICKER_CANCEL);

      if (scaffolding.getPrivacyXsdId() != null){
         Node schemaFile = getMatrixManager().getNode(scaffolding.getPrivacyXsdId());
         model.put("xsdName",schemaFile.getDisplayName());
         SchemaNode schemaNode = SchemaFactory.getInstance().getSchema(schemaFile.getInputStream());
         model.put("elements", schemaNode.getRootChildren());
      }
      return model;
   }
   

   /* (non-Javadoc)
    * @see org.theospi.utils.mvc.intf.Controller#handleRequest(java.lang.Object, java.util.Map, java.util.Map, java.util.Map, org.springframework.validation.Errors)
    */
   public ModelAndView handleRequest(Object requestModel, Map request, Map session, Map application, Errors errors) {
      String action = (String) request.get("action");
      if (action == null) action = (String) request.get("submitAction");
      String generateAction = (String)request.get("generateAction");
      String cancelAction = (String)request.get("cancelAction");
      
      Id worksiteId = worksiteManager.getCurrentWorksiteId();
      Map model = new HashMap();
      
      EditedScaffoldingStorage sessionBean = (EditedScaffoldingStorage)session.get(
            EditedScaffoldingStorage.EDITED_SCAFFOLDING_STORAGE_SESSION_KEY);
      Scaffolding scaffolding = sessionBean.getScaffolding();
      scaffolding.setWorksiteId(worksiteId);
      scaffolding.setOwnerId(authManager.getAgent().getId());
      
      if (generateAction != null) {
         if (scaffolding.isPublished()) {                              
            return new ModelAndView("editScaffoldingConfirm");             
         }           
         
         saveMatrixTool(scaffolding);
         session.remove(EditedScaffoldingStorage.STORED_SCAFFOLDING_FLAG);
         session.remove(EditedScaffoldingStorage.EDITED_SCAFFOLDING_STORAGE_SESSION_KEY);
         model.put("scaffolding_id", scaffolding.getId());
         return new ModelAndView("view", model);
      }
      if (cancelAction != null) {
         return new ModelAndView("return");
      }
      
      if (action != null) {
         if (action.equals("forward")) {
            String forwardView = (String)request.get("dest");
            model.put("label", request.get("label"));
            model.put("finalDest", request.get("finalDest"));
            model.put("displayText", request.get("displayText"));
            String params = (String)request.get("params");
            model.put("params", params);
            if (!params.equals("")) {
               String[] paramsList = params.split(":");
               for (int i=0; i<paramsList.length; i++) {
                  String[] pair = paramsList[i].split("=");
                  String val = null;
                  if (pair.length>1)
                     val = pair[1];
                  model.put(pair[0], val);
               }
            }
            //matrixManager.storeScaffolding(scaffolding);
            
            //touchAllCells(scaffolding);
            sessionBean.setScaffolding(scaffolding);
            model.put("scaffolding_id", scaffolding.getId());
            if ("pickPrivacy".equals(forwardView)) {
               session.put(EditedScaffoldingStorage.STORED_SCAFFOLDING_FLAG, "true");
               List files = new ArrayList();
               if (scaffolding.getPrivacyXsdId() != null){
                  String id = getContentHosting().resolveUuid(scaffolding.getPrivacyXsdId().getValue());
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
               
               session.put(FilePickerHelper.FILE_PICKER_ATTACHMENTS, files);
               
            }
            return new ModelAndView(forwardView, model);
            
         }
      }
      return new ModelAndView("success");
   }

   private void touchAllScaffolding(Scaffolding scaffolding) {
	  scaffolding.getLevels().size();
 	  scaffolding.getCriteria().size();
 	 for (Iterator iter = scaffolding.getScaffoldingCells().iterator(); iter.hasNext();) {
         ScaffoldingCell sCell = (ScaffoldingCell) iter.next();
         sCell.getCells().size();
         //sCell.getExpectations().size();
      }
   }

   protected void touchAllCells(Scaffolding scaffolding) {
      for (Iterator iter = scaffolding.getScaffoldingCells().iterator(); iter.hasNext();) {
         ScaffoldingCell sCell = (ScaffoldingCell) iter.next();
         sCell.getCells().size();
      }
      
   }

   /**
    * @return Returns the worksiteManager.
    */
   public WorksiteManager getWorksiteManager() {
      return worksiteManager;
   }
   /**
    * @param worksiteManager The worksiteManager to set.
    */
   public void setWorksiteManager(WorksiteManager worksiteManager) {
      this.worksiteManager = worksiteManager;
   }
   /**
    * @return Returns the authManager.
    */
   public AuthenticationManager getAuthManager() {
      return authManager;
   }
   /**
    * @param authManager The authManager to set.
    */
   public void setAuthManager(AuthenticationManager authManager) {
      this.authManager = authManager;
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

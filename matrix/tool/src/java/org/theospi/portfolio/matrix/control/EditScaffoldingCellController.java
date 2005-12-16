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
 * $Header: /opt/CVS/osp2.x/matrix/tool/src/java/org/theospi/portfolio/matrix/control/EditScaffoldingCellController.java,v 1.5 2005/09/29 16:05:48 chmaurer Exp $
 * $Revision$
 * $Date$
 */


package org.theospi.portfolio.matrix.control;

import java.util.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.metaobj.shared.mgt.AgentManager;
import org.sakaiproject.metaobj.utils.mvc.intf.CustomCommandController;
import org.sakaiproject.metaobj.utils.mvc.intf.FormController;
import org.sakaiproject.metaobj.utils.mvc.intf.LoadObjectController;
import org.sakaiproject.metaobj.worksite.mgt.WorksiteManager;
import org.springframework.validation.Errors;
import org.springframework.web.servlet.ModelAndView;
import org.theospi.portfolio.matrix.MatrixFunctionConstants;
import org.theospi.portfolio.matrix.model.ScaffoldingCell;
import org.theospi.portfolio.security.AudienceSelectionHelper;

/**
 * @author chmaurer
 */
public class EditScaffoldingCellController extends BaseScaffoldingCellController
   implements FormController, CustomCommandController, LoadObjectController {

   protected final Log logger = LogFactory.getLog(getClass());
   private WorksiteManager worksiteManager = null;
   private AgentManager agentManager;
   
   /* (non-Javadoc)
    * @see org.theospi.utils.mvc.intf.FormController#referenceData(java.util.Map, java.lang.Object, org.springframework.validation.Errors)
    */
   public Map referenceData(Map request, Object command, Errors errors) {
      Map model = new HashMap();

      //String siteId = getWorksiteManager().getCurrentWorksiteId().getValue();

      //String filter = (String) request.get("filterSelect");
      //if (filter != null) {
      //   List members = getAgentManager().getWorksiteAgents(siteId);
      //   model.put("members", members);
      //   model.put("filterSelect", filter);
      //}

      //model.put("roles", getAgentManager().getWorksiteRoles(siteId));
      model.put("reviewFunction", MatrixFunctionConstants.REVIEW_MATRIX);
      return model;
   }
   /* (non-Javadoc)
    * @see org.theospi.utils.mvc.intf.CustomCommandController#formBackingObject(java.util.Map, java.util.Map, java.util.Map)
    */
   public Object formBackingObject(Map request, Map session, Map application) {
      return new ScaffoldingCell();
   }
   
   
   /* (non-Javadoc)
    * @see org.theospi.utils.mvc.intf.Controller#handleRequest(java.lang.Object, java.util.Map, java.util.Map, java.util.Map, org.springframework.validation.Errors)
    */
   
   public ModelAndView handleRequest(Object requestModel, Map request,
         Map session, Map application, Errors errors) {
      String action = (String) request.get("action");
      if (action  == null) action = (String) request.get("submitAction");
      
      if (action != null && action.length() > 0) {
         Map model = new HashMap();
         
         ScaffoldingCell scaffoldingCell = (ScaffoldingCell) requestModel; 
         
         if (request.get("reviewers") == null) {
            scaffoldingCell.getReviewers().clear();
         }
         
         if (action.equals("Save")) {
            
            if (request.get("gradableReflection") == null) {
               scaffoldingCell.setGradableReflection(false);
            }
            
            if (scaffoldingCell.getScaffolding().isPublished()) {
               model.put("scaffoldingCell", scaffoldingCell);
               model.put(EditedScaffoldingStorage.STORED_SCAFFOLDING_FLAG, "true");
               return new ModelAndView("editScaffoldingCellConfirm", model);
            }
            
            saveScaffoldingCell(request, scaffoldingCell);
         }
         else if (action.equals("forward")) {
            String forwardView = (String)request.get("dest");
            
            EditedScaffoldingStorage sessionBean = (EditedScaffoldingStorage)session.get(
                  EditedScaffoldingStorage.EDITED_SCAFFOLDING_STORAGE_SESSION_KEY);
            sessionBean.setScaffoldingCell(scaffoldingCell);
            model.put("scaffolding_id", scaffoldingCell.getScaffolding().getId());
            model.put("scaffoldingCell_id", scaffoldingCell.getId()); 
            
            if (!forwardView.equals("selectEvaluators")) {
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
            }
            else {
               session.put(EditedScaffoldingStorage.STORED_SCAFFOLDING_FLAG, "true");
               model.put(EditedScaffoldingStorage.STORED_SCAFFOLDING_FLAG, "true");
               setAudienceSelectionVariables(session, scaffoldingCell);        
               
            }

            return new ModelAndView(forwardView, model);
            
         }
         model.put("scaffolding_id", scaffoldingCell.getScaffolding().getId());
         return new ModelAndView("return", model);
      }
      return new ModelAndView("success");
   }
   
   protected void setAudienceSelectionVariables(Map session, ScaffoldingCell sCell) {
      session.put(AudienceSelectionHelper.AUDIENCE_FUNCTION, MatrixFunctionConstants.REVIEW_MATRIX);
      session.put(AudienceSelectionHelper.AUDIENCE_QUALIFIER, sCell.getId().getValue());
      session.put(AudienceSelectionHelper.AUDIENCE_INSTRUCTIONS, "Add evaluators to your cell");
      session.put(AudienceSelectionHelper.AUDIENCE_GLOBAL_TITLE, "Evaluators to Publish to");
      session.put(AudienceSelectionHelper.AUDIENCE_INDIVIDUAL_TITLE, "Publish to an Individual");
      session.put(AudienceSelectionHelper.AUDIENCE_GROUP_TITLE, "Publish to a Group");
      session.put(AudienceSelectionHelper.AUDIENCE_PUBLIC_FLAG, "false");
      session.put(AudienceSelectionHelper.AUDIENCE_PUBLIC_TITLE, "Publish to the Internet");
      session.put(AudienceSelectionHelper.AUDIENCE_SELECTED_TITLE, "Selected Evaluators");
      session.put(AudienceSelectionHelper.AUDIENCE_FILTER_INSTRUCTIONS, "Select filter criteria to narrow user list");
      session.put(AudienceSelectionHelper.AUDIENCE_GUEST_EMAIL, "false");
      session.put(AudienceSelectionHelper.AUDIENCE_WORKSITE_LIMITED, "true");
   }
   
   protected void clearAudienceSelectionVariables(Map session) {
      session.remove(AudienceSelectionHelper.AUDIENCE_FUNCTION);
      session.remove(AudienceSelectionHelper.AUDIENCE_QUALIFIER);
      session.remove(AudienceSelectionHelper.AUDIENCE_INSTRUCTIONS);
      session.remove(AudienceSelectionHelper.AUDIENCE_GLOBAL_TITLE);
      session.remove(AudienceSelectionHelper.AUDIENCE_INDIVIDUAL_TITLE);
      session.remove(AudienceSelectionHelper.AUDIENCE_GROUP_TITLE);
      session.remove(AudienceSelectionHelper.AUDIENCE_PUBLIC_FLAG);
      session.remove(AudienceSelectionHelper.AUDIENCE_PUBLIC_TITLE);
      session.remove(AudienceSelectionHelper.AUDIENCE_SELECTED_TITLE);
      session.remove(AudienceSelectionHelper.AUDIENCE_FILTER_INSTRUCTIONS);
      session.remove(AudienceSelectionHelper.AUDIENCE_GUEST_EMAIL);
      session.remove(AudienceSelectionHelper.AUDIENCE_WORKSITE_LIMITED);
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
    * @return Returns the agentManager.
    */
   public AgentManager getAgentManager() {
      return agentManager;
   }
   /**
    * @param agentManager The agentManager to set.
    */
   public void setAgentManager(AgentManager agentManager) {
      this.agentManager = agentManager;
   }
}

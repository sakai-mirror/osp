/**********************************************************************************
* $URL:https://source.sakaiproject.org/svn/osp/trunk/presentation/tool/src/java/org/theospi/portfolio/presentation/control/CommentListController.java $
* $Id:CommentListController.java 9134 2006-05-08 20:28:42Z chmaurer@iupui.edu $
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
package org.theospi.portfolio.presentation.control;

import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.sakaiproject.metaobj.shared.model.Agent;
import org.sakaiproject.metaobj.utils.mvc.intf.CustomCommandController;
import org.sakaiproject.tool.cover.ToolManager;
import org.springframework.validation.Errors;
import org.springframework.web.servlet.ModelAndView;
import org.theospi.portfolio.presentation.CommentSortBy;
import org.theospi.portfolio.presentation.PresentationManager;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Jun 1, 2004
 * Time: 4:36:32 PM
 * To change this template use File | Settings | File Templates.
 */
public class CommentListController extends AbstractPresentationController implements CustomCommandController {

   private String type = null;

   public ModelAndView handleRequest(Object requestModel, Map request, Map session, Map application, Errors errors) {
      List commentList;
      Agent agent = getAuthManager().getAgent();
      PresentationManager presentationManager = getPresentationManager();
      CommentSortBy sortBy = (CommentSortBy) requestModel;
      String toolId = ToolManager.getCurrentPlacement().getId();
      if (type.equals("owner")) {
         commentList = presentationManager.getOwnerComments(agent, toolId, sortBy, false);
      } else {
         commentList = presentationManager.getCreatorComments(agent, toolId, sortBy);
      }

      Map model = new Hashtable();
      model.put("comments", commentList);
      model.put("sortBy", requestModel);
      model.put("currentAgent", getAuthManager().getAgent());

      return new ModelAndView("success", model);
   }

   public String getType() {
      return type;
   }

   public void setType(String type) {
      this.type = type;
   }

   public Object formBackingObject(Map request, Map session, Map application) {
      return new CommentSortBy();
   }
}

/**********************************************************************************
* $URL:https://source.sakaiproject.org/svn/osp/trunk/presentation/tool/src/java/org/theospi/portfolio/presentation/control/DeleteViewerController.java $
* $Id:DeleteViewerController.java 9134 2006-05-08 20:28:42Z chmaurer@iupui.edu $
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.metaobj.shared.mgt.IdManager;
import org.sakaiproject.metaobj.shared.model.Agent;
import org.sakaiproject.metaobj.shared.model.Id;
import org.springframework.validation.Errors;
import org.springframework.web.servlet.ModelAndView;
import org.theospi.portfolio.presentation.model.Presentation;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Apr 22, 2004
 * Time: 9:58:31 AM
 * To change this template use File | Settings | File Templates.
 */
public class DeleteViewerController extends AbstractPresentationController {
   protected final Log logger = LogFactory.getLog(getClass());
   private IdManager idManager;

   public ModelAndView handleRequest(Object requestModel, Map request, Map session, Map application, Errors errors) {
      String[] viewerIds = new String[1];
      if (request.get("id") instanceof String) {
         viewerIds[0] = (String) request.get("id");
      } else {
         viewerIds = (String[]) request.get("id");
      }

      Presentation presentation = (Presentation) session.get("presentation");

      for (int i = 0; i < viewerIds.length; i++) {
         if (viewerIds[i] != null && viewerIds[i].length() > 0) {
            removeViewer(viewerIds[i], presentation.getViewers());
         }
      }
      return new ModelAndView("success");
   }

   public IdManager getIdManager() {
      return idManager;
   }

   public void setIdManager(IdManager idManager) {
      this.idManager = idManager;
   }

   protected void removeViewer(String idStr, Collection viewers){
      Id id = getIdManager().getId(idStr);
      Agent viewer = getAgentManager().getAgent(id);
      List viewerCopy = new ArrayList(viewers);
      if (viewer != null) {
         viewers.remove(viewer);
      } else {
         //un-created guest users
         for (int i =0; i < viewerCopy.size();i++){
            Agent curViewer = (Agent) viewerCopy.get(i);
            if (curViewer.getDisplayName().equals(idStr)){
               viewers.remove(curViewer);
            }
         }
      }

   }
}

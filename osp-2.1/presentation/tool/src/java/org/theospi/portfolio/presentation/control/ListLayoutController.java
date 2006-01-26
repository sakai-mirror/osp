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
package org.theospi.portfolio.presentation.control;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.validation.Errors;
import org.springframework.web.servlet.ModelAndView;
import org.theospi.portfolio.presentation.model.PresentationLayout;
import org.sakaiproject.metaobj.shared.model.Agent;
import org.sakaiproject.metaobj.utils.mvc.intf.ListScrollIndexer;
import org.sakaiproject.metaobj.utils.mvc.intf.ListScroll;
import org.sakaiproject.service.framework.portal.cover.PortalService;

import java.util.*;

public class ListLayoutController extends AbstractPresentationController {

   protected final Log logger = LogFactory.getLog(getClass());
   private ListScrollIndexer listScrollIndexer;

   public ModelAndView handleRequest(Object requestModel, Map request, Map session,
                                     Map application, Errors errors) {

      Hashtable model = new Hashtable();
      Agent agent = getAuthManager().getAgent();
      List layouts = new ArrayList(
         getPresentationManager().findLayoutsByOwner(agent, PortalService.getCurrentSiteId()));
      layouts.addAll(getPresentationManager().findPublishedLayouts(PortalService.getCurrentSiteId()));
      model.put("layoutCount", String.valueOf(layouts.size()));

      if (request.get("newPresentationLayoutId") != null) {
         request.put(ListScroll.ENSURE_VISIBLE_TAG, "" + getPresentationIndex(layouts,
            (String)request.get("newPresentationLayoutId")));
      }

      layouts = getListScrollIndexer().indexList(request, model, layouts);
      model.put("layouts", layouts);

      model.put("osp_agent", agent);
      String worksiteId = getWorksiteManager().getCurrentWorksiteId().getValue();
      model.put("worksite", getWorksiteManager().getSite(worksiteId));
      model.put("tool", getWorksiteManager().getTool(PortalService.getCurrentToolId()));
      model.put("isMaintainer", isMaintainer());
      return new ModelAndView("success", model);
   }

   protected int getPresentationIndex(List layouts, String layoutId) {
      if (layoutId == null) {
         return 0;
      }

      for (int i=0;i<layouts.size();i++){
         PresentationLayout current = (PresentationLayout)layouts.get(i);
         if (current.getId().getValue().equals(layoutId)) {
            return i;
         }
      }
      return 0;
   }

   public ListScrollIndexer getListScrollIndexer() {
      return listScrollIndexer;
   }

   public void setListScrollIndexer(ListScrollIndexer listScrollIndexer) {
      this.listScrollIndexer = listScrollIndexer;
   }

}

/**********************************************************************************
* $URL:https://source.sakaiproject.org/svn/osp/trunk/presentation/tool/src/java/org/theospi/portfolio/presentation/control/AddViewerController.java $
* $Id:AddViewerController.java 9134 2006-05-08 20:28:42Z chmaurer@iupui.edu $
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import org.sakaiproject.metaobj.shared.mgt.AgentManager;
import org.sakaiproject.metaobj.shared.model.Agent;
import org.sakaiproject.metaobj.shared.model.impl.AgentImpl;
import org.sakaiproject.metaobj.utils.Config;
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;
import org.springframework.web.servlet.ModelAndView;
import org.theospi.portfolio.presentation.model.Presentation;

public class AddViewerController extends AbstractPresentationController {
   
   private static final Pattern emailPattern = Pattern.compile(".*@.*"); 

   public ModelAndView handleRequest(Object requestModel, Map request, Map session, Map application, Errors errors) {
      AgentImpl viewer = (AgentImpl) requestModel;
      List viewers = null;
      boolean isGuest = request.get("isGuest") != null && request.get("isGuest").equals("true");

      if (Config.getInstance().getProperties().getProperty("allowGuests").equals("true") && isGuest){
         viewers = findByEmailOrDisplayName(viewer.getDisplayName());
         if (viewers.size() == 0){
            if (validateEmail(viewer, errors)) {
               viewer.setRole(Agent.ROLE_GUEST);
               viewer.setId(getIdManager().getId(viewer.getDisplayName()));
               viewers.add(viewer);
            }
         }
      } else {
         viewers = getAgentManager().findByProperty(AgentManager.TYPE_DISPLAY_NAME, viewer.getDisplayName());
      }

      if (viewers != null && viewers.size() == 1) {
         Presentation presentation = (Presentation) session.get("presentation");
         presentation.getViewers().add(viewers.get(0));
      } else {
         errors.rejectValue("displayName","unknown user","unknown user: " + viewer.getDisplayName());
      }

      request.put("presentation", session.get("presentation"));
      request.put(BindException.ERROR_KEY_PREFIX + "viewer", errors);
      BindException newErrors = new BindException(session.get("presentation"),"presentation");
      request.put(BindException.ERROR_KEY_PREFIX + "presentation",
         newErrors);

      return new ModelAndView("success", referenceData(request, session.get("presentation"), newErrors));
   }
   
   protected boolean validateEmail(AgentImpl viewer, Errors errors) {
      String email = viewer.getDisplayName();
      if (!emailPattern.matcher(email).matches()) {
         errors.rejectValue("displayName", "Invalid email address",
               new Object[0], "Invalid email address");
         return false;
      }
      
      try {
         InternetAddress.parse(email, true);
      }
      catch (AddressException e) {
         errors.rejectValue("displayName", "Invalid email address",
               new Object[0], "Invalid email address");
         return false;
      }

      return true;
   } 

   public Map referenceData(Map request, Object command, Errors errors){
      Map model = new HashMap();
      model.put("viewer", new AgentImpl());
      //if (!errors.hasFieldErrors("displayName")){
      //   model.put(BindException.ERROR_KEY_PREFIX + "viewer", new BindException(viewer,"viewer"));
      //}
      String siteId = getWorksiteManager().getCurrentWorksiteId().getValue();

      String filter = (String) request.get("filter");
      if (filter != null) {
         List members = getAgentManager().getWorksiteAgents(siteId);
         model.put("members", members);
         model.put("filter", filter);
      }

      model.put("roles", getAgentManager().getWorksiteRoles(siteId));
      return model;
   }

   /**
    *
    * @param displayName - for a guest user, this is the email address
    * @return
    */
   protected List findByEmailOrDisplayName(String displayName) {
      List retVal = new ArrayList();

      List guestUsers = getAgentManager().findByProperty("email", displayName);

      if (guestUsers == null || guestUsers.size() == 0) {
         guestUsers = getAgentManager().findByProperty("displayName", displayName);
      }

      if (guestUsers != null) {
         retVal.addAll(guestUsers);
      }

      return retVal;
   }
}


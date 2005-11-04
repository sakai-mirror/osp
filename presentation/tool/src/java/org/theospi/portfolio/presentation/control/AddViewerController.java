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
 * $Header: /opt/CVS/osp2.x/presentation/tool/src/java/org/theospi/portfolio/presentation/control/AddViewerController.java,v 1.4 2005/09/16 15:34:37 chmaurer Exp $
 * $Revision$
 * $Date$
 */
/*
 * $Header: /opt/CVS/osp2.x/presentation/tool/src/java/org/theospi/portfolio/presentation/control/AddViewerController.java,v 1.4 2005/09/16 15:34:37 chmaurer Exp $
 * $Revision$
 * $Date$
 */
package org.theospi.portfolio.presentation.control;

import org.springframework.validation.BindException;
import org.springframework.validation.Errors;
import org.springframework.web.servlet.ModelAndView;
import org.theospi.portfolio.presentation.model.Presentation;
import org.sakaiproject.metaobj.shared.model.Agent;
import org.sakaiproject.metaobj.shared.model.impl.AgentImpl;
import org.sakaiproject.metaobj.utils.Config;

import javax.mail.internet.InternetAddress;
import javax.mail.internet.AddressException;
import java.util.*;
import java.util.regex.Pattern;

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
         viewers = getAgentManager().findByProperty("displayName", viewer.getDisplayName());
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


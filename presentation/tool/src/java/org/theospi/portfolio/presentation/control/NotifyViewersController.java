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
package org.theospi.portfolio.presentation.control;

import org.springframework.mail.MailSender;
import org.springframework.validation.Errors;
import org.springframework.web.servlet.ModelAndView;
import org.theospi.portfolio.presentation.model.NotificationForm;
import org.theospi.portfolio.presentation.model.Presentation;
import org.sakaiproject.metaobj.shared.model.Agent;
import org.sakaiproject.metaobj.shared.model.Id;
import org.sakaiproject.metaobj.utils.Config;
import org.theospi.portfolio.presentation.model.VelocityMailMessage;
import org.sakaiproject.metaobj.utils.mvc.intf.CustomCommandController;
import org.sakaiproject.metaobj.utils.mvc.intf.ListScrollIndexer;
import org.sakaiproject.component.api.ServerConfigurationService;
import org.sakaiproject.email.cover.EmailService;

import org.sakaiproject.tool.cover.ToolManager;
import org.sakaiproject.user.api.User;
import org.sakaiproject.user.cover.UserDirectoryService;

import java.util.*;

public class NotifyViewersController extends AbstractPresentationController implements CustomCommandController {
   public static final String PARAM_CANCEL = "_cancel";
   private Config ospConfig;
   private VelocityMailMessage mailMessage;
   private MailSender mailer;
   private ListScrollIndexer listScrollIndexer;
   private ServerConfigurationService serverConfigurationService;

   public Object formBackingObject(Map request, Map session, Map application) {
      NotificationForm form = new NotificationForm();
      Id id = getIdManager().getId((String) request.get("presentationId"));
      form.setPresentationId(id);
      request.put("presentation", getPresentationManager().getPresentation(id));
      return form;
   }

   public ModelAndView handleRequest(Object requestModel, Map request, Map session, Map application, Errors errors) {
      NotificationForm form = (NotificationForm) requestModel;
      Agent agent = getAuthManager().getAgent();
      Presentation presentation = getPresentationManager().getPresentation(form.getPresentationId());
      if (request.containsKey(PARAM_CANCEL)) {
         return setupPresentationList(new Hashtable(), request, presentation);
      }

         Map model = new HashMap();
         model.put("owner", agent.getDisplayName());
         model.put("message", form.getMessage());
         model.put("presentation", presentation);
         model.put("osp_agent", agent);
         User user = UserDirectoryService.getCurrentUser();
         
       String url = getServerConfigurationService().getServerUrl() +
                  "/osp-presentation-tool/viewPresentation.osp?id=" + presentation.getId().getValue();

         
         String message = form.getMessage() + 
            "\n" + "****************************************************************" +
            "\n" + user.getDisplayName() + " has shared a Presentation" +
            "\n" + "This Presentation's name is: " + presentation.getName() + 
            "\n" + "Click here to view the presentation: " + url;         
         

      try {
          //getMailMessage().setTo(form.getRecipients());
          //getMailMessage().setModel(model);
          //getMailMessage().setFrom(user.getEmail());
          //getMailer().send(getMailMessage());
         for (int i=0; i<form.getRecipients().length; i++)
         {
            String toUser = form.getRecipients()[i];
            EmailService.send(user.getEmail(), toUser, 
                  getMailMessage().getSubject(), message, null, null, null);
         }

      } catch (Exception e) {
         logger.error("error sending email notification", e);
      }

      return setupPresentationList(model, request, presentation);
   }

   protected ModelAndView setupPresentationList(Map model, Map request, Presentation presentation) {
      model.put("isMaintainer", isMaintainer());

      List presentations = new ArrayList(getPresentationManager().findPresentationsByViewer(getAuthManager().getAgent(),
         ToolManager.getCurrentPlacement().getId()));



      model.put("presentations", getListScrollIndexer().indexList(request, model, presentations));
      model.put("osp_agent", getAuthManager().getAgent());
      
      return new ModelAndView("success", model);
   }

   protected int getPresentationIndex(List presentations, Presentation presentation) {
      if (presentation.getId() == null) {
         return 0;
      }

      for (int i=0;i<presentations.size();i++){
         Presentation current = (Presentation)presentations.get(i);
         if (current.getId().equals(presentation.getId())) {
            return i;
         }
      }
      return 0;
   }

   public ModelAndView processCancel(Map request, Map session, Map application, Object command, Errors errors) throws Exception {
      NotificationForm form = (NotificationForm) command;
      Presentation presentation = getPresentationManager().getPresentation(form.getPresentationId());

      return setupPresentationList(new Hashtable(), request, presentation);
   }

   public Config getOspConfig() {
      return ospConfig;
   }

   public void setOspConfig(Config ospConfig) {
      this.ospConfig = ospConfig;
   }

   public VelocityMailMessage getMailMessage() {
      return mailMessage;
   }

   public void setMailMessage(VelocityMailMessage mailMessage) {
      this.mailMessage = mailMessage;
   }

   public MailSender getMailer() {
      return mailer;
   }

   public void setMailer(MailSender mailer) {
      this.mailer = mailer;
   }

   public Map referenceData(Map request, Object command, Errors errors) {
      return null;  //To change body of implemented methods use File | Settings | File Templates.
   }

   public ListScrollIndexer getListScrollIndexer() {
      return listScrollIndexer;
   }

   public void setListScrollIndexer(ListScrollIndexer listScrollIndexer) {
      this.listScrollIndexer = listScrollIndexer;
   }

   public ServerConfigurationService getServerConfigurationService() {
      return serverConfigurationService;
   }

   public void setServerConfigurationService(
         ServerConfigurationService serverConfigurationService) {
      this.serverConfigurationService = serverConfigurationService;
   }
}

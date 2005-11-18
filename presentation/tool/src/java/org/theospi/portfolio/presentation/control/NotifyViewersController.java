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
 * $Header: /opt/CVS/osp2.x/presentation/tool/src/java/org/theospi/portfolio/presentation/control/NotifyViewersController.java,v 1.4 2005/09/28 14:28:13 chmaurer Exp $
 * $Revision$
 * $Date$
 */
/*
 * $Header: /opt/CVS/osp2.x/presentation/tool/src/java/org/theospi/portfolio/presentation/control/NotifyViewersController.java,v 1.4 2005/09/28 14:28:13 chmaurer Exp $
 * $Revision$
 * $Date$
 */
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
import org.sakaiproject.metaobj.utils.mvc.intf.ListScroll;
import org.sakaiproject.service.framework.config.ServerConfigurationService;
import org.sakaiproject.service.framework.email.cover.EmailService;
import org.sakaiproject.service.framework.portal.cover.PortalService;
import org.sakaiproject.service.legacy.user.User;
import org.sakaiproject.service.legacy.user.cover.UserDirectoryService;

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
         model.put("owner", agent);
         model.put("message", form.getMessage());
         model.put("presentation", presentation);
         
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
         PortalService.getCurrentToolId()));

      request.put(ListScroll.ENSURE_VISIBLE_TAG, "" + getPresentationIndex(presentations, presentation));

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

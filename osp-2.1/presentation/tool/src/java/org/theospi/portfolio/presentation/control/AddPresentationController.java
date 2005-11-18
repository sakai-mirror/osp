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
 * $Header: /opt/CVS/osp2.x/presentation/tool/src/java/org/theospi/portfolio/presentation/control/AddPresentationController.java,v 1.8 2005/09/28 14:09:40 chmaurer Exp $
 * $Revision$
 * $Date$
 */

package org.theospi.portfolio.presentation.control;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.metaobj.security.AuthenticationManager;
import org.sakaiproject.metaobj.shared.mgt.AgentManager;
import org.sakaiproject.metaobj.shared.mgt.HomeFactory;
import org.sakaiproject.metaobj.shared.mgt.IdManager;
import org.sakaiproject.metaobj.shared.model.Agent;
import org.sakaiproject.metaobj.shared.model.ElementBean;
import org.sakaiproject.metaobj.shared.model.Id;
import org.sakaiproject.metaobj.shared.model.impl.AgentImpl;
import org.sakaiproject.metaobj.utils.Config;
import org.sakaiproject.metaobj.utils.mvc.impl.servlet.ServletRequestBeanDataBinder;
import org.sakaiproject.metaobj.utils.mvc.intf.ListScroll;
import org.sakaiproject.metaobj.utils.mvc.intf.ListScrollIndexer;
import org.sakaiproject.metaobj.utils.mvc.intf.TypedPropertyEditor;
import org.sakaiproject.metaobj.utils.xml.SchemaFactory;
import org.sakaiproject.metaobj.utils.xml.SchemaNode;
import org.sakaiproject.metaobj.worksite.mgt.WorksiteManager;
import org.sakaiproject.service.framework.config.ServerConfigurationService;
import org.sakaiproject.service.framework.portal.cover.PortalService;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractWizardFormController;
import org.springframework.web.util.WebUtils;
import org.theospi.portfolio.presentation.PresentationFunctionConstants;
import org.theospi.portfolio.presentation.PresentationManager;
import org.theospi.portfolio.presentation.model.Presentation;
import org.theospi.portfolio.presentation.model.PresentationItem;
import org.theospi.portfolio.presentation.model.PresentationItemDefinition;
import org.theospi.portfolio.presentation.model.PresentationTemplate;
import org.theospi.portfolio.security.AuthorizationFacade;
import org.theospi.portfolio.shared.model.Node;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.SimpleDateFormat;
import java.util.*;

public class AddPresentationController extends AbstractWizardFormController {
   public static final String PARAM_FINISH_AND_NOTIFY = "_finish_notify";
   public static final String PARAM_RESET_FORM = "resetForm";

   final public static int INITIAL_PAGE = 0;
   final public static int PROPERTY_PAGE = 1;
   final public static int PRESENTATION_ITEMS = 2;
   final public static int PRESENTATION_AUTHORIZATIONS = 3;
   final public static int PRESENTATION_NOTIFICATIONS = 4;

   private IdManager idManager;
   private List customTypedEditors = new ArrayList();
   private AgentManager agentManager;
   private PresentationManager presentationManager;
   private AuthenticationManager authManager;
   private HomeFactory homeFactory;
   protected final Log logger = LogFactory.getLog(getClass());
   private AuthorizationFacade authzManager = null;
   private Config ospConfig;
   private WorksiteManager worksiteManager;
   private ListScrollIndexer listScrollIndexer;
   private ServerConfigurationService serverConfigurationService;

   public Object formBackingObject(HttpServletRequest request) throws Exception {
      Presentation presentation = new Presentation();
      presentation.setTemplate(new PresentationTemplate());

      // this is an edit, load model
      if (request.getParameter("id") != null) {
         Id id = getIdManager().getId(request.getParameter("id"));
         presentation = getPresentationManager().getPresentation(id);
         getAuthzManager().checkPermission(PresentationFunctionConstants.EDIT_PRESENTATION,
            presentation.getId());
      } else {
         getAuthzManager().checkPermission(PresentationFunctionConstants.CREATE_PRESENTATION,
            getIdManager().getId(PortalService.getCurrentToolId()));
      }

      if (request.getParameter("templateId") != null) {
         presentation.setTemplate(getPresentationManager().getPresentationTemplate(getIdManager().getId(request.getParameter("templateId"))));

         presentation.setName(presentation.getTemplate().getName());
         presentation.setDescription(presentation.getTemplate().getDescription());
      }

      return presentation;
   }

   protected String getFormSessionAttributeName() {
      return getCommandName();
   }
      
   protected void onBindAndValidate(HttpServletRequest request, Object command, BindException errors, int page)
         throws Exception {
      Presentation presentation = (Presentation) command;

      if (page == PRESENTATION_AUTHORIZATIONS){
         if (request.getParameter("viewers") == null) {
            presentation.setViewers(new ArrayList());
         }
         if (request.getParameter("isPublic") == null) {
            presentation.setIsPublic(false);
         }
      }
      if (page == INITIAL_PAGE && request.getParameter("isDefault") == null) {
         presentation.setIsDefault(false);
      }
   }

   protected void initBinder(HttpServletRequest request, ServletRequestDataBinder binder) {
      SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
      dateFormat.setLenient(false);
      binder.registerCustomEditor(Date.class, null, new CustomDateEditor(dateFormat, true));

      for (Iterator i = getCustomTypedEditors().iterator(); i.hasNext();) {
         TypedPropertyEditor editor = (TypedPropertyEditor) i.next();
         binder.registerCustomEditor(editor.getType(), editor);
      }
   }

   protected ServletRequestDataBinder createBinder(HttpServletRequest request, Object command) throws Exception {
      ServletRequestDataBinder binder = null;
      binder = new ServletRequestBeanDataBinder(command, getCommandName());
      initBinder(request, binder);

      return binder;
   }

   /**
    * loads a schema from a file id in the repository.  Puts
    * the technical metadata for the schema file in the model
    *
    * @param xsdId - id of the xsd file in the repository
    * @return schema
    */
   protected SchemaNode loadSchema(Id xsdId, Map model) {
      //RepositoryNode rNode = (RepositoryNode) getRepositoryManager().getNode(xsdId);
      Node rNode = (Node) getPresentationManager().getNode(xsdId);
      model.put("propertyFileMetadata", rNode.getTechnicalMetadata());
      SchemaFactory schemaFactory = SchemaFactory.getInstance();
      return schemaFactory.getSchema(rNode.getInputStream());
   }

   protected Map referenceData(HttpServletRequest request, int page) throws Exception {
      Map model = new HashMap();
      Presentation presentation = (Presentation) request.getSession().getAttribute(getCommandName());

      String baseUrl = this.getServerConfigurationService().getServerUrl();
      model.put("baseUrl", baseUrl);

      model.put("currentPage", getCurrentPageNumber(presentation, page));
      model.put("totalPages", getTotalPages(presentation,  page));
      model.put("allowGuests", Config.getInstance().getProperties().getProperty("allowGuests"));

      if (page == INITIAL_PAGE) {
         Agent agent = getAuthManager().getAgent();
         model.put("templates", getPresentationManager().findTemplatesByOwner(agent, PortalService.getCurrentSiteId()));
         model.put("publishedTemplates", getPresentationManager().findPublishedTemplates(PortalService.getCurrentSiteId()));
         return model;
      }
      if (page == PROPERTY_PAGE) {
         PresentationTemplate template = getPresentationManager().getPresentationTemplate(presentation.getTemplate().getId());
         presentation.setTemplate(template);

         SchemaNode schema = loadSchema(presentation.getTemplate().getPropertyPage(), model).getChild(presentation.getTemplate().getDocumentRoot());

         //make sure schema for properties is set correctly for both add, or edit
         if (presentation.getProperties() == null) {
            presentation.setProperties(new ElementBean(presentation.getTemplate().getDocumentRoot(),
                  schema));
         } else {
            presentation.getProperties().setCurrentSchema(schema);
         }
      }
      if (page == PRESENTATION_ITEMS) {
         Map artifacts = new HashMap();
         Map artifactCache = new HashMap();
         Agent agent = getAuthManager().getAgent();

         PresentationTemplate template = getPresentationManager().getPresentationTemplate(presentation.getTemplate().getId());
         presentation.setTemplate(template);

         model.put("types", template.getSortedItems());
         for (Iterator i = template.getSortedItems().iterator(); i.hasNext();) {
            PresentationItemDefinition itemDef = (PresentationItemDefinition) i.next();

            if (artifactCache.containsKey(itemDef.getType()) && !itemDef.getHasMimeTypes()){
               artifacts.put(itemDef.getId().getValue(), artifactCache.get(itemDef.getType()));
            } else {
               Collection itemArtifacts = getPresentationManager().loadArtifactsForItemDef(itemDef, agent);

               if (!itemDef.getHasMimeTypes()) {
                  // cache only full list
                  artifactCache.put(itemDef.getType(), itemArtifacts);
               }
               artifacts.put(itemDef.getId().getValue(), itemArtifacts);
            }
         }
         model.put("artifacts", artifacts);
         Collection items = new ArrayList();
         for (Iterator i = presentation.getPresentationItems().iterator(); i.hasNext();) {
            PresentationItem item = (PresentationItem) i.next();
            items.add(item.getDefinition().getId().getValue() + "." +
                  item.getArtifactId());
         }
         model.put("items", items);
         return model;
      }
      if (page == PRESENTATION_AUTHORIZATIONS) {
         Agent viewer = new AgentImpl();

         model.put("viewer", new AgentImpl());
         model.put(BindException.ERROR_KEY_PREFIX + "viewer", new BindException(viewer,"viewer"));
         String siteId = getWorksiteManager().getCurrentWorksiteId().getValue();

         String filter = request.getParameter("filterSelect");
         if (filter != null) {
            List members = getAgentManager().getWorksiteAgents(siteId);
            model.put("members", members);
            model.put("filterSelect", filter);
         }

         model.put("roles", getAgentManager().getWorksiteRoles(siteId));
      }
      return model;
   }

   protected Integer getTotalPages(Presentation presentation, int page) {
      if (presentation.getTemplate().getPropertyPage() == null) {
         return new Integer(4);
      }
      else {
         return new Integer(5);
      }
   }

   protected Integer getCurrentPageNumber(Presentation presentation, int page) {
      if (page == 0 || getTotalPages(presentation, page).intValue() == 5) {
         return new Integer(page + 1);
      }
      else {
         // skipping two
         return new Integer(page);
      }
   }

   protected int getTargetPage(HttpServletRequest request, Object command, Errors errors, int currentPage) {
      if (logger.isDebugEnabled()) {
         logger.debug("getTargetPage()");
      }

      if (errors.getErrorCount() > 0){
         return currentPage;
      }

      int target = super.getTargetPage(request, command, errors, currentPage);

      if (target == PROPERTY_PAGE) {
         Id templateId = ((Presentation) command).getTemplate().getId();
         Id propId = presentationManager.getPresentationTemplate(templateId).getPropertyPage();

         if (propId == null && currentPage == INITIAL_PAGE && target > currentPage)
            return target + 1;
         if (propId == null && currentPage == PRESENTATION_ITEMS && target < currentPage)
            return target - 1;
      }

      return target;
   }

   protected boolean isFormSubmission(HttpServletRequest request) {

      Presentation pres =
            (Presentation) request.getSession().getAttribute(getCommandName());

      if (pres == null) {
         return false;
      }

      if (isFinish(request) || isNext(request) ||
            WebUtils.hasSubmitParameter(request, PARAM_CANCEL)) {
         return true;
      }
/*
      if (request.getParameter(PARAM_RESET_FORM) != null &&
            (pres.getId() != null || request.getParameter("id") != null ||
            request.getParameter("templateId") != null)) {
         return false;
      }
  */
      if (request.getParameter(PARAM_RESET_FORM) != null) {
         return false;
      }

      return true;
   }

   protected boolean isNext(HttpServletRequest request) {
//      String direction = request.getParameter("direction");
//      return ("next".equals(direction) || "previous".equals(direction));
//         return true;
      Enumeration enumer = request.getParameterNames();

      while (enumer.hasMoreElements()) {
         String param = (String) enumer.nextElement();
         if (param.startsWith(PARAM_TARGET)) {
            return true;
         }
      }

      return false;
   }

   protected boolean isFinish(HttpServletRequest request) {
      return WebUtils.hasSubmitParameter(request, PARAM_FINISH) ||
            WebUtils.hasSubmitParameter(request, PARAM_FINISH_AND_NOTIFY);
   }

   protected void validatePage(Object model, Errors errors, int page) {
      PresentationValidator validator = (PresentationValidator) getValidator();
      switch (page) {
         case INITIAL_PAGE:
            validator.validatePresentationFirstPage(model, errors);
            break;
         case PROPERTY_PAGE:
            Presentation presentation = (Presentation) model;
            if (presentation.getProperties() != null) {
               SchemaNode schemaNode = presentation.getProperties().getCurrentSchema();
               if (schemaNode == null) {
                  schemaNode = loadSchema(presentation.getTemplate().getPropertyPage(), new HashMap()).getChild(presentation.getTemplate().getDocumentRoot());
                  presentation.getProperties().setCurrentSchema(schemaNode);
               }
               validator.validatePresentationProperties(presentation, errors);
            }
            break;
         case PRESENTATION_ITEMS:
            validator.validatePresentationSecondPage(model, errors);
            break;
         case PRESENTATION_AUTHORIZATIONS:
            validator.validatePresentationThirdPage(model, errors);
            break;
      }
   }

   protected ModelAndView processFinish(HttpServletRequest httpServletRequest,
                                        HttpServletResponse httpServletResponse,
                                        Object o, BindException e) throws Exception {
      Presentation presentation = (Presentation) o;
      Agent agent = getAuthManager().getAgent();

      //don't do this for an edit
      if (presentation.getId() == null){
         presentation.setOwner(agent);
      }

      if (presentation.getTemplate().getPropertyPage() == null){
         presentation.setProperties(null);
      }
      presentation.setToolId(PortalService.getCurrentToolId());
      getPresentationManager().storePresentation(presentation);

      httpServletRequest.getSession().removeAttribute(getCommandName());
      if (WebUtils.hasSubmitParameter(httpServletRequest, PARAM_FINISH_AND_NOTIFY)) {
         Map params = new HashMap();
         params.put("presentationId", presentation.getId().getValue());
         return new ModelAndView("notifyViewersRedirect", params);
      }

      Map model = new Hashtable();

      model.put("isMaintainer", isMaintainer());
      model.put("newPresentationId", presentation.getId());

      List presentations = new ArrayList(getPresentationManager().findPresentationsByViewer(getAuthManager().getAgent(),
         PortalService.getCurrentToolId()));

      Map request = new Hashtable();
      request.put(ListScroll.ENSURE_VISIBLE_TAG, "" + getPresentationIndex(presentations, presentation));

      model.put("presentations", getListScrollIndexer().indexList(request, model, presentations));
      model.put("osp_agent", getAuthManager().getAgent());

      return new ModelAndView("listPresentation", model);

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

   protected ModelAndView processCancel(HttpServletRequest httpServletRequest,
                                        HttpServletResponse httpServletResponse,
                                        Object o, BindException e) throws Exception {
      Presentation presentation = (Presentation) o;

      Map model = new Hashtable();

      model.put("isMaintainer", isMaintainer());

      List presentations = new ArrayList(getPresentationManager().findPresentationsByViewer(getAuthManager().getAgent(),
         PortalService.getCurrentToolId()));

      Map request = new Hashtable();
      request.put(ListScroll.ENSURE_VISIBLE_TAG, "" + getPresentationIndex(presentations, presentation));

      model.put("presentations", getListScrollIndexer().indexList(request, model, presentations));
      model.put("osp_agent", getAuthManager().getAgent());
      
      return new ModelAndView("listPresentation", model);
   }

   /**
    *
    * @return true is current agent is a maintainer in the current site
    */
   protected Boolean isMaintainer(){
      return new Boolean(getAuthzManager().isAuthorized(WorksiteManager.WORKSITE_MAINTAIN,
            getIdManager().getId(PortalService.getCurrentSiteId())));
   }

   public PresentationManager getPresentationManager() {
      return presentationManager;
   }

   public void setPresentationManager(PresentationManager presentationManager) {
      this.presentationManager = presentationManager;
   }

   public AuthenticationManager getAuthManager() {
      return authManager;
   }

   public void setAuthManager(AuthenticationManager authManager) {
      this.authManager = authManager;
   }

   public AgentManager getAgentManager() {
      return agentManager;
   }

   public void setAgentManager(AgentManager agentManager) {
      this.agentManager = agentManager;
   }

   public HomeFactory getHomeFactory() {
      return homeFactory;
   }

   public void setHomeFactory(HomeFactory homeFactory) {
      this.homeFactory = homeFactory;
   }

   public List getCustomTypedEditors() {
      return customTypedEditors;
   }

   public void setCustomTypedEditors(List customTypedEditors) {
      this.customTypedEditors = customTypedEditors;
   }

   public IdManager getIdManager() {
      return idManager;
   }

   public void setIdManager(IdManager idManager) {
      this.idManager = idManager;
   }

   public AuthorizationFacade getAuthzManager() {
      return authzManager;
   }

   public void setAuthzManager(AuthorizationFacade authzManager) {
      this.authzManager = authzManager;
   }

   public Config getOspConfig() {
      return ospConfig;
   }

   public void setOspConfig(Config ospConfig) {
      this.ospConfig = ospConfig;
   }

   public WorksiteManager getWorksiteManager() {
      return worksiteManager;
   }

   public void setWorksiteManager(WorksiteManager worksiteManager) {
      this.worksiteManager = worksiteManager;
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


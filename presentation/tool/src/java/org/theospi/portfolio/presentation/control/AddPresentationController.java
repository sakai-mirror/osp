/**********************************************************************************
* $URL:https://source.sakaiproject.org/svn/osp/trunk/presentation/tool/src/java/org/theospi/portfolio/presentation/control/AddPresentationController.java $
* $Id:AddPresentationController.java 9134 2006-05-08 20:28:42Z chmaurer@iupui.edu $
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

import java.text.SimpleDateFormat;
import java.util.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.component.api.ServerConfigurationService;
import org.sakaiproject.metaobj.security.AuthenticationManager;
import org.sakaiproject.metaobj.shared.mgt.AgentManager;
import org.sakaiproject.metaobj.shared.mgt.HomeFactory;
import org.sakaiproject.metaobj.shared.mgt.IdManager;
import org.sakaiproject.metaobj.shared.model.Agent;
import org.sakaiproject.metaobj.shared.model.ElementBean;
import org.sakaiproject.metaobj.shared.model.Id;
import org.sakaiproject.metaobj.utils.Config;
import org.sakaiproject.metaobj.utils.mvc.impl.servlet.ServletRequestBeanDataBinder;
import org.sakaiproject.metaobj.utils.mvc.intf.ListScroll;
import org.sakaiproject.metaobj.utils.mvc.intf.ListScrollIndexer;
import org.sakaiproject.metaobj.utils.mvc.intf.TypedPropertyEditor;
import org.sakaiproject.metaobj.utils.xml.SchemaFactory;
import org.sakaiproject.metaobj.utils.xml.SchemaNode;
import org.sakaiproject.metaobj.worksite.mgt.WorksiteManager;
import org.sakaiproject.tool.api.ToolSession;
import org.sakaiproject.tool.api.Tool;
import org.sakaiproject.tool.cover.SessionManager;
import org.sakaiproject.tool.cover.ToolManager;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractWizardFormController;
import org.springframework.web.util.WebUtils;
import org.theospi.portfolio.presentation.PresentationFunctionConstants;
import org.theospi.portfolio.presentation.PresentationManager;
import org.theospi.portfolio.presentation.intf.FreeFormHelper;
import org.theospi.portfolio.presentation.model.Presentation;
import org.theospi.portfolio.presentation.model.PresentationItem;
import org.theospi.portfolio.presentation.model.PresentationItemDefinition;
import org.theospi.portfolio.presentation.model.PresentationTemplate;
import org.theospi.portfolio.security.AudienceSelectionHelper;
import org.theospi.portfolio.security.AuthorizationFacade;
import org.theospi.portfolio.shared.model.Node;

public class AddPresentationController extends AbstractWizardFormController {
   public static final String PARAM_FINISH_AND_NOTIFY = "_finish_notify";
   public static final String PARAM_RESET_FORM = "resetForm";

   final public static int ADD_PAGE = 0;
   final public static int INITIAL_PAGE = 1;
   final public static int PROPERTY_PAGE = 2;
   final public static int PRESENTATION_ITEMS = 3;
   final public static int PRESETATION_FREE_FORM_PAGES = 4;
   final public static int PRESENTATION_AUTHORIZATIONS = 5;
   final public static int PRESENTATION_NOTIFICATIONS = 6;

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
   private int initialPage = 0;

   public Object formBackingObject(HttpServletRequest request) throws Exception {
      Presentation presentation = new Presentation();
      presentation.setTemplate(new PresentationTemplate());

      presentation.setToolId(ToolManager.getCurrentPlacement().getId());
      
      // this is an edit, load model
      if (request.getParameter("id") != null) {
         setInitialPage(1);
         Id id = getIdManager().getId(request.getParameter("id"));
         presentation = getPresentationManager().getPresentation(id);
         getAuthzManager().checkPermission(PresentationFunctionConstants.EDIT_PRESENTATION,
            presentation.getId());
      } else {
         setInitialPage(0);
         getAuthzManager().checkPermission(PresentationFunctionConstants.CREATE_PRESENTATION,
            getIdManager().getId(ToolManager.getCurrentPlacement().getId()));
         presentation.setId(getIdManager().createId());
         presentation.setNewObject(true);
         presentation.setSiteId(ToolManager.getCurrentPlacement().getContext());
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

      if (presentation.getName() != null && page == 0)
      {
          page++;
      }
      String baseUrl = this.getServerConfigurationService().getServerUrl();
      model.put("baseUrl", baseUrl);
      model.put("currentPage", getCurrentPageNumber(presentation, page));
      model.put("totalPages", getTotalPages(presentation,  page));
      model.put("allowGuests", Config.getInstance().getProperties().getProperty("allowGuests"));

      if (page == ADD_PAGE) {
         Agent agent = getAuthManager().getAgent();
         model.put("templates", getPresentationManager().findTemplatesByOwner(agent, ToolManager.getCurrentPlacement().getContext()));
         model.put("publishedTemplates", getPresentationManager().findPublishedTemplates(ToolManager.getCurrentPlacement().getContext()));
         model.put("globalPublishedTemplates", getPresentationManager().findGlobalTemplates());
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
      if (page == PRESETATION_FREE_FORM_PAGES) {
         if (presentation.getPages() == null) {
            presentation.setPages(
                  getPresentationManager().getPresentationPagesByPresentation(presentation.getId()));
         }

         if (presentation.getPages().size() == 0) {
            presentation.setPages(new ArrayList());
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

       setAudienceSelectionVariables(request.getSession(), presentation);
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
      boolean hasProperties = getTotalPages(presentation, page).intValue() == 4;
      if (page == ADD_PAGE) {
          return new Integer(1);
      }
      else if (page == INITIAL_PAGE) {
         return new Integer(2);
      }
      else if (page == PROPERTY_PAGE) {
         return new Integer(3);
      }
      else if (page == PRESENTATION_ITEMS || page == PRESETATION_FREE_FORM_PAGES) {
         return new Integer(hasProperties?4:3);
      }
      else if (page == PRESENTATION_AUTHORIZATIONS) {
         return new Integer(hasProperties?5:4);
      }
      else {
         return new Integer(0);
      }
   }

   protected int getTargetPage(HttpServletRequest request, Object command, Errors errors, int currentPage) {
      if (logger.isDebugEnabled()) {
         logger.debug("getTargetPage()");
      }

      if (errors.getErrorCount() > 0){
         return currentPage;
      }

      Presentation presentation = ((Presentation) command);
      int target = super.getTargetPage(request, command, errors, currentPage);

      if (target == PROPERTY_PAGE &&
            presentation.getPresentationType().equals(Presentation.FREEFORM_TYPE)) {
         return PRESETATION_FREE_FORM_PAGES;
      }

      if (target == PRESETATION_FREE_FORM_PAGES) {
         if (presentation.getPresentationType().equals(Presentation.FREEFORM_TYPE)) {
            ToolSession session = SessionManager.getCurrentToolSession();
            String action = (String) session.getAttribute(FreeFormHelper.FREE_FORM_ACTION);
            if (action != null) {
               if (action.equals(FreeFormHelper.ACTION_BACK)) {
                  session.removeAttribute(FreeFormHelper.FREE_FORM_ACTION);
                  target -= hasProperties(presentation)?2:3;
               }
               else if (action.equals(FreeFormHelper.ACTION_CONTINUE)) {
                  session.removeAttribute(FreeFormHelper.FREE_FORM_ACTION);
                  target++;
               }
            }
         }
         else {
            // skip free form
            if (target > currentPage)
               return target + 1;
            if (target < currentPage)
               return target - 1;
         }
      }

      if (target == PROPERTY_PAGE) {
         boolean hasProperties = hasProperties(presentation);

         if (!hasProperties && currentPage == INITIAL_PAGE && target > currentPage)
            return target + 1;
         if (!hasProperties && currentPage == PRESENTATION_ITEMS && target < currentPage)
            return target - 1;
      }

      return target;
   }

   protected boolean hasProperties(Presentation presentation) {
      Id templateId = presentation.getTemplate().getId();
      Id propId = presentationManager.getPresentationTemplate(templateId).getPropertyPage();
      boolean hasProperties = propId != null;
      return hasProperties;
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
      ToolSession session = SessionManager.getCurrentToolSession();
      String action = (String) session.getAttribute(FreeFormHelper.FREE_FORM_ACTION);

      if (action != null) {
         if (action.equals(FreeFormHelper.ACTION_SAVE)) {
            return true;
         }
      }

    return WebUtils.hasSubmitParameter(request, PARAM_FINISH) ||
            WebUtils.hasSubmitParameter(request, PARAM_FINISH_AND_NOTIFY);


   }

   protected boolean isCancelRequest(HttpServletRequest request) {
      ToolSession session = SessionManager.getCurrentToolSession();
      String action = (String) session.getAttribute(FreeFormHelper.FREE_FORM_ACTION);
      if (action != null) {
         if (action.equals(FreeFormHelper.ACTION_CANCEL)) {
            return true;
         }
      }
      return super.isCancelRequest(request);
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
      ToolSession session = SessionManager.getCurrentToolSession();
      session.removeAttribute(FreeFormHelper.FREE_FORM_ACTION);
      Presentation presentation = (Presentation) o;
      Agent agent = getAuthManager().getAgent();
      String isPublic = (String) session.getAttribute(AudienceSelectionHelper.AUDIENCE_PUBLIC_FLAG);
      if (isPublic != null && !isPublic.equals("")) {
          presentation.setIsPublic(isPublic.equals("true") ? true : false);
      }
      List viewers =  (List) session.getAttribute("PRESENTATION_VIEWERS");
      if (viewers != null) {
          presentation.setViewers(viewers);
          session.removeAttribute("PRESENTATION_VIEWERS");
      }

      //don't do this for an edit
      if (presentation.getId() == null){
         presentation.setOwner(agent);
      }

      if (presentation.getTemplate().getPropertyPage() == null){
         presentation.setProperties(null);
      }
      presentation.setToolId(ToolManager.getCurrentPlacement().getId());
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
            ToolManager.getCurrentPlacement().getId()));

      Map request = new Hashtable();
      request.put(ListScroll.ENSURE_VISIBLE_TAG, "" + getPresentationIndex(presentations, presentation));

      model.put("presentations", getListScrollIndexer().indexList(request, model, presentations));
      model.put("osp_agent", getAuthManager().getAgent());

      return new ModelAndView("listPresentationRedirect", model);

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
      ToolSession session = SessionManager.getCurrentToolSession();
      session.removeAttribute(FreeFormHelper.FREE_FORM_ACTION);
      Presentation presentation = (Presentation) o;

      Map model = new Hashtable();

      model.put("isMaintainer", isMaintainer());

      List presentations = new ArrayList(getPresentationManager().findPresentationsByViewer(getAuthManager().getAgent(),
            ToolManager.getCurrentPlacement().getId()));

      Map request = new Hashtable();
      request.put(ListScroll.ENSURE_VISIBLE_TAG, "" + getPresentationIndex(presentations, presentation));

      model.put("presentations", getListScrollIndexer().indexList(request, model, presentations));
      model.put("osp_agent", getAuthManager().getAgent());

      return new ModelAndView("listPresentationRedirect", model);
   }
    protected void setAudienceSelectionVariables(HttpSession session, Presentation pres) {
      String baseUrl = this.getServerConfigurationService().getServerUrl();
      String url =  baseUrl + "/osp-presentation-tool/viewPresentation.osp?id=" + pres.getId().getValue();
      url += "&" + Tool.PLACEMENT_ID + "=" + SessionManager.getCurrentToolSession().getPlacementId();

      ResourceBundle myResources =
         ResourceBundle.getBundle("org.theospi.portfolio.presentation.bundle.Messages");
      session.setAttribute(AudienceSelectionHelper.AUDIENCE_FUNCTION, "osp.presentation.view");

      String id = pres.getId()!=null ? pres.getId().getValue() : pres.getNewId().getValue();
      session.setAttribute(AudienceSelectionHelper.AUDIENCE_PORTFOLIO_WIZARD, "true");
      session.setAttribute(AudienceSelectionHelper.AUDIENCE_QUALIFIER, id);
      session.setAttribute(AudienceSelectionHelper.AUDIENCE_GLOBAL_TITLE,
            pres.isNewObject()? myResources.getString("title_addPortfolio") : myResources.getString("title_editPresentation1"));
      session.setAttribute(AudienceSelectionHelper.AUDIENCE_INSTRUCTIONS,
            myResources.getString("instructions_addViewersToPresentation"));
      session.setAttribute(AudienceSelectionHelper.AUDIENCE_GROUP_TITLE,
            myResources.getString("instructions_publishToGroup"));

      session.setAttribute(AudienceSelectionHelper.AUDIENCE_INDIVIDUAL_TITLE,
            myResources.getString("instructions_publishToIndividual"));

      session.setAttribute(AudienceSelectionHelper.AUDIENCE_PUBLIC_FLAG, pres.getIsPublic() ? "true" : "false");
      session.setAttribute(AudienceSelectionHelper.AUDIENCE_PUBLIC_TITLE, myResources.getString("instructions_publishToInternet"));
      session.setAttribute(AudienceSelectionHelper.AUDIENCE_SELECTED_TITLE,
            myResources.getString("instructions_selectedAudience"));
      session.setAttribute(AudienceSelectionHelper.AUDIENCE_FILTER_INSTRUCTIONS,
            myResources.getString("instructions_selectFilterUserList"));
      session.setAttribute(AudienceSelectionHelper.AUDIENCE_GUEST_EMAIL, "true");
      session.setAttribute(AudienceSelectionHelper.AUDIENCE_WORKSITE_LIMITED, "false");
      session.setAttribute(AudienceSelectionHelper.AUDIENCE_PUBLIC_INSTRUCTIONS,
              myResources.getString("publish_message"));
      session.setAttribute(AudienceSelectionHelper.AUDIENCE_PUBLIC_URL,  url);

      session.setAttribute(AudienceSelectionHelper.AUDIENCE_CANCEL_TARGET, PARAM_CANCEL);
      session.setAttribute(AudienceSelectionHelper.AUDIENCE_SAVE_NOTIFY_TARGET, PARAM_FINISH_AND_NOTIFY);
      session.setAttribute(AudienceSelectionHelper.AUDIENCE_SAVE_TARGET, PARAM_FINISH);
      session.setAttribute(AudienceSelectionHelper.AUDIENCE_BACK_TARGET, PARAM_TARGET +
              (pres.getPresentationType().equals(Presentation.FREEFORM_TYPE)?2:3));
      session.setAttribute(AudienceSelectionHelper.AUDIENCE_BROWSE_INDIVIDUAL,
            myResources.getString("audience_browse_individual"));
   }


   /**
    *
    * @return true is current agent is a maintainer in the current site
    */
   protected Boolean isMaintainer(){
      return new Boolean(getAuthzManager().isAuthorized(WorksiteManager.WORKSITE_MAINTAIN,
            getIdManager().getId(ToolManager.getCurrentPlacement().getContext())));
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

    protected int getInitialPage(HttpServletRequest request) {
        return initialPage;
    }
    private void setInitialPage(int page){
        initialPage = page;
    }
}


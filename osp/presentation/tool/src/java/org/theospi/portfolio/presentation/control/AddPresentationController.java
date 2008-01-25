/**********************************************************************************
* $URL:https://source.sakaiproject.org/svn/osp/trunk/presentation/tool/src/java/org/theospi/portfolio/presentation/control/AddPresentationController.java $
* $Id:AddPresentationController.java 9134 2006-05-08 20:28:42Z chmaurer@iupui.edu $
***********************************************************************************
*
* Copyright (c) 2005, 2006, 2007 The Sakai Foundation.
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
import org.sakaiproject.content.api.ContentCollection;
import org.sakaiproject.content.api.ContentCollectionEdit;
import org.sakaiproject.content.api.ContentHostingService;
import org.sakaiproject.content.api.ContentResource;
import org.sakaiproject.content.api.ResourceEditingHelper;
import org.sakaiproject.entity.api.ResourceProperties;
import org.sakaiproject.exception.IdUnusedException;
import org.sakaiproject.exception.IdUsedException;
import org.sakaiproject.exception.PermissionException;
import org.sakaiproject.exception.TypeException;
import org.sakaiproject.metaobj.security.AuthenticationManager;
import org.sakaiproject.metaobj.shared.FormHelper;
import org.sakaiproject.metaobj.shared.mgt.AgentManager;
import org.sakaiproject.metaobj.shared.mgt.HomeFactory;
import org.sakaiproject.metaobj.shared.mgt.IdManager;
import org.sakaiproject.metaobj.shared.model.Agent;
import org.sakaiproject.metaobj.shared.model.Id;
import org.sakaiproject.metaobj.utils.Config;
import org.sakaiproject.metaobj.utils.mvc.impl.servlet.ServletRequestBeanDataBinder;
import org.sakaiproject.metaobj.utils.mvc.intf.ListScroll;
import org.sakaiproject.metaobj.utils.mvc.intf.ListScrollIndexer;
import org.sakaiproject.metaobj.utils.mvc.intf.TypedPropertyEditor;
import org.sakaiproject.metaobj.utils.xml.SchemaFactory;
import org.sakaiproject.metaobj.utils.xml.SchemaNode;
import org.sakaiproject.metaobj.worksite.mgt.WorksiteManager;
import org.sakaiproject.site.cover.SiteService;
import org.sakaiproject.tool.api.Placement;
import org.sakaiproject.tool.api.ToolSession;
import org.sakaiproject.tool.api.Tool;
import org.sakaiproject.tool.cover.SessionManager;
import org.sakaiproject.tool.cover.ToolManager;
import org.sakaiproject.user.api.User;
import org.sakaiproject.user.cover.UserDirectoryService;
import org.sakaiproject.util.ResourceLoader;
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
   
   final public static String SESSION_PAGE_PROP_ENTRY = "portfolio.pageProp.entry";
   final public static String SESSION_PAGE_PROP_DIRECTION = "portfolio.pageProp.direction";
   final public static int PAGE_DIRECTION_FORWARD = 1;
   final public static int PAGE_DIRECTION_BACKWARD = -1;

	private static ResourceLoader myResources = new ResourceLoader(PresentationManager.PRESENTATION_MESSAGE_BUNDLE);
   
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
   private ContentHostingService contentHosting = null;
   private int initialPage = 0;

   public Object formBackingObject(HttpServletRequest request) throws Exception {
      Presentation presentation = new Presentation();
      presentation.setTemplate(new PresentationTemplate());

      // this is an edit, load model
      if (request.getParameter("id") != null) {
         int page = parseTarget(request.getParameter("target"));
         setInitialPage(page);
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
         presentation.setToolId(ToolManager.getCurrentPlacement().getId());
      }

      if (request.getParameter("templateId") != null) {
         presentation.setTemplate(getPresentationManager().getPresentationTemplate(getIdManager().getId(request.getParameter("templateId"))));

         presentation.setName(presentation.getTemplate().getName());
         presentation.setDescription(presentation.getTemplate().getDescription());
      }

      return presentation;
   }
   
   /**
    * 
    * @param target The target parameter from the request string - Like "_target3"
    * @return The number at the end of the "_target".  If it happens to be 
    * something else, an exception is caught and 1 is returned 
    */
   protected int parseTarget(String target) {
      try {
         if (target.startsWith(PARAM_TARGET)) {
            String retTar = target.substring(PARAM_TARGET.length(), target.length());
            return Integer.parseInt(retTar);
         }
      }
      catch (Exception e) {
         //In case something goes wrong, just return 1
         return 1;
      }
      return 1;
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

      if ("true".equals(request.getParameter("preview"))) {
         model.put("preview", true);
      }
      
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

         String formTypeId = template.getPropertyFormType().getValue();
         HttpSession session = request.getSession();

         session.setAttribute(ResourceEditingHelper.CREATE_TYPE,
               ResourceEditingHelper.CREATE_TYPE_FORM);
         
         Node propFormNode = getPresentationManager().getNode(presentation.getPropertyForm());
         
         if (template.getPropertyFormType() != null) {
            String nodeId = propFormNode == null ? null : propFormNode.getResource().getId();
            setupSessionInfoForPropertyForm(session, presentation.getName(), formTypeId, nodeId);
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
   
   protected void setupSessionInfoForPropertyForm(HttpSession session, String presentationTitle, 
         String formTypeId, String currentFormId) {
      
      if (currentFormId == null) {
         session.removeAttribute(ResourceEditingHelper.ATTACHMENT_ID);
         session.setAttribute(ResourceEditingHelper.CREATE_TYPE,
               ResourceEditingHelper.CREATE_TYPE_FORM);
         session.setAttribute(ResourceEditingHelper.CREATE_SUB_TYPE, formTypeId);

         String propFormText = myResources.getString("propertyForm");
         List contentResourceList = null;
         try {
            String folderBase = getUserCollection().getId();
            
            Placement placement = ToolManager.getCurrentPlacement();
            String currentSite = placement.getContext();
            
            String rootDisplayName = myResources.getString(PresentationManager.PORTFOLIO_INTERACTION_FOLDER_DISPNAME);
            String rootDescription = myResources.getString(PresentationManager.PORTFOLIO_INTERACTION_FOLDER_DESC);
            
            String folderPath = createFolder(folderBase, "portfolio-interaction", rootDisplayName, rootDescription);
            folderPath = createFolder(folderPath, currentSite, SiteService.getSiteDisplay(currentSite), null);
            
            String dispName = 
               myResources.getString(PresentationManager.PRESENTATION_PROPERTIES_FOLDER_DISPNAME);
            String desc = myResources.getString(PresentationManager.PRESENTATION_PROPERTIES_FOLDER_DESC);
            
            folderPath = createFolder(folderPath, 
                  PresentationManager.PRESENTATION_PROPERTIES_FOLDER, 
                  dispName, desc);
            
            contentResourceList = this.getContentHosting().getAllResources(folderPath);
            
            session.setAttribute(FormHelper.PARENT_ID_TAG, folderPath);
         } catch (TypeException e) {
            throw new RuntimeException("Failed to redirect to helper", e);
         } catch (IdUnusedException e) {
            throw new RuntimeException("Failed to redirect to helper", e);
         } catch (PermissionException e) {
            throw new RuntimeException("Failed to redirect to helper", e);
         }
         
         //CWM OSP-UI-09 - for auto naming
         session.setAttribute(FormHelper.NEW_FORM_DISPLAY_NAME_TAG, getFormDisplayName(presentationTitle, propFormText, 1, contentResourceList));
      } else {
         //session.put(ResourceEditingHelper.ATTACHMENT_ID, request.get("current_form_id"));
         session.removeAttribute(ResourceEditingHelper.CREATE_TYPE);
         session.removeAttribute(ResourceEditingHelper.CREATE_SUB_TYPE);
         session.removeAttribute(ResourceEditingHelper.CREATE_PARENT);
         session.setAttribute(ResourceEditingHelper.CREATE_TYPE,
            ResourceEditingHelper.CREATE_TYPE_FORM);
         session.setAttribute(ResourceEditingHelper.ATTACHMENT_ID, currentFormId);
      }
   }
   
   /**
    * 
    * @param objectTitle
    * @param formTypeName
    * @param count: this keeps track of the number of times getFormDisplayName is called for naming reasons
    * @param contentResourceList: a list of the resources for looking up the names to compare to the new name
    * @return
    */
   protected String getFormDisplayName(String objectTitle, String formTypeName, int count, List contentResourceList) {
	   String name = objectTitle + "-" + formTypeName;

	   if(count > 1){
		   name = name + " (" + count + ")";
	   }

	   count++;

	   return formDisplayNameExists(name, contentResourceList) && contentResourceList != null ? 
			   getFormDisplayName(objectTitle, formTypeName, count, contentResourceList) : name;

   }

   /**
    * 
    * @param name
    * @param contentResourceList
    * @return
    * 
    * returns true if the name passed exists in the list of contentResource
    * otherwise returns false
    */
   protected boolean formDisplayNameExists(String name, List contentResourceList){


	   if(contentResourceList != null){
		   ContentResource cr;
		   for(int i = 0; i < contentResourceList.size(); i++){
			   cr = (ContentResource) contentResourceList.get(i);
			   if(name.equals(cr.getProperties().getProperty(cr.getProperties().getNamePropDisplayName()).toString())){
				   return true;
			   }
		   }
	   }

	   return false;
   }
   
   
   protected String createFolder(String base, String append, String appendDisplay, String appendDescription) {
      //String folder = "/user/" + 
      //SessionManager.getCurrentSessionUserId() + 
      //PresentationManager.PRESENTATION_PROPERTIES_FOLDER_PATH;
      String folder = base + append + "/";

      try {
         ContentCollectionEdit propFolder = getContentHosting().addCollection(folder);
         propFolder.getPropertiesEdit().addProperty(ResourceProperties.PROP_DISPLAY_NAME, appendDisplay);
         propFolder.getPropertiesEdit().addProperty(ResourceProperties.PROP_DESCRIPTION, appendDescription);
         getContentHosting().commitCollection(propFolder);
         return propFolder.getId();
      }
      catch (IdUsedException e) {
         // ignore... it is already there.
      }
      catch (Exception e) {
         throw new RuntimeException(e);
      }
      return folder;
   }
   
   protected ContentCollection getUserCollection() throws TypeException, IdUnusedException, PermissionException {
      User user = UserDirectoryService.getCurrentUser();
      String userId = user.getId();
      String wsId = SiteService.getUserSiteId(userId);
      String wsCollectionId = getContentHosting().getSiteCollection(wsId);
      ContentCollection collection = getContentHosting().getCollection(wsCollectionId);
      return collection;
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
            if (target > currentPage) {
               return target + 1;
            }
            if (target < currentPage) {
               return target - 1;
            }
         }
      }

      if (target == PROPERTY_PAGE) {
         HttpSession session = request.getSession();
         boolean hasProperties = hasProperties(presentation);

         if (!hasProperties && currentPage == INITIAL_PAGE && target > currentPage) {
            return target + 1;
         }
         if (!hasProperties && currentPage == PRESENTATION_ITEMS && target < currentPage) {
            return target - 1;
         }
         if (hasProperties && 
               FormHelper.RETURN_ACTION_SAVE.equals((String)session.getAttribute(FormHelper.RETURN_ACTION_TAG)) && 
               session.getAttribute(FormHelper.RETURN_REFERENCE_TAG) != null) {
            String artifactId = (String)session.getAttribute(FormHelper.RETURN_REFERENCE_TAG);
            
            Node node = getPresentationManager().getNode(getIdManager().getId(artifactId));
            presentation.setPropertyForm(node.getId());
            
            session.removeAttribute(FormHelper.RETURN_REFERENCE_TAG);
            session.removeAttribute(FormHelper.RETURN_ACTION_TAG);
            session.removeAttribute(SESSION_PAGE_PROP_ENTRY);
            return getDirection(session, target, false);
         }
         
         if (hasProperties && 
               (FormHelper.RETURN_ACTION_CANCEL.equals((String)session.getAttribute(FormHelper.RETURN_ACTION_TAG)) || 
                     session.getAttribute(SESSION_PAGE_PROP_ENTRY) != null)) {
            session.removeAttribute(FormHelper.RETURN_ACTION_TAG);
            session.removeAttribute(SESSION_PAGE_PROP_ENTRY);
            return getDirection(session, target, true);
         }
         
         //I think if we get here, we just want to go to the prop page...
         session.setAttribute(SESSION_PAGE_PROP_ENTRY, "true");
         if (currentPage < target) {
            session.setAttribute(SESSION_PAGE_PROP_DIRECTION, new Integer(PAGE_DIRECTION_FORWARD));
         }
         else if (currentPage > target) {
            session.setAttribute(SESSION_PAGE_PROP_DIRECTION, new Integer(PAGE_DIRECTION_BACKWARD));
         }
      }

      return target;
   }
   
   protected int getDirection(HttpSession session, int target, boolean cancelSwitch) {
      int cancelSwitcher = cancelSwitch ? -1 : 1;
      int direction = PAGE_DIRECTION_FORWARD;
      try {
         direction = ((Integer)session.getAttribute(SESSION_PAGE_PROP_DIRECTION)).intValue();
      }
      catch (Exception e) {
         logger.warn("Couldn't get direction.  Moving to target: " + (target + (direction * cancelSwitcher)));
         //return target + (direction * cancelSwitcher);
      }
      session.removeAttribute(SESSION_PAGE_PROP_DIRECTION);
      return target + (direction * cancelSwitcher);
   }

   protected boolean hasProperties(Presentation presentation) {
      Id templateId = presentation.getTemplate().getId();
      Id propId = presentationManager.getPresentationTemplate(templateId).getPropertyFormType();
      boolean hasProperties = propId != null;
      return hasProperties;
   }

   protected boolean isFormSubmission(HttpServletRequest request) {

      Presentation pres =
            (Presentation) request.getSession().getAttribute(getCommandName());

      if (pres == null) {
         return false;
      }

      if (isFinishRequest(request) || isNext(request) ||
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

   protected boolean isFinishRequest(HttpServletRequest request) {
      ToolSession session = SessionManager.getCurrentToolSession();
      String action = (String) session.getAttribute(FreeFormHelper.FREE_FORM_ACTION);

      if (action != null) {
         if (action.equals(FreeFormHelper.ACTION_SAVE)) {
            return true;
         }
      }
      
      //Since allow comments checkbox saves a "false" (uncheck) as null value,
      //a session variable allowComments keeps track of true and false.  
      //Only set it if this value hasn't been set yet.
      if(session.getAttribute("allowComments") == null){
    	  if (request.getParameter("allowComments") == null || request.getParameter("allowComments").equals("false")) {
    		  session.setAttribute("allowComments", "false");
    	  }else{
    		  session.setAttribute("allowComments", "true");
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
      
      //This is if the form helper was a cancel...
      if (FormHelper.RETURN_ACTION_CANCEL.equals((String)session.getAttribute(FormHelper.RETURN_ACTION_TAG))) {
         session.removeAttribute(FormHelper.RETURN_ACTION_TAG);
         session.removeAttribute(SESSION_PAGE_PROP_ENTRY);
         return true;
      }
      
      return super.isCancelRequest(request);
   }

   protected void validatePage(Object model, Errors errors, int page) {
      PresentationValidator validator = (PresentationValidator) getValidator();
      switch (page) {
         case ADD_PAGE:
            validator.validatePresentationInitialPage(model, errors);
            break;
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
         presentation.setToolId(ToolManager.getCurrentPlacement().getId());
      }

      if (presentation.getTemplate().getPropertyPage() == null){
         presentation.setProperties(null);
      }

      //Since allow comments checkbox saves a "false" (uncheck) as null value,
      //a session variable allowComments keeps track of true and false (set in
      //isFinishedRequest)
      
      if(session.getAttribute("allowComments") != null){
    	  if(session.getAttribute("allowComments").equals("true")){
    		  presentation.setAllowComments(true);
    	  }else{
    		  presentation.setAllowComments(false);  
    	  }
    	  session.removeAttribute("allowComments");
      }

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

      session.setAttribute(AudienceSelectionHelper.AUDIENCE_FUNCTION, 
                           AudienceSelectionHelper.AUDIENCE_FUNCTION_PORTFOLIO );

      String id = pres.getId()!=null ? pres.getId().getValue() : pres.getNewId().getValue();
      session.setAttribute(AudienceSelectionHelper.AUDIENCE_QUALIFIER, id);
		session.setAttribute(AudienceSelectionHelper.AUDIENCE_SITE, pres.getSiteId());
      
      session.setAttribute(AudienceSelectionHelper.AUDIENCE_PUBLIC_FLAG, pres.getIsPublic() ? "true" : "false");
      session.setAttribute(AudienceSelectionHelper.AUDIENCE_PUBLIC_URL,  url);

      session.setAttribute(AudienceSelectionHelper.AUDIENCE_CANCEL_TARGET, PARAM_CANCEL);
      session.setAttribute(AudienceSelectionHelper.AUDIENCE_SAVE_NOTIFY_TARGET, PARAM_FINISH_AND_NOTIFY);
      session.setAttribute(AudienceSelectionHelper.AUDIENCE_SAVE_TARGET, PARAM_FINISH);
      session.setAttribute(AudienceSelectionHelper.AUDIENCE_BACK_TARGET, PARAM_TARGET +
              (pres.getPresentationType().equals(Presentation.FREEFORM_TYPE)?2:3));
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

   /**
    * @return the contentHosting
    */
   public ContentHostingService getContentHosting() {
      return contentHosting;
   }

   /**
    * @param contentHosting the contentHosting to set
    */
   public void setContentHosting(ContentHostingService contentHosting) {
      this.contentHosting = contentHosting;
   }
}


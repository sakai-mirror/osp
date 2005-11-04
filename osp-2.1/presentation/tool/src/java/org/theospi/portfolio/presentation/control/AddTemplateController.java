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
 * $Header: /opt/CVS/osp2.x/presentation/tool/src/java/org/theospi/portfolio/presentation/control/AddTemplateController.java,v 1.9 2005/10/26 23:53:01 jellis Exp $
 * $Revision$
 * $Date$
 */
/*
 * $Header: /opt/CVS/osp2.x/presentation/tool/src/java/org/theospi/portfolio/presentation/control/AddTemplateController.java,v 1.9 2005/10/26 23:53:01 jellis Exp $
 * $Revision$
 * $Date$
 */
package org.theospi.portfolio.presentation.control;

import org.sakaiproject.service.framework.portal.cover.PortalService;
import org.sakaiproject.service.legacy.content.ContentHostingService;
import org.sakaiproject.service.legacy.filepicker.FilePickerHelper;
import org.sakaiproject.service.legacy.entity.Reference;
import org.sakaiproject.service.legacy.entity.EntityManager;
import org.springframework.validation.Errors;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractWizardFormController;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.theospi.portfolio.presentation.model.PresentationTemplate;
import org.theospi.portfolio.presentation.model.PresentationItemDefinition;
import org.theospi.portfolio.presentation.model.TemplateFileRef;
import org.theospi.portfolio.presentation.PresentationManager;
import org.theospi.portfolio.presentation.PresentationFunctionConstants;
import org.sakaiproject.api.kernel.session.SessionManager;
import org.sakaiproject.api.kernel.session.ToolSession;
import org.sakaiproject.metaobj.shared.model.Agent;
import org.sakaiproject.metaobj.shared.model.Id;
import org.sakaiproject.metaobj.shared.mgt.IdManager;
import org.sakaiproject.metaobj.shared.mgt.HomeFactory;
import org.sakaiproject.metaobj.security.AuthenticationManager;
import org.theospi.portfolio.security.AuthorizationFacade;
import org.theospi.portfolio.shared.model.Node;
import org.sakaiproject.metaobj.worksite.mgt.WorksiteManager;
import org.sakaiproject.metaobj.utils.mvc.intf.TypedPropertyEditor;
import org.sakaiproject.metaobj.utils.xml.SchemaNode;
import org.sakaiproject.metaobj.utils.xml.SchemaFactory;
import org.sakaiproject.metaobj.utils.xml.SchemaInvalidException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;
import java.text.SimpleDateFormat;

public class AddTemplateController extends AbstractWizardFormController {
   final public static int DESCRIBE_PAGE = 0;
   final public static int TEMPLATE_PAGE = 1;
   final public static int CONTENT_PAGE = 2;
   final public static int FILES_PAGE = 3;
   final public static int PICKER_PAGE = 4;
   
   public static final String TEMPLATE_RENDERER = "osp.presentation.template.renderer";
   public static final String TEMPLATE_PROPERTYFILE = "osp.presentation.template.propertyFile";
   public static final String TEMPLATE_SUPPORTFILE = "osp.presentation.template.supportFile";
   public static final String TEMPLATE_PICKER = "osp.presentation.template.picker";
   private static final String STARTING_PAGE = "osp.presentation.template.startingPage";


   private WorksiteManager worksiteManager;
   private AuthenticationManager authManager;
   private PresentationManager presentationManager;
   private List customTypedEditors;
   private AuthorizationFacade authzManager;
   private IdManager idManager;
   private HomeFactory homeFactory;
   private Collection mimeTypes;
   private SessionManager sessionManager;
   private ContentHostingService contentHosting;
   private EntityManager entityManager;

   protected ModelAndView processFinish(HttpServletRequest request, HttpServletResponse response, Object o, BindException e) throws Exception {
      PresentationTemplate template = (PresentationTemplate) o;
      Agent agent = getAuthManager().getAgent();
      template.setOwner(agent);
      template.setToolId(PortalService.getCurrentToolId());
      template.setSiteId(PortalService.getCurrentSiteId());

      // remove id's from new dependent object, so hibernate doesn't freak out
      //removeTemporaryIds(template);

      template = getPresentationManager().storeTemplate(template);

      Map model = new Hashtable();
      model.put("newPresentationTemplateId", template.getId().getValue());

      return new ModelAndView("listTemplateRedirect", model);
   }

   protected void removeTemporaryIds(PresentationTemplate template){
      PresentationTemplate oldTemplate = new PresentationTemplate();
      if (template.getId() != null && template.getId().getValue().length() > 0){
         oldTemplate = getPresentationManager().getPresentationTemplate(template.getId());
      }

      for (Iterator i= template.getItems().iterator();i.hasNext();){
         PresentationItemDefinition item = (PresentationItemDefinition) i.next();
         if (!oldTemplate.getItems().contains(item)){
            item.setId(null);
         }
      }

      for (Iterator i= template.getFiles().iterator();i.hasNext();){
         TemplateFileRef file = (TemplateFileRef) i.next();
         if (!oldTemplate.getFiles().contains(file)){
            file.setId(null);
         }
      }
   }

   public Object formBackingObject(HttpServletRequest request) throws Exception {
      PresentationTemplate template = new PresentationTemplate();

      // this is an edit, load model
      if (request.getParameter("id") != null) {
         Id id = getIdManager().getId(request.getParameter("id"));
         getAuthzManager().checkPermission(PresentationFunctionConstants.EDIT_TEMPLATE, id);
         template = getPresentationManager().getPresentationTemplate(id);
      } else {
         getAuthzManager().checkPermission(PresentationFunctionConstants.CREATE_TEMPLATE,
               getIdManager().getId(PortalService.getCurrentToolId()));
      }
      ToolSession session = getSessionManager().getCurrentToolSession();
      if (session.getAttribute("SessionPresentationTemplate") != null) {
         template = (PresentationTemplate)session.getAttribute("SessionPresentationTemplate");
         session.removeAttribute("SessionPresentationTemplate");
         request.setAttribute(STARTING_PAGE, Integer.valueOf((String)session.getAttribute(STARTING_PAGE)));
         session.removeAttribute(STARTING_PAGE);
      }
      
      return template;
   }
   
   

   protected ModelAndView processCancel(HttpServletRequest request, HttpServletResponse response, Object command, BindException errors) {
      PresentationTemplate template = (PresentationTemplate)command;
      Map model = new Hashtable();
      if (template.getId() != null) {
         model.put("newPresentationTemplateId", template.getId().getValue());
      }

      return new ModelAndView("listTemplateRedirect", model);
   }

   protected void onBindAndValidate(javax.servlet.http.HttpServletRequest request,
                                 java.lang.Object command,
                                 BindException errors,
                                 int page)
                          throws java.lang.Exception {

   }

   protected void validatePage(Object model, Errors errors, int page) {
      PresentationValidator validator = (PresentationValidator) getValidator();
      switch (page) {
         case DESCRIBE_PAGE:
            validator.validateTemplateFirstPage(model, errors);
            break;
         case TEMPLATE_PAGE:
            if (((PresentationTemplate)model).isValidate()) {
               validator.validateTemplateSecondPage(model, errors);
            }
            break;
         case CONTENT_PAGE:
            validator.validateTemplateThirdPage(model, errors);
            break;
         case FILES_PAGE:
            validator.validateTemplateFourthPage(model, errors);
            break;
      }
   }

   protected Map referenceData(HttpServletRequest request,
                               Object command,
                               Errors errors,
                               int page)
                        throws Exception{
      Map model = new HashMap();
      PresentationTemplate template = (PresentationTemplate) command;
      model.put("currentPage", new Integer(page + 1));
      model.put("totalPages", new Integer(4));
      model.put("template", template);
      ToolSession session = getSessionManager().getCurrentToolSession();
      
      
      
      model.put("STARTING_PAGE", STARTING_PAGE);

      switch (page) {
         case DESCRIBE_PAGE :
            break;
         case TEMPLATE_PAGE :
            model.put("TEMPLATE_RENDERER", TEMPLATE_RENDERER);
            model.put("TEMPLATE_PROPERTYFILE", TEMPLATE_PROPERTYFILE);
        	 //ToolSession session = getSessionManager().getCurrentToolSession();
             if (session.getAttribute(FilePickerHelper.FILE_PICKER_CANCEL) == null &&
                   session.getAttribute(FilePickerHelper.FILE_PICKER_ATTACHMENTS) != null) {
                // here is where we setup the id
                List refs = (List)session.getAttribute(FilePickerHelper.FILE_PICKER_ATTACHMENTS);
                
                Id nodeId = null;
                String nodeName = "";
                
                if (refs.size() == 1) {
                   Reference ref = (Reference)refs.get(0);
                   Node node = getPresentationManager().getNode(ref);
                   nodeId = node.getId();
                   nodeName = node.getDisplayName();
                }
                if (session.getAttribute(TEMPLATE_PICKER).equals(TEMPLATE_RENDERER)) {
                   template.setRendererName(nodeName);
                   template.setRenderer(nodeId);
                }
                else {
                   template.setPropertyPageName(nodeName);
                   template.setPropertyPage(nodeId);
                }
                
                session.removeAttribute(TEMPLATE_PICKER);
                session.removeAttribute(FilePickerHelper.FILE_PICKER_ATTACHMENTS);
                session.removeAttribute(FilePickerHelper.FILE_PICKER_CANCEL);
             }
        	 
        	 
            if (template.getRenderer() != null){
               Node artifact = (Node) getPresentationManager().getNode(template.getRenderer());
               model.put("rendererName",artifact.getDisplayName());
            }
            if (template.getPropertyPage() != null){
               Node artifact = (Node) getPresentationManager().getNode(template.getPropertyPage());
               SchemaNode schemaNode;
               try {
                  schemaNode = SchemaFactory.getInstance().getSchema(artifact.getInputStream());
                  model.put("propertyPageName",artifact.getDisplayName());
                  model.put("elements", schemaNode.getRootChildren());
               }
               catch (SchemaInvalidException e) {
                  template.setPropertyPage(null);
                  String errorMessage = "Invalid outline properties file: " + e.getMessage();
                  errors.rejectValue("propertyPage", errorMessage, errorMessage);
               }
            }
            break;
         case CONTENT_PAGE :
            Collection mimeTypes = getMimeTypes();
            model.put("mimeTypeListSize", new Integer(mimeTypes.size()));
            model.put("mimeTypeList", mimeTypes);
            model.put("homes", getHomes());            
            break;
         case FILES_PAGE :
            model.put("TEMPLATE_SUPPORTFILE", TEMPLATE_SUPPORTFILE);
            
            if (session.getAttribute(FilePickerHelper.FILE_PICKER_CANCEL) == null &&
                  session.getAttribute(FilePickerHelper.FILE_PICKER_ATTACHMENTS) != null) {
               // here is where we setup the id
               
               String fileId = "";
               String nodeName = "";
               String fileType = "";
               List refs = (List)session.getAttribute(FilePickerHelper.FILE_PICKER_ATTACHMENTS);
               if (refs.size() == 1) {
                  Reference ref = (Reference)refs.get(0);
                  Node node = getPresentationManager().getNode(ref);
                  fileId = node.getId().getValue();
                  nodeName = node.getDisplayName();
                  fileType = node.getFileType();
               }
               if (session.getAttribute(TEMPLATE_PICKER).equals(TEMPLATE_SUPPORTFILE)) {
                  template.getFileRef().setFileId(fileId);
                  template.getFileRef().setArtifactName(nodeName);
                  template.getFileRef().setFileType(fileType);
               }
            
               session.removeAttribute(TEMPLATE_PICKER);
               session.removeAttribute(FilePickerHelper.FILE_PICKER_ATTACHMENTS);
               session.removeAttribute(FilePickerHelper.FILE_PICKER_CANCEL);
            }
            break;
         case PICKER_PAGE :            
            session.setAttribute(TEMPLATE_PICKER, request.getParameter("pickerField"));
            session.setAttribute("SessionPresentationTemplate", template);
            session.setAttribute(STARTING_PAGE, request.getParameter("returnPage"));
            
            List files = new ArrayList();
            
            String pickField = (String)request.getParameter("pickerField");
            String id = "";
            if (pickField.equals(TEMPLATE_RENDERER) && template.getRenderer() != null) {
               id = getContentHosting().resolveUuid(template.getRenderer().getValue());
            }
            else if (pickField.equals(TEMPLATE_PROPERTYFILE) && template.getPropertyPage() != null) {
               id = getContentHosting().resolveUuid(template.getPropertyPage().getValue());
            }
            else if (pickField.equals(TEMPLATE_SUPPORTFILE) && template.getFileRef() != null && template.getFileRef().getFileId() != null) {
               id = getContentHosting().resolveUuid(template.getFileRef().getFileId());
            }
            if (id != null && !id.equals("")) {
               Reference ref = getEntityManager().newReference(getContentHosting().getResource(id).getReference());
               files.add(ref);              
               session.setAttribute(FilePickerHelper.FILE_PICKER_ATTACHMENTS, files);
            }
            
            break;
      }
      return model;

   }

   protected Collection getHomes() {
      return getHomeFactory().getWorksiteHomes(
         getWorksiteManager().getCurrentWorksiteId()).entrySet();
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

	protected int getTargetPage(HttpServletRequest request, Object command, Errors errors, int currentPage) {
		int retVal = super.getTargetPage(request, command, errors, currentPage);
      if (isFormSubmission(request)){
         onSubmit(request, command, errors, currentPage);
      }
      return retVal;
	}

   protected int getInitialPage(HttpServletRequest request, Object command) {
      Integer startingPage = (Integer)request.getAttribute(STARTING_PAGE);
      if (startingPage != null) {
         request.removeAttribute(STARTING_PAGE);
         return startingPage.intValue();
      }
      else {
         return super.getInitialPage(request, command);
      }
   }

   /**
    * perform page specific business logic after bind and validate
    * @param request
    * @param command
    * @param errors
    * @param currentPage - page just submitted
    */
   protected void onSubmit(HttpServletRequest request, Object command, Errors errors, int currentPage){
      PresentationTemplate template = (PresentationTemplate) command;
      switch (currentPage) {
         case CONTENT_PAGE :
            // save add item to backing object
            if (template.getItem().getAction() != null &&
                  template.getItem().getAction().equalsIgnoreCase("addItem") &&
                  !errors.hasErrors() ) {

               PresentationItemDefinition itemDefinition = template.getItem();
               if (itemDefinition.getId() == null || itemDefinition.getId().getValue().length() == 0){
                  itemDefinition.setId(getIdManager().createId());
               }
               itemDefinition.setPresentationTemplate(template);
               template.getItemDefinitions().remove(itemDefinition);
               if (itemDefinition.getSequence() == -1) {
                  itemDefinition.setSequence(Integer.MAX_VALUE);
               }
               template.getItemDefinitions().add(itemDefinition);
               template.setItem(new PresentationItemDefinition());
               template.orderItemDefs();
            }
            break;
         case FILES_PAGE :
            if (template.getFileRef().getAction() != null &&
                  template.getFileRef().getAction().equalsIgnoreCase("addFile") &&
                  !errors.hasErrors() ){

               TemplateFileRef file = (TemplateFileRef)template.getFileRef();
               file.setPresentationTemplate(template);
               if (file.getId() == null || file.getId().getValue().length() == 0){
                  file.setId(getIdManager().createId());
               }
               template.getFiles().remove(file);
               template.getFiles().add(file);
               template.setFileRef(new TemplateFileRef());
            }
            break;
      }
   }

   public String getFormAttributeName(){
      return getFormSessionAttributeName();
   }

   protected boolean isFormSubmission(HttpServletRequest request){
      if (request.getParameter("formSubmission") != null &&
            request.getParameter("formSubmission").equalsIgnoreCase("true")){
         return true;
      }
      return super.isFormSubmission(request);
   }

   public Collection getMimeTypes() {
      return mimeTypes;
   }

   public void setMimeTypes(Collection mimeTypes) {
      this.mimeTypes = mimeTypes;
   }

   public HomeFactory getHomeFactory() {
      return homeFactory;
   }

   public void setHomeFactory(HomeFactory homeFactory) {
      this.homeFactory = homeFactory;
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

   public WorksiteManager getWorksiteManager() {
      return worksiteManager;
   }

   public List getCustomTypedEditors() {
      return customTypedEditors;
   }

   public void setCustomTypedEditors(List customTypedEditors) {
      this.customTypedEditors = customTypedEditors;
   }

   public void setWorksiteManager(WorksiteManager worksiteManager) {
      this.worksiteManager = worksiteManager;
   }

   public AuthenticationManager getAuthManager() {
      return authManager;
   }

   public void setAuthManager(AuthenticationManager authManager) {
      this.authManager = authManager;
   }

   public PresentationManager getPresentationManager() {
      return presentationManager;
   }

   public void setPresentationManager(PresentationManager presentationManager) {
      this.presentationManager = presentationManager;
   }

	public SessionManager getSessionManager() {
		return sessionManager;
	}
	
	public void setSessionManager(SessionManager sessionManager) {
		this.sessionManager = sessionManager;
	}

   public ContentHostingService getContentHosting() {
      return contentHosting;
   }

   public void setContentHosting(ContentHostingService contentHosting) {
      this.contentHosting = contentHosting;
   }

   public EntityManager getEntityManager() {
      return entityManager;
   }

   public void setEntityManager(EntityManager entityManager) {
      this.entityManager = entityManager;
   }
}


package org.theospi.portfolio.presentation.support;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.content.api.ContentCollection;
import org.sakaiproject.content.api.ContentCollectionEdit;
import org.sakaiproject.content.api.ContentHostingService;
import org.sakaiproject.content.api.ContentResource;
import org.sakaiproject.content.api.ResourceEditingHelper;
import org.sakaiproject.entity.api.Entity;
import org.sakaiproject.entity.api.ResourceProperties;
import org.sakaiproject.exception.IdUnusedException;
import org.sakaiproject.exception.IdUsedException;
import org.sakaiproject.exception.PermissionException;
import org.sakaiproject.exception.TypeException;
import org.sakaiproject.metaobj.security.AuthenticationManager;
import org.sakaiproject.metaobj.shared.FormHelper;
import org.sakaiproject.metaobj.shared.mgt.AgentManager;
import org.sakaiproject.metaobj.shared.mgt.IdCustomEditor;
import org.sakaiproject.metaobj.shared.mgt.IdManager;
import org.theospi.portfolio.presentation.PresentationFunctionConstants;
import org.theospi.portfolio.presentation.PresentationManager;
import org.theospi.portfolio.presentation.model.Presentation;
import org.theospi.portfolio.presentation.model.PresentationItem;
import org.theospi.portfolio.presentation.model.PresentationItemDefinition;
import org.theospi.portfolio.presentation.model.PresentationTemplate;
import org.theospi.portfolio.presentation.model.PresentationTemplateNameComparator;
import org.theospi.portfolio.security.AuthorizationFacade;
import org.theospi.portfolio.security.AuthorizationFailedException;
import org.theospi.portfolio.shared.model.Node;
import org.theospi.portfolio.wizard.model.CompletedWizard;
import org.sakaiproject.metaobj.shared.model.Agent;
import org.sakaiproject.metaobj.shared.model.Artifact;
import org.sakaiproject.metaobj.shared.model.ContentResourceArtifact;
import org.sakaiproject.metaobj.shared.model.Id;
import org.sakaiproject.metaobj.utils.mvc.intf.TypedPropertyEditor;
import org.sakaiproject.site.api.SiteService;
import org.sakaiproject.tool.api.Placement;
import org.sakaiproject.tool.api.ToolManager;
import org.sakaiproject.user.api.User;
import org.sakaiproject.user.cover.UserDirectoryService;
import org.sakaiproject.util.ResourceLoader;

public class PresentationService {
	private IdManager idManager;
	private AuthenticationManager authnManager;
	private AgentManager agentManager;
	private AuthorizationFacade authzManager;
	private PresentationManager presentationManager;
	private ToolManager toolManager;
	private IdCustomEditor idCustomEditor;
	private TypedPropertyEditor presentationItemCustomEditor;
	private TypedPropertyEditor presentationViewerCustomEditor;
	
	private SiteService siteService;
	private ContentHostingService contentHostingService;
	
	private static final Log log = LogFactory.getLog(PresentationService.class);
	private static ResourceLoader resourceBundle = new ResourceLoader(PresentationManager.PRESENTATION_MESSAGE_BUNDLE);	
	
	//TODO: Add signature for more parameterized creation -- not just complete current context (user, site, tool)
	public Presentation createPresentation(String presentationType, String templateId) {
		if (!Presentation.FREEFORM_TYPE.equals(presentationType) && !Presentation.TEMPLATE_TYPE.equals(presentationType)) {
			log.warn("Cannot Create Presentation -- Invalid Presentation Type (" + presentationType + ") -- Must be template or free-form.");
			return null;
		}
		
		PresentationTemplate template = presentationManager.getPresentationTemplate(idManager.getId(templateId));
		if (template == null) {
			log.warn("Cannot Create Presentation -- Invalid Presentation Template ID: " + templateId);
			return null;
		}
		
		Presentation presentation = new Presentation();
		presentation.setNewObject(true);
		//presentation.setId(idManager.createId());
		//The Site ID is coalesced in PresentationManager -- the Tool ID should be too, but is not 
		presentation.setToolId(toolManager.getCurrentPlacement().getId());
		presentation.setPresentationType(presentationType);
		presentation.setTemplate(template);
		presentation.setName(template.getName());
		presentation.setExpiresOn(new GregorianCalendar(1970, 1, 1).getTime());
		return presentationManager.storePresentation(presentation);
	}
	
	//NOTE: This method is context-aware, returning available templates for the current user/site/tool
	public List<PresentationTemplate> getAvailableTemplates() {
        Agent agent = authnManager.getAgent();
        TreeSet<PresentationTemplate> availableTemplates = new TreeSet<PresentationTemplate>(new PresentationTemplateNameComparator());
        availableTemplates.addAll(presentationManager.findTemplatesByOwner(agent, toolManager.getCurrentPlacement().getContext()));
        availableTemplates.addAll(presentationManager.findPublishedTemplates(toolManager.getCurrentPlacement().getContext()));
        availableTemplates.addAll(presentationManager.findGlobalTemplates());
        return new ArrayList<PresentationTemplate>(availableTemplates);
	}
	
	public boolean updatePresentation(String presentationId, String name, String description, Boolean active) {
		Presentation presentation = editPresentation(presentationId);
		
		if (name != null)
			presentation.setName(name);
		
		if (description != null)
			presentation.setDescription(description);
				
		if (Boolean.TRUE.equals(active)) {
			presentation.setExpiresOn(null);
		}
		else if (Boolean.FALSE.equals(active)) {
			presentation.setExpiresOn(new GregorianCalendar(1970, 1, 1).getTime());
		}
		
		presentation = presentationManager.storePresentation(presentation);
		return (presentation != null);
	}
	
	public Presentation getPresentation(String id) {
		Presentation presentation = presentationManager.getPresentation(idManager.getId(id));
		if (presentation == null)
			throw new IllegalArgumentException("Portfolio does not exist with ID: " + id);
		return presentation;
	}
	
	public Presentation editPresentation(String id) {
		Presentation presentation = getPresentation(id);
        authzManager.checkPermission(PresentationFunctionConstants.EDIT_PRESENTATION, presentation.getId());
        return presentation;
	}
	
	public Presentation savePresentation(Presentation presentation) {
		return presentationManager.storePresentation(presentation);
	}
	
	public Map<String, Object> editOptions(String presentationId) {
		HashMap<String, Object> params = new HashMap<String, Object>();
		Presentation presentation = editPresentation(presentationId);		
		PresentationTemplate template = presentation.getTemplate();
		
		if (template.getPropertyFormType() == null || Presentation.FREEFORM_TYPE.equals(presentation.getPresentationType()))
			throw new IllegalArgumentException("Portfolio Type does not accept options.");

		//Try to find the attached form
		String formId = null;
		if (presentation.getPropertyForm() != null)
			formId = contentHostingService.resolveUuid(presentation.getPropertyForm().getValue()); 
		
		//No form or invalid attachment results in creation, otherwise edit existing
		if (formId == null) {
			//Create or locate the folder to hold the new form
			String folderPath = getPropertiesFolder();					
            params.put(FormHelper.PARENT_ID_TAG, folderPath);
			params.put(FormHelper.NEW_FORM_DISPLAY_NAME_TAG, presentation.getName() + " (" + presentationId + ")");
		}
		else {
			params.put(ResourceEditingHelper.ATTACHMENT_ID, formId);
		}
		
		params.put(ResourceEditingHelper.CREATE_TYPE, ResourceEditingHelper.CREATE_TYPE_FORM);
		params.put(ResourceEditingHelper.CREATE_SUB_TYPE, template.getPropertyFormType().getValue());
		return params;
	}
	
	public Map<String, Object> createForm(String presentationId, String formTypeId) {
		HashMap<String, Object> params = new HashMap<String, Object>();
		Presentation presentation = editPresentation(presentationId);
		if (formTypeId == null)
			throw new IllegalArgumentException("Cannot create null-typed forms");
		
		boolean found = false;
		for (PresentationItemDefinition itemDef : (Collection<PresentationItemDefinition>) presentation.getTemplate().getItemDefinitions())
			if (formTypeId.equals(itemDef.getType()))
				found = true;
		
		if (!found)
			throw new IllegalArgumentException("Presentation [ID: " + presentationId + "] does not accept forms of type: " + formTypeId);
		
		String folderPath = getFormsFolder();
		params.put(FormHelper.PARENT_ID_TAG, folderPath);
		params.put(ResourceEditingHelper.CREATE_TYPE, ResourceEditingHelper.CREATE_TYPE_FORM);
		params.put(ResourceEditingHelper.CREATE_SUB_TYPE, formTypeId);
		return params;
	}
	
	public Map<String, Object> editForm(String presentationId, String formTypeId, String formId) {
		HashMap<String, Object> params = new HashMap<String, Object>();
		Presentation presentation = editPresentation(presentationId);
		if (formId == null)
			throw new IllegalArgumentException("Cannot edit null form");
		
		if (formTypeId == null)
			formTypeId = getFormType(formId);
		
		if (formTypeId == null)
			throw new IllegalArgumentException("Cannot edit with null form type");
		
		//String resourceId = contentHostingService.resolveUuid(formId);
		params.put(ResourceEditingHelper.ATTACHMENT_ID, formId);
		params.put(ResourceEditingHelper.CREATE_TYPE, ResourceEditingHelper.CREATE_TYPE_FORM);
		params.put(ResourceEditingHelper.CREATE_SUB_TYPE, formTypeId);
		return params;
	}
	
	public Map<String, Object> editForm(String presentationId, String formId) {
		return editForm(presentationId, getFormType(formId), formId);
	}
	
	public String getFormType(String formId) {
		return null;
	}
	
	public void saveOptions(String presentationId, String artifactId) {
		try {
			Presentation presentation = editPresentation(presentationId);
			String formId = contentHostingService.resolveUuid(artifactId);
			contentHostingService.checkResource(formId);
			presentation.setPropertyForm(idManager.getId(artifactId));
			presentationManager.storePresentation(presentation);
		} catch (PermissionException e) {
			log.warn("Cannot attach unreadable options form [] to presentation [].");
		} catch (IdUnusedException e) {
			throw new IllegalArgumentException("Cannot find options form: " + artifactId);
		} catch (TypeException e) {
			throw new RuntimeException("Cannot attach collection [" + artifactId + "] to presentation [" + presentationId + "] as options form.");
		}
	}
	
	public Map<String, Object> getPresentationArtifacts(String presentationId) {
		Presentation presentation = getPresentation(presentationId);
		Map<String, Object> model = new HashMap<String, Object>();
        Map<String, Object> artifacts = new HashMap<String, Object>();
        Map<String, Object> artifactCache = new HashMap<String, Object>();
        Agent agent = authnManager.getAgent();

        PresentationTemplate template = presentationManager.getPresentationTemplate(presentation.getTemplate().getId());
        presentation.setTemplate(template);

        model.put("types", template.getSortedItems());
        
        // Create sorted list of artifacts
        for (PresentationItemDefinition itemDef : (Set<PresentationItemDefinition>) template.getSortedItems()) {

           if (artifactCache.containsKey(itemDef.getType()) && !itemDef.getHasMimeTypes()){
              artifacts.put(itemDef.getId().getValue(), artifactCache.get(itemDef.getType()));
           } 
           else {
              List itemArtifacts = (List) presentationManager.loadArtifactsForItemDef(itemDef, agent);
              // Update display name of resource content to include abbreviated folder name
              if (itemArtifacts.size() > 0 && itemArtifacts.get(0) instanceof ContentResourceArtifact) {
                 // don't do this for forms, as their display name is OK as is
                 if (! ((ContentResourceArtifact)itemArtifacts.get(0)).getBase().getContentType().equals("application/x-osp"))
                    for (ContentResourceArtifact art: (List<ContentResourceArtifact>)itemArtifacts)
                       art.setDisplayName( abbreviateResourceName(art) );
              }
              // Sort artifacts by display name (wizards also sorted by date)
              if (itemArtifacts.size() > 0 && itemArtifacts.get(0) instanceof CompletedWizard)
                 Collections.sort((List<CompletedWizard>)itemArtifacts, new SortWizards());
              else
                 Collections.sort((List<Artifact>)itemArtifacts, new SortArtifacts());

              // cache only full list
              if (!itemDef.getHasMimeTypes()) {
                 artifactCache.put(itemDef.getType(), itemArtifacts);
              }
              artifacts.put(itemDef.getId().getValue(), itemArtifacts);
           }
        }
        model.put("artifacts", artifacts);
        
        Collection items = new ArrayList();
        for (Iterator i = presentation.getPresentationItems().iterator(); i.hasNext();) {
           PresentationItem item = (PresentationItem) i.next();
           items.add(item.getDefinition().getId().getValue() + "." + item.getArtifactId());
        }
        model.put("items", items);
        
        return model;		
	}
	
	private int FOLDER_MAX_LEN = 16; // maximum display size for folder name
	private int FOLDER_ABBR_SIZE = 7; // abbreviated folder name prefix/suffix size
	private String FOLDER_ABBR_TOKEN = ".."; // abbreviated folder name token

	/**
	 * Prepend containing folder name to resource name, abbreviating if too long
	 */
	private String abbreviateResourceName(ContentResourceArtifact art) {
		String folder = art.getBase().getContainingCollection().getProperties().getProperty(ResourceProperties.PROP_DISPLAY_NAME);

		if (folder.length() > FOLDER_MAX_LEN) {
			int len = folder.length();
			StringBuilder newFolder = new StringBuilder(folder.substring(0, FOLDER_ABBR_SIZE));
			newFolder.append(FOLDER_ABBR_TOKEN);
			newFolder.append(folder.substring(len - FOLDER_ABBR_SIZE, len));
			folder = newFolder.toString();
		}
		StringBuilder resourceName = new StringBuilder(folder);
		resourceName.append(Entity.SEPARATOR);
		resourceName.append(art.getDisplayName());
		return resourceName.toString();
	}
	

	/**
	  * Sort artifacts by display name
	  */
	protected class SortArtifacts implements Comparator<Artifact> {
		public int compare(Artifact o1, Artifact o2) {
			return o1.getDisplayName().compareTo(o2.getDisplayName());
		}
	}

	/**
	 * Sort Wizards by name and date (in case any share the same name)
	 */
	protected class SortWizards implements Comparator<CompletedWizard> {
		public int compare(CompletedWizard o1, CompletedWizard o2) {
			if (o1.getDisplayName().equals(o2.getDisplayName())) {
				return o1.getCreated().compareTo(o2.getCreated());
			}
			else {
				return o1.getDisplayName().compareTo(o2.getDisplayName());
			}
		}
	}
	
	protected String getFormsFolder() {
		String idChunk = PresentationManager.PRESENTATION_FORMS_FOLDER;
		String displayName = resourceBundle.getString(PresentationManager.PRESENTATION_FORMS_FOLDER_DISPNAME);
		String description = resourceBundle.getString(PresentationManager.PRESENTATION_FORMS_FOLDER_DESC);
		return getFolder(idChunk, displayName, description);
	}
	
	protected String getPropertiesFolder() {
		String idChunk = PresentationManager.PRESENTATION_PROPERTIES_FOLDER;
		String displayName = resourceBundle.getString(PresentationManager.PRESENTATION_PROPERTIES_FOLDER_DISPNAME);
		String description = resourceBundle.getString(PresentationManager.PRESENTATION_PROPERTIES_FOLDER_DESC);
		return getFolder(idChunk, displayName, description);
	}
	
	protected String getFolder(String idChunk, String displayName, String description) {
		try {
			String folderBase = getUserCollection().getId();

			Placement placement = toolManager.getCurrentPlacement();
			String currentSite = placement.getContext();

			String rootDisplayName = resourceBundle.getString(PresentationManager.PORTFOLIO_INTERACTION_FOLDER_DISPNAME);
			String rootDescription = resourceBundle.getString(PresentationManager.PORTFOLIO_INTERACTION_FOLDER_DESC);

			String folderPath = createFolder(folderBase, "portfolio-interaction", rootDisplayName, rootDescription);
			folderPath = createFolder(folderPath, currentSite, siteService.getSiteDisplay(currentSite), null);

			folderPath = createFolder(folderPath, idChunk, displayName, description);
			return folderPath;
		} catch (TypeException e) {
			throw new RuntimeException("Failed to redirect to helper", e);
		} catch (IdUnusedException e) {
			throw new RuntimeException("Failed to redirect to helper", e);
		} catch (PermissionException e) {
			throw new RuntimeException("Failed to redirect to helper", e);
		}
	}
	
	protected ContentCollection getUserCollection() throws TypeException, IdUnusedException, PermissionException {
		User user = UserDirectoryService.getCurrentUser();
		String userId = user.getId();
		String wsId = siteService.getUserSiteId(userId);
		String wsCollectionId = contentHostingService.getSiteCollection(wsId);
		ContentCollection collection = contentHostingService.getCollection(wsCollectionId);
		return collection;
	}
	
	protected String createFolder(String base, String append, String appendDisplay, String appendDescription) {
		String folder = base + append + "/";
		try {
			ContentCollectionEdit propFolder = contentHostingService.addCollection(folder);
			propFolder.getPropertiesEdit().addProperty(ResourceProperties.PROP_DISPLAY_NAME, appendDisplay);
			propFolder.getPropertiesEdit().addProperty(ResourceProperties.PROP_DESCRIPTION, appendDescription);
			contentHostingService.commitCollection(propFolder);
			return propFolder.getId();
		} catch (IdUsedException e) {
			// ignore... it is already there.
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return folder;
	}
	
	
	public void setIdManager(IdManager idManager) {
		this.idManager = idManager;
	}
	public void setAuthnManager(AuthenticationManager authnManager) {
		this.authnManager = authnManager;
	}
	public void setAgentManager(AgentManager agentManager) {
		this.agentManager = agentManager;
	}
	public void setAuthzManager(AuthorizationFacade authzManager) {
		this.authzManager = authzManager;
	}
	public void setPresentationManager(PresentationManager presentationManager) {
		this.presentationManager = presentationManager;
	}
	public void setToolManager(ToolManager toolManager) {
		this.toolManager = toolManager;
	}
	public IdCustomEditor getIdCustomEditor() {
		return idCustomEditor;
	}
	public void setIdCustomEditor(IdCustomEditor idCustomEditor) {
		this.idCustomEditor = idCustomEditor;
	}

	public TypedPropertyEditor getPresentationItemCustomEditor() {
		return presentationItemCustomEditor;
	}

	public void setPresentationItemCustomEditor(TypedPropertyEditor presentationItemCustomEditor) {
		this.presentationItemCustomEditor = presentationItemCustomEditor;
	}

	public TypedPropertyEditor getPresentationViewerCustomEditor() {
		return presentationViewerCustomEditor;
	}

	public void setPresentationViewerCustomEditor(TypedPropertyEditor presentationViewerCustomEditor) {
		this.presentationViewerCustomEditor = presentationViewerCustomEditor;
	}

	public void setSiteService(SiteService siteService) {
		this.siteService = siteService;
	}

	public void setContentHostingService(ContentHostingService contentHostingService) {
		this.contentHostingService = contentHostingService;
	}
	
}

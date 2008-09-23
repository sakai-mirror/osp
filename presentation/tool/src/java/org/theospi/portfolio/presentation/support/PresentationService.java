package org.theospi.portfolio.presentation.support;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TreeSet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.metaobj.security.AuthenticationManager;
import org.sakaiproject.metaobj.shared.mgt.AgentManager;
import org.sakaiproject.metaobj.shared.mgt.IdCustomEditor;
import org.sakaiproject.metaobj.shared.mgt.IdManager;
import org.theospi.portfolio.presentation.PresentationManager;
import org.theospi.portfolio.presentation.model.Presentation;
import org.theospi.portfolio.presentation.model.PresentationTemplate;
import org.theospi.portfolio.presentation.model.PresentationTemplateNameComparator;
import org.theospi.portfolio.security.AuthorizationFacade;
import org.theospi.portfolio.security.AuthorizationFailedException;
import org.sakaiproject.metaobj.shared.model.Agent;
import org.sakaiproject.metaobj.shared.model.Id;
import org.sakaiproject.tool.api.ToolManager;

public class PresentationService {
	private IdManager idManager;
	private AuthenticationManager authnManager;
	private AgentManager agentManager;
	private AuthorizationFacade authzManager;
	private PresentationManager presentationManager;
	private ToolManager toolManager;
	private IdCustomEditor idCustomEditor;
	
	private static final Log log = LogFactory.getLog(PresentationService.class);
	
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
	
	public boolean updatePresentation(String presentationId, String name, String description) {
		Presentation presentation = presentationManager.getPresentation(idManager.getId(presentationId));
		if (presentation == null)
			throw new IllegalArgumentException("Portfolio does not exist with ID: " + presentationId);
		
		if (name != null)
			presentation.setName(name);
		
		if (description != null)
			presentation.setDescription(description);
		
        presentation.setModified(new Date(System.currentTimeMillis()));
		presentation = presentationManager.storePresentation(presentation);
		return (presentation != null);
	}
	
	public Presentation getPresentation(String id) {
		Presentation presentation = presentationManager.getPresentation(idManager.getId(id));
		if (presentation == null)
			throw new IllegalArgumentException("Portfolio does not exist with ID: " + id);
		return presentation;
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
	
}

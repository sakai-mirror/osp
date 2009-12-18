package org.theospi.portfolio.presentation.control;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.sakaiproject.metaobj.shared.mgt.IdManager;
import org.theospi.portfolio.presentation.model.Presentation;
import org.theospi.portfolio.presentation.model.PresentationItem;
import org.theospi.portfolio.presentation.model.PresentationItemDefinition;

public class EditPresentationFormController extends AbstractCalloutController {

	//NOTE: This controller handles the creation/edits of forms from the context
	//      of an existing presentation. It sets the return view to the contents
	//      of that presentation.
	
	protected static final String PROP_PRESENTATION_ITEM_DEF_ID = "_PresentationItemDef:Id";
	protected IdManager idManager;
	
	public EditPresentationFormController() {
		setReturnView("editContentRedirect");
	}
	
	@Override
	protected Map<String, Object> getSessionParams(String presentationId, HttpServletRequest request) {
		String formTypeId = request.getParameter("formTypeId");
		String formId = request.getParameter("formId");
		String itemDefId = request.getParameter("itemDefId");
		
		if (formId != null)
			return presentationService.editForm(presentationId, formTypeId, formId);
		else {
			Map<String, Object> retMap = presentationService.createForm(presentationId, formTypeId);
			retMap.put(PROP_PRESENTATION_ITEM_DEF_ID, itemDefId);
			return retMap;
		}
	}

	@Override
	protected void save(String presentationId, String reference,
			HttpSession session) {
		String itemDefId = (String) session.getAttribute(PROP_PRESENTATION_ITEM_DEF_ID);
		//Check for an itemDefId...if none, was an edit and don't need to do anything
		if (itemDefId != null) {
			Presentation presentation = presentationService.getPresentation(presentationId);
			PresentationItemDefinition itemDef = presentationService.getPresentationItemDefinition(itemDefId);
			PresentationItem pi = new PresentationItem();
			pi.setArtifactId(idManager.getId(reference));
			pi.setDefinition(itemDef);
			int size = presentation.getPresentationItems().size();
			if (!itemDef.isAllowMultiple() && size > 0) {
			//If I can only have one item and there is already one, clear it so the new one wins
				presentation.getPresentationItems().clear();
			}
			presentation.getPresentationItems().add(pi);
		}
		return;
	}	
	
	@Override
	protected void cleanUpSession(HttpSession session) {
		super.cleanUpSession(session);
		session.removeAttribute(PROP_PRESENTATION_ITEM_DEF_ID);
	}
	
	public void setIdManager(IdManager idManager) {
		this.idManager = idManager;
	}
	
}

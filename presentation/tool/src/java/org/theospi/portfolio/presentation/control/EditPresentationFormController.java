package org.theospi.portfolio.presentation.control;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

public class EditPresentationFormController extends AbstractCalloutController {

	//NOTE: This controller handles the creation/edits of forms from the context
	//      of an existing presentation. It sets the return view to the contents
	//      of that presentation. It also does not do any special handling of
	//      saves or cancels, because those materials will be reflected on the
	//      the contents screen when rendered. We may decide that newly created
	//      forms should be selected automatically.
	
	public EditPresentationFormController() {
		setReturnView("editContentRedirect");
	}
	
	@Override
	protected Map<String, Object> getSessionParams(String presentationId, HttpServletRequest request) {
		String formTypeId = request.getParameter("formTypeId");
		String formId = request.getParameter("formId");
		
		if (formId != null)
			return presentationService.editForm(presentationId, formTypeId, formId);
		else
			return presentationService.createForm(presentationId, formTypeId);
	}
}

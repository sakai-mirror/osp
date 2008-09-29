package org.theospi.portfolio.presentation.control;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

public class EditPresentationFormController extends AbstractCalloutController {

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

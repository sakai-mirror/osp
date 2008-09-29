package org.theospi.portfolio.presentation.control;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

public class EditOptionsController extends AbstractCalloutController {

	/*
	public EditOptionsController() {
		setHelperView("addPresentationProperty");
		setReturnView("editPresentationRedirect");
	}
	*/
	
	@Override
	protected Map<String, Object> getSessionParams(String presentationId, HttpServletRequest request) {
		return presentationService.editOptions(presentationId);
	}
	
	@Override
	protected void save(String presentationId, String reference) {
		presentationService.saveOptions(presentationId, reference);
	}
}

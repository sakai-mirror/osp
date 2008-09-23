package org.theospi.portfolio.presentation.control;

import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.servlet.mvc.SimpleFormController;
import org.theospi.portfolio.presentation.support.PresentationService;

public class EditOptionsController extends SimpleFormController {
	private PresentationService presentationService;

	public EditOptionsController() {
		setFormView("editOptions");
		setSuccessView("listPresentationRedirect");
	}

	@Override
	protected Object formBackingObject(HttpServletRequest request) throws Exception {
		return new HashMap<String, Object>();
	}

	public void setPresentationService(PresentationService presentationService) {
		this.presentationService = presentationService;
	}

}

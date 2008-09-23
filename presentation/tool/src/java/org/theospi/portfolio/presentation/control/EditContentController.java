package org.theospi.portfolio.presentation.control;

import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.servlet.mvc.SimpleFormController;
import org.theospi.portfolio.presentation.support.PresentationService;

public class EditContentController extends SimpleFormController {
	private PresentationService presentationService;
	
	public EditContentController() {
		setFormView("editContent");
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

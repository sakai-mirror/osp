package org.theospi.portfolio.presentation.control;

import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.SimpleFormController;
import org.theospi.portfolio.presentation.model.Presentation;
import org.theospi.portfolio.presentation.support.PresentationService;

public class EditPresentationController extends SimpleFormController {
	private PresentationService presentationService;
	
	public EditPresentationController() {
		setCommandClass(Presentation.class);
		setCommandName("presentation");
		setFormView("editPresentation");
		setSuccessView("listPresentationRedirect");
	}

	@Override
	protected Object formBackingObject(HttpServletRequest request) throws Exception {
		//NOTE: Authorization failures and bad IDs throw exceptions here
		String presentationId = request.getParameter("id");
		return presentationService.getPresentation(presentationId);
	}
	
	@Override
	protected ModelAndView processFormSubmission(HttpServletRequest request, HttpServletResponse response, Object command, BindException errors)
			throws Exception {
		return new ModelAndView("listPresentationRedirect");
	}

	public void setPresentationService(PresentationService presentationService) {
		this.presentationService = presentationService;
	}

}

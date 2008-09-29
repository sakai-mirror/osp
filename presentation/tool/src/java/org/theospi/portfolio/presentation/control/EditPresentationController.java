package org.theospi.portfolio.presentation.control;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.validation.BindException;
import org.springframework.validation.Errors;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.SimpleFormController;
import org.theospi.portfolio.presentation.model.Presentation;
import org.theospi.portfolio.presentation.model.PresentationComment;
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
	protected Map referenceData(HttpServletRequest request, Object command, Errors errors) throws Exception {
		Map<String, Object> model = new HashMap<String, Object>();
		Presentation presentation = (Presentation) command;
		if (presentation.getExpiresOn() == null || presentation.getExpiresOn().after(new Date())) {
			model.put("active", Boolean.TRUE);
		}
		else {
			model.put("active", Boolean.FALSE);
		}
		List<PresentationComment> comments = presentationService.getComments(presentation.getId().getValue());
		model.put("comments", comments);
		model.put("numComments", new Integer(comments.size()));
		return model;
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

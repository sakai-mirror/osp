package org.theospi.portfolio.presentation.control;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.sakaiproject.metaobj.shared.model.Id;
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.SimpleFormController;
import org.theospi.portfolio.presentation.model.Presentation;
import org.theospi.portfolio.presentation.support.PresentationService;

public class EditContentController extends SimpleFormController {
	private PresentationService presentationService;
	
	public EditContentController() {
		setCommandClass(Presentation.class);
		setCommandName("presentation");
		setFormView("editContent");
		setSuccessView("editContentRedirect");
	}
	
	@Override
	protected Object formBackingObject(HttpServletRequest request) throws Exception {
		String presentationId = request.getParameter("id");
		return presentationService.getPresentation(presentationId);
	}
	
	@Override
	protected Map referenceData(HttpServletRequest request, Object command, Errors errors) throws Exception {
		Presentation presentation = (Presentation) command;
		Map model = presentationService.getPresentationArtifacts(presentation.getId().getValue());
		if ( request.getParameter("save") != null )
			model.put("actionSave", true ); 
		else if ( request.getParameter("undo") != null )
			model.put("actionUndo", true ); 
		return model;
	}

	@Override
	protected boolean isFormSubmission(HttpServletRequest request) {
		return request.getParameter("undo") == null && super.isFormSubmission(request);
	}

	@Override
	protected ModelAndView onSubmit(HttpServletRequest request, HttpServletResponse response, Object command, BindException errors) throws Exception {
		Presentation presentation = presentationService.savePresentation((Presentation) command);
		if (presentation == null)
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		return showForm(request,response,errors);
	}

	@Override
	protected void initBinder(HttpServletRequest request, ServletRequestDataBinder binder) throws Exception {
		binder.registerCustomEditor(Id.class, presentationService.getIdCustomEditor());
		binder.registerCustomEditor(presentationService.getPresentationItemCustomEditor().getType(), presentationService.getPresentationItemCustomEditor());
	}
		
	public void setPresentationService(PresentationService presentationService) {
		this.presentationService = presentationService;
	}
	
}

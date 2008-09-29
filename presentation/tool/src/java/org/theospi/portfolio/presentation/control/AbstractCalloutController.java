package org.theospi.portfolio.presentation.control;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.sakaiproject.content.api.ResourceEditingHelper;
import org.sakaiproject.metaobj.shared.FormHelper;
import org.sakaiproject.metaobj.shared.model.Id;
import org.springframework.validation.BindException;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractCommandController;
import org.springframework.web.servlet.mvc.AbstractController;
import org.springframework.web.servlet.mvc.SimpleFormController;
import org.theospi.portfolio.presentation.model.Presentation;
import org.theospi.portfolio.presentation.model.PresentationTemplate;
import org.theospi.portfolio.presentation.support.PresentationService;
import org.theospi.portfolio.shared.model.Node;

public abstract class AbstractCalloutController extends AbstractController {
	protected PresentationService presentationService;
	protected String helperView = "formHelper";
	protected String returnView = "editPresentationRedirect";
	protected static final String PROP_PRESENTATION_ID = "_Presentation:Id";
	
	//There are only three ways this controller gets invoked
	// 1: Initial request -- set up session and call out to helper
	// 2: Return from helper -- handle helper action, tear down session, return to EditPresentation
	// 2a: Save callback -- attach form if not already attached, otherwise no-op
	// 2b: Cancel callback -- no-op
	
	@Override
	protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception {
		HttpSession session = request.getSession();
		Object helperAction = session.getAttribute(FormHelper.RETURN_ACTION_TAG);
		String cachedId = (String) session.getAttribute(PROP_PRESENTATION_ID);
		
		if (FormHelper.RETURN_ACTION_SAVE.equals(helperAction)) {
			return handleSave(cachedId, session);
		}
		else if (FormHelper.RETURN_ACTION_CANCEL.equals(helperAction)) {
			return handleCancel(cachedId, session);
		}
		else {
			return handleEdit(request);
		}
	}
	
	protected ModelAndView handleEdit(HttpServletRequest request) {
		String presentationId = request.getParameter("id");
		HttpSession session = request.getSession();
		cleanUpSession(session);
		for (Entry<String, Object> entry : getSessionParams(presentationId, request).entrySet())
			session.setAttribute(entry.getKey(), entry.getValue());
		session.setAttribute(PROP_PRESENTATION_ID, presentationId);
		return sendToHelper();
	}
	
	protected Map<String, Object> getSessionParams(String presentationId, HttpServletRequest request) {
		return new HashMap<String, Object>();
	}
	
	protected ModelAndView handleSave(String presentationId, HttpSession session) {
		String reference = (String) session.getAttribute(FormHelper.RETURN_REFERENCE_TAG);
		save(presentationId, reference);
		cleanUpSession(session);
		return sendToReturn(presentationId);
	}
		
	protected void save(String presentationId, String reference) {
		return;
	}
	
	protected ModelAndView handleCancel(String presentationId, HttpSession session) {
		cancel(presentationId);
		cleanUpSession(session);
		return sendToReturn(presentationId);
	}	
	
	protected void cancel(String presentationId) {
		return;
	}
	
	protected void cleanUpSession(HttpSession session) {
        session.removeAttribute(ResourceEditingHelper.CREATE_TYPE);
        session.removeAttribute(ResourceEditingHelper.CREATE_SUB_TYPE);
        session.removeAttribute(ResourceEditingHelper.CREATE_PARENT);
        session.removeAttribute(ResourceEditingHelper.ATTACHMENT_ID);
        session.removeAttribute(FormHelper.RETURN_ACTION_TAG);
        session.removeAttribute(FormHelper.PARENT_ID_TAG);
        session.removeAttribute(FormHelper.NEW_FORM_DISPLAY_NAME_TAG);
        session.removeAttribute(PROP_PRESENTATION_ID);
	}
	
	private ModelAndView sendToHelper() {
		return new ModelAndView(helperView);
	}
	
	private ModelAndView sendToHelper(Map model) {
		return new ModelAndView(helperView, model);
	}
	
	private ModelAndView sendToReturn() {
		return new ModelAndView(returnView);
	}
	
	private ModelAndView sendToReturn(String presentationId) {
		HashMap<String, Object> model = new HashMap<String, Object>();
		model.put("id", presentationId);
		return new ModelAndView(returnView, model);
	}
	
	private ModelAndView sendToReturn(Map model) {
		return new ModelAndView(returnView, model);
	}

	public String getHelperView() {
		return helperView;
	}
	
	public String getReturnView() {
		return returnView;
	}

	public void setReturnView(String returnView) {
		this.returnView = returnView;
	}

	public void setHelperView(String helperView) {
		this.helperView = helperView;
	}

	public void setPresentationService(PresentationService presentationService) {
		this.presentationService = presentationService;
	}

}

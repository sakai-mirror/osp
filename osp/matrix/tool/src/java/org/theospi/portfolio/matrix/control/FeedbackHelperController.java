package org.theospi.portfolio.matrix.control;

import java.util.HashMap;
import java.util.Map;

import org.sakaiproject.component.api.ServerConfigurationService;
import org.sakaiproject.metaobj.shared.mgt.IdManager;
import org.sakaiproject.metaobj.utils.mvc.intf.Controller;
import org.springframework.validation.Errors;
import org.springframework.web.servlet.ModelAndView;
import org.theospi.portfolio.matrix.MatrixManager;
import org.theospi.portfolio.matrix.model.Cell;
import org.theospi.portfolio.security.AudienceSelectionHelper;

public class FeedbackHelperController implements Controller {

	private ServerConfigurationService serverConfigurationService;
	private MatrixManager matrixManager;
	private IdManager idManager = null;
	
	public ModelAndView handleRequest(Object requestModel, Map request,
			Map session, Map application, Errors errors) {
		
		if(session.get("feedbackMatrixCall") != null){
			if(session.get("feedbackCellId") != null){
				Cell cell = matrixManager.getCell(idManager.getId(session.get("feedbackCellId").toString()));
				setAudienceSelectionVariables(cell, session);
				session.remove("feedbackMatrixCall");
				session.remove("feedbackCellId");
				return new ModelAndView("inviteFeedback");	
			}
		}
		
		Map model = new HashMap();
		
		//this checks if the user requested feedback.  There are two return values: 1. inviteFeedbackReturn (user clicked cancel or just finish)
		//2. inviteFeedbackNotify (user clicked finish and notify button).  Both values return the cell id.  inviteFeedbackNotify needs to call another
		//helper to finish the notify part.
		if(request.get("inviteFeedbackReturn") != null || request.get("inviteFeedbackNotify") != null){
			if(request.get("inviteFeedbackReturn") != null){
				model.put("page_id", request.get("inviteFeedbackReturn"));
				model.put("feedbackReturn", request.get("inviteFeedbackReturn"));
				return new ModelAndView("viewCell", model);
			}else if(request.get("inviteFeedbackNotify") != null){
				//inviteFeedbackNotify is returned from FeedbackHelperController and is the Id of the wizardPage of the cell.
				Cell cell = matrixManager.getCellFromPage(idManager.getId(request.get("inviteFeedbackNotify").toString()));
				setAudienceSelectionVariables(cell, session);				
				return new ModelAndView("notifyAudience");
			}
		}
		
		return null;
	}
	
	protected Map setAudienceSelectionVariables(Cell cell, Map session) {
		String baseUrl = this.getServerConfigurationService().getServerUrl();
	//	String url =  baseUrl + "/osp-matrix-tool/viewCell.osp?page_id=" + cell.getWizardPage().getId().getValue();

		session.put(AudienceSelectionHelper.AUDIENCE_FUNCTION, 
				AudienceSelectionHelper.AUDIENCE_FUNCTION_INVITE_FEEDBACK );

		String id = cell.getWizardPage().getId()!=null ? cell.getWizardPage().getId().getValue() : cell.getWizardPage().getNewId().getValue();
		session.put(AudienceSelectionHelper.AUDIENCE_QUALIFIER, id);
		session.put(AudienceSelectionHelper.AUDIENCE_SITE,cell.getWizardPage().getPageDefinition().getSiteId());

//		//session.put(AudienceSelectionHelper.AUDIENCE_PUBLIC_FLAG, cell.getIsPublic() ? "true" : "false");
//		session.put(AudienceSelectionHelper.AUDIENCE_PUBLIC_URL,  url);
//
		session.put(AudienceSelectionHelper.AUDIENCE_CANCEL_TARGET, "inviteFeedbackReturn=" + id);
		session.put(AudienceSelectionHelper.AUDIENCE_SAVE_NOTIFY_TARGET, "inviteFeedbackNotify=" + id);
		session.put(AudienceSelectionHelper.AUDIENCE_SAVE_TARGET, "inviteFeedbackReturn=" + id);
//
//		
//		session.put(AudienceSelectionHelper.AUDIENCE_SITE, cell.getWizardPage().getPageDefinition().getSiteId());
		
		//cleans up any previous context values
		session.remove(AudienceSelectionHelper.CONTEXT);
		session.remove(AudienceSelectionHelper.CONTEXT2);

		if(cell.getScaffoldingCell().getScaffolding() != null){ 
			session.put(AudienceSelectionHelper.CONTEXT,
					cell.getScaffoldingCell().getScaffolding().getTitle());
		}
		session.put(AudienceSelectionHelper.CONTEXT2,
				cell.getScaffoldingCell().getWizardPageDefinition().getTitle());
		
		return session;
	}

	public ServerConfigurationService getServerConfigurationService() {
		return serverConfigurationService;
	}

	public void setServerConfigurationService(
			ServerConfigurationService serverConfigurationService) {
		this.serverConfigurationService = serverConfigurationService;
	}

	public MatrixManager getMatrixManager() {
		return matrixManager;
	}

	public void setMatrixManager(MatrixManager matrixManager) {
		this.matrixManager = matrixManager;
	}

	public IdManager getIdManager() {
		return idManager;
	}

	public void setIdManager(IdManager idManager) {
		this.idManager = idManager;
	}
}

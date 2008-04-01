package org.theospi.portfolio.matrix.control;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.metaobj.shared.mgt.IdManager;
import org.sakaiproject.metaobj.shared.model.Id;
import org.sakaiproject.metaobj.utils.mvc.intf.Controller;
import org.sakaiproject.metaobj.utils.mvc.intf.LoadObjectController;
import org.sakaiproject.tool.api.SessionManager;
import org.springframework.validation.Errors;
import org.springframework.web.servlet.ModelAndView;
import org.theospi.portfolio.matrix.MatrixManager;
import org.theospi.portfolio.matrix.WizardPageHelper;
import org.theospi.portfolio.matrix.model.Cell;
import org.theospi.portfolio.matrix.model.WizardPage;
import org.theospi.portfolio.matrix.model.WizardPageDefinition;

public class ViewCellInformationController implements Controller{

	private MatrixManager matrixManager;
	private IdManager idManager = null;
	private SessionManager sessionManager;
	protected final Log logger = LogFactory.getLog(getClass());
	
	public ModelAndView handleRequest(Object requestModel, Map request,
			Map session, Map application, Errors errors) {
		
		
		WizardPageDefinition wizPageDef = null;
		
		if(session.get(WizardPageHelper.WIZARD_PAGE) != null){
			WizardPage wizPage = (WizardPage) session.get(WizardPageHelper.WIZARD_PAGE);
			if(wizPage != null){
				wizPageDef = wizPage.getPageDefinition();
			}
		}else{

			String strId = (String) request.get("page_id");
			if (strId == null) {
				strId = (String) session.get("page_id");
				session.remove("page_id");
			}

			Cell cell = null;
			Id id = getIdManager().getId(strId);


			try {
				cell = matrixManager.getCellFromPage(id);


				if (request.get("view_user") != null) {
					session.put("view_user", cell.getWizardPage().getOwner()
							.getId().getValue());
				}
			} catch (Exception e) {
				logger.error("Error with cell: " + strId + " " + e.toString());
				// tbd how to report error back to user?
			}
			
			if(cell != null)
				wizPageDef = cell.getScaffoldingCell().getWizardPageDefinition();
		}

		
		return new ModelAndView("success", "wizardPageDef", wizPageDef);
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

	public SessionManager getSessionManager() {
		return sessionManager;
	}

	public void setSessionManager(SessionManager sessionManager) {
		this.sessionManager = sessionManager;
	}

}

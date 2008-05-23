package org.theospi.portfolio.matrix.control;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.ObjectNotFoundException;
import org.sakaiproject.metaobj.shared.mgt.IdManager;
import org.sakaiproject.metaobj.shared.model.Id;
import org.sakaiproject.metaobj.utils.mvc.intf.Controller;
import org.sakaiproject.tool.api.SessionManager;
import org.springframework.validation.Errors;
import org.springframework.web.servlet.ModelAndView;
import org.theospi.portfolio.matrix.MatrixManager;
import org.theospi.portfolio.matrix.WizardPageHelper;
import org.theospi.portfolio.matrix.model.ScaffoldingCell;
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
		Map model = new HashMap();
		
		model.put("site_title", "");
		model.put("matrix_title", "");
		
		if(session.get(WizardPageHelper.WIZARD_PAGE) != null){
			WizardPage wizPage = (WizardPage) session.get(WizardPageHelper.WIZARD_PAGE);
			if(wizPage != null){
				wizPageDef = wizPage.getPageDefinition();
			}
		}else{

			String strId = (String) request.get("sCell_id");
			if (strId == null) {
				strId = (String) session.get("sCell_id");
				session.remove("sCell_id");
			}
			
			if (strId == null) {
				//must have passed something else
				strId = (String) session.get("page_def_id");
				session.remove("page_def_id");
			}

			ScaffoldingCell sCell = null;
			Id id = getIdManager().getId(strId);


			try {
				sCell = matrixManager.getScaffoldingCell(id);
			} catch (ObjectNotFoundException e) {
				logger.warn("Can't find scaffolding cell with idl: " + strId + ".  Trying as a wizard page definition.");
			}
			if (sCell == null) {
				sCell = matrixManager.getScaffoldingCellByWizardPageDef(id);
			}

			model.put("site_title", sCell.getScaffolding().getWorksiteName());
			model.put("matrix_title", sCell.getScaffolding().getTitle());



			
			if(sCell != null)
				wizPageDef = sCell.getWizardPageDefinition();
		}

		model.put("wizardPageDef", wizPageDef);
		
		return new ModelAndView("success", model);
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

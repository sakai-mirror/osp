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

public class ViewCellInformationController implements Controller, LoadObjectController {

	private MatrixManager matrixManager;
	private IdManager idManager = null;
	private SessionManager sessionManager;
	protected final Log logger = LogFactory.getLog(getClass());
	
	public ModelAndView handleRequest(Object requestModel, Map request,
			Map session, Map application, Errors errors) {
		
		
		CellFormBean cellBean = (CellFormBean) requestModel;
		Cell cell = cellBean.getCell();
		
		return null;
	}

	public Object fillBackingObject(Object incomingModel, Map request,
			Map session, Map application) throws Exception {

		// coming from matrix cell, not helper
		session.remove(WizardPageHelper.WIZARD_PAGE);

		CellFormBean cellBean = (CellFormBean) incomingModel;

		String strId = (String) request.get("page_id");
		if (strId == null) {
			strId = (String) session.get("page_id");
			session.remove("page_id");
		}

		Cell cell;
		Id id = getIdManager().getId(strId);

		// Check if the cell has been removed, which can happen if:
		// (1) user views matrix
		// (2) owner removes column or row (the code verifies that no one has
		// modified the matrix)
		// (3) user selects a cell that has just been removed with the column or
		// row
		try {
			cell = matrixManager.getCellFromPage(id);

			cellBean.setCell(cell);

			List nodeList = new ArrayList(matrixManager.getPageContents(cell
					.getWizardPage()));
			cellBean.setNodes(nodeList);

         if (request.get("view_user") != null) {
            session.put("view_user", cell.getWizardPage().getOwner()
               .getId().getValue());
         }
		} catch (Exception e) {
			logger.error("Error with cell: " + strId + " " + e.toString());
			// tbd how to report error back to user?
		}

		return cellBean;
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

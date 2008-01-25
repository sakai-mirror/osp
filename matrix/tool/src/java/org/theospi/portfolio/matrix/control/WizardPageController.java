/**********************************************************************************
 * $URL:https://source.sakaiproject.org/svn/osp/trunk/matrix/tool/src/java/org/theospi/portfolio/matrix/control/WizardPageController.java $
 * $Id:WizardPageController.java 9134 2006-05-08 20:28:42Z chmaurer@iupui.edu $
 ***********************************************************************************
 *
 * Copyright (c) 2005, 2006, 2007 The Sakai Foundation.
 *
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.opensource.org/licenses/ecl1.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 **********************************************************************************/

package org.theospi.portfolio.matrix.control;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.sakaiproject.metaobj.shared.model.Agent;
import org.sakaiproject.metaobj.shared.model.Id;
import org.sakaiproject.metaobj.shared.control.ToolFinishedView;
import org.sakaiproject.tool.api.ToolSession;
import org.springframework.validation.Errors;
import org.springframework.web.servlet.ModelAndView;
import org.theospi.portfolio.matrix.WizardPageHelper;
import org.theospi.portfolio.matrix.model.Cell;
import org.theospi.portfolio.matrix.model.ScaffoldingCell;
import org.theospi.portfolio.matrix.model.WizardPage;
import org.theospi.portfolio.matrix.model.WizardPageDefinition;
import org.theospi.portfolio.style.model.Style;
import org.theospi.portfolio.wizard.mgt.WizardManager;
import org.theospi.portfolio.wizard.model.CompletedWizard;
import org.theospi.portfolio.wizard.model.Wizard;
import org.theospi.portfolio.wizard.model.WizardPageSequence;

/**
 * openEvaluationPageHierRedirect will put the user here
 * 
 * Created by IntelliJ IDEA. User: John Ellis Date: Jan 24, 2006 Time: 3:46:49
 * PM To change this template use File | Settings | File Templates.
 */
public class WizardPageController extends CellController {

	private WizardManager wizardManager;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.theospi.utils.mvc.intf.FormController#referenceData(java.util.Map,
	 *      java.lang.Object, org.springframework.validation.Errors)
	 */
	public Map referenceData(Map request, Object command, Errors errors) {
		ToolSession session = getSessionManager().getCurrentToolSession();

		Map model = super.referenceData(request, command, errors);
		Boolean wizardPreview = Boolean.valueOf( (String)request.get("wizardPreview") );

		Agent owner = (Agent) request.get(WizardPageHelper.WIZARD_OWNER);

		if (owner == null)
			owner = (Agent) session.getAttribute(WizardPageHelper.WIZARD_OWNER);

		session.setAttribute(WizardPageHelper.WIZARD_OWNER, owner);

		model.put("readOnlyMatrix", super.isReadOnly(owner, null));
		model.put("pageTitleKey", "view_wizardPage");
		model.put("helperPage", "true");
		model.put("isWizard", "true");
		model.put("isMatrix", "false");
		model.put("categoryTitle", request.get("categoryTitle"));
		model.put("wizardTitle", request.get("wizardTitle"));
		model.put("wizardDescription", request.get("wizardDescription"));
		return model;
	}

	/**
	 * {@inheritDoc}
	 */
	protected Style getDefaultStyle(Id pageId) {
		// Get the wizard default style
		CompletedWizard cw = getWizardManager()
				.getCompletedWizardByPage(pageId);
		return cw.getWizard().getStyle();
	}

	/**
	 * {@inheritDoc}
	 */
	protected String[] getObjectMetadata(String pageId, Map request) {
		String[] objectMetadata = new String[3];

		WizardPage page = getMatrixManager().getWizardPage(
				getIdManager().getId(pageId));
		WizardPageSequence seq = wizardManager.getWizardPageSeqByDef(page
				.getPageDefinition().getId());
		Wizard wizard = seq.getCategory().getWizard();
      
		objectMetadata[METADATA_ID_INDEX] = wizard.getId().getValue();
		objectMetadata[METADATA_TITLE_INDEX] = wizard.getName();
		objectMetadata[METADATA_DESC_INDEX] = wizard.getDescription();
		return objectMetadata;
	}

	/**
	 * If there is a page in the session we want to display that. Otherwise look
	 * in the request for "page_id" If you are getting the wrong page displayed
	 * then you should make sure that the appropriate session/request variables
	 * are set.
	 * 
	 * @param incomingModel
	 * @param request
	 * @param session
	 * @param application
	 * @throws Exception
	 */
	public Object fillBackingObject(Object incomingModel, Map request,
			Map session, Map application) throws Exception {
		WizardPage page = (WizardPage) session
				.get(WizardPageHelper.WIZARD_PAGE);
		Id pageId = null;
		if (page != null)
			pageId = page.getId();
		else
			pageId = getIdManager().getId((String) request.get("page_id"));
		page = getMatrixManager().getWizardPage(pageId);
		session.put(WizardPageHelper.WIZARD_PAGE, page);
		session.remove(WizardPageHelper.CANCELED);

		Agent owner = (Agent) session.get(WizardPageHelper.WIZARD_OWNER);
		request.put(WizardPageHelper.WIZARD_OWNER, owner);

		WizardPageSequence seq = wizardManager.getWizardPageSeqByDef(page
				.getPageDefinition().getId());
		if (seq.getCategory().getParentCategory() != null)
			request.put("categoryTitle", seq.getCategory().getTitle());
		else
			request.put("categoryTitle", "");

		request.put("wizardPreview", Boolean.toString(seq.getCategory().getWizard().isPreview()));
		request.put("wizardTitle", seq.getCategory().getWizard().getName());
		request.put("wizardDescription", seq.getCategory().getWizard()
				.getDescription());

		Cell cell = createCellWrapper(page);

		CellFormBean cellBean = (CellFormBean) incomingModel;
		cellBean.setCell(cell);
		List nodeList = new ArrayList(getMatrixManager().getPageContents(page));
		cellBean.setNodes(nodeList);

		return cellBean;
	}

	public ModelAndView handleRequest(Object requestModel, Map request,
			Map session, Map application, Errors errors) {

		String submitWizardAction = (String) request.get("submitWizard");

		if (submitWizardAction != null) {
			session.put(ToolFinishedView.ALTERNATE_DONE_URL, "submitWizard");
			return new ModelAndView("confirmWizard", "", "");
		}

		return super.handleRequest(requestModel, request, session, application,
				errors);
	}

	public static Cell createCellWrapper(WizardPage page) {
		Cell cell = new Cell();
		cell.setWizardPage(page);
		if (page.getId() == null) {
			cell.setId(page.getNewId());
		} else {
			cell.setId(page.getId());
		}

		WizardPageDefinition pageDef = page.getPageDefinition();

		ScaffoldingCell cellDef = new ScaffoldingCell();
		cellDef.setWizardPageDefinition(pageDef);
		if (pageDef.getId() == null) {
			cellDef.setId(pageDef.getNewId());
		} else {
			cellDef.setId(pageDef.getId());
		}

		cell.setScaffoldingCell(cellDef);
		return cell;
	}

	public WizardManager getWizardManager() {
		return wizardManager;
	}

	public void setWizardManager(WizardManager wizardManager) {
		this.wizardManager = wizardManager;
	}
}

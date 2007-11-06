/**********************************************************************************
 * $URL:https://source.sakaiproject.org/svn/osp/trunk/matrix/tool/src/java/org/theospi/portfolio/matrix/control/EditScaffoldingCellController.java $
 * $Id:EditScaffoldingCellController.java 9134 2006-05-08 20:28:42Z chmaurer@iupui.edu $
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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.component.cover.ServerConfigurationService;
import org.sakaiproject.assignment.taggable.api.TaggableActivity;
import org.sakaiproject.assignment.taggable.api.TaggingHelperInfo;
import org.sakaiproject.assignment.taggable.api.TaggingProvider;
import org.sakaiproject.metaobj.shared.mgt.AgentManager;
import org.sakaiproject.metaobj.shared.mgt.StructuredArtifactDefinitionManager;
import org.sakaiproject.metaobj.shared.model.Agent;
import org.sakaiproject.metaobj.shared.model.Id;
import org.sakaiproject.metaobj.shared.model.StructuredArtifactDefinitionBean;
import org.sakaiproject.metaobj.utils.mvc.intf.FormController;
import org.sakaiproject.metaobj.utils.mvc.intf.LoadObjectController;
import org.sakaiproject.metaobj.worksite.mgt.WorksiteManager;
import org.sakaiproject.tool.api.ToolSession;
import org.sakaiproject.util.ResourceLoader;
import org.springframework.validation.Errors;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;
import org.theospi.portfolio.guidance.mgt.GuidanceHelper;
import org.theospi.portfolio.guidance.mgt.GuidanceManager;
import org.theospi.portfolio.guidance.model.Guidance;
import org.theospi.portfolio.matrix.MatrixFunctionConstants;
import org.theospi.portfolio.matrix.model.ScaffoldingCell;
import org.theospi.portfolio.matrix.model.WizardPageDefinition;
import org.theospi.portfolio.security.AudienceSelectionHelper;
import org.theospi.portfolio.security.Authorization;
import org.theospi.portfolio.security.AuthorizationFacade;
import org.theospi.portfolio.shared.model.CommonFormBean;
import org.theospi.portfolio.wizard.WizardFunctionConstants;
import org.theospi.portfolio.wizard.mgt.WizardManager;
import org.theospi.portfolio.wizard.model.Wizard;
import org.theospi.portfolio.wizard.taggable.api.WizardActivityProducer;
import org.theospi.portfolio.matrix.model.Matrix;
import org.theospi.portfolio.matrix.model.Cell;
import org.theospi.portfolio.matrix.model.WizardPage;
import org.theospi.portfolio.matrix.taggable.tool.DecoratedTaggingProvider;
import org.theospi.portfolio.matrix.taggable.tool.DecoratedTaggingProvider.Pager;
import org.theospi.portfolio.matrix.taggable.tool.DecoratedTaggingProvider.Sort;
import org.theospi.portfolio.review.mgt.ReviewManager;
import org.theospi.portfolio.assignment.AssignmentHelper;

import java.text.MessageFormat;
import java.util.*;

/**
 * @author chmaurer
 */
public class EditScaffoldingCellController extends
		BaseScaffoldingCellController implements FormController,
		LoadObjectController {

	protected final Log logger = LogFactory.getLog(getClass());

	private WorksiteManager worksiteManager = null;

	private AgentManager agentManager;

	private WizardManager wizardManager;

	private AuthorizationFacade authzManager = null;

	private WizardActivityProducer wizardActivityProducer;

	private StructuredArtifactDefinitionManager structuredArtifactDefinitionManager;

	private ReviewManager reviewManager;
	

	private static ResourceLoader myResources = new ResourceLoader("org.theospi.portfolio.matrix.bundle.Messages");
   
	// TODO move this constant somewhere where I can get to them from here and
	// in WizardTool
	public final static String FORM_TYPE = "form";

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.theospi.utils.mvc.intf.FormController#referenceData(java.util.Map,
	 *      java.lang.Object, org.springframework.validation.Errors)
	 */
	public Map referenceData(Map request, Object command, Errors errors) {
		ScaffoldingCell sCell = (ScaffoldingCell) command;
		Map model = new HashMap();

		WizardPageDefinition def = sCell.getWizardPageDefinition();
		if (def != null && def.getId() != null) {
			TaggableActivity activity = wizardActivityProducer.getActivity(def);
			if (getTaggingManager().isTaggable()) {
				model.put("taggable", "true");
				ToolSession session = getSessionManager()
						.getCurrentToolSession();
				List<DecoratedTaggingProvider> providers = (List) session
						.getAttribute(PROVIDERS_PARAM);
				if (providers == null) {
					providers = getDecoratedProviders(activity);
					session.setAttribute(PROVIDERS_PARAM, providers);
				}
				model.put("helperInfoList", getHelperInfo(activity));
				model.put("providers", providers);
			}
		}


		model.put("reflectionDevices", getReflectionDevices(def.getSiteId()));
		model.put("evaluationDevices", getEvaluationDevices(def.getSiteId()));
		model.put("reviewDevices", getReviewDevices(def.getSiteId()));
		model.put("additionalFormDevices", getAdditionalFormDevices(def.getSiteId()));
		model.put("selectedAdditionalFormDevices",
				getSelectedAdditionalFormDevices(sCell,def.getSiteId()));
		model.put("selectedAssignments",
                AssignmentHelper.getSelectedAssignments(sCell.getWizardPageDefinition().getAttachments()) );
		model.put("evaluators", getEvaluators(sCell.getWizardPageDefinition()));
		model.put("pageTitleKey", "title_editCell");
		model.put("pageInstructionsKey", "instructions_cellSettings");
		model.put("returnView", getReturnView());
      model.put("enableAssignments", ServerConfigurationService.getBoolean("osp.experimental.assignments",false) );

		if (sCell != null && sCell.getScaffolding() != null)
			model.put("isCellUsed", sCell.getScaffolding().isPublished()
					&& isCellUsed(sCell));
		else
			model.put("isCellUsed", false);

		return model;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.theospi.utils.mvc.intf.CustomCommandController#formBackingObject(java.util.Map,
	 *      java.util.Map, java.util.Map)
	 */
	// public Object formBackingObject(Map request, Map session, Map
	// application) {
	// return new ScaffoldingCell();
	// }
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.theospi.utils.mvc.intf.Controller#handleRequest(java.lang.Object,
	 *      java.util.Map, java.util.Map, java.util.Map,
	 *      org.springframework.validation.Errors)
	 */

	public ModelAndView handleRequest(Object requestModel, Map request,
			Map session, Map application, Errors errors) {
		ScaffoldingCell scaffoldingCell = (ScaffoldingCell) requestModel;
		String action = (String) request.get("action");
		String addFormAction = (String) request.get("addForm");
		String saveAction = (String) request.get("saveAction");
		Map model = new HashMap();


	      
		if(request.get("suppressItems") == null || request.get("suppressItems").toString() == "false"){
			scaffoldingCell.setSuppressItems(false);
		}else{
			scaffoldingCell.setSuppressItems(true);  
		}


		if (addFormAction != null) {

			String id = (String) request.get("selectAdditionalFormId");
			if ( id != null && !id.equals("") && !scaffoldingCell.getAdditionalForms().contains(id) )
				scaffoldingCell.getAdditionalForms().add(id);
			session.put(EditedScaffoldingStorage.STORED_SCAFFOLDING_FLAG,
					"true");
			model.put("scaffoldingCell", scaffoldingCell);
			return new ModelAndView("success", model);
		}
		if (saveAction != null) {

			if (isPublished(scaffoldingCell)) {
				model.put("scaffoldingCell", scaffoldingCell);
				model.put(EditedScaffoldingStorage.STORED_SCAFFOLDING_FLAG,
						"true");
				return new ModelAndView("editScaffoldingCellConfirm", model);
			}

			if (getTaggingManager().isTaggable()) {
				session.remove(PROVIDERS_PARAM);
			}

			saveScaffoldingCell(request, scaffoldingCell);
			session.remove(EditedScaffoldingStorage.STORED_SCAFFOLDING_FLAG);
			session
					.remove(EditedScaffoldingStorage.EDITED_SCAFFOLDING_STORAGE_SESSION_KEY);
			prepareModelWithScaffoldingId(model, scaffoldingCell);
			return new ModelAndView("return", model);
		}

		if (action == null)
			action = (String) request.get("submitAction");

		if (action != null && action.length() > 0) {

			if (request.get("reviewers") == null) {
				scaffoldingCell.getEvaluators().clear();
			}
			if (action.equals("removeFormDef")) {
				String params = (String) request.get("params");
				Map parmModel = parseParams(params);
				String formDefId = (String) parmModel.get("id");
				scaffoldingCell.getWizardPageDefinition().getAdditionalForms()
						.remove(formDefId);
				session.put(EditedScaffoldingStorage.STORED_SCAFFOLDING_FLAG,
						"true");
				model.put("scaffoldingCell", scaffoldingCell);
				return new ModelAndView("success", model);
			} else if (action.equals("forward")) {
				String forwardView = (String) request.get("dest");
				Map forwardModel = doForwardAction(forwardView, request,
						session, scaffoldingCell);
				model.putAll(forwardModel);
				return new ModelAndView(forwardView, model);
			} else if (action.equals("cancel")) {
				session.remove(PROVIDERS_PARAM);
				return new ModelAndView(new RedirectView(
						"viewScaffolding.osp?scaffolding_id="
								+ scaffoldingCell.getScaffolding().getId()));
			} else if (action.equals("tagActivity")) {
				return tagActivity(scaffoldingCell, model, request, session);
			} else if (action.equals("sortList")) {
				return sortList(scaffoldingCell, model, request, session);
			} else if (action.equals("pageList")) {
				return pageList(scaffoldingCell, model, request, session);
			}
			prepareModelWithScaffoldingId(model, scaffoldingCell);
			return new ModelAndView("return", model);
		}

		return new ModelAndView("success");
	}

	protected ModelAndView tagActivity(ScaffoldingCell scaffoldingCell,
			Map model, Map request, Map session) {
		EditedScaffoldingStorage sessionBean = (EditedScaffoldingStorage) session
				.get(EditedScaffoldingStorage.EDITED_SCAFFOLDING_STORAGE_SESSION_KEY);
		sessionBean.setScaffoldingCell(scaffoldingCell);
		session.put(EditedScaffoldingStorage.STORED_SCAFFOLDING_FLAG, "true");
		ModelAndView view = null;
		// Get appropriate helperInfo
		for (TaggingHelperInfo info : getHelperInfo(wizardActivityProducer
				.getActivity(scaffoldingCell.getWizardPageDefinition()))) {
			if (info.getProvider().getId().equals(request.get("providerId"))) {
				// Add parameters to session
				for (String key : info.getParameterMap().keySet()) {
					session.put(key, info.getParameterMap().get(key));
				}
				session.remove(PROVIDERS_PARAM);
				view = new ModelAndView(new RedirectView(info.getHelperId()
						+ ".helper"));
				break;
			}
		}
		return view;
	}

	protected ModelAndView sortList(ScaffoldingCell scaffoldingCell, Map model,
			Map request, Map session) {
		EditedScaffoldingStorage sessionBean = (EditedScaffoldingStorage) session
				.get(EditedScaffoldingStorage.EDITED_SCAFFOLDING_STORAGE_SESSION_KEY);
		sessionBean.setScaffoldingCell(scaffoldingCell);
		prepareModelWithScaffoldingId(model, scaffoldingCell);
		model.put("scaffoldingCell_id", scaffoldingCell.getId());

		String providerId = (String) request.get("providerId");
		String criteria = (String) request.get("criteria");

		List<DecoratedTaggingProvider> providers = (List) getSessionManager()
				.getCurrentToolSession().getAttribute(PROVIDERS_PARAM);
		for (DecoratedTaggingProvider dtp : providers) {
			if (dtp.getProvider().getId().equals(providerId)) {
				Sort sort = dtp.getSort();
				if (sort.getSort().equals(criteria)) {
					sort.setAscending(sort.isAscending() ? false : true);
				} else {
					sort.setSort(criteria);
					sort.setAscending(true);
				}
				break;
			}
		}
		return new ModelAndView("success", model);
	}

	protected ModelAndView pageList(ScaffoldingCell scaffoldingCell, Map model,
			Map request, Map session) {
		EditedScaffoldingStorage sessionBean = (EditedScaffoldingStorage) session
				.get(EditedScaffoldingStorage.EDITED_SCAFFOLDING_STORAGE_SESSION_KEY);
		sessionBean.setScaffoldingCell(scaffoldingCell);
		prepareModelWithScaffoldingId(model, scaffoldingCell);
		model.put("scaffoldingCell_id", scaffoldingCell.getId());

		String page = (String) request.get("page");
		String pageSize = (String) request.get("pageSize");
		String providerId = (String) request.get("providerId");

		List<DecoratedTaggingProvider> providers = (List) getSessionManager()
				.getCurrentToolSession().getAttribute(PROVIDERS_PARAM);
		for (DecoratedTaggingProvider dtp : providers) {
			if (dtp.getProvider().getId().equals(providerId)) {
				Pager pager = dtp.getPager();
				pager.setPageSize(Integer.valueOf(pageSize));
				if (Pager.FIRST.equals(page)) {
					pager.setFirstItem(0);
				} else if (Pager.PREVIOUS.equals(page)) {
					pager.setFirstItem(pager.getFirstItem()
							- pager.getPageSize());
				} else if (Pager.NEXT.equals(page)) {
					pager.setFirstItem(pager.getFirstItem()
							+ pager.getPageSize());
				} else if (Pager.LAST.equals(page)) {
					pager.setFirstItem((pager.getTotalItems() / pager
							.getPageSize())
							* pager.getPageSize());
				}
				break;
			}
		}
		return new ModelAndView("success", model);
	}

	protected void prepareModelWithScaffoldingId(Map model,
			ScaffoldingCell scaffoldingCell) {
		model.put("scaffolding_id", scaffoldingCell.getScaffolding().getId());
	}

	protected boolean isPublished(ScaffoldingCell scaffoldingCell) {
		return scaffoldingCell.getScaffolding().isPublished();
	}

	protected String getGuidanceViewPermission() {
		return MatrixFunctionConstants.VIEW_SCAFFOLDING_GUIDANCE;
	}

	protected String getGuidanceEditPermission() {
		return MatrixFunctionConstants.EDIT_SCAFFOLDING_GUIDANCE;
	}

	protected String getGuidanceTitle() {
		return myResources.getString("cell_guidance_title");
		// return "Guidance for Cell";
	}

	protected String getReturnView() {
		return "cell";
	}

	private Map doForwardAction(String forwardView, Map request, Map session,
			ScaffoldingCell scaffoldingCell) {
		Map model = new HashMap();

		EditedScaffoldingStorage sessionBean = (EditedScaffoldingStorage) session
				.get(EditedScaffoldingStorage.EDITED_SCAFFOLDING_STORAGE_SESSION_KEY);
		sessionBean.setScaffoldingCell(scaffoldingCell);
		prepareModelWithScaffoldingId(model, scaffoldingCell);
		model.put("scaffoldingCell_id", scaffoldingCell.getId());

		if (forwardView.equals("createGuidance")
				|| forwardView.equals("editInstructions")
				|| forwardView.equals("editRationale")
				|| forwardView.equals("editExamples")) {
			Boolean bTrue = new Boolean(true);
			Boolean bFalse = new Boolean(false);
			session.put(GuidanceHelper.SHOW_INSTRUCTION_FLAG, bFalse);
			session.put(GuidanceHelper.SHOW_RATIONALE_FLAG, bFalse);
			session.put(GuidanceHelper.SHOW_EXAMPLE_FLAG, bFalse);

			if (forwardView.equals("editInstructions")
					|| forwardView.equals("createGuidance"))
				session.put(GuidanceHelper.SHOW_INSTRUCTION_FLAG, bTrue);
			if (forwardView.equals("editRationale")
					|| forwardView.equals("createGuidance"))
				session.put(GuidanceHelper.SHOW_RATIONALE_FLAG, bTrue);
			if (forwardView.equals("editExamples")
					|| forwardView.equals("createGuidance"))
				session.put(GuidanceHelper.SHOW_EXAMPLE_FLAG, bTrue);

			String currentSite = scaffoldingCell.getWizardPageDefinition().getSiteId();
			session.put(EditedScaffoldingStorage.STORED_SCAFFOLDING_FLAG,
					"true");
			model.put(EditedScaffoldingStorage.STORED_SCAFFOLDING_FLAG, "true");

			Guidance guidance = scaffoldingCell.getGuidance();
			if (guidance == null) {
				String title = getGuidanceTitle();
				guidance = getGuidanceManager().createNew(title, currentSite,
						scaffoldingCell.getWizardPageDefinition().getId(),
						getGuidanceViewPermission(),
						getGuidanceEditPermission());
			}

			session.put(GuidanceManager.CURRENT_GUIDANCE, guidance);
		} else if (forwardView.equals("deleteGuidance")) {
			scaffoldingCell.setDeleteGuidanceId(scaffoldingCell.getGuidance()
					.getId());
			scaffoldingCell.setGuidance(null);
			session.put(EditedScaffoldingStorage.STORED_SCAFFOLDING_FLAG,
					"true");
			model.put(EditedScaffoldingStorage.STORED_SCAFFOLDING_FLAG, "true");
		} else if (!forwardView.equals("selectEvaluators")) {
			model.put("label", request.get("label"));
			model.put("finalDest", request.get("finalDest"));
			model.put("displayText", request.get("displayText"));
			String params = (String) request.get("params");
			model.put("params", params);
			if (!params.equals("")) {
				model.putAll(parseParams(params));
			}
		} 
      else {
			session.put(EditedScaffoldingStorage.STORED_SCAFFOLDING_FLAG,
					"true");
			model.put(EditedScaffoldingStorage.STORED_SCAFFOLDING_FLAG, "true");
			setAudienceSelectionVariables(session, scaffoldingCell
					.getWizardPageDefinition());

		}
		return model;

	}

	protected Map parseParams(String params) {
		Map model = new HashMap();
		if (!params.equals("")) {
			String[] paramsList = params.split(":");
			for (int i = 0; i < paramsList.length; i++) {
				String[] pair = paramsList[i].split("=");
				String val = null;
				if (pair.length > 1)
					val = pair[1];
				model.put(pair[0], val);
			}
		}
		return model;
	}

	protected List getEvaluators(WizardPageDefinition wpd) {
		List evalList = new ArrayList();
		Id id = wpd.getId() == null ? wpd.getNewId() : wpd.getId();

		List evaluators = getAuthzManager().getAuthorizations(null,
				MatrixFunctionConstants.EVALUATE_MATRIX, id);

		for (Iterator iter = evaluators.iterator(); iter.hasNext();) {
			Authorization az = (Authorization) iter.next();
			Agent agent = az.getAgent();
			String userId = az.getAgent().getEid().getValue();
			if (agent.isRole()) {
				evalList.add(MessageFormat.format(myResources
						.getString("decorated_role_format"),
						new Object[] { agent.getDisplayName() }));
			} else {
				evalList.add(MessageFormat.format(myResources
						.getString("decorated_user_format"), new Object[] {
						agent.getDisplayName(), userId }));
			}
		}

		return evalList;
	}

	protected void setAudienceSelectionVariables(Map session,
			WizardPageDefinition wpd) {
		session.put(AudienceSelectionHelper.AUDIENCE_FUNCTION,
						AudienceSelectionHelper.AUDIENCE_FUNCTION_MATRIX);

		String id = wpd.getId() != null ? wpd.getId().getValue() : wpd
				.getNewId().getValue();

		session.put(AudienceSelectionHelper.AUDIENCE_QUALIFIER, id);
		session.put(AudienceSelectionHelper.AUDIENCE_SITE, wpd.getSiteId());
	}

	protected Collection getAvailableForms(String siteId, String type) {
		return getStructuredArtifactDefinitionManager().findHomes(
				getIdManager().getId(siteId), true);
	}

	protected Collection getFormsForSelect(String type, String currentSiteId) {
		Collection commentForms = getAvailableForms(currentSiteId, type);

		List retForms = new ArrayList();
		for (Iterator iter = commentForms.iterator(); iter.hasNext();) {
			StructuredArtifactDefinitionBean sad = (StructuredArtifactDefinitionBean) iter
					.next();
			retForms.add(new CommonFormBean(sad.getId().getValue(), sad
					.getDecoratedDescription(), FORM_TYPE, sad.getOwner()
					.getName(), sad.getModified()));
		}

		Collections.sort(retForms, CommonFormBean.beanComparator);
		return retForms;
	}

	protected Collection getWizardsForSelect(String type, String currentSiteId) {
		List wizards = getWizardManager().listWizardsByType(
				getSessionManager().getCurrentSessionUserId(), currentSiteId,
				type);
		List retWizards = new ArrayList();
		for (Iterator iter = wizards.iterator(); iter.hasNext();) {
			Wizard wizard = (Wizard) iter.next();
			retWizards.add(new CommonFormBean(wizard.getId().getValue(), wizard
					.getName(), WizardFunctionConstants.WIZARD_TYPE_SEQUENTIAL,
					wizard.getOwner().getName(), wizard.getModified()));
		}

		Collections.sort(retWizards, CommonFormBean.beanComparator);
		return retWizards;
	}

	protected Collection getReviewDevices(String siteId) {
		Collection all = getFormsForSelect(WizardFunctionConstants.COMMENT_TYPE, siteId);
		all.addAll(getWizardsForSelect(WizardFunctionConstants.COMMENT_TYPE, siteId));
		return all;
	}

	protected Collection getReflectionDevices(String siteId) {
		Collection all = getFormsForSelect(WizardFunctionConstants.REFLECTION_TYPE, siteId);
		all
				.addAll(getWizardsForSelect(WizardFunctionConstants.REFLECTION_TYPE, siteId));
		return all;
	}

	protected Collection getEvaluationDevices(String siteId) {
		Collection all = getFormsForSelect(WizardFunctionConstants.EVALUATION_TYPE, siteId);
		all
				.addAll(getWizardsForSelect(WizardFunctionConstants.EVALUATION_TYPE, siteId));
		return all;
	}

	protected Collection getAdditionalFormDevices( String siteId ) {
		// Return all forms
		return getFormsForSelect(null, siteId);
	}

	protected Collection getSelectedAdditionalFormDevices(ScaffoldingCell sCell, String siteId) {
		// cwm need to preserve the ordering
		Collection returnCol = new ArrayList();
		Collection col = getAdditionalFormDevices(siteId);
		for (Iterator iter = col.iterator(); iter.hasNext();) {
			CommonFormBean bean = (CommonFormBean) iter.next();
			if (sCell.getAdditionalForms().contains(bean.getId()))
				returnCol.add(bean);
		}
		return returnCol;
	}

	/**
	 * @return Returns the worksiteManager.
	 */
	public WorksiteManager getWorksiteManager() {
		return worksiteManager;
	}

	/**
	 * @param worksiteManager
	 *            The worksiteManager to set.
	 */
	public void setWorksiteManager(WorksiteManager worksiteManager) {
		this.worksiteManager = worksiteManager;
	}

	/**
	 * @return Returns the agentManager.
	 */
	public AgentManager getAgentManager() {
		return agentManager;
	}

	/**
	 * @param agentManager
	 *            The agentManager to set.
	 */
	public void setAgentManager(AgentManager agentManager) {
		this.agentManager = agentManager;
	}

	public StructuredArtifactDefinitionManager getStructuredArtifactDefinitionManager() {
		return structuredArtifactDefinitionManager;
	}

	public void setStructuredArtifactDefinitionManager(
			StructuredArtifactDefinitionManager structuredArtifactDefinitionManager) {
		this.structuredArtifactDefinitionManager = structuredArtifactDefinitionManager;
	}

	public WizardManager getWizardManager() {
		return wizardManager;
	}

	public void setWizardManager(WizardManager wizardManager) {
		this.wizardManager = wizardManager;
	}

	/**
	 * @return Returns the authzManager.
	 */
	public AuthorizationFacade getAuthzManager() {
		return authzManager;
	}

	/**
	 * @param authzManager
	 *            The authzManager to set.
	 */
	public void setAuthzManager(AuthorizationFacade authzManager) {
		this.authzManager = authzManager;
	}

	public WizardActivityProducer getWizardActivityProducer() {
		return wizardActivityProducer;
	}

	public void setWizardActivityProducer(
			WizardActivityProducer wizardActivityProducer) {
		this.wizardActivityProducer = wizardActivityProducer;
	}

	/**
	 * @return Returns the reviewManager.
	 */
	public ReviewManager getReviewManager() {
		return reviewManager;
	}

	/**
	 * @param reviewManager
	 *            The reviewManager to set.
	 */
	public void setReviewManager(ReviewManager reviewManager) {
		this.reviewManager = reviewManager;
	}

	/**
	 * * Determine if any matrix cell with the specified scaffoldingCell has
	 * been 'used' * (containing reflections and/or added form items)
	 */
	private boolean isCellUsed(ScaffoldingCell scaffoldingCell) {
		Id scaffoldingId = scaffoldingCell.getScaffolding().getId();
		List matrices = getMatrixManager().getMatrices(scaffoldingId);

		for (Iterator matrixIt = matrices.iterator(); matrixIt.hasNext();) {
			Matrix matrix = (Matrix) matrixIt.next();
			List cells = getMatrixManager().getCells(matrix);

			for (Iterator cellIt = cells.iterator(); cellIt.hasNext();) {
				Cell cell = (Cell) cellIt.next();

				if (cell.getScaffoldingCell().equals(scaffoldingCell)) {
					WizardPage wizardPage = cell.getWizardPage();
					String pageId = wizardPage.getId().getValue();
					if (wizardPage.getReflections() != null
							&& wizardPage.getReflections().size() > 0)
						return true;
					if (wizardPage.getPageForms() != null
							&& wizardPage.getPageForms().size() > 0)
						return true;
					if (wizardPage.getFeedback() != null
							&& wizardPage.getFeedback().size() > 0)
						return true;
					if (wizardPage.getAttachments() != null
							&& wizardPage.getAttachments().size() > 0)
						return true;
					if (reviewManager.getReviewsByParent(pageId) != null
							&& reviewManager.getReviewsByParent(pageId).size() > 0)
						return true;
					// note: wizardPage.[get|set]Feedback() does not appear to
					// be used
				}
			}
		}

		return false;
	}

	protected List<TaggingHelperInfo> getHelperInfo(TaggableActivity activity) {
		List<TaggingHelperInfo> infoList = new ArrayList<TaggingHelperInfo>();
		if (getTaggingManager().isTaggable()) {
			for (TaggingProvider provider : getTaggingManager().getProviders()) {
				TaggingHelperInfo info = provider
						.getActivityHelperInfo(activity.getReference());
				if (info != null) {
					infoList.add(info);
				}
			}
		}
		return infoList;
	}

	protected List<DecoratedTaggingProvider> getDecoratedProviders(
			TaggableActivity activity) {
		List<DecoratedTaggingProvider> providers = new ArrayList<DecoratedTaggingProvider>();
		for (TaggingProvider provider : getTaggingManager().getProviders()) {
			providers.add(new DecoratedTaggingProvider(activity, provider));
		}
		return providers;
	}
}

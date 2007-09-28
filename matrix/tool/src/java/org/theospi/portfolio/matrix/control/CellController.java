/**********************************************************************************
 * $URL:https://source.sakaiproject.org/svn/osp/trunk/matrix/tool/src/java/org/theospi/portfolio/matrix/control/CellController.java $
 * $Id:CellController.java 9134 2006-05-08 20:28:42Z chmaurer@iupui.edu $
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
import org.sakaiproject.assignment.taggable.api.TaggableActivity;
import org.sakaiproject.assignment.taggable.api.TaggableItem;
import org.sakaiproject.assignment.taggable.api.TaggingHelperInfo;
import org.sakaiproject.assignment.taggable.api.TaggingManager;
import org.sakaiproject.assignment.taggable.api.TaggingProvider;
import org.sakaiproject.content.api.FilePickerHelper;
import org.sakaiproject.content.api.ResourceEditingHelper;
import org.sakaiproject.metaobj.security.AuthenticationManager;
import org.sakaiproject.metaobj.shared.mgt.IdManager;
import org.sakaiproject.metaobj.shared.mgt.StructuredArtifactDefinitionManager;
import org.sakaiproject.metaobj.shared.model.Agent;
import org.sakaiproject.metaobj.shared.model.Id;
import org.sakaiproject.metaobj.shared.model.StructuredArtifactDefinitionBean;
import org.sakaiproject.metaobj.utils.mvc.intf.FormController;
import org.sakaiproject.metaobj.utils.mvc.intf.LoadObjectController;
import org.sakaiproject.tool.api.SessionManager;
import org.sakaiproject.tool.api.ToolSession;
import org.sakaiproject.assignment.cover.AssignmentService;
import org.sakaiproject.assignment.api.AssignmentSubmission;
import org.sakaiproject.assignment.api.Assignment;
import org.sakaiproject.user.api.User;
import org.sakaiproject.user.cover.UserDirectoryService;

import org.springframework.validation.Errors;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;
import org.theospi.portfolio.guidance.mgt.GuidanceManager;
import org.theospi.portfolio.matrix.MatrixManager;
import org.theospi.portfolio.matrix.WizardPageHelper;
import org.theospi.portfolio.matrix.MatrixFunctionConstants;
import org.theospi.portfolio.matrix.model.Cell;
import org.theospi.portfolio.matrix.model.Scaffolding;
import org.theospi.portfolio.matrix.model.ScaffoldingCell;
import org.theospi.portfolio.matrix.model.WizardPage;
import org.theospi.portfolio.matrix.model.impl.MatrixContentEntityProducer;
import org.theospi.portfolio.matrix.taggable.tool.DecoratedTaggingProvider;
import org.theospi.portfolio.matrix.taggable.tool.DecoratedTaggingProvider.Pager;
import org.theospi.portfolio.matrix.taggable.tool.DecoratedTaggingProvider.Sort;
import org.theospi.portfolio.review.ReviewHelper;
import org.theospi.portfolio.review.mgt.ReviewManager;
import org.theospi.portfolio.review.model.Review;
import org.theospi.portfolio.shared.model.Node;
import org.theospi.portfolio.shared.model.CommonFormBean;
import org.theospi.portfolio.style.model.Style;
import org.theospi.portfolio.style.mgt.StyleManager;
import org.theospi.portfolio.security.AuthorizationFacade;
import org.theospi.portfolio.wizard.taggable.api.WizardActivityProducer;

import org.theospi.portfolio.assignment.AssignmentHelper;

import java.util.*;

public class CellController implements FormController, LoadObjectController {

	protected final Log logger = LogFactory.getLog(getClass());

	private MatrixManager matrixManager;

	private AuthenticationManager authManager = null;

	private IdManager idManager = null;

	private ReviewManager reviewManager;

	private StructuredArtifactDefinitionManager structuredArtifactDefinitionManager;

	private AuthorizationFacade authzManager = null;

	private TaggingManager taggingManager;

	private WizardActivityProducer wizardActivityProducer;

	private SessionManager sessionManager;

	private List<String> ratingProviderIds;

   private StyleManager styleManager;

   public static final String WHICH_HELPER_KEY = "filepicker.helper.key";

	public static final String KEEP_HELPER_LIST = "filepicker.helper.keeplist";

	protected static final int METADATA_ID_INDEX = 0;

	protected static final int METADATA_TITLE_INDEX = 1;

	protected static final int METADATA_DESC_INDEX = 2;

	protected static final String PROVIDERS_PARAM = "providers";

	public Map referenceData(Map request, Object command, Errors errors) {
		ToolSession session = getSessionManager().getCurrentToolSession();

		CellFormBean cell = (CellFormBean) command;
		Map model = new HashMap();

		model.put("isMatrix", "true");
		model.put("currentUser", getSessionManager().getCurrentSessionUserId());
		model.put("CURRENT_GUIDANCE_ID_KEY", "session."
				+ GuidanceManager.CURRENT_GUIDANCE_ID);

		model.put("isEvaluation", "false");

		// This is the tool session so evaluation tool gets "is_eval_page_id"
		// and the matrix/wizard does not
		if (session.getAttribute("is_eval_page_id") != null) {
			String eval_page_id = (String) session
					.getAttribute("is_eval_page_id");
			model.put("isEvaluation", "true");
		}

		model.put("pageTitleKey", "view_cell");

		// Check for cell being deleted while user was attempting to view
		if (cell == null || cell.getCell() == null) {
			clearSession(session);
			return model;
		}

		String pageId = cell.getCell().getWizardPage().getId().getValue();
		String siteId = cell.getCell().getWizardPage().getPageDefinition()
				.getSiteId();

		model.put("assignments", getUserAssignments(cell)); 
		model.put("reviews", getReviewManager().getReviewsByParentAndType(
				pageId, Review.FEEDBACK_TYPE, siteId, getEntityProducer()));
		model.put("evaluations", getReviewManager().getReviewsByParentAndType(
				pageId, Review.EVALUATION_TYPE, siteId, getEntityProducer()));
		model.put("reflections", getReviewManager().getReviewsByParentAndType(
				pageId, Review.REFLECTION_TYPE, siteId, getEntityProducer()));

		model.put("cellFormDefs", processAdditionalForms(cell.getCell()
				.getScaffoldingCell().getAdditionalForms()));

		model.put("cellForms", getMatrixManager().getPageForms(
				cell.getCell().getWizardPage()));

		Boolean readOnly = new Boolean(false);

		if (cell.getCell().getMatrix() != null) {
			Agent owner = cell.getCell().getMatrix().getOwner();
			readOnly = isReadOnly(owner, cell.getCell().getMatrix()
					.getScaffolding().getWorksiteId());
		}
		model.put("readOnlyMatrix", readOnly);

		String[] objectMetadata = getObjectMetadata(pageId, request);
		model.put("objectId", objectMetadata[METADATA_ID_INDEX]);
		model.put("objectTitle", objectMetadata[METADATA_TITLE_INDEX]);
		model.put("objectDesc", objectMetadata[METADATA_DESC_INDEX]);

      model.put("styles",
         createStylesList(getStyleManager().getStyles(getIdManager().getId(pageId))));

      if (taggingManager.isTaggable()) {
			TaggableItem item = wizardActivityProducer.getItem(cell.getCell()
					.getWizardPage());
			model.put("taggable", "true");
			ToolSession toolSession = getSessionManager()
					.getCurrentToolSession();
			List<DecoratedTaggingProvider> providers = (List) toolSession
					.getAttribute(PROVIDERS_PARAM);
			if (providers == null) {
				providers = getDecoratedProviders(item.getActivity());
				toolSession.setAttribute(PROVIDERS_PARAM, providers);
			}
			model.put("helperInfoList", getHelperInfo(item));
			model.put("providers", providers);
		}

		clearSession(session);
		return model;
	}

	/**
	 ** Return list of AssignmentSubmissions, associated with this cell
	 ** for the current user
	 **/
	protected List getUserAssignments(CellFormBean cell) {
		ArrayList submissions = new ArrayList();
		try {
			Agent owner = cell.getCell().getWizardPage().getOwner();
			User user = UserDirectoryService.getUser(owner.getId().getValue());
			ArrayList assignments = 
				AssignmentHelper.getSelectedAssignments(cell.getCell().getWizardPage().getPageDefinition().getAttachments());
			
			for ( Iterator it=assignments.iterator(); it.hasNext(); ) {
				Assignment assign = (Assignment)it.next();
				AssignmentSubmission assignSubmission = AssignmentService.getSubmission( assign.getId(),
																												 user );
				if (assignSubmission != null)
					submissions.add(assignSubmission);
			}
		}
		catch ( Exception e ) {
			logger.warn(".getUserAssignments: " + e.toString());
		}
		
		return submissions;
	}

	protected String getEntityProducer() {
		return MatrixContentEntityProducer.MATRIX_PRODUCER;
	}

	protected Boolean isReadOnly(Agent owner, Id id) {
      if ((owner != null && owner.equals(getAuthManager().getAgent()))
         && (id == null || getAuthzManager().isAuthorized(
         MatrixFunctionConstants.USE_SCAFFOLDING, id))) {
         return new Boolean(false);
      }
		return new Boolean(true);
	}
   
   protected String getStyleUrl(Style style) {
      Node styleNode = getMatrixManager().getNode(style.getStyleFile());
      return styleNode.getExternalUri();
   }

   /**
	 * 
	 * @param pageId
	 *            String representation of the wizard page id
	 * @param request
	 *            Map containing all of the request variables
	 * @return String[] containing the id, title, and description of the object
	 *         (matrix or wizard)
	 */
	protected String[] getObjectMetadata(String pageId, Map request) {
		String[] objectMetadata = new String[3];

		Cell cell = getMatrixManager().getCellFromPage(
				getIdManager().getId(pageId));
		Scaffolding scaffolding = cell.getMatrix().getScaffolding();
		objectMetadata[METADATA_ID_INDEX] = scaffolding.getId().getValue();
		objectMetadata[METADATA_TITLE_INDEX] = scaffolding.getTitle();
		objectMetadata[METADATA_DESC_INDEX] = scaffolding.getDescription();
		return objectMetadata;
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

		clearSession(getSessionManager().getCurrentToolSession());
		return cellBean;
	}

	private String ListToString(String[] strArray) {
		String result = "";
		if (strArray != null) {
			for (int i = 0; i < strArray.length; i++) {
            if (i == 0) {
               result = strArray[i];
            } else {
               result = result.concat(",").concat(strArray[i]);
            }
			}
		}
		return result;
	}

   protected List createStylesList(List styles) {
      List returned = new ArrayList(styles.size());
      for (Iterator<Style> i=styles.iterator();i.hasNext();) {
         returned.add(getStyleUrl(i.next()));
      }

      return returned;
   }

	public ModelAndView handleRequest(Object requestModel, Map request,
			Map session, Map application, Errors errors) {
		CellFormBean cellBean = (CellFormBean) requestModel;
		Cell cell = cellBean.getCell();

		// Check for cell being deleted while user was attempting to view
      if (cell == null) {
         return new ModelAndView("matrixError");
      }

		// String action = (String)request.get("action");
		String submit = (String) request.get("submit");
		String matrixAction = (String) request.get("matrix");
		String submitAction = (String) request.get("submitAction");

		if ("tagItem".equals(submitAction)) {
			return tagItem(cell, request, session);
		} else if ("sortList".equals(submitAction)) {
			return sortList(request, session);
		} else if ("pageList".equals(submitAction)) {
			return pageList(request, session);
		}

		if (submit != null) {
			Map map = new HashMap();
			map.put("page_id", cell.getWizardPage().getId());
			map.put("selectedArtifacts", ListToString(cellBean
					.getSelectedArtifacts()));
			map.put("cellBean", cellBean);
			// cwm change this to use the reflection submission confirmation
			return new ModelAndView("confirm", map);
		}

		if (matrixAction != null) {
			String scaffId = "";

			if (getTaggingManager().isTaggable()) {
				session.remove(PROVIDERS_PARAM);
			}

         if (cell.getMatrix() != null) {
            scaffId = cell.getMatrix().getScaffolding().getId().getValue();
         }

			if (session.get("is_eval_page_id") != null) {
				String eval_page_id = (String) session.get("is_eval_page_id");
				String pageId = cell.getWizardPage().getId().getValue();
            if (eval_page_id.equals(pageId)) {
               return new ModelAndView("cancelEvaluation");
            }
			}

			return new ModelAndView("cancel", "scaffolding_id", scaffId);
		}

		return new ModelAndView("success", "cellBean", cellBean);
	}

	protected ModelAndView tagItem(Cell cell, Map request, Map session) {
		ModelAndView view = null;
		// Get appropriate helperInfo
		for (TaggingHelperInfo info : getHelperInfo(wizardActivityProducer
				.getItem(cell.getWizardPage()))) {
			if (info.getProvider().getId().equals(request.get("providerId"))) {
				// Add parameters to session
				for (String key : info.getParameterMap().keySet()) {
					session.put(key, info.getParameterMap().get(key));
				}
				session.put("page_id", (String) request.get("page_id"));
				view = new ModelAndView(new RedirectView(info.getHelperId()
						+ ".helper"));
				break;
			}
		}
		return view;
	}

	protected ModelAndView sortList(Map request, Map session) {
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
		session.put("page_id", (String) request.get("page_id"));
		return new ModelAndView(new RedirectView((String) request.get("view")));
	}

	protected ModelAndView pageList(Map request, Map session) {
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
		session.put("page_id", (String) request.get("page_id"));
		return new ModelAndView(new RedirectView((String) request.get("view")));
	}

	protected List processAdditionalForms(List formTypes) {
		List retList = new ArrayList();
		for (Iterator iter = formTypes.iterator(); iter.hasNext();) {
			String strFormDefId = (String) iter.next();
			StructuredArtifactDefinitionBean bean = getStructuredArtifactDefinitionManager()
					.loadHome(strFormDefId);
			bean.getDescription();
			// cwm use a different bean below, as the name has implications
			retList.add(new CommonFormBean(strFormDefId, bean
					.getDecoratedDescription(), strFormDefId, bean.getOwner()
					.getName(), bean.getModified()));
		}
		return retList;
	}

	protected void clearSession(ToolSession session) {
		session.removeAttribute(FilePickerHelper.FILE_PICKER_ATTACHMENTS);
		session.removeAttribute(FilePickerHelper.FILE_PICKER_CANCEL);
		session.removeAttribute(FilePickerHelper.FILE_PICKER_RESOURCE_FILTER);

		session.removeAttribute(ResourceEditingHelper.CREATE_TYPE);
		session.removeAttribute(ResourceEditingHelper.CREATE_PARENT);
		session.removeAttribute(ResourceEditingHelper.CREATE_SUB_TYPE);
		session.removeAttribute(ResourceEditingHelper.ATTACHMENT_ID);

		session.removeAttribute(ReviewHelper.REVIEW_TYPE);
		session.removeAttribute(ReviewHelper.REVIEW_TYPE_KEY);

		session.removeAttribute(WHICH_HELPER_KEY);
		session.removeAttribute(KEEP_HELPER_LIST);

	}

	protected List<TaggingHelperInfo> getHelperInfo(TaggableItem item) {
		List<TaggingHelperInfo> infoList = new ArrayList<TaggingHelperInfo>();
		if (taggingManager.isTaggable()) {
			for (TaggingProvider provider : taggingManager.getProviders()) {
				// Only get helpers for accepted rating providers
				if (ratingProviderIds.contains(provider.getId())) {
					TaggingHelperInfo info = provider.getItemHelperInfo(item
							.getReference());
					if (info != null) {
						infoList.add(info);
					}
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

	/**
	 * @return
	 */
	public AuthenticationManager getAuthManager() {
		return authManager;
	}

	/**
	 * @param manager
	 */
	public void setAuthManager(AuthenticationManager manager) {
		authManager = manager;
	}

	/**
	 * @return
	 */
	public IdManager getIdManager() {
		return idManager;
	}

	/**
	 * @param manager
	 */
	public void setIdManager(IdManager manager) {
		idManager = manager;
	}

	/**
	 * @return Returns the matrixManager.
	 */
	public MatrixManager getMatrixManager() {
		return matrixManager;
	}

	/**
	 * @param matrixManager
	 *            The matrixManager to set.
	 */
	public void setMatrixManager(MatrixManager matrixManager) {
		this.matrixManager = matrixManager;
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
	 * @return Returns the structuredArtifactDefinitionManager.
	 */
	public StructuredArtifactDefinitionManager getStructuredArtifactDefinitionManager() {
		return structuredArtifactDefinitionManager;
	}

	/**
	 * @param structuredArtifactDefinitionManager
	 *            The structuredArtifactDefinitionManager to set.
	 */
	public void setStructuredArtifactDefinitionManager(
			StructuredArtifactDefinitionManager structuredArtifactDefinitionManager) {
		this.structuredArtifactDefinitionManager = structuredArtifactDefinitionManager;
	}

	public AuthorizationFacade getAuthzManager() {
		return authzManager;
	}

	public void setAuthzManager(AuthorizationFacade authzManager) {
		this.authzManager = authzManager;
	}

	public TaggingManager getTaggingManager() {
		return taggingManager;
	}

	public void setTaggingManager(TaggingManager taggingManager) {
		this.taggingManager = taggingManager;
	}

	public WizardActivityProducer getWizardActivityProducer() {
		return wizardActivityProducer;
	}

	public void setWizardActivityProducer(
			WizardActivityProducer wizardActivityProducer) {
		this.wizardActivityProducer = wizardActivityProducer;
	}

	public List<String> getRatingProviderIds() {
		return ratingProviderIds;
	}

	public void setRatingProviderIds(List<String> ratingProviderIds) {
		this.ratingProviderIds = ratingProviderIds;
	}

	public SessionManager getSessionManager() {
		return sessionManager;
	}

	public void setSessionManager(SessionManager sessionManager) {
		this.sessionManager = sessionManager;
	}

   public StyleManager getStyleManager() {
      return styleManager;
   }

   public void setStyleManager(StyleManager styleManager) {
      this.styleManager = styleManager;
   }
}

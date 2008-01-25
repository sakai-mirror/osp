/**********************************************************************************
 * $URL$
 * $Id$
 ***********************************************************************************
 *
 * Copyright (c) 2007 The Sakai Foundation.
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

import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.Iterator;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.assignment.taggable.api.TaggableActivity;
import org.sakaiproject.assignment.taggable.api.TaggingManager;
import org.sakaiproject.assignment.taggable.api.TaggingProvider;
import org.sakaiproject.exception.PermissionException;
import org.sakaiproject.metaobj.shared.mgt.IdManager;
import org.sakaiproject.metaobj.shared.model.Id;
import org.sakaiproject.metaobj.utils.mvc.intf.Controller;
import org.springframework.validation.Errors;
import org.springframework.web.servlet.ModelAndView;
import org.theospi.portfolio.matrix.MatrixManager;
import org.theospi.portfolio.security.AuthorizationFacade;
import org.theospi.portfolio.wizard.taggable.api.WizardActivityProducer;
import org.theospi.portfolio.matrix.MatrixFunctionConstants;
import org.theospi.portfolio.matrix.model.Matrix;
import org.theospi.portfolio.matrix.model.Scaffolding;
import org.theospi.portfolio.matrix.model.ScaffoldingCell;

/**
 * Delete scaffolding and associated matrix data if user confirms
 */
public class DeleteScaffoldingConfirmationController implements Controller {

	private MatrixManager matrixManager = null;

	private IdManager idManager = null;

	private AuthorizationFacade authzManager = null;

	private TaggingManager taggingManager = null;

	private WizardActivityProducer wizardActivityProducer = null;

	private final Log logger = LogFactory.getLog(getClass());

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.theospi.utils.mvc.intf.Controller#handleRequest(java.lang.Object,
	 *      java.util.Map, java.util.Map, java.util.Map,
	 *      org.springframework.validation.Errors)
	 */
	public ModelAndView handleRequest(Object requestModel, Map request,
			Map session, Map application, Errors errors) {
		String viewName = "success";
		Id id = idManager.getId((String) request.get("scaffolding_id"));
		Scaffolding scaffolding = getMatrixManager().getScaffolding(id);

		Map model = new HashMap();
		model.put("scaffolding_published", scaffolding.isPublished());

		String cancel = (String) request.get("cancel");
		String doit = (String) request.get("continue");

		if (cancel != null)
			return new ModelAndView("cancel", model);
		else if (doit == null)
			return new ModelAndView("delete", model);

		getAuthzManager().checkPermission(
				MatrixFunctionConstants.DELETE_SCAFFOLDING, id);

		if (scaffolding.getExposedPageId() != null
				&& !scaffolding.getExposedPageId().equals("")) {
			getMatrixManager().removeExposedMatrixTool(scaffolding);
		}

		// First delete any associated matrix data (if published scaffolding)
		List matrices = getMatrixManager().getMatrices(id);
		for (Iterator matrixIt = matrices.iterator(); matrixIt.hasNext();) {
			Matrix matrix = (Matrix) matrixIt.next();
			getMatrixManager().deleteMatrix(matrix.getId());
		}

		// if taggable, remove tags for all page defs
		try {
			if (getTaggingManager().isTaggable()) {
				Set<ScaffoldingCell> cells = scaffolding.getScaffoldingCells();
				for (ScaffoldingCell cell : cells) {
					for (TaggingProvider provider : getTaggingManager()
							.getProviders()) {
						System.out.println(cell.getTitle());
						TaggableActivity activity = getWizardActivityProducer()
								.getActivity(cell.getWizardPageDefinition());
						provider.removeTags(activity);
					}
				}
			}
		} catch (PermissionException pe) {
			logger.error(pe.getMessage(), pe);
		}

		// Next delete the scaffolding
		getMatrixManager().deleteScaffolding(id);

		return new ModelAndView("success", model);
	}

	public AuthorizationFacade getAuthzManager() {
		return authzManager;
	}

	public void setAuthzManager(AuthorizationFacade facade) {
		authzManager = facade;
	}

	public IdManager getIdManager() {
		return idManager;
	}

	public void setIdManager(IdManager idManager) {
		this.idManager = idManager;
	}

	public MatrixManager getMatrixManager() {
		return matrixManager;
	}

	public void setMatrixManager(MatrixManager matrixManager) {
		this.matrixManager = matrixManager;
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
}

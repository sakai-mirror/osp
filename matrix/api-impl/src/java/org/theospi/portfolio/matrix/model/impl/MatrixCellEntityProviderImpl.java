/**********************************************************************************
 * $URL$
 * $Id$
 ***********************************************************************************
 *
 * Copyright (c) 2008 The Sakai Foundation.
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

package org.theospi.portfolio.matrix.model.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.sakaiproject.entity.api.Entity;
import org.sakaiproject.entitybroker.entityprovider.CoreEntityProvider;
import org.sakaiproject.entitybroker.entityprovider.capabilities.AutoRegisterEntityProvider;
import org.sakaiproject.entitybroker.entityprovider.capabilities.PropertyProvideable;
import org.sakaiproject.exception.IdUnusedException;
import org.sakaiproject.metaobj.shared.mgt.IdManager;
import org.sakaiproject.site.api.Site;
import org.sakaiproject.site.api.SiteService;
import org.sakaiproject.tool.cover.SessionManager;
import org.theospi.portfolio.matrix.MatrixCellEntityProvider;
import org.theospi.portfolio.matrix.MatrixManager;
import org.theospi.portfolio.matrix.model.WizardPage;

public class MatrixCellEntityProviderImpl implements MatrixCellEntityProvider,
CoreEntityProvider, AutoRegisterEntityProvider, PropertyProvideable {

	private MatrixManager matrixManager;
	private SiteService siteService;
	private IdManager idManager;

	public void setMatrixManager(MatrixManager matrixManager) {
		this.matrixManager = matrixManager;
	}

	public boolean entityExists(String id) {
		boolean rv = false;

		try {
			WizardPage page = matrixManager.getWizardPage(idManager.getId(id));
			if (page != null) {
				rv = true;
			}
		}
		catch (Exception e) {}
		return rv;
	}

	public String getEntityPrefix() {
		return ENTITY_PREFIX;
	}

	public List<String> findEntityRefs(String[] prefixes, String[] name,
			String[] searchValue, boolean exactMatch) {
		// TODO Auto-generated method stub
		return null;
	}
	
	/*
	public List<String> findEntityRefs(String[] prefixes, String[] name, String[] searchValue, boolean exactMatch) {
		String siteId = null;
		String userId = null;
		List<String> rv = new ArrayList<String>();

		if (ENTITY_PREFIX.equals(prefixes[0])) {

			for (int i = 0; i < name.length; i++) {
				if ("context".equalsIgnoreCase(name[i]) || "site".equalsIgnoreCase(name[i]))
					siteId = searchValue[i];
				else if ("user".equalsIgnoreCase(name[i]) || "userId".equalsIgnoreCase(name[i]))
					userId = searchValue[i];
			}

			if (siteId != null && userId != null) {
				List<Scaffolding> scaffolding = matrixManager.findAvailableScaffolding(siteId, agentManager.getAgent(userId), false);
				//Iterator assignmentSorter = assignmentService.getAssignmentsForContext(siteId, userId);
				// filter to obtain only grade-able assignments
				while (scaffolding.iterator().hasNext()) {
					Scaffolding s = (Scaffolding) scaffolding.iterator().next();
					matrixManager.get
					if (assignmentService.allowGradeSubmission(a.getReference())) {
						rv.add(Entity.SEPARATOR + ENTITY_PREFIX + Entity.SEPARATOR + a.getId());
					}
				}
			}
		}
		return rv;
	}
*/

	public Map<String, String> getProperties(String reference) {
		Map<String, String> props = new HashMap<String, String>();
		//String parsedRef = reference;
		String placement = null;
		String pageId = null;
		String[] refParts = reference.split(Entity.SEPARATOR);
		//String submissionId = "";

		if (refParts.length >= 4) {
			//parsedRef = refParts[0] + Entity.SEPARATOR + refParts[1] + Entity.SEPARATOR + refParts[2];
			placement = refParts[3];
			pageId = refParts[2];
			//if (refParts.length >= 5) {
			//	submissionId = refParts[4].replaceAll("_", Entity.SEPARATOR);
			//}
		}

		//String pageId = parsedRef;
		try {
			WizardPage page = matrixManager.getWizardPage(idManager.getId(pageId));
			/*
			props.put("title", assignment.getTitle());
			props.put("author", assignment.getCreator());
			if (assignment.getTimeCreated() != null)
				props.put("created_time", assignment.getTimeCreated().getDisplay());
			if (assignment.getAuthorLastModified() != null)
				props.put("modified_by", assignment.getAuthorLastModified());
			if (assignment.getTimeLastModified() != null)
				props.put("modified_time", assignment.getTimeLastModified().getDisplay());
*/
			Site site = siteService.getSite(page.getPageDefinition().getSiteId());
			//String placement = site.getToolForCommonId("sakai.assignment.grades").getId();

			props.put("security.user", SessionManager.getCurrentSessionUserId());
			props.put("security.site.function", SiteService.SITE_VISIT);
			props.put("security.site.ref", site.getReference());
			//props.put("security.assignment.function", AssignmentService.SECURE_ACCESS_ASSIGNMENT);
			//props.put("security.assignment.ref", submissionId);

			String url = "/portal/directtool/" +  placement +
			"/viewCell.osp?page_id=" + pageId;

			props.put("url", url);
			/*
			props.put("url", "/portal/tool/" + placement + "?assignmentId=" + assignment.getId() + 
					"&submissionId=" + submissionId +
					"&assignmentReference=" + assignment.getReference() + 
					"&panel=Main&sakai_action=" + defaultView);
			props.put("status", assignment.getStatus());
			props.put("due_time", assignment.getDueTimeString());
			props.put("open_time", assignment.getOpenTimeString());
			if (assignment.getDropDeadTime() != null)
				props.put("retract_time", assignment.getDropDeadTime().getDisplay());
			props.put("description", assignment.getContentReference());
			props.put("draft", "" + assignment.getDraft());
			props.put("siteId", assignment.getContext());
			props.put("section", assignment.getSection());
			*/
		}
		catch (IdUnusedException e) {
			e.printStackTrace();
		}
		return props;
	}

	public String getPropertyValue(String reference, String name) {
		String rv = null;
		//lazy code, if any of the parts of getProperties is found to be slow this should be changed.
		Map<String, String> props = getProperties(reference);
		if (props != null && props.containsKey(name)) {
			rv = props.get(name);
		}
		return rv;
	}

	public void setPropertyValue(String reference, String name, String value) {
		// TODO: add ability to set properties of an assignment
	}

	public void setSiteService(SiteService siteService) {
		this.siteService = siteService;
	}

	public void setIdManager(IdManager idManager) {
		this.idManager = idManager;
	}
}

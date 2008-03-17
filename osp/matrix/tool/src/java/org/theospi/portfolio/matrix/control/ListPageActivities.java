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
package org.theospi.portfolio.matrix.control;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.entity.api.Reference;
import org.sakaiproject.entity.api.EntityManager;
import org.sakaiproject.exception.PermissionException;
import org.sakaiproject.taggable.api.Link;
import org.sakaiproject.taggable.api.LinkManager;
import org.sakaiproject.taggable.api.TaggableActivity;
import org.sakaiproject.taggable.api.TaggingManager;
import org.sakaiproject.taggable.api.TaggingProvider;
import org.springframework.validation.Errors;
import org.springframework.web.servlet.ModelAndView;
import org.theospi.portfolio.matrix.model.WizardPageDefinition;
import org.theospi.portfolio.tagging.api.MatrixTaggingProvider;

public class ListPageActivities extends AbstractMatrixController
{
	protected final Log logger = LogFactory.getLog(getClass());
	private TaggingManager taggingManager;
	private LinkManager linkManager;
	private TaggingProvider matrixTaggingProvider;
	private EntityManager entityManager;

	public ModelAndView handleRequest(Object requestModel, Map request, Map session,
			Map application, Errors errors)
	{
		Map<String, Object> model = new HashMap<String, Object>();
		Set<TaggableActivity> activities = new HashSet<TaggableActivity>();
		
		String criteriaRef = (String) request.get("criteriaRef");
		Reference ref = getEntityManager().newReference(criteriaRef);
		WizardPageDefinition wpd = getMatrixManager().getWizardPageDefinition(getIdManager().getId(ref.getId()));
		model.put("pageTitle", wpd.getTitle());
		try
		{
			List<Link> links = getLinkManager().getLinks(criteriaRef, true);
			for (Link link : links) {				
				activities.add(getTaggingManager().getActivity(link.getActivityRef(), getMatrixTaggingProvider()));
			}
		}
		catch (PermissionException e)
		{
			logger.warn("unable to get links for criteriaRef " + criteriaRef, e);
		}
		
		model.put("pageActivities", activities);
		return new ModelAndView("success", model);
	}
	
	
	
	public TaggingManager getTaggingManager() {
		return taggingManager;
	}

	public void setTaggingManager(TaggingManager taggingManager) {
		this.taggingManager = taggingManager;
	}

	public LinkManager getLinkManager()
	{
		return linkManager;
	}

	public void setLinkManager(LinkManager linkManager)
	{
		this.linkManager = linkManager;
	}

	protected TaggingProvider getMatrixTaggingProvider() {
		if (matrixTaggingProvider == null) {
			matrixTaggingProvider = getTaggingManager().findProviderById(
					MatrixTaggingProvider.PROVIDER_ID);
		}
		return matrixTaggingProvider;
	}

	public void setMatrixTaggingProvider(TaggingProvider matrixTaggingProvider) {
		this.matrixTaggingProvider = matrixTaggingProvider;
	}



	public EntityManager getEntityManager()
	{
		return entityManager;
	}



	public void setEntityManager(EntityManager entityManager)
	{
		this.entityManager = entityManager;
	}

}

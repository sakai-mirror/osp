package org.theospi.portfolio.matrix.control;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.entity.api.ResourceProperties;
import org.sakaiproject.metaobj.shared.model.Id;
import org.sakaiproject.metaobj.utils.mvc.intf.FormController;
import org.sakaiproject.metaobj.utils.mvc.intf.LoadObjectController;
import org.sakaiproject.site.api.Site;
import org.sakaiproject.site.cover.SiteService;
import org.sakaiproject.tool.api.Placement;
import org.sakaiproject.tool.api.ToolManager;
import org.springframework.validation.Errors;
import org.springframework.web.servlet.ModelAndView;
import org.theospi.portfolio.matrix.MatrixFunctionConstants;
import org.theospi.portfolio.matrix.MatrixManager;
import org.theospi.portfolio.matrix.model.Scaffolding;

public class ViewMatrixSummaryController extends AbstractMatrixController implements FormController, LoadObjectController {

	protected final Log logger = LogFactory.getLog(getClass());
	
	private ToolManager toolManager;
		
	//TODO: Find a place to put these without binding packages
	private static final String PROP_EXTRACTION_JOB = "extraction.job";
	private static final String PROP_EXTRACTION_JS_REVIEWER = "extraction.js.reviewer";
	private static final String PROP_EXTRACTION_JS_LEARNER = "extraction.js.learner";
	
	public Object fillBackingObject(Object incomingModel, Map request,
			Map session, Map application) throws Exception {
		
		MatrixSummaryBean data = (MatrixSummaryBean)incomingModel;
		
		String strScaffoldingId = (String)request.get("scaffolding_id");
		
		if (strScaffoldingId == null) {
			Placement placement = getToolManager().getCurrentPlacement();	
			strScaffoldingId = placement.getPlacementConfig().getProperty(
					MatrixManager.EXPOSED_MATRIX_KEY);
		}
		
		Id scaffoldingId = getIdManager().getId(strScaffoldingId);
		Scaffolding scaffolding = getMatrixManager().getScaffolding(scaffoldingId);
		
		if (scaffolding == null) {
			//redirect
		}
		
		data.setScaffolding(scaffolding);
		data.setScaffoldingId(strScaffoldingId);
		
		Site site = SiteService.getSite(scaffolding.getWorksiteId().getValue());
		if (site == null)
		{
			logger.warn("Cannot find site for housing scaffolding: " + strScaffoldingId);
			//redirect
		}
		
		ResourceProperties config = site.getProperties();
		String jobName = config.getProperty(PROP_EXTRACTION_JOB);
		String reviewerJs  = config.getProperty(PROP_EXTRACTION_JS_REVIEWER);
		String learnerJs = config.getProperty(PROP_EXTRACTION_JS_LEARNER);
		
		if (jobName == null || "".equals(jobName) || reviewerJs == null || "".equals(reviewerJs)) {
			//redirect
		}
		
		if (learnerJs == null || "".equals(learnerJs))
			learnerJs = reviewerJs;
		
		data.setJobId(jobName);
		
		request.put("summary-scaffolding-id", strScaffoldingId);
		request.put("summary-job-id", jobName);
		//FIXME: Need real enforcement here...
		if (getAuthzManager().isAuthorized(MatrixFunctionConstants.REVIEW_MATRIX, getWorksiteManager().getCurrentWorksiteId()))
			request.put("summary-script", reviewerJs);
		else
			request.put("summary-script", learnerJs);
		
		return incomingModel;
	}
	
	public ModelAndView handleRequest(Object requestModel, Map request,
			Map session, Map application, Errors errors) {
		
		Map model = new HashMap();

		return new ModelAndView("success", model);
	}
	
	public ToolManager getToolManager() {
		return toolManager;
	}
	
	public void setToolManager(ToolManager manager) {
		toolManager = manager;
	}

}

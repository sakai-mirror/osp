package org.theospi.portfolio.matrix.control;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.transform.JDOMSource;
import org.sakaiproject.metaobj.shared.model.Id;
import org.sakaiproject.metaobj.shared.model.OspException;
import org.sakaiproject.metaobj.utils.mvc.intf.FormController;
import org.sakaiproject.metaobj.utils.mvc.intf.LoadObjectController;
import org.sakaiproject.tool.api.Placement;
import org.sakaiproject.tool.api.ToolManager;
import org.theospi.portfolio.matrix.MatrixManager;
import org.theospi.portfolio.matrix.model.Scaffolding;
import org.sakaiproject.component.cover.ComponentManager;
import org.sakaiproject.extraction.api.ExtractionService;
import org.sakaiproject.extraction.api.XmlExtractionJob;
import org.springframework.validation.Errors;
import org.springframework.web.servlet.ModelAndView;

public class ViewMatrixSummaryController extends AbstractMatrixController implements FormController, LoadObjectController {

	protected final Log logger = LogFactory.getLog(getClass());
	
	private ToolManager toolManager;
	
	private ExtractionService extractionService;
	
	public Object fillBackingObject(Object incomingModel, Map request,
			Map session, Map application) throws Exception {
		
		MatrixSummaryBean data = (MatrixSummaryBean)incomingModel;
		
		String strScaffoldingId = (String)request.get("scaffolding_id");
		
		if (strScaffoldingId == null) {
			Placement placement = getToolManager().getCurrentPlacement();
			strScaffoldingId = placement.getPlacementConfig().getProperty(
					MatrixManager.EXPOSED_MATRIX_KEY);
		}
				
		//TODO: pull from more than just the current matrix
		
		Id scaffoldingId = getIdManager().getId(strScaffoldingId);
		Scaffolding scaffolding = getMatrixManager().getScaffolding(scaffoldingId);

		data.setScaffolding(scaffolding);
		
		String reload = (String)request.get("reload");
		if (reload != null) {
			extractionService.rehashLoader();
		}
		
		String loadJob = (String)request.get("load_job");
		String jobAlias = (String)request.get("job_alias");
		if (loadJob != null) {
			if (jobAlias != null) {
				logger.warn("Loading job from controller: " + loadJob + " as ID: " + jobAlias);
				extractionService.loadJob(loadJob, jobAlias);
			}
			else {
				logger.warn("Loading job from controller: " + loadJob);
				extractionService.loadJob(loadJob);
			}
		}
				
		String jobName = (String)request.get("job");
		String xslName = (String)request.get("xsl");
		String jobData = (String)request.get("data");
		
		logger.warn("Running: " + jobName + ", " + xslName);
		
		if (jobName == null || jobName.length() == 0) {
			jobName = "edu.umich.ctools.extraction.DHJobXml";
		}
		if (xslName == null || xslName.length() == 0) {
			xslName = "extract";
		}
		if (jobData == null || jobData.length() == 0) {
			jobData = strScaffoldingId;
		}
		
		
		if (jobName != null && jobName.length() > 0) {
			if (xslName != null && xslName.length() > 0) {
				//TODO: This should probably ask for the appropriate stream from the extractionService, probably from CHS				
				File xslFile = new File(extractionService.getXslPath() + xslName + ".xsl");
				TransformerFactory factory = TransformerFactory.newInstance();
				try {
					logger.warn("Trying to create transformer");
					Transformer transformer = factory.newTransformer(new StreamSource(xslFile));
					logger.warn("Putting transformer into model");
					request.put("renderer", transformer);
				} catch (TransformerConfigurationException e) {
					throw new IllegalArgumentException(e);
				}
				
				Document doc = new Document();
				if (jobData != null && jobData.length() > 0)
					doc.addContent(extractionService.runXmlJob(jobName, jobData));
				else
					doc.addContent(extractionService.runXmlJob(jobName));
				logger.warn("Putting document into model");
				request.put("document", doc);
				data.setShouldTransform(true);
				data.setRaw("");
			}
			else {
				if (jobData != null && jobData.length() > 0)
					data.setRaw(extractionService.runJob(jobName, jobData).toString());
				else
					data.setRaw(extractionService.runJob(jobName).toString());
			}
		}
		
		logger.warn("about to return incomingModel");
		return incomingModel;
	}
	
	public ModelAndView handleRequest(Object requestModel, Map request,
			Map session, Map application, Errors errors) {
		
		//MatrixSummaryBean data = (MatrixSummaryBean) requestModel;
		
		Map model = new HashMap();

		return new ModelAndView("success", model);
	}
	
	public ExtractionService getExtractionService() {
		return extractionService;
	}
	
	public void setExtractionService(ExtractionService service) {
		extractionService = service;
	}
	
	public ToolManager getToolManager() {
		return toolManager;
	}
	
	public void setToolManager(ToolManager manager) {
		toolManager = manager;
	}

}

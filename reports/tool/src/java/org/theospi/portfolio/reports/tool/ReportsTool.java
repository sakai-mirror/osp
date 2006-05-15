/**********************************************************************************
* $URL:https://source.sakaiproject.org/svn/osp/trunk/reports/tool/src/java/org/theospi/portfolio/reports/tool/ReportsTool.java $
* $Id:ReportsTool.java 9134 2006-05-08 20:28:42Z chmaurer@iupui.edu $
***********************************************************************************
*
* Copyright (c) 2005, 2006 The Sakai Foundation.
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
package org.theospi.portfolio.reports.tool;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import org.sakaiproject.exception.IdUnusedException;
import org.sakaiproject.site.api.Site;
import org.sakaiproject.site.cover.SiteService;
import org.sakaiproject.tool.api.Tool;
import org.sakaiproject.tool.cover.ToolManager;
import org.theospi.portfolio.reports.model.Report;
import org.theospi.portfolio.reports.model.ReportDefinition;
import org.theospi.portfolio.reports.model.ReportExecutionException;
import org.theospi.portfolio.reports.model.ReportFunctions;
import org.theospi.portfolio.reports.model.ReportResult;
import org.theospi.portfolio.reports.model.ReportsManager;
import org.theospi.portfolio.shared.tool.ToolBase;


/**
 * This class is the controller and model to the jsp view.<BR>
 * 
 * There is an inner class for allowing the report data classes to 
 * interact with the jsp.<BR>
 * 
 * Each session gets its own ReportsTool.<BR><BR>
 * 
 * &nbsp; &nbsp; Testing procedures:<BR>
 *
 *	Test the different parameter types
 *		Make sure the sql param is pulling data
 *  Test a live and a non-live report
 *  Save the results
 *  Re-run a live report, save results
 *  External dependencies:
 *  	worksite, users, tool, 
 * 
 * @author andersjb
 *
 */

public class ReportsTool extends ToolBase {
	
	/** A singlton manager for reports */
	private ReportsManager reportsManager = null;
	
	/** The reports to which the user has access */
	private List decoratedReportDefinition = null;
	
	/** The reportDefinition from which the tool is working with */
	private DecoratedReportDefinition workingReportDefinition = null;
	
	/** The report from which the tool is working with */
	private DecoratedReport workingReport = null;
	
	/** The reportresult from which the tool is working with */
	private DecoratedReportResult workingResult = null;

   private Site worksite = null;

   private Tool tool = null;
   private Map userCan = null;

	protected static final String mainPage = "main";
	protected static final String createReportPage = "processCreateReport";
	protected static final String createReportParamsPage = "processCreateReportParams";
	protected static final String reportResultsPage = "showReportResults";
	protected static final String saveResultsPage = "saveReportResults";

	/** when a live report is saved, tell the user */
	private boolean savedLiveReport = false;
	
	
	/**
	 * getter for the ReportsManager property
	 * @return ReportsManager
	 */
	public ReportsManager getReportsManager()
	{
		return reportsManager;
	}
	
	/**
	 * setter for the ReportsManager property
	 * @param reportsManager
	 */
	public void setReportsManager(ReportsManager reportsManager)
	{
		this.reportsManager = reportsManager;
	}

   public Tool getTool() {
      if (tool == null) {
         tool = ToolManager.getCurrentTool();
      }
      return tool;
   }

   public void setTool(Tool tool) {
      this.tool = tool;
   }

   public Site getWorksite() {
      if (worksite == null) {
         try {
            worksite = SiteService.getSite(ToolManager.getCurrentPlacement().getContext());
         }
         catch (IdUnusedException e) {
            throw new RuntimeException(e);
         }
      }
      return worksite;
   }

   public String getReportFunctionPrefix() {
      return ReportFunctions.REPORT_FUNCTION_PREFIX;
   }

   public String getPermissionsMessage() {
      return getMessageFromBundle("perm_description", new Object[]{
         getTool().getTitle(), getWorksite().getTitle()});
   }

	public void setWorkingReportDefinition(DecoratedReportDefinition workingReportDefinition)
	{
		this.workingReportDefinition = workingReportDefinition;
	}
	
	/**
	 * getter for the WorkingReportDefinition property
	 * @return DecoratedReportDefinition
	 */
	public DecoratedReportDefinition getWorkingReportDefinition()
	{
		return workingReportDefinition;
	}
	
	/**
	 * setter for the Working Report
	 * @param workingReport DecoratedReport
	 */
	public void setWorkingReport(DecoratedReport workingReport)
	{
		this.workingReport = workingReport;
	}
	
	/**
	 * getter for the WorkingReport property
	 * @return DecoratedReport
	 */
	public DecoratedReport getWorkingReport()
	{
		return workingReport;
	}
	
	public void setWorkingResult(DecoratedReportResult workingResult)
	{
		this.workingResult = workingResult;
      getReportsManager().setCurrentResult(workingResult.getReportResult());
	}
	
	/**
	 * getter for the WorkingReport property
	 * @return DecoratedReport
	 */
	public DecoratedReportResult getWorkingResult()
	{
		return workingResult;
	}
	
	/**
	 * This method gets the list of reports encapsulated by 
	 * DecoratedReportDefinition.
	 * @return List of DecoratedReportDefinition
	 */
	public List getReports()
	{
		if(decoratedReportDefinition == null)
		{
			List reportDefinitions = reportsManager.getReportDefinitions();
			decoratedReportDefinition = new ArrayList();
			
			Iterator iter = reportDefinitions.iterator();
			while(iter.hasNext()) {
				ReportDefinition reportDef =(ReportDefinition)iter.next();
				
				decoratedReportDefinition.add(new DecoratedReportDefinition(reportDef, this));
			}
		}
		return decoratedReportDefinition;
	}
	
	public List getResults()
	{
      List decoratedResults = new ArrayList();
		
		List results = reportsManager.getCurrentUserResults();
		

		Iterator iter = results.iterator();
		while(iter.hasNext()) {
			Object rr = iter.next();
			
			if(rr instanceof ReportResult)
				decoratedResults.add(new DecoratedReportResult((ReportResult)rr, this));
			else if (rr instanceof Report)
				decoratedResults.add(new DecoratedReport((Report)rr, this));
		}
		return decoratedResults;
	}
	
	/**
	 * Tells the interface if the live report was saved.  it goes to false
	 * after the message is complete.
	 */
	public boolean getSavedLiveReport()
	{
		boolean saved = savedLiveReport;
		return saved;
	}
	

	//***********************************************************
	//***********************************************************
	//	Actions for the JSP

	

	
	/**
	 * An action called from the JSP through the JSF framework.
	 * This is called when the user wants to move to the next screen
	 * @return String the next page
	 */
	public String processReportBaseProperties()
	{
		String nextPage = ReportsTool.createReportParamsPage;
		
		//	ensure that there is a title for the report
		if(getWorkingReport().testInvalidateTitle())
			nextPage = "";
		
		return nextPage;
	}
	
	/**
	 * An action called from the JSP through the JSF framework.
	 * Called when the user wants to stop creating a new report
	 * @return String the next page
	 */
	public String processCancelReport()
	{
		savedLiveReport = false;
		
		//	remove the working report
		setWorkingReport(null);
		
		return ReportsTool.mainPage;
	}
	
	public String processCancelExport()
	{
		savedLiveReport = false;
		return ReportsTool.reportResultsPage;
	}
	
	/**
	 * This goes from entering the parameter values to the results page
	 * @return Next page
	 */
	public String processEditParamsContinue()
	{
		//check that the parameters are all good
		if(!getWorkingReport().getParamsAreValid()) {
			
			String msg = "";
			for(Iterator iter = getWorkingReport().getReportParams().iterator(); iter.hasNext(); ) {
				DecoratedReportParam drp = (DecoratedReportParam)iter.next();
				
				if(!drp.getIsValid()) {
					if(msg.length() != 0)
						msg += "<BR />";
					msg += getMessageFromBundle("badParam_start");
					msg += drp.getReportDefinitionParam().getTitle();
					msg += getMessageFromBundle("badParam_mid");
					if(drp.getIsString())
						msg += getMessageFromBundle("badParam_string_reason");
					if(drp.getIsInteger())
						msg += getMessageFromBundle("badParam_int_reason");
					if(drp.getIsFloat())
						msg += getMessageFromBundle("badParam_float_reason");
					if(drp.getIsDate())
						msg += getMessageFromBundle("badParam_date_reason");
					msg += getMessageFromBundle("badParam_end");
				}
			}
			getWorkingReport().setParamErrorMessages(msg);
			return "";
		}
		
		try {
			//	get the results
			ReportResult result = reportsManager.generateResults(getWorkingReport().getReport());
			
			//	make it the working result
			setWorkingResult(new DecoratedReportResult(result, this));
			
			//	go to the results page
			return reportResultsPage;
		} catch(ReportExecutionException ree) {
			getWorkingReport().setParamErrorMessages(getMessageFromBundle("run_report_problem"));
			return "";
		}
	}
	
	public String processEditParamsBack()
	{
		return createReportPage;
	}
	
	public String processChangeViewXsl()
	{
		savedLiveReport = false;
		return reportResultsPage;
	}
	
	/**
	 * We want to use an action to forward to the helper.  We don't want
    * to forward to the permission helper in the jsp beause we need to 
    * clear out the cached permissions
	 * @return String unused
	 */
	public String processPermissions()
	{
	   ExternalContext context = FacesContext.getCurrentInstance().getExternalContext();
	    
	   userCan = null;

       
	   try {
          String url = "sakai.permissions.helper.helper/tool?" + 
            "session.sakaiproject.permissions.description=" + 
               getPermissionsMessage() + 
            "&session.sakaiproject.permissions.siteRef=" + 
               getWorksite().getReference() + 
            "&session.sakaiproject.permissions.prefix=" + 
               getReportFunctionPrefix();
           
	        context.redirect(url);
	   }
	   catch (IOException e) {
	        throw new RuntimeException("Failed to redirect to helper", e);
	   }
	   return null;
	}

	/**
	 * An action called from the JSP through the JSF framework.
	 * @return String the next page
	 */
	public String gotoOptions()
	{
		return mainPage;
	}
	
	public String processSaveResults()
	{
		savedLiveReport = false;
		return saveResultsPage;
	}
	public String processCancelSave()
	{
		savedLiveReport = false;
		return reportResultsPage;
	}
	public String processSaveResultsToDB()
	{
		savedLiveReport = false;
		reportsManager.saveReportResult(getWorkingResult().getReportResult());
		
		return reportResultsPage;
	}
	
	public String processSaveReport()
	{
		reportsManager.saveReport(getWorkingResult().getReportResult().getReport());
        savedLiveReport = true;
        
		return reportResultsPage;
	}
	
	/**
	 * this function loads the full report result and the report
	 * sets these in the tool
	 * @return String which page to go to next
	 */
	public String processSelectReportResult(DecoratedReportResult reportResult)
	{
		ReportResult result = reportsManager.loadResult(reportResult.getReportResult());
		Report report = result.getReport();
		
		setWorkingReport(new DecoratedReport(report, this));
		setWorkingResult(new DecoratedReportResult(result, this));
		
		return ReportsTool.reportResultsPage;
	}
	
	/**
	 * this function loads a live report.  It generates a new result,
	 * sets the report as having been saved (aka, it was loaded from the db)
	 * @param report DecoratedReport
	 * @return String the next page
	 */
	public String processSelectLiveReport(DecoratedReport report)
	{
		ReportResult result = reportsManager.generateResults(report.getReport());
		
		result.getReport().setIsSaved(true);
		
		//	make it the working result
		setWorkingReport(new DecoratedReport(result.getReport(), this));
		setWorkingResult(new DecoratedReportResult(result, this));
		
		return ReportsTool.reportResultsPage;
	}
   
   /**
    * When deleting a report result, delete the report result...
    * then if the report is not live, then delete the report as well
    * @param reportResult
    * @return String the next page
    */
   public String processDeleteReportResult(DecoratedReportResult reportResult)
   {
      reportsManager.deleteReportResult(reportResult.getReportResult());
      
      return "";
   }
   
   /**
    * 
    * @param report
    * @return String the next page
    */
   public String processDeleteLiveReport(DecoratedReport report)
   {
      reportsManager.deleteReport(report.getReport(), true);
      return "";
   }
   
   /**
    * 
    * @param report
    * @return String the next page
    */
   public String processEditLiveReport(DecoratedReport report)
   {
      getReportsManager().checkEditAccess();
      
      setWorkingReport(report);
      return createReportParamsPage;
   }

   public Map getUserCan() {
      if (userCan == null) {
         userCan = getReportsManager().getAuthorizationsMap();
      }
      return userCan;
   }

   public void setUserCan(Map userCan) {
      this.userCan = userCan;
   }

   public boolean isMaintainer() {
      return getReportsManager().isMaintaner();
   }

}
/*
 * The Open Source Portfolio Initiative Software is Licensed under the Educational Community License Version 1.0:
 *
 * This Educational Community License (the "License") applies to any original work of authorship
 * (the "Original Work") whose owner (the "Licensor") has placed the following notice immediately
 * following the copyright notice for the Original Work:
 *
 * Copyright (c) 2004 Trustees of Indiana University and r-smart Corporation
 *
 * This Original Work, including software, source code, documents, or other related items, is being
 * provided by the copyright holder(s) subject to the terms of the Educational Community License.
 * By obtaining, using and/or copying this Original Work, you agree that you have read, understand,
 * and will comply with the following terms and conditions of the Educational Community License:
 *
 * Permission to use, copy, modify, merge, publish, distribute, and sublicense this Original Work and
 * its documentation, with or without modification, for any purpose, and without fee or royalty to the
 * copyright holder(s) is hereby granted, provided that you include the following on ALL copies of the
 * Original Work or portions thereof, including modifications or derivatives, that you make:
 *
 * - The full text of the Educational Community License in a location viewable to users of the
 * redistributed or derivative work.
 *
 * - Any pre-existing intellectual property disclaimers, notices, or terms and conditions.
 *
 * - Notice of any changes or modifications to the Original Work, including the date the changes were made.
 *
 * - Any modifications of the Original Work must be distributed in such a manner as to avoid any confusion
 *  with the Original Work of the copyright holders.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
 * SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 * The name and trademarks of copyright holder(s) may NOT be used in advertising or publicity pertaining
 * to the Original or Derivative Works without specific, written prior permission. Title to copyright
 * in the Original Work and any associated documentation will at all times remain with the copyright holders.
 *
 * $Header: /opt/CVS/osp2.x/presentation/tool/src/java/org/theospi/portfolio/presentation/model/VelocityMailMessage.java,v 1.1 2005/09/16 17:34:53 chmaurer Exp $
 * $Revision: 3474 $
 * $Date: 2005-11-03 18:05:53 -0500 (Thu, 03 Nov 2005) $
 */

package org.theospi.tool.reports;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.sakaiproject.service.framework.portal.cover.PortalService;
import org.sakaiproject.service.legacy.site.Site;
import org.sakaiproject.service.legacy.site.cover.SiteService;
import org.sakaiproject.exception.IdUnusedException;
import org.sakaiproject.api.kernel.tool.Tool;
import org.sakaiproject.api.kernel.tool.cover.ToolManager;
import org.theospi.portfolio.shared.tool.ToolBase;
import org.theospi.portfolio.reports.model.*;
import org.theospi.api.app.reports.*;


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
            worksite = SiteService.getSite(PortalService.getCurrentSiteId());
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
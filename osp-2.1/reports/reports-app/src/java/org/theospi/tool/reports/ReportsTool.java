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

public class ReportsTool 
{
	/** A singlton manager for reports */
	private ReportsManager reportsManager = null;
	
	/** The reports to which the user has access */
	private List decoratedReportDefinition = null;
	
	/** The reportDefinition from which the tool is working with */
	private DecoratedReportDefinition workingReportDefinition = null;
	
	/** The report from which the tool is working with */
	private DecoratedReportDefinition workingReport = null;
	
	/** The reportresult from which the tool is working with */
	private DecoratedReportDefinition workingReportResult = null;

	private static final String mainPage = "main";
	private static final String genReportPage = "processCreateReport";

	/**
	 * getter for the ReportsManager property
	 * @return ReportsManager
	 */
	public ReportsManager getReportsManager()
	{
		return reportsManager;
	}
	
	/**
	 * setter for the ReportsManager
	 * @param reportsManager ReportsManager
	 */
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
	 * setter for the WorkingReportDefinition
	 * @param WorkingReportDefinition DecoratedReportDefinition
	 */
	public void setReportsManager(ReportsManager reportsManager)
	{
		this.reportsManager = reportsManager;
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
			List reportDefinitions = reportsManager.getReports();
			decoratedReportDefinition = new ArrayList();
			
			Iterator iter = reportDefinitions.iterator();
			while(iter.hasNext()) {
				ReportDefinition reportDef =(ReportDefinition)iter.next();
				
				decoratedReportDefinition.add(new DecoratedReportDefinition(reportDef));
			}
		}
		return decoratedReportDefinition;
	}
	

	//***********************************************************
	//***********************************************************
	//	Actions for the JSP
	
	public String gotoOptions()
	{
		return mainPage;
	}


	//***********************************************************
	//***********************************************************
	//	Controller Encapsulation Classes
	
	/**
	 * This class allows the ReportDefinition to interact with
	 *
	 */
	public class DecoratedReportDefinition {
		
		private ReportDefinition	reportDefinition = null;	
		
		public DecoratedReportDefinition(ReportDefinition reportDefinition)
		{
			this.reportDefinition = reportDefinition;
		}
		
		public ReportDefinition getReportDefinition()
		{
			return reportDefinition;
		}
		
		public String selectReportDefinition()
		{
			setWorkingReportDefinition(this);
			
			Report report = reportsManager.createReport(reportDefinition);
			
			report.setTitle("Report Title");
			report.setKeywords("keywords for report");
			report.setDescription("Description of report");
			report.setIsLive(false);
			
			List params = report.getReportParams();
			
			ReportResult result = reportsManager.generateResults(report);
			
			return genReportPage;
		}
	}
}
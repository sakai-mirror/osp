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

import javax.faces.model.SelectItem;

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
	private DecoratedReport workingReport = null;
	
	/** The reportresult from which the tool is working with */
	private DecoratedReportResult workingReportResult = null;

	private static final String mainPage = "main";
	private static final String createReportPage = "processCreateReport";
	private static final String createReportParamsPage = "processCreateReportParams";

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
	 * @param ReportsManager
	 */
	public void setReportsManager(ReportsManager reportsManager)
	{
		this.reportsManager = reportsManager;
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


	/**
	 * An action called from the JSP through the JSF framework.
	 * @return String the next page
	 */
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

		/**
		 * An action called from the JSP through the JSF framework.
		 * @return String the next page
		 */
		public String selectReportDefinition()
		{
			setWorkingReportDefinition(this);
			
			setWorkingReport(new DecoratedReport( reportsManager.createReport(reportDefinition) ));
			
			return createReportPage;
		}
	}
	
	/**
	 * This class allows the Report to interact with the view
	 *
	 */
	public class DecoratedReport {
		
		private Report	report = null;
		private List	reportParams = null;
		
		private boolean	invalidTitle = false;
		
		public DecoratedReport(Report report)
		{
			this.report = report;
		}
		
		public Report getReport()
		{
			return report;
		}
		
		public List getReportParams()
		{
			if(reportParams == null) {
				reportParams = new ArrayList();
					if(report.getReportParams() != null) {
					Iterator iter = report.getReportParams().iterator();
					while(iter.hasNext()) {
						ReportParam rp = (ReportParam)iter.next();
						
						reportParams.add(new DecoratedReportParam(rp));
					}
				}
			}
			return reportParams;
		}
		
		public boolean getInvalidTitle()
		{
			return invalidTitle;
		}
		
		/**
		 * An action called from the JSP through the JSF framework.
		 * This is called when the user wants to move to the next screen
		 * @return String the next page
		 */
		public String processReportBaseProperties()
		{
			String nextPage = createReportParamsPage;
			
			//	reset all the vars
			invalidTitle = false;
			
			//	if no title, then error
			if(report.getTitle() == null || report.getTitle().trim().equals("")) {
				nextPage = "";
				invalidTitle = true;
			}
			
			
			return nextPage;
		}
		
		/**
		 * An action called from the JSP through the JSF framework.
		 * Called when the user wants to stop creating a new report
		 * @return String the next page
		 */
		public String processCancel()
		{
			//	remove the working report
			setWorkingReport(null);
			
			return mainPage;
		}
		
		public String processEditParamsContinue()
		{
			//do something with the params
			List params = report.getReportParams();
			
			ReportResult result = reportsManager.generateResults(report);
			
			return mainPage;
		}
	}
	
	/**
	 * This class allows the ReportResult to interact with the view
	 *
	 */
	public class DecoratedReportParam {
		private ReportParam reportParam;
		
		public DecoratedReportParam(ReportParam reportParam)
		{
			setReportParam(reportParam);
		}
		public ReportParam getReportParam()
		{
			return reportParam;
		}
		public void setReportParam(ReportParam reportParam)
		{
			this.reportParam = reportParam;
		}
		public ReportDefinitionParam getReportDefinitionParam()
		{
			return reportParam.getReportDefinitionParam();
		}
		
		
		/**
		 * gets the list of possible titles and values
		 * @return List of ...
		 */
		public List getSelectableValues()
		{
			ArrayList array = new ArrayList();
			if(getIsSet()) {
				if(getIsDynamic()) {
					
				} else {
					String strSet = reportParam.getReportDefinitionParam().getValue();
					strSet = strSet.substring(strSet.indexOf("[")+1, strSet.indexOf("]"));
					String[] set = strSet.split(",");
					
					for(int i = 0; i < set.length; i++) {
						String element = set[i].trim();
						
						element = reportsManager.replaceSystemValues(element);
						
						if(element.indexOf("(") != -1) {
							element = element.substring(element.indexOf("(")+1, element.indexOf(")"));
							
							String[] elementData = element.split(";");
							if(elementData.length == 0)
								array.add(new SelectItem());
							if(elementData.length == 1)
								array.add(new SelectItem(elementData[0].trim()));
							if(elementData.length > 1)
								array.add(new SelectItem(elementData[0].trim(), elementData[1].trim()));
						} else {
							array.add(new SelectItem(element));
						}
					}
				}
			}
			
			return array;
		}
		
		
		/**
		 * tells whether this parameter is a set
		 * @return boolean
		 */
		public boolean getIsSet()
		{
			String type = reportParam.getReportDefinitionParam().getValueType();
			return type.equals(ReportDefinitionParam.VALUE_TYPE_ONE_OF_SET) ||
					type.equals(ReportDefinitionParam.VALUE_TYPE_ONE_OF_QUERY)||
					type.equals(ReportDefinitionParam.VALUE_TYPE_MULTI_OF_SET) ||
					type.equals(ReportDefinitionParam.VALUE_TYPE_MULTI_OF_QUERY);
		}
		
		
		/**
		 * tells whether this parameter is the result of a sql query
		 * @return boolean
		 */
		public boolean getIsDynamic()
		{
			String type = reportParam.getReportDefinitionParam().getValueType();
			return type.equals(ReportDefinitionParam.VALUE_TYPE_ONE_OF_QUERY)||
					type.equals(ReportDefinitionParam.VALUE_TYPE_MULTI_OF_QUERY);
		}
		
		
		/**
		 * tells whether this parameter can have multiple values selected
		 * @return boolean
		 */
		public boolean getIsMultiSelectable()
		{
			String type = reportParam.getReportDefinitionParam().getValueType();
			return type.equals(ReportDefinitionParam.VALUE_TYPE_MULTI_OF_SET) ||
					type.equals(ReportDefinitionParam.VALUE_TYPE_MULTI_OF_QUERY);
		}
		
		
		/**
		 * tells whether this parameter is a fill in value
		 * @return boolean
		 */
		public boolean getIsFillIn()
		{
			return reportParam.getReportDefinitionParam().getValueType().equals(
							ReportDefinitionParam.VALUE_TYPE_FILLIN);
		}
		
		
		/**
		 * tells whether this parameter is a static value
		 * @return boolean
		 */
		public boolean getIsStatic()
		{
			return reportParam.getReportDefinitionParam().getValueType().equals(
							ReportDefinitionParam.VALUE_TYPE_STATIC);
		}
	}
	
	/**
	 * This class allows the ReportResult to interact with the view
	 *
	 */
	public class DecoratedReportResult {
		
	}
}
/**********************************************************************************
* $URL:https://source.sakaiproject.org/svn/osp/trunk/reports/tool/src/java/org/theospi/portfolio/reports/tool/DecoratedReportDefinition.java $
* $Id:DecoratedReportDefinition.java 9134 2006-05-08 20:28:42Z chmaurer@iupui.edu $
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

import org.theospi.portfolio.reports.model.ReportDefinition;

/**
 * This class allows the ReportDefinition to interact with
 *
 */
public class DecoratedReportDefinition {

	/** The link to the main tool */
	private ReportsTool	reportsTool = null;
	
	/** The report definition to decorate */
	private ReportDefinition	reportDefinition = null;	
	
	public DecoratedReportDefinition(ReportDefinition reportDefinition, ReportsTool reportsTool)
	{
		this.reportDefinition = reportDefinition;
		this.reportsTool = reportsTool;
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
		reportsTool.setWorkingReportDefinition(this);
		
		reportsTool.setWorkingReport(new DecoratedReport( reportsTool.getReportsManager().createReport(reportDefinition), reportsTool ));
		
		return ReportsTool.createReportPage;
	}
}
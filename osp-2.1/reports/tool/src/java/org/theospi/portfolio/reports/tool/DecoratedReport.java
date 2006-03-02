/**********************************************************************************
* $URL$
* $Id$
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

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.theospi.portfolio.reports.model.ReportParam;
import org.theospi.portfolio.reports.model.Report;
import org.theospi.portfolio.reports.tool.DecoratedAbstractResult;

/**
 * This class allows the Report to interact with the view
 *
 */
public class DecoratedReport implements DecoratedAbstractResult {

	/** The link to the main tool */
	private ReportsTool	reportsTool = null;
	
	/** The report to decorate */
	private Report	report = null;
	
	/** The decorated report parameters */
	private List	reportParams = null;
	
	/** for telling the interface when a parameter is not correct */
	private String	paramErrorMsgs = "";
	
	/** informs the interface if the title is not proper */
	private boolean	invalidTitle = false;
	
	public DecoratedReport(Report report, ReportsTool reportsTool)
	{
		this.report = report;
		this.reportsTool = reportsTool;
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
					
					DecoratedReportParam drp = new DecoratedReportParam(rp, reportsTool);
					drp.setIndex(reportParams.size());
					reportParams.add(drp);
				}
			}
		}
		return reportParams;
	}
	
	public boolean getInvalidTitle()
	{
		return invalidTitle;
	}
	
	public boolean getIsSaved()
	{
		return report.getIsSaved();
	}

	public boolean testInvalidateTitle()
	{
		//	reset all the vars
		invalidTitle = false;
		
		//	if no title, then error
		if(report.getTitle() == null || report.getTitle().trim().equals(""))
			invalidTitle = true;
			
		return invalidTitle;
	}
   
   
   /**
    * this function loads the full report result and the report
    * sets these in the tool
    * @return String which page to go to next
    */
   public String processSelectReportResult()
   {
      return reportsTool.processSelectLiveReport(this);
   }
   
   
   /**
    * this function deletes the full report result and the report
    * sets these in the tool
    * @return String which page to go to next
    */
   public String processDelete()
   {
      return reportsTool.processDeleteLiveReport(this);
   }

	public String getResultType()
	{
		return DecoratedAbstractResult.REPORT;
	}
	
	public String getTitle()
	{
		return report.getTitle();
	}
	
	public Date getCreationDate()
	{
		return report.getCreationDate();
	}
	
	public boolean getIsLive()
	{
		return report.getIsLive();
	}
	
	public boolean getParamsAreValid()
	{
		boolean isGood = true;
		
		paramErrorMsgs = "";
		
		for(Iterator iter = reportParams.iterator(); iter.hasNext(); ) {
			DecoratedReportParam drp = (DecoratedReportParam)iter.next();
			
			isGood &= drp.getIsValid();
		}
		return isGood;
	}
	
	public void setParamErrorMessages(String paramErrorMsgs)
	{
		this.paramErrorMsgs = paramErrorMsgs;
	}
	
	public String getParamErrorMessages()
	{
		return paramErrorMsgs;
	}
}

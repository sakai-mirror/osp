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

import org.theospi.portfolio.reports.model.*;
import org.theospi.portfolio.reports.tool.DecoratedAbstractResult;

import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import javax.faces.model.SelectItem;
import javax.faces.event.ValueChangeEvent;

/**
 * This class allows the ReportResult to interact with the view
 *
 */
public class DecoratedReportResult implements DecoratedAbstractResult {
	
	
	/** The link to the main tool */
	private ReportsTool	reportsTool = null;

	private ReportResult reportResult = null;
	private Report report = null;
	
	private String currentViewXsl = null;
	private String currentExportXsl = null;

	public DecoratedReportResult(ReportResult reportResult, ReportsTool reportsTool)
	{
		this.reportResult = reportResult;
		this.reportsTool = reportsTool;
		this.report = reportResult.getReport();
      getExportXslSeletionList();
	}
	
	public Report getReport()
	{
		return report;
	}
	
	public ReportResult getReportResult()
	{
		return reportResult;
	}
	
	public List getViewXslSeletionList()
	{
		List options = new ArrayList();

		if(report.getReportDefinition() != null && report.getReportDefinition().getXsls() != null) {
			Iterator iter = report.getReportDefinition().getXsls().iterator();
			
			while(iter.hasNext()) {
				ReportXsl xsl = (ReportXsl)iter.next();
				
				if(!xsl.getIsExport()) {
					String xslTitle = xsl.getTitle();
					if(xslTitle == null || xslTitle.trim().length() == 0)
						xslTitle = xsl.getXslLink();
					options.add(new SelectItem(xsl.getXslLink(), xslTitle));
				}
			}
		}
		return options;
	}
	
	public List getExportXslSeletionList()
	{
		List options = new ArrayList();

		if(report.getReportDefinition() != null && report.getReportDefinition().getXsls() != null) {
			Iterator iter = report.getReportDefinition().getXsls().iterator();
			
			while(iter.hasNext()) {
				ReportXsl xsl = (ReportXsl)iter.next();
				
				if(xsl.getIsExport()) {
               if (getCurrentExportXsl() == null) {
                  setCurrentExportXsl(xsl.getRuntimeId());
               }
					String xslTitle = xsl.getTitle();
					if(xslTitle == null || xslTitle.trim().length() == 0)
						xslTitle = xsl.getXslLink();
					options.add(new SelectItem(xsl.getRuntimeId(), xslTitle));
				}
			}
		}
		return options;
	}

   public void changeExportXsl(ValueChangeEvent event) {
      event.getComponent();
   }

   public String getCurrentExportLink() {
      ReportXsl xsl = getExportXsl();

      String extention = "";
      if (xsl.getExtension() != null) {
         extention = "." + xsl.getExtension();
      }
      try {
         return "repository/" + "manager=" + ReportsManager.class.getName() + "&" +
               ReportsManager.EXPORT_XSL_ID + "=" +
               URLEncoder.encode(xsl.getRuntimeId(), "UTF-8") + "/" +
               URLEncoder.encode(getReport().getTitle() + extention, "UTF-8");
      }
      catch (UnsupportedEncodingException e) {
         throw new RuntimeException(e);
      }
   }

   public ReportXsl getExportXsl() {
      ReportXsl xsl = getReport().getReportDefinition().findReportXslByRuntimeId(getCurrentExportXsl());
      return xsl;
   }

   public boolean isExportable() {
      List xsls = getExportXslSeletionList();

      return xsls != null && xsls.size() > 0;
   }

   protected ReportXsl getCurrentView() {
      return getView(getCurrentViewXsl());
   }

   public String getCurrentViewXsl()
	{
		if(currentViewXsl == null) {
			return report.getReportDefinition().getDefaultXsl().getXslLink();
		}
		return currentViewXsl;
	}
	
	public void setCurrentViewXsl(String currentViewXsl)
	{
		if(isAView(currentViewXsl))
			this.currentViewXsl = currentViewXsl;
	}
	
	public String getCurrentExportXsl()
	{
		return currentExportXsl;
	}
	
	public void setCurrentExportXsl(String currentExportXsl)
	{
		this.currentExportXsl = currentExportXsl;
	}
	
	public boolean getIsSaved()
	{
		return reportResult.getIsSaved();
	}
	
	/**
	 * this function loads the full report result and the report
	 * sets these in the tool
	 * @return String which page to go to next
	 */
	public String processSelectReportResult()
	{
		return reportsTool.processSelectReportResult(this);
	}
   
   
   /**
    * this function deletes the full report result and the report
    * sets these in the tool
    * @return String which page to go to next
    */
   public String processDelete()
   {
      return reportsTool.processDeleteReportResult(this);
   }
	
	
	/**
	 * This generates the final html based on the selected XSL and the result (xml).
	 * @return String
	 */
	public String getCurrentViewResults()
	{
		//use the getter for the currentViewXsl so we handle null correctly
		return reportsTool.getReportsManager().transform(reportResult, getCurrentView());		
	}

   /**
	 * This is the tester for whether a view is in the list of views (not exports)
	 * @param view String
	 * @return boolean
	 */
	private boolean isAView(String view)
	{
      return getView(view) != null;
	}

   protected ReportXsl getView(String link) {
      ReportDefinition rdef = report.getReportDefinition();

      Iterator iter = rdef.getXsls().iterator();

      while(iter.hasNext()) {
         ReportXsl xsl = (ReportXsl)iter.next();

         if(!xsl.getIsExport())
            if(xsl.getXslLink().equals(link))
               return xsl;
      }
      return null;
   }

	public String getResultType()
	{
		return DecoratedAbstractResult.RESULT;
	}
	
	public String getTitle()
	{
		return reportResult.getTitle();
	}
	
	public Date getCreationDate()
	{
		return reportResult.getCreationDate();
	}
	
	public boolean getIsLive()
	{
		return report.getIsLive() && !reportResult.getIsSaved();
	}
}
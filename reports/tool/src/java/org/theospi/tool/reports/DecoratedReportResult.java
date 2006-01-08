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

import org.theospi.api.app.reports.*;
import org.theospi.portfolio.reports.model.*;

import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import javax.faces.model.SelectItem;
import javax.faces.event.ActionEvent;
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
	 * This generates the final html based on the selected XSL and the result (xml).
	 * @return String
	 */
	public String getCurrentViewResults()
	{
		//use the getter for the currentViewXsl so we handle null correctly
		return reportsTool.getReportsManager().transform(reportResult, getCurrentViewXsl());
		
	}
	
	/**
	 * This is the tester for whether a view is in the list of views (not exports)
	 * @param view String
	 * @return boolean
	 */
	private boolean isAView(String view)
	{
		ReportDefinition rdef = report.getReportDefinition();
		
		Iterator iter = rdef.getXsls().iterator();
		
		while(iter.hasNext()) {
			ReportXsl xsl = (ReportXsl)iter.next();
			
			if(!xsl.getIsExport())
				if(xsl.getXslLink().equals(view))
					return true;
		}
		return false;
	}
	
	/**
	 * This is the tester for whether a export xsl is in the list of export xsls (does not consider the views)
	 * @param view String
	 * @return boolean
	 */
	private boolean isAnExport(String view)
	{
		ReportDefinition rdef = report.getReportDefinition();
		
		Iterator iter = rdef.getXsls().iterator();
		
		while(iter.hasNext()) {
			ReportXsl xsl = (ReportXsl)iter.next();
			
			if(xsl.getIsExport())
				if(xsl.getXslLink().equals(view))
					return true;
		}
		return false;
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
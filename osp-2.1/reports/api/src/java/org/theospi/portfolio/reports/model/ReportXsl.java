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
 * $Revision$
 * $Date$
 */

package org.theospi.portfolio.reports.model;

import org.sakaiproject.metaobj.shared.model.Id;
import org.theospi.portfolio.reports.model.ReportDefinition;
import org.theospi.portfolio.reports.model.ResultsPostProcessor;

public class ReportXsl
{

   /** The primary key */
	private Id	reportXslId = null;

	/** the link to the report definition */
	private ReportDefinition reportDefinition = null;

	/** whether or not this xsl is for export or view */
	private boolean isExport = false;

	/** the xsl location */
	private String xslLink;

	/** the title */
	private String title;

	/** the contentType */
	private String contentType;

	/** the extension */
	private String extension;

   private ResultsPostProcessor resultsPostProcessor;

   private String target = "_blank";

	/**
	 * the getter for the reportId property
	 */
	public ReportXsl()
	{
		
	}

	public Id getReportXslId()
	{
		return reportXslId;
	}

	public void setReportXslId(Id reportXslId)
	{
		this.reportXslId = reportXslId;
	}

	/**
	 * the getter for the reportDefinition property
	 * @return ReportDefinition the unique identifier
	 */
	public ReportDefinition getReportDefinition()
	{
		return reportDefinition;
	}
	
	
	/**
	 * the setter for the reportDefinition property.  This is set by the bean 
	 * and by hibernate.
	 * @param reportDefinition String
	 */
	public void setReportDefinition(ReportDefinition reportDefinition)
	{
		this.reportDefinition = reportDefinition;
	}
	
	
	/**
	 * the getter for the isExport property
	 * @return boolean the isExport
	 */
	public boolean getIsExport()
	{
		return isExport;
	}
	
	
	/**
	 * the setter for the isExport property.  This is set by the bean 
	 * and by hibernate.
	 * @param isExport boolean
	 */
	public void setIsExport(boolean isExport)
	{
		this.isExport = isExport;
	}
	
	
	/**
	 * the getter for the xslLink property
	 * @return String the xslLink
	 */
	public String getXslLink()
	{
		return xslLink;
	}
	
	
	/**
	 * the setter for the xslLink property.  This is set by the bean 
	 * and by hibernate.
	 * @param xslLink String
	 */
	public void setXslLink(String xslLink)
	{
		this.xslLink = xslLink;
	}
	
	
	/**
	 * the getter for the title property
	 * @return String the title
	 */
	public String getTitle()
	{
		return title;
	}
	
	
	/**
	 * the setter for the title property.  This is set by the bean 
	 * and by hibernate.
	 * @param title String
	 */
	public void setTitle(String title)
	{
		this.title = title;
	}
	
	
	/**
	 * the getter for the contentType property
	 * @return String the contentType
	 */
	public String getContentType()
	{
		return contentType;
	}
	
	
	/**
	 * the setter for the contentType property.  This is set by the bean 
	 * and by hibernate.
	 * @param contentType String
	 */
	public void setContentType(String contentType)
	{
		this.contentType = contentType;
	}
	
	
	/**
	 * the getter for the extension property
	 * @return String the extension
	 */
	public String getExtension()
	{
		return extension;
	}
	
	
	/**
	 * the setter for the extension property.  This is set by the bean 
	 * and by hibernate.
	 * @param extension String
	 */
	public void setExtension(String extension)
	{
		this.extension = extension;
	}

   public ResultsPostProcessor getResultsPostProcessor() {
      return resultsPostProcessor;
   }

   public void setResultsPostProcessor(ResultsPostProcessor resultsPostProcessor) {
      this.resultsPostProcessor = resultsPostProcessor;
   }

   /** return the singleton's object id, this will be unique and permanent until the next restart **/
   public String getRuntimeId() {
      return this.toString().hashCode() + "";
   }

   public String getTarget() {
      return target;
   }

   public void setTarget(String target) {
      this.target = target;
   }
}
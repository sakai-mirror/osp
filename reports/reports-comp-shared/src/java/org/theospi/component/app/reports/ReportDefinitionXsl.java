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

package org.theospi.component.app.reports;

import java.util.List;

public class ReportDefinitionXsl
{
	/** the identifier for the report definition XSL */
	private String reportDefXslId;
	
	/** the unique identifier for the report definition XSL */
	private ReportDefinition reportDefinition;

	/** the fileLink of the report definition XSL */
	private String fileLink;

	/** the property of whether the xsl is for "view" or "export" */
	private String isExport;

	
	/**
	 * the getter for the reportDefXslId property
	 * @return String the unique identifier
	 */
	public String getReportDefXslId()
	{
		return reportDefXslId;
	}
	
	
	/**
	 * the setter for the reportDefXslId property.  This is set by the bean 
	 * and by hibernate.
	 * @param reportDefXslId String
	 */
	public void setReportDefXslId(String reportDefXslId)
	{
		this.reportDefXslId = reportDefXslId;
	}
	
	/**
	 * the getter for the reportDefinition property
	 * @return String the unique identifier
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
	 * the getter for the fileLink property
	 * @return String the fileLink
	 */
	public String getFileLink()
	{
		return fileLink;
	}
	
	
	/**
	 * the setter for the fileLink property.  This is set by the bean 
	 * and by hibernate.
	 * @param fileLink String
	 */
	public void setFileLink(String fileLink)
	{
		this.fileLink = fileLink;
	}
	
	
	/**
	 * the getter for the isExport property
	 * @return String the isExport
	 */
	public String getIsExport()
	{
		return isExport;
	}
	
	
	/**
	 * the setter for the fileLink property.  This is set by the bean 
	 * and by hibernate.
	 * @param isExport String
	 */
	public void setIsExport(String isExport)
	{
		this.isExport = isExport;
	}
	
}
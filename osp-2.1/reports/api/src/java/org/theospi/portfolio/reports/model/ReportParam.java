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

import java.util.ArrayList;
import java.util.List;

import org.sakaiproject.metaobj.shared.model.Id;

public class ReportParam
{
	/** the identifier to the report paramater */
	private Id paramId = null;
	
	/** the identifier to the report definition for the paramater */
	private Report report = null;
	
	/** the reportDefParamId for the report definition parameter */
	private ReportDefinitionParam reportDefinitionParam = null;


	/** the type for the report definition Parameter 
	 * 	This is validation rules for fillin parameters,
	 * 	a set of strings for sets (both value and title),
	 * and the query if the value type is a sql query
	 */
	private String value = null;

	/** true when the value passes the test and false when the value changes */
	private boolean	validated = false;
	
	/** the results of the last validation */
	private boolean	valid = false;
	
	private String	reportDefParamIdMark = null;
	
	/**
	 * the getter for the paramId property
	 * @return String the unique identifier
	 */
	public Id getParamId()
	{
		return paramId;
	}
	
	
	/**
	 * the setter for the paramId property.  This is set by the bean 
	 * and by hibernate.
	 * @param paramId String
	 */
	public void setParamId(Id paramId)
	{
		this.paramId = paramId;
	}
	
	
	/**
	 * the getter for the report property
	 * @return String the unique identifier
	 */
	public Report getReport()
	{
		return report;
	}
	
	
	/**
	 * the setter for the report property.  This is set by the bean 
	 * and by hibernate.
	 * @param report Report
	 */
	public void setReport(Report report)
	{
		this.report = report;
	}
	
	/**
	 * the getter for the reportDefinitionParam property
	 * @return ReportDefinitionParam the unique identifier
	 */
	public ReportDefinitionParam getReportDefinitionParam()
	{
		return reportDefinitionParam;
	}
	
	
	/**
	 * the setter for the reportDefinitionParam property.  This is set by the bean 
	 * and by hibernate.
	 * @param reportDefinitionParam String
	 */
	public void setReportDefinitionParam(ReportDefinitionParam reportDefinitionParam)
	{
		this.reportDefinitionParam = reportDefinitionParam;
	}
	
	
	
	/**
	 * This is a way of separating the report definition from the report in the database
	 * this is a temp solution while the report definitions aren't being stored in the database
	 * @return String
	 */
	public String getReportDefParamIdMark()
	{
		if(reportDefinitionParam == null)
			return reportDefParamIdMark;
		return reportDefinitionParam.getIdString();
	}
	
	/**
	 * this is the link to report definition
	 * @param reportDefIdMark String
	 */
	public void setReportDefParamIdMark(String reportDefParamIdMark)
	{
		reportDefinitionParam = null;
		this.reportDefParamIdMark = reportDefParamIdMark;
	}
	
	
	/**
	 * the getter for the value property
	 * @return String the value
	 */
	public String getValue()
	{
		return value;
	}
	
	
	/**
	 * the setter for the value property.  This is set by the bean or the user 
	 * and by hibernate.
	 * @param value String
	 */
	public void setValue(String value)
	{
		this.value = value;
		validated = false;
	}
	
	
	/**
	 * Checks to make sure that the value can be selected.
	 * Apply validation rules, check against set, check against the sql results
	 * @return boolean
	 */
	public boolean valid()
	{
		if(value == null)
			return false;
		if(!validated) {
			valid = true;
			
			//do the check here
			
			validated = true;
		}
		return valid;
	}
}
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

package org.theospi.api.app.reports;

import java.util.List;

public class ReportDefinitionParam
{
	/** the reportDefParamId for the report definition parameter */
	private String reportDefParamId;
	
	/** the identifier to the report definition for the paramater */
	private ReportDefinition reportDefinition;

	/** the parameter name of the report definition parameter */
	private String paramName;

	/** the description for the report definition parameter */
	private String description;

	/** the type for the report definition Parameter parameter
	 *  This is defined as "int", "float", "String", "date"
	 */
	private String type;

	/** the valueType for the report definition Parameter 
	 *	The value type
	 */
	private String valueType;

	public static final String VALUE_TYPE_FILLIN = "fillin";
	public static final String VALUE_TYPE_ONE_OF_SET = "set";
	public static final String VALUE_TYPE_MULTI_OF_SET = "multiset";
	public static final String VALUE_TYPE_ONE_OF_QUERY = "sql";
	public static final String VALUE_TYPE_MULTI_OF_QUERY = "multisql";
	public static final String VALUE_TYPE_STATIC = "static";

	/** the type for the report definition Parameter 
	 * 	This is validation rules for fillin parameters,
	 * 	a set of strings for sets (both value and title),
	 *  the query if the value type is a sql query,
	 *  or the static value
	 */
	private String value;

	
	
	/**
	 * the getter for the reportDefParamId property
	 * @return String the unique identifier
	 */
	public String getReportDefParamId()
	{
		return reportDefParamId;
	}
	
	
	/**
	 * the setter for the reportDefParamId property.  This is set by the bean 
	 * and by hibernate.
	 * @param reportDefId String
	 */
	public void setReportDefParamId(String reportDefParamId)
	{
		this.reportDefParamId = reportDefParamId;
	}
	
	
	/**
	 * the getter for the ReportDefinition property
	 * @return String the unique identifier
	 */
	public ReportDefinition getReportDefinition()
	{
		return reportDefinition;
	}
	
	
	/**
	 * the setter for the ReportDefinition property.  This is set by the bean 
	 * and by hibernate.
	 * @param ReportDefinition String
	 */
	public void setReportDefinition(ReportDefinition reportDefinition)
	{
		this.reportDefinition = reportDefinition;
	}
	
	
	/**
	 * the getter for the paramName property
	 * @return String the paramName
	 */
	public String getParamName()
	{
		return paramName;
	}
	
	
	/**
	 * the setter for the paramName property.  This is set by the bean 
	 * and by hibernate.
	 * @param paramName String
	 */
	public void setParamName(String paramName)
	{
		this.paramName = paramName;
	}
	
	
	/**
	 * the getter for the description property
	 * @return String the description
	 */
	public String getDescription()
	{
		return description;
	}
	
	
	/**
	 * the setter for the description property.  This is set by the bean 
	 * and by hibernate.
	 * @param description String
	 */
	public void setDescription(String description)
	{
		this.description = description;
	}
	
	
	/**
	 * the getter for the type property
	 * @return String the type
	 */
	public String getType()
	{
		return type;
	}
	
	
	/**
	 * the setter for the type property.  This is set by the bean 
	 * and by hibernate.
	 * @param type String
	 */
	public void setType(String type)
	{
		this.type = type.trim();
	}
	
	
	/**
	 * the getter for the valueType property
	 * @return String the valueType
	 */
	public String getValueType()
	{
		return valueType;
	}
	
	
	/**
	 * the setter for the valueType property.  This is set by the bean 
	 * and by hibernate.
	 * @param valueType String
	 */
	public void setValueType(String valueType)
	{
		this.valueType = valueType.trim();
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
	 * the setter for the value property.  This is set by the bean 
	 * and by hibernate.
	 * @param value String
	 */
	public void setValue(String value)
	{
		this.value = value.trim();
	}
}
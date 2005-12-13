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
import java.util.List;

import javax.faces.model.SelectItem;

import org.theospi.api.app.reports.*;


/**
 * This class allows the ReportResult to interact with the view
 *
 */
public class DecoratedReportParam {

	/** The link to the main tool */
	private ReportsTool	reportsTool = null;
	
	/** the report to decorate */
	private ReportParam reportParam;
	
	public DecoratedReportParam(ReportParam reportParam, ReportsTool reportsTool)
	{
		setReportParam(reportParam);
		this.reportsTool = reportsTool;
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

	public String getStaticValue()
	{
		return reportParam.getValue();
	}
	
	public String getTextValue()
	{
		return reportParam.getValue();
	}
	public void setTextValue(String value)
	{
		if(getIsFillIn())
			reportParam.setValue(value);
	}
	
	public String getMenuValue()
	{
		return reportParam.getValue();
	}
	public void setMenuValue(String value)
	{
		if(getIsSet() && !getIsMultiSelectable())
			reportParam.setValue(value);
	}
	
	public List getListValue()
	{
		
		return new ArrayList();
	}
	public void setListValue(List value)
	{
		if(getIsSet() && getIsMultiSelectable()) {
			System.out.println(value);
			reportParam.setValue(value.toString());
		}
	}
	
	/**
	 * gets the list of possible titles and values
	 * This will return a List of drop down SelectItem.
	 * The list is generated from any of the following:<br>
	 * 		[value1, value2, value3, ...]<br>
	 * 		[(value1), (value2), (value3), ...]<br>
	 * 		[(value1; item title1), (value2; item title2), (value3; item title3), ...]<br>
	 * These values would be in the value field of the report definition parameter.
	 * If the parameter is created from a sql query, then
	 * it is run:
	 * 		select value from table where ...<br>
	 * 		select value, itemTitle from table where ...
	 * 
	 * @return List of SelectItem
	 */
	public List getSelectableValues()
	{
		ArrayList array = new ArrayList();
		if(getIsSet()) {
			String strSet = null;
			if(getIsDynamic()) {
				//	run the sql in the report definition parameter value
				strSet = reportsTool.getReportsManager().generateSQLParameterValue(reportParam);
			} else {
				strSet = reportParam.getReportDefinitionParam().getValue();
			}

			strSet = strSet.substring(strSet.indexOf("[")+1, strSet.indexOf("]"));
			String[] set = strSet.split(",");
			
			for(int i = 0; i < set.length; i++) {
				String element = set[i].trim();
				
				//	replace any system values for display in the interface
				element = reportsTool.getReportsManager().replaceSystemValues(element);
				
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
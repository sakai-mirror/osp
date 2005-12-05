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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sf.hibernate.HibernateException;
import net.sf.hibernate.Session;

import org.theospi.api.app.reports.*;
import org.theospi.portfolio.shared.model.OspException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.orm.hibernate.HibernateCallback;
import org.springframework.orm.hibernate.support.HibernateDaoSupport;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.XMLOutputter;

/**
 * This class is a singleton that manages the reports on a general basis
 * 
 * 
 * @author andersjb
 *
 */
public class ReportsManagerImpl extends HibernateDaoSupport  implements ReportsManager
{
	protected final transient Log logger = LogFactory.getLog(getClass());
	   
	/** The global list of reports */
	private List reports;
	
	/** Tells us if the global database reports were loaded */
	private boolean isDBLoaded = false;

	private final static char returnChar = '\n';
	private final static char tabChar = '\t';

	/**
	 * This is the setter for the predefined reports, via the bean
	 * @param reports List of reports
	 */
	public void setReports(List reports)
	{
		this.reports = reports;
	}
	
	
	/**
	 * This is the getter for the total list of reports
	 * @return List
	 */
	public List getReports()
	{
		//load any reports in the database
		loadReportsFromDB();
		
		return reports;
	}
	
	
	/**
	 * Loads the global database reports if they haven't been loaded yet
	 *
	 */
	private void loadReportsFromDB()
	{
		if(isDBLoaded)
			return;
		
		isDBLoaded = true;
	}
	

//	*************************************************************************
	//	*************************************************************************
	//			The process functions (non-getter/setter)

	/**
	 * Creates parameters in the report linked to the parameters in the report definition
	 * 
	 * @param parameters a Collection of ReportParam
	 */
	public void createReportParameters(Report report)
	{
		List reportDefParams = report.getReportDefinition().getReportDefinitionParams();
		ArrayList reportParams = new ArrayList(reportDefParams.size());

		Iterator iter = reportDefParams.iterator();

		while (iter.hasNext()) {
			ReportDefinitionParam rdp = (ReportDefinitionParam) iter.next();

			ReportParam rp = new ReportParam();

			rp.setReportDefinitionParam(rdp);
			
			//	if the parameter is static then copy the value, otherwise it is filled by user
			if(rdp.getValueType().equals( ReportDefinitionParam.VALUE_TYPE_STATIC))
				rp.setValue(replaceSystemValues(rdp.getValue()));
			reportParams.add(rp);
		}
		report.setReportParams(reportParams);
	}

	/**
	 * Does a test to ensure that the parameters are valid
	 * One can get to the parameter definitions through the
	 * report parameter.
	 * @param parameters a Collection of ReportParam
	 */
	public boolean validateParameters(Collection parameters)
	{
		return true;
	}

	/**
	 * Creates a new blank Report based on a report definition
	 * 
	 * @param parameters a Collection of ReportParam
	 */
	public Report createReport(ReportDefinition reportDefinition)
	{
		Report report = new Report(reportDefinition);
		
		//Create the report parameters
		createReportParameters(report);
		
		return report;
	}
	
	
	public Connection getWarehouseConnection() throws HibernateException
	{
		//Get the data warehouse database connection
		//if fails, use the hibernate connection

		Session				session = getSession();
		
		return session.connection();
	}

	
	/**
	 * runs a report and creates a ReportResult.  The parameters were
	 * verified on the creation of this report object.
	 * TODO; Use JDC Binding
	 * @return ReportResult
	 */
	public ReportResult generateResults(Report report)
	{
		ReportResult		rr = new ReportResult();
		
		Connection			connection = null;
		PreparedStatement	stmt = null;

		try {
			ReportDefinition rd = report.getReportDefinition();
			
			connection = getWarehouseConnection();
			stmt = connection
					.prepareStatement(replaceSystemValues(rd.getQuery()));
				
			
			//	get the query from the Definition and replace the values
			//	no should be able to put in a system parameter into a report parameter and have it work
			//		so replace the system values before processing the report parameters
			
			//	replace the parameters with the values
			List reportParams = report.getReportParams();
			
			//If there are params, place them with values in the query
			if(reportParams != null) {
				Iterator	iter = reportParams.iterator();
				int			paramIndex = 0;
				
				//	loop through all the parameters and find in query for replacement
				while(iter.hasNext()) {
					
					//	get the paremeter and associated parameter definition
					ReportParam rp = (ReportParam)iter.next();
					ReportDefinitionParam rdp = rp.getReportDefinitionParam();
					
					if(rp.getValue() == null)
						throw new OspException("The Report Parameter Value was blank.  Offending parameter: " + rdp.getParamName());
	
					//TODO: what to do?
					//	if a parameter is not valid, fail gracefully
					if(!rp.valid()) {
						return null;
					}

					stmt.setString(paramIndex+1, rp.getValue());
					/*
					int i = query.indexOf(rdp.getParamName());
					
					//	Loop until no instances exist
					while(i != -1) {
						
						//	replace the parameter with the value
						query.replace(i, i + rdp.getParamName().length(), rp.getValue());
						
						//	look for a second instance
						i = query.indexOf(rdp.getParamName());
					}*/
					
					paramIndex++;
				}
			}
			
			rr.setCreationDate(new Date());
			
			// run the query
			ResultSet			rs = null;
			int					resultSetIndex = 0;
		
		
			rs = stmt.executeQuery();
			
			int columns = rs.getMetaData().getColumnCount();
			
			String []columnNames = new String[columns];
			
			for(int i = 0; i < columns; i++) {
				columnNames[i] = rs.getMetaData().getColumnName(i+1);
			}
			
			  
			Element reportElement = new Element("reportResult");
			
			Document document = new Document(reportElement);

			reportElement.setAttribute("title", report.getTitle());
			reportElement.setAttribute("description", report.getDescription());
			reportElement.setAttribute("keywords", report.getKeywords());
			reportElement.setAttribute("runDate", rr.getCreationDate().toString());
			
			Element paramsNode = new Element("parameters");

			if(reportParams != null) {
				Iterator iter = report.getReportParams().iterator();
				
				//	loop through all the parameters
				while(iter.hasNext()) {
					
					//	get the paremeter and associated parameter definition
					ReportParam rp = (ReportParam)iter.next();
					ReportDefinitionParam rdp = rp.getReportDefinitionParam();

					Element paramNode = new Element("parameter");

					paramNode.setAttribute("name", rdp.getParamName());
					paramNode.setAttribute("type", rdp.getType());
					paramNode.setAttribute("value", rp.getValue());
					
					paramsNode.addContent(paramNode);
				}
			}
			
			reportElement.addContent(paramsNode);
				
			while(rs.next()) {
				
				Element dataRow = new Element("datarow");;
				
				dataRow.setAttribute("index", "" + resultSetIndex++);
				reportElement.addContent(dataRow);
					
				for(int i = 0; i < columns; i++) {
					
					String data = rs.getString(i+1);
					
					Element columnNode = new Element("element");
					
					dataRow.addContent(columnNode);

					columnNode.setAttribute("colIndex", "" + i);
					columnNode.setAttribute("colName", columnNames[i]);
					
					if(data == null) {
						columnNode.setAttribute("isNull", "true");
						data = "";
					}
					columnNode.setText(data);
				}
				
			}

			rr.setReport(report);
			rr.setTitle("Report Title");
			rr.setKeywords("keywords, blah");
			rr.setDescription("This is the sample description of the report result");
			rr.setXml(document.toString());
			System.out.println((new XMLOutputter()).outputString(document));
			
		} catch (SQLException e) {
			logger.error("", e);
			throw new OspException(e);
		} catch (HibernateException e) {
			logger.error("", e);
			throw new OspException(e);
		} finally {
			try {
				stmt.close();
			} catch (Exception e) {
				logger.error("", e);
			}
		}
		
		// create an xml string with the data
		// any xml file links are pulled and entered into the xml in turn
		return rr;
	}
	
	public String replaceSystemValues(String inString)
	{
		Map map = new HashMap();

		map.put("{userid}", "admin");
		map.put("{username}", "The Administrator");
		map.put("{worksiteid}", "209348029348203984");
		map.put("{toolid}", "8765897589648");
		

		Iterator		iter = map.keySet().iterator();
		StringBuffer	str = new StringBuffer(inString);
		
		//	loop through all the parameters and find in query for replacement
		while(iter.hasNext()) {
			
			//	get the paremeter and associated parameter definition
			String key = (String)iter.next();
			
			int i = str.indexOf(key);
			
			//	Loop until no instances exist
			while(i != -1) {
				
				//	replace the parameter with the value
				str.replace(i, i + key.length(), (String)map.get(key));
				
				//	look for a second instance
				i = str.indexOf(key);
			}
		}
		
		return str.toString();
	}
}


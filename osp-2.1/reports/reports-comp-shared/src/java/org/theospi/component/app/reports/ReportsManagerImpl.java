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
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Date;

import net.sf.hibernate.HibernateException;
import net.sf.hibernate.Session;

import org.theospi.api.app.reports.*;
import org.theospi.portfolio.shared.model.OspException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.orm.hibernate.HibernateCallback;
import org.springframework.orm.hibernate.support.HibernateDaoSupport;

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
	 * @return
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
	 * Does a test to ensure that the parameters are valid
	 * 
	 * @param parameters a Collection of ReportParam
	 */
	public void createReportParameters(Report report) {
		List reportDefParams = report.getReportDefinition().getReportDefinitionParams();
		ArrayList reportParams = new ArrayList(reportDefParams.size());

		Iterator iter = reportDefParams.iterator();

		while (iter.hasNext()) {
			ReportDefinitionParam rdp = (ReportDefinitionParam) iter.next();

			ReportParam rp = new ReportParamImpl();

			rp.setReportDefinitionParam(rdp);
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
		Report report = new ReportImpl(reportDefinition);
		
		//Create the report parameters
		createReportParameters(report);
		
		return report;
	}

	
	/**
	 * runs a report and creates a ReportResult.  The parameters were
	 * verified on the creation of this report object.
	 * @return ReportResult
	 */
	public ReportResult generateResults(Report report)
	{
		ReportDefinition rd = report.getReportDefinition();
		
		//	get the query from the Definition
		StringBuffer query = new StringBuffer(rd.getQuery());
		
		//	replace the parameters with the values
		List reportParams = report.getReportParams();
		
		//If there are params, place them with values in the query
		if(reportParams != null) {
			Iterator iter = report.getReportParams().iterator();
			
			//	loop through all the parameters
			while(iter.hasNext()) {
				
				//	get the paremeter and associated parameter definition
				ReportParam rp = (ReportParam)iter.next();
				ReportDefinitionParam rdp = rp.getReportDefinitionParam();
				
				int i = query.indexOf(rdp.getParamName());
				
				while(i != -1) {
					if(rp.getValue() == null)
						throw new OspException("The Report Parameter Value was blank.  Offending parameter: " + rdp.getParamName());
					
					//	if a parameter is not valid, fail gracefully
					if(!rp.valid()) {
						return null;
					}
					
					//	replace the parameter with the value
					query.replace(i, i + rdp.getParamName().length(), rp.getValue());
					
					//	look for a second instance
					i = query.indexOf(rdp.getParamName());
					
					//	(rinse, lather,) repeat the process until there is no more 
					//		(shampoo) instances of the parameter string
				}
			}
		}
		// By here we have the query with the filled in parameters
		String queryString = query.toString();
		
		//	TODO: fill in site values
		// <worksite-id>, <worksite-name>, <tool-id>, <current-user>, etc.
		
		System.out.println(queryString);
		
		ReportResult		rr = new ReportResultImpl();
		
		// run the query
		Connection			connection = null;
		PreparedStatement	stmt = null;
		Session				session = getSession();
		ResultSet			rs = null;
		int					resultSetIndex = 0;
		
		
		try {
			connection = session.connection();
			stmt = connection
					.prepareStatement(queryString);
			//stmt.setString(1, itemDefId.getValue());
			rs = stmt.executeQuery();
			
			int columns = rs.getMetaData().getColumnCount();
			
			String []columnNames = new String[columns];
			
			for(int i = 0; i < columns; i++) {
				columnNames[i] = rs.getMetaData().getColumnName(i+1);
			}
			
			StringBuffer xml = new StringBuffer();

			xml.append("<report title=\"");
			xml.append(report.getTitle());
			xml.append("\" description=\"");
			xml.append(report.getDescription());
			xml.append("\" keywords=\"");
			xml.append(report.getKeywords());
			xml.append("\">");
				xml.append(returnChar);

			xml.append(tabChar);
			xml.append("<parameters>");
				xml.append(returnChar);
			if(reportParams != null) {
				Iterator iter = report.getReportParams().iterator();
				
				//	loop through all the parameters
				while(iter.hasNext()) {
					
					//	get the paremeter and associated parameter definition
					ReportParam rp = (ReportParam)iter.next();
					ReportDefinitionParam rdp = rp.getReportDefinitionParam();

					xml.append(tabChar);
					xml.append(tabChar);

					xml.append("<parameter name=\"");
					xml.append(rdp.getParamName());
					xml.append("\" value=\"");
					xml.append(rp.getValue());
					xml.append("\" type=\"");
					xml.append(rdp.getType());
					xml.append("\" />");
						xml.append(returnChar);
				}
			}
			xml.append(tabChar);
			xml.append("</parameters>");
				xml.append(returnChar);
				
			while(rs.next()) {
				xml.append(tabChar);
				xml.append("<datarow index=\"");
				xml.append(resultSetIndex++);
				xml.append("\">");
				xml.append(returnChar);
					
				for(int i = 0; i < columns; i++) {
					String data = rs.getString(i+1);
					xml.append(tabChar);
					xml.append(tabChar);
					xml.append("<element colIndex=\"");
					xml.append(i);
					xml.append("\" colName=\"");
					xml.append(columnNames[i]);
					if(data == null) {
						xml.append("\" isNull=\"");
						xml.append("true");
						data = "";
					}
					xml.append("\">");
					xml.append(data);
					xml.append("</element>");
					xml.append(returnChar);
				}
				
				xml.append(tabChar);
				xml.append("</datarow>");
				xml.append(returnChar);
			}
				
			xml.append("</report>");

			rr.setReport(report);
			rr.setTitle("Report Title");
			rr.setKeywords("keywords, blah");
			rr.setDescription("This is the sample description of the report result");
			rr.setCreationDate(new Date());
			rr.setXml(xml.toString());
			System.out.println(xml.toString());
			
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
	
	private String replaceSiteValues(String inString)
	{
		return inString;
	}
}


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

package org.theospi.portfolio.reports.model.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;

import javax.sql.DataSource;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringReader;
import java.io.ByteArrayOutputStream;
import java.text.ParseException;

import javax.servlet.http.HttpServletResponse;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamSource;

import org.sakaiproject.api.kernel.session.Session;
import org.sakaiproject.api.kernel.session.ToolSession;
import org.sakaiproject.api.kernel.session.cover.SessionManager;

import org.sakaiproject.metaobj.shared.mgt.IdManager;
import org.sakaiproject.metaobj.security.AuthorizationFailedException;
import org.sakaiproject.metaobj.security.AuthorizationFacade;
import org.sakaiproject.metaobj.security.model.AuthZMap;
import org.sakaiproject.metaobj.worksite.mgt.WorksiteManager;

import net.sf.hibernate.Hibernate;
import net.sf.hibernate.HibernateException;

import org.theospi.portfolio.shared.model.OspException;
import org.theospi.portfolio.security.impl.AllowAllSecurityAdvisor;
import org.theospi.portfolio.reports.model.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.orm.hibernate.support.HibernateDaoSupport;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.XMLOutputter;
import org.jdom.input.SAXBuilder;
import org.jdom.transform.JDOMResult;
import org.jdom.transform.JDOMSource;

import javax.xml.transform.stream.StreamResult;
import javax.faces.context.FacesContext;

import org.sakaiproject.api.kernel.component.ComponentManager;

import org.sakaiproject.service.framework.portal.cover.PortalService;
import org.sakaiproject.service.legacy.user.UserDirectoryService;
import org.sakaiproject.service.legacy.user.User;
import org.sakaiproject.service.legacy.security.SecurityService;

import org.sakaiproject.service.legacy.site.cover.SiteService;
import org.sakaiproject.service.legacy.site.Site;
import org.sakaiproject.exception.IdUnusedException;

import org.sakaiproject.api.kernel.function.cover.FunctionManager;

/**
 * This class is a singleton that manages the reports on a general basis
 * 
 * 
 * 
 * @author andersjb
 *
 */
public class ReportsManagerImpl extends HibernateDaoSupport  implements ReportsManager
{
	/** Enebles logging */
	protected final transient Log logger = LogFactory.getLog(getClass());
	
	/** The global list of reports */
	private List reportDefinitions;
	
	/** Class for converting a Id string to an Id class */
	private IdManager		idManager = null;
	
	/** the sakai class that manages permissions */
   private AuthorizationFacade authzManager;

   /** The class that generates the database connection.  it is pulled from the datawarehouse */
	private DataSource	dataSource = null;
	
	/** an internal variable for whether or not the database connection should be closed after its use */
	private boolean		canCloseConnection = true;
   
	/** used to hash a reference so the hash isn't straight from the reference */
   private String secretKey = "ospReports";

   /** used to allow artifacts to be downloaded (through adding an advisor)  */
   private SecurityService securityService;

   /** convert between the user formatted date and the database formatted date */
	private static SimpleDateFormat userDateFormat = new SimpleDateFormat("MM/dd/yyyy");
	private static SimpleDateFormat dbDateFormat = new SimpleDateFormat("yyyy-MM-dd");

	/** Tells us if the global database reportDefinitions was loaded */
	private boolean isDBLoaded = false;
	
	/** the name of key in the session into which the result is saved into */
   private static final String CURRENT_RESULTS_TAG = "org.theospi.portfolio.reports.model.ReportsManager.currentResults";

   	/**
   	 * Called on after the startup of the singleton.  This sets the global
   	 * list of functions which will will have permission managed by sakai
   	 * @throws Exception
   	 */
    protected void init() throws Exception
    {
       FunctionManager.registerFunction(ReportFunctions.REPORT_FUNCTION_CREATE);
       FunctionManager.registerFunction(ReportFunctions.REPORT_FUNCTION_RUN);
       FunctionManager.registerFunction(ReportFunctions.REPORT_FUNCTION_VIEW);
    }
   
   
   
   /**
	 * Sets the list of ReportDefinitions.  It also iterates through the list
	 * and tells the report definition to complete it's loading.
	 * @param reportDefinitions List of reportDefinitions
	 */
	public void setReportDefinitions(List reportDefinitions)
	{
		this.reportDefinitions = reportDefinitions;
		
		Iterator iter = reportDefinitions.iterator();
		while(iter.hasNext()) {
			ReportDefinition rd = (ReportDefinition)iter.next();
			
			rd.finishLoading();
		}
	}
	
	
	/**
	 * Returns the ReportDefinitions.  The list returned is filtered
	 * for the worksite type against the report types
	 * @return List
	 */
	public List getReportDefinitions()
	{
		String currentWorksiteType = getCurrentSiteType();
		
		//load any reportDefinitions in the database
		loadReportsFromDB();
		
		List reportsDefs = new ArrayList();
		
		Iterator iter = reportDefinitions.iterator();
		while(iter.hasNext()) {
			ReportDefinition rd = (ReportDefinition)iter.next();
			
			String typesStr = rd.getType();
			if(typesStr != null) {
				String []types = typesStr.split(",");
				for(int i = 0; i < types.length; i++) {
					String type = types[i];
					if(type.trim().equals(currentWorksiteType))
						reportsDefs.add(rd);
				}
			}
		}
		
		return reportsDefs;
	}
	
	
	/**
	 * This is the setter for the idManager
	 */
	public void setIdManager(IdManager idManager)
	{
		this.idManager = idManager;
	}
	
	
	/**
	 * This is the getter for the idManager
	 * @return IdManager
	 */
	public IdManager getIdManager()
	{
		return idManager;
	}
	
	
	/**
	 * This is the setter for the idManager
	 */
	public void setDataSource(DataSource dataSource)
	{
		this.dataSource = dataSource;
	}
	
	
	/**
	 * This is the getter for the idManager
	 * @return IdManager
	 */
	public DataSource getDataSource()
	{
		return dataSource;
	}
	
	
	/**
	 * this gets the list of report results that a user can view.
	 * If the user has permissions to run or view reports, 
	 *   then this grabs the ReportResults
	 * If the user has permissions to run reports,
	 *   then this grabs the Live Reports
	 * 
	 * @return List of ReportResult objects
	 */
	public List getCurrentUserResults()
	{
		Session s = SessionManager.getCurrentSession();

		boolean runReports = can(ReportFunctions.REPORT_FUNCTION_RUN);
		boolean viewReports = can(ReportFunctions.REPORT_FUNCTION_VIEW);

		List returned = new ArrayList();

		if (viewReports | runReports) {
			List results = getHibernateTemplate().find("from ReportResult r WHERE r.userId=?", s.getUserId(), Hibernate.STRING);

			Iterator iter = results.iterator();
			while(iter.hasNext()) {
				ReportResult r = (ReportResult)iter.next();

				r.setIsSaved(true);
			}
			returned.addAll(results);
		}

		if (runReports) {
			List liveReports = getHibernateTemplate().find("from Report r WHERE r.userId=? AND r.isLive=1", s.getUserId(), Hibernate.STRING);

			Iterator iter = liveReports.iterator();
			while(iter.hasNext()) {
				Report r = (Report)iter.next();

				r.getReportParams().size();

				r.connectToDefinition(reportDefinitions);
				r.setIsSaved(true);
			}

			returned.addAll(liveReports);
		}

		return returned;
	}
	
	
	/**
	 * Loads the global database reportDefinitions if they haven't been loaded yet
	 * This is a stub.
	 */
	private void loadReportsFromDB()
	{
		if(isDBLoaded)
			return;
		
		isDBLoaded = true;
	}
	
	/**
	 * Reloads a ReportResult.  During the loading process it loads the
	 * report from which the ReportResult is derived, It links the report to
	 * the report definition, and sets the report in the report result.
	 * @param ReportResult result
	 * @return ReportResult
	 */
    public ReportResult loadResult(ReportResult result)
    {
    	ReportResult reportResult =
    			(ReportResult)getHibernateTemplate().get(
    					ReportResult.class, 
    					result.getResultId()
    			);

      //load the report too
      Report report = reportResult.getReport();

      String function = report.getIsLive()?
            ReportFunctions.REPORT_FUNCTION_RUN:ReportFunctions.REPORT_FUNCTION_VIEW;

      getAuthzManager().checkPermission(function,
        getIdManager().getId(PortalService.getCurrentToolId()));

    	//set the report and report result to that of already been saved
    	reportResult.setIsSaved(true);
    	report.setIsSaved(true);
    	
    	//link the report deinition
    	report.connectToDefinition(reportDefinitions);
    	
    	
    	reportResult.setReport(report);
    	
    	//give back the result
    	return reportResult;
    }

    /**
     * Generates a unique id for a reference
     * @param result
     * @param ref
     * @return
     */
   public String getReportResultKey(ReportResult result, String ref) {
      String hashCode = DigestUtils.md5Hex(ref + getSecretKey());

      return hashCode;
   }

   /**
    * checks the id against the generated unique id of the reference.
    * It throws an AuthorizationFailedException if the ids don't match.
    * Otherwise it adds the AllowAllSecurityAdvisor to the securityService
    * @param id
    * @param ref
    */
   public void checkReportAccess(String id, String ref) {
      String hashCode = DigestUtils.md5Hex(ref + getSecretKey());

      if (!hashCode.equals(id)) {
         throw new AuthorizationFailedException();
      }

      getSecurityService().pushAdvisor(new AllowAllSecurityAdvisor());
   }

   /**
    * Puts the ReportResult into the session
    * @param result
    */
   public void setCurrentResult(ReportResult result) {
      ToolSession session = SessionManager.getCurrentToolSession();
      session.setAttribute(CURRENT_RESULTS_TAG, result);
   }

   /**
    * Pulls the ReportResults out of the session
    * @return ReportResult
    */
   public ReportResult getCurrentResult() {
      ToolSession session = SessionManager.getCurrentToolSession();
      return (ReportResult) session.getAttribute(CURRENT_RESULTS_TAG);
   }

   /**
    * Given an id, this method finds and returns the ReportDefinition
    * @param Id
    * @return ReportDefinition
    */
   public ReportDefinition findReportDefinition(String Id)
    {
    	Iterator iter = reportDefinitions.iterator();
    	
    	while(iter.hasNext()) {
    		ReportDefinition rd = (ReportDefinition)iter.next();
    		if(rd.getIdString().equals(Id))
    			return rd;
    	}
    	return null;
    }

//	*************************************************************************
	//	*************************************************************************
	//			The process functions (non-getter/setter)

	/**
	 * Creates parameters in the report linked to the parameters in the report definition
	 * 
	 * @param report a Collection of ReportParam
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
			rp.setReport(report);
			
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
	 * @param reportDefinition a Collection of ReportParam
	 */
	public Report createReport(ReportDefinition reportDefinition)
	{
		getAuthzManager().checkPermission(ReportFunctions.REPORT_FUNCTION_CREATE,
        getIdManager().getId(PortalService.getCurrentToolId()));

      Report report = new Report(reportDefinition);

		//Create the report parameters
		createReportParameters(report);

		Session s = SessionManager.getCurrentSession();
		report.setUserId(s.getUserId());
		report.setCreationDate(new Date());

		return report;
	}
	
	/**
	 * This function generates the data warehouse sql connection.
	 * If the datawarehouse connection fails then we want to fail over to
	 * the hibernate session connection.
	 * @return Connection
	 * @throws HibernateException
	 * @throws SQLException
	 */
	public Connection getWarehouseConnection() throws HibernateException, SQLException
	{
		Connection con = dataSource.getConnection();
		
		canCloseConnection = true;
		
		//fail over to the session connection
		if(con == null) {
			net.sf.hibernate.Session	session = getSession();
		
			con = session.connection();
			canCloseConnection = false;
		}
		
		return con;
	}
	
	/**
	 * This closes the database connection if it was pulled from the
	 * data warehouse.  (IOW, doesn't close if the connection came from
	 * the hibernate session)
	 * @param connection
	 * @throws SQLException
	 */
	public void closeWarehouseConnection(Connection connection) throws SQLException
	{
		if(canCloseConnection)
			connection.close();
	}

	
	/**
	 * gathers the data for dropdown/list box.
	 * @return String
	 */
	public String generateSQLParameterValue(ReportParam reportParam)
	{
		Connection			connection = null;
		PreparedStatement	stmt = null;
		ResultSet			rs = null;
		String				results = "[]";
		StringBuffer		strbuffer = new StringBuffer();
		try {
			connection = getWarehouseConnection();
			stmt = connection
					.prepareStatement(replaceSystemValues(reportParam.getReportDefinitionParam().getValue()));
			
			rs = stmt.executeQuery();
			strbuffer.append("[");
			int columns = rs.getMetaData().getColumnCount();
			while(rs.next()) {
				if(columns >= 2)
					strbuffer.append("(");
				if(columns >= 1)
					strbuffer.append(rs.getString(1));
				if(columns >= 2) {
					strbuffer.append(";");
					strbuffer.append(rs.getString(2));
					strbuffer.append(")");
				}
				strbuffer.append(",");
			}
			strbuffer.append("]");
			results = strbuffer.toString();
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
			try {
				closeWarehouseConnection(connection);
			} catch (Exception e) {
				logger.error("", e);
			}
		}
		return results;
	}

	/**
	 * gets the xsl tranform file, does the transform on the Results,
	 * then applies the post processor.
	 * 
	 * The result is a file being placed into the output Stream.
	 * @param params
	 * @param out
	 * @throws IOException
	 */
   public void packageForDownload(Map params, OutputStream out) throws IOException {
      ReportResult result = getCurrentResult();

      String exportResultsId = ((String[]) params.get(EXPORT_XSL_ID))[0];
      ReportXsl xslt = result.getReport().getReportDefinition().findReportXslByRuntimeId(exportResultsId);

      String fileData = transform(result, xslt);

      if (xslt.getResultsPostProcessor() != null) {
         out.write(xslt.getResultsPostProcessor().postProcess(fileData));
      }
      else {
         out.write(fileData.getBytes());
      }
   }

	/**
	 * runs a report and creates a ReportResult.  The parameters were
	 * verified on the creation of this report object.
	 * @return ReportResult
	 */
	public ReportResult generateResults(Report report) throws ReportExecutionException
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
						//return null;
					}
					
					String value = rp.getValue();
					
					//	Dates need to be formatted from user format to database format
					if( ReportDefinitionParam.TYPE_DATE.equals(rdp.getType())) {
						value = dbDateFormat.format(userDateFormat.parse( rp.getValue() ));
					}
					stmt.setString(paramIndex+1, value);
					
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

			Element docAttrNode = new Element("attributes");
			{
				Element attr = new Element("title");
				attr.setText(report.getTitle());
				reportElement.addContent(attr);
				
				attr = new Element("description");
				attr.setText(report.getDescription());
				reportElement.addContent(attr);
				
				attr = new Element("keywords");
				attr.setText(report.getKeywords());
				reportElement.addContent(attr);
				
				attr = new Element("runDate");
				attr.setText(rr.getCreationDate().toString());
				reportElement.addContent(attr);
			}
			reportElement.addContent(docAttrNode);
			
			Element paramsNode = new Element("parameters");

			if(reportParams != null) {
				Iterator iter = report.getReportParams().iterator();
				
				//	loop through all the parameters
				while(iter.hasNext()) {
					
					//	get the paremeter and associated parameter definition
					ReportParam rp = (ReportParam)iter.next();
					ReportDefinitionParam rdp = rp.getReportDefinitionParam();

					Element paramNode = new Element("parameter");

					paramNode.setAttribute("title", rdp.getTitle());
					paramNode.setAttribute("name", rdp.getParamName());
					paramNode.setAttribute("type", rdp.getType());

					paramNode.setText(rp.getValue());
					
					paramsNode.addContent(paramNode);
				}
			}
			reportElement.addContent(paramsNode);
			

			Element columnsNode = new Element("columns");
			for(int i = 0; i < columnNames.length; i++) {

				Element column = new Element("column");
				column.setAttribute("colIndex", "" + i);
				column.setAttribute("title", columnNames[i]);
				columnsNode.addContent(column);
			}
			reportElement.addContent(columnsNode);

			Element datarowsNode = new Element("data");
			while(rs.next()) {
				
				Element dataRow = new Element("datarow");
				
				dataRow.setAttribute("index", "" + resultSetIndex++);
				datarowsNode.addContent(dataRow);
					
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
			reportElement.addContent(datarowsNode);

			rr.setReport(report);
			rr.setTitle(report.getTitle());
			rr.setKeywords(report.getKeywords());
			rr.setDescription(report.getDescription());
			rr.setUserId(report.getUserId());
			rr.setXml((new XMLOutputter()).outputString(document));

         rr = postProcessResult(rd, rr);

	      } catch (SQLException e) {
				logger.error("", e);
				throw new OspException(e);
	      } catch (ParseException e) {
				logger.error("", e);
				throw new ReportExecutionException(e);
		} catch (HibernateException e) {
			logger.error("", e);
			throw new OspException(e);
		} finally {
			try {
				stmt.close();
			} catch (Exception e) {
				logger.error("", e);
			}
			try {
				closeWarehouseConnection(connection);
			} catch (Exception e) {
				logger.error("", e);
			}
		}
		
		// create an xml string with the data
		// any xml file links are pulled and entered into the xml in turn
		return rr;
	}

	/**
	 * applies all the post processing filters and returns the processed results
	 * @param rd
	 * @param rr
	 * @return
	 */
   protected ReportResult postProcessResult(ReportDefinition rd, ReportResult rr) {
      List resultProcessors = rd.getResultProcessors();
      if (resultProcessors != null) {
         for (Iterator i=resultProcessors.iterator();i.hasNext();) {
            ResultProcessor processor = (ResultProcessor) i.next();
            rr = processor.process(rr);
         }
      }
      return rr;
   }

   /**
    * Replaces the the system value proxy with the values.
    * The list of system value proxies(without quote characters):
    * "{userid}", "{userdisplayname}", "{useremail}", 
    * "{userfirstname}", "{userlastname}", "{worksiteid}", 
    * "{toolid}", 
    * @param inString
    * @return String with replaced values
    */
   public String replaceSystemValues(String inString)
	{
		UserDirectoryService dirServ = org.sakaiproject.service.legacy.user.cover.UserDirectoryService.getInstance();
		User u = dirServ.getCurrentUser();
		Session s = SessionManager.getCurrentSession();
		
		Map map = new HashMap();

		map.put("{userid}", s.getUserId());
		map.put("{userdisplayname}", u.getDisplayName());
		map.put("{useremail}", u.getEmail());
		map.put("{userfirstname}", u.getFirstName());
		map.put("{userlastname}", u.getLastName());
		map.put("{worksiteid}", PortalService.getCurrentSiteId());
		map.put("{toolid}", PortalService.getCurrentToolId());

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

	/**
	 * 
	 * @param reportResult ReportResult
	 * @param xslFile String to xsl resource
	 * @return
	 */
	public String transform(ReportResult reportResult, ReportXsl reportXsl)
	{
		try {

			JDOMResult result = new JDOMResult();
			SAXBuilder builder = new SAXBuilder();

			StreamSource xsltSource = new StreamSource(reportXsl.getResource().getInputStream());
			Transformer transformer = TransformerFactory.newInstance()
					.newTransformer(xsltSource);
			Document rootElement = builder.build(new StringReader(reportResult
					.getXml()));
			
			ByteArrayOutputStream sourceOut = new ByteArrayOutputStream();
			StreamResult resultstream = new StreamResult(sourceOut);

			transformer.transform(new JDOMSource(rootElement), resultstream);

			return sourceOut.toString();

		} catch (Exception e) {
         logger.error("", e);
         throw new OspException(e);
		}
	}
	
	
	private String getResourceFrom(String file)
	{
		String componentsRoot = System.getProperty(ComponentManager.SAKAI_COMPONENTS_ROOT_SYS_PROP);
		
		return componentsRoot + "osp-reports-components/WEB-INF/" + file;
	}
	
	private void writeFile(String fileString, String fileName, String contentType)
	{
		FacesContext faces = FacesContext.getCurrentInstance();
		HttpServletResponse response = (HttpServletResponse)faces.getExternalContext().getResponse();
		protectAgainstInstantDeletion(response);
		response.setContentType(contentType);
		response.setHeader("Content-disposition", "attachment; filename=" + fileName + ".csv");
		response.setContentLength(fileString.length());
		OutputStream out = null;
		try {
			out = response.getOutputStream();
			out.write(fileString.getBytes());
			out.flush();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (out != null) out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		faces.responseComplete();
	}

    /**
     * THIS IS TAKEN FROM GRADEBOOK: org.sakai.tool.gradebook.ui.ExportBean
     * 
     * Try to head off a problem with downloading files from a secure HTTPS
     * connection to Internet Explorer.
     *
     * When IE sees it's talking to a secure server, it decides to treat all hints
     * or instructions about caching as strictly as possible. Immediately upon
     * finishing the download, it throws the data away.
     *
     * Unfortunately, the way IE sends a downloaded file on to a helper
     * application is to use the cached copy. Having just deleted the file,
     * it naturally isn't able to find it in the cache. Whereupon it delivers
     * a very misleading error message like:
     * "Internet Explorer cannot download roster from sakai.yoursite.edu.
     * Internet Explorer was not able to open this Internet site. The requested
     * site is either unavailable or cannot be found. Please try again later."
     *
     * There are several ways to turn caching off, and so to be safe we use
     * several ways to turn it back on again.
     *
     * This current workaround should let IE users save the files to disk.
     * Unfortunately, errors may still occur if a user attempts to open the
     * file directly in a helper application from a secure web server.
     *
     * TODO Keep checking on the status of this.
     */
    private static void protectAgainstInstantDeletion(HttpServletResponse response) {
    	response.reset();	// Eliminate the added-on stuff
    	response.setHeader("Pragma", "public");	// Override old-style cache control
    	response.setHeader("Cache-Control", "public, must-revalidate, post-check=0, pre-check=0, max-age=0");	// New-style
    }
	
    
    
    public void saveReportResult(ReportResult result)
    {
        getHibernateTemplate().saveOrUpdate(result.getReport());
        getHibernateTemplate().saveOrUpdate(result);
        
        //	the user can't save results that have already been saved
        result.getReport().setIsSaved(true);
        result.setIsSaved(true);
    }
	
    
    
    public void saveReport(Report report)
    {
        getHibernateTemplate().saveOrUpdate(report);
        
        //	the user can't save reports that have already been saved
        report.setIsSaved(true);
    }

   public String getSecretKey() {
      return secretKey;
   }

   public void setSecretKey(String secretKey) {
      this.secretKey = secretKey;
   }

   public SecurityService getSecurityService() {
      return securityService;
   }

   public void setSecurityService(SecurityService securityService) {
      this.securityService = securityService;
   }
   
   /**
    * Returns the type of current worksite 
    * @return String
    */
   private String getCurrentSiteType()
   {
	   try {
		   Site site = SiteService.getSite(PortalService.getCurrentSiteId());
		   return site.getType();
	   } catch(IdUnusedException iue) {
		   //	just return null
	   }
	   return null;
   }

   public AuthorizationFacade getAuthzManager() {
      return authzManager;
   }

   public void setAuthzManager(AuthorizationFacade authzManager) {
      this.authzManager = authzManager;
   }

   protected void checkPermission(String function) {
      getAuthzManager().checkPermission(function, getIdManager().getId(PortalService.getCurrentToolId()));
   }

   public Map getAuthorizationsMap() {
      return new AuthZMap(getAuthzManager(), ReportFunctions.REPORT_FUNCTION_PREFIX,
            getIdManager().getId(PortalService.getCurrentToolId()));
   }

   protected boolean can(String function) {
      return new Boolean(getAuthzManager().isAuthorized(function,
            getIdManager().getId(PortalService.getCurrentToolId()))).booleanValue();
   }

   public boolean isMaintaner() {
      return new Boolean(getAuthzManager().isAuthorized(WorksiteManager.WORKSITE_MAINTAIN,
            getIdManager().getId(PortalService.getCurrentSiteId()))).booleanValue();
   }

}


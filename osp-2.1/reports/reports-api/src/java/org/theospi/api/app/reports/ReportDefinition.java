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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.sakaiproject.metaobj.shared.model.Id;

public class ReportDefinition
{
	private static final String DEFAULT_VIEW_LINK = "/reportxsls/default.xsl";
	
	/** the unique identifier for the report definition */
	private Id reportDefId = null;
	
	/** the unique identifier for the report definition */
	private String idString = null;

	/** the title of the report definition */
	private String title;

	/** the sql query for the report definition */
	private String query;

	/** the keyword for the report definition */
	private String keywords;

	/** the description for the report definition */
	private String description;

	/** the defaultXsl for the report definition */
	private ReportXsl defaultXsl;

	/** the exportXsl for the report definition */
	private String exportXsl;

	/** the link to the report parameters for the report definition */
	private List reportDefinitionParams;

	/** the defaultXsl for the report definition */
	//private List forms;

	/** the defaultXsl for the report definition */
	private List xsls;

   /** list of any special result processors this report needs.
    * These should be of type ResultProcessor
    * @see ResultProcessor
    */
	private List resultProcessors;
	
	/**
	 * when the report is finished loading the link in the report parameters
	 * needs to be set to the owning report definition
	 *
	 */
	public void finishLoading()
	{
		if(reportDefinitionParams == null)
			return;
		Iterator iter = reportDefinitionParams.iterator();
		
		while(iter.hasNext()) {
			ReportDefinitionParam rdp = (ReportDefinitionParam) iter.next();
			
			rdp.setReportDefinition(this);
		}
	}
	
	
	/**
	 * the getter for the reportDefId property
	 * @return String the unique identifier
	 */
	public Id getReportDefId()
	{
		return reportDefId;
	}
	
	
	/**
	 * the setter for the reportDefId property.  This is set by the bean 
	 * and by hibernate.
	 * @param reportDefId String
	 */
	public void setReportDefId(Id reportDefId)
	{
		this.reportDefId = reportDefId;
	}
	
	/**
	 * return the id as a string.  return the actual id if there is one then
	 * the configured definition id if not
	 * @return String
	 */
	public String getIdString()
	{
		if(reportDefId == null)
			return idString;
		return reportDefId.getValue();
	}
	public void setIdString(String idString)
	{
		this.idString = idString;
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
	 * @param reportDefId String
	 */
	public void setTitle(String title)
	{
		this.title = title;
	}
	
	
	/**
	 * the getter for the query property
	 * @return String the query
	 */
	public String getQuery()
	{
		return query;
	}
	
	
	/**
	 * the setter for the query property.  This is set by the bean 
	 * and by hibernate.
	 * @param query String
	 */
	public void setQuery(String query)
	{
		this.query = query;
	}
	
	
	/**
	 * the getter for the keywords property
	 * @return String the keywords
	 */
	public String getKeywords()
	{
		return keywords;
	}
	
	
	/**
	 * the setter for the keywords property.  This is set by the bean 
	 * and by hibernate.
	 * @param keywords String
	 */
	public void setKeywords(String keywords)
	{
		this.keywords = keywords;
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
	 * the getter for the defaultXsl property
	 * @return ReportXsl the defaultXsl
	 */
	public ReportXsl getDefaultXsl()
	{
		if(defaultXsl == null) {
			defaultXsl = new ReportXsl();
			defaultXsl.setIsExport(false);
			defaultXsl.setReportDefinition(this);
			defaultXsl.setXslLink(DEFAULT_VIEW_LINK);
		}
		return defaultXsl;
	}
	
	
	/**
	 * the setter for the defaultXsl property.  This is set by the bean 
	 * and by hibernate.
	 * @param defaultXsl ReportXsl
	 */
	public void setDefaultXsl(ReportXsl defaultXsl)
	{
		this.defaultXsl = defaultXsl;
	}
	
	
	/**
	 * the getter for the exportXsl property
	 * @return String the exportXsl
	 */
	public String getExportXsl()
	{
		return exportXsl;
	}
	
	
	/**
	 * the setter for the exportXsl property.  This is set by the bean 
	 * and by hibernate.
	 * @param exportXsl List
	 */
	public void setExportXsl(String exportXsl)
	{
		this.exportXsl = exportXsl;
	}
	
	
	/**
	 * the getter for the reportDefinitionParams property
	 * @return List of ReportDefinitionParam
	 */
	public List getReportDefinitionParams()
	{
		return reportDefinitionParams;
	}
	
	
	/**
	 * the setter for the reportDefinitionParams property.  This is set by the bean 
	 * and by hibernate.
	 * @param reportDefinitionParams List
	 */
	public void setReportDefinitionParams(List reportDefinitionParams)
	{
		this.reportDefinitionParams = reportDefinitionParams;
	}
	
	
	/**
	 * the getter for the xsls property
	 * @return List the xsls
	 */
	public List getXsls()
	{
		return xsls;
	}
	
	
	/**
	 * the setter for the xsl property.  This is set by the bean 
	 * and by hibernate.
	 * @param xsl List
	 */
	public void setXsls(List xsls)
	{
		this.xsls = xsls;
	}

   /** list of any special result processors this report needs.
    * These should be of type ResultProcessor
    * @see ResultProcessor
    */
   public List getResultProcessors() {
      return resultProcessors;
   }

   public void setResultProcessors(List resultProcessors) {
      this.resultProcessors = resultProcessors;
   }

   public ReportXsl findReportXsl(String link)
   {
      Iterator iter = xsls.iterator();

      while(iter.hasNext()) {
         ReportXsl xslInfo = (ReportXsl)iter.next();
         if(xslInfo.getXslLink() != null && xslInfo.getXslLink().equals(link))
            return xslInfo;
      }
      return null;
   }

   public ReportXsl findReportXslByRuntimeId(String runtimeId)
   {
      Iterator iter = xsls.iterator();

      while(iter.hasNext()) {
         ReportXsl xslInfo = (ReportXsl)iter.next();
         if(xslInfo.getXslLink() != null && xslInfo.getRuntimeId().equals(runtimeId))
            return xslInfo;
      }
      return null;
   }

}
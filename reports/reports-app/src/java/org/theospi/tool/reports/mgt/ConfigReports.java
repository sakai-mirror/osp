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

package org.theospi.tool.reports.mgt;

import java.util.List;
import org.theospi.api.app.reports.ReportsManager;
/**
 * This class takes the reportsfrom the given list and sets them in the ReportsManager
 * This class is needed because the reports annot be directly implanted into the 
 * ReportsManager because it is in the component and cannot access the web.xml beans.
 * 
 * This is the web.xml bean that can access both.
 * 
 * @author andersjb
 *
 */

public class ConfigReports
{
	/** the ReportManager from the components.xml */
	private ReportsManager	reportsManager;
	
	/** the reports from reports-definition.xml */
	private List			reports;

	
	/**
	 * The properties are set and then this is called (as the init function 
	 * defined by the bean).  This sets the reports in the manager.
	 *
	 */
	public void init()
	{
		reportsManager.setReportDefinitions(getReports());
	}
	
	
	/**
	 * the getter for the property
	 * @return ReportsManager
	 */
	public ReportsManager getReportsManager()
	{
		return reportsManager;
	}
	
	
	/**
	 * the setter for the property, set by the bean
	 * @param reportsManager ReportsManager
	 */
	public void setReportsManager(ReportsManager reportsManager)
	{
		this.reportsManager = reportsManager;
	}
	
	
	/**
	 * the getter for the property
	 * @return List
	 */
	public List getReports()
	{
		return reports;
	}
	
	
	/**
	 * the setter for the property, set by the bean
	 * @param reports List
	 */
	public void setReports(List reports)
	{
		this.reports = reports;
	}
	
}



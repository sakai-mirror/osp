/**********************************************************************************
* $URL$
* $Id$
***********************************************************************************
*
* Copyright (c) 2005, 2006 The Sakai Foundation.
*
* Licensed under the Educational Community License, Version 1.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*      http://www.opensource.org/licenses/ecl1.php
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*
**********************************************************************************/
package org.theospi.portfolio.reports.tool.mgt;

import java.util.List;
import org.theospi.portfolio.reports.model.ReportsManager;
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



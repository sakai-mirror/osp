package org.theospi.portfolio.reports.model;

import java.util.List;


public class AdditionalReportsConfig {

	
	/** the ReportManager from the components.xml */
	private ReportsManager	reportsManager;
	
	/** the reports from reports-definition.xml */
	private List			reports;
	
	public List getReports() {
		return reports;
	}

	public void setReports(List reports) {
		this.reports = reports;
	}

	public ReportsManager getReportsManager() {
		return reportsManager;
	}

	public void setReportsManager(ReportsManager reportsManager) {
		this.reportsManager = reportsManager;
	}

	/**
	 * The properties are set and then this is called (as the init function 
	 * defined by the bean).  This sets the reports in the manager.
	 *
	 */
	public void init()
	{
		reportsManager.addReportDefinitions(getReports());
	}
	
}

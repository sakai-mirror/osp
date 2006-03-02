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
package org.theospi.portfolio.reports.model;

import org.sakaiproject.metaobj.shared.DownloadableManager;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface ReportsManager extends DownloadableManager
{
   public static final String RESULTS_ID = "reportResultsId";
   public static final String EXPORT_XSL_ID = "reportExportId";

	public void setReportDefinitions(List reportdefs);
	public List getReportDefinitions();	

	public void createReportParameters(Report report);
	public Report createReport(ReportDefinition reportDefinition);
	public ReportResult generateResults(Report report);

	public String replaceSystemValues(String inString);
	public String generateSQLParameterValue(ReportParam reportParam);

	public String transform(ReportResult result, ReportXsl reportXsl);

    public void saveReportResult(ReportResult result);
    public void saveReport(Report result);
    
    public List getCurrentUserResults();
    public ReportResult loadResult(ReportResult result);

   public String getReportResultKey(ReportResult result, String ref);
   public void checkReportAccess(String id, String ref);

   public void setCurrentResult(ReportResult result);
   
   public void deleteReportResult(ReportResult result);
   public void deleteReport(Report report, boolean deactivate);

   public Map getAuthorizationsMap();

   public boolean isMaintaner();
}
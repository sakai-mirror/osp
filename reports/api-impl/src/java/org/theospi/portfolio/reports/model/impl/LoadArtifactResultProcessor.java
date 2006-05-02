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
package org.theospi.portfolio.reports.model.impl;

import org.theospi.portfolio.reports.model.ReportResult;
import org.theospi.portfolio.reports.model.ReportsManager;
import org.theospi.portfolio.security.impl.AllowAllSecurityAdvisor;
import org.theospi.portfolio.shared.intf.EntityContextFinder;
import org.theospi.portfolio.reports.model.impl.BaseResultProcessor;
import org.jdom.Document;
import org.jdom.Element;
import org.sakaiproject.metaobj.shared.model.Id;
import org.sakaiproject.metaobj.shared.model.OspException;
import org.sakaiproject.metaobj.shared.model.Artifact;
import org.sakaiproject.metaobj.shared.mgt.IdManager;
import org.sakaiproject.metaobj.shared.mgt.PresentableObjectHome;
import org.sakaiproject.metaobj.shared.ArtifactFinder;
import org.sakaiproject.metaobj.shared.ArtifactFinderManager;
import org.sakaiproject.authz.api.SecurityService;
import org.sakaiproject.content.cover.ContentHostingService;

import javax.sql.DataSource;
import java.util.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.ResultSet;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Dec 22, 2005
 * Time: 5:31:42 PM
 * To change this template use File | Settings | File Templates.
 */
public class LoadArtifactResultProcessor extends BaseResultProcessor {

   private IdManager idManager;
   private String columnNamePattern = ".*_artifact$";
   private DataSource dataSource;
   private ArtifactFinderManager artifactFinderManager;
   private SecurityService securityService;
   private ReportsManager reportsManager;

   /**
    * Post Processor method
    */
   public ReportResult process(ReportResult result) {
      Document rootDoc = getResults(result);
      Map artifactsToLoad = new Hashtable();

      List data = rootDoc.getRootElement().getChild("data").getChildren("datarow");

      for (Iterator i=data.iterator();i.hasNext();) {
         Element dataRow = (Element)i.next();
         processRow(dataRow, artifactsToLoad);
      }

      loadArtifactTypes(artifactsToLoad);

      getSecurityService().pushAdvisor(new AllowAllSecurityAdvisor());

      for (Iterator i=artifactsToLoad.values().iterator();i.hasNext();) {
         ArtifactHolder holder = (ArtifactHolder) i.next();
         loadArtifact(result, holder);
      }

      getSecurityService().popAdvisor();

      return setResult(result, rootDoc);
   }

   /**
    * find the artifact columns, stores them into a map (artifactsToLoad)
    * also puts the column into a list so as the artifact is loaded into it.
    * @param dataRow
    * @param artifactsToLoad
    */
   protected void processRow(Element dataRow, Map artifactsToLoad) {
      List columns = dataRow.getChildren("element");

      for (Iterator i=columns.iterator();i.hasNext();) {
         Element data = (Element) i.next();
         if (isArtifactColumn(data) && !isColumnNull(data)) {
            Id artifactId = getIdManager().getId(getColumnData(data));
            String type = getColumnType(data, dataRow);
            ArtifactHolder holder =
                  (ArtifactHolder) artifactsToLoad.get(artifactId.getValue());
            if (holder == null) {
               holder = new ArtifactHolder();
               holder.artifactId = artifactId;
               holder.artifactType = type;
               artifactsToLoad.put(artifactId.getValue(), holder);
            }
            holder.reportElements.add(data);
         }
      }
   }

   /**
    * This loads the artifact type into the Map of ArtifactHolders if the type
    * does not exist.  This method packs a bunch of artifact Ids together before
    * going out and getting their associated types.  Where there are 100 types to
    * be pulled, it loads the types.  It does this so as to not have the list of ids
    * be larger than what the database can handle.  Some limit the size of a IN (...)
    * to 1000 elements.  Others may have smaller or larger limits.
    * 
    * ERROR: the list of artifact Ids can only be 1000 elements in size, restriction of Oracle
    * @param artifactsToLoad
    */
   protected void loadArtifactTypes(Map artifactsToLoad) {
      String artifactIds = "";
      int numFound = 0;

      for (Iterator i=artifactsToLoad.values().iterator();i.hasNext();) {
         ArtifactHolder holder = (ArtifactHolder) i.next();
         if (holder.artifactType == null) {
            if (numFound != 0) {
               artifactIds += ",";
            }
            numFound++;
            artifactIds += "'" + holder.artifactId.getValue() + "'";
            
            if(numFound >= 100) {
               loadArtifactTypes(artifactIds, artifactsToLoad);
               numFound = 0;
               artifactIds = "";
            }
         }
      }

      if (numFound > 0) {
         loadArtifactTypes(artifactIds, artifactsToLoad);
      }
   }

   
   protected void loadArtifact(ReportResult results, ArtifactHolder holder) {
      ArtifactFinder finder = getArtifactFinderManager().getArtifactFinderByType(holder.artifactType);

      Artifact art;

      if (finder instanceof EntityContextFinder) {
         String uri = ContentHostingService.resolveUuid(holder.artifactId.getValue());
         String hash = getReportsManager().getReportResultKey(
               results, ContentHostingService.getReference(uri));
         art = ((EntityContextFinder)finder).loadInContext(holder.artifactId,
            ReportsEntityProducer.REPORTS_PRODUCER,
            holder.artifactId.getValue(), hash);
      }
      else {
         art = finder.load(holder.artifactId);
      }

      PresentableObjectHome home = (PresentableObjectHome)art.getHome();
      Element xml = home.getArtifactAsXml(art);

      for (Iterator i=holder.reportElements.iterator();i.hasNext();) {
         Element element = (Element) i.next();
         element.removeContent();
         element.addContent(xml);
         
         // each element can only have one parent
         xml = (Element)xml.clone();
      }
   }

   /**
    * The calling function has the responsibility of limiting the string of artifactIds to less than 1000
    * Oracle can only do 1000.  it should be limited to maybe 100 at a time!
    * @param artifactIds  String
    * @param artifactsToLoad Map
    */
   protected void loadArtifactTypes(String artifactIds, Map artifactsToLoad) {
      Connection conn = null;

      try {
         conn = getDataSource().getConnection();
         ResultSet rs = conn.createStatement().executeQuery(
               "select id, sub_type from dw_resource where id in (" + artifactIds + ")");
         while (rs.next()) {
            String id = rs.getString(1);
            String type = rs.getString(2);
            ArtifactHolder holder = (ArtifactHolder) artifactsToLoad.get(id);
            if (holder != null) {
               holder.artifactType = type;
            }
         }
      }
      catch (SQLException e) {
         logger.error("", e);
         throw new OspException(e);
      }
      finally {
         try {
            conn.close();
         }
         catch (SQLException e) {
            // con't do nothing here... let the last error go through
         }
      }
   }

   protected String getColumnType(Element data, Element dataRow) {
      return null; // null return will look up type
   }

   protected String getColumnData(Element data) {
      return data.getTextNormalize();
   }

   protected boolean isColumnNull(Element data) {
      return new Boolean(data.getAttributeValue("isNull", "false")).booleanValue();
   }

   protected boolean isArtifactColumn(Element data) {
      String columnName = data.getAttributeValue("colName");
      return columnName.matches(getColumnNamePattern());
   }

   public String getColumnNamePattern() {
      return columnNamePattern;
   }

   public void setColumnNamePattern(String columnNamePattern) {
      this.columnNamePattern = columnNamePattern;
   }

   public IdManager getIdManager() {
      return idManager;
   }

   public void setIdManager(IdManager idManager) {
      this.idManager = idManager;
   }

   public DataSource getDataSource() {
      return dataSource;
   }

   public void setDataSource(DataSource dataSource) {
      this.dataSource = dataSource;
   }

   public ArtifactFinderManager getArtifactFinderManager() {
      return artifactFinderManager;
   }

   public void setArtifactFinderManager(ArtifactFinderManager artifactFinderManager) {
      this.artifactFinderManager = artifactFinderManager;
   }

   public SecurityService getSecurityService() {
      return securityService;
   }

   public void setSecurityService(SecurityService securityService) {
      this.securityService = securityService;
   }

   public ReportsManager getReportsManager() {
      return reportsManager;
   }

   public void setReportsManager(ReportsManager reportsManager) {
      this.reportsManager = reportsManager;
   }

   protected class ArtifactHolder {
      public Id artifactId;
      public String artifactType;
      public List reportElements = new ArrayList();
   }

}

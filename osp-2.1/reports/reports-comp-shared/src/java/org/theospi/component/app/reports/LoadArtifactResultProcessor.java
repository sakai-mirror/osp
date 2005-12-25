/**********************************************************************************
* $URL$
* $Id$
***********************************************************************************
*
* Copyright (c) 2003, 2004 The Regents of the University of Michigan, Trustees of Indiana University,
*                  Board of Trustees of the Leland Stanford, Jr., University, and The MIT Corporation
*
* Licensed under the Educational Community License Version 1.0 (the "License");
* By obtaining, using and/or copying this Original Work, you agree that you have read,
* understand, and will comply with the terms and conditions of the Educational Community License.
* You may obtain a copy of the License at:
*
*      http://cvs.sakaiproject.org/licenses/license_1_0.html
*
* THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
* INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE
* AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
* DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
* FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*
**********************************************************************************/
package org.theospi.component.app.reports;

import org.theospi.api.app.reports.ReportResult;
import org.theospi.api.app.reports.ReportsManager;
import org.theospi.portfolio.security.impl.AllowAllSecurityAdvisor;
import org.theospi.portfolio.shared.intf.EntityContextFinder;
import org.theospi.portfolio.security.impl.AllowAllSecurityAdvisor;
import org.jdom.Document;
import org.jdom.Element;
import org.sakaiproject.metaobj.shared.model.Id;
import org.sakaiproject.metaobj.shared.model.OspException;
import org.sakaiproject.metaobj.shared.model.Artifact;
import org.sakaiproject.metaobj.shared.mgt.IdManager;
import org.sakaiproject.metaobj.shared.mgt.HomeFactory;
import org.sakaiproject.metaobj.shared.mgt.ReadableObjectHome;
import org.sakaiproject.metaobj.shared.mgt.PresentableObjectHome;
import org.sakaiproject.metaobj.shared.ArtifactFinder;
import org.sakaiproject.metaobj.shared.ArtifactFinderManager;
import org.sakaiproject.service.legacy.security.SecurityService;
import org.sakaiproject.service.legacy.content.cover.ContentHostingService;

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
   private String columnNamePattern = ".*_artifact";
   private DataSource dataSource;
   private ArtifactFinderManager artifactFinderManager;
   private SecurityService securityService;
   private ReportsManager reportsManager;

   public ReportResult process(ReportResult result) {
      Document rootDoc = getResults(result);
      Map artifactsToLoad = new Hashtable();

      List data = rootDoc.getRootElement().getChildren("datarow");

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
      }
   }

   protected void loadArtifactTypes(Map artifactsToLoad) {
      String artifactIds = "";
      boolean foundOne = false;

      for (Iterator i=artifactsToLoad.values().iterator();i.hasNext();) {
         ArtifactHolder holder = (ArtifactHolder) i.next();
         if (holder.artifactType == null) {
            if (foundOne) {
               artifactIds += ",";
            }
            foundOne = true;
            artifactIds += "'" + holder.artifactId.getValue() + "'";
         }
      }

      if (foundOne) {
         loadArtifactTypes(artifactIds, artifactsToLoad);
      }
   }

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

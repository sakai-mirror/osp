/**********************************************************************************
* $URL: https://source.sakaiproject.org/svn/trunk/osp/osp-2.1/reports/api-impl/src/java/org/theospi/portfolio/reports/model/impl/LoadArtifactResultProcessor.java $
* $Id: LoadArtifactResultProcessor.java 5557 2006-01-26 06:02:52Z john.ellis@rsmart.com $
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
import org.theospi.portfolio.security.impl.AllowAllSecurityAdvisor;
import org.theospi.portfolio.reports.model.ReportsManager;
import org.theospi.portfolio.reports.model.ReportResult;
import org.theospi.portfolio.reports.model.impl.BaseResultProcessor;
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
public class GroupingResultProcessor extends BaseResultProcessor {

   private DataSource dataSource;
   private ArtifactFinderManager artifactFinderManager;
   private SecurityService securityService;
   private ReportsManager reportsManager;
   private List grouping = null;

   public List getGrouping()
   {
      return grouping;
   }
   public void setGrouping(List grouping)
   {
      this.grouping = grouping;
   }
   
   public ReportResult process(ReportResult result) {
      Document rootDoc = getResults(result);


      Element groupings = new Element("groupings");
      for (Iterator i = grouping.iterator(); i.hasNext(); ) {
         String theGrouping = (String)i.next();
         String groups[] = theGrouping.split(",");
         List elements = processGroup(rootDoc, theGrouping);
         Element group = new Element("group");
         
         group.setAttribute("by", groups[0]);
         for(Iterator ii = elements.iterator(); ii.hasNext(); ) {
            Element element = (Element)ii.next();
            group.addContent((Element)element.clone());
         }
         groupings.addContent(group);
      }
      
      rootDoc.getRootElement().addContent(groupings);
      
      
      return setResult(result, rootDoc);
   }
   protected List processGroup(Document rootDoc, String inGrouping)
   {
      
      List data = rootDoc.getRootElement().getChild("data").getChildren("datarow");
      
     return groupElements(data, inGrouping);
   }
   
   protected List groupElements(List rows, String inGrouping)
   {
      String groups[] = inGrouping.split(",");
      String groupStr = groups[0].trim();
      Map  groupHash = new HashMap();
      
      // Loop through all the data rows
      for(Iterator i = rows.iterator(); i.hasNext(); ) {
         Element dataRow = (Element)i.next();
         List columns = dataRow.getChildren("element");
         
         // go through each column and find the grouping column
         for(Iterator ii = columns.iterator(); ii.hasNext(); ) {
            Element column = (Element)ii.next();
            
            if(column.getAttribute("colName").getValue().equals(groupStr)) {
               // add the grouping data
               List groupList = (List)groupHash.get(column.getTextTrim());
               if(groupList == null)
                  groupList = new ArrayList();
               groupList.add(dataRow);
               groupHash.put(column.getTextTrim(), groupList);
            }
         }
      }

      List matchingElements = new ArrayList();
      for(Iterator i = groupHash.keySet().iterator(); i.hasNext(); ) {
         String key = (String)i.next();
         
         // Get the first Element for each unique group value
         matchingElements.add(  ((List)(groupHash.get(key))).get(0)  );
      }
       
      return matchingElements;
   }

}

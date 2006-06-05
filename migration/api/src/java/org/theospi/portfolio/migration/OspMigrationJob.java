/**********************************************************************************
* $URL$
* $Id$
***********************************************************************************
*
* Copyright (c) 2006 The Sakai Foundation.
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

package org.theospi.portfolio.migration;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.sakaiproject.component.cover.ServerConfigurationService;
import org.sakaiproject.metaobj.shared.mgt.AgentManager;
import org.sakaiproject.metaobj.shared.mgt.IdManager;
import org.sakaiproject.metaobj.shared.model.Id;
import org.sakaiproject.metaobj.shared.mgt.StructuredArtifactDefinitionManager;
import org.sakaiproject.metaobj.shared.model.StructuredArtifactDefinitionBean;
import org.theospi.portfolio.help.model.Glossary;
import org.theospi.portfolio.help.model.GlossaryEntry;
import org.theospi.portfolio.security.AuthorizationFacade;
import org.theospi.portfolio.matrix.MatrixManager;
import org.theospi.portfolio.matrix.model.Scaffolding;
import org.theospi.portfolio.matrix.model.ScaffoldingCell;
import org.theospi.portfolio.matrix.model.Criterion;
import org.theospi.portfolio.matrix.model.Level;
import org.theospi.portfolio.presentation.PresentationManager;
import org.theospi.portfolio.style.model.Style;

/**
 * 
 *    TODO:  
 *       the authorization needs to convertion from tool to site for things that have switched
 *
 */
public class OspMigrationJob implements Job {

   protected final transient Log logger = LogFactory.getLog(getClass());
   
   private DataSource dataSource;
   private IdManager idManager;
   private AgentManager agentManager;
   private AuthorizationFacade authzManager;
   private Glossary glossaryManager;
   private StructuredArtifactDefinitionManager structuredArtifactDefinitionManager;
   private PresentationManager presentationManager;
   private MatrixManager matrixManager;
   private Statement stmt;
   private Map tableMap = new HashMap();
   
   public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
      logger.info("Quartz job started: "+this.getClass().getName());
      Connection connection = null;
      try {

         String developerFlag = ServerConfigurationService.getString("osp.migration.developer");
         
         boolean isDeveloper = developerFlag.equalsIgnoreCase("true");
         
         connection = getDataSource().getConnection();
         
         if(isDeveloper)
            developerClearTables(connection);
         
         runAuthzMigration(connection, isDeveloper);
         runGlossaryMigration(connection, isDeveloper);
         runFormMigration(connection, isDeveloper);
         runMatrixMigration(connection, isDeveloper);
         runPresentationMigration(connection, isDeveloper);
      } catch (SQLException e) {
         throw new JobExecutionException(e);
      } finally {
         if (connection != null) {
            try {
               connection.close();
            }
            catch (Exception e) {
               // can't do anything with this.
            }
         }
      }
      
      logger.info("Quartz job fininshed: "+this.getClass().getName());
   }
   
   private void developerClearTables(Connection con) throws JobExecutionException
   {
      String sql = "";
      try {
         stmt = con.createStatement();
         
         sql = "TRUNCATE osp_help_glossary";
         stmt.executeUpdate(sql);
         sql = "TRUNCATE osp_help_glossary_desc";
         stmt.executeUpdate(sql);
         sql = "TRUNCATE osp_scaffolding_cell_form_defs";
         stmt.executeUpdate(sql);
         sql = "TRUNCATE osp_scaffolding_cell";
         stmt.executeUpdate(sql);
         sql = "TRUNCATE osp_scaffolding_levels";
         stmt.executeUpdate(sql);
         sql = "TRUNCATE osp_scaffolding_criteria";
         stmt.executeUpdate(sql);
         sql = "TRUNCATE osp_matrix_label";
         stmt.executeUpdate(sql);
         sql = "TRUNCATE osp_scaffolding";
         stmt.executeUpdate(sql);
         
      } catch (Exception e) {
         logger.error("error selecting data with this sql: " + sql);
         logger.error("", e);
         throw new JobExecutionException(e);
     } finally {
         try {
             stmt.close();
         } catch (Exception e) {
         }
     }
   }
   
   protected void runAuthzMigration(Connection con, boolean isDeveloper) throws JobExecutionException {
      logger.info("Quartz task started: runAuthzMigration()");
      String tableName = getOldTableName("osp_authz_simple");
      String sql = "select * from " + tableName;
      
      try {
            stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            try {
               while (rs.next()) {
                  //String id = rs.getString("id");
                  String qual = rs.getString("qualifier_id");
                  String agent = rs.getString("agent_id");
                  String func = rs.getString("function_name");
                  //TODOCWM: Do transformations on the authz stuff that needs to 
                  // change from a tool_id to a site_id
                  try {
                     authzManager.createAuthorization(agentManager.getAgent(agent), func, idManager.getId(qual));
                  } catch(Exception e) {
                     if(!isDeveloper)
                        throw e;
                  }
               }
           } finally {
               rs.close();
           }
        } catch (Exception e) {
            logger.error("error selecting data with this sql: " + sql);
            logger.error("", e);
            throw new JobExecutionException(e);
        } finally {
            try {
                stmt.close();
            } catch (Exception e) {
            }
        }
        logger.info("Quartz task fininshed: runAuthzMigration()");
   }
   
   protected void runGlossaryMigration(Connection con, boolean isDeveloper) throws JobExecutionException {
      logger.info("Quartz task started: runGlossaryMigration()");
      String tableName = getOldTableName("osp_help_glossary");
      String tableName2 = getOldTableName("osp_help_glossary_desc");
      String sql = "select g.id, g.term, g.description, g.worksite_id, " +
            "gd.long_description from " + tableName + " g, " + tableName2 +
            " gd where g.id = gd.entry_id";
      
      try {
            stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            try {
               while (rs.next()) {
                  String id = rs.getString("id");
                  String term = rs.getString("term");
                  String desc = rs.getString("description");
                  String longDesc = rs.getString("long_description");
                  String site_id = rs.getString("worksite_id");
                  
                  Id theId = idManager.getId(id);

                  GlossaryEntry entry = new GlossaryEntry(term, desc);
                  entry.setId(theId);
                  entry.setWorksiteId(site_id);
                  entry.getLongDescriptionObject().setEntryId(theId);
                  entry.getLongDescriptionObject().setLongDescription(longDesc);
                  glossaryManager.addEntry(entry);
                  
               }
           } finally {
               rs.close();
           }
        } catch (Exception e) {
            logger.error("error selecting data with this sql: " + sql);
            logger.error("", e);
            throw new JobExecutionException(e);
        } finally {
            try {
                stmt.close();
            } catch (Exception e) {
            }
        }
        logger.info("Quartz task fininshed: runGlossaryMigration()");
   }
   
   protected void runFormMigration(Connection con, boolean isDeveloper) throws JobExecutionException {
      logger.info("Quartz task started: runFormMigration()");
      String tableName = getOldTableName("osp_structured_artifact_def");
      String sql = "select * from " + tableName;
      
      try {
            stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            try {
               while (rs.next()) {
                  String id = rs.getString("id");
                  String desc = rs.getString("description");
                  String docRoot = rs.getString("documentRoot");
                  String owner = rs.getString("owner");
                  Date created = rs.getDate("created");
                  Date modified = rs.getDate("modified");
                  boolean systemOnly = rs.getBoolean("systemOnly");
                  String extType = rs.getString("externalType");
                  String siteId = rs.getString("siteId");
                  int siteState = rs.getInt("siteState");
                  int globalState = rs.getInt("globalState");
                  byte[] schemaData = rs.getBytes("schemaData");
                  String instr = rs.getString("instruction");
                  
                  StructuredArtifactDefinitionBean sad = new StructuredArtifactDefinitionBean();
                  sad.setId(idManager.getId(id));
                  sad.setDescription(desc);
                  sad.setDocumentRoot(docRoot);
                  sad.setOwner(agentManager.getAgent(owner));
                  sad.setCreated(created);
                  sad.setModified(modified);
                  sad.setSystemOnly(systemOnly);
                  sad.setExternalType(extType);
                  sad.setSiteId(siteId);
                  sad.setSiteState(siteState);
                  sad.setGlobalState(globalState);
                  String schema = new String(schemaData);
                  if(schema != null && schema.equals("Err"))
                     schemaData = null;
                  sad.setSchema(schemaData);
                  sad.setInstruction(instr);
                  
                  structuredArtifactDefinitionManager.save(sad, false);
                  
               }
           } finally {
               rs.close();
           }
        } catch (Exception e) {
            logger.error("error selecting data with this sql: " + sql);
            logger.error("", e);
            throw new JobExecutionException(e);
        } finally {
            try {
                stmt.close();
            } catch (Exception e) {
            }
        }
        logger.info("Quartz task fininshed: runFormMigration()");
   }

   protected void runMatrixMigration(Connection con, boolean isDeveloper) throws JobExecutionException {
      logger.info("Quartz task started: runMatrixMigration()");
      String tableName = getOldTableName("osp_scaffolding"), tableName2 = null;
      String sql = "select * from " + tableName;
      
      try {
         Statement innerStmt = con.createStatement();
            stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            try {
               while (rs.next()) {
                  String id = rs.getString("id");
                  String owner = rs.getString("ownerid");
                  String title = rs.getString("title");
                  String description = rs.getString("description");
                  String worksite = rs.getString("worksiteId");
                  boolean published = rs.getBoolean("published");
                  String publishedBy = rs.getString("publishedBy");
                  Date   publishedDate = rs.getDate("publishedDate");
                  String columnLabel = rs.getString("columnLabel");
                  String rowLabel = rs.getString("rowLabel");
                  String readyColor = rs.getString("readyColor");
                  String pendingColor = rs.getString("pendingColor");
                  String completedColor = rs.getString("completedColor");
                  String lockColor = rs.getString("lockColor");
                  int    workflowOption = rs.getInt("workflowOption");
                  String exposed_page_id = rs.getString("exposed_page_id");
                  String style_id = rs.getString("style_id");
                  
                  Scaffolding scaffolding = new Scaffolding();

                  scaffolding.setId(idManager.getId(id));
                  scaffolding.setOwner(agentManager.getAgent(owner));
                  scaffolding.setTitle(title);
                  scaffolding.setDescription(description);
                  scaffolding.setWorksiteId(idManager.getId(worksite));
                  scaffolding.setPublished(published);
                  scaffolding.setPublishedBy(agentManager.getAgent(publishedBy));
                  scaffolding.setColumnLabel(columnLabel);
                  scaffolding.setPublishedDate(publishedDate);
                  scaffolding.setRowLabel(rowLabel);
                  
                  scaffolding.setReadyColor(readyColor);
                  scaffolding.setPendingColor(pendingColor);
                  scaffolding.setCompletedColor(completedColor);
                  scaffolding.setLockedColor(lockColor);
                  scaffolding.setWorkflowOption(workflowOption);
                  scaffolding.setExposedPageId(exposed_page_id);
                  
                  Style style = new Style();
                  
                  style.setId(idManager.getId(style_id));
                  scaffolding.setStyle(style);

                  

                  tableName = getOldTableName("osp_scaffolding_criteria");
                  tableName2 = getOldTableName("osp_matrix_label");
                  sql = "select * from " + tableName + " join" + tableName2 + " on ELT=ID" +
                     " where scaffolding_id='" + id + "' order by seq_num";
                  
                  ResultSet rss = innerStmt.executeQuery(sql);

                  Map criteriaMap = new HashMap();
                  
                  while (rss.next()) {
                     int sequenceNumber = rss.getInt("seq_num");
                     Id lid = idManager.getId(rss.getString("elt"));
                     String color = rss.getString("color");
                     String textColor = null;
                     String ldescription = rss.getString("description");
                     
                     Criterion criterion = new Criterion();
                     
                     criterion.setId(lid);
                     criterion.setColor(color);
                     criterion.setTextColor(textColor);
                     criterion.setScaffolding(scaffolding);
                     criterion.setDescription(ldescription);
                     criterion.setSequenceNumber(sequenceNumber);

                     scaffolding.add(criterion);
                     
                     criteriaMap.put(lid.getValue(), criterion);
                  }
                  
                  
                  
                  tableName = getOldTableName("osp_scaffolding_levels");
                  tableName2 = getOldTableName("osp_matrix_label");
                  sql = "select * from " + tableName + " join" + tableName2 + " on ELT=ID" +
                     " where scaffolding_id='" + id + "' order by seq_num";
                  
                  rss = innerStmt.executeQuery(sql);

                  Map levelMap = new HashMap();

                  while (rss.next()) {
                     int sequenceNumber = rss.getInt("seq_num");
                     Id lid = idManager.getId(rss.getString("elt"));
                     String color = rss.getString("color");
                     String textColor = null;
                     String ldescription = rss.getString("description");
                     
                     Level level = new Level();

                     level.setId(lid);
                     level.setColor(color);
                     level.setTextColor(textColor);
                     level.setScaffolding(scaffolding);
                     level.setDescription(ldescription);
                     level.setSequenceNumber(sequenceNumber);
                     
                     scaffolding.add(level);
                     
                     levelMap.put(lid.getValue(), level);
                  }
                  
                  
                  
                  tableName = getOldTableName("osp_scaffolding_cell");
                  sql = "select * from " + tableName + " where scaffolding_id='" + id + "' ";
                  
                  rss = innerStmt.executeQuery(sql);

                  while (rss.next()) {
                     Id cid = idManager.getId(rss.getString("id"));
                     String criterionStr = rss.getString("rootcriterion_id");
                     String levelStr = rss.getString("level_id");
                     String expectationheader = rss.getString("expectationheader");
                     String initialStatus = rss.getString("initialstatus");
                     String gradablereflection = rss.getString("gradablereflection");
                     
                     ScaffoldingCell cell = new ScaffoldingCell();

                     cell.setId(cid);
                     cell.setInitialStatus(initialStatus);
                     cell.setLevel((Level)levelMap.get(cid.getValue()));
                     cell.setRootCriterion((Criterion)criteriaMap.get(cid.getValue()));
                     
                     //TODO: create the wizard page
                     
                     scaffolding.add(cell);
                  }
                  
                  scaffolding = matrixManager.storeScaffolding(scaffolding);
                  
               }
           } finally {
               rs.close();
           }
        } catch (Exception e) {
            logger.error("error selecting data with this sql: " + sql);
            logger.error("", e);
            throw new JobExecutionException(e);
        } finally {
            try {
                stmt.close();
            } catch (Exception e) {
            }
        }
        logger.info("Quartz task fininshed: runMatrixMigration()");
      
   }

   protected void runPresentationMigration(Connection con, boolean isDeveloper) throws JobExecutionException {
      logger.info("Quartz task started: runFormMigration()");
      String tableName = getOldTableName("osp_structured_artifact_def");
      String sql = "select * from " + tableName;
      
      try { /*
            stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            try {
               while (rs.next()) {
                  String id = rs.getString("id");
                  
                  StructuredArtifactDefinitionBean sad = new StructuredArtifactDefinitionBean();
                  sad.setId(idManager.getId(id));
                  
                  structuredArtifactDefinitionManager.save(sad, false);
                  
               }
           } finally {
               rs.close();
           } */
        } catch (Exception e) {
            logger.error("error selecting data with this sql: " + sql);
            logger.error("", e);
            throw new JobExecutionException(e);
        } finally {
            try {
                stmt.close();
            } catch (Exception e) {
            }
        }
        logger.info("Quartz task fininshed: runFormMigration()");
   }
   
   protected String getOldTableName(String tableName)
   {
      return (String)getTableMap().get(tableName);
   }

   public DataSource getDataSource() {
      return dataSource;
   }

   public void setDataSource(DataSource dataSource) {
      this.dataSource = dataSource;
   }

   public IdManager getIdManager() {
      return idManager;
   }

   public void setIdManager(IdManager idManager) {
      this.idManager = idManager;
   }

   public AgentManager getAgentManager() {
      return agentManager;
   }

   public void setAgentManager(AgentManager agentManager) {
      this.agentManager = agentManager;
   }

   public AuthorizationFacade getAuthzManager() {
      return authzManager;
   }

   public void setAuthzManager(AuthorizationFacade authzManager) {
      this.authzManager = authzManager;
   }

   public Map getTableMap() {
      return tableMap;
   }

   public void setTableMap(Map tableMap) {
      this.tableMap = tableMap;
   }

   public Glossary getGlossaryManager() {
      return glossaryManager;
   }

   public void setGlossaryManager(Glossary glossaryManager) {
      this.glossaryManager = glossaryManager;
   }

   public MatrixManager getMatrixManager() {
      return matrixManager;
   }

   public void setMatrixManager(
         MatrixManager matrixManager) {
      this.matrixManager = matrixManager;
   }

   public PresentationManager getPresentationManager() {
      return presentationManager;
   }

   public void setPresentationManager(
         PresentationManager presentationManager) {
      this.presentationManager = presentationManager;
   }

   public StructuredArtifactDefinitionManager getStructuredArtifactDefinitionManager() {
      return structuredArtifactDefinitionManager;
   }

   public void setStructuredArtifactDefinitionManager(
         StructuredArtifactDefinitionManager structuredArtifactDefinitionManager) {
      this.structuredArtifactDefinitionManager = structuredArtifactDefinitionManager;
   }
   
   

}

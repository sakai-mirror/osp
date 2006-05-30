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
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.sakaiproject.metaobj.shared.mgt.AgentManager;
import org.sakaiproject.metaobj.shared.mgt.IdManager;
import org.sakaiproject.metaobj.shared.mgt.StructuredArtifactDefinitionManager;
import org.sakaiproject.metaobj.shared.model.StructuredArtifactDefinitionBean;
import org.theospi.portfolio.help.model.Glossary;
import org.theospi.portfolio.help.model.GlossaryEntry;
import org.theospi.portfolio.security.AuthorizationFacade;

public class OspMigrationJob implements Job {

   protected final transient Log logger = LogFactory.getLog(getClass());
   
   private DataSource dataSource;
   private IdManager idManager;
   private AgentManager agentManager;
   private AuthorizationFacade authzManager;
   private Glossary glossaryManager;
   private StructuredArtifactDefinitionManager structuredArtifactDefinitionManager;
   private Statement stmt;
   private Map tableMap = new HashMap();
   
   public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
      logger.info("Quartz job started: "+this.getClass().getName());
      Connection connection = null;
      try {
         connection = getDataSource().getConnection();
         runAuthzMigration(connection);
         runGlossaryMigration(connection);
         runFormMigration(connection);
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
   
   protected void runAuthzMigration(Connection con) throws JobExecutionException {
      logger.info("Quartz task started: runAuthzMigration()");
      String tableName = (String)getTableMap().get("osp_authz_simple");
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
                  
                  authzManager.createAuthorization(agentManager.getAgent(agent), func, idManager.getId(qual));
                  
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
   
   protected void runGlossaryMigration(Connection con) throws JobExecutionException {
      logger.info("Quartz task started: runGlossaryMigration()");
      String tableName = (String)getTableMap().get("osp_help_glossary");
      String tableName2 = (String)getTableMap().get("osp_help_glossary_desc");
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

                  GlossaryEntry entry = new GlossaryEntry(term, desc);
                  entry.setWorksiteId(site_id);
                  entry.getLongDescriptionObject().setEntryId(idManager.getId(id));
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
   
   protected void runFormMigration(Connection con) throws JobExecutionException {
      logger.info("Quartz task started: runFormMigration()");
      String tableName = (String)getTableMap().get("osp_structured_artifact_def");
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
                  String hash = rs.getString("schemaHash");
                  
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
                  sad.setSchema(schemaData);
                  sad.setInstruction(instr);
                  sad.setSchemaHash(hash);
                  
                  structuredArtifactDefinitionManager.save(sad);
                  
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

   protected void runMatrixMigration(Connection con) throws JobExecutionException {
      
   }

   protected void runPresentationMigration(Connection con) throws JobExecutionException {
   
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

   public StructuredArtifactDefinitionManager getStructuredArtifactDefinitionManager() {
      return structuredArtifactDefinitionManager;
   }

   public void setStructuredArtifactDefinitionManager(
         StructuredArtifactDefinitionManager structuredArtifactDefinitionManager) {
      this.structuredArtifactDefinitionManager = structuredArtifactDefinitionManager;
   }
   
   

}

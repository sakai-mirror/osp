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
import org.theospi.portfolio.security.AuthorizationFacade;

public class OspMigrationJob implements Job {

   protected final transient Log logger = LogFactory.getLog(getClass());
   
   private DataSource dataSource;
   private IdManager idManager;
   private AgentManager agentManager;
   private AuthorizationFacade authzManager;
   private Statement stmt;
   private Map tableMap = new HashMap();
   
   public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
      logger.info("Quartz job started: "+this.getClass().getName());
      Connection connection = null;
      try {
         connection = getDataSource().getConnection();
         runAuthzMigration(connection);
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
                  String id = rs.getString("id");
                  String qual = rs.getString("qualifier_id");
                  String agent = rs.getString("agent_id");
                  String func = rs.getString("function_name");
                  
                  authzManager.createAuthorization(agentManager.getAgent(agent), func, idManager.getId(qual));
                  
               }
           } finally {
               rs.close();
           }
        } catch (Exception e) {
            logger.error("error selecting data with this sql: " + sql);
            logger.error("", e);
        } finally {
            try {
                stmt.close();
            } catch (Exception e) {
            }
        }
        logger.info("Quartz task fininshed: runAuthzMigration()");
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
   
   

}

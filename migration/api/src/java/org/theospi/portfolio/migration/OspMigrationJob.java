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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.sakaiproject.component.cover.ServerConfigurationService;
import org.sakaiproject.metaobj.shared.mgt.AgentManager;
import org.sakaiproject.metaobj.shared.mgt.IdManager;
import org.sakaiproject.metaobj.shared.model.ElementBean;
import org.sakaiproject.metaobj.shared.model.Id;
import org.sakaiproject.metaobj.shared.model.IdentifiableObject;
import org.sakaiproject.metaobj.shared.mgt.StructuredArtifactDefinitionManager;
import org.sakaiproject.site.api.SiteService;
import org.sakaiproject.site.api.ToolConfiguration;
import org.theospi.portfolio.help.model.Glossary;
import org.theospi.portfolio.help.model.GlossaryEntry;
import org.theospi.portfolio.security.AuthorizationFacade;
import org.theospi.portfolio.shared.model.ItemDefinitionMimeType;
import org.theospi.portfolio.matrix.MatrixManager;
import org.theospi.portfolio.matrix.model.Cell;
import org.theospi.portfolio.matrix.model.Matrix;
import org.theospi.portfolio.matrix.model.Scaffolding;
import org.theospi.portfolio.matrix.model.ScaffoldingCell;
import org.theospi.portfolio.matrix.model.Criterion;
import org.theospi.portfolio.matrix.model.Level;
import org.theospi.portfolio.matrix.model.WizardPageDefinition;
import org.theospi.portfolio.presentation.PresentationManager;
import org.theospi.portfolio.presentation.model.Presentation;
import org.theospi.portfolio.presentation.model.PresentationComment;
import org.theospi.portfolio.presentation.model.PresentationItem;
import org.theospi.portfolio.presentation.model.PresentationItemDefinition;
import org.theospi.portfolio.presentation.model.PresentationLog;
import org.theospi.portfolio.presentation.model.PresentationTemplate;
import org.theospi.portfolio.presentation.model.TemplateFileRef;
import org.theospi.portfolio.presentation.model.impl.HibernatePresentationProperties;
import org.theospi.portfolio.style.model.Style;

/**
 * 
 *    
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
   private SiteService siteService;
   private Statement stmt;
   private Map tableMap = new HashMap();
   private List authzToolFunctions;
   
   public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
      logger.info("Quartz job started: "+this.getClass().getName());
      Connection connection = null;
      try {

         String developerFlag = ServerConfigurationService.getString("osp.migration.developer");
         
         boolean isDeveloper = developerFlag.equalsIgnoreCase("true");
         
         connection = getDataSource().getConnection();
         
         if(isDeveloper)
            developerClearAllTables(connection);
         
         runAuthzMigration(connection, isDeveloper);
         runGlossaryMigration(connection, isDeveloper);
         runMatrixMigration(connection, isDeveloper);
         runPresentationTemplateMigration(connection, isDeveloper);
         runPresentationMigration(connection, isDeveloper);
      } catch (SQLException e) {
         logger.error("Quartz job errored: "+this.getClass().getName(), e);
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
         
         sql = "SET FOREIGN_KEY_CHECKS=0";
         stmt.executeUpdate(sql);
         
         sql = "TRUNCATE osp_authz_simple";
         stmt.executeUpdate(sql);
         
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

         sql = "TRUNCATE osp_presentation_comment";
         stmt.executeUpdate(sql);
         sql = "TRUNCATE osp_presentation_log";
         stmt.executeUpdate(sql);
         sql = "TRUNCATE osp_presentation_item";
         stmt.executeUpdate(sql);
         sql = "TRUNCATE osp_presentation";
         stmt.executeUpdate(sql);
         
         sql = "TRUNCATE osp_pres_itemdef_mimetype";
         stmt.executeUpdate(sql);
         sql = "TRUNCATE osp_presentation_item_def";
         stmt.executeUpdate(sql);
         sql = "TRUNCATE osp_template_file_ref";
         stmt.executeUpdate(sql);
         sql = "TRUNCATE osp_presentation_template";
         stmt.executeUpdate(sql);
         
      } catch (Exception e) {
         logger.error("error truncating data with this sql: " + sql);
         logger.error("", e);
         throw new JobExecutionException(e);
     } finally {
         try {
            sql = "SET FOREIGN_KEY_CHECKS=1";
            stmt.executeUpdate(sql);
             stmt.close();
         } catch (Exception e) {
         }
     }
   }
   
   private void developerClearAllTables(Connection con) throws JobExecutionException
   {
      logger.info("Quartz task started: developerClearAllTables()");
      String sql = "";
      try {
         stmt = con.createStatement();
         Statement innerstmt = con.createStatement();
         
         sql = "SET FOREIGN_KEY_CHECKS=0";
         stmt.executeUpdate(sql);
         
         sql = "show tables like 'osp_%'";
         ResultSet rs = stmt.executeQuery(sql);
         try {
            while (rs.next()) {
               String tableName = rs.getString(1);
               sql = "TRUNCATE " + tableName;
               innerstmt.executeUpdate(sql);
            }
         }
         finally {
            try {
               innerstmt.close();
               rs.close();
            } catch (Exception e) {
            }
         }
         
         
      } catch (Exception e) {
         logger.error("error truncating data with this sql: " + sql);
         logger.error("", e);
         throw new JobExecutionException(e);
     } finally {
        logger.info("Quartz task finished: developerClearAllTables()");        
        try {
            sql = "SET FOREIGN_KEY_CHECKS=1";
            stmt.executeUpdate(sql);
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
                  // Transformations on the authz stuff that needs to 
                  // change from a tool_id to a site_id
                  try {
                     if (getAuthzToolFunctions().contains(func)) {
                        ToolConfiguration toolConfig = siteService.findTool(qual);
                        if (toolConfig != null) {
                           qual = toolConfig.getContext();
                        }
                     }
                     
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

   protected void runMatrixMigration(Connection con, boolean isDeveloper) throws JobExecutionException {
      logger.info("Quartz task started: runMatrixMigration()");
      
      String tableName = getOldTableName("osp_scaffolding"), 
         tableName2 = null,
         tableName3 = null;
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
                  String documentRoot = rs.getString("documentroot");
                  String privacyxsdid = rs.getString("privacyxsdid");
                  String worksite = rs.getString("worksiteId");
                  boolean published = rs.getBoolean("published");
                  String publishedBy = rs.getString("publishedBy");
                  Date   publishedDate = rs.getDate("publishedDate");
                  
                  String columnLabel = ""; //rs.getString("columnLabel");
                  String rowLabel = ""; //rs.getString("rowLabel");
                  String readyColor = ""; //rs.getString("readyColor");
                  String pendingColor = ""; //rs.getString("pendingColor");
                  String completedColor = ""; //rs.getString("completedColor");
                  String lockColor = ""; //rs.getString("lockColor");
                  int    workflowOption = Scaffolding.HORIZONTAL_PROGRESSION; //rs.getInt("workflowOption");
                  String exposed_page_id = ""; //rs.getString("exposed_page_id");
                  String style_id = ""; //rs.getString("style_id");
                  
                  Scaffolding scaffolding = new Scaffolding();
                  Id sid = idManager.getId(id);
                  scaffolding.setId(null);
                  scaffolding.setNewId(sid);
                  scaffolding.setOwner(agentManager.getAgent(owner));
                  scaffolding.setTitle(title);
                  scaffolding.setDescription(description);
                  scaffolding.setWorksiteId(idManager.getId(worksite));
                  scaffolding.setPublished(published);
                  if(publishedBy != null)
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
                  
                  Style style = null; //new Style();
                  
                  //style.setId(idManager.getId(style_id));
                  scaffolding.setStyle(style);

                  
                  //*****************  run through the criteria
                  tableName = getOldTableName("osp_scaffolding_criteria");
                  tableName2 = getOldTableName("osp_matrix_label");
                  sql = "select * from " + tableName + " join " + tableName2 + " on ELT=ID" +
                     " where parent_id='" + id + "' order by seq_num";
                  
                  ResultSet rss = innerStmt.executeQuery(sql);

                  Map criteriaMap = new HashMap();
                  
                  while (rss.next()) {
                     int sequenceNumber = rss.getInt("seq_num");
                     Id lid = idManager.getId(rss.getString("elt"));
                     String color = rss.getString("color");
                     String textColor = null;
                     String ldescription = rss.getString("description");
                     
                     Criterion criterion = new Criterion();
                     
                     criterion.setId(null);
                     criterion.setNewId(lid);
                     criterion.setColor(color);
                     criterion.setTextColor(textColor);
                     criterion.setScaffolding(scaffolding);
                     criterion.setDescription(ldescription);
                     criterion.setSequenceNumber(sequenceNumber);

                     scaffolding.add(criterion);
                     
                     criteriaMap.put(lid.getValue(), criterion);
                  }
                  
                  

                  //*****************  run through the levels
                  tableName = getOldTableName("osp_scaffolding_levels");
                  tableName2 = getOldTableName("osp_matrix_label");
                  sql = "select * from " + tableName + " join " + tableName2 + " on ELT=ID" +
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

                     level.setId(null);
                     level.setNewId(lid);
                     level.setColor(color);
                     level.setTextColor(textColor);
                     level.setScaffolding(scaffolding);
                     level.setDescription(ldescription);
                     level.setSequenceNumber(sequenceNumber);
                     
                     scaffolding.add(level);
                     
                     levelMap.put(lid.getValue(), level);
                  }
                  
                  

                  //*****************  run through the cells
                  tableName = getOldTableName("osp_scaffolding_cell");
                  sql = "select * from " + tableName + " where scaffolding_id='" + id + "' ";
                  
                  rss = innerStmt.executeQuery(sql);
                  
                  Map scaffoldingCellMap = new HashMap();

                  while (rss.next()) {
                     Id cid = idManager.getId(rss.getString("id"));
                     String criterionStr = rss.getString("rootcriterion_id");
                     String levelStr = rss.getString("level_id");
                     String expectationheader = rss.getString("expectationheader");
                     String initialStatus = rss.getString("initialstatus");
                     String gradablereflection = rss.getString("gradablereflection");
                     
                     Level level = (Level)levelMap.get(levelStr);
                     Criterion criterion = (Criterion)criteriaMap.get(criterionStr);
                     ScaffoldingCell cell = new ScaffoldingCell();

                     cell.setId(null);
                     cell.setNewId(cid);
                     cell.setInitialStatus(initialStatus);
                     cell.setLevel(level);
                     cell.setRootCriterion(criterion);
                     cell.setScaffolding(scaffolding);
                     WizardPageDefinition page = cell.getWizardPageDefinition();
                     
                     page.setSiteId(worksite);
                     page.setTitle(
                           (criterion.getDescription() != null ? criterion.getDescription() : "") 
                           + " - " + 
                           (level.getDescription() != null ? level.getDescription() : ""));
                     
                     scaffolding.add(cell);
                     scaffoldingCellMap.put(cid.getValue(), cell);
                  }
                  
                  Id scaffId = (Id)matrixManager.save(scaffolding);
                  scaffolding = matrixManager.getScaffolding(scaffId);
                  
                  

                  //*****************  run through the user matrices
                  tableName = getOldTableName("osp_matrix_tool");
                  tableName2 = getOldTableName("osp_matrix");
                  tableName3 = getOldTableName("osp_matrix_cell");
                  sql = "select " + tableName3 + ".id, matrix_id, owner, status, reflection_id, scaffolding_cell_id " +
                     " from " + tableName + " join " + tableName2 + 
                        " on matrixtool_id=" + tableName + ".id " + 
                     " join " + tableName3 + " on matrix_id=" + tableName2 + ".id " +
                     " where scaffolding_id='" + id + "' order by owner";
                  
                  rss = innerStmt.executeQuery(sql);

                  String lastOwner = "";
                  Matrix matrix = null;
                  boolean badCell = false;
                  while (rss.next()) {

                     String mowner = rss.getString("owner");
                     String status = rss.getString("status");
                     String scaffolding_cell_id = rss.getString("scaffolding_cell_id");
                     
                     if(!mowner.equals(lastOwner)) {
                        lastOwner = mowner;
                        if(matrix != null && !badCell)
                           matrixManager.save(matrix);
                        
                        badCell = false;
                        matrix = new Matrix();
                        
                        matrix.setOwner(agentManager.getAgent(mowner));
                        matrix.setScaffolding(scaffolding);
                     }
                     badCell = scaffolding_cell_id == null;
                     
                     if(!badCell) {
                        ScaffoldingCell sCell = (ScaffoldingCell)scaffoldingCellMap.get(scaffolding_cell_id);
                        
                        Cell cell = new Cell();
                        cell.getWizardPage().setOwner(matrix.getOwner());
                        cell.setScaffoldingCell(sCell);
                        cell.setStatus(status);
                        
                        matrix.add(cell);
                     }
                  }
                  
                  if(matrix != null && !badCell)
                     matrixManager.save(matrix);
                  
                  
                  
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

   protected void runPresentationTemplateMigration(Connection con, boolean isDeveloper) throws JobExecutionException {
      logger.info("Quartz task started: runPresentationTemplateMigration()");
      String templateTableName = getOldTableName("osp_presentation_template");
      String sql = "select * from " + templateTableName;
      
      try { 
            stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            try {
               while (rs.next()) {
                  String id = rs.getString("id");
                  String name = rs.getString("name");
                  String desc = rs.getString("description");
                  boolean includeHeaderAndFooter = rs.getBoolean("includeHeaderAndFooter");
                  boolean includeComments = rs.getBoolean("includeComments");
                  boolean published = rs.getBoolean("published");
                  String owner = rs.getString("owner_id");
                  String renderer = rs.getString("renderer");
                  String markup = rs.getString("markup");
                  String propertyPage = rs.getString("propertyPage");
                  String documentRoot = rs.getString("documentRoot");
                  Date created = rs.getDate("created");
                  Date modified = rs.getDate("modified");
                  String siteId = rs.getString("site_id");
                  Id tid = idManager.getId(id);
                  
                  PresentationTemplate template = new PresentationTemplate();
                  template.setId(null);
                  template.setNewId(tid);
                  template.setName(name);
                  template.setDescription(desc);
                  template.setIncludeHeaderAndFooter(includeHeaderAndFooter);
                  template.setIncludeComments(includeComments);
                  template.setPublished(published);
                  template.setOwner(agentManager.getAgent(owner));
                  template.setRenderer(idManager.getId(renderer));
                  template.setMarkup(markup);
                  template.setPropertyPage(idManager.getId(propertyPage));
                  template.setDocumentRoot(documentRoot);
                  template.setCreated(created);
                  template.setModified(modified);
                  template.setSiteId(siteId);
                  
                  Set itemDefs = createTemplateItemDefs(con, template);
                  template.setItems(itemDefs);
                  
                  Set fileRefs = createTemplateFileRefs(con, template);
                  template.setFiles(fileRefs);
                  
                  
                  presentationManager.storeTemplate(template, false, false);
                  
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
        logger.info("Quartz task fininshed: runPresentationTemplateMigration()");
   }
   
   protected void runPresentationMigration(Connection con, boolean isDeveloper) throws JobExecutionException {
      logger.info("Quartz task started: runPresentationMigration()");
      String templateTableName = getOldTableName("osp_presentation");
      String sql = "select * from " + templateTableName;
      
      try { 
            stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            try {
               while (rs.next()) {
                  String id = rs.getString("id");
                  String owner = rs.getString("owner_id");
                  String templateId = rs.getString("template_id");
                  String name = rs.getString("name");
                  String desc = rs.getString("description");
                  boolean isDefault = rs.getBoolean("isDefault");
                  boolean isPublic = rs.getBoolean("isPublic");
                  
                  Date expiresOn = rs.getDate("expiresOn");
                  Date created = rs.getDate("created");
                  Date modified = rs.getDate("modified");
                  //Blob properties = rs.getBlob("properties");
                  String toolId = rs.getString("tool_id");
                  Id pid = idManager.getId(id);
                  
                  Presentation presentation = new Presentation();
                  presentation.setNewObject(true);
                  presentation.setId(null);
                  presentation.setNewId(pid);
                  presentation.setName(name);
                  presentation.setDescription(desc);
                  presentation.setIsDefault(isDefault);
                  presentation.setIsPublic(isPublic);
                  presentation.setOwner(agentManager.getAgent(owner));
                  presentation.setExpiresOn(expiresOn);
                  presentation.setCreated(created);
                  presentation.setModified(modified);
                  presentation.setToolId(toolId);
                                    
                  String siteId = "";
                  try {
                     siteId = siteService.findTool(toolId).getContext();
                  } catch (NullPointerException npe) {
                     logger.warn("Quartz task warning: runPresentationMigration().  Can't find context for toolId: " + toolId);
                     siteId = "dataMigrationError";
                  }
                  
                  presentation.setSiteId(siteId);
                  
                  HibernatePresentationProperties hpp = new HibernatePresentationProperties();
                  String[] names = {"properties"};
                  Object props = hpp.nullSafeGet(rs, names, null);
                  presentation.setProperties((ElementBean)props);
                  presentation.setPresentationType(Presentation.TEMPLATE_TYPE);
                  PresentationTemplate template = 
                     presentationManager.getPresentationTemplate(idManager.getId(templateId));
                  presentation.setTemplate(template);
                  
                  presentation.setAllowComments(template.isIncludeComments());
                  
                  Set items = createPresentationItems(con, presentation);
                  presentation.setItems(items);
                  
                  presentationManager.storePresentation(presentation, false, false);
                  
                  createPresentationComments(con, presentation);
                  createPresentationLogs(con, presentation);
                  
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
        logger.info("Quartz task fininshed: runPresentationMigration()");
   }
   
   protected Set createTemplateItemDefs(Connection con, PresentationTemplate template) throws JobExecutionException {
      Set itemDefs = new HashSet();
      String itemDefTableName = getOldTableName("osp_presentation_item_def");
      String sql = "select * from " + itemDefTableName + " where template_id = '" + resolveId(template).getValue() + "'";
      Statement innerstmt = null;
      try {
         
         innerstmt = con.createStatement();
         ResultSet rs2 = innerstmt.executeQuery(sql);
         try {
            while (rs2.next()) {

               String id = rs2.getString("id");
               String name = rs2.getString("name");
               String title = rs2.getString("title");
               String description = rs2.getString("description");
               boolean allowMultiple = rs2.getBoolean("allowMultiple");
               String type = rs2.getString("type");
               String externalType = rs2.getString("external_type");
               int seq = rs2.getInt("sequence_no");
               //String templateId = rs2.getString("template_id");
               
               Id defid = idManager.getId(id);
               PresentationItemDefinition pid = new PresentationItemDefinition();
               pid.setId(null);
               pid.setNewId(defid);
               pid.setName(name);
               pid.setTitle(title);
               pid.setDescription(description);
               pid.setAllowMultiple(allowMultiple);
               pid.setType(type);
               pid.setExternalType(externalType);
               pid.setSequence(seq);
               pid.setPresentationTemplate(template);
               Set mimeTypes = createTemplateItemDefMimeTypes(con, pid);
               pid.setMimeTypes(mimeTypes);
               
               itemDefs.add(pid);
            }

         } finally {
            rs2.close();
         } 
      } catch (Exception e) {
         logger.error("error selecting data with this sql: " + sql);
         logger.error("", e);
         throw new JobExecutionException(e);
      } finally {
         try {
            innerstmt.close();
         } catch (Exception e) {
         }
      }
      return itemDefs;
   }
   
   protected Set createTemplateFileRefs(Connection con, PresentationTemplate template) throws JobExecutionException {
      Set fileRefs = new HashSet();
      String fileRefTableName = getOldTableName("osp_template_file_ref");
      String sql = "select * from " + fileRefTableName + " where template_id = '" + resolveId(template).getValue() + "'";
      Statement innerstmt = null;
      try {
         
         innerstmt = con.createStatement();
         ResultSet rs2 = innerstmt.executeQuery(sql);
         try {
            while (rs2.next()) {

               String id = rs2.getString("id");
               String fileId = rs2.getString("file_id");
               String fileTypeId = rs2.getString("file_type_id");
               String usage = rs2.getString("usage_desc");
               //String templateId = rs2.getString("template_id");
               
               Id refid = idManager.getId(id);
               TemplateFileRef tfr = new TemplateFileRef();
               tfr.setId(null);
               tfr.setNewId(refid);
               tfr.setFileId(fileId);
               tfr.setFileType(fileTypeId);
               tfr.setUsage(usage);
               
               tfr.setPresentationTemplate(template);
               
               fileRefs.add(tfr);
            }

         } finally {
            rs2.close();
         } 
      } catch (Exception e) {
         logger.error("error selecting data with this sql: " + sql);
         logger.error("", e);
         throw new JobExecutionException(e);
      } finally {
         try {
            innerstmt.close();
         } catch (Exception e) {
         }
      }
      return fileRefs;
   }
   
   protected Set createTemplateItemDefMimeTypes(Connection con, PresentationItemDefinition itemDef) throws JobExecutionException {
      Set mimeTypes = new HashSet();
      String itemDefMimeTypeTableName = getOldTableName("osp_pres_itemdef_mimetype");
      String sql = "select * from " + itemDefMimeTypeTableName + " where item_def_id = '" + resolveId(itemDef).getValue() + "'";
      Statement innerstmt = null;
      try {
         
         innerstmt = con.createStatement();
         ResultSet rs2 = innerstmt.executeQuery(sql);
         try {
            while (rs2.next()) {

               String primary = rs2.getString("primaryMimeType");
               String secondary = rs2.getString("secondaryMimeType");
               
               ItemDefinitionMimeType mimeType = new ItemDefinitionMimeType(primary, secondary);
               
               mimeTypes.add(mimeType);
            }

         } finally {
            rs2.close();
         } 
      } catch (Exception e) {
         logger.error("error selecting data with this sql: " + sql);
         logger.error("", e);
         throw new JobExecutionException(e);
      } finally {
         try {
            innerstmt.close();
         } catch (Exception e) {
         }
      }
      return mimeTypes;
   }
   
   protected Set createPresentationItems(Connection con, Presentation presentation) throws JobExecutionException {
      Set items = new HashSet();
      String itemTableName = getOldTableName("osp_presentation_item");
      String sql = "select * from " + itemTableName + " where presentation_id = '" + resolveId(presentation).getValue() + "'";
      Statement innerstmt = null;
      try {
         
         innerstmt = con.createStatement();
         ResultSet rs2 = innerstmt.executeQuery(sql);
         try {
            while (rs2.next()) {

               String artifactId = rs2.getString("artifact_id");
               String itemDef = rs2.getString("item_definition_id");
               
               PresentationItem item = new PresentationItem();
               item.setArtifactId(idManager.getId(artifactId));
               item.setDefinition(presentationManager.getPresentationItemDefinition(idManager.getId(itemDef)));
               
               items.add(item);
            }

         } finally {
            rs2.close();
         } 
      } catch (Exception e) {
         logger.error("error selecting data with this sql: " + sql);
         logger.error("", e);
         throw new JobExecutionException(e);
      } finally {
         try {
            innerstmt.close();
         } catch (Exception e) {
         }
      }
      return items;
   }
   
   protected void createPresentationComments(Connection con, Presentation presentation) throws JobExecutionException {
      String commentTableName = getOldTableName("osp_presentation_comment");
      String sql = "select * from " + commentTableName + " where presentation_id = '" + resolveId(presentation).getValue() + "'";
      Statement innerstmt = null;
      try {
         
         innerstmt = con.createStatement();
         ResultSet rs2 = innerstmt.executeQuery(sql);
         try {
            while (rs2.next()) {

               String id = rs2.getString("id");
               String title = rs2.getString("title");
               String commentText = rs2.getString("commentText");
               String owner = rs2.getString("creator_id");
               byte visibility = rs2.getByte("visibility");
               Date created = rs2.getDate("created");
               
               Id cid = idManager.getId(id);
               PresentationComment comment = new PresentationComment();
               comment.setId(null);
               comment.setNewId(cid);
               comment.setTitle(title);
               comment.setComment(commentText);
               comment.setCreator(agentManager.getAgent(idManager.getId(owner)));
               comment.setPresentation(presentation);
               comment.setVisibility(visibility);
               comment.setCreated(created);
               presentationManager.createComment(comment, false, false);
            }

         } finally {
            rs2.close();
         } 
      } catch (Exception e) {
         logger.error("error selecting data with this sql: " + sql);
         logger.error("", e);
         throw new JobExecutionException(e);
      } finally {
         try {
            innerstmt.close();
         } catch (Exception e) {
         }
      }
   }
   
   protected void createPresentationLogs(Connection con, Presentation presentation) throws JobExecutionException {
      String logTableName = getOldTableName("osp_presentation_log");
      String sql = "select * from " + logTableName + " where presentation_id = '" + resolveId(presentation).getValue()  +"'";
      Statement innerstmt = null;
      try {
         
         innerstmt = con.createStatement();
         ResultSet rs2 = innerstmt.executeQuery(sql);
         try {
            while (rs2.next()) {

               String id = rs2.getString("id");
               String viewer = rs2.getString("viewer_id");
               Date viewed = rs2.getDate("view_date");
               
               Id lid = idManager.getId(id);
               PresentationLog log = new PresentationLog();
               log.setId(null);
               log.setNewId(lid);
               log.setViewer(agentManager.getAgent(idManager.getId(viewer)));
               log.setViewDate(viewed);
               log.setPresentation(presentation);
               presentationManager.storePresentationLog(log);
            }

         } finally {
            rs2.close();
         } 
      } catch (Exception e) {
         logger.error("error selecting data with this sql: " + sql);
         logger.error("", e);
         throw new JobExecutionException(e);
      } finally {
         try {
            innerstmt.close();
         } catch (Exception e) {
         }
      }
   }
   
   private Id resolveId(IdentifiableObject obj) {
      if (obj.getId() == null)
         return obj.getNewId();
      return obj.getId();
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

   public SiteService getSiteService() {
      return siteService;
   }

   public void setSiteService(SiteService siteService) {
      this.siteService = siteService;
   }

   public List getAuthzToolFunctions() {
      return authzToolFunctions;
   }

   public void setAuthzToolFunctions(List authzToolFunctions) {
      this.authzToolFunctions = authzToolFunctions;
   }

}

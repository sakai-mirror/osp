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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.sakaiproject.authz.api.SecurityService;
import org.sakaiproject.component.cover.ServerConfigurationService;
import org.sakaiproject.content.api.ContentCollectionEdit;
import org.sakaiproject.content.api.ContentHostingService;
import org.sakaiproject.content.api.ContentResource;
import org.sakaiproject.db.cover.SqlService;
import org.sakaiproject.entity.api.ResourceProperties;
import org.sakaiproject.entity.api.ResourcePropertiesEdit;
import org.sakaiproject.event.cover.NotificationService;
import org.sakaiproject.exception.IdUsedException;
import org.sakaiproject.metaobj.shared.mgt.AgentManager;
import org.sakaiproject.metaobj.shared.mgt.IdManager;
import org.sakaiproject.metaobj.shared.mgt.MetaobjEntityManager;
import org.sakaiproject.metaobj.shared.model.Agent;
import org.sakaiproject.metaobj.shared.model.ElementBean;
import org.sakaiproject.metaobj.shared.model.Id;
import org.sakaiproject.metaobj.shared.model.IdentifiableObject;
import org.sakaiproject.metaobj.shared.model.StructuredArtifactDefinitionBean;
import org.sakaiproject.metaobj.shared.mgt.StructuredArtifactDefinitionManager;
import org.sakaiproject.metaobj.utils.xml.SchemaFactory;
import org.sakaiproject.metaobj.utils.xml.SchemaNode;
import org.sakaiproject.site.api.SiteService;
import org.sakaiproject.site.api.ToolConfiguration;
import org.sakaiproject.tool.cover.SessionManager;
import org.theospi.portfolio.guidance.mgt.GuidanceManager;
import org.theospi.portfolio.guidance.model.Guidance;
import org.theospi.portfolio.help.model.Glossary;
import org.theospi.portfolio.help.model.GlossaryEntry;
import org.theospi.portfolio.security.AuthorizationFacade;
import org.theospi.portfolio.security.impl.AllowAllSecurityAdvisor;
import org.theospi.portfolio.security.Authorization;
import org.theospi.portfolio.shared.model.ItemDefinitionMimeType;
import org.theospi.portfolio.matrix.MatrixFunctionConstants;
import org.theospi.portfolio.matrix.DefaultScaffoldingBean;
import org.theospi.portfolio.review.mgt.ReviewManager;
import org.theospi.portfolio.review.model.Review;
import org.theospi.portfolio.matrix.MatrixManager;
import org.theospi.portfolio.matrix.model.Attachment;
import org.theospi.portfolio.matrix.model.Cell;
import org.theospi.portfolio.matrix.model.Matrix;
import org.theospi.portfolio.matrix.model.Scaffolding;
import org.theospi.portfolio.matrix.model.ScaffoldingCell;
import org.theospi.portfolio.matrix.model.Criterion;
import org.theospi.portfolio.matrix.model.Level;
import org.theospi.portfolio.matrix.model.WizardPageDefinition;
import org.theospi.portfolio.matrix.model.WizardPageForm;
import org.theospi.portfolio.migration.model.impl.FormWrapper;
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
import org.theospi.portfolio.workflow.mgt.WorkflowManager;

/**
 *
 */
public class OspMigrationJob implements Job {

   protected final transient Log logger = LogFactory.getLog(getClass());
   
   private DataSource dataSource;
   private IdManager idManager;
   private AgentManager agentManager;
   private AuthorizationFacade authzManager;
   private Glossary glossaryManager;
   private GuidanceManager guidanceManager;
   private StructuredArtifactDefinitionManager structuredArtifactDefinitionManager;
   private PresentationManager presentationManager;
   private MatrixManager matrixManager;
   private ReviewManager reviewManager;
   private WorkflowManager workflowManager;
   private SiteService siteService;
   private SecurityService securityService;
   private ContentHostingService contentHosting;
   private DefaultScaffoldingBean defaultScaffoldingBean;
   private Statement stmt;
   private Map tableMap = new HashMap();
   private List authzToolFunctions;
   private List matrixForms;
   
   private Map userUniquenessMap;
   
   private static final String MIGRATED_FOLDER = "migratedMatrixForms";
   private static final String MIGRATED_FOLDER_PATH = "/" + MIGRATED_FOLDER + "/";
   private static final String EXPECTATION_FORM_ID_VALUE = "expectationForm";
   private static final String INTELLECTUAL_GROWTH_FORM_ID_VALUE = "intellectualGrowthForm";
   private static final String FEEDBACK_FORM_ID_VALUE = "feedbackForm";
   private static final String EVALUATION_FORM_ID_VALUE = "evaluationForm";
   
   
   public final static String FORM_TYPE = "form";
   
   private Id feedbackFormId, evaluationFormId, intelGrowthFormId, expectationFormId;
   
   public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
      logger.info("Quartz job started: "+this.getClass().getName());
      Connection connection = null;
      try {

         feedbackFormId = idManager.getId(FEEDBACK_FORM_ID_VALUE);
         evaluationFormId = idManager.getId(EVALUATION_FORM_ID_VALUE);
         intelGrowthFormId = idManager.getId(INTELLECTUAL_GROWTH_FORM_ID_VALUE);
         expectationFormId = idManager.getId(EXPECTATION_FORM_ID_VALUE);
         
         String developerFlag = ServerConfigurationService.getString("osp.migration.developer");
         
         boolean isDeveloper = developerFlag.equalsIgnoreCase("true");
         
         connection = SqlService.borrowConnection(); // getDataSource().getConnection();
         
         if(isDeveloper)
            developerClearAllTables(connection);
         
         userUniquenessMap = new HashMap();
         
         initMatrixForms();
         //createFeedbackForm("admin", "testform", "foobar");
         
         runAuthzMigration(connection, isDeveloper);
         runGlossaryMigration(connection, isDeveloper);
         runMatrixMigration(connection, isDeveloper);
         runPresentationTemplateMigration(connection, isDeveloper);
         runPresentationMigration(connection, isDeveloper);
         
         userUniquenessMap = null;
         
      } catch (SQLException e) {
         logger.error("Quartz job errored: "+this.getClass().getName(), e);
         throw new JobExecutionException(e);
      } finally {
         if (connection != null) {
            try {
               SqlService.returnConnection(connection); //connection.close();
            }
            catch (Exception e) {
               // can't do anything with this.
            }
         }
      }
      
      logger.info("Quartz job fininshed: "+this.getClass().getName());
   }
   
   private Id saveForm(String owner, String name, byte[] fileContent, String formType) {
      getSecurityService().pushAdvisor(new AllowAllSecurityAdvisor());

      org.sakaiproject.tool.api.Session sakaiSession = SessionManager.getCurrentSession();
      String userId = sakaiSession.getUserId();
      sakaiSession.setUserId(owner);
      sakaiSession.setUserEid(owner);
      
      String description = "";
      String folder = "/user/" + owner;
      String type = "application/x-osp";

      try {
         ContentCollectionEdit groupCollection = getContentHosting().addCollection(folder);
         groupCollection.getPropertiesEdit().addProperty(ResourceProperties.PROP_DISPLAY_NAME, owner);
         getContentHosting().commitCollection(groupCollection);
      }
      catch (IdUsedException e) {
         // ignore... it is already there.
      }
      catch (Exception e) {
         throw new RuntimeException(e);
      }

      folder = "/user/" + owner + MIGRATED_FOLDER_PATH;
      
      try {
         ContentCollectionEdit groupCollection = getContentHosting().addCollection(folder);
         groupCollection.getPropertiesEdit().addProperty(ResourceProperties.PROP_DISPLAY_NAME, MIGRATED_FOLDER);
         groupCollection.getPropertiesEdit().addProperty(ResourceProperties.PROP_DESCRIPTION, "Folder for Migrated Matrix Forms");
         getContentHosting().commitCollection(groupCollection);
      }
      catch (IdUsedException e) {
         // ignore... it is already there.
      }
      catch (Exception e) {
         throw new RuntimeException(e);
      }
      
      try {
         ResourcePropertiesEdit resourceProperties = getContentHosting().newResourceProperties();
         resourceProperties.addProperty (ResourceProperties.PROP_DISPLAY_NAME, name);
         resourceProperties.addProperty (ResourceProperties.PROP_DESCRIPTION, description);
         resourceProperties.addProperty(ResourceProperties.PROP_CONTENT_ENCODING, "UTF-8");
         resourceProperties.addProperty(ResourceProperties.PROP_STRUCTOBJ_TYPE, formType);
         resourceProperties.addProperty(ContentHostingService.PROP_ALTERNATE_REFERENCE, MetaobjEntityManager.METAOBJ_ENTITY_PREFIX);
         
         ContentResource resource = getContentHosting().addResource(name, folder, 0, type,
               fileContent, resourceProperties, NotificationService.NOTI_NONE);
         return idManager.getId(getContentHosting().getUuid(resource.getId()));
      }
      catch (Exception e) {
         throw new RuntimeException(e);
      } finally {
         getSecurityService().popAdvisor();
         sakaiSession.setUserEid(userId);
         sakaiSession.setUserId(userId);
      }
   }
   
   private Id createFeedbackForm(String owner, String title, String comment) {
      String formId = FEEDBACK_FORM_ID_VALUE;
      ElementBean feedbackForm = setupForm(formId);
      feedbackForm.put("comment", comment.replace('\u0000', ' '));
      byte[] xml = feedbackForm.toXmlString().getBytes();
      return saveForm(owner, title, xml, formId);
   }

   private Id createEvaluationForm(String owner, String title, String grade, String comment) {
      String formId = EVALUATION_FORM_ID_VALUE;
      ElementBean evalForm = setupForm(formId);
      evalForm.put("grade", grade);
      if(comment == null)
         comment = "";
      evalForm.put("comment", comment.replace('\u0000', ' '));
      byte[] xml = evalForm.toXmlString().getBytes();
      return saveForm(owner, title, xml, formId);
   }
   
   private Id createReflectionForm(String owner, String title, String evidence) {
      String formId = INTELLECTUAL_GROWTH_FORM_ID_VALUE;
      ElementBean feedbackForm = setupForm(formId);
      if(evidence == null)
         evidence = "";
      feedbackForm.put("evidence", evidence.replace('\u0000', ' '));
      byte[] xml = feedbackForm.toXmlString().getBytes();
      return saveForm(owner, title, xml, formId);
   }
   
   private Id createExpectationForm(String owner, String title, String evidence, String connect) {
      String formId = EXPECTATION_FORM_ID_VALUE;
      ElementBean feedbackForm = setupForm(formId);
      if(evidence == null)
         evidence = "";
      if(connect == null)
         connect = "";
      feedbackForm.put("evidence", evidence.replace('\u0000', ' '));
      feedbackForm.put("connect", connect.replace('\u0000', ' '));
      byte[] xml = feedbackForm.toXmlString().getBytes();
      return saveForm(owner, title, xml, formId);
   }
   
   private ElementBean setupForm(String formId) {
      StructuredArtifactDefinitionBean bean = structuredArtifactDefinitionManager.loadHome(idManager.getId(formId));
      SchemaFactory schemaFactory = SchemaFactory.getInstance();
      SchemaNode schema = schemaFactory.getSchema(new ByteArrayInputStream(bean.getSchema())).getChild(bean.getDocumentRoot());
      ElementBean form = new ElementBean(bean.getDocumentRoot(),schema);
      return form;
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

         sql = "DELETE FROM content_resource WHERE RESOURCE_ID LIKE '%" + MIGRATED_FOLDER + "%'";
         stmt.executeUpdate(sql);
         
         sql = "TRUNCATE content_resource_lock";
         stmt.executeUpdate(sql);
         
         sql = "show tables like 'osp_%'";
         ResultSet rs = stmt.executeQuery(sql);
         try {
            while (rs.next()) {
               String tableName = rs.getString(1);
               sql = "TRUNCATE " + tableName;
               if(!tableName.endsWith("_BKP"))
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
   
   protected void initMatrixForms() {
      logger.info("Quartz task started: initMatrixForms()"); 
      getSecurityService().pushAdvisor(new AllowAllSecurityAdvisor());

      org.sakaiproject.tool.api.Session sakaiSession = SessionManager.getCurrentSession();
      String userId = sakaiSession.getUserId();
      sakaiSession.setUserId("admin");
      sakaiSession.setUserEid("admin");
      List forms = new ArrayList();

      try {
         for (Iterator i=getMatrixForms().iterator();i.hasNext();) {
            forms.add(processDefinedForm((FormWrapper)i.next()));
         }

         for (Iterator i=forms.iterator();i.hasNext();) {
            StructuredArtifactDefinitionBean form = (StructuredArtifactDefinitionBean) i.next();
            structuredArtifactDefinitionManager.save(form);
         }

      } finally {
         getSecurityService().popAdvisor();
         sakaiSession.setUserEid(userId);
         sakaiSession.setUserId(userId);
      }
      logger.info("Quartz task finished: initMatrixForms()"); 
   }
   
   protected StructuredArtifactDefinitionBean processDefinedForm(FormWrapper wrapper) {
      StructuredArtifactDefinitionBean form = structuredArtifactDefinitionManager.loadHome(getIdManager().getId(wrapper.getIdValue()));

      if (form == null) {
         form = new StructuredArtifactDefinitionBean();
         form.setCreated(new Date());
         form.setNewId(getIdManager().getId(wrapper.getIdValue()));
      }

      updateForm(wrapper, form);
      
      return form;
   }
   
   protected void updateForm(FormWrapper wrapper, StructuredArtifactDefinitionBean form) {
      if (form.getId() == null) {
         
      }
      
      form.setSchema(loadResource(wrapper.getXsdFileLocation()).toByteArray());
      form.setModified(new Date());
      form.setDescription(wrapper.getDescription());
      form.setGlobalState(StructuredArtifactDefinitionBean.STATE_PUBLISHED);
      form.setSiteState(StructuredArtifactDefinitionBean.STATE_UNPUBLISHED);
      form.setDocumentRoot(wrapper.getDocumentRoot());
      form.setInstruction(wrapper.getInstruction());
      form.setExternalType(wrapper.getExternalType());
      
      form.setSiteId(null);
      form.setOwner(getAgentManager().getAgent("admin"));
   }
   
   protected ByteArrayOutputStream loadResource(String name) {
      ByteArrayOutputStream bos = new ByteArrayOutputStream();
      InputStream is = getClass().getResourceAsStream(name);

      try {
         int c = is.read();
         while (c != -1) {
            bos.write(c);
            c = is.read();
         }
         bos.flush();
      }
      catch (IOException e) {
         throw new RuntimeException(e);
      } finally {
         try {
            is.close();
         }
         catch (IOException e) {
            // can't do anything now..
         }
      }
      return bos;
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
               String agentStr = rs.getString("agent_id");
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
                  if (func.equalsIgnoreCase(MatrixFunctionConstants.REVIEW_MATRIX)) {
                     func = MatrixFunctionConstants.EVALUATE_MATRIX;
                  }
                  
                  Agent agent = agentManager.getAgent(agentStr);
                  
                  // there needs to be an agent or else the authorization is invalid
                  if(agent == null)
                     logger.error("OSP Migration error: agent was null: " + agentStr);
                  else if(agent.getId() == null)
                     logger.error("OSP Migration error: agent id was null: " + agentStr);
                  else if(agent.getId().getValue() == null)
                     logger.error("OSP Migration error: agent id value was null: " + agentStr);
                  else if(qual == null)
                     logger.error("OSP Migration error: qualifier was null: " + qual);
                  else
                     authzManager.createAuthorization(agent, func, idManager.getId(qual));
               } catch(Exception e) {
                  if(!isDeveloper)
                     throw e;
               }
            }
         } finally {
            rs.close();
         }
        
         //This will create new authorizations for the review and view functions
         sql = "select distinct ss.site_id, role_name, '" + MatrixFunctionConstants.USE_SCAFFOLDING + "' as func " + 
               "From sakai_site_tool st JOIN sakai_site ss ON st.site_id = ss.site_id "+
               "JOIN SAKAI_REALM r ON r.realm_id = CONCAT('/site/', ss.site_id) " +
               "JOIN sakai_realm_rl_fn rf ON r.REALM_KEY = rf.REALM_KEY " +
               " JOIN sakai_realm_role rr ON rf.ROLE_KEY = rr.ROLE_KEY " +
               "where st.registration = 'osp.matrix' " +
               "and role_name in ('access', 'member', 'student') " + 
               "union " +
               "select distinct ss.site_id, role_name, '" + MatrixFunctionConstants.REVIEW_MATRIX + "' as func " + 
               "From sakai_site_tool st JOIN sakai_site ss ON st.site_id = ss.site_id " +
               "JOIN SAKAI_REALM r ON r.realm_id = CONCAT('/site/', ss.site_id) " +
               "JOIN sakai_realm_rl_fn rf ON r.REALM_KEY = rf.REALM_KEY " +
               " JOIN sakai_realm_role rr ON rf.ROLE_KEY = rr.ROLE_KEY " +
               "where st.registration = 'osp.matrix' " +
               "and role_name in ('maintain', 'project owner', 'instructor')";
      
         stmt = con.createStatement();
         rs = stmt.executeQuery(sql);
         try {
            while (rs.next()) {
               //String id = rs.getString("id");
               String siteId = rs.getString("site_id");
               String role = rs.getString("role_name");
               String func = rs.getString("func");

               String agent = "/site/" + siteId + "/" + role;
               try {
                  // the agent has already been verified as they are coming from the db
                  authzManager.createAuthorization(agentManager.getAgent(agent), func, idManager.getId(siteId));
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

      Statement matrixInnerStmt = null, innerStmt = null;
      try {
         List additionalForms = new ArrayList();
         additionalForms.add(expectationFormId.getValue());

         matrixInnerStmt = con.createStatement();
         innerStmt = con.createStatement();
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

               String columnLabel = defaultScaffoldingBean.getColumnLabel(); //rs.getString("columnLabel");
               String rowLabel = defaultScaffoldingBean.getRowLabel(); //rs.getString("rowLabel");
               String readyColor = defaultScaffoldingBean.getReadyColor(); //rs.getString("readyColor");
               String pendingColor = defaultScaffoldingBean.getPendingColor(); //rs.getString("pendingColor");
               String completedColor = defaultScaffoldingBean.getCompletedColor(); //rs.getString("completedColor");
               String lockColor = defaultScaffoldingBean.getLockedColor(); //rs.getString("lockColor");
               int    workflowOption = Scaffolding.HORIZONTAL_PROGRESSION; //rs.getInt("workflowOption");
               String exposed_page_id = ""; //rs.getString("exposed_page_id");
               String style_id = ""; //rs.getString("style_id");

               Scaffolding scaffolding = new Scaffolding();
               Id sid = idManager.getId(id);
               scaffolding.setId(null);
               scaffolding.setNewId(sid);
               Agent scaffAgent = agentManager.getAgent(owner);
               
               if(scaffAgent == null) {
                  logger.error("OSP Migration Error: The scaffolding owner agent couldn't be found: " + owner);
                  continue;
               } else if(scaffAgent.getId() == null) {
                  logger.error("OSP Migration Error: The scaffolding owner agent id couldn't be found: " + owner);
                  continue;
               } else if(scaffAgent.getId().getValue() == null) {
                  logger.error("OSP Migration Error: The scaffolding owner agent id value couldn't be found: " + owner);
                  continue;
               } 
               
               
               scaffolding.setOwner(scaffAgent);
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
               
               try {
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
               } finally {
                  rss.close();
               }


               //*****************  run through the levels
               tableName = getOldTableName("osp_scaffolding_levels");
               tableName2 = getOldTableName("osp_matrix_label");
               sql = "select * from " + tableName + " join " + tableName2 + " on ELT=ID" +
               " where scaffolding_id='" + id + "' order by seq_num";

               rss = innerStmt.executeQuery(sql);

               Map levelMap = new HashMap();

               try {
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
               } finally {
                  rss.close();
               }


               //*****************  run through the scells
               tableName = getOldTableName("osp_scaffolding_cell");
               sql = "select * from " + tableName + " where scaffolding_id='" + id + "' ";

               rss = innerStmt.executeQuery(sql);

               Map scaffoldingCellMap = new HashMap();
               Map scaffoldingCellExpheadMap = new HashMap();

               try {
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
                     
                     page.setNewId(idManager.createId());
                     page.setSiteId(worksite);
                     page.setTitle(
                           (criterion.getDescription() != null ? criterion.getDescription() : "") 
                           + " - " + 
                           (level.getDescription() != null ? level.getDescription() : ""));

   
                     cell.setEvaluationDevice(evaluationFormId);
                     cell.setEvaluationDeviceType(FORM_TYPE);
                     cell.setReflectionDevice(intelGrowthFormId);
                     cell.setReflectionDeviceType(FORM_TYPE);
                     cell.setReviewDevice(feedbackFormId);
                     cell.setReviewDeviceType(FORM_TYPE);
                     cell.setAdditionalForms(additionalForms);

                     // this needs to be after setting the forms
                     page.setEvalWorkflows(
                           new HashSet(getWorkflowManager().createEvalWorkflows(page))
                           );
   
                     scaffolding.add(cell);
                     scaffoldingCellMap.put(cid.getValue(), cell);
                     scaffoldingCellExpheadMap.put(cid.getValue(), expectationheader);
                     

                     List scellAuthzs = authzManager.getAuthorizations(null, MatrixFunctionConstants.EVALUATE_MATRIX, cid);
                     
                      for(Iterator i = scellAuthzs.iterator(); i.hasNext(); ) {
                         Authorization a = (Authorization)i.next();
                         
                         authzManager.createAuthorization(a.getAgent(), a.getFunction(), page.getNewId());
                         authzManager.deleteAuthorization(a.getAgent(), a.getFunction(), a.getQualifier());
                      }
                  }
               } finally {
                  rss.close();
               }

               // save the scaffolding!
               Id scaffId = (Id)matrixManager.save(scaffolding);
               scaffolding = matrixManager.getScaffolding(scaffId);


               // migrate the expectations into the guidance.instruction of the cell

               tableName = getOldTableName("osp_scaffolding_cell");
               tableName2 = getOldTableName("osp_expectation");
               tableName3 = getOldTableName("osp_matrix_label");
               sql = "select SCAFFOLDING_CELL_ID, " + tableName3 + ".ID, DESCRIPTION " +
               " FROM " + tableName +
               " JOIN " + tableName2 + " ON " + tableName + ".ID=SCAFFOLDING_CELL_ID " +
               " JOIN " + tableName3 + " ON ELT=" + tableName3 + ".ID " + 
               " where scaffolding_id='" + id + "' ORDER BY SCAFFOLDING_CELL_ID, SEQ_NUM";

               rss = innerStmt.executeQuery(sql);

               String lastScaffoldingCellId = "", guidanceText = null, scaffoldingCellId = null;

               try {
                  while (rss.next()) {
                     scaffoldingCellId = rss.getString("SCAFFOLDING_CELL_ID");
   
                     if(!scaffoldingCellId.equals(lastScaffoldingCellId)) {
                        if(guidanceText != null) {
                           ScaffoldingCell scell = (ScaffoldingCell)scaffoldingCellMap.get(lastScaffoldingCellId);
                           Guidance guide = guidanceManager.createNew(
                                 "", worksite, 
                                 scell.getWizardPageDefinition().getId(), 
                                 MatrixFunctionConstants.VIEW_SCAFFOLDING_GUIDANCE, 
                                 MatrixFunctionConstants.EDIT_SCAFFOLDING_GUIDANCE);
                           guidanceText += "</ul>";
                           guide.getInstruction().setText(guidanceText);
                           scell.setGuidance(guide);
                           matrixManager.storeScaffoldingCell(scell);
                        }
                        lastScaffoldingCellId = scaffoldingCellId;
                        //starts a new cell
                        String expHeader = (String)scaffoldingCellExpheadMap.get(scaffoldingCellId);
                        guidanceText = expHeader + "\n<ul>";
                     }
                     if(guidanceText.length() > 0)
                        guidanceText += "\n<br />";
                     guidanceText += "<li>" + rss.getString("DESCRIPTION") + "</li>";
                  }
                  
                  if(guidanceText != null) {
                     ScaffoldingCell scell = (ScaffoldingCell)scaffoldingCellMap.get(scaffoldingCellId);
                     Guidance guide = guidanceManager.createNew(
                           "", worksite, 
                           scell.getWizardPageDefinition().getId(), 
                           MatrixFunctionConstants.VIEW_SCAFFOLDING_GUIDANCE, 
                           MatrixFunctionConstants.EDIT_SCAFFOLDING_GUIDANCE);
                     guidanceText += "</ul>";
                     guide.getInstruction().setText(guidanceText);
                     scell.setGuidance(guide);
                     matrixManager.storeScaffoldingCell(scell);
                  }
               } finally {
                  rss.close();
               }



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

               tableName = getOldTableName("osp_reflection");
               tableName2 = getOldTableName("osp_reflection_item");
               tableName3 = getOldTableName("osp_reviewer_item");

               String lastOwner = "";
               Matrix matrix = null;
               boolean badCell = false, badMatrix = false;
               int intelGrowthIndex = 1;

               try {
                  while (rss.next()) {
   
                     String mcidStr = rss.getString("id");
                     Id mcid = idManager.getId(mcidStr);
                     String mowner = rss.getString("owner");
                     String status = rss.getString("status");
                     String scaffolding_cell_id = rss.getString("scaffolding_cell_id");
   
                     if(!mowner.equals(lastOwner)) {
                        if(matrix != null && !badCell) {
                           if(matrix.getOwner() == null)
                              logger.error("OSP Migration Error: The matrix owner agent couldn't be found: " + lastOwner);
                           else if(matrix.getOwner().getId() == null)
                              logger.error("OSP Migration Error: The matrix owner agent id couldn't be found: " + lastOwner);
                           else if(matrix.getOwner().getId().getValue() == null)
                              logger.error("OSP Migration Error: The matrix owner agent id value couldn't be found: " + lastOwner);
                           else
                              matrixManager.save(matrix);
                        }
   
                        lastOwner = mowner;
   
                        matrix = new Matrix();
   
                        matrix.setOwner(agentManager.getAgent(mowner));
                        matrix.setScaffolding(scaffolding);

                        badMatrix = matrix.getOwner() == null ||
                                  matrix.getOwner().getId() == null   ||
                                  matrix.getOwner().getId().getValue() == null;
                        badCell = false;
                     }
                     badCell = scaffolding_cell_id == null || badMatrix;
   
                     if(!badCell) {
                        ScaffoldingCell sCell = (ScaffoldingCell)scaffoldingCellMap.get(scaffolding_cell_id);
   
                        boolean isReady = status.equals(MatrixFunctionConstants.READY_STATUS);
                        Cell cell = new Cell();
                        cell.setNewId(mcid);
                        cell.getWizardPage().setNewId(idManager.createId());
                        cell.getWizardPage().setOwner(matrix.getOwner());
                        cell.setScaffoldingCell(sCell);
                        cell.setStatus(status);
   
                        Set attachments = new HashSet();
                        String cellAttachmentTable = getOldTableName("osp_cell_attachment");
                        sql = "select * from " + cellAttachmentTable + " where cell_id='" + mcidStr + "'";
                        ResultSet rsCellFiles = matrixInnerStmt.executeQuery(sql);
                        try {
                           while(rsCellFiles.next()) {
                              String attid = rsCellFiles.getString("id");
                              String artifact = rsCellFiles.getString("artifactId");
                              Attachment att = new Attachment();
                              att.setNewId(idManager.getId(attid));
                              att.setArtifactId(idManager.getId(artifact));
                              att.setWizardPage(cell.getWizardPage());
                              attachments.add(att);
                              if(!isReady)
                                 contentHosting.lockObject(artifact, 
                                    cell.getWizardPage().getNewId().getValue(), "cell atts locked on submit", true);
                           }
                           cell.setAttachments(attachments);
                        } finally {
                           rsCellFiles.close();
                        }
   
                        // get the intellectual growth
                        sql = "SELECT ID, GROWTHSTATEMENT FROM " + tableName + " WHERE CELL_ID='" + mcidStr + "'";
                        ResultSet intelGrowthRS = matrixInnerStmt.executeQuery(sql);
                        try {
                           if(intelGrowthRS.next()) {
                              String reflection_id = intelGrowthRS.getString(1);
                              String growth = intelGrowthRS.getString(2);
      
                              // save the intellectual growth
                              Integer anId = incUser(matrix.getOwner().getId().getValue());
                              Id reflectionForm = createReflectionForm(matrix.getOwner().getId().getValue(), 
                                    "IntGrowth " + scaffolding.getTitle() + 
                                    " " + sCell.getRootCriterion().getDescription() +
                                    " " + sCell.getLevel().getDescription() +
                                    " - " + anId.toString(), 
                                    growth);
                              Review review = reviewManager.createNew("", worksite);
                              review.setDeviceId(intelGrowthFormId.getValue());// form idvalue
                              review.setParent(cell.getWizardPage().getNewId().getValue());// wizard page
                              review.setType(Review.REFLECTION_TYPE);//contant
                              review.setReviewContent(reflectionForm);
                              getReviewManager().saveReview(review);
                              
                              if(!isReady)
                                 contentHosting.lockObject(reflectionForm.getValue(), 
                                       review.getId().getValue(), "reflection submitted", true);
      
                              sql = "SELECT CONNECTTEXT, EVIDENCE FROM " + tableName2 + " WHERE REFLECTION_ID='" + reflection_id + "' ORDER BY SEQ_NUM";
                              ResultSet reflectionRS = matrixInnerStmt.executeQuery(sql);
                              Set pageForms = new HashSet();
                              try {
                                 while(reflectionRS.next()) {
                                    String connect_text = reflectionRS.getString("CONNECTTEXT");
                                    String evidence = reflectionRS.getString("EVIDENCE");
         
                                    // save the expectation
                                    anId = incUser(matrix.getOwner().getId().getValue());
                                    Id expectationForm = createExpectationForm(matrix.getOwner().getId().getValue(), 
                                          "Reflection " + scaffolding.getTitle() + 
                                          " " + sCell.getRootCriterion().getDescription() +
                                          " " + sCell.getLevel().getDescription() +
                                          " - " + anId.toString(), 
                                          evidence, connect_text);
         
                                    WizardPageForm pageForm = new WizardPageForm();
         
                                    pageForm.setArtifactId(expectationForm);
                                    pageForm.setFormType(EXPECTATION_FORM_ID_VALUE);
                                    pageForm.setWizardPage(cell.getWizardPage());

                                    if(!isReady)
                                       contentHosting.lockObject(expectationForm.getValue(), 
                                             cell.getWizardPage().getNewId().getValue(), "expectation submitted", true);
         
                                    pageForms.add(pageForm);
                                 }
                                 cell.getWizardPage().setPageForms(pageForms);
                              } finally {
                                 reflectionRS.close();
                              }
                           }
                        } finally {
                           intelGrowthRS.close();
                        }
   
                        //
                        sql = "SELECT ID, REVIEWER_ID, COMMENTS, GRADE, STATUS, CREATED, MODIFIED FROM " + tableName3 + " WHERE CELL_ID='" + mcidStr + "'";
                        ResultSet evalRS = matrixInnerStmt.executeQuery(sql);
                        try {
                           while(evalRS.next()) {
                              String riid = evalRS.getString("ID");
                              String reviewer_id = evalRS.getString("REVIEWER_ID");
                              String comment = evalRS.getString("COMMENTS");
                              String grade = evalRS.getString("GRADE");
                              String ri_status = evalRS.getString("STATUS");
                              String ri_created = evalRS.getString("CREATED");
                              String ri_modified = evalRS.getString("MODIFIED");
      
                              // save the Reviews
                              //Skip if the reviewer is null
                              if (reviewer_id != null && !reviewer_id.equalsIgnoreCase("")) {
                                 Integer anId = incUser(reviewer_id);
                                 Id evaluationForm = createEvaluationForm(reviewer_id, 
                                       "Review "+ scaffolding.getTitle() + 
                                       " " + sCell.getRootCriterion().getDescription() +
                                       " " + sCell.getLevel().getDescription() +
                                       " - " + anId.toString(), 
                                       grade, comment);
      
                                 Review review = reviewManager.createNew("", worksite);
                                 review.setDeviceId(evaluationFormId.getValue());// form idvalue
                                 Id pageId = resolveId(cell.getWizardPage());
                                 review.setParent(pageId.getValue());// wizard page
                                 review.setType(Review.EVALUATION_TYPE);//contant
                                 review.setReviewContent(evaluationForm);
                                 review = getReviewManager().saveReview(review);
                                 
                                 contentHosting.lockObject(evaluationForm.getValue(), 
                                       review.getId().getValue(), "evaluation is once off", true);
                              }
                           }
                        } finally {
                           evalRS.close();
                        }
   
                        matrix.add(cell);
                     }
                  }  // end while(rss.next()) -- looping through each matrix
               } finally {
                  rss.close();
               }
               
               if(matrix != null && !badCell) {
                  if(matrix.getOwner() == null)
                     logger.error("OSP Migration Error: The matrix owner agent couldn't be found: " + lastOwner);
                  else if(matrix.getOwner().getId() == null)
                     logger.error("OSP Migration Error: The matrix owner agent id couldn't be found: " + lastOwner);
                  else if(matrix.getOwner().getId().getValue() == null)
                     logger.error("OSP Migration Error: The matrix owner agent id value couldn't be found: " + lastOwner);
                  else
                     matrixManager.save(matrix);
               }
               
            }  // end while(rs.next()) -- looping through each scaffolding
            
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
            innerStmt.close();
            matrixInnerStmt.close();
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
                  //boolean includeComments = rs.getBoolean("includeComments");
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
                  

                  if(template.getOwner() == null)
                     logger.error("OSP Migration Error: The template owner agent couldn't be found: " + owner);
                  else if(template.getOwner().getId() == null)
                     logger.error("OSP Migration Error: The template owner agent id couldn't be found: " + owner);
                  else if(template.getOwner().getId().getValue() == null)
                     logger.error("OSP Migration Error: The template owner agent id value couldn't be found: " + owner);
                  else
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

                  if(presentation.getOwner() == null) {
                     logger.error("OSP Migration Error: The presentation owner agent couldn't be found: " + owner);
                     continue;
                  } else if(presentation.getOwner().getId() == null) {
                     logger.error("OSP Migration Error: The presentation owner agent id couldn't be found: " + owner);
                     continue;
                  } else if(presentation.getOwner().getId().getValue() == null) {
                     logger.error("OSP Migration Error: The presentation owner agent id value couldn't be found: " + owner);
                     continue;
                  }
                                    
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
                  
                   //TODO template no longer has comments, replace line below
                  //presentation.setAllowComments(template.isIncludeComments());
                  
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
   
   private Integer incUser(String userId)
   {
      Integer accessTimes = (Integer)userUniquenessMap.get(userId);
      
      if(accessTimes == null)
         accessTimes = new Integer(1);
      else
         accessTimes = new Integer(accessTimes.intValue() + 1);
      userUniquenessMap.put(userId, accessTimes);
      return accessTimes;
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

   public List getMatrixForms() {
      return matrixForms;
   }

   public void setMatrixForms(List matrixForms) {
      this.matrixForms = matrixForms;
   }

   public SecurityService getSecurityService() {
      return securityService;
   }

   public void setSecurityService(SecurityService securityService) {
      this.securityService = securityService;
   }

   public ContentHostingService getContentHosting() {
      return contentHosting;
   }

   public void setContentHosting(ContentHostingService contentHosting) {
      this.contentHosting = contentHosting;
   }

   public GuidanceManager getGuidanceManager() {
      return guidanceManager;
   }

   public void setGuidanceManager(GuidanceManager guidanceManager) {
      this.guidanceManager = guidanceManager;
   }

   public ReviewManager getReviewManager() {
      return reviewManager;
   }

   public void setReviewManager(ReviewManager reviewManager) {
      this.reviewManager = reviewManager;
   }

   public DefaultScaffoldingBean getDefaultScaffoldingBean() {
      return defaultScaffoldingBean;
   }

   public void setDefaultScaffoldingBean(
         DefaultScaffoldingBean defaultScaffoldingBean) {
      this.defaultScaffoldingBean = defaultScaffoldingBean;
   }

   /**
    * @return Returns the workflowManager.
    */
   public WorkflowManager getWorkflowManager() {
      return workflowManager;
   }

   /**
    * @param workflowManager The workflowManager to set.
    */
   public void setWorkflowManager(WorkflowManager workflowManager) {
      this.workflowManager = workflowManager;
   }

}

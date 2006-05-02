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
package org.theospi.portfolio.presentation.model.impl;

import net.sf.hibernate.Hibernate;
import net.sf.hibernate.HibernateException;
import net.sf.hibernate.Query;
import net.sf.hibernate.Session;
import org.jdom.CDATA;
import org.jdom.Document;
import org.jdom.Element;
import org.sakaiproject.tool.cover.SessionManager;
import org.sakaiproject.tool.cover.ToolManager;
import org.sakaiproject.exception.*;
import org.sakaiproject.metaobj.security.AuthenticationManager;
import org.sakaiproject.metaobj.shared.ArtifactFinder;
import org.sakaiproject.metaobj.shared.ArtifactFinderManager;
import org.sakaiproject.metaobj.shared.DownloadableManager;
import org.sakaiproject.metaobj.shared.mgt.*;
import org.sakaiproject.metaobj.shared.mgt.home.StructuredArtifactHomeInterface;
import org.sakaiproject.metaobj.shared.model.*;
import org.sakaiproject.metaobj.worksite.mgt.WorksiteManager;

import org.sakaiproject.content.api.*;
import org.sakaiproject.entity.api.Reference;
import org.sakaiproject.entity.api.ResourceProperties;
import org.sakaiproject.entity.api.ResourcePropertiesEdit;
import org.sakaiproject.event.cover.NotificationService;
import org.sakaiproject.service.legacy.resource.DuplicatableToolService;
import org.sakaiproject.authz.api.SecurityService;
import org.sakaiproject.site.api.Site;
import org.sakaiproject.site.api.ToolConfiguration;
import org.sakaiproject.site.cover.SiteService;
import org.sakaiproject.user.api.User;
import org.sakaiproject.user.cover.UserDirectoryService;
import org.springframework.orm.hibernate.HibernateCallback;
import org.springframework.orm.hibernate.HibernateObjectRetrievalFailureException;
import org.springframework.orm.hibernate.support.HibernateDaoSupport;
import org.theospi.portfolio.presentation.CommentSortBy;
import org.theospi.portfolio.presentation.PresentationFunctionConstants;
import org.theospi.portfolio.presentation.PresentationManager;
import org.theospi.portfolio.presentation.export.PresentationExport;
import org.theospi.portfolio.presentation.model.*;
import org.theospi.portfolio.security.AllowMapSecurityAdvisor;
import org.theospi.portfolio.security.Authorization;
import org.theospi.portfolio.security.AuthorizationFacade;
import org.theospi.portfolio.security.AuthorizationFailedException;
import org.theospi.portfolio.security.impl.AllowAllSecurityAdvisor;
import org.theospi.portfolio.shared.intf.EntityContextFinder;
import org.theospi.portfolio.shared.model.ItemDefinitionMimeType;
import org.theospi.portfolio.shared.model.Node;
import org.theospi.portfolio.shared.model.OspException;
import org.theospi.portfolio.style.StyleConsumer;
import org.theospi.portfolio.style.model.Style;
import org.theospi.utils.zip.UncloseableZipInputStream;

import java.io.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;
import java.util.zip.*;

public class PresentationManagerImpl extends HibernateDaoSupport
   implements PresentationManager, DuplicatableToolService, DownloadableManager, StyleConsumer {

   private List definedLayouts;
   private AgentManager agentManager;
   private AuthorizationFacade authzManager = null;
   private AuthenticationManager authnManager = null;
   private IdManager idManager = null;
   private WritableObjectHome fileHome;
   private HomeFactory homeFactory = null;
   private WorksiteManager worksiteManager;
   private LockManager lockManager;
   private ArtifactFinderManager artifactFinderManager;
   private ContentHostingService contentHosting = null;
   private SecurityService securityService = null;
   private Map secretExportKeys = new Hashtable();
   private String tempPresDownloadDir;
   private StructuredArtifactDefinitionManager structuredArtifactDefinitionManager;
   private List globalSites;
   private List globalSiteTypes;
   private List initializedServices;

   private static final String TEMPLATE_ID_TAG = "templateId";
   private static final String PRESENTATION_ID_TAG = "presentationId";
   private static final String SYSTEM_COLLECTION_ID = "/system/";

   public PresentationTemplate storeTemplate(final PresentationTemplate template) {
      return storeTemplate(template, true);
   }

   protected PresentationTemplate storeTemplate(final PresentationTemplate template, boolean checkAuthz) {
      template.setModified(new Date(System.currentTimeMillis()));

      boolean newTemplate = (template.getId() == null);

      if (newTemplate || template.isNewObject()) {
         template.setCreated(new Date(System.currentTimeMillis()));

         if (checkAuthz) {
            getAuthzManager().checkPermission(PresentationFunctionConstants.CREATE_TEMPLATE,
               getIdManager().getId(template.getToolId()));
         }
      } else {
         deleteUnusedItemDefinition(template);
         if (checkAuthz) {
            getAuthzManager().checkPermission(PresentationFunctionConstants.EDIT_TEMPLATE,
                  template.getId());
         }
      }

      if (template.isNewObject()) {
         getHibernateTemplate().save(template, template.getId());
         template.setNewObject(false);
      }
      else {
         getHibernateTemplate().saveOrUpdateCopy(template);
      }

      lockTemplateFiles(template);

      return template;
   }

   /**
    * remove all the locks associated with this template
    */
   protected void clearLocks(Id id) {
      getLockManager().removeAllLocks(id.getValue());
   }


   /**
    * locks all the files associated with this template.
    * @param template
    */
   protected void lockTemplateFiles(PresentationTemplate template){
      clearLocks(template.getId());
      for (Iterator i = template.getFiles().iterator();i.hasNext();){
         TemplateFileRef fileRef = (TemplateFileRef) i.next();
         //getLockManager().addLock(fileRef.getFile().getId(), template.getId(), "saving a presentation template");
         getLockManager().lockObject(fileRef.getFileId(),
        		 template.getId().getValue(), "saving a presentation template", true);
      }
      //getLockManager().addLock(template.getRenderer(), template.getId(), "saving a presentation template");
      getLockManager().lockObject(template.getRenderer().getValue(),
    		  template.getId().getValue(), "saving a presentation template", true);

      if (template.getPropertyPage() != null) {
         getLockManager().lockObject(template.getPropertyPage().getValue(),
              template.getId().getValue(), "saving a presentation template", true);
      }
   }

   public PresentationTemplate getPresentationTemplate(final Id id) {
      HibernateCallback callback = new HibernateCallback() {

         public Object doInHibernate(Session session) throws HibernateException {
            PresentationTemplate template = (PresentationTemplate) session.load(PresentationTemplate.class, id);
            template.getItems().size(); //force load
            if (template.getOwner() == null){
                reassignOwner(template);
            }

            for (Iterator i = template.getItemDefinitions().iterator(); i.hasNext();) {
               PresentationItemDefinition itemDef = (PresentationItemDefinition) i.next();
//               if (itemDef.getHasMimeTypes()) {
                  itemDef.getMimeTypes().size();
//               }
            }

            return template;
         }

      };

      try {
         PresentationTemplate template = (PresentationTemplate) getHibernateTemplate().execute(callback);
         if (template.getOwner() == null){
                reassignOwner(template);
         }
         if (template.getPropertyPage() != null) {
            String propPage = getContentHosting().resolveUuid(template.getPropertyPage().getValue());
            getSecurityService().pushAdvisor(
               new AllowMapSecurityAdvisor(ContentHostingService.EVENT_RESOURCE_READ,
                     getContentHosting().getReference(propPage)));
         }
         return template;
      } catch (HibernateObjectRetrievalFailureException e) {
         logger.debug(e);
         return null;
      }
   }

    public void reassignOwner(PresentationTemplate templateId)
   {
          templateId.setOwner(getAgentManager().getAgent("admin"));
   }
   public Presentation getPresentation(final Id id, String secretExportKey) {
      Presentation pres = (Presentation)secretExportKeys.get(secretExportKey);
      if (pres == null || !id.equals(pres.getId())) {
         throw new AuthorizationFailedException("Exporting inappropriate presentation");
      }

      switchUser(pres.getOwner());

      return getPresentation(id);
   }

   protected void switchUser(Agent owner) {
      org.sakaiproject.tool.api.Session sakaiSession = SessionManager.getCurrentSession();
      sakaiSession.setUserId(owner.getId().getValue());
      sakaiSession.setUserEid(owner.getId().getValue());
   }

   public Presentation getLightweightPresentation(final Id id) {
      HibernateCallback callback = new HibernateCallback() {

         public Object doInHibernate(Session session) throws HibernateException, SQLException {
            Presentation pres = (Presentation) session.load(Presentation.class, id);
            return pres;
         }

      };

      try {
         Presentation presentation = (Presentation) getHibernateTemplate().execute(callback);

         return presentation;
      } catch (HibernateObjectRetrievalFailureException e) {
         logger.debug(e);
         return null;
      }
   }

   public Presentation getPresentation(final Id id) {

      HibernateCallback callback = new HibernateCallback() {

         public Object doInHibernate(Session session) throws HibernateException, SQLException {
            Presentation pres = (Presentation) session.load(Presentation.class, id);

            viewingPresentation(pres);

            //remove any artifacts that have been removed from the repository
            for (Iterator i= pres.getPresentationItems().iterator();i.hasNext();){
               PresentationItem item = (PresentationItem) i.next();
               ArtifactFinder artifactFinder = getArtifactFinderManager().getArtifactFinderByType(item.getDefinition().getType());
               if (artifactFinder.load(item.getArtifactId()) == null){
                  deleteArtifactReference(item.getArtifactId());
               }
            }
            pres.getTemplate().getItemDefinitions().size(); //force load

            return pres;
         }

      };

      try {
         Presentation presentation = (Presentation) getHibernateTemplate().execute(callback);

         if (!presentation.getIsPublic() &&
             !presentation.getOwner().equals(getAuthnManager().getAgent())) {
            getAuthzManager().checkPermission(PresentationFunctionConstants.VIEW_PRESENTATION, presentation.getId());
         }

         Collection viewerAuthzs = getAuthzManager().getAuthorizations(null,
            PresentationFunctionConstants.VIEW_PRESENTATION, presentation.getId());

         for (Iterator i = viewerAuthzs.iterator(); i.hasNext();) {
            Authorization viewer = (Authorization) i.next();
            presentation.getViewers().add(viewer.getAgent());
         }
         return presentation;
      } catch (HibernateObjectRetrievalFailureException e) {
         logger.debug(e);
         return null;
      }
   }

   protected boolean artifactExists(Id artifactId) {
      return (getNode(artifactId) != null);
   }

   public void deleteUnusedItemDefinition(PresentationTemplate template) {
      Set deletedItems = template.getDeletedItems();

      if (deletedItems == null) {
         return;
      }

      for (Iterator i=deletedItems.iterator();i.hasNext();) {
         PresentationItemDefinition item = (PresentationItemDefinition)i.next();
         if (item.getId() != null) {
            deleteUnusedItemDefinition(item.getId());
         }
      }
   }

   public void deleteUnusedItemDefinition(Id itemDefId) {
      Connection connection = null;
      PreparedStatement stmt = null;
      Session session = getSession();
      try {
         connection = session.connection();
         stmt = connection.prepareStatement("delete from osp_presentation_item " +
            " where osp_presentation_item.item_definition_id = ?");
         stmt.setString(1, itemDefId.getValue());
         stmt.execute();
      } catch (SQLException e) {
         logger.error("",e);
         throw new OspException(e);
      } catch (HibernateException e) {
         logger.error("",e);
         throw new OspException(e);
      } finally {
         try {
            stmt.close();
         } catch (Exception e) {
         }
      }
   }

   public boolean deletePresentationTemplate(final Id id) {
      PresentationTemplate template = getPresentationTemplate(id);
      getAuthzManager().checkPermission(PresentationFunctionConstants.DELETE_TEMPLATE, template.getId());
      clearLocks(template.getId());

      // first delete all presentations that use this template
      // this will delete all authorization as well
      Collection presentations = getHibernateTemplate().find("from Presentation where template_id=?", id.getValue(), Hibernate.STRING);
      for (Iterator i = presentations.iterator(); i.hasNext();) {
         //Presentation presentation = (Presentation) i.next();
         //deletePresentation(presentation.getId(), false);

         //Don't think we want to just delete presentations.
         // Let's return false instead.
         return false;
      }

      HibernateCallback callback = new HibernateCallback() {

         public Object doInHibernate(Session session) throws HibernateException, SQLException {
            session.delete("from PresentationTemplate where id=?", id.getValue(), Hibernate.STRING);
            return null;
         }

      };
      getHibernateTemplate().execute(callback);
      return true;

   }

   protected void deleteViewers(Id presId) {
      Collection authzs = getAuthzManager().getAuthorizations(null, PresentationFunctionConstants.VIEW_PRESENTATION, presId);

      for (Iterator i = authzs.iterator(); i.hasNext();) {
         Authorization authz = (Authorization) i.next();
         getAuthzManager().deleteAuthorization(authz.getAgent(),
            authz.getFunction(), authz.getQualifier());
      }
   }

   public void deletePresentation(final Id id) {
      deletePresentation(id, true);
   }

   public void deletePresentation(final Id id, boolean checkAuthz) {
      if (checkAuthz) {
         getAuthzManager().checkPermission(PresentationFunctionConstants.DELETE_PRESENTATION, id);
      }

      HibernateCallback callback = new HibernateCallback() {
         public Object doInHibernate(Session session) throws HibernateException, SQLException {
            Presentation presentation =
               (Presentation) session.load(Presentation.class, id);

            // delete viewer authz
            deleteViewers(presentation.getId());

            deleteComments(session, presentation);
            deleteLogs(session, presentation);
            deletePresentationPages(session, presentation);
            session.delete(presentation);
            return null;
         }
      };

      getHibernateTemplate().execute(callback);
   }

   protected void deleteLogs(Session session, Presentation presentation) throws HibernateException {
      session.delete("from PresentationLog where presentation_id=?",
         presentation.getId().getValue(), Hibernate.STRING);
   }

   protected void deleteComments(Session session, Presentation presentation) throws HibernateException {
      session.delete("from PresentationComment where presentation_id=?",
         presentation.getId().getValue(), Hibernate.STRING);
   }

   protected void deletePresentationPages(Session session, Presentation presentation) throws HibernateException {
      session.delete("from PresentationPage where presentation_id=?",
            presentation.getId().getValue(), Hibernate.STRING);
   }

   public PresentationItemDefinition getPresentationItemDefinition(final Id id) {
      HibernateCallback callback = new HibernateCallback() {

         public Object doInHibernate(Session session) throws HibernateException, SQLException {
            PresentationItemDefinition itemDef = (PresentationItemDefinition) session.load(PresentationItemDefinition.class, id);
            itemDef.getMimeTypes().size(); //force load
            return itemDef;
         }

      };

      try {
         return (PresentationItemDefinition) getHibernateTemplate().execute(callback);
      } catch (HibernateObjectRetrievalFailureException e) {
         logger.debug(e);
         return null;
      }
   }

   public void deletePresentationItemDefinition(Id id) {
      PresentationItemDefinition item = getPresentationItemDefinition(id);
      if (item == null) return;
      getHibernateTemplate().delete(item);
   }

   public PresentationItem getPresentationItem(Id id) {
      try {
         return (PresentationItem) getHibernateTemplate().load(PresentationItem.class, id);
      } catch (HibernateObjectRetrievalFailureException e) {
         logger.error("",e);
         throw new OspException(e);
      }
   }

   public void updateItemDefintion(PresentationItemDefinition itemDef) {
      getHibernateTemplate().saveOrUpdate(itemDef);
   }

   public void deletePresentationItem(Id id) {
      PresentationItem item = getPresentationItem(id);
      if (item == null) return;
      getHibernateTemplate().delete(item);
   }

   /**
    * saves or updates a presentation and any associated presentention_items.
    * This method does not persist the viewer list, for that use addViewer(), or deleteViewer()
    *
    * @param presentation
    * @return
    */
   public Presentation storePresentation(Presentation presentation) {

      presentation.setModified(new Date(System.currentTimeMillis()));
      presentation.setSiteId(ToolManager.getCurrentPlacement().getContext());
      setupPresItemDefinition(presentation);

      if (presentation.getOwner() == null) {
         presentation.setOwner(getAuthnManager().getAgent());
      }

      if (presentation.isNewObject()) {
         presentation.setCreated(new Date(System.currentTimeMillis()));
         getAuthzManager().checkPermission(PresentationFunctionConstants.CREATE_PRESENTATION,
            getIdManager().getId(ToolManager.getCurrentPlacement().getToolId()));
         getHibernateTemplate().save(presentation, presentation.getId());
      } else {
         getAuthzManager().checkPermission(PresentationFunctionConstants.EDIT_PRESENTATION,
            presentation.getId());
         getHibernateTemplate().saveOrUpdateCopy(presentation);
      }

      storePresentationPages(presentation.getPages(), presentation.getId());

      return presentation;
   }

   protected void setupPresItemDefinition(Presentation presentation) {
      if (presentation.getPresentationType().equals(Presentation.FREEFORM_TYPE)) {
         PresentationTemplate template = getPresentationTemplate(Presentation.FREEFORM_TEMPLATE_ID);
         presentation.setTemplate(template);
         PresentationItemDefinition itemDef =
               (PresentationItemDefinition) template.getItemDefinitions().iterator().next();
         for (Iterator i = presentation.getItems().iterator();i.hasNext();) {
            PresentationItem item = (PresentationItem) i.next();
            item.setDefinition(itemDef);
         }
      }
   }

   protected void storePresentationPages(List pages, Id presentationId) {
      if (pages == null) {
         return;
      }

      for (Iterator i=pages.iterator();i.hasNext();) {
         PresentationPage page = (PresentationPage) i.next();
         page.setModified(new Date(System.currentTimeMillis()));
         fixupRegions(page);
         if (page.isNewObject()) {
            page.setCreated(new Date(System.currentTimeMillis()));
            getHibernateTemplate().save(page, page.getId());
         }
         else {
            getHibernateTemplate().saveOrUpdateCopy(page);
         }
      }

      List allPages = getPresentationPagesByPresentation(presentationId);
      for (Iterator i=allPages.iterator();i.hasNext();) {
         PresentationPage page = (PresentationPage) i.next();
         if (!pages.contains(page)) {
            getHibernateTemplate().delete(page);
         }
      }
   }

   protected void fixupRegions(PresentationPage page) {
      for (Iterator i = page.getRegions().iterator();i.hasNext();) {
         PresentationPageRegion region = (PresentationPageRegion) i.next();
         region.setPage(page);
      }
   }      

   public Collection findPresentationsByOwner(Agent owner) {
      return getHibernateTemplate().find("from Presentation where owner_id=? Order by name",
         owner.getId().getValue());
   }

   public Collection findPresentationsByOwner(Agent owner, String toolId) {
      return getHibernateTemplate().find("from Presentation where owner_id=? and tool_id=? Order by name",
            new Object[]{owner.getId().getValue(), toolId});
   }

   public Collection findTemplatesByOwner(Agent owner, String siteId) {
      return getHibernateTemplate().find("from PresentationTemplate where owner_id=? and site_id=? Order by name",
            new Object[]{owner.getId().getValue(), siteId});
   }


   public Collection findTemplatesByOwner(Agent owner) {
      return getHibernateTemplate().find("from PresentationTemplate where owner_id=? Order by name", owner.getId().getValue());
   }

   public Collection findPublishedTemplates() {
      return getHibernateTemplate().find("from PresentationTemplate where published=? and owner_id!=? Order by name",
         new Object[]{new Boolean(true), getAuthnManager().getAgent().getId().getValue()});
   }

   public Collection findPublishedTemplates(String siteId) {
      return getHibernateTemplate().find(
         "from PresentationTemplate where published=? and owner_id!=? and site_id=? Order by name",
         new Object[]{new Boolean(true), getAuthnManager().getAgent().getId().getValue(), siteId});
   }

   public Collection findPresentationsByViewer(Agent viewer) {

      Collection presentationAuthzs = getAuthzManager().getAuthorizations(viewer,
         PresentationFunctionConstants.VIEW_PRESENTATION, null);

      Collection returned = new ArrayList(findPresentationsByOwner(viewer));

      for (Iterator i=returned.iterator();i.hasNext();) {
         getHibernateTemplate().evict(i.next());
      }

      for (Iterator i = presentationAuthzs.iterator(); i.hasNext();) {
         Id presId = ((Authorization) i.next()).getQualifier();
         Presentation pres = getLightweightPresentation(presId);

        if (!returned.contains(pres) && !pres.isExpired() && (pres.getOwner() != null)) {
                getHibernateTemplate().evict(pres);
                returned.add(pres);
         }
         else{
            getHibernateTemplate().evict(pres);
        }
      }

      return returned;
   }

   public Collection findPresentationsByViewer(Agent viewer, String toolId) {

      Collection presentationAuthzs = getAuthzManager().getAuthorizations(viewer,
            PresentationFunctionConstants.VIEW_PRESENTATION,  null);

      Collection returned = findPresentationsByOwner(viewer, toolId);

      for (Iterator i=returned.iterator();i.hasNext();) {
         Presentation pres = (Presentation)i.next();
         pres.setAuthz(new PresentationAuthzMap(viewer, pres));
      }

      String query = "from Presentation where tool_id=? and id in (" +
         buildPresList(presentationAuthzs) + ")";

      Collection authzPres = getHibernateTemplate().find(query,
            new Object[]{toolId});

      for (Iterator i=authzPres.iterator();i.hasNext();) {
         Presentation pres = (Presentation)i.next();

         if (!returned.contains(pres) && !pres.isExpired() && (pres.getOwner() != null)) {
            pres.setAuthz(new PresentationAuthzMap(viewer, pres));
            returned.add(pres);
         }
          else{
             getHibernateTemplate().evict(pres);
         }
      }

      return returned;
   }

   protected String buildPresList(Collection presentationAuthzs) {
      String presIdList = "";

      for (Iterator i=presentationAuthzs.iterator();i.hasNext();) {
         Authorization authz = (Authorization)i.next();
         presIdList += "'" + authz.getQualifier() + "',";
      }

      presIdList += "'last'";

      return presIdList;
   }

   public void createComment(PresentationComment comment) {
      getAuthzManager().checkPermission(PresentationFunctionConstants.COMMENT_PRESENTATION,
         comment.getPresentationId());

      comment.setCreated(new Date(System.currentTimeMillis()));
      comment.setCreator(getAuthnManager().getAgent());

      getHibernateTemplate().save(comment);
   }

   public List getPresentationComments(Id presentationId, Agent viewer) {
      Session session = getSession();

      Query query = session.createSQLQuery("SELECT {osp_presentation_comment.*} " +
         " FROM osp_presentation_comment {osp_presentation_comment}, osp_presentation p " +
         " WHERE {osp_presentation_comment}.presentation_id = p.id and p.id = :presentationId and" +
         " (visibility = " + PresentationComment.VISABILITY_PUBLIC + " or " +
         "   (visibility = " + PresentationComment.VISABILITY_SHARED + " and " +
         "    p.owner_id = :viewerId) or " +
         " creator_id = :viewerId)" +
         " ORDER BY {osp_presentation_comment}.created",
         "osp_presentation_comment",
         PresentationComment.class);

      query.setString("presentationId", presentationId.getValue());
      query.setString("viewerId", viewer.getId().getValue());

      try {
         return query.list();
      } catch (HibernateException e) {
         logger.error("",e);
         throw new OspException(e);
      } finally {
         /*
         try {
             session.close();
         } catch (HibernateException e) {
            logger.error("",e);
         }
         */
      }
   }

   public PresentationComment getPresentationComment(Id id) {
      try {
         return (PresentationComment) getHibernateTemplate().load(PresentationComment.class, id);
      } catch (HibernateObjectRetrievalFailureException e) {
         logger.error("",e);
         throw new OspException(e);
      }
   }

   public void deletePresentationComment(PresentationComment comment) {
      getHibernateTemplate().delete(comment);
   }

   public void updatePresentationComment(PresentationComment comment) {
      getHibernateTemplate().saveOrUpdate(comment);
   }

   public List getOwnerComments(Agent owner, CommentSortBy sortBy) {
      return  getOwnerComments(owner, sortBy, false);
   }

   public List getOwnerComments(Agent owner, CommentSortBy sortBy, boolean excludeOwner) {
      String orderBy = sortBy.getSortByColumn();

      if (orderBy.startsWith("owner_id") || orderBy.startsWith("name")) {
         orderBy = "p." + orderBy;
      } else {
         orderBy = "{osp_presentation_comment}." + orderBy;
      }

      Session session = getSession();

      String includeOwnerCondition = "";
      if (!excludeOwner) {
         includeOwnerCondition = " or creator_id = :ownerId ) ";
      } else {
         includeOwnerCondition = " ) and ( creator_id != :ownerId )";
      }

      Query query = session.createSQLQuery("SELECT {osp_presentation_comment.*} " +
         " FROM osp_presentation_comment {osp_presentation_comment}, osp_presentation p " +
         " WHERE {osp_presentation_comment}.presentation_id = p.id and " +
         " (visibility = " + PresentationComment.VISABILITY_PUBLIC + " or " +
         "  visibility = " + PresentationComment.VISABILITY_SHARED +
         includeOwnerCondition + " and " +
         "  p.owner_id = :ownerId " +
         " ORDER BY " + orderBy + " " + sortBy.getDirection(),
         "osp_presentation_comment",
         PresentationComment.class);

      query.setString("ownerId", owner.getId().getValue());

      try {
         return query.list();
      } catch (HibernateException e) {
         logger.error("",e);
         throw new OspException(e);
      } finally {
         /*
         try {
            // session.close();
         } catch (HibernateException e) {
            logger.error("",e);
         }
         */
      }
   }

   public List getOwnerComments(Agent owner, String toolId, CommentSortBy sortBy) {
      return  getOwnerComments(owner, toolId, sortBy, false);
   }

   public List getOwnerComments(Agent owner, String toolId, CommentSortBy sortBy, boolean excludeOwner) {
      String orderBy = sortBy.getSortByColumn();

      if (orderBy.startsWith("owner_id") || orderBy.startsWith("name")) {
         orderBy = "p." + orderBy;
      } else {
         orderBy = "{osp_presentation_comment}." + orderBy;
      }

      Session session = getSession();
      String includeOwnerCondition = "";
      if (!excludeOwner) {
         includeOwnerCondition = " or creator_id = :ownerId ) ";
      } else {
         includeOwnerCondition = " ) and ( creator_id != :ownerId )";
      }

      Query query = session.createSQLQuery("SELECT {osp_presentation_comment.*} " +
         " FROM osp_presentation_comment {osp_presentation_comment}, osp_presentation p " +
         " WHERE {osp_presentation_comment}.presentation_id = p.id and " +
         " p.tool_id = :toolId and " +
         " (visibility = " + PresentationComment.VISABILITY_PUBLIC + " or " +
         "  visibility = " + PresentationComment.VISABILITY_SHARED +
         includeOwnerCondition + " and " +
         "  p.owner_id = :ownerId " +
         " ORDER BY " + orderBy + " " + sortBy.getDirection(),
         "osp_presentation_comment",
         PresentationComment.class);

      query.setString("toolId", toolId);
      query.setString("ownerId", owner.getId().getValue());

      try {
         return query.list();
      } catch (HibernateException e) {
         logger.error("",e);
         throw new OspException(e);
      } /* finally {
         try {
            // session.close();
         } catch (HibernateException e) {
            logger.error("",e);
         }
      }        */
   }


   public List getCreatorComments(Agent creator, CommentSortBy sortBy) {
      String orderBy = sortBy.getSortByColumn();

      if (orderBy.startsWith("owner_id") || orderBy.startsWith("name")) {
         orderBy = "p." + orderBy;
      } else {
         orderBy = "{osp_presentation_comment}." + orderBy;
      }

      String queryString = "SELECT {osp_presentation_comment.*} " +
         " FROM osp_presentation_comment {osp_presentation_comment}, osp_presentation p " +
         " WHERE {osp_presentation_comment}.presentation_id = p.id and " +
         " creator_id = :creatorId" +
         " ORDER BY " + orderBy + " " + sortBy.getDirection();

      Session session = getSession();

      Query query = session.createSQLQuery(queryString,
         "osp_presentation_comment",
         PresentationComment.class);

      query.setString("creatorId", creator.getId().getValue());

      try {
         return query.list();
      } catch (HibernateException e) {
         logger.error("",e);
         throw new OspException(e);
      } /* finally {
         try {
            // session.close();
         } catch (HibernateException e) {
            logger.error("",e);
         }
      }    */
   }

   public List getCreatorComments(Agent creator, String toolId, CommentSortBy sortBy) {
      String orderBy = sortBy.getSortByColumn();

      if (orderBy.startsWith("owner_id") || orderBy.startsWith("name")) {
         orderBy = "p." + orderBy;
      } else {
         orderBy = "{osp_presentation_comment}." + orderBy;
      }

      String queryString = "SELECT {osp_presentation_comment.*} " +
         " FROM osp_presentation_comment {osp_presentation_comment}, osp_presentation p " +
         " WHERE {osp_presentation_comment}.presentation_id = p.id and " +
         " tool_id = :toolId and" +
         " creator_id = :creatorId" +
         " ORDER BY " + orderBy + " " + sortBy.getDirection();

      Session session = getSession();

      Query query = session.createSQLQuery(queryString,
         "osp_presentation_comment",
         PresentationComment.class);

      query.setString("toolId", toolId);
      query.setString("creatorId", creator.getId().getValue());

      try {
         return query.list();
      } catch (HibernateException e) {
         logger.error("",e);
         throw new OspException(e);
      } /* finally {
         try {
            // session.close();
         } catch (HibernateException e) {
            logger.error("",e);
         }
      }  */
   }

   public boolean isGlobal() {
      String siteId = getWorksiteManager().getCurrentWorksiteId().getValue();

      if (getGlobalSites().contains(siteId)) {
         return true;
      }

      Site site = getWorksiteManager().getSite(siteId);
      if (site.getType() != null && getGlobalSiteTypes().contains(site.getType())) {
         return true;
      }

      return false;
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


   public Collection getPresentationItems(Id artifactId) {
      Session session = getSession();

      Query query = session.createSQLQuery("SELECT {osp_presentation.*} " +
         " FROM osp_presentation {osp_presentation}, osp_presentation_item pi " +
         " WHERE {osp_presentation}.id = pi.presentation_id and pi.artifact_id = :artifactId",
         "osp_presentation",
         Presentation.class);

      query.setString("artifactId", artifactId.getValue());

      try {
         return query.list();
      } catch (HibernateException e) {
         logger.error("",e);
         throw new OspException(e);
      } /* finally {
         try {
            // session.close();
         } catch (HibernateException e) {
            logger.error("",e);
         }
      }    */
   }

   public Collection getPresentationsBasedOnTemplateFileRef(Id artifactId) {
      Session session = getSession();

      try {
         Query query = session.createSQLQuery("SELECT {osp_presentation.*} " +
            " FROM osp_presentation {osp_presentation}, osp_template_file_ref tfr" +
            " WHERE {osp_presentation}.template_id = tfr.template_id and tfr.file_id = :artifactId",
            "osp_presentation",
            Presentation.class);

         query.setString("artifactId", artifactId.getValue());

         Collection tfr = query.list();
         query = session.createSQLQuery("SELECT {osp_presentation.*} " +
            " FROM osp_presentation {osp_presentation}, osp_presentation_template templ " +
            " WHERE {osp_presentation}.template_id = templ.id and (templ.renderer = :artifactId " +
            "       or templ.propertyPage = :artifactId)",
            "osp_presentation",
            Presentation.class);
         query.setString("artifactId", artifactId.getValue());
         tfr.addAll(query.list());
         return tfr;
      } catch (HibernateException e) {
         logger.error("",e);
         throw new OspException(e);
      } /* finally {
         try {
            // session.close();
         } catch (HibernateException e) {
            logger.error("",e);
         }
      }    */
   }

   public Collection findPresentationsByTool(Id id) {
      return getHibernateTemplate().find("from Presentation where tool_id=?", id.getValue());
   }

   public void deleteArtifactReference(Id artifactId) {
      // I can't figure out how to write a hibernate query to do this
      // I'm not sure how to directly access composite-elements, maybe you can't ?
      Connection connection = null;
      PreparedStatement stmt = null;
      Session session = getSession();
      try {
         connection = session.connection();
         stmt = connection.prepareStatement("delete from osp_presentation_item where artifact_id = ?");
         stmt.setString(1, artifactId.getValue());
         stmt.execute();
      } catch (SQLException e) {
         logger.error("",e);
         throw new OspException(e);
      } catch (HibernateException e) {
         logger.error("",e);
         throw new OspException(e);
      } finally {
         try {
            stmt.close();
         } catch (Exception e) {
         }
      }
   }

   public PresentationTemplate copyTemplate(Id templateId) {
      return copyTemplate(templateId,
         getWorksiteManager().getTool(ToolManager.getCurrentPlacement().getToolId()), true, true);
   }

   public void packageTemplateForExport(Id templateId, OutputStream os) throws IOException {
      getAuthzManager().checkPermission(PresentationFunctionConstants.CREATE_TEMPLATE,
         getIdManager().getId(ToolManager.getCurrentPlacement().getToolId()));
      packageTemplateForExportInternal(templateId, os);
   }

   protected void packageTemplateForExportInternal(Id templateId, OutputStream os) throws IOException {
      PresentationTemplate oldTemplate = this.getPresentationTemplate(templateId);
      Set items = oldTemplate.getItems();
      Set files = oldTemplate.getFiles();

      CheckedOutputStream checksum = new CheckedOutputStream(os, new Adler32());
      ZipOutputStream zos = new ZipOutputStream(new BufferedOutputStream(checksum));

      storeTemplateFile(zos, oldTemplate.getRenderer());
      storeTemplateFile(zos, oldTemplate.getPropertyPage());

      // go through each associated file... store them...
      if (files != null) {
         for (Iterator i=files.iterator();i.hasNext();) {
            TemplateFileRef fileRef = (TemplateFileRef)i.next();
            storeTemplateFile(zos, getIdManager().getId(fileRef.getFileId()));
         }
         oldTemplate.setFiles(new HashSet(files));
      }

      Collection exportedFormIds = new ArrayList();

      if (items != null) {
         oldTemplate.setItems(new HashSet(items));
         for (Iterator i=oldTemplate.getItems().iterator();i.hasNext();) {
            PresentationItemDefinition item = (PresentationItemDefinition)i.next();

            ReadableObjectHome home =
               (ReadableObjectHome)getHomeFactory().getHome(item.getType());

            if (home != null) {
               item.setExternalType(home.getExternalType());
            }

            if (home instanceof StructuredArtifactHomeInterface &&
                  !exportedFormIds.contains(home.getType().getId().getValue())) {
               // need to store the form
               storeFormInZip(zos, home);
               exportedFormIds.add(home.getType().getId().getValue());
            }

            if (item.getMimeTypes() != null) {
               item.setMimeTypes(new HashSet(item.getMimeTypes()));
            }
         }
      }

      ByteArrayOutputStream bos = new ByteArrayOutputStream();
      ObjectOutputStream oos = new ObjectOutputStream(bos);
      oos.writeObject(oldTemplate);

      storeFileInZip(zos, new ByteArrayInputStream(bos.toByteArray()),
         "template");

      oldTemplate.setFiles(files);
      oldTemplate.setItems(items);

      bos.close();

      zos.finish();
      zos.flush();
   }

   protected void storeFormInZip(ZipOutputStream zos, ReadableObjectHome home) throws IOException {

      ZipEntry newfileEntry = new ZipEntry("forms/" + home.getType().getId().getValue() + ".form");

      zos.putNextEntry(newfileEntry);

      getStructuredArtifactDefinitionManager().packageFormForExport(home.getType().getId().getValue(), zos, false);

      zos.closeEntry();
   }

   public PresentationTemplate uploadTemplate(String templateFileName, String toolId,
                                              InputStream zipFileStream) throws IOException {
      try {
         return uploadTemplate(templateFileName, getWorksiteManager().getTool(toolId), zipFileStream, true);
      }
      catch (InvalidUploadException exp) {
         throw exp;
      }
      catch (Exception exp) {
         throw new InvalidUploadException("Invalid template file.", exp, "uploadedTemplate");
      }
   }

   protected PresentationTemplate uploadTemplate(String templateFileName, ToolConfiguration toolConfiguration,
                                              InputStream zipFileStream, boolean checkAuthz) throws IOException {

      if (checkAuthz) {
         getAuthzManager().checkPermission(PresentationFunctionConstants.CREATE_TEMPLATE,
            getIdManager().getId(toolConfiguration.getId()));
      }

      ZipInputStream zis = new UncloseableZipInputStream(zipFileStream);

      ZipEntry currentEntry = zis.getNextEntry();
      Hashtable fileMap = new Hashtable();
      Hashtable formMap = new Hashtable();
      PresentationTemplate template = null;

      String tempDirName = getIdManager().createId().getValue();

      boolean itWorked = false;

      try {
         ContentCollectionEdit fileParent = getTemplateFileDir(tempDirName);
         boolean gotFile = false;
         
         while (currentEntry != null) {
            logger.debug("current entry name: " + currentEntry.getName());

            if (currentEntry.getName().equals("template")) {
               try {
                  template = processTemplate(zis);
               } catch (ClassNotFoundException e) {
                  logger.error("Class not found loading template", e);
                  throw new OspException(e);
               }
            }
            else if (!currentEntry.isDirectory()) {
               if (currentEntry.getName().startsWith("forms/")) {
                  processTemplateForm(currentEntry, zis, formMap,
                        getIdManager().getId(toolConfiguration.getSiteId()));
               }
               else {
                  gotFile = true;
                  processTemplateFile(currentEntry, zis, fileMap, fileParent);
               }
            }

            zis.closeEntry();
            currentEntry = zis.getNextEntry();
         }

         if (template == null) {
            throw new InvalidUploadException("Template zip must contain template definition", "uploadedTemplate");
         }
         
         if (gotFile) {
            fileParent.getPropertiesEdit().addProperty(
                  ResourceProperties.PROP_DISPLAY_NAME, template.getName());
            getContentHosting().commitCollection(fileParent);
         }
         else {
            getContentHosting().cancelCollection(fileParent);
         }

         template.setId(null);
         template.setOwner(getAuthnManager().getAgent());
         template.setRenderer((Id)fileMap.get(template.getRenderer()));
         template.setToolId(toolConfiguration.getId());
         template.setSiteId(toolConfiguration.getSiteId());

         if (template.getPropertyPage() != null) {
            template.setPropertyPage((Id)fileMap.get(template.getPropertyPage()));
         }

         for (Iterator i=template.getFiles().iterator();i.hasNext();) {
            TemplateFileRef ref = (TemplateFileRef)i.next();
            Id refFileId = getIdManager().getId(ref.getFileId());
            ref.setFileId(((Id)fileMap.get(refFileId)).getValue());
            ref.setPresentationTemplate(template);
         }

         int index = 100;
         for (Iterator i=template.getItems().iterator();i.hasNext();) {
            PresentationItemDefinition item = (PresentationItemDefinition)i.next();

            if (item.getSequence() == 0) {
               item.setSequence(index);
            }
            index++;

            if (formMap.containsKey(item.getType())) {
               item.setType((String) formMap.get(item.getType()));
            }

            item.setId(null);
            item.setPresentationTemplate(template);
         }
         template.orderItemDefs();

         substituteIds(fileMap);

         storeTemplate(template, checkAuthz);
         //TODO: 20050810 ContentHosting
         //fileParent.persistent().rename(getUniqueTemplateName(fileParent, template.getName()));
         itWorked = true;
         return template;
      } catch (Exception exp) {
         throw new RuntimeException(exp);
      }
      finally {
         //if (!itWorked) {
         //   fileParent.persistent().destroy();
         //}
               
         try {
            zis.closeEntry();
         }
         catch (IOException e) {
            logger.error("", e);
         }
      }
   }

// TODO: 20050810 ContentHosting
   /*
   protected String getUniqueTemplateName(Node currentNode, String name) {
      Node parent = currentNode.getParent();
      String newName = name;
      int count = 1;

      while (parent.hasChild(newName)) {
         count++;
         newName = name + "_" + count;
      }

      return newName;
   }
*/
   protected void processTemplateForm(ZipEntry currentEntry, ZipInputStream zis, Map formMap, Id worksite)
         throws IOException {
      File file = new File(currentEntry.getName());
      String fileName = file.getName();
      String oldId = fileName.substring(0, fileName.indexOf(".form"));

      StructuredArtifactDefinitionBean bean;
      try {
         //we want the bean even if it exists already
         bean = getStructuredArtifactDefinitionManager().importSad(
               worksite, zis, true, true, false);
      } catch(ImportException ie) {
         throw new RuntimeException("the structured artifact failed to import", ie);
      }

      formMap.put(oldId, bean.getId().getValue());
   }

   protected void processTemplateFile(ZipEntry currentEntry, ZipInputStream zis,
                                      Hashtable fileMap, ContentCollection fileParent) throws IOException {

      File file = new File(currentEntry.getName());

      MimeType mimeType = new MimeType(file.getParentFile().getParentFile().getParent(),
         file.getParentFile().getParentFile().getName());

      String contentType = mimeType.getValue();

      Id oldId = getIdManager().getId(file.getParentFile().getName());

      try {
         ByteArrayOutputStream bos = new ByteArrayOutputStream();
         int c = zis.read();
   
         while (c != -1) {
            bos.write(c);
            c = zis.read();
         }
         
         String fileId = fileParent.getId() + file.getName();
         ContentResourceEdit resource = getContentHosting().addResource(fileId);
         ResourcePropertiesEdit resourceProperties = resource.getPropertiesEdit();
         resourceProperties.addProperty (ResourceProperties.PROP_DISPLAY_NAME, file.getName());
         resource.setContent(bos.toByteArray());
         resource.setContentType(contentType);
         getContentHosting().commitResource(resource);
         
         Id newId = getIdManager().getId(getContentHosting().getUuid(resource.getId()));
         fileMap.put(oldId, newId);
      }
      catch (Exception exp) {
         throw new RuntimeException(exp);
      }
   }

   protected PresentationTemplate processTemplate(ZipInputStream zis) throws IOException, ClassNotFoundException {
      ObjectInputStream oos = new ObjectInputStream(zis);

      return (PresentationTemplate)oos.readObject();
   }

   protected void storeTemplateFile(ZipOutputStream zos, Id fileId) throws IOException {
      if (fileId == null) {
         return;
      }
      Node oldNode = getNode(fileId);
      String newName = oldNode.getName();
      //if (newName.lastIndexOf('\\') != -1) {
         String cleanedName = newName.substring(newName.lastIndexOf('\\')+1);
      //}
      storeFileInZip(zos, oldNode.getInputStream(),
            oldNode.getMimeType().getValue() + File.separator +
            fileId.getValue() + File.separator + cleanedName);
   }

   protected void storeFileInZip(ZipOutputStream zos, InputStream in, String entryName)
      throws IOException {

      byte data[] = new byte[1024 * 10];

      if (File.separatorChar == '\\') {
         entryName = entryName.replace('\\', '/');
      }

      ZipEntry newfileEntry = new ZipEntry(entryName);

      zos.putNextEntry(newfileEntry);

      BufferedInputStream origin = new BufferedInputStream(in, data.length);

      int count;
      while ((count = origin.read(data, 0, data.length)) != -1) {
         zos.write(data, 0, count);
      }
      zos.closeEntry();
      in.close();
   }

   protected WritableObjectHome getFileHome() {
      return fileHome;
   }

   protected void substituteIds(Hashtable fileMap) {
      // go through each file....  all text mime types, do the subst on
	      
      for (Iterator i=fileMap.values().iterator();i.hasNext();) {
         Node node = getNode((Id)i.next());

         if (node.getMimeType().getPrimaryType().equals("text")) {
            try {
               processFile(node, fileMap);
            } catch (IOException e) {
               logger.error("error processing file.", e);
               throw new OspException(e);
            }
         }
      }
      

   }
   
   protected void processFile(Node node, Hashtable fileMap) throws IOException {
      // read file into StringBuffer
      InputStream is = null;
      try {
         is = node.getInputStream();

         byte[] buffer = new byte[1024 * 10];

         StringBuffer sb = new StringBuffer();

         int read = is.read(buffer);

         while (read != -1) {
            sb.append(new String(buffer, 0, read));
            read = is.read(buffer);
         }

         is.close();
         is = null;

         boolean changed = false;

         // subst.
         for (Iterator i=fileMap.keySet().iterator();i.hasNext();) {
            Id key = (Id)i.next();

            if (substituteFileId(sb, key, (Id)fileMap.get(key))) {
               changed = true;
            }
         }

         if (changed) {
            // write StringBuffer out
            ContentResourceEdit cre = (ContentResourceEdit)node.getResource();
            cre.setContent(sb.toString().getBytes());
            getContentHosting().commitResource(cre);
         }
      } catch (OverQuotaException e) {
         // TODO Better error message here?
         logger.error("", e);
      }
      catch (ServerOverloadException e) {
         // TODO Better error message here?
         logger.error("", e);
      } finally {
         try {
            if (is != null) is.close();
         } catch (Exception e){
            logger.warn("",e);
         }
      }
   }

   protected boolean substituteFileId(StringBuffer sb, Id oldId, Id newId) {
      int index = sb.indexOf(oldId.getValue());
      boolean changed = false;
      while (index != -1) {
         sb.replace(index, index + oldId.getValue().length(), newId.getValue());
         changed = true;
         index = sb.indexOf(oldId.getValue());
      }

      return changed;
   }

   protected void handleChildren(PresentationTemplate oldTemplate, ContentCollection templateParent, Hashtable fileMap) {
      Set files = oldTemplate.getFiles();
      oldTemplate.setFiles(new HashSet());
      for (Iterator i=files.iterator();i.hasNext();) {
         TemplateFileRef fileRef = (TemplateFileRef)i.next();

         fileRef.setId(null);
         fileRef.setFileId(
               copyTemplateFile(templateParent, 
                     getIdManager().getId(fileRef.getFileId()), fileMap).getValue());
         oldTemplate.getFiles().add(fileRef);
      }

      Set items = oldTemplate.getItems();
      oldTemplate.setItems(new HashSet());
      for (Iterator i=items.iterator();i.hasNext();) {
         PresentationItemDefinition itemDef = (PresentationItemDefinition)i.next();
         itemDef.setId(null);
         Set itemMimeTypes = new HashSet();

         for (Iterator j=itemDef.getMimeTypes().iterator();j.hasNext();) {
            ItemDefinitionMimeType mimeType = (ItemDefinitionMimeType)j.next();

            itemMimeTypes.add(
               new ItemDefinitionMimeType(mimeType.getPrimary(), mimeType.getSecondary()));
         }

         itemDef.setMimeTypes(itemMimeTypes);
         oldTemplate.getItems().add(itemDef);
      }
   }
   
   protected ContentCollection getUserCollection() throws TypeException, IdUnusedException, PermissionException {
      User user = UserDirectoryService.getCurrentUser();
      String userId = user.getId();
      String wsId = SiteService.getUserSiteId(userId);
      String wsCollectionId = getContentHosting().getSiteCollection(wsId);
      ContentCollection collection = getContentHosting().getCollection(wsCollectionId);
      return collection;
   }

   protected ContentCollectionEdit getTemplateFileDir(String origName) throws TypeException, IdUnusedException, PermissionException, IdUsedException, IdInvalidException, InconsistentException {
      ContentCollection collection = getUserCollection();
      String childId = collection.getId() + origName;
      return getContentHosting().addCollection(childId);
   }

   protected Id copyTemplateFile(ContentCollection templateParent, Id oldFileId, Hashtable fileMap) {
      if (oldFileId == null) {
         return null;
      }

      Node oldNode = (Node) getNode(oldFileId);

      String newName = oldNode.getName();
//    TODO: 20050810 ContentHosting
      /*
      int index = 1;
      while (templateParent.hasChild(newName)) {
         newName = "copy_" + index + "_" + oldNode.getName();
         index++;
      }

      RepositoryNode newNode = oldNode.copy(oldNode.getName(), templateParent.getId());

      fileMap.put(oldFileId, newNode.getId());

      return newNode.getId();
      */
      return null;
   }
   
   public Document createDocument(Presentation presentation) {
      // build up the document from objects...
      viewingPresentation(presentation);

      Collection items = presentation.getItems();

      Element root = new Element("ospiPresentation");

      for (Iterator i = items.iterator(); i.hasNext();) {
         PresentationItem item = (PresentationItem) i.next();
         Element itemElement = root.getChild(item.getDefinition().getName());

         if (itemElement == null) {
            itemElement = new Element(item.getDefinition().getName());
            root.addContent(itemElement);
         }
         
         Artifact art = getPresentationItem(item.getDefinition().getType(), item.getArtifactId(), presentation);

         if (art != null && art.getHome() instanceof PresentableObjectHome) {
            PresentableObjectHome home = (PresentableObjectHome) art.getHome();
            Element node = home.getArtifactAsXml(art);
            node.setName("artifact");
            itemElement.addContent(node);
         }
      }

      if (presentation.getProperties() != null) {
         Element presProperties = new Element("presentationProperties");
         presProperties.addContent((Element) presentation.getProperties().currentElement().clone());
         root.addContent(presProperties);
      }

      if (presentation.getTemplate().getFiles() != null) {
         Element presFiles = new Element("presentationFiles");
         root.addContent(presFiles);

         for (Iterator files = presentation.getTemplate().getFiles().iterator(); files.hasNext(); ){
            TemplateFileRef fileRef = (TemplateFileRef) files.next();
            presFiles.addContent(getFileRefAsXml(presentation, fileRef));
         }
      }

      return new Document(root);
   }

   public Collection getAllPresentations() {
      return getHibernateTemplate().find("from Presentation");
   }

   public Collection getAllPresentationTemplates() {
      HibernateCallback callback = new HibernateCallback() {
         public Object doInHibernate(Session session) throws HibernateException, SQLException {
            List templates = getHibernateTemplate().find("from PresentationTemplate");
            for (Iterator i = templates.iterator();i.hasNext();) {
               PresentationTemplate template = (PresentationTemplate) i.next();
               for (Iterator j = template.getItems().iterator();j.hasNext();) {
                  PresentationItemDefinition itemDef = (PresentationItemDefinition) j.next();
                  itemDef.getMimeTypes().size();
               }
            }
            return templates;
         }
      };

      try {
         return (Collection) getHibernateTemplate().execute(callback);
      } catch (HibernateObjectRetrievalFailureException e) {
         logger.debug(e);
         return new ArrayList();
      }
   }

   public void viewingPresentation(Presentation presentation) {
      // go through and setup all pres and pres template files for read access
      List readableFiles = new ArrayList();
      Collection artifacts = presentation.getItems();

      for (Iterator i=artifacts.iterator();i.hasNext();) {
         PresentationItem item = (PresentationItem) i.next();
         String id = getContentHosting().resolveUuid(item.getArtifactId().getValue());
         if (id != null) {
            readableFiles.add(getContentHosting().getReference(id));
         }
      }

      if (presentation.getTemplate().getFiles() != null) {

         for (Iterator files = presentation.getTemplate().getFiles().iterator(); files.hasNext(); ){
            TemplateFileRef fileRef = (TemplateFileRef) files.next();
            String id = getContentHosting().resolveUuid(fileRef.getFileId());
            if (id != null) {
               readableFiles.add(getContentHosting().getReference(id));
            }
         }
      }

      String id = getContentHosting().resolveUuid(presentation.getTemplate().getRenderer().getValue());
      if (id != null) {
         readableFiles.add(getContentHosting().getReference(id));
      }
      
      //Files related to layouts
      List pages = getPresentationPagesByPresentation(presentation.getId());
      for (Iterator pagesIter = pages.iterator(); pagesIter.hasNext();) {
         PresentationPage page = (PresentationPage) pagesIter.next();
         String xhtmlFileId = getContentHosting().resolveUuid(page.getLayout().getXhtmlFileId().getValue());
         if (xhtmlFileId != null) {
            readableFiles.add(getContentHosting().getReference(xhtmlFileId));
         }
         if (page.getLayout().getPreviewImageId() != null) {
            String previewImageId = getContentHosting().resolveUuid(page.getLayout().getPreviewImageId().getValue());
            if (previewImageId != null) {
               readableFiles.add(getContentHosting().getReference(previewImageId));
            }
         }
         Style pageStyle = page.getStyle() != null ? page.getStyle() : page.getPresentation().getStyle();
         if (pageStyle != null && pageStyle.getStyleFile() != null) {
            String styleFileId = getContentHosting().resolveUuid(pageStyle.getStyleFile().getValue());
            readableFiles.add(getContentHosting().getReference(styleFileId));
         }
         
         for (Iterator regions = page.getRegions().iterator(); regions.hasNext();) {
            PresentationPageRegion region = (PresentationPageRegion) regions.next();
            for (Iterator items = region.getItems().iterator(); items.hasNext();) {
               PresentationPageItem pageItem = (PresentationPageItem) items.next();
               String itemId = getContentHosting().resolveUuid(pageItem.getValue());
               if (itemId != null) {
                  readableFiles.add(getContentHosting().getReference(itemId));
               }
            }
         }
      }
      

      getSecurityService().pushAdvisor(
         new AllowMapSecurityAdvisor(ContentHostingService.EVENT_RESOURCE_READ, readableFiles));
   }

   protected Element getFileRefAsXml(Presentation presentation, TemplateFileRef fileRef) {
      Element fileRefElement = new Element(fileRef.getUsage());
      String fileId = fileRef.getFileId();
            
      Artifact art = getPresentationItem(fileRef.getFileType(), 
            getIdManager().getId(fileId), presentation);

      PresentableObjectHome home = (PresentableObjectHome) art.getHome();
      fileRefElement.addContent(home.getArtifactAsXml(art));
      return fileRefElement;
   }

   public void storePresentationLog(PresentationLog log) {
      getHibernateTemplate().save(log);
   }

   public Collection findLogsByPresID(Id presID) {
      return getHibernateTemplate().find("from PresentationLog where presentation_id=? ORDER BY view_date DESC", presID.getValue());
   }

   public TemplateFileRef getTemplateFileRef(Id refId) {
      return (TemplateFileRef) getHibernateTemplate().load(TemplateFileRef.class,  refId);
   }

   public void updateTemplateFileRef(TemplateFileRef ref) {
      getHibernateTemplate().saveOrUpdate(ref);
   }

   public void deleteTemplateFileRef(Id refId) {
      getHibernateTemplate().delete(getTemplateFileRef(refId));
   }


   public AuthenticationManager getAuthnManager() {
      return authnManager;
   }

   public void setAuthnManager(AuthenticationManager authnManager) {
      this.authnManager = authnManager;
   }

   public IdManager getIdManager() {
      return idManager;
   }

   public void setIdManager(IdManager idManager) {
      this.idManager = idManager;
   }

   public void setFileHome(WritableObjectHome fileHome) {
      this.fileHome = fileHome;
   }

   public HomeFactory getHomeFactory() {
      return homeFactory;
   }

   public void setHomeFactory(HomeFactory homeFactory) {
      this.homeFactory = homeFactory;
   }

   public WorksiteManager getWorksiteManager() {
      return worksiteManager;
   }

   public void setWorksiteManager(WorksiteManager worksiteManager) {
      this.worksiteManager = worksiteManager;
   }

   public LockManager getLockManager() {
      return lockManager;
   }

   public void setLockManager(LockManager lockManager) {
      this.lockManager = lockManager;
   }

   public ArtifactFinderManager getArtifactFinderManager() {
      return artifactFinderManager;
   }

   public void setArtifactFinderManager(ArtifactFinderManager artifactFinderManager) {
      this.artifactFinderManager = artifactFinderManager;
   }


   public void importResources(ToolConfiguration fromTool, ToolConfiguration toTool, List resourceIds) {
      Agent agent = getAuthnManager().getAgent();
      Collection templates = findTemplatesByOwner(agent, fromTool.getSiteId());
      templates.addAll(findPublishedTemplates(fromTool.getSiteId()));

      for (Iterator i=templates.iterator();i.hasNext();) {
         PresentationTemplate template = (PresentationTemplate)i.next();
         copyTemplate(template.getId(), toTool, false, false);
      }
   }

   protected PresentationTemplate copyTemplate(Id templateId, ToolConfiguration toolConfiguration,
                                               boolean checkAuthz, boolean rename) {
      try {
         if (checkAuthz)
            getAuthzManager().checkPermission(PresentationFunctionConstants.COPY_TEMPLATE, templateId);

         ByteArrayOutputStream bos = new ByteArrayOutputStream();

         PresentationTemplate oldTemplate = this.getPresentationTemplate(templateId);

         packageTemplateForExportInternal(templateId, bos);

         ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());


         PresentationTemplate newTemplate = uploadTemplate(oldTemplate.getName() + ".zip",
            toolConfiguration, bis, false);

         if (rename) {
            newTemplate.setName(newTemplate.getName() + " Copy");
            storeTemplate(newTemplate, false);
         }
         return newTemplate;
      } catch (IOException e) {
         logger.error("", e);
         throw new OspException(e);
      }
   }

   public void packageForDownload(Map params, OutputStream out) throws IOException {

      if (params.get(TEMPLATE_ID_TAG) != null) {
         packageTemplateForExport(getIdManager().getId(((String[])params.get(TEMPLATE_ID_TAG))[0]),
            out);
      }
      else if (params.get(PRESENTATION_ID_TAG) != null) {
         packagePresentationForExport(getIdManager().getId(((String[])params.get(PRESENTATION_ID_TAG))[0]), out);
      }
   }

   protected void packagePresentationForExport(Id presentationId, OutputStream out) throws IOException {
      Presentation presentation = getLightweightPresentation(presentationId);

      if (!presentation.getOwner().equals(getAuthnManager().getAgent())) {
         throw new AuthorizationFailedException("Only the presentation owner can export a presentation");
      }

      File tempDir = new File(tempPresDownloadDir);
      if (!tempDir.exists()) {
         tempDir.mkdirs();
      }

      String secretExportKey = getIdManager().createId().getValue();
      String url = presentation.getExternalUri() + "&secretExportKey=" + secretExportKey;
      File tempDirectory = new File(tempDir, secretExportKey);

      PresentationExport export = new PresentationExport(
        url, tempDirectory.getPath());

      try {
         synchronized(secretExportKeys) {
            secretExportKeys.put(secretExportKey, presentation);
         }

         export.run();

         synchronized(secretExportKeys) {
            secretExportKeys.remove(secretExportKey);
         }

         export.createZip(out);
      }
      finally {
         export.deleteTemp();
      }
   }

   public Node getNode(Id artifactId) {
      String id = getContentHosting().resolveUuid(artifactId.getValue());
      if (id == null) {
         return null;
      }

      try {
         ContentResource resource = getContentHosting().getResource(id);
         String ownerId = resource.getProperties().getProperty(resource.getProperties().getNamePropCreator());
         Agent owner = getAgentManager().getAgent(getIdManager().getId(ownerId));
         return new Node(artifactId, resource, owner);
      }
      catch (PermissionException e) {
         logger.error("", e);
         throw new RuntimeException(e);
      }
      catch (IdUnusedException e) {
         logger.error("", e);
         throw new RuntimeException(e);
      }
      catch (TypeException e) {
         logger.error("", e);
         throw new RuntimeException(e);
      }
   }

   public Node getNode(Reference ref, Presentation presentation) {
     return getNode(getNode(ref), presentation);
   }

   public Node getNode(Id nodeId, Presentation presentation) {
      Node node = getNode(nodeId);
      return getNode(node, presentation);
   }

   public Node getNode(Id artifactId, PresentationLayout layout) {
      String id = getContentHosting().resolveUuid(artifactId.getValue());
      String ref = getContentHosting().getReference(id);
      getSecurityService().pushAdvisor(
            new AllowMapSecurityAdvisor(ContentHostingService.EVENT_RESOURCE_READ, ref));
      Node node = getNode(artifactId);
      return getNode(node, layout);
   }

   protected Node getNode(Node node, Presentation presentation) {
      if (node == null) {
         return null;
      }

      ContentResource wrapped = new ContentEntityWrapper(node.getResource(),
            buildRef(presentation.getSiteId(), presentation.getId().getValue(), node.getResource()));

      return new Node(node.getId(), wrapped, node.getTechnicalMetadata().getOwner());
   }

   protected Node getNode(Node node, PresentationLayout layout) {
      if (node == null) {
         return null;
      }
      String siteId = layout.getSiteId();
      if (siteId == null) {
         siteId = "~admin";
      }
      ContentResource wrapped = new ContentEntityWrapper(node.getResource(),
            buildLayoutRef(layout.getSiteId(), layout.getId().getValue(), node.getResource()));

      return new Node(node.getId(), wrapped, node.getTechnicalMetadata().getOwner());
   }

   protected String buildRef(String siteId, String contextId, ContentResource resource) {
      return ContentEntityUtil.getInstance().buildRef(
         PresentationContentEntityProducer.PRODUCER_NAME, siteId, contextId, resource.getReference());
   }

   protected String buildLayoutRef(String siteId, String contextId, ContentResource resource) {
      return ContentEntityUtil.getInstance().buildRef(
         LayoutEntityProducer.PRODUCER_NAME, siteId, contextId, resource.getReference());
   }

   public Node getNode(Reference ref) {
      String nodeId = getContentHosting().getUuid(ref.getId());

      return getNode(getIdManager().getId(nodeId));
   }

   public Collection loadArtifactsForItemDef(PresentationItemDefinition itemDef, Agent agent) {
      ArtifactFinder artifactFinder = getArtifactFinderManager().getArtifactFinderByType(itemDef.getType());
      // for performance, don't do a deep load, only load id, displayName
      artifactFinder.setLoadArtifacts(false);

      if (itemDef.getHasMimeTypes()) {
         Collection items = new ArrayList();
         if (itemDef.getMimeTypes().size() > 0) {
            for (Iterator i=itemDef.getMimeTypes().iterator();i.hasNext();) {
               ItemDefinitionMimeType mimeType = (ItemDefinitionMimeType)i.next();
               items.addAll(artifactFinder.findByOwnerAndType(agent.getId(), itemDef.getType(),
                  new MimeType(mimeType.getPrimary(), mimeType.getSecondary())));
            }
         }
         else {
            return artifactFinder.findByOwnerAndType(agent.getId(), itemDef.getType());
         }

         return items;
      }
      else {
         return artifactFinder.findByOwnerAndType(agent.getId(), itemDef.getType());
      }
   }

   public void cleanupTool(Id toolId) {
      for (Iterator i=findPresentationsByTool(toolId).iterator();i.hasNext();){
         Presentation presentation = (Presentation) i.next();
         deletePresentation(presentation.getId());
      }
   }

   public ContentHostingService getContentHosting() {
      return contentHosting;
   }

   public void setContentHosting(ContentHostingService contentHosting) {
      this.contentHosting = contentHosting;
   }

   public SecurityService getSecurityService() {
      return securityService;
   }

   public void setSecurityService(SecurityService securityService) {
      this.securityService = securityService;
   }

   public Collection findPublishedLayouts(String siteId) {
      /*
      return getHibernateTemplate().find(
            "from PresentationLayout where globalState=? and owner_id!=? and site_id=? Order by name",
            new Object[]{new Integer(PresentationLayout.STATE_PUBLISHED), 
                  getAuthnManager().getAgent().getId().getValue(), siteId});
      */
      return new ArrayList();
   }

   
   
   public Collection findLayoutsByOwner(Agent owner, String siteId) {
      return getHibernateTemplate().find("from PresentationLayout where owner_id=? and site_id=? Order by name",
            new Object[]{owner.getId().getValue(), siteId});
   }

   public Collection findMyGlobalLayouts() {
      return getHibernateTemplate().find(
         "from PresentationLayout where globalState=? Order by name",
         new Object[]{new Integer(PresentationLayout.STATE_PUBLISHED)});
   }

   public Collection findAllGlobalLayouts() {
      return getHibernateTemplate().find(
         "from PresentationLayout where globalState=? or globalState=? Order by name",
         new Object[]{new Integer(PresentationLayout.STATE_PUBLISHED), new Integer(PresentationLayout.STATE_WAITING_APPROVAL)});
   }
   
   public PresentationLayout storeLayout (PresentationLayout layout) {
      return storeLayout(layout, true);
   }
   
   public PresentationLayout storeLayout (PresentationLayout layout, boolean checkAuthz) {
      layout.setModified(new Date(System.currentTimeMillis()));

      boolean newLayout = (layout.getId() == null);

      if (newLayout) {
         layout.setCreated(new Date(System.currentTimeMillis()));

         if (checkAuthz) {
            getAuthzManager().checkPermission(PresentationFunctionConstants.CREATE_LAYOUT,
               getIdManager().getId(layout.getSiteId()));
         }
      } else {
         if (checkAuthz) {
            getAuthzManager().checkPermission(PresentationFunctionConstants.EDIT_LAYOUT,
                  layout.getId());
         }
      }
      getHibernateTemplate().saveOrUpdateCopy(layout);
      lockLayoutFiles(layout);

      return layout;
   }
   
   protected void lockLayoutFiles(PresentationLayout layout){
      clearLocks(layout.getId());
      getLockManager().lockObject(layout.getXhtmlFileId().getValue(), 
           layout.getId().getValue(), "saving a presentation layout", true);
      
      if (layout.getPreviewImageId() != null) {
         getLockManager().lockObject(layout.getPreviewImageId().getValue(), 
              layout.getId().getValue(), "saving a presentation layout", true);
      }
   }
   
   protected void lockStyleFiles(Style style){
      clearLocks(style.getId());
      getLockManager().lockObject(style.getStyleFile().getValue(), 
            style.getId().getValue(), "saving a style", true);
      
   }
   
   public PresentationLayout getPresentationLayout(Id id) {
      return (PresentationLayout) getHibernateTemplate().get(PresentationLayout.class, id);
   }
   
   public List getPresentationPagesByPresentation(Id presentationId) {
      return getHibernateTemplate().find(
            "from PresentationPage page where page.presentation.id=? order by seq_num",
            new Object[]{presentationId.getValue()});
   }

   public void deletePresentationLayout(final Id id) {
      PresentationLayout layout = getPresentationLayout(id);
      getAuthzManager().checkPermission(PresentationFunctionConstants.DELETE_LAYOUT, layout.getId());
      clearLocks(layout.getId());
      
      //TODO handle things that are using this layout
      // first delete all presentations that use this template
      // this will delete all authorization as well
      //Collection presentations = getHibernateTemplate().find("from Presentation where template_id=?", id.getValue(), Hibernate.STRING);
      //for (Iterator i = presentations.iterator(); i.hasNext();) {
      //   Presentation presentation = (Presentation) i.next();
      //   deletePresentation(presentation.getId(), false);
      //}

      HibernateCallback callback = new HibernateCallback() {

         public Object doInHibernate(Session session) throws HibernateException, SQLException {
            session.delete("from PresentationLayout where id=?", id.getValue(), Hibernate.STRING);
            return null;
         }

      };
      getHibernateTemplate().execute(callback);
   }
   
   public PresentationPage getPresentationPage(Id id) {
      PresentationPage page = (PresentationPage) getHibernateTemplate().get(PresentationPage.class, id);

      for (Iterator i=page.getRegions().iterator();i.hasNext();) {
         PresentationPageRegion region = (PresentationPageRegion) i.next();
         for (Iterator j=region.getItems().iterator();j.hasNext();) {
            PresentationPageItem item = (PresentationPageItem) j.next();
            item.getProperties().size();
         }
      }
      return page;
   }
   
   public PresentationPage getFirstPresentationPage(Id presentationId) {
      return getPresentationPage(presentationId, 0);
   }
   
   public PresentationPage getPresentationPage(Id presentationId, int pageIndex) {
      String query = "from PresentationPage page where page.presentation.id=? and page.sequence=? ";

      List pages = getHibernateTemplate().find(query, 
            new Object[]{presentationId.getValue(), new Integer(pageIndex)});

      return (PresentationPage)pages.get(0);
   }
   
   
   public Document getPresentationLayoutAsXml(Presentation presentation, String pageId) {
      viewingPresentation(presentation);
      PresentationPage page;
      if (pageId == null || pageId.equals(""))
         page = getFirstPresentationPage(presentation.getId());
      else
         page = getPresentationPage(getIdManager().getId(pageId));
      return getPresentationLayoutAsXml(page.getId());
   }
   
   
   protected Document getPresentationLayoutAsXml(Id pageId) {

      Element root = new Element("ospiPresentation");
      Element pageStyleElement = new Element("pageStyle");
      Element layoutElement = new Element("layout");
      Element regionsElement = new Element("regions");
      
      PresentationPage page = getPresentationPage(pageId);
      
      Id fileId = page.getLayout().getXhtmlFileId();
      Artifact art = getPresentationItem("fileArtifact", fileId, page.getPresentation());

      PresentableObjectHome home = (PresentableObjectHome) art.getHome();
      layoutElement.addContent(home.getArtifactAsXml(art));
      
      
         Style pageStyle = page.getStyle() != null ? page.getStyle() : page.getPresentation().getStyle();
         if (pageStyle != null && pageStyle.getStyleFile() != null) {
         Id cssFileId = pageStyle.getStyleFile();
         Artifact cssArt = getPresentationItem("fileArtifact", cssFileId, page.getPresentation());
         PresentableObjectHome cssHome = (PresentableObjectHome) cssArt.getHome();
         pageStyleElement.addContent(cssHome.getArtifactAsXml(cssArt));
         root.addContent(pageStyleElement);
      }
      
      for (Iterator regions = page.getRegions().iterator(); regions.hasNext();) {
         PresentationPageRegion region = (PresentationPageRegion) regions.next();
         int itemSeq = 0;
         for (Iterator items = region.getItems().iterator(); items.hasNext();) {
            PresentationPageItem item = (PresentationPageItem) items.next();
            Element regionElement = new Element("region");
            regionElement.setAttribute("id", region.getRegionId());
            if (region.getItems().size() > 1)
               regionElement.setAttribute("sequence",  String.valueOf(itemSeq));
            regionElement.setAttribute("type", item.getType());
            Element itemPropertiesElement = new Element("itemProperties");
            String contentType = "";
            for (Iterator properties = item.getProperties().iterator(); properties.hasNext();) {
               PresentationItemProperty prop = (PresentationItemProperty) properties.next();
               itemPropertiesElement.addContent(createElementNode(prop.getKey(), prop.getValue()));
               if (prop.getKey().equals(PresentationItemProperty.CONTENT_TYPE))
                  contentType = prop.getValue();
            }
            regionElement.addContent(itemPropertiesElement);
            regionElement.addContent(outputTypedContent(item.getType(), 
                  item.getValue(), page.getPresentation(), contentType));
            regionsElement.addContent(regionElement);
            itemSeq++;
         }
      }      
      
      root.addContent(layoutElement);
      root.addContent(createNavigationElement(page));
      root.addContent(regionsElement);
      return new Document(root);
   }
   
   protected Element outputTypedContent(String type, String value, 
         Presentation presentation, String contentType) {
      if (type.equals("text") || type.equals("richtext")) {
         Element textRegion = new Element("value");
         textRegion.addContent(new CDATA(value));
         return textRegion;
      }
      else if (type.equals("form") || type.equals("link") || type.equals("inline")) {         
         //String fileId = value;
         Element artifactAsXml = null;
         Id itemId = getIdManager().getId(value);
         if (!contentType.equals("page")) {
            Artifact art = getPresentationItem(contentType, itemId, presentation);

            PresentableObjectHome home = (PresentableObjectHome) art.getHome();
            artifactAsXml = home.getArtifactAsXml(art);
         }
         else {
            artifactAsXml = getPresentationPageAsXml(getPresentationPage(itemId));
         }
         return artifactAsXml;
      }
      return new Element("empty");
   }

   protected Artifact getPresentationItem(String type, Id itemId, Presentation presentation) {
      ArtifactFinder finder = getArtifactFinderManager().getArtifactFinderByType(type);
      
      Artifact art;

      if (finder instanceof EntityContextFinder) {
         art = ((EntityContextFinder)finder).loadInContext(itemId,
               PresentationContentEntityProducer.PRODUCER_NAME, 
               presentation.getSiteId(),
               presentation.getId().getValue());
      }
      else {
         art = finder.load(itemId);
      }
      return art;
   }
   
   protected Element getPresentationPageAsXml(PresentationPage page) {
      Element root = new Element("artifact");
      
      Element metadata = new Element("metaData");
      metadata.addContent(createElementNode("id", page.getId().getValue()));
      metadata.addContent(createElementNode("displayName", page.getTitle()));

      Element type = new Element("type");
      metadata.addContent(type);

      type.addContent(createElementNode("id", "page"));
      type.addContent(createElementNode("description", "Presentation Page"));
      
      Element fileData = new Element("fileArtifact");
      Element uri = new Element("uri");
      uri.addContent(page.getUrl());
      fileData.addContent(uri);

      root.addContent(metadata);
      root.addContent(fileData);
      
      return root;
   }
   
   protected Element createElementNode(String name, String value) {
      Element newNode = new Element(name);
      newNode.addContent(value);
      return newNode;
   }
   
   
   protected Element createNavigationElement(PresentationPage page) {
      int currentPage = page.getSequence();
      Element navigationElement = new Element("navigation");
      Element previousPage = new Element("previousPage");
      Element nextPage = new Element("nextPage");

      boolean isAdvancedNavigation = page.getPresentation().isAdvancedNavigation();

       if (!isAdvancedNavigation) {
         List pages = getPresentationPagesByPresentation(page.getPresentation().getId());
         PresentationPage lastNavPage = null;
         PresentationPage nextNavPage = null;
         boolean foundCurrent = false;

         for (Iterator i = pages.iterator(); i.hasNext();) {
            PresentationPage iterPage = (PresentationPage)i.next();
            if (iterPage.getSequence() == currentPage) {
               foundCurrent = true;
            }
            else if (!isAdvancedNavigation && !foundCurrent) {
               lastNavPage = iterPage;
            }
            else if (!isAdvancedNavigation) {
               nextNavPage = iterPage;
               break;
            }
         }

         if (lastNavPage != null) {
            previousPage.addContent(getPresentationPageAsXml(lastNavPage));
            navigationElement.addContent(previousPage);
         }
         if (nextNavPage != null) {
            nextPage.addContent(getPresentationPageAsXml(nextNavPage));
            navigationElement.addContent(nextPage);
         }
      }
      return navigationElement;
   }

   public String getTempPresDownloadDir() {
      return tempPresDownloadDir;
   }

   public void setTempPresDownloadDir(String tempPresDownloadDir) {
      this.tempPresDownloadDir = tempPresDownloadDir;
   }

   public void init() {
      initFreeFormTemplate();
      initGlobalLayouts();
   }

   protected void initGlobalLayouts() {
      getSecurityService().pushAdvisor(new AllowAllSecurityAdvisor());

      org.sakaiproject.tool.api.Session sakaiSession = SessionManager.getCurrentSession();
      String userId = sakaiSession.getUserId();
      sakaiSession.setUserId("admin");
      sakaiSession.setUserEid("admin");
      List layouts = new ArrayList();

      try {
         for (Iterator i=getDefinedLayouts().iterator();i.hasNext();) {
            layouts.add(processDefinedLayout((PresentationLayoutWrapper)i.next()));
         }

         for (Iterator i=layouts.iterator();i.hasNext();) {
            PresentationLayout layout = (PresentationLayout) i.next();
            getHibernateTemplate().saveOrUpdate(layout);
         }

      } finally {
         getSecurityService().popAdvisor();
         sakaiSession.setUserEid(userId);
         sakaiSession.setUserId(userId);
      }

   }

   protected PresentationLayout processDefinedLayout(PresentationLayoutWrapper wrapper) {
      PresentationLayout layout = getPresentationLayout(getIdManager().getId(wrapper.getIdValue()));

      if (layout == null) {
         layout = new PresentationLayout();
         layout.setCreated(new Date());
         layout.setNewId(getIdManager().getId(wrapper.getIdValue()));
      }

      updateLayout(wrapper, layout);
      return layout;
   }

   protected void updateLayout(PresentationLayoutWrapper wrapper, PresentationLayout layout) {
      if (layout.getPreviewImageId() != null) {
         deleteResource(layout.getId(), layout.getPreviewImageId());
      }
      if (layout.getXhtmlFileId() != null) {
         deleteResource(layout.getId(), layout.getXhtmlFileId());
      }

      layout.setPreviewImageId(createResource(wrapper.getPreviewFileLocation(),
         wrapper.getPreviewFileName(), wrapper.getIdValue() + " layout preview", wrapper.getPreviewFileType()));
      layout.setXhtmlFileId(createResource(wrapper.getLayoutFileLocation(),
         wrapper.getIdValue() + ".xml", wrapper.getIdValue() + " layout file", "text/xml"));

      layout.setModified(new Date());
      layout.setName(wrapper.getName());
      layout.setDescription(wrapper.getDescription());
      layout.setGlobalState(PresentationLayout.STATE_PUBLISHED);
      layout.setSiteId(null);
      layout.setToolId(null);
      layout.setOwner(getAgentManager().getAgent("admin"));
   }

   protected void deleteResource(Id qualifierId, Id resourceId) {
      try {
         getContentHosting().removeAllLocks(qualifierId.getValue());
         String id = getContentHosting().resolveUuid(resourceId.getValue());
         if (id == null) {
            return;
         }
         getContentHosting().removeResource(id);
      }
      catch (Exception e) {
         throw new RuntimeException(e);
      }
   }

   protected void initFreeFormTemplate() {
      getSecurityService().pushAdvisor(new AllowAllSecurityAdvisor());

      org.sakaiproject.tool.api.Session sakaiSession = SessionManager.getCurrentSession();
      String userId = sakaiSession.getUserId();
      sakaiSession.setUserId("admin");
      sakaiSession.setUserEid("admin");
      String resourceLocation = "/org/theospi/portfolio/presentation/freeform_template.xsl";

      try {
         PresentationTemplate template = getPresentationTemplate(getFreeFormTemplateId());
         if (template == null) {
            template = createFreeFormTemplate(createResource(resourceLocation,
               "freeFormRenderer", "used for rendering the free form template", "text/xml"));
         }
         else {
            updateResource(template.getId(), template.getRenderer(), resourceLocation);
            if (template.getItemDefinitions().size() == 0) {
               template.getItemDefinitions().add(createFreeFormItemDef(template));
            }
         }
         storeTemplate(template, false);
      } finally {
         getSecurityService().popAdvisor();
         sakaiSession.setUserEid(userId);
         sakaiSession.setUserId(userId);
      }
   }

   protected PresentationTemplate createFreeFormTemplate(Id rendererId) {
      PresentationTemplate template = new PresentationTemplate();
      template.setId(getFreeFormTemplateId());
      template.setName("Free Form Presentation");
      template.setRenderer(rendererId);
      template.setNewObject(true);
      template.setSiteId(getIdManager().createId().getValue());
      template.setToolId(getIdManager().createId().getValue());
      template.setOwner(getAgentManager().getAnonymousAgent());
      template.getItemDefinitions().add(createFreeFormItemDef(template));
      return template;
   }

   protected PresentationItemDefinition createFreeFormItemDef(PresentationTemplate template) {
      PresentationItemDefinition def = new PresentationItemDefinition();
      def.setPresentationTemplate(template);
      def.setAllowMultiple(true);
      def.setName("freeFormItem");
      def.setSequence(0);
      return def;
   }

   protected Id updateResource(Id qualifierId, Id resourceId, String resourceLocation) {
      ByteArrayOutputStream bos = loadResource(resourceLocation);

      try {
         getContentHosting().removeAllLocks(qualifierId.getValue());
         ContentResourceEdit resourceEdit =
               getContentHosting().editResource(getContentHosting().resolveUuid(resourceId.getValue()));
         resourceEdit.setContent(bos.toByteArray());
         getContentHosting().commitResource(resourceEdit);
         return resourceId;
      }
      catch (Exception e) {
         throw new RuntimeException(e);
      }
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

   protected Id createResource(String resourceLocation,
                               String name, String description, String type) {
      ByteArrayOutputStream bos = loadResource(resourceLocation);
      ContentResource resource;
      ResourcePropertiesEdit resourceProperties = getContentHosting().newResourceProperties();
      resourceProperties.addProperty (ResourceProperties.PROP_DISPLAY_NAME, name);
      resourceProperties.addProperty (ResourceProperties.PROP_DESCRIPTION, description);
      resourceProperties.addProperty(ResourceProperties.PROP_CONTENT_ENCODING, "UTF-8");
      
      String folder = "/group/PortfolioAdmin" + SYSTEM_COLLECTION_ID;
      
      try {
         //TODO use the bean org.theospi.portfolio.admin.model.IntegrationOption.siteOption 
         // in common/components to get the name and id for this site.
         
         ContentCollectionEdit groupCollection = getContentHosting().addCollection("/group/PortfolioAdmin");
         groupCollection.getPropertiesEdit().addProperty(ResourceProperties.PROP_DISPLAY_NAME, "Portfolio Admin");
         getContentHosting().commitCollection(groupCollection);
                  
         ContentCollectionEdit collection = getContentHosting().addCollection(folder);
         collection.getPropertiesEdit().addProperty(ResourceProperties.PROP_DISPLAY_NAME, "system");
         getContentHosting().commitCollection(collection);
         
      }
      catch (IdUsedException e) {
         // ignore... it is already there.
      }
      catch (Exception e) {
         throw new RuntimeException(e);
      }

      try {
         String id = folder + name;
         getContentHosting().removeResource(id);
      }
      catch (TypeException e) {
         // ignore, must be new
      }
      catch (IdUnusedException e) {
         // ignore, must be new
      }
      catch (PermissionException e) {
         // ignore, must be new
      }
      catch (InUseException e) {
         // ignore, must be new
      }

      try {
         resource = getContentHosting().addResource(name, folder, 0, type,
                     bos.toByteArray(), resourceProperties, NotificationService.NOTI_NONE);
      }
      catch (Exception e) {
         throw new RuntimeException(e);
      }
      String uuid = getContentHosting().getUuid(resource.getId());
      return getIdManager().getId(uuid);
   }

   public Id getFreeFormTemplateId() {
      return Presentation.FREEFORM_TEMPLATE_ID;
   }

   public List getDefinedLayouts() {
      return definedLayouts;
   }

   public void setDefinedLayouts(List definedLayouts) {
      this.definedLayouts = definedLayouts;
   }

   public StructuredArtifactDefinitionManager getStructuredArtifactDefinitionManager() {
      return structuredArtifactDefinitionManager;
   }

   public void setStructuredArtifactDefinitionManager(StructuredArtifactDefinitionManager structuredArtifactDefinitionManager) {
      this.structuredArtifactDefinitionManager = structuredArtifactDefinitionManager;
   }

   public List getGlobalSites() {
      return globalSites;
   }

   public void setGlobalSites(List globalSites) {
      this.globalSites = globalSites;
   }

   public List getGlobalSiteTypes() {
      return globalSiteTypes;
   }

   public void setGlobalSiteTypes(List globalSiteTypes) {
      this.globalSiteTypes = globalSiteTypes;
   }

   public List getInitializedServices() {
      return initializedServices;
   }

   public void setInitializedServices(List initializedServices) {
      this.initializedServices = initializedServices;
   }

   protected List getPresentationPagesByStyle(Id styleId) {
      Object[] params = new Object[]{styleId.getValue()};
      return getHibernateTemplate().find("from PresentationPage pp where pp.style.id=? " , 
               params);
   }
   
   protected List getPresentationsByStyle(Id styleId) {
      Object[] params = new Object[]{styleId.getValue()};
      return getHibernateTemplate().find("from Presentation p where p.style.id=? " , 
               params);
   }
   
   public boolean checkStyleConsumption(Id styleId) {
      List pages = getPresentationPagesByStyle(styleId);
      if (pages != null && !pages.isEmpty() && pages.size() > 0)
         return true;
      
      List presentations = getPresentationsByStyle(styleId);
      if (presentations != null && !presentations.isEmpty() && presentations.size() > 0)
         return true;
      
      return false;
   }

}

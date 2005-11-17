/*
 * The Open Source Portfolio Initiative Software is Licensed under the Educational Community License Version 1.0:
 *
 * This Educational Community License (the "License") applies to any original work of authorship
 * (the "Original Work") whose owner (the "Licensor") has placed the following notice immediately
 * following the copyright notice for the Original Work:
 *
 * Copyright (c) 2004 Trustees of Indiana University and r-smart Corporation
 *
 * This Original Work, including software, source code, documents, or other related items, is being
 * provided by the copyright holder(s) subject to the terms of the Educational Community License.
 * By obtaining, using and/or copying this Original Work, you agree that you have read, understand,
 * and will comply with the following terms and conditions of the Educational Community License:
 *
 * Permission to use, copy, modify, merge, publish, distribute, and sublicense this Original Work and
 * its documentation, with or without modification, for any purpose, and without fee or royalty to the
 * copyright holder(s) is hereby granted, provided that you include the following on ALL copies of the
 * Original Work or portions thereof, including modifications or derivatives, that you make:
 *
 * - The full text of the Educational Community License in a location viewable to users of the
 * redistributed or derivative work.
 *
 * - Any pre-existing intellectual property disclaimers, notices, or terms and conditions.
 *
 * - Notice of any changes or modifications to the Original Work, including the date the changes were made.
 *
 * - Any modifications of the Original Work must be distributed in such a manner as to avoid any confusion
 *  with the Original Work of the copyright holders.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
 * SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 * The name and trademarks of copyright holder(s) may NOT be used in advertising or publicity pertaining
 * to the Original or Derivative Works without specific, written prior permission. Title to copyright
 * in the Original Work and any associated documentation will at all times remain with the copyright holders.
 *
 * $Header: /opt/CVS/osp2.x/presentation/api-impl/src/java/org/theospi/portfolio/presentation/model/impl/PresentationManagerImpl.java,v 1.13 2005/10/26 23:53:01 jellis Exp $
 * $Revision$
 * $Date$
 */
/*
 * $Header: /opt/CVS/osp2.x/presentation/api-impl/src/java/org/theospi/portfolio/presentation/model/impl/PresentationManagerImpl.java,v 1.13 2005/10/26 23:53:01 jellis Exp $
 * $Revision$
 * $Date$
 */
package org.theospi.portfolio.presentation.model.impl;

import net.sf.hibernate.Hibernate;
import net.sf.hibernate.HibernateException;
import net.sf.hibernate.Query;
import net.sf.hibernate.Session;

import org.jdom.Document;
import org.jdom.Element;
import org.sakaiproject.service.framework.portal.cover.PortalService;
import org.sakaiproject.service.legacy.resource.DuplicatableToolService;
import org.sakaiproject.service.legacy.resource.cover.EntityManager;
import org.sakaiproject.service.legacy.entity.Reference;
import org.sakaiproject.service.legacy.entity.ResourceProperties;
import org.sakaiproject.service.legacy.entity.ResourcePropertiesEdit;
import org.sakaiproject.service.legacy.entity.EntityProducer;
import org.sakaiproject.service.legacy.site.ToolConfiguration;
import org.sakaiproject.service.legacy.site.cover.SiteService;
import org.sakaiproject.service.legacy.user.User;
import org.sakaiproject.service.legacy.user.cover.UserDirectoryService;
import org.springframework.orm.hibernate.HibernateCallback;
import org.springframework.orm.hibernate.HibernateObjectRetrievalFailureException;
import org.springframework.orm.hibernate.support.HibernateDaoSupport;
import org.theospi.portfolio.presentation.CommentSortBy;
import org.theospi.portfolio.presentation.PresentationFunctionConstants;
import org.theospi.portfolio.presentation.PresentationManager;
import org.theospi.portfolio.presentation.model.*;
import org.sakaiproject.exception.*;
import org.sakaiproject.metaobj.shared.*;
import org.theospi.portfolio.shared.model.Node;
import org.theospi.portfolio.shared.intf.DownloadableManager;
import org.theospi.portfolio.shared.intf.EntityContextFinder;
import org.sakaiproject.service.legacy.content.ContentCollection;
import org.sakaiproject.service.legacy.content.ContentCollectionEdit;
import org.sakaiproject.service.legacy.content.ContentHostingService;
import org.sakaiproject.service.legacy.content.ContentResource;
import org.sakaiproject.service.legacy.content.ContentResourceEdit;
import org.sakaiproject.service.legacy.content.LockManager;
import org.sakaiproject.service.legacy.security.SecurityService;
import org.sakaiproject.metaobj.security.AuthenticationManager;
import org.theospi.portfolio.security.Authorization;
import org.theospi.portfolio.security.AuthorizationFacade;
import org.theospi.portfolio.security.AllowMapSecurityAdvisor;
import org.theospi.portfolio.shared.model.*;
import org.sakaiproject.metaobj.shared.mgt.AgentManager;
import org.sakaiproject.metaobj.shared.mgt.HomeFactory;
import org.sakaiproject.metaobj.shared.mgt.IdManager;
import org.sakaiproject.metaobj.shared.mgt.PresentableObjectHome;
import org.sakaiproject.metaobj.shared.mgt.ReadableObjectHome;
import org.sakaiproject.metaobj.shared.mgt.WritableObjectHome;
import org.sakaiproject.metaobj.shared.model.Agent;
import org.sakaiproject.metaobj.shared.model.Artifact;
import org.sakaiproject.metaobj.shared.model.Id;
import org.sakaiproject.metaobj.shared.model.InvalidUploadException;
import org.sakaiproject.metaobj.shared.model.MimeType;
import org.sakaiproject.metaobj.shared.model.impl.AgentImpl;
import org.sakaiproject.metaobj.worksite.mgt.WorksiteManager;
import org.theospi.utils.zip.UncloseableZipInputStream;

import java.io.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;
import java.util.zip.*;

public class PresentationManagerImpl extends HibernateDaoSupport
   implements PresentationManager, DuplicatableToolService, DownloadableManager {

   private AgentManager agentManager;
   private AuthorizationFacade authzManager = null;
   private AuthenticationManager authnManager = null;
   //private RepositoryManager repositoryManager = null;
   private IdManager idManager = null;
   private WritableObjectHome fileHome;
   private HomeFactory homeFactory = null;
   private WorksiteManager worksiteManager;
   private LockManager lockManager;
   private ArtifactFinderManager artifactFinderManager;
   private ContentHostingService contentHosting = null;
   private SecurityService securityService = null;

   private static final String TOOL_ID = "osp.pres.template";
   private static final String TEMPLATE_ID_TAG = "templateId";

   public PresentationTemplate storeTemplate(final PresentationTemplate template) {
      return storeTemplate(template, true);
   }

   protected PresentationTemplate storeTemplate(final PresentationTemplate template, boolean checkAuthz) {
      template.setModified(new Date(System.currentTimeMillis()));

      boolean newTemplate = (template.getId() == null);

      if (newTemplate) {
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
      getHibernateTemplate().saveOrUpdateCopy(template);
      lockTemplateFiles(template);

      return template;
   }

   /**
    * remove all the locks associated with this template
    */
   protected void clearLocks(PresentationTemplate template){
      getLockManager().removeAllLocks(template.getId().getValue());
   }

   /**
    * locks all the files associated with this template.
    * @param template
    */
   protected void lockTemplateFiles(PresentationTemplate template){
      clearLocks(template);
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

         if (!presentation.getIsPublic()) {
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

   public void deletePresentationTemplate(final Id id) {
      PresentationTemplate template = getPresentationTemplate(id);
      getAuthzManager().checkPermission(PresentationFunctionConstants.DELETE_TEMPLATE, template.getId());
      clearLocks(template);

      // first delete all presentations that use this template
      // this will delete all authorization as well
      Collection presentations = getHibernateTemplate().find("from Presentation where template_id=?", id.getValue(), Hibernate.STRING);
      for (Iterator i = presentations.iterator(); i.hasNext();) {
         Presentation presentation = (Presentation) i.next();
         deletePresentation(presentation.getId(), false);
      }

      HibernateCallback callback = new HibernateCallback() {

         public Object doInHibernate(Session session) throws HibernateException, SQLException {
            session.delete("from PresentationTemplate where id=?", id.getValue(), Hibernate.STRING);
            return null;
         }

      };
      getHibernateTemplate().execute(callback);


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
      if (presentation.getId() == null) {
         presentation.setCreated(new Date(System.currentTimeMillis()));
         getAuthzManager().checkPermission(PresentationFunctionConstants.CREATE_PRESENTATION,
            getIdManager().getId(PortalService.getCurrentToolId()));
      } else {
            getAuthzManager().checkPermission(PresentationFunctionConstants.EDIT_PRESENTATION,
                  presentation.getId());
      }
      getHibernateTemplate().saveOrUpdateCopy(presentation);

      storePresentationViewers(presentation);

      return presentation;
   }

   protected Agent createGuestUser(Agent viewer){
      AgentImpl guest = (AgentImpl) viewer;
      guest.setRole(Agent.ROLE_GUEST);
      return getAgentManager().createAgent(guest);
   }

   protected void storePresentationViewers(Presentation presentation) {
      Agent agent = presentation.getOwner();

      Collection viewers = new ArrayList(presentation.getViewers());

      Collection oldViewerAuthzs = getAuthzManager().getAuthorizations(null,
            PresentationFunctionConstants.VIEW_PRESENTATION, presentation.getId());

      boolean currentAgentFound = false;

      for (Iterator i = viewers.iterator(); i.hasNext();) {
         Agent viewer = (Agent) i.next();


         if (viewer instanceof AgentImpl) {
            viewer = createGuestUser(viewer);
         }

         if (agent.getId().equals(viewer.getId())){
            currentAgentFound = true;
         }

         Authorization newAuthz = new Authorization(viewer,
               PresentationFunctionConstants.VIEW_PRESENTATION, presentation.getId());

         if (oldViewerAuthzs.contains(newAuthz)) {
            oldViewerAuthzs.remove(newAuthz);
         } else {
            getAuthzManager().createAuthorization(newAuthz.getAgent(),
                  newAuthz.getFunction(), newAuthz.getQualifier());
         }
      }

      // any leftovers must be removes...
      for (Iterator i = oldViewerAuthzs.iterator(); i.hasNext();) {
         Authorization authz = (Authorization) i.next();
         getAuthzManager().deleteAuthorization(authz.getAgent(),
               authz.getFunction(), authz.getQualifier());
      }

      // make sure owner always has view authz for presentation they own
      if (!currentAgentFound) {
         Authorization newAuthz = new Authorization(agent,
               PresentationFunctionConstants.VIEW_PRESENTATION, presentation.getId());
         getAuthzManager().createAuthorization(newAuthz.getAgent(),
               newAuthz.getFunction(), newAuthz.getQualifier());
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
         Presentation pres = getPresentation(presId);

         if (!returned.contains(pres) && !pres.isExpired()) {
            getHibernateTemplate().evict(pres);
            returned.add(pres);
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

         if (!returned.contains(pres) && !pres.isExpired()) {
            pres.setAuthz(new PresentationAuthzMap(viewer, pres));
            returned.add(pres);
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
         getWorksiteManager().getTool(PortalService.getCurrentToolId()), true, true);
   }

   public void packageTemplateForExport(Id templateId, OutputStream os) throws IOException {
      getAuthzManager().checkPermission(PresentationFunctionConstants.CREATE_TEMPLATE,
         getIdManager().getId(PortalService.getCurrentToolId()));
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

      if (items != null) {
         oldTemplate.setItems(new HashSet(items));
         for (Iterator i=oldTemplate.getItems().iterator();i.hasNext();) {
            PresentationItemDefinition item = (PresentationItemDefinition)i.next();

            ReadableObjectHome home =
               (ReadableObjectHome)getHomeFactory().getHome(item.getType());

            if (home != null) {
               item.setExternalType(home.getExternalType());
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
               gotFile = true;
               processTemplateFile(currentEntry, zis, fileMap, fileParent);
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
            ref.setFileId((String)fileMap.get(ref.getFileId()));
            ref.setPresentationTemplate(template);
         }

         int index = 100;
         for (Iterator i=template.getItems().iterator();i.hasNext();) {
            PresentationItemDefinition item = (PresentationItemDefinition)i.next();

            if (item.getSequence() == 0) {
               item.setSequence(index);
            }
            index++;

            if (item.getExternalType() != null) {
               ReadableObjectHome home = getHomeFactory().findHomeByExternalId(
                  item.getExternalType(), getIdManager().getId(template.getSiteId()));
               if (home != null) {
                  item.setType(home.getType().getId().getValue());
               }
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

         if (art.getHome() instanceof PresentableObjectHome) {
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
         String previewImageId = getContentHosting().resolveUuid(page.getLayout().getPreviewImageId().getValue());
         if (previewImageId != null) {
            readableFiles.add(getContentHosting().getReference(previewImageId));
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
      packageTemplateForExport(getIdManager().getId(((String[])params.get(TEMPLATE_ID_TAG))[0]),
         out);
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
      return getHibernateTemplate().find(
            "from PresentationLayout where published=? and owner_id!=? and site_id=? Order by name",
            new Object[]{new Boolean(true), getAuthnManager().getAgent().getId().getValue(), siteId});
   }

   public Collection findLayoutsByOwner(Agent owner, String siteId) {
      return getHibernateTemplate().find("from PresentationLayout where owner_id=? and site_id=? Order by name",
            new Object[]{owner.getId().getValue(), siteId});
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
               getIdManager().getId(layout.getToolId()));
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
      clearLocks(layout);
      getLockManager().lockObject(layout.getXhtmlFileId().getValue(), 
           layout.getId().getValue(), "saving a presentation layout", true);
      
      if (layout.getPreviewImageId() != null) {
         getLockManager().lockObject(layout.getPreviewImageId().getValue(), 
              layout.getId().getValue(), "saving a presentation layout", true);
      }
   }
   
   protected void clearLocks(PresentationLayout layout){
      getLockManager().removeAllLocks(layout.getId().getValue());
   }
   
   public PresentationLayout getPresentationLayout(Id id) {
      return (PresentationLayout) getHibernateTemplate().get(PresentationLayout.class, id);
   }
   
   protected List getPresentationPagesByPresentation(Id presentationId) {
      return getHibernateTemplate().find(
            "from PresentationPage page where page.presentation.id=? ", 
            new Object[]{presentationId.getValue()});
   }

   public void deletePresentationLayout(final Id id) {
      PresentationLayout layout = getPresentationLayout(id);
      getAuthzManager().checkPermission(PresentationFunctionConstants.DELETE_LAYOUT, layout.getId());
      clearLocks(layout);
      
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
      return (PresentationPage) getHibernateTemplate().get(PresentationPage.class, id);
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
      Element layoutElement = new Element("layout");
      Element navigationElement = new Element("navigation");
      Element regionsElement = new Element("regions");
      
      PresentationPage page = getPresentationPage(pageId);
      
      Id fileId = page.getLayout().getXhtmlFileId();
      Artifact art = getPresentationItem("fileArtifact", fileId, page.getPresentation());

      PresentableObjectHome home = (PresentableObjectHome) art.getHome();
      layoutElement.addContent(home.getArtifactAsXml(art));
      
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
      root.addContent(navigationElement);
      root.addContent(regionsElement);
      return new Document(root);
   }
   
   protected Element outputTypedContent(String type, String value, 
         Presentation presentation, String contentType) {
      if (type.equals("text") || type.equals("richtext")) {
         Element textRegion = new Element("value");
         textRegion.addContent(value);
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
               presentation.getTemplate().getSiteId(),
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
      uri.addContent("viewPresentation.osp?id=" + page.getPresentation().getId().getValue() + 
            "&page=" + page.getId().getValue());
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

}

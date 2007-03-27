/**********************************************************************************
* $URL:https://source.sakaiproject.org/svn/osp/trunk/presentation/tool/src/java/org/theospi/portfolio/presentation/control/ViewPresentationControl.java $
* $Id:ViewPresentationControl.java 9134 2006-05-08 20:28:42Z chmaurer@iupui.edu $
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
package org.theospi.portfolio.presentation.control;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.URIResolver;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdom.Document;
import org.sakaiproject.exception.IdUnusedException;
import org.sakaiproject.metaobj.shared.ArtifactFinder;
import org.sakaiproject.metaobj.shared.ArtifactFinderManager;
import org.sakaiproject.metaobj.shared.mgt.HomeFactory;
import org.sakaiproject.metaobj.shared.model.Agent;
import org.sakaiproject.metaobj.shared.model.Id;
import org.sakaiproject.metaobj.shared.model.PersistenceException;
import org.sakaiproject.metaobj.utils.mvc.intf.LoadObjectController;
import org.sakaiproject.site.api.Site;
import org.sakaiproject.site.api.ToolConfiguration;
import org.sakaiproject.site.cover.SiteService;
import org.sakaiproject.tool.api.ToolSession;
import org.sakaiproject.tool.cover.SessionManager;
import org.sakaiproject.tool.cover.ToolManager;
import org.sakaiproject.util.Tool;
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;
import org.springframework.web.servlet.ModelAndView;
import org.theospi.portfolio.presentation.PresentationFunctionConstants;
import org.theospi.portfolio.presentation.PresentationManager;
import org.theospi.portfolio.presentation.model.Presentation;
import org.theospi.portfolio.presentation.model.PresentationComment;
import org.theospi.portfolio.presentation.model.PresentationLog;
import org.theospi.portfolio.security.AuthorizationFacade;
import org.theospi.portfolio.shared.model.Node;
import org.theospi.portfolio.shared.model.OspException;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: May 25, 2004
 * Time: 1:52:39 PM
 * To change this template use File | Settings | File Templates.
 */
public class ViewPresentationControl extends AbstractPresentationController implements LoadObjectController {

   protected final Log logger = LogFactory.getLog(getClass());
   private HomeFactory homeFactory = null;
   private ArtifactFinder artifactFinder = null;
   private AuthorizationFacade authzManager = null;
   private ArtifactFinderManager artifactFinderManager;
   private Hashtable presentationTemplateCache = new Hashtable();
   private URIResolver uriResolver;

   public Object fillBackingObject(Object incomingModel, Map request,
                                   Map session, Map application) throws Exception {
      PresentationManager presentationManager = getPresentationManager();
      Presentation presentation = (Presentation) incomingModel;
      if (presentation.getSecretExportKey() != null) {
         String secretExportKey = presentation.getSecretExportKey();
         presentation = presentationManager.getPresentation(presentation.getId(),
               secretExportKey);
         presentation.setSecretExportKey(secretExportKey);
         return presentation;
      }
      else {
         // if it exists, get the presentation from memory that is being edited
         Presentation previewPres = (Presentation) session.get("presentation");
         if (previewPres != null && previewPres.getId().getValue().equals(presentation.getId().getValue())) {

            //side step any authz issues as the presentation only exists in the users session
            previewPres.setIsPublic(true);
            previewPres.setIsPreview(true);

            return previewPres;
    	 }

         return getPresentationManager().getLightweightPresentation(presentation.getId());
      }
   }

   public ModelAndView handleRequest(Object requestModel, Map request,
                                     Map session, Map application, Errors errors) {
      Presentation pres = (Presentation) requestModel;

      if (pres.getSecretExportKey() == null) {
         if (!pres.getIsPublic()) {
            if (getAuthManager().getAgent().isInRole(Agent.ROLE_ANONYMOUS)){
               try {
                  Site site = SiteService.getSite(pres.getSiteId());               
                  ToolConfiguration toolConfig = site.getToolForCommonId(PresentationFunctionConstants.PRES_TOOL_ID);
                  String placement = toolConfig.getId();
                  ToolSession ts = SessionManager.getCurrentSession().getToolSession(placement);               
                  SessionManager.setCurrentToolSession(ts);
                 
                  SessionManager.getCurrentSession().setAttribute(Tool.HELPER_DONE_URL, pres.getExternalUri());

                  Map model = new Hashtable();
                  model.put("sakai.tool.placement.id", placement);
                  return new ModelAndView("authnRedirect", model);
                 
               } catch (IdUnusedException e) {
                  logger.error("", e);
               }
            }
            else {

               getAuthzManager().checkPermission(PresentationFunctionConstants.VIEW_PRESENTATION, pres.getId());
            }
         }

         if (pres.isExpired() &&
            !pres.getOwner().getId().equals(getAuthManager().getAgent().getId())) {
            return new ModelAndView("expired");
         }
      }

      if (!pres.isPreview()) {
         logViewedPresentation(pres);
      }
      
      Hashtable model = new Hashtable();

      try {
         model.put("presentation", pres);
         Document doc = null;
         
         if (pres.getPresentationType().equals(Presentation.TEMPLATE_TYPE))
            doc = getPresentationManager().createDocument(pres);
         else {
            String page = (String)request.get("page");
            if (pres.isPreview()) {
               doc = getPresentationManager().getPresentationPreviewLayoutAsXml(pres, page);
            }
            else {
               doc = getPresentationManager().getPresentationLayoutAsXml(pres, page);
            }
         }
         Site site = SiteService.getSite(pres.getSiteId());
         getAuthzManager().pushAuthzGroups(site.getId());
         ToolConfiguration toolConfig = site.getToolForCommonId(PresentationFunctionConstants.PRES_TOOL_ID);
         String placement = toolConfig.getId();
         model.put("placementId", placement); 
         if(doc != null)
            model.put("document", doc);
         else
            return new ModelAndView("notFound", model);
         model.put("renderer", getTransformer(pres, request));
         model.put("uriResolver", getUriResolver());

         if (!getAuthManager().getAgent().isInRole(Agent.ROLE_ANONYMOUS)) {
            model.put("currentAgent", getAuthManager().getAgent());
         }

         if (!pres.isPreview()) {
            model.put("comments", getPresentationManager().getPresentationComments(pres.getId(),
                getAuthManager().getAgent()));

            boolean allowComments = getAuthzManager().isAuthorized( 
                PresentationFunctionConstants.COMMENT_PRESENTATION, pres.getId() );
            model.put("allowComments", allowComments );
         }
         else {
            model.put("allowComments", pres.isAllowComments());
         }
	         
         if (request.get(BindException.ERROR_KEY_PREFIX + "newComment") == null) {
            request.put(BindException.ERROR_KEY_PREFIX + "newComment",
                  new BindException(new PresentationComment(), "newComment"));
         }

      } catch (PersistenceException e) {
         logger.error("",e);
         throw new OspException(e);
      } catch (IdUnusedException e) {
         logger.error("", e);
      }


      boolean headers = pres.getTemplate().isIncludeHeaderAndFooter();
      String viewName = "withoutHeader";

      if (headers) {
         if (ToolManager.getCurrentPlacement() == null) {
            viewName = "withHeaderStandalone";
         }
         else {
            viewName = "withHeader";
         }
      }
      return new ModelAndView(viewName, model);
   }
   
   /**
    * creates a new log that this presentation has been viewed
    * @param pres
    */
   protected void logViewedPresentation(Presentation pres){
      PresentationLog log = new PresentationLog();
      log.setPresentation(pres);
      log.setViewDate(new java.util.Date());
      log.setViewer(getAuthManager().getAgent());
      getPresentationManager().storePresentationLog(log);
   }

   // cache the template...
   protected Transformer getTransformer(Presentation presentation, Map request) throws PersistenceException {
      Id renderer = presentation.getTemplate().getRenderer();
      TransformerWrapper wrapper = (TransformerWrapper) presentationTemplateCache.get(renderer);

      if (wrapper == null) {
         wrapper = new TransformerWrapper();
         wrapper.modified = 0;
      }

      Node xsl = getPresentationManager().getNode(renderer);

      if (xsl.getTechnicalMetadata().getLastModified().getTime() > wrapper.modified) {
         try {
            wrapper.transformer = TransformerFactory.newInstance()
                  .newTransformer(new StreamSource(xsl.getInputStream()));
            wrapper.modified = xsl.getTechnicalMetadata().getLastModified()
                  .getTime();
         } catch (TransformerConfigurationException e) {
            throw new OspException(e);
         }
      }

      wrapper.transformer.clearParameters();

      //send request params in as transform params
      for(Iterator i=request.keySet().iterator();i.hasNext();){
         String paramName = (String) i.next();
         wrapper.transformer.setParameter(paramName,request.get(paramName));
      }

      presentationTemplateCache.put(renderer,wrapper);

      return wrapper.transformer;
   }

   private class TransformerWrapper {
      public long modified;
      public Transformer transformer;
   }

   public HomeFactory getHomeFactory() {
      return homeFactory;
   }

   public void setHomeFactory(HomeFactory homeFactory) {
      this.homeFactory = homeFactory;
   }

   public ArtifactFinder getArtifactFinder() {
      return artifactFinder;
   }

   public void setArtifactFinder(ArtifactFinder artifactFinder) {
      this.artifactFinder = artifactFinder;
   }

   public AuthorizationFacade getAuthzManager() {
      return authzManager;
   }

   public void setAuthzManager(AuthorizationFacade authzManager) {
      this.authzManager = authzManager;
   }

   public ArtifactFinderManager getArtifactFinderManager() {
      return artifactFinderManager;
   }

   public void setArtifactFinderManager(ArtifactFinderManager artifactFinderManager) {
      this.artifactFinderManager = artifactFinderManager;
   }

   public Hashtable getPresentationTemplateCache() {
      return presentationTemplateCache;
   }

   public void setPresentationTemplateCache(Hashtable presentationTemplateCache) {
      this.presentationTemplateCache = presentationTemplateCache;
   }

   public URIResolver getUriResolver() {
      return uriResolver;
   }

   public void setUriResolver(URIResolver uriResolver) {
      this.uriResolver = uriResolver;
   }
}

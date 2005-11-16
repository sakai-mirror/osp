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
 * $Header: /opt/CVS/osp2.x/presentation/tool/src/java/org/theospi/portfolio/presentation/control/ViewPresentationControl.java,v 1.6 2005/09/16 15:34:37 chmaurer Exp $
 * $Revision$
 * $Date$
 */
package org.theospi.portfolio.presentation.control;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdom.Document;
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;
import org.springframework.web.servlet.ModelAndView;
import org.theospi.portfolio.presentation.PresentationManager;
import org.theospi.portfolio.presentation.PresentationFunctionConstants;
import org.theospi.portfolio.presentation.model.*;
import org.sakaiproject.metaobj.shared.ArtifactFinder;
import org.sakaiproject.metaobj.shared.ArtifactFinderManager;
import org.theospi.portfolio.security.AuthorizationFacade;
import org.sakaiproject.metaobj.shared.mgt.HomeFactory;
import org.sakaiproject.metaobj.shared.model.Agent;
import org.theospi.portfolio.shared.model.Node;
import org.theospi.portfolio.shared.model.OspException;
import org.sakaiproject.metaobj.shared.model.PersistenceException;
import org.sakaiproject.metaobj.shared.model.Id;
import org.sakaiproject.metaobj.utils.mvc.intf.LoadObjectController;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.URIResolver;
import javax.xml.transform.stream.StreamSource;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;

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
      return presentationManager.getPresentation(presentation.getId());
   }

   public ModelAndView handleRequest(Object requestModel, Map request,
                                     Map session, Map application, Errors errors) {
      Presentation pres = (Presentation) requestModel;

      if (!pres.getIsPublic()) {
         getAuthzManager().checkPermission(PresentationFunctionConstants.VIEW_PRESENTATION, pres.getId());
      }

      if (pres.isExpired() &&
         !pres.getOwner().getId().equals(getAuthManager().getAgent().getId())) {
         return new ModelAndView("expired");
      }

      logViewedPresentation(pres);

      Hashtable model = new Hashtable();

      try {
         model.put("presentation", pres);
         Document doc = null;
         if (pres.getPresentationType().equals(Presentation.TEMPLATE_TYPE))
            doc = getPresentationManager().createDocument(pres);
         else
            doc = getPresentationManager().getPresentationPageAsXml(pres);
         
         model.put("document", doc);
         model.put("renderer", getTransformer(pres, request));
         model.put("uriResolver", getUriResolver());

         if (!getAuthManager().getAgent().isInRole(Agent.ROLE_ANONYMOUS)) {
            model.put("currentAgent", getAuthManager().getAgent());
         }

         model.put("comments", getPresentationManager().getPresentationComments(pres.getId(),
               getAuthManager().getAgent()));

         if (request.get(BindException.ERROR_KEY_PREFIX + "newComment") == null) {
            request.put(BindException.ERROR_KEY_PREFIX + "newComment",
                  new BindException(new PresentationComment(), "newComment"));
         }

      } catch (PersistenceException e) {
         logger.error("",e);
         throw new OspException(e);
      }

      boolean headers = pres.getTemplate().isIncludeHeaderAndFooter();

      return new ModelAndView(headers ? "withHeader" : "withoutHeader", model);
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

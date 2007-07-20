package org.sakaiproject.portal.xsltcharon.impl;

import org.sakaiproject.portal.api.PortalRenderEngine;
import org.sakaiproject.portal.api.PortalRenderContext;
import org.sakaiproject.portal.api.PortalService;
import org.sakaiproject.portal.api.Portal;
import org.sakaiproject.tool.api.Placement;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.context.ServletContextAware;
import org.w3c.dom.Document;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletContext;
import javax.servlet.ServletConfig;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.*;
import java.io.Writer;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by IntelliJ IDEA.
 * User: johnellis
 * Date: Jul 13, 2007
 * Time: 12:06:22 PM
 * To change this template use File | Settings | File Templates.
 */
public class XsltRenderEngine implements PortalRenderEngine, ServletContextAware {

   private static final String XSLT_CONTEXT = "xsltCharon";

   private static final Log log = LogFactory.getLog(XsltRenderEngine.class);

   /** injected **/
   private PortalService portalService;
   private String transformerPath;

   private ServletContext servletContext;

   private Templates templates;
   private URIResolver servletResolver;

   public XsltRenderEngine() {
   }

   public XsltRenderEngine(PortalService portalService, ServletContext servletContext) {
      this.portalService = portalService;
      this.servletContext = servletContext;
   }

   /**
    * Initialise the render engine
    *
    * @throws Exception
    */
   public void init() throws Exception {
   }

   public void springInit() {
      getPortalService().addRenderEngine(XSLT_CONTEXT, this);
      try {
         setTemplates(createTemplate());
      } catch (MalformedURLException e) {
         log.error("unable to init portal transformation", e);
      } catch (TransformerConfigurationException e) {
         log.error("unable to init portal transformation", e);
      }
      
      setServletResolver(new ServletResourceUriResolver(getServletContext()));
   }

   public void destroy() {
      getPortalService().removeRenderEngine(XSLT_CONTEXT, this);
   }

   /**
    * generate a non thread safe render context for the current
    * request/thread/operation
    *
    * @param request
    * @return new render context
    */
   public PortalRenderContext newRenderContext(HttpServletRequest request) {
      PortalRenderContext base =
         getPortalService().getRenderEngine(Portal.DEFAULT_PORTAL_CONTEXT, request).newRenderContext(request);

      return new XsltRenderContext(this, base, request);
   }

   /**
    * Render a PortalRenderContext against a template. The real template may be
    * based on a skining name, out output will be send to the Writer
    *
    * @param template
    * @param rcontext
    * @param out
    * @throws Exception
    */
   public void render(String template, PortalRenderContext rcontext, Writer out) throws Exception {
      XsltRenderContext xrc = (XsltRenderContext) rcontext;

      if (template.equals("site")) {
         Document doc = xrc.produceDocument();
         writeDocument(doc, out);
      }
      else {
         xrc.getBaseContext().getRenderEngine().render(template, xrc.getBaseContext(), out);
      }
   }

   protected void writeDocument(Document doc, Writer out) {
      try {
         StreamResult outputTarget = new StreamResult(out);
         getTransformer().transform(new DOMSource(doc), outputTarget);
      }
      catch (TransformerException e) {
         throw new RuntimeException(e);
      }

   }

   public Transformer getTransformer() {
      try {
         Transformer trans = getTemplates().newTransformer();
         trans.setURIResolver(getServletResolver());
         return trans;
      }
      catch (TransformerConfigurationException e) {
         throw new RuntimeException(e);
      }
   }

   public Templates getTemplates() {
      return templates;
   }

   public void setTemplates(Templates templates) {
      this.templates = templates;
   }

   public URIResolver getServletResolver() {
      return servletResolver;
   }

   public void setServletResolver(URIResolver servletResolver) {
      this.servletResolver = servletResolver;
   }

   /**
    * prepare for a forward operation in the render engine, this might include
    * modifying the request attributes.
    *
    * @param req
    * @param res
    * @param p
    * @param skin
    */
   public void setupForward(HttpServletRequest req, HttpServletResponse res, Placement p, String skin) {
      getPortalService().getRenderEngine(Portal.DEFAULT_PORTAL_CONTEXT, req).setupForward(req, res, p, skin);
   }

   public PortalService getPortalService() {
      return portalService;
   }

   public void setPortalService(PortalService portalService) {
      this.portalService = portalService;
   }

   public ServletContext getServletContext() {
      return servletContext;
   }

   public void setServletContext(ServletContext servletContext) {
      this.servletContext = servletContext;
   }

   protected Templates createTemplate() 
      throws MalformedURLException, TransformerConfigurationException {
      InputStream stream = getServletContext().getResourceAsStream(
            getTransformerPath());
      URL url = getServletContext().getResource(getTransformerPath());
      String urlPath = url.toString();
      String systemId = urlPath.substring(0, urlPath.lastIndexOf('/') + 1);
      Templates templates = TransformerFactory.newInstance().newTemplates(
                     new StreamSource(stream, systemId));
      return templates;
   }

   public String getTransformerPath() {
      return transformerPath;
   }

   public void setTransformerPath(String transformerPath) {
      this.transformerPath = transformerPath;
   }
}

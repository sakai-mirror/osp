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
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.*;
import java.io.Writer;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import java.util.Iterator;
import java.util.Hashtable;

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
   private String defaultTransformerPath;

   private ServletContext servletContext;

   private Templates defaultTemplates;
   private URIResolver servletResolver;

   private Map<String, Templates> templates = new Hashtable<String, Templates>();
   private Map<String, String> transformerPaths;

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
         setDefaultTemplates(createTemplate());
         setupTemplates();
      } catch (MalformedURLException e) {
         log.error("unable to init portal transformation", e);
      } catch (TransformerConfigurationException e) {
         log.error("unable to init portal transformation", e);
      }

      setServletResolver(new ServletResourceUriResolver(getServletContext()));
   }

   protected void setupTemplates() throws MalformedURLException, TransformerConfigurationException {
      for (Iterator<Map.Entry<String, String>> i=getTransformerPaths().entrySet().iterator();
           i.hasNext();) {
         Map.Entry<String, String> entry = i.next();
         getTemplates().put(entry.getKey(), createTemplate(entry.getValue()));
      }
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
         writeDocument(doc, out, xrc.getAlternateTemplate());
      }
      else {
         xrc.getBaseContext().getRenderEngine().render(template, xrc.getBaseContext(), out);
      }
   }

   protected void writeDocument(Document doc, Writer out, String altTemplate) {
      try {
         StreamResult outputTarget = new StreamResult(out);

         Transformer transformer = getTransformer(altTemplate);
         transformer.transform(new DOMSource(doc), outputTarget);
      }
      catch (TransformerException e) {
         throw new RuntimeException(e);
      }

   }

   public Transformer getTransformer(String altTemplate) {
      try {
         Templates templates = null;
         
         if (altTemplate != null) {
            templates = getTemplates().get(altTemplate);
         }
         
         // test seperately in case the param wasnt' correct
         if (templates == null) {
            templates = getDefaultTemplates();
         }
         
         Transformer trans = templates.newTransformer();
         trans.setURIResolver(getServletResolver());
         return trans;
      }
      catch (TransformerConfigurationException e) {
         throw new RuntimeException(e);
      }
   }

   public Templates getDefaultTemplates() {
      return defaultTemplates;
   }

   public void setDefaultTemplates(Templates defaultTemplates) {
      this.defaultTemplates = defaultTemplates;
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
      String transformerPath = getDefaultTransformerPath();
      return createTemplate(transformerPath);
   }

   protected Templates createTemplate(String transformerPath) throws MalformedURLException, TransformerConfigurationException {
      InputStream stream = getServletContext().getResourceAsStream(
            transformerPath);
      URL url = getServletContext().getResource(transformerPath);
      String urlPath = url.toString();
      String systemId = urlPath.substring(0, urlPath.lastIndexOf('/') + 1);
      Templates templates = TransformerFactory.newInstance().newTemplates(
                     new StreamSource(stream, systemId));
      return templates;
   }

   public String getDefaultTransformerPath() {
      return defaultTransformerPath;
   }

   public void setDefaultTransformerPath(String defaultTransformerPath) {
      this.defaultTransformerPath = defaultTransformerPath;
   }

   public Map<String, Templates> getTemplates() {
      return templates;
   }

   public void setTemplates(Map<String, Templates> templates) {
      this.templates = templates;
   }

   public Map<String, String> getTransformerPaths() {
      return transformerPaths;
   }

   public void setTransformerPaths(Map<String, String> transformerPaths) {
      this.transformerPaths = transformerPaths;
   }
}

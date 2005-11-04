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
 * $Header: /opt/CVS/osp2.x/common/tool-lib/src/java/org/theospi/portfolio/shared/control/XmlElementView.java,v 1.1 2005/07/30 00:06:52 jellis Exp $
 * $Revision$
 * $Date$
 */
package org.theospi.portfolio.shared.control;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContextException;
import org.sakaiproject.metaobj.utils.mvc.impl.TemplateJstlView;
import org.sakaiproject.metaobj.utils.mvc.intf.VelocityEngineFactory;
import org.sakaiproject.metaobj.utils.xml.SchemaNode;
import org.sakaiproject.metaobj.shared.mgt.HomeFactory;
import org.sakaiproject.metaobj.shared.mgt.home.StructuredArtifactHomeInterface;
import org.sakaiproject.metaobj.shared.model.StructuredArtifact;
import org.sakaiproject.metaobj.shared.control.EditedArtifactStorage;
import org.sakaiproject.metaobj.shared.control.SchemaBean;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileWriter;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Apr 20, 2004
 * Time: 3:32:34 PM
 * To change this template use File | Settings | File Templates.
 */
public class XmlElementView extends TemplateJstlView {

   private String templateName = "";
   private VelocityEngine velocityEngine;
   private Template template;
   private String baseUrl = null;

   public void render(Map model, HttpServletRequest request, HttpServletResponse response) throws Exception {
      setBody(prepareTemplateForRendering(model, request, response));
      super.render(model, request, response);
   }

   /**
    * Prepare for rendering, and determine the request dispatcher path
    * to forward to respectively to include.
    * <p>This implementation simply returns the configured URL.
    * Subclasses can override this to determine a resource to render,
    * typically interpreting the URL in a different manner.
    *
    * @param request  current HTTP request
    * @param response current HTTP response
    * @return the request dispatcher path to use
    * @throws Exception if preparations failed
    * @see #getUrl
    * @see org.springframework.web.servlet.view.tiles.TilesView#prepareForRendering
    */
   protected String prepareTemplateForRendering(Map model, HttpServletRequest request, HttpServletResponse response) throws Exception {
      HomeFactory factory = (HomeFactory) getWebApplicationContext().getBean("homeFactory");

      SchemaNode schema = null;
      StructuredArtifactHomeInterface home = null;
      String schemaName = null;
      schemaName = request.getParameter("schema");
      if (schemaName == null) {

         schemaName = (String) request.getAttribute("schema");

         if (schemaName == null) {
            schemaName = request.getParameter("artifactType");
         }
      }

      home = (StructuredArtifactHomeInterface) factory.getHome(schemaName);

      if (request.getAttribute(EditedArtifactStorage.STORED_ARTIFACT_FLAG) != null) {
         EditedArtifactStorage sessionBean = (EditedArtifactStorage)request.getSession().getAttribute(
            EditedArtifactStorage.EDITED_ARTIFACT_STORAGE_SESSION_KEY);

         if (!(sessionBean.getCurrentElement() instanceof StructuredArtifact)) {
            home = (StructuredArtifactHomeInterface)sessionBean.getHome();
            schema = sessionBean.getCurrentSchemaNode();
            schemaName += "." + sessionBean.getCurrentPath();
         }
      }

      String baseFile = getBaseUrl() + "_" + schemaName;

      File genFile = new File(getWebApplicationContext().getServletContext().getRealPath(""),
         baseFile + ".jsp");
      File customFile = new File(getWebApplicationContext().getServletContext().getRealPath(""),
         baseFile + "_custom.jsp");

      if (customFile.exists()) {
         return baseFile + "_custom.jsp";
      }

      if (genFile.exists()) {
         if (genFile.lastModified() > home.getModified().getTime() &&
            genFile.lastModified() > getVelocityTemplate().getLastModified()) {
            return baseFile + ".jsp";
         }
      }

      return createJspFromTemplate(home, baseFile + ".jsp", genFile, schemaName, schema);
   }


   protected String createJspFromTemplate(StructuredArtifactHomeInterface home, String resultFile, File jspFile,
                                          String schemaName, SchemaNode schema) throws Exception {
      VelocityContext context = new VelocityContext();

      if (schema != null) {
         context.put("schema", new SchemaBean(schema, home.getRootNode(), null, home.getType().getDescription()));
      }
      else {
         context.put("schema", new SchemaBean(home.getRootNode(), home.getSchema(), schemaName, home.getType().getDescription()));
      }

      context.put("instruction", home.getInstruction());

      FileWriter output = new FileWriter(jspFile);

      getVelocityTemplate().merge(context, output);

      output.close();

      return resultFile;
   }


   /**
    * Invoked on startup. Looks for a single VelocityConfig bean to
    * find the relevant VelocityEngine for this factory.
    */
   protected void initApplicationContext() throws BeansException {
      super.initApplicationContext();

      if (this.velocityEngine == null) {
         try {
            VelocityEngineFactory velocityConfig = (VelocityEngineFactory)
               BeanFactoryUtils.beanOfTypeIncludingAncestors(getApplicationContext(),
                  VelocityEngineFactory.class, true, true);
            this.velocityEngine = velocityConfig.getVelocityEngine();
         } catch (NoSuchBeanDefinitionException ex) {
            throw new ApplicationContextException("Must define a single VelocityConfig bean in this web application " +
               "context (may be inherited): VelocityConfigurer is the usual implementation. " +
               "This bean may be given any name.", ex);
         }
      }

      try {
         // check that we can get the template, even if we might subsequently get it again
         this.template = getVelocityTemplate();
      } catch (ResourceNotFoundException ex) {
         throw new ApplicationContextException("Cannot find Velocity template for URL [" + getBaseUrl() +
            "]: Did you specify the correct resource loader path?", ex);
      } catch (Exception ex) {
         throw new ApplicationContextException("Cannot load Velocity template for URL [" + getBaseUrl() + "]", ex);
      }
   }

   /**
    * Retrieve the Velocity template.
    *
    * @return the Velocity template to process
    * @throws Exception if thrown by Velocity
    */
   protected Template getVelocityTemplate() throws Exception {
      return this.velocityEngine.getTemplate(templateName);
   }


   public String getTemplateName() {
      return templateName;
   }

   public void setTemplateName(String templateName) {
      this.templateName = templateName;
   }

   public String getBaseUrl() {
      return baseUrl;
   }

   public void setBaseUrl(String baseUrl) {
      this.baseUrl = baseUrl;
   }
}

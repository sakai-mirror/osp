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
 * $Header: /opt/CVS/osp2.x/presentation/tool/src/java/org/theospi/portfolio/presentation/control/PresentationPropertyFileView.java,v 1.2 2005/09/08 17:05:08 chmaurer Exp $
 * $Revision$
 * $Date$
 */
package org.theospi.portfolio.presentation.control;

import org.apache.velocity.VelocityContext;
import org.theospi.portfolio.presentation.model.Presentation;
import org.sakaiproject.metaobj.shared.control.SchemaBean;
import org.theospi.portfolio.shared.control.XmlElementView;
import org.theospi.portfolio.shared.model.Node;
import org.theospi.portfolio.shared.model.TechnicalMetadata;
import org.sakaiproject.metaobj.utils.xml.SchemaNode;

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
public class PresentationPropertyFileView extends XmlElementView {

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
   protected String prepareTemplateForRendering(Map model,HttpServletRequest request, HttpServletResponse response) throws Exception {
      Presentation presentation = (Presentation) model.get("presentation");
      TechnicalMetadata propertyFileMetadata = (TechnicalMetadata) model.get("propertyFileMetadata");

      String baseFile = getBaseUrl() + "_" + presentation.getTemplate().getId().toString();

      File genFile = new File(getWebApplicationContext().getServletContext().getRealPath(""),
         baseFile + ".jsp");
      File customFile = new File(getWebApplicationContext().getServletContext().getRealPath(""),
         baseFile + "_custom.jsp");

      if (customFile.exists()) {
         return baseFile + "_custom.jsp";
      }

      //update jsp only if xsd or velocity template has changed or genFile doesn't exist
      if (!genFile.exists() ||
            (genFile.lastModified() > propertyFileMetadata.getLastModified().getTime() &&
            genFile.lastModified() > getVelocityTemplate().getLastModified())) {
         return createJspFromTemplate(presentation, baseFile + ".jsp", genFile);
      }

      return baseFile + ".jsp";
   }


   protected String createJspFromTemplate(Presentation presentation, String resultFile,
                                          File jspFile) throws Exception {
      VelocityContext context = new VelocityContext();

      SchemaNode schema = presentation.getProperties().getCurrentSchema();
      context.put("schema",
         new SchemaBean(schema, presentation.getTemplate().getDocumentRoot(), null, presentation.getTemplate().getDescription()));

      FileWriter output = new FileWriter(jspFile);

      getVelocityTemplate().merge(context, output);

      output.close();

      return resultFile;
   }
}

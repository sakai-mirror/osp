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
 * $Header: /opt/CVS/osp2.x/presentation/tool/src/java/org/theospi/portfolio/presentation/control/TemplateBuilderController.java,v 1.1 2005/08/10 21:08:30 chmaurer Exp $
 * $Revision$
 * $Date$
 */
/*
 * $Header: /opt/CVS/osp2.x/presentation/tool/src/java/org/theospi/portfolio/presentation/control/TemplateBuilderController.java,v 1.1 2005/08/10 21:08:30 chmaurer Exp $
 * $Revision$
 * $Date$
 */
package org.theospi.portfolio.presentation.control;

import org.springframework.validation.Errors;
import org.springframework.web.servlet.ModelAndView;
import org.theospi.portfolio.presentation.model.PresentationItemDefinition;
import org.theospi.portfolio.presentation.model.PresentationTemplate;
import org.theospi.portfolio.presentation.model.TemplateFileRef;
//import org.theospi.portfolio.repository.model.FileArtifact;
import org.sakaiproject.metaobj.shared.mgt.HomeFactory;
import org.sakaiproject.metaobj.shared.mgt.ReadableObjectHome;
import org.sakaiproject.metaobj.shared.mgt.WritableObjectHome;
import org.sakaiproject.metaobj.shared.mgt.home.StructuredArtifactHomeInterface;
import org.sakaiproject.metaobj.shared.model.Id;
import org.sakaiproject.metaobj.shared.model.PersistenceException;
import org.sakaiproject.metaobj.utils.mvc.intf.LoadObjectController;
import org.sakaiproject.metaobj.utils.xml.SchemaNode;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

public class TemplateBuilderController extends AbstractPresentationController implements LoadObjectController{
   private WritableObjectHome fileArtifactHome;
   private HomeFactory homeFactory;

   public ModelAndView handleRequest(Object requestModel, Map request, Map session, Map application, Errors errors) {
      PresentationTemplate template = (PresentationTemplate) requestModel;
      try {
//    	TODO: 20050810 ContentHosting
         //FileArtifact artifact = (FileArtifact) getFileArtifactHome().load(template.getRenderer());
         //artifact.setSize(template.getMarkup().getBytes().length);
         //artifact.setFile(new ByteArrayInputStream(prepareBody(template.getMarkup()).getBytes()));
         //getFileArtifactHome().store(artifact);
         getPresentationManager().storeTemplate(template);
         Map params = new HashMap();
         params.put("id", template.getId().getValue());
         params.put("_target1","1");
         params.put("formSubmission","true");
         return new ModelAndView("success", params);
      } catch (PersistenceException e) {
         //TODO is this right ?
         errors.reject("markup", e.getMessage());
         return new ModelAndView("failure");
      }
   }

    public ModelAndView processCancel(Map request, Map session, Map application,
                                      Object command, Errors errors) throws Exception {
        PresentationTemplate template = (PresentationTemplate) command;
        Map params = new HashMap();
        params.put("id", template.getId().getValue());
        params.put("_target1","1");
        params.put("formSubmission","true");
        return new ModelAndView("success", params);
    }

   protected String prepareBody(String body){
      StringBuffer buffer = new StringBuffer();
      buffer.append("<?xml version=\"1.0\" ?>\n" +
            "<xsl:stylesheet version=\"1.0\" xmlns:xsl=\"http://www.w3.org/1999/XSL/Transform\">\n" +
            "\t\n\t<xsl:template match=\"ospiPresentation\">");
      buffer.append(body.replaceAll("\\$\\{(.*?)\\}","<xsl:copy-of select=\"$1\"/>"));
      buffer.append("\t</xsl:template>\n\n</xsl:stylesheet>");
      return buffer.toString();
   }

   public Object fillBackingObject(Object incomingModel, Map request, Map session, Map application) throws Exception {
      PresentationTemplate template = (PresentationTemplate) incomingModel;
      template = getPresentationManager().getPresentationTemplate(template.getId());

      Map elements = new HashMap();

      for (Iterator i=template.getSortedItems().iterator();i.hasNext();){
         PresentationItemDefinition itemDef = (PresentationItemDefinition) i.next();
         elements.put(itemDef.getName(),findPaths(itemDef));
      }

      request.put("elements",elements);

      Map images = new HashMap();

      for (Iterator i=template.getFiles().iterator();i.hasNext();){
         TemplateFileRef fileRef = (TemplateFileRef) i.next();
//       TODO: 20050810 ContentHosting
         //if (fileRef.getFileArtifact() instanceof FileArtifact){
         //   FileArtifact fileArtifact = (FileArtifact)fileRef.getFileArtifact();
         //   if (fileArtifact.getMimeType().getPrimaryType().equals("image")){
         //      images.put(fileArtifact.getDisplayName(),fileArtifact.getExternalUri());
         //   }
         //}
      }

      session.put("images",images);

      return template;
   }

   /**
    * places ${ } around each path
    * @param paths
    * @return
    */
   protected Collection tagPaths(Collection paths){
      Collection taggedPaths = new ArrayList();
      for (Iterator i=paths.iterator();i.hasNext();){
         taggedPaths.add("${" + i.next() + "}");
      }
      return taggedPaths;
   }

   /**
    * creates collection of possible xpaths associated with the rendered xml for this item definition
    * @param itemDef
    * @return
    */
   protected Collection findPaths(PresentationItemDefinition itemDef){
      ReadableObjectHome home = getHomeFactory().getHome(itemDef.getType());
      Collection paths = new ArrayList();

      paths.add(itemDef.getName() + "/artifact/metaData/id");
      paths.add(itemDef.getName() + "/artifact/metaData/displayName");
      paths.add(itemDef.getName() + "/artifact/metaData/type/id");
      paths.add(itemDef.getName() + "/artifact/metaData/type/description");

      if (home instanceof StructuredArtifactHomeInterface){
         StructuredArtifactHomeInterface structuredArtifactHome = (StructuredArtifactHomeInterface) home;
         addPath(paths, structuredArtifactHome.getRootSchema(),itemDef.getName() + "/artifact/structuredData");
      } else if (home != null && home.getType().getId().equals(getFileArtifactHome().getType().getId())){
         //TODO deal with technical metadata
         paths.add(itemDef.getName() + "/artifact/fileArtifact/uri");
      }
      return tagPaths(paths);
   }

   /**
    * recursively finds all possible xpaths for given schema
    * @param paths
    * @param node
    * @param parent
    */
   protected void addPath(Collection paths, SchemaNode node, String parent){
      String path = parent + "/" + node.getName();
      paths.add(path);
      if (node.getChildren() != null && node.getChildren().size() > 0){
         for (Iterator i= node.getChildren().iterator();i.hasNext();){
            addPath(paths, (SchemaNode)i.next(), path);
         }
      }
   }

   /**
    * loads contents of a file into a string
    * @param fileId
    * @return
    */
   protected String loadContents(Id fileId) throws IOException, PersistenceException {
//	 TODO: 20050810 ContentHosting
	   //FileArtifact artifact = (FileArtifact)getFileArtifactHome().load(fileId);
      //BufferedReader reader = new BufferedReader(new InputStreamReader(artifact.getFile()));
      StringBuffer buffer = new StringBuffer();
      //String line;
      //while ((line = reader.readLine()) != null){
      //   if (line == null) break;
      //   buffer.append(line + "\n");
      //}
      return buffer.toString();
   }

   public WritableObjectHome getFileArtifactHome() {
      return fileArtifactHome;
   }

   public void setFileArtifactHome(WritableObjectHome fileArtifactHome) {
      this.fileArtifactHome = fileArtifactHome;
   }

   public HomeFactory getHomeFactory() {
      return homeFactory;
   }

   public void setHomeFactory(HomeFactory homeFactory) {
      this.homeFactory = homeFactory;
   }
}


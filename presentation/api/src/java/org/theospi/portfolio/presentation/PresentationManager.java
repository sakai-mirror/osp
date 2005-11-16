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
 * $Header: /opt/CVS/osp2.x/presentation/api/src/java/org/theospi/portfolio/presentation/PresentationManager.java,v 1.5 2005/10/26 23:53:01 jellis Exp $
 * $Revision$
 * $Date$
 */
package org.theospi.portfolio.presentation;

import org.theospi.portfolio.presentation.model.Presentation;
import org.theospi.portfolio.presentation.model.PresentationComment;
import org.theospi.portfolio.presentation.model.PresentationItem;
import org.theospi.portfolio.presentation.model.PresentationItemDefinition;
import org.theospi.portfolio.presentation.model.PresentationLayout;
import org.theospi.portfolio.presentation.model.PresentationLog;
import org.theospi.portfolio.presentation.model.PresentationPage;
import org.theospi.portfolio.presentation.model.PresentationTemplate;
import org.theospi.portfolio.presentation.model.TemplateFileRef;
import org.theospi.portfolio.shared.model.Node;
import org.theospi.portfolio.security.model.CleanupableService;
import org.jdom.Document;
import org.jdom.Element;
import org.sakaiproject.metaobj.shared.model.Agent;
import org.sakaiproject.metaobj.shared.model.Id;
import org.sakaiproject.service.legacy.entity.Reference;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collection;
import java.util.List;

/**
 * This class provides a management layer into the presentations included in the system.
 * @author John Bush (jbush@rsmart.com)
 * $Header: /opt/CVS/osp2.x/presentation/api/src/java/org/theospi/portfolio/presentation/PresentationManager.java,v 1.5 2005/10/26 23:53:01 jellis Exp $
 * $Revision$
 * $Date$
 */

public interface PresentationManager extends CleanupableService {

   public PresentationTemplate storeTemplate(PresentationTemplate template);

   public void deletePresentationTemplate(Id id);
   public void deletePresentationLayout(Id id);

   public PresentationTemplate getPresentationTemplate(Id id);

   public PresentationItemDefinition getPresentationItemDefinition(Id item);

   public void deletePresentationItem(Id item);

   public Presentation getPresentation(Id id);

   public Presentation storePresentation(Presentation presentation);

   public void deletePresentation(Id presentation);

   public PresentationItem getPresentationItem(Id itemDef);

   public void updateItemDefintion(PresentationItemDefinition itemDef);

   public void deletePresentationItemDefinition(Id itemDef);

   public TemplateFileRef getTemplateFileRef(Id refId);
   public void updateTemplateFileRef(TemplateFileRef ref);
   public void deleteTemplateFileRef(Id refId);

   /**
    * returns a list of all presentation owned by agent.
    *
    * @param owner
    * @return
    */
   public Collection findPresentationsByOwner(Agent owner);

   /**
    * returns a list of all presentation owned by agent within the given toolId.
    *
    * @param owner
    * @return
    */
   public Collection findPresentationsByOwner(Agent owner, String toolId);


   /**
    * returns a list of all presentation templates owned by agent.
    *
    * @param owner
    * @return
    */
   public Collection findTemplatesByOwner(Agent owner);

   /**
    * returns a list of all presentation templates owned by agent within the given siteId.
    *
    * @param owner
    * @return
    */
   public Collection findTemplatesByOwner(Agent owner, String siteId);

   public Collection findPublishedTemplates(String siteId);

   public Collection findPublishedTemplates();
   
   public Collection findPublishedLayouts(String siteId);
   
   public Collection findLayoutsByOwner(Agent owner, String siteId);
   
   public PresentationLayout storeLayout(PresentationLayout layout);
   
   public PresentationLayout getPresentationLayout(Id layoutId);
   
   public PresentationPage getPresentationPage(Id id);
   public Document getPresentationPageAsXml(Presentation presentation);

   /**
    * returns a list of all presentations agent can view </br>
    *
    * @param viewer
    * @return
    */
   public Collection findPresentationsByViewer(Agent viewer);

   /**
    * returns a list of all presentations agent can view within the given tool</br>
    *
    * @param viewer
    * @return
    */
   public Collection findPresentationsByViewer(Agent viewer, String toolId);

   public void createComment(PresentationComment comment);

   public List getPresentationComments(Id presentationId, Agent viewer);

   public PresentationComment getPresentationComment(Id id);

   public void deletePresentationComment(PresentationComment comment);

   public void updatePresentationComment(PresentationComment oldComment);

   /**
     * returns list of comments owned by agent in given tool.  Includes comments created by the owner.
     * @param owner
     * @param sortBy
     * @return
     */
   public List getOwnerComments(Agent owner, CommentSortBy sortBy);

   /**
    * returns list of comments owned by agent in given tool.  Includes comments created by the owner.
    * @param owner
    * @param toolId
    * @param sortBy
    * @param excludeOwner - set to true to exclude comments created by the owner
    * @return
    */
   public List getOwnerComments(Agent owner, String toolId, CommentSortBy sortBy, boolean excludeOwner);

   /**
    * returns list of comments owned by agent in given tool.  Includes comments created by the owner.
    * @param owner
    * @param toolId
    * @param sortBy
    * @return
    */
   public List getOwnerComments(Agent owner, String toolId, CommentSortBy sortBy);

   public List getCreatorComments(Agent creator, CommentSortBy sortBy);

   /**
    * returns list of comments created by creator in given tool.
    * @param creator
    * @param toolId
    * @param sortBy
    * @return
    */
   public List getCreatorComments(Agent creator, String toolId, CommentSortBy sortBy);

   public PresentationTemplate copyTemplate(Id templateId);

   public void packageTemplateForExport(Id templateId, OutputStream os) throws IOException;

   public PresentationTemplate uploadTemplate(String templateFileName, String toolId, InputStream zipFileStream) throws IOException;

   public void storePresentationLog(PresentationLog log);

   public Collection findLogsByPresID(Id presID);

   public Collection getPresentationItems(Id artifactId);

   public Collection getPresentationsBasedOnTemplateFileRef(Id artifactId);

   public Collection findPresentationsByTool(Id id);
   
   public Node getNode(Id artifactId);

   public Node getNode(Reference ref);

   public Collection loadArtifactsForItemDef(PresentationItemDefinition itemDef, Agent agent);
   
   public Document createDocument(Presentation presentation);
}

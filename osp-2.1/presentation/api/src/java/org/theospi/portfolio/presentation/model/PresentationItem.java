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
 * $Header: /opt/CVS/osp2.x/presentation/api/src/java/org/theospi/portfolio/presentation/model/PresentationItem.java,v 1.1 2005/08/10 21:08:30 chmaurer Exp $
 * $Revision$
 * $Date$
 */
/*
 * $Header: /opt/CVS/osp2.x/presentation/api/src/java/org/theospi/portfolio/presentation/model/PresentationItem.java,v 1.1 2005/08/10 21:08:30 chmaurer Exp $
 * $Revision$
 * $Date$
 */
package org.theospi.portfolio.presentation.model;

import org.sakaiproject.metaobj.shared.mgt.HomeFactory;
import org.sakaiproject.metaobj.shared.mgt.ReadableObjectHome;
import org.sakaiproject.metaobj.shared.model.Artifact;
import org.sakaiproject.metaobj.shared.model.Id;
import org.sakaiproject.metaobj.shared.model.IdentifiableObject;
import org.sakaiproject.metaobj.shared.model.PersistenceException;
import org.sakaiproject.service.framework.component.cover.ComponentManager;
import org.apache.commons.codec.digest.DigestUtils;

public class PresentationItem extends IdentifiableObject {
   private PresentationItemDefinition definition;
   private Id artifactId;

   public Artifact getArtifact() throws PersistenceException {
      ReadableObjectHome home = getHomeFactory().getHome(definition.getType());
      return (Artifact) home.load(artifactId);
   }

   public void setArtifact(Artifact artifact) {
      artifactId = artifact.getId();
   }

   public void setDefinition(PresentationItemDefinition definition) {
      this.definition = definition;
   }

   public PresentationItemDefinition getDefinition() {
      return definition;
   }

   public Id getArtifactId() {
      return artifactId;
   }

   public int hashCode() {
      if (getId() != null){
         return getId().hashCode();
      }
      return (artifactId != null) ?
         DigestUtils.md5Hex(artifactId.getValue() + getHashDefId()).hashCode() : 0;
   }

   protected String getHashDefId() {
      if (definition == null) {
         return "";
      }
      else if (definition.getId() == null) {
         return "";
      }
      else {
         return definition.getId().getValue();
      }
   }

   public void setArtifactId(Id artifactId) {
      this.artifactId = artifactId;
   }

   public HomeFactory getHomeFactory() {
      return (HomeFactory) ComponentManager.getInstance().get("homeFactory");
   }

}

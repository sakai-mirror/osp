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
 * $Header: /opt/CVS/osp2.x/common/api/src/java/org/theospi/portfolio/security/model/PermissionsEdit.java,v 1.1.1.1 2005/07/14 15:53:41 jellis Exp $
 * $Revision$
 * $Date$
 */
package org.theospi.portfolio.security.model;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.metaobj.shared.model.Id;

import java.util.List;

public class PermissionsEdit {
   protected final transient Log logger = LogFactory.getLog(getClass());

   private String name = null;
   private Id qualifier = null;
   private List permissions = null;
   private String siteId;

   public PermissionsEdit() {
   }

   public String getSiteId() {
      return siteId;
   }

   public void setSiteId(String siteId) {
      this.siteId = siteId;
   }

   public PermissionsEdit(String name, Id qualifier, String siteId, List permissions) {
      this.name = name;
      this.permissions = permissions;
      this.qualifier = qualifier;
      this.siteId = siteId;
   }

   public String getName() {
      return name;
   }

   public void setName(String name) {
      this.name = name;
   }

   public List getPermissions() {
      return permissions;
   }

   public void setPermissions(List permissions) {
      this.permissions = permissions;
   }

   public Id getQualifier() {
      return qualifier;
   }

   public void setQualifier(Id qualifier) {
      this.qualifier = qualifier;
   }

   public Object clone() {
      return new PermissionsEdit(getName(), getQualifier(), getSiteId(), getPermissions());
   }
}

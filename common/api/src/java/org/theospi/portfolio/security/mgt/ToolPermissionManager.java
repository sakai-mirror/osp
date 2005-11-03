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
 * $Header: /opt/CVS/osp2.x/common/api/src/java/org/theospi/portfolio/security/mgt/ToolPermissionManager.java,v 1.1.1.1 2005/07/14 15:53:41 jellis Exp $
 * $Revision$
 * $Date$
 */
package org.theospi.portfolio.security.mgt;

import org.sakaiproject.service.legacy.site.ToolConfiguration;
import org.theospi.portfolio.security.model.PermissionsEdit;

import java.util.List;

public interface ToolPermissionManager {

   /**
    * Get a list of functions that this tool is interested in setting.
    * This list should be in some reasonable order (read to delete, etc).
    * @param edit contains information about the permissions edit such as
    * qualifier, etc.
    * @return list of strings that name the functions in some reasonable order
    */
   public List getFunctions(PermissionsEdit edit);

   /**
    * This method is called to see if the qualifier being edited
    * has some parent qualifiers that imply permissions for this qualifier.
    * One example might be a directory that has implied permissions of
    * the parent directory.  Since the permissions are implied, the
    * set permissions screen will not allow these permissions to be turned off.
    * @param edit contains information about the permissions edit such as
    * qualifier, etc.
    * @return list of Id objects that are parents of the passed in qualifier.
    */
   public List getReadOnlyQualifiers(PermissionsEdit edit);

   public void duplicatePermissions(ToolConfiguration fromTool, ToolConfiguration toTool);

}

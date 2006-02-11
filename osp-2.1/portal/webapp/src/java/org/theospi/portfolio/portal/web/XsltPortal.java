/**********************************************************************************
* $URL$
* $Id$
***********************************************************************************
*
* Copyright (c) 2006 The Sakai Foundation.
*
* Licensed under the Educational Community License, Version 1.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*      http://www.opensource.org/licenses/ecl1.php
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*
**********************************************************************************/
package org.theospi.portfolio.portal.web;

import org.sakaiproject.portal.charon.CharonPortal;
import org.sakaiproject.api.kernel.session.Session;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;
import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Feb 9, 2006
 * Time: 2:21:33 PM
 * To change this template use File | Settings | File Templates.
 */
public class XsltPortal extends CharonPortal {

   protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
      super.doGet(req, res);
   }

}

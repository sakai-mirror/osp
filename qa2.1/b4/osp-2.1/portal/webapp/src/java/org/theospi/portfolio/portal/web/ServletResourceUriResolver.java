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

import javax.xml.transform.URIResolver;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import javax.xml.transform.stream.StreamSource;
import javax.servlet.ServletContext;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Feb 22, 2006
 * Time: 10:08:45 PM
 * To change this template use File | Settings | File Templates.
 */
public class ServletResourceUriResolver implements URIResolver {

   private ServletContext context;

   public ServletResourceUriResolver(ServletContext context) {
      this.context = context;
   }

   public Source resolve(String href, String base) throws TransformerException {
      return new StreamSource(context.getResourceAsStream(href));
   }
}

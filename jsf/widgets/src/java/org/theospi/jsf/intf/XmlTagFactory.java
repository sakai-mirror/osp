/**********************************************************************************
* $URL:https://source.sakaiproject.org/svn/osp/trunk/jsf/widgets/src/java/org/theospi/jsf/intf/XmlTagFactory.java $
* $Id:XmlTagFactory.java 9134 2006-05-08 20:28:42Z chmaurer@iupui.edu $
***********************************************************************************
*
 * Copyright (c) 2005, 2006 Sakai Foundation
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.osedu.org/licenses/ECL-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*
**********************************************************************************/
package org.theospi.jsf.intf;


/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Dec 29, 2005
 * Time: 10:08:03 AM
 * To change this template use File | Settings | File Templates.
 */
public interface XmlTagFactory {

   public XmlTagHandler getDefaultHandler();

   public XmlTagHandler getHandler(String uri, String localName, String qName);

}

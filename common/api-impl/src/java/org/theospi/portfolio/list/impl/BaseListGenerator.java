/**********************************************************************************
* $URL:https://source.sakaiproject.org/svn/osp/trunk/common/api-impl/src/java/org/theospi/portfolio/list/impl/BaseListGenerator.java $
* $Id:BaseListGenerator.java 9134 2006-05-08 20:28:42Z chmaurer@iupui.edu $
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
package org.theospi.portfolio.list.impl;

import org.theospi.portfolio.list.intf.CustomLinkListGenerator;
import org.theospi.portfolio.list.intf.ListGenerator;
import org.theospi.portfolio.list.intf.ListService;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Feb 10, 2006
 * Time: 3:15:45 PM
 * To change this template use File | Settings | File Templates.
 */
public abstract class BaseListGenerator implements CustomLinkListGenerator {
   private String listGeneratorId;
   private ListService listService;
   private ListGenerator listGenerator;


   public void init()
   {
       listService.register(listGeneratorId, listGenerator);  
   }

    public String getListGeneratorId() {
        return listGeneratorId;
    }

    public void setListGeneratorId(String listGeneratorId) {
        this.listGeneratorId = listGeneratorId;
    }

    public ListService getListService() {
        return listService;
    }

    public void setListService(ListService listService) {
        this.listService = listService;
    }

    public ListGenerator getListGenerator() {
        return listGenerator;
    }

    public void setListGenerator(ListGenerator listGenerator) {
        this.listGenerator = listGenerator;
    }
}

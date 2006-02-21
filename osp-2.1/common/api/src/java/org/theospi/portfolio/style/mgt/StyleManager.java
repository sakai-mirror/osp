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

package org.theospi.portfolio.style.mgt;

import java.util.Collection;

import org.sakaiproject.metaobj.shared.model.Agent;
import org.sakaiproject.metaobj.shared.model.Id;
import org.sakaiproject.service.legacy.entity.Reference;
import org.springframework.dao.DataIntegrityViolationException;
import org.theospi.portfolio.shared.model.Node;
import org.theospi.portfolio.style.model.Style;

public interface StyleManager {

   public Node getNode(Id artifactId);
   public Node getNode(Reference ref);
   
   public Style storeStyle(Style style);
   public Style getStyle(Id styleId);
   public void deleteStyle(final Id styleId) throws DataIntegrityViolationException;
   public Style getLightWeightStyle(final Id styleId);
   public Collection findPublishedStyles(String currentWorksiteId);
   public Collection findPublishedStyles();
   public Collection findStylesByOwner(Agent owner, String siteId);
   public Collection findGlobalStyles(Agent agent);
   
   public boolean isGlobal();
}

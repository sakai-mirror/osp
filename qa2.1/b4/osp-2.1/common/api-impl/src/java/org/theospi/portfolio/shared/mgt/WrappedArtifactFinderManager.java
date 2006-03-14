/**********************************************************************************
* $URL$
* $Id$
***********************************************************************************
*
* Copyright (c) 2005, 2006 The Sakai Foundation.
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
package org.theospi.portfolio.shared.mgt;

import org.sakaiproject.metaobj.shared.ArtifactFinderManager;
import org.sakaiproject.metaobj.shared.ArtifactFinder;

import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Nov 7, 2005
 * Time: 6:36:26 PM
 * To change this template use File | Settings | File Templates.
 */
public class WrappedArtifactFinderManager implements ArtifactFinderManager {

   private ArtifactFinderManager base;
   private Map substitutions;

   public ArtifactFinder getArtifactFinderByType(String key) {
      ArtifactFinder finder = (ArtifactFinder) substitutions.get(key);

      if (finder != null) {
         return finder;
      }
      return base.getArtifactFinderByType(key);
   }

   public Map getFinders() {
      return base.getFinders();
   }

   public Map getSubstitutions() {
      return substitutions;
   }

   public void setSubstitutions(Map substitutions) {
      this.substitutions = substitutions;
   }

   public ArtifactFinderManager getBase() {
      return base;
   }

   public void setBase(ArtifactFinderManager base) {
      this.base = base;
   }
}

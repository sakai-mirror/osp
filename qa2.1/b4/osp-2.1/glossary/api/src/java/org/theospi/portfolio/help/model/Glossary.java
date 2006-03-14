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
package org.theospi.portfolio.help.model;

import org.sakaiproject.metaobj.shared.model.Id;
import org.sakaiproject.service.legacy.resource.DuplicatableToolService;

import java.util.Collection;
import java.util.Set;

public interface Glossary extends DuplicatableToolService {
   public GlossaryEntry load(Id id);
           
   /**
    * find the keyword in the glossary.
    * return null if not found.
    * @param keyword
    * @return
    */
   public GlossaryEntry find(String keyword, String worksite);
   /**
    * returns the list of all GlossaryEntries
    * @return
    */
   public Collection findAll(String keyword, String worksite);

   public Collection findAll(String worksite);

   public Collection findAll();

   public Collection findAllGlobal();

   /**
    * url to glossary web page
    * @return
    */
   public String getUrl();

   public GlossaryEntry addEntry(GlossaryEntry newEntry);

   public void removeEntry(GlossaryEntry entry);

   public void updateEntry(GlossaryEntry entry);

   public boolean isPhraseStart(String phraseFragment, String worksite);

   public Set getSortedWorksiteTerms(String siteId);
}

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
package org.theospi.portfolio.help;

import net.sf.hibernate.HibernateException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.orm.hibernate.support.HibernateDaoSupport;
import org.theospi.portfolio.help.model.GlossaryEntry;
import org.theospi.portfolio.help.model.Glossary;
import org.theospi.portfolio.help.model.HelpFunctionConstants;
import org.theospi.portfolio.help.model.HelpManager;
import org.theospi.portfolio.security.AuthorizationFacade;
import org.theospi.portfolio.security.AuthorizationFailedException;
import org.sakaiproject.service.legacy.site.ToolConfiguration;
import org.sakaiproject.service.framework.portal.PortalService;
import org.sakaiproject.metaobj.shared.mgt.IdManager;
import org.sakaiproject.metaobj.shared.model.PersistenceException;
import org.sakaiproject.metaobj.shared.model.Id;
import org.sakaiproject.metaobj.worksite.mgt.WorksiteManager;
import org.sakaiproject.api.kernel.tool.Placement;
import org.sakaiproject.api.kernel.tool.ToolManager;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import java.util.Properties;

/**
 * This implementation uses the spring config to configure the system, and
 * uses as a database for indexing resources, and configuring which contexts
 * are associated with what resources.  Lucene is also responsible for
 * performing help searches.
 *
 * <br/><br/>
 *
 * Contexts are mapped to views in the spring config.  To do this, define
 * a bean of type, org.theospi.portfolio.help.model.HelpContextConfig.
 * Create a map of contexts which are keyed by the view name.  Contexts are
 * just string ids.  An example:
 * <br/><br/>
 *   &lt;bean id="presentationHelpContexts" class="org.theospi.portfolio.help.model.HelpContextConfig"&gt;<br/>
 *     &lt;constructor-arg&gt;<br/>
 *        &lt;map&gt;<br/>
 *          &lt;entry key="addPresentation1"&gt;<br/>
 *              &lt;list&gt;                          <br/>
 *                 &lt;value&gt;Creating a Presentation&lt;/value&gt;<br/>
 *              &lt;/list&gt;                                             <br/>
 *           &lt;/entry&gt;                                                    <br/>
 *  ...
 *  <br/><br/>
 * An explanation: what this means is that when a user navigates to the
 * addPresentation1 view a context called "Creating a Presentation" is created.
 * This context is just an identifier for possible actions the user might perform
 * from this page.
 *  <br/><br/>
 * To create resources define a bean of type, org.theospi.portfolio.help.model.Resource.
 * The name is the display name that is shown on jsp pages.  The location is
 * the url of the resource.  Configure all contexts associated with this resource.
 * An example,
 *   <br/><br/>
 *    &lt;bean id="pres_resource_2" class="org.theospi.portfolio.help.model.Resource"&gt; <br/>
 *     &lt;property name="name"&gt;&lt;value&gt;Creating a Presentation&lt;/value&gt;&lt;/property&gt;   <br/>
 *     &lt;property name="location"&gt;&lt;value&gt;${system.baseUrl}/help/creatingPresentations.html&lt;/value&gt;&lt;/property&gt;<br/>
 *     &lt;property name="contexts"&gt;<br/>
 *        &lt;list&gt;<br/>
 *           &lt;value&gt;Creating a Presentation&lt;/value&gt;<br/>
 *        &lt;/list&gt;<br/>
 *     &lt;/property&gt;<br/>
 *  &lt;/bean&gt;<br/>
 * <br/><br/>
 * If all this is configured correctly, when a user navigates to the addPresentation1
 * view a context of "Creating a Presentation" is created.  If the user navigates
 * to help, the user will be presented with links to all the resources associated with
 * this context.
 * <br/><br/>
 *
 * @see org.theospi.portfolio.help.model.Resource
 * @see org.theospi.portfolio.help.model.Source
 *
 */
public class HelpManagerImpl extends HibernateDaoSupport
   implements HelpManager, HelpFunctionConstants {

   protected final Log logger = LogFactory.getLog(getClass());
   private boolean initialized = false;
   private Glossary glossary;
   private IdManager idManager;
   private AuthorizationFacade authzManager;
   private WorksiteManager worksiteManager;
   private PortalService portalService;
   private ToolManager toolManager;

   public GlossaryEntry searchGlossary(String keyword) {
      return getGlossary().find(keyword, portalService.getCurrentSiteId());
   }

   public boolean isPhraseStart(String phraseFragment) {
      return getGlossary().isPhraseStart(phraseFragment, portalService.getCurrentSiteId());
   }
  
   public void setIdManager(IdManager idManager){
      this.idManager = idManager;
   }

   public Glossary getGlossary() {
      return glossary;
   }

   public void setGlossary(Glossary glossary) {
      this.glossary = glossary;
   }

   public GlossaryEntry addEntry(GlossaryEntry newEntry) {
		if (isGlobal()) {
			//Prepare for Global add
			 getAuthzManager().checkPermission(ADD_TERM,
			    idManager.getId(GLOBAL_GLOSSARY_QUALIFIER));
			 newEntry.setWorksiteId(null);
		} else {
			//Prepare for Local add
			 getAuthzManager().checkPermission(ADD_TERM,
			    getToolId());
			
			 newEntry.setWorksiteId(getWorksiteManager().getCurrentWorksiteId().getValue());
		}

      if (entryExists(newEntry)) {
         throw new PersistenceException("Glossary term {0} already defined.",
            new Object[]{newEntry.getTerm()}, "term");
      }

		return getGlossary().addEntry(newEntry);
   }

   public void removeEntry(GlossaryEntry entry) {
      if (isGlobal()) {
         getAuthzManager().checkPermission(DELETE_TERM,
            idManager.getId(GLOBAL_GLOSSARY_QUALIFIER));
         getGlossary().removeEntry(entry);
      }
      else {
         getAuthzManager().checkPermission(DELETE_TERM,
            getToolId());

         if (entry.getWorksiteId().equals(getWorksiteManager().getCurrentWorksiteId().getValue())) {
            getGlossary().removeEntry(entry);
         }
         else {
            throw new AuthorizationFailedException("Unable to update from another worksite");
         }
      }
   }

   public void updateEntry(GlossaryEntry entry) {
      if (isGlobal()) {
         getAuthzManager().checkPermission(EDIT_TERM,
            idManager.getId(GLOBAL_GLOSSARY_QUALIFIER));
         entry.setWorksiteId(null);
      }
      else {
         getAuthzManager().checkPermission(EDIT_TERM,
            getToolId());

         if (!entry.getWorksiteId().equals(getWorksiteManager().getCurrentWorksiteId().getValue())) {
            throw new AuthorizationFailedException("Unable to update from another worksite");
         }
      }
      if (entryExists(entry)) {
         throw new PersistenceException("Glossary term {0} already defined.",
            new Object[]{entry.getTerm()}, "term");
      }
      getGlossary().updateEntry(entry);
   }

   public boolean isMaintainer(){
      return getAuthzManager().isAuthorized(WorksiteManager.WORKSITE_MAINTAIN,
         idManager.getId(portalService.getCurrentSiteId()));
   }

   public Collection getWorksiteTerms() {
      if (isGlobal()) {
         return getGlossary().findAllGlobal();
      }
      else {
         return getGlossary().findAll(getWorksiteManager().getCurrentWorksiteId().getValue());
      }
   }

   public boolean isGlobal() {
      Placement placement = toolManager.getCurrentPlacement();
      Properties placementProps = placement.getPlacementConfig();

      String isGlobal = null;

      if (placementProps != null) {
         isGlobal = placementProps.getProperty(TOOL_GLOBAL_GLOSSARY);
      }

      if (isGlobal == null) {
         return false;
      }
      else {
         return (isGlobal.equals("true"));
      }
   }
   
   protected boolean entryExists(GlossaryEntry entry){
      Collection entryFound = getGlossary().findAll(entry.getTerm(), portalService.getCurrentSiteId());
		for (Iterator i = entryFound.iterator();i.hasNext();){
			GlossaryEntry entryIter = (GlossaryEntry)i.next();
			String entryWID = entryIter.getWorksiteId();

         if (entryIter.getId().equals(entry.getId())) {
            continue;
         }
         else if (entryWID == null && isGlobal()) {
            return true;
         }
         else if (entryWID != null) {
            return true;
         }
		}
		
		return false;
		
   }
		
   protected Id getToolId() {
      Placement placement = toolManager.getCurrentPlacement();
      return idManager.getId(placement.getId());
   }

   public AuthorizationFacade getAuthzManager() {
      return authzManager;
   }

   public void setAuthzManager(AuthorizationFacade authzManager) {
      this.authzManager = authzManager;
   }

   public WorksiteManager getWorksiteManager() {
      return worksiteManager;
   }

   public void setWorksiteManager(WorksiteManager worksiteManager) {
      this.worksiteManager = worksiteManager;
   }
   
   public void removeFromSession(Object obj) {
      this.getHibernateTemplate().evict(obj);
      try {
         getHibernateTemplate().getSessionFactory().evict(obj.getClass());
      } catch (HibernateException e) {
         logger.error(e);
      }
   }

   public Set getSortedWorksiteTerms() {
      return getGlossary().getSortedWorksiteTerms(portalService.getCurrentSiteId());
   }

   public PortalService getPortalService() {
      return portalService;
   }

   public void setPortalService(PortalService portalService) {
      this.portalService = portalService;
   }

   public ToolManager getToolManager() {
      return toolManager;
   }

   public void setToolManager(ToolManager toolManager) {
      this.toolManager = toolManager;
   }

}

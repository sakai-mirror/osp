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
 * $Header: /opt/CVS/osp2.x/glossary/api-impl/src/java/org/theospi/portfolio/help/HelpManagerImpl.java,v 1.3 2005/07/18 16:57:49 jellis Exp $
 * $Revision$
 * $Date$
 */
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

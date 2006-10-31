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

import java.util.Collection;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.HibernateException;
import org.sakaiproject.event.api.Event;
import org.sakaiproject.event.cover.EventTrackingService;
import org.sakaiproject.metaobj.shared.mgt.IdManager;
import org.sakaiproject.metaobj.shared.model.Id;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import org.springframework.transaction.support.TransactionSynchronizationAdapter;
import org.springframework.transaction.support.TransactionSynchronizationManager;

public class DbGlossary  extends HibernateDaoSupport implements Glossary, Observer {
   protected final transient Log logger = LogFactory.getLog(getClass());
   private Map worksiteGlossary = new Hashtable();
   private IdManager idManager;

   private String url;

   private static final String EVENT_UPDATE_ADD = "org.theospi.glossary.updateAdd";
   private static final String EVENT_DELETE = "org.theospi.glossary.delete";

   public GlossaryEntry load(Id id) {
      GlossaryEntry entry = load(id, true);
      getHibernateTemplate().evict(entry);
      return entry;
   }

   protected GlossaryEntry load(Id id, boolean deep) {
      try {
         GlossaryEntry entry = (GlossaryEntry)getSession().load(GlossaryEntry.class, id);
         if (deep) {
            entry.setLongDescriptionObject(loadDescription(id));
         }
         return entry;
      } catch (HibernateException e) {
         logger.warn("", e);
         return null;
      }
   }

   protected GlossaryDescription loadDescription(Id entryId) {
      Collection entries = getHibernateTemplate().findByNamedQuery("loadDescription",
         entryId);

      if (entries.size() > 0) {
         return (GlossaryDescription)entries.iterator().next();
      }
      else {
         return new GlossaryDescription();
      }
   }

   /**
    * find the keyword in the glossary.
    * return null if not found.
    *
    * @param keyword
    * @return
    */
   public GlossaryEntry find(String keyword, String worksite) {
      Collection entries = getHibernateTemplate().findByNamedQuery("findTerms", new Object[]{keyword, worksite});
      if (entries.size() == 0) {
         return null;
      }
      else if (entries.size() == 1) {
         return (GlossaryEntry)entries.iterator().next();
      }
      else {
         for (Iterator i=entries.iterator();i.hasNext();) {
            GlossaryEntry entry = (GlossaryEntry)i.next();
            if (entry.getWorksiteId() != null) {
               return entry;
            }
         }
      }

      return (GlossaryEntry)entries.iterator().next();
   }

   /**
    * returns the list of all GlossaryEntries
    *
    * @return
    */
   
   public Collection findAll(String keyword, String worksite) {
      return getHibernateTemplate().findByNamedQuery("findTerms", new Object[]{keyword, worksite});
   }

   /**
    * returns the list of all GlossaryEntries
    *
    * @return
    */
   public Collection findAll(String worksite) {
      return getHibernateTemplate().findByNamedQuery("findAllSiteTerms",
         new Object[]{worksite});
   }

   public Collection findAll() {
      return getHibernateTemplate().findByNamedQuery("findAllTerms");
   }

   public Collection findAllGlobal() {
      return getHibernateTemplate().findByNamedQuery("findGlobalTerms");
   }

   public GlossaryEntry addEntry(GlossaryEntry newEntry) {
      getHibernateTemplate().save(newEntry);
      newEntry.getLongDescriptionObject().setEntryId(newEntry.getId());
      getHibernateTemplate().save(newEntry.getLongDescriptionObject());
      updateCache(newEntry, false);
      return newEntry;
   }

   public void removeEntry(GlossaryEntry entry) {
      getHibernateTemplate().delete(entry);
      GlossaryDescription desc = loadDescription(entry.getId());
      getHibernateTemplate().delete(desc);
      updateCache(entry, true);
   }

   public void updateEntry(GlossaryEntry entry) {
      getHibernateTemplate().merge(entry);
      GlossaryDescription desc = loadDescription(entry.getId());
      desc.setLongDescription(entry.getLongDescription());
      getHibernateTemplate().merge(desc);
      updateCache(entry, false);
   }

   protected void updateCache(GlossaryEntry entry, boolean remove) {
      GlossaryTxSync txSync = new GlossaryTxSync(entry, remove);

      if (TransactionSynchronizationManager.isSynchronizationActive()) {
         TransactionSynchronizationManager.registerSynchronization(txSync);
      }
      else {
         txSync.afterCompletion(GlossaryTxSync.STATUS_COMMITTED);
      }
   }

   public Set getSortedWorksiteTerms(String worksiteId) {
      Set sortedSet = new TreeSet(new TermComparator());

      Map worksiteTerms = (Map)worksiteGlossary.get(worksiteId);
      if (worksiteTerms != null) {
         sortedSet.addAll(worksiteTerms.values());
      }

      String globalId = null;
      Map globalTerms = (Map)worksiteGlossary.get(globalId + "");

      if (globalTerms != null) {
         for (Iterator i=globalTerms.values().iterator();i.hasNext();) {
            GlossaryEntry entry = (GlossaryEntry)i.next();
            if (!sortedSet.contains(entry)) {
               sortedSet.add(entry);
            }
         }
      }

      return sortedSet;
   }

   public boolean isPhraseStart(String phraseFragment, String worksite) {
      phraseFragment += "%";
      Collection entries = getHibernateTemplate().findByNamedQuery("findByPhrase", new Object[]{phraseFragment, worksite});
      if (entries.size() > 0) {
         return true;
      }

      return false;
   }

   public String getUrl() {
      return url;
   }

   public void setUrl(String url) {
      this.url = url;
   }
   
   public void importResources(String fromContext, String toContext, List resourceIds) {
      Collection orig = findAll(fromContext);

      for (Iterator i=orig.iterator();i.hasNext();) {
         GlossaryEntry entry = (GlossaryEntry)i.next();
         getHibernateTemplate().evict(entry);
         entry.setWorksiteId(toContext);
         entry.setId(null);
         getHibernateTemplate().save(entry);
      }
   }

   public void init() {
      logger.info("init()");
      Collection terms = findAll();

      for (Iterator i=terms.iterator();i.hasNext();) {
         GlossaryEntry entry = (GlossaryEntry)i.next();
         addUpdateTermCache(entry);
      }

      EventTrackingService.addObserver(this);
   }

   protected void addUpdateTermCache(GlossaryEntry entry) {
      String worksiteId = entry.getWorksiteId() + "";
      Map worksiteMap = (Map) worksiteGlossary.get(worksiteId);

      if (worksiteMap == null) {
         worksiteGlossary.put(worksiteId, new Hashtable());
         worksiteMap = (Map) worksiteGlossary.get(worksiteId);
      }
      worksiteMap.put(entry.getId(), entry);
   }

   protected void removeCachedEntry(Id entryId) {
      for (Iterator i=worksiteGlossary.values().iterator();i.hasNext();) {
         Map map = (Map)i.next();
         if (map.remove(entryId) != null) {
            // found it
            return;
         }
      }
   }

   /**
    * This method is called whenever the observed object is changed. An
    * application calls an <tt>Observable</tt> object's
    * <code>notifyObservers</code> method to have all the object's
    * observers notified of the change.
    *
    * @param o   the observable object.
    * @param arg an argument passed to the <code>notifyObservers</code>
    *            method.
    */
   public void update(Observable o, Object arg) {
      if (arg instanceof Event) {
         Event event = (Event)arg;
         if (event.getEvent().equals(EVENT_UPDATE_ADD)) {
            addUpdateTermCache(load(getIdManager().getId(event.getResource()), false));
         }
         else if (event.getEvent().equals(EVENT_DELETE)) {
            removeCachedEntry(getIdManager().getId(event.getResource()));
         }
      }
   }

   public IdManager getIdManager() {
      return idManager;
   }

   public void setIdManager(IdManager idManager) {
      this.idManager = idManager;
   }

   private class GlossaryTxSync extends TransactionSynchronizationAdapter {
      private GlossaryEntry entry;
      private boolean remove = false;

      public GlossaryTxSync(GlossaryEntry entry, boolean remove) {
         this.entry = entry;
         this.remove = remove;
      }

      public void afterCompletion(int status) {
         Event event = null;
         if (status == STATUS_COMMITTED && remove) {
            event = EventTrackingService.newEvent(EVENT_DELETE, entry.getId().getValue(), false);
         }
         else if (status == STATUS_COMMITTED) {
            event = EventTrackingService.newEvent(EVENT_UPDATE_ADD, entry.getId().getValue(), false);
         }

         if (event != null) {
            EventTrackingService.post(event);
         }
      }

      public GlossaryEntry getEntry() {
         return entry;
      }

      public void setEntry(GlossaryEntry entry) {
         this.entry = entry;
      }
   }
}

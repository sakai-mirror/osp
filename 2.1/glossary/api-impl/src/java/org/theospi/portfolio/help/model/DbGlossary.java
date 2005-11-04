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
 * $Header: /opt/CVS/osp2.x/glossary/api-impl/src/java/org/theospi/portfolio/help/model/DbGlossary.java,v 1.1 2005/07/08 01:18:46 jellis Exp $
 * $Revision$
 * $Date$
 */
package org.theospi.portfolio.help.model;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.theospi.portfolio.help.model.Glossary;
import org.springframework.orm.hibernate.support.HibernateDaoSupport;
import org.springframework.transaction.support.TransactionSynchronizationAdapter;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.sakaiproject.service.legacy.site.ToolConfiguration;
import org.sakaiproject.service.legacy.event.Event;
import org.sakaiproject.service.legacy.event.cover.EventTrackingService;
import org.sakaiproject.metaobj.shared.mgt.IdManager;
import org.sakaiproject.metaobj.shared.model.Id;

import java.util.*;

import net.sf.hibernate.HibernateException;

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
      Collection entries = getHibernateTemplate().find("from GlossaryDescription where entry_id = ?",
         entryId.getValue());

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
      Collection entries = getHibernateTemplate().find("from GlossaryEntry where lower(term)=lower(?) AND " +
         "(worksite_id=? or worksite_id is null)", new Object[]{keyword, worksite});
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
      return getHibernateTemplate().find("from GlossaryEntry where lower(term)=lower(?) AND " +
            "(worksite_id=? or worksite_id is null)", new Object[]{keyword, worksite});
   }

   /**
    * returns the list of all GlossaryEntries
    *
    * @return
    */
   public Collection findAll(String worksite) {
      return getHibernateTemplate().find("from GlossaryEntry where worksite_id=? Order by term",
         new Object[]{worksite});
   }

   public Collection findAll() {
      return getHibernateTemplate().find("from GlossaryEntry Order by term");
   }

   public Collection findAllGlobal() {
      return getHibernateTemplate().find("from GlossaryEntry where worksite_id is null Order by term");
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
      getHibernateTemplate().saveOrUpdateCopy(entry);
      GlossaryDescription desc = loadDescription(entry.getId());
      desc.setLongDescription(entry.getLongDescription());
      getHibernateTemplate().saveOrUpdateCopy(desc);
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
      Collection entries = getHibernateTemplate().find("from GlossaryEntry where term like(?) AND " +
         "(worksite_id=? or worksite_id is null)", new Object[]{phraseFragment, worksite});
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
   
   public void importResources(ToolConfiguration fromTool, ToolConfiguration toTool, List resourceIds) {
      Collection orig = findAll(fromTool.getSiteId());

      for (Iterator i=orig.iterator();i.hasNext();) {
         GlossaryEntry entry = (GlossaryEntry)i.next();
         entry.setWorksiteId(toTool.getSiteId());
         entry.setId(null);
         getHibernateTemplate().saveOrUpdateCopy(entry);
      }
   }

   public void init() {
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

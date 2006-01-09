/**********************************************************************************
* $URL$
* $Id$
***********************************************************************************
*
* Copyright (c) 2003, 2004 The Regents of the University of Michigan, Trustees of Indiana University,
*                  Board of Trustees of the Leland Stanford, Jr., University, and The MIT Corporation
*
* Licensed under the Educational Community License Version 1.0 (the "License");
* By obtaining, using and/or copying this Original Work, you agree that you have read,
* understand, and will comply with the terms and conditions of the Educational Community License.
* You may obtain a copy of the License at:
*
*      http://cvs.sakaiproject.org/licenses/license_1_0.html
*
* THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
* INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE
* AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
* DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
* FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*
**********************************************************************************/
package org.theospi.portfolio.presentation.tool;

import org.theospi.portfolio.shared.tool.HelperToolBase;
import org.theospi.portfolio.shared.model.Node;
import org.theospi.portfolio.presentation.intf.FreeFormHelper;
import org.theospi.portfolio.presentation.PresentationManager;
import org.theospi.portfolio.presentation.model.*;
import org.theospi.jsf.intf.XmlTagFactory;
import org.sakaiproject.api.kernel.session.ToolSession;
import org.sakaiproject.api.kernel.session.cover.SessionManager;
import org.sakaiproject.service.legacy.filepicker.FilePickerHelper;
import org.sakaiproject.service.legacy.resource.cover.EntityManager;
import org.sakaiproject.service.legacy.entity.Reference;
import org.sakaiproject.metaobj.shared.mgt.IdManager;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import java.util.*;
import java.io.IOException;


/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Dec 31, 2005
 * Time: 9:23:29 PM
 * To change this template use File | Settings | File Templates.
 */
public class FreeFormTool extends HelperToolBase {

   private PresentationManager presentationManager;
   private IdManager idManager;
   private XmlTagFactory factory;

   private Presentation presentation = null;

   private DecoratedPage currentPage = null;
   private List pageList;
   private List attachableItems = null;
   private List listableItems = null;
   private List layouts = null;

   public String processActionBack() {
      setAttribute(FreeFormHelper.FREE_FORM_ACTION, FreeFormHelper.ACTION_BACK);
      return returnToCaller();
   }

   public String processActionContinue() {
      setAttribute(FreeFormHelper.FREE_FORM_ACTION, FreeFormHelper.ACTION_CONTINUE);
      return returnToCaller();
   }

   public String processActionSave() {
      setAttribute(FreeFormHelper.FREE_FORM_ACTION, FreeFormHelper.ACTION_SAVE);
      return returnToCaller();
   }

   public String processActionCancel() {
      initValues();
      setAttribute(FreeFormHelper.FREE_FORM_ACTION, FreeFormHelper.ACTION_CANCEL);
      return returnToCaller();
   }

   protected void initValues() {
      currentPage = null;
      pageList = null;
      attachableItems = null;
      listableItems = null;
   }

   public PresentationManager getPresentationManager() {
      return presentationManager;
   }

   public void setPresentationManager(PresentationManager presentationManager) {
      this.presentationManager = presentationManager;
   }

   public Presentation getPresentation() {
      Presentation sessionPresentation =
            (Presentation) getAttribute(FreeFormHelper.FREE_FORM_PREFIX + "presentation");
      if (sessionPresentation != null) {
         removeAttribute(FreeFormHelper.FREE_FORM_PREFIX + "presentation");
         presentation = sessionPresentation;
         List pages = presentation.getPages();
         if (pages == null) {
            pages = getPresentationManager().getPresentationPagesByPresentation(presentation.getId());
            presentation.setPages(pages);
         }
         initValues();
      }
      return presentation;
   }

   public DecoratedPage getCurrentPage() {
      return currentPage;
   }

   public void setCurrentPage(DecoratedPage currentPage) {
      this.currentPage = currentPage;
   }

   public List getPageList() {
      Presentation presentation = getPresentation();
      if (pageList == null) {
         List pages = presentation.getPages();

         pageList = new ArrayList();
         for (Iterator i=pages.iterator();i.hasNext();) {
            pageList.add(new DecoratedPage((PresentationPage) i.next(), this));
         }
      }
      return pageList;
   }

   public void setPageList(List pageList) {
      this.pageList = pageList;
   }

   public XmlTagFactory getFactory() {
      return factory;
   }

   public void setFactory(XmlTagFactory factory) {
      this.factory = factory;
   }

   public void processActionManageItems(ActionEvent event) {
      ExternalContext context = FacesContext.getCurrentInstance().getExternalContext();
      ToolSession session = SessionManager.getCurrentToolSession();
      session.setAttribute(FilePickerHelper.FILE_PICKER_ATTACH_LINKS, new Boolean(true).toString());

      List attachments = new ArrayList(getPresentation().getItems());
      List attachmentRefs = EntityManager.newReferenceList();

      for (Iterator i=attachments.iterator();i.hasNext();) {
         PresentationItem attachment = (PresentationItem)i.next();
         Node item = getPresentationManager().getNode(attachment.getArtifactId());
         attachmentRefs.add(EntityManager.newReference(item.getResource().getReference()));
      }

      session.setAttribute(FilePickerHelper.FILE_PICKER_ATTACHMENTS, attachmentRefs);

      try {
         context.redirect("sakai.filepicker.helper/tool");
      }
      catch (IOException e) {
         throw new RuntimeException("Failed to redirect to helper", e);
      }
   }

   public Set getItems() {
      checkUpdateItems();
      if (getPresentation().getItems() != null) {
         return getPresentation().getItems();
      }
      getPresentation().setItems(new HashSet());
      return getPresentation().getItems();
   }

   protected void checkUpdateItems() {
      ToolSession session = SessionManager.getCurrentToolSession();
      if (session.getAttribute(FilePickerHelper.FILE_PICKER_CANCEL) == null &&
         session.getAttribute(FilePickerHelper.FILE_PICKER_ATTACHMENTS) != null) {

         List refs = (List)session.getAttribute(FilePickerHelper.FILE_PICKER_ATTACHMENTS);
         List newAttachments = new ArrayList();

         for(int i=0; i<refs.size(); i++) {
            Reference ref = (Reference) refs.get(i);
            PresentationItem item = new PresentationItem();
            Node node = getPresentationManager().getNode(ref);
            item.setArtifactId(node.getId());
            newAttachments.add(item);
         }
         session.removeAttribute(FilePickerHelper.FILE_PICKER_ATTACHMENTS);
         getPresentation().getItems().clear();
         getPresentation().getItems().addAll(newAttachments);
         attachableItems = null;
         listableItems = null;
      }

   }

   public List getAttachableItems() {
      checkUpdateItems();
      if (attachableItems == null) {
         attachableItems = new ArrayList();
         for (Iterator i=getListableItems().iterator();i.hasNext();) {
            DecoratedItem item = (DecoratedItem)i.next();
            Node node = item.getNode();
            attachableItems.add(createSelect(node.getExternalUri(),
                  node.getDisplayName()));
         }
      }

      List pages = getPageList();
      for (Iterator i=pages.iterator();i.hasNext();) {
         DecoratedPage page = (DecoratedPage) i.next();
         attachableItems.add(createSelect(page.getBase().getUrl(),
               page.getBase().getTitle()));
      }

      return attachableItems;
   }

   public List getListableItems() {
      checkUpdateItems();
      if (listableItems == null) {
         listableItems = new ArrayList();
         for (Iterator i=getItems().iterator();i.hasNext();) {
            PresentationItem item = (PresentationItem)i.next();
            listableItems.add(new DecoratedItem(item, this));
         }
      }

      return listableItems;
   }

   public String getCurrentPageId() {
      return getCurrentPage().getBase().getId().getValue();
   }

   public void setCurrentPageId(String pageId) {
      List base = getPageList();
      for (Iterator i=base.iterator();i.hasNext();) {
         DecoratedPage page = (DecoratedPage) i.next();
         if (page.getBase().getId().getValue().equals(pageId)) {
            setCurrentPage(page);
            break;
         }
      }
   }

   public List getPageDropList() {
      List base = getPageList();
      List returned = new ArrayList();
      for (Iterator i=base.iterator();i.hasNext();) {
         DecoratedPage page = (DecoratedPage)i.next();
         returned.add(createSelect(page.getBase().getId().getValue(), page.getBase().getTitle()));
      }
      return returned;
   }

   public List getLayouts() {
      if (layouts == null) {
         layouts = new ArrayList();
         List baseLayouts = getPresentationManager().getLayouts();
         for (Iterator i=baseLayouts.iterator();i.hasNext();) {
            PresentationLayout layout = (PresentationLayout) i.next();
            layouts.add(createSelect(layout.getId().getValue(), layout.getName()));
         }
      }

      return layouts;
   }

   public void setLayouts(List layouts) {
      this.layouts = layouts;
   }

   public IdManager getIdManager() {
      return idManager;
   }

   public void setIdManager(IdManager idManager) {
      this.idManager = idManager;
   }

   public String processActionNewPage() {
      PresentationPage page = new PresentationPage();

      page.setNewObject(true);
      page.setId(getIdManager().createId());
      page.setPresentation(getPresentation());
      page.setRegions(new HashSet());
      page.setAdvancedNavigation(false); // this is the default
      presentation.getPages().add(page);
      reorderPages();
      DecoratedPage decoratedPage = new DecoratedPage(page, this);
      setCurrentPage(decoratedPage);
      return "edit";
   }

   protected void reorderPages() {
      int index = 0;
      for (Iterator i=presentation.getPages().iterator();i.hasNext();) {
         PresentationPage page = (PresentationPage) i.next();
         page.setSequence(index);
         index++;
      }
      pageList = null;
      attachableItems = null; // make sure list gets re-created in order
   }

   public String processRemoveSelectedPages() {
      List localPageList = pageList;

      for (Iterator i=localPageList.iterator();i.hasNext();) {
         DecoratedPage page = (DecoratedPage) i.next();
         if (page.isSelected()) {
            deletePage(page);
         }
      }

      reorderPages();
      return "main";
   }

   public void deletePage(DecoratedPage page) {
      getPresentation().getPages().remove(page.getBase());
      pageList = null;
      attachableItems = null; // make sure list gets re-created in order
   }

}

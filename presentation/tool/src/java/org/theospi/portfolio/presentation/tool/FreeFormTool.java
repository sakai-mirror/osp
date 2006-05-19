/**********************************************************************************
* $URL:https://source.sakaiproject.org/svn/osp/trunk/presentation/tool/src/java/org/theospi/portfolio/presentation/tool/FreeFormTool.java $
* $Id:FreeFormTool.java 9134 2006-05-08 20:28:42Z chmaurer@iupui.edu $
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
package org.theospi.portfolio.presentation.tool;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;

import org.sakaiproject.content.api.FilePickerHelper;
import org.sakaiproject.entity.api.Reference;
import org.sakaiproject.entity.cover.EntityManager;
import org.sakaiproject.metaobj.shared.mgt.IdManager;
import org.sakaiproject.tool.api.ToolSession;
import org.sakaiproject.tool.cover.SessionManager;
import org.theospi.jsf.intf.XmlTagFactory;
import org.theospi.portfolio.presentation.PresentationManager;
import org.theospi.portfolio.presentation.intf.FreeFormHelper;
import org.theospi.portfolio.presentation.model.Presentation;
import org.theospi.portfolio.presentation.model.PresentationItem;
import org.theospi.portfolio.presentation.model.PresentationPage;
import org.theospi.portfolio.shared.model.Node;
import org.theospi.portfolio.shared.tool.HelperToolBase;
import org.theospi.portfolio.style.StyleHelper;
import org.theospi.portfolio.style.model.Style;


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
   private String nextPageId = null;
   private int step = 1;
   private int pageCount;

   public String processActionBack() {
      if (!validPages()) {
         return null;
      }
      setAttribute(FreeFormHelper.FREE_FORM_ACTION, FreeFormHelper.ACTION_BACK);
      return returnToCaller();
   }

   protected boolean validPages() {
      if (getPageList() == null || getPageList().size() == 0) {
         FacesContext.getCurrentInstance().addMessage(null,
            getFacesMessageFromBundle("one_page_required", new Object[]{}));
         return false;
      }

      return true;
   }

   public String processActionContinue() {
      if (!validPages()) {
         return null;
      }
      setAttribute(FreeFormHelper.FREE_FORM_ACTION, FreeFormHelper.ACTION_CONTINUE);
      return returnToCaller();
   }

   public String processActionSave() {
      if (!validPages()) {
         return null;
      }
      setAttribute(FreeFormHelper.FREE_FORM_ACTION, FreeFormHelper.ACTION_SAVE);
      return returnToCaller();
   }

   public String processActionCancel() {
      initValues();
      setAttribute(FreeFormHelper.FREE_FORM_ACTION, FreeFormHelper.ACTION_CANCEL);
      return returnToCaller();
   }

   public String processActionCancelPage() {
       if (getCurrentPage().getBase().isNewObject()) {
            deletePage(getCurrentPage());
       }
       cancelBoundValues();
       return "main";
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
      nextPageId = null;
      this.currentPage = currentPage;
   }

   public void processPageSelectChange(ValueChangeEvent event) {

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
      nextPageId = pageId;
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

   public String processChangeCurrentPage() {
      List base = getPageList();
      for (Iterator i=base.iterator();i.hasNext();) {
         DecoratedPage page = (DecoratedPage) i.next();
         if (page.getBase().getId().getValue().equals(nextPageId)) {
            setCurrentPage(page);
            break;
         }
      }
      return "arrange";
   }
 public String processActionSelectStyle() {

      ExternalContext context = FacesContext.getCurrentInstance().getExternalContext();
      ToolSession session = SessionManager.getCurrentToolSession();
      session.removeAttribute(StyleHelper.CURRENT_STYLE);
      session.removeAttribute(StyleHelper.CURRENT_STYLE_ID);

      session.setAttribute(StyleHelper.STYLE_SELECTABLE, "true");
      if (presentation.getStyle() != null)
         session.setAttribute(StyleHelper.CURRENT_STYLE_ID, presentation.getStyle().getId().getValue());

      try {
         context.redirect("osp.style.helper/listStyle");
      }
      catch (IOException e) {
         throw new RuntimeException("Failed to redirect to helper", e);
      }
      return null;
   }
    public int getStep(){
        return step;
    }
    public String getStepString() {
        return "" + (step);
    }

    public int getPageCount () {
        return getPageList().size();
    }
    
    
    public String getStyleName() {
       ToolSession session = SessionManager.getCurrentToolSession();
       if (session.getAttribute(StyleHelper.CURRENT_STYLE) != null) {
          Style style = (Style)session.getAttribute(StyleHelper.CURRENT_STYLE);
          presentation.setStyle(style);
       }
       else if (session.getAttribute(StyleHelper.UNSELECTED_STYLE) != null) {
          presentation.setStyle(null);
          session.removeAttribute(StyleHelper.UNSELECTED_STYLE);
          return "";
       }
       
       if (presentation.getStyle() != null)
          return presentation.getStyle().getName();
       return "";
    }


}
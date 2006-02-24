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
package org.theospi.portfolio.presentation.tool;

import org.theospi.portfolio.presentation.model.PresentationPage;
import org.theospi.portfolio.presentation.model.PresentationLayout;
import org.theospi.portfolio.shared.model.Node;
import org.theospi.portfolio.style.StyleHelper;
import org.theospi.portfolio.style.model.Style;
import org.sakaiproject.api.kernel.session.ToolSession;
import org.sakaiproject.api.kernel.session.cover.SessionManager;
import org.sakaiproject.metaobj.shared.model.Id;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Jan 1, 2006
 * Time: 7:32:11 PM
 * To change this template use File | Settings | File Templates.
 */
public class DecoratedPage implements Comparable {

   private FreeFormTool parent;
   private PresentationPage base;
   private RegionMap regionMap;
   private boolean selected;
   private DecoratedLayout selectedLayout = null;

   public DecoratedPage(PresentationPage base, FreeFormTool parent) {
      this.base = base;
      this.parent = parent;
      initLayout();
   }

   protected void initLayout() {
      if (base.getLayout() != null) {
         setSelectedLayout(new DecoratedLayout(parent, base.getLayout()));
      }
      else if (getParent().getLayouts().size() > 0) {
         setSelectedLayout(getParent().getFirstLayout());
      }
   }
   
   public String getStyleName() {
      ToolSession session = SessionManager.getCurrentToolSession();
      if (session.getAttribute(StyleHelper.CURRENT_STYLE) != null) {
         Style style = (Style)session.getAttribute(StyleHelper.CURRENT_STYLE);
         base.setStyle(style);
      }
      else if (session.getAttribute(StyleHelper.UNSELECTED_STYLE) != null) {
         base.setStyle(null);
         session.removeAttribute(StyleHelper.UNSELECTED_STYLE);
         return "";
      }
      
      if (base.getStyle() != null)
         return base.getStyle().getName();
      return "";
   }

   public PresentationPage getBase() {
      return base;
   }

   public void setBase(PresentationPage base) {
      this.base = base;
   }

   public InputStream getXmlFile() {
      Node node = getParent().getPresentationManager().getNode(
            getBase().getLayout().getXhtmlFileId(), getBase().getLayout());
      return node.getInputStream();
   }

   public String getXmlFileId() {
      return getBase().getLayout().getId().getValue() + getBase().getLayout().getModified().toString();
   }

   public RegionMap getRegionMap() {
      if (regionMap == null) {
         regionMap = new RegionMap(getBase());
      }
      return regionMap;
   }

   public void setRegionMap(RegionMap regionMap) {
      this.regionMap = regionMap;
   }

   public FreeFormTool getParent() {
      return parent;
   }

   public void setParent(FreeFormTool parent) {
      this.parent = parent;
   }

   public String processActionArrange() {
      getParent().setCurrentPage(this);
      initLayout();
      return "arrange";
   }

   public String processActionEdit() {
      getParent().setCurrentPage(this);
      initLayout();
      return "edit";
   }

   public String processActionDelete() {
      getParent().deletePage(this);
      getParent().reorderPages();
      return "main";
   }
   
   public String processActionSelectStyle() {      
      ExternalContext context = FacesContext.getCurrentInstance().getExternalContext();
      ToolSession session = SessionManager.getCurrentToolSession();
      session.removeAttribute(StyleHelper.CURRENT_STYLE);
      
      session.setAttribute(StyleHelper.STYLE_SELECTABLE, "true");
      if (base.getStyle() != null)
         session.setAttribute(StyleHelper.CURRENT_STYLE_ID, base.getStyle().getId().getValue());
      
      try {
         context.redirect("osp.style.helper/listStyle");
      }
      catch (IOException e) {
         throw new RuntimeException("Failed to redirect to helper", e);
      }
      return null;
   }

   public boolean isSelected() {
      return selected;
   }

   public void setSelected(boolean selected) {
      this.selected = selected;
   }

   public DecoratedLayout getSelectedLayout() {
      return selectedLayout;
   }

   public void setSelectedLayout(DecoratedLayout selectedLayout) {
      this.selectedLayout = selectedLayout;
   }

   public void setSelectedLayoutId(String layoutId) {
      Id id = getParent().getIdManager().getId(layoutId);
      PresentationLayout layout = getParent().getPresentationManager().getPresentationLayout(id);
      setSelectedLayout(new DecoratedLayout(getParent(), layout));
   }

   public String getSelectedLayoutId() {
      if (getSelectedLayout() != null) {
         return getSelectedLayout().getBase().getId().getValue();
      }
      return null;
   }

   public boolean islayoutSelected() {
      return getSelectedLayout() != null;
   }

   public int compareTo(Object o) {
      DecoratedPage other = (DecoratedPage) o;
      return getBase().compareTo(other.getBase());
   }

   public void pagePropertiesSaved(ActionEvent event) {
      if (getBase().getLayout() != null &&
          !getBase().getLayout().equals(getSelectedLayout().getBase())) {
         getBase().getRegions().clear();
         regionMap = null;
      }
      getBase().setLayout(getSelectedLayout().getBase());
   }

   public boolean getHasLayout() {
      return getBase().getLayout() != null;
   }

   public String moveUp() {
      if (getBase().getSequence() != 0) {
         Collections.swap(getParent().getPresentation().getPages(),
               getBase().getSequence(), getBase().getSequence() - 1);
         getParent().reorderPages();
      }
      return null;
   }

   public String moveDown() {
      if (getBase().getSequence() < getParent().getPresentation().getPages().size() - 1) {
         Collections.swap(getParent().getPresentation().getPages(),
               getBase().getSequence(), getBase().getSequence() + 1);
         getParent().reorderPages();
      }
      return null;
   }

   public boolean isLast() {
      return getBase().getSequence() >= getParent().getPresentation().getPages().size() - 1;
   }
}

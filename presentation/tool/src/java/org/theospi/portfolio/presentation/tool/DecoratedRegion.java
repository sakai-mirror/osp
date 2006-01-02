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

import org.theospi.portfolio.presentation.model.PresentationPageRegion;
import org.theospi.portfolio.presentation.model.PresentationPageItem;
import org.theospi.portfolio.presentation.component.SequenceComponent;

import javax.faces.event.ActionEvent;
import javax.faces.component.UIComponent;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Jan 1, 2006
 * Time: 5:59:36 PM
 * To change this template use File | Settings | File Templates.
 */
public class DecoratedRegion {

   private PresentationPageRegion region;
   private int regionItemSeq = 0;
   private RegionMap regionMap;
   private List regionItemList = null;

   public DecoratedRegion(RegionMap regionMap, PresentationPageRegion region) {
      this.regionMap = regionMap;
      this.region = region;
      initRegionList();
   }

   public DecoratedRegion(RegionSequenceMap regionMap, PresentationPageRegion region, int regionItemSeq) {
      this.region = region;
      this.regionItemSeq = regionItemSeq;
   }

   public PresentationPageRegion getBase() {
      return region;
   }

   public PresentationPageItem getItem() {
      return (PresentationPageItem) getRegion().getItems().get(regionItemSeq);
   }

   public PresentationPageRegion getRegion() {
      return region;
   }

   public void setRegion(PresentationPageRegion region) {
      this.region = region;
   }

   public int getRegionItemSeq() {
      return regionItemSeq;
   }

   public void setRegionItemSeq(int regionItemSeq) {
      this.regionItemSeq = regionItemSeq;
   }

   public RegionMap getRegionMap() {
      return regionMap;
   }

   public void setRegionMap(RegionMap regionMap) {
      this.regionMap = regionMap;
   }

   public List getRegionItemList() {
      return regionItemList;
   }

   public void setRegionItemList(List regionItemList) {
      this.regionItemList = regionItemList;
   }

   protected void initRegionList() {
      regionItemList = new ArrayList();
      for (int i=0;i<getBase().getItems().size();i++) {
         regionItemList.add(new RegionSequenceMap(getRegionMap(), i));
      }
   }

   public void addToSequence(ActionEvent event) {
      UIComponent component = event.getComponent();

      while (!(component instanceof SequenceComponent) && component != null) {
         component = component.getParent();
      }

      if (component != null) {
         ((SequenceComponent)component).addToSequence();
      }
   }
}

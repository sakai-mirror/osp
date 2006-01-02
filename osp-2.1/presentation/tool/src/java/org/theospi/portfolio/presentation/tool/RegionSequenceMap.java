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
import org.theospi.portfolio.presentation.model.PresentationPage;
import org.theospi.portfolio.presentation.model.PresentationPageItem;

import javax.faces.event.ActionEvent;
import javax.faces.el.ValueBinding;
import javax.faces.context.FacesContext;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Jan 2, 2006
 * Time: 2:45:03 AM
 * To change this template use File | Settings | File Templates.
 */
public class RegionSequenceMap extends Hashtable {

   private PresentationPage page;
   private int regionSeqNo = 0;
   private List childRegions;

   public RegionSequenceMap(RegionMap map, int regionSeqNo) {
      this.page = map.getPage();
      setRegionSeqNo(regionSeqNo);
      for (Iterator i=map.getPage().getRegions().iterator();i.hasNext();) {
         PresentationPageRegion region = (PresentationPageRegion) i.next();
         put(region.getRegionId(), new DecoratedRegion(this, region, regionSeqNo));
      }
   }

   public void remove(ActionEvent event) {
      for (Iterator i=getChildRegions().iterator();i.hasNext();) {
         ValueBinding binding = (ValueBinding) i.next();
         DecoratedRegion region = (DecoratedRegion) binding.getValue(FacesContext.getCurrentInstance());;
         removeItem(region.getBase());
      }
   }

   protected void removeItem(PresentationPageRegion region) {
      for (Iterator i=region.getItems().iterator();i.hasNext();) {
         PresentationPageItem item = (PresentationPageItem) i.next();
         if (item.getRegionItemSeq() == getRegionSeqNo()) {
            i.remove();
         }
      }

      region.reorderItems();
   }

   public List getChildRegions() {
      return childRegions;
   }

   public void setChildRegions(List childRegions) {
      this.childRegions = childRegions;
   }

   public PresentationPage getPage() {
      return page;
   }

   public void setPage(PresentationPage page) {
      this.page = page;
   }

   public int getRegionSeqNo() {
      return regionSeqNo;
   }

   public void setRegionSeqNo(int regionSeqNo) {
      this.regionSeqNo = regionSeqNo;
   }

   public synchronized Object get(Object key) {
      return super.get(key);
   }

}

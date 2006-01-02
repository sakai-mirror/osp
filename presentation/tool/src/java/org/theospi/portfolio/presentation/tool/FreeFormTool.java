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
import org.theospi.portfolio.presentation.intf.FreeFormHelper;
import org.theospi.portfolio.presentation.PresentationManager;
import org.theospi.portfolio.presentation.model.Presentation;
import org.theospi.portfolio.presentation.model.PresentationPage;
import org.theospi.jsf.intf.XmlTagFactory;

import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;


/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Dec 31, 2005
 * Time: 9:23:29 PM
 * To change this template use File | Settings | File Templates.
 */
public class FreeFormTool extends HelperToolBase {

   private PresentationManager presentationManager;
   private Presentation presentation = null;
   private DecoratedPage currentPage = null;
   private List pageList;
   private XmlTagFactory factory;

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
      setAttribute(FreeFormHelper.FREE_FORM_ACTION, FreeFormHelper.ACTION_CANCEL);
      return returnToCaller();
   }

   public PresentationManager getPresentationManager() {
      return presentationManager;
   }

   public void setPresentationManager(PresentationManager presentationManager) {
      this.presentationManager = presentationManager;
   }

   public Presentation getPresentation() {
      if (presentation == null) {
         presentation = (Presentation) getAttribute(FreeFormHelper.FREE_FORM_PREFIX + "presentation");
      }
      return presentation;
   }

   public void setPresentation(Presentation presentation) {
      this.presentation = presentation;
   }

   public DecoratedPage getCurrentPage() {
      if (currentPage == null) {
         currentPage = (DecoratedPage) getPageList().get(0);
      }
      return currentPage;
   }

   public void setCurrentPage(DecoratedPage currentPage) {
      this.currentPage = currentPage;
   }

   public List getPageList() {
      if (pageList == null) {
         List pages = getPresentationManager().getPresentationPagesByPresentation(getPresentation().getId());
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

}

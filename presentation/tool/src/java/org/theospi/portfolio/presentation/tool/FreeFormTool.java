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

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Dec 31, 2005
 * Time: 9:23:29 PM
 * To change this template use File | Settings | File Templates.
 */
public class FreeFormTool extends HelperToolBase {

   private PresentationManager presentationManager;

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

}

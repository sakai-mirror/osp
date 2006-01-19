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
package org.theospi.portfolio.shared.tool;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Jan 19, 2006
 * Time: 1:27:50 PM
 * To change this template use File | Settings | File Templates.
 */
public class BuilderScreen {

   private BuilderTool tool;
   private String navigationKey;
   private int step = 0;

   private BuilderScreen next;
   private BuilderScreen prev;

   public BuilderScreen(String navigationKey) {
      this.navigationKey = navigationKey;
   }

   public String getNavigationKey() {
      return navigationKey;
   }

   public void setNavigationKey(String navigationKey) {
      this.navigationKey = navigationKey;
   }

   public BuilderScreen getNext() {
      return next;
   }

   public void setNext(BuilderScreen next) {
      this.next = next;
   }

   public BuilderScreen getPrev() {
      return prev;
   }

   public void setPrev(BuilderScreen prev) {
      this.prev = prev;
   }

   public BuilderTool getTool() {
      return tool;
   }

   public void setTool(BuilderTool tool) {
      this.tool = tool;
   }

   public int getStep() {
      return step;
   }

   public String getStepString() {
      return "" + (step);
   }

   public void setStep(int step) {
      this.step = step;
   }

   public BuilderScreen processActionSave(boolean forward) {
      getTool().saveScreen(this);

      return forward?getNext():getPrev();
   }

   public String processActionSaveNext() {
      BuilderScreen next = processActionSave(true);
      getTool().setCurrentScreen(next);
      return next.getNavigationKey();
   }

   public String processActionSaveBack() {
      BuilderScreen prev = processActionSave(false);
      getTool().setCurrentScreen(prev);
      return prev.getNavigationKey();
   }

   public boolean isLast() {
      return getNext() == null;
   }

   public boolean isFirst() {
      return getPrev() == null;
   }

}

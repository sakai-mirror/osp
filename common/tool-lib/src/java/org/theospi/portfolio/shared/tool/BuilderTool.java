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
 * Time: 1:26:29 PM
 * To change this template use File | Settings | File Templates.
 */
public abstract class BuilderTool extends ToolBase {

   private BuilderScreen currentScreen;
   private BuilderScreen[] screens;

   protected String startBuilder() {
      setCurrentScreen(screens[0]);
      return getCurrentScreen().getNavigationKey();
   }

   protected abstract void saveScreen(BuilderScreen screen);

   public BuilderScreen getCurrentScreen() {
      return currentScreen;
   }

   public void setCurrentScreen(BuilderScreen currentScreen) {
      this.currentScreen = currentScreen;
   }

   public BuilderScreen[] getScreens() {
      return screens;
   }

   public void setScreens(BuilderScreen[] screens) {
      for (int i=0;i<screens.length;i++) {
         BuilderScreen screen = screens[i];
         screen.setStep(i);
         screen.setTool(this);
         if (i > 0) {
            screen.setPrev(screens[i-1]);
         }

         if (i+1<screens.length) {
            screen.setNext(screens[i+1]);
         }
      }
      this.screens = screens;
   }
}

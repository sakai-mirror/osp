
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
package org.theospi.portfolio.matrix.model;

/**
 * @author chmaurer
 */
public class ReviewRubricValue {
   private String id;
   private String displayText;
   private String nextStatus;
   private boolean unlockContent; 

   public String getDisplayText() {
      return displayText;
   }
   public void setDisplayText(String display) {
      this.displayText = display;
   }
   public String getNextStatus() {
      return nextStatus;
   }
   public void setNextStatus(String flow) {
      this.nextStatus = flow;
   }
   public boolean isUnlockContent() {
      return unlockContent;
   }
   public void setUnlockContent(boolean unlockContent) {
      this.unlockContent = unlockContent;
   }
   public String getId() {
      return id;
   }
   public void setId(String id) {
      this.id = id;
   }
}

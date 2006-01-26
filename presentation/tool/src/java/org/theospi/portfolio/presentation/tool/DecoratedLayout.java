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

import org.theospi.portfolio.presentation.model.PresentationLayout;
import org.theospi.portfolio.shared.model.Node;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Jan 8, 2006
 * Time: 4:57:52 PM
 * To change this template use File | Settings | File Templates.
 */
public class DecoratedLayout {

   private FreeFormTool parent;
   private PresentationLayout base;

   public DecoratedLayout(FreeFormTool parent, PresentationLayout base) {
      this.parent = parent;
      this.base = base;
   }

   public Node getPreviewImage() {
      return getParent().getPresentationManager().getNode(base.getPreviewImageId(), base);
   }

   public PresentationLayout getBase() {
      return base;
   }

   public void setBase(PresentationLayout base) {
      this.base = base;
   }

   public FreeFormTool getParent() {
      return parent;
   }

   public void setParent(FreeFormTool parent) {
      this.parent = parent;
   }

}

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
package org.theospi.portfolio.presentation.component;

import org.theospi.jsf.util.TagUtil;

import javax.faces.component.UIOutput;
import javax.faces.context.FacesContext;
import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Jan 2, 2006
 * Time: 1:23:20 AM
 * To change this template use File | Settings | File Templates.
 */
public class SequenceComponentProxy extends UIOutput {

   public static final String COMPONENT_TYPE = "org.theospi.presentation.SequenceComponentProxy";
   private SequenceComponent base;

   public boolean getRendersChildren() {
      return true;
   }

   public void encodeChildren(FacesContext context) throws IOException {
      TagUtil.renderChild(context, getBase());
   }

   public SequenceComponent getBase() {
      return base;
   }

   public void setBase(SequenceComponent base) {
      this.base = base;
   }

}

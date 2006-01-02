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

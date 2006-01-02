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

import org.theospi.jsf.intf.XmlDocumentContainer;
import org.theospi.portfolio.presentation.model.PresentationPageRegion;
import org.theospi.portfolio.presentation.tool.RegionMap;
import org.theospi.portfolio.presentation.tool.DecoratedRegion;
import org.theospi.portfolio.presentation.tool.RegionSequenceMap;

import javax.faces.component.UIData;
import javax.faces.component.html.HtmlDataTable;
import javax.faces.el.ValueBinding;
import javax.faces.context.FacesContext;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Jan 1, 2006
 * Time: 5:48:27 PM
 * To change this template use File | Settings | File Templates.
 */
public class SequenceComponent extends HtmlDataTable implements XmlDocumentContainer {

   public static final String COMPONENT_TYPE = "org.theospi.presentation.SequenceComponent";

   private List childRegions = new ArrayList();

   public String getVariableName() {
      return getVar();
   }

   public void addRegion(ValueBinding region) {
      childRegions.add(region);
   }

   public Object getRowData() {
      RegionSequenceMap map = (RegionSequenceMap) super.getRowData();
      map.setChildRegions(childRegions);      
      return map;
   }

   public void addToSequence() {
      for (Iterator i=childRegions.iterator();i.hasNext();) {
         ValueBinding binding = (ValueBinding) i.next();
         DecoratedRegion region = (DecoratedRegion) binding.getValue(FacesContext.getCurrentInstance());;
         region.getBase().addBlank();
      }
   }

}

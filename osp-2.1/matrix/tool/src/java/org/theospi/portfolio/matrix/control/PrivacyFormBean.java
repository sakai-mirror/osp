
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
package org.theospi.portfolio.matrix.control;

import org.sakaiproject.metaobj.shared.model.ElementBean;
import org.sakaiproject.metaobj.shared.model.Id;
import org.theospi.portfolio.matrix.model.Cell;
import org.theospi.portfolio.shared.model.Node;

/**
 * @author chmaurer
 */
public class PrivacyFormBean {
   
   private Cell cell;
   private ElementBean privacyResponse;
   private Node xsdFile;

   /**
    * @return Returns the privacyResponse.
    */
   public ElementBean getPrivacyResponse() {
      return privacyResponse;
   }
   /**
    * @param privacyResponse The privacyResponse to set.
    */
   public void setPrivacyResponse(ElementBean privacyResponse) {
      this.privacyResponse = privacyResponse;
   }
   /**
    * @return Returns the cell.
    */
   public Cell getCell() {
      return cell;
   }
   /**
    * @param cell The cell to set.
    */
   public void setCell(Cell cell) {
      this.cell = cell;
   }

   public Node getXsdFile() {
      return xsdFile;
   }

   public void setXsdFile(Node xsdFile) {
      this.xsdFile = xsdFile;
   }
}

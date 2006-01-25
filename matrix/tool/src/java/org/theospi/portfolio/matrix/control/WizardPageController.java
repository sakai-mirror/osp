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
package org.theospi.portfolio.matrix.control;

import org.springframework.validation.Errors;
import org.theospi.portfolio.matrix.model.WizardPageDefinition;
import org.theospi.portfolio.matrix.model.ScaffoldingCell;
import org.theospi.portfolio.matrix.model.WizardPage;
import org.theospi.portfolio.matrix.model.Cell;
import org.theospi.portfolio.matrix.WizardPageHelper;

import java.util.Map;
import java.util.List;
import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Jan 24, 2006
 * Time: 3:46:49 PM
 * To change this template use File | Settings | File Templates.
 */
public class WizardPageController extends CellController {

   /* (non-Javadoc)
    * @see org.theospi.utils.mvc.intf.FormController#referenceData(java.util.Map, java.lang.Object, org.springframework.validation.Errors)
    */
   public Map referenceData(Map request, Object command, Errors errors) {
      Map model = super.referenceData(request, command, errors);
      model.put("helperPage", "true");
      return model;
   }

   public Object fillBackingObject(Object incomingModel, Map request, Map session, Map application) throws Exception {
      WizardPage page = (WizardPage) session.get(WizardPageHelper.WIZARD_PAGE);
      session.remove(WizardPageHelper.CANCELED);

      Cell cell = new Cell();
      cell.setWizardPage(page);
      if (page.getId() == null) {
         cell.setId(page.getNewId());
      }
      else {
         cell.setId(page.getId());
      }

      WizardPageDefinition pageDef = page.getPageDefinition();

      ScaffoldingCell cellDef = new ScaffoldingCell();
      cellDef.setWizardPageDefinition(pageDef);
      if (pageDef.getId() == null) {
         cellDef.setId(pageDef.getNewId());
      }
      else {
         cellDef.setId(pageDef.getId());
      }
      
      cell.setScaffoldingCell(cellDef);

      CellFormBean cellBean = (CellFormBean) incomingModel;
      cellBean.setCell(cell);
      List nodeList = new ArrayList(getMatrixManager().getCellContents(cell));
      cellBean.setNodes(nodeList);

      return cellBean;
   }

}

/*
 * The Open Source Portfolio Initiative Software is Licensed under the Educational Community License Version 1.0:
 *
 * This Educational Community License (the "License") applies to any original work of authorship
 * (the "Original Work") whose owner (the "Licensor") has placed the following notice immediately
 * following the copyright notice for the Original Work:
 *
 * Copyright (c) 2004 Trustees of Indiana University and r-smart Corporation
 *
 * This Original Work, including software, source code, documents, or other related items, is being
 * provided by the copyright holder(s) subject to the terms of the Educational Community License.
 * By obtaining, using and/or copying this Original Work, you agree that you have read, understand,
 * and will comply with the following terms and conditions of the Educational Community License:
 *
 * Permission to use, copy, modify, merge, publish, distribute, and sublicense this Original Work and
 * its documentation, with or without modification, for any purpose, and without fee or royalty to the
 * copyright holder(s) is hereby granted, provided that you include the following on ALL copies of the
 * Original Work or portions thereof, including modifications or derivatives, that you make:
 *
 * - The full text of the Educational Community License in a location viewable to users of the
 * redistributed or derivative work.
 *
 * - Any pre-existing intellectual property disclaimers, notices, or terms and conditions.
 *
 * - Notice of any changes or modifications to the Original Work, including the date the changes were made.
 *
 * - Any modifications of the Original Work must be distributed in such a manner as to avoid any confusion
 *  with the Original Work of the copyright holders.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
 * SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 * The name and trademarks of copyright holder(s) may NOT be used in advertising or publicity pertaining
 * to the Original or Derivative Works without specific, written prior permission. Title to copyright
 * in the Original Work and any associated documentation will at all times remain with the copyright holders.
 *
 * $Header: /opt/CVS/osp2.x/matrix/tool/src/java/org/theospi/portfolio/matrix/control/DeleteLevelController.java,v 1.1 2005/07/15 21:10:34 rpembry Exp $
 * $Revision$
 * $Date$
 */


package org.theospi.portfolio.matrix.control;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.metaobj.utils.mvc.intf.Controller;
import org.sakaiproject.service.legacy.content.LockManager;
import org.springframework.validation.Errors;
import org.springframework.web.servlet.ModelAndView;
import org.theospi.portfolio.matrix.MatrixManager;
import org.theospi.portfolio.matrix.model.Cell;
import org.theospi.portfolio.matrix.model.Scaffolding;
import org.theospi.portfolio.matrix.model.ScaffoldingCell;

/**
 * @author chmaurer
 */
public class DeleteLevelController implements Controller {

   protected final Log logger = LogFactory.getLog(getClass());
   
   private MatrixManager matrixManager;
   private LockManager lockManager;
   
   /* (non-Javadoc)
    * @see org.theospi.utils.mvc.intf.Controller#handleRequest(java.lang.Object, java.util.Map, java.util.Map, java.util.Map, org.springframework.validation.Errors)
    */
   public ModelAndView handleRequest(Object requestModel, Map request, Map session, Map application, Errors errors) {
      EditedScaffoldingStorage sessionBean = (EditedScaffoldingStorage)session.get(
            EditedScaffoldingStorage.EDITED_SCAFFOLDING_STORAGE_SESSION_KEY);
      Scaffolding scaffolding = sessionBean.getScaffolding();
      Map model = new HashMap();
      
      String levelId = (String)request.get("level_id");
      String levelIndex = (String)request.get("index");
      
      if (levelIndex != null) {
         scaffolding.getLevels().remove(Integer.parseInt(levelIndex));
      }
      
      if (levelId != null && !levelId.equals("")) {
         Set scaffoldingCells = scaffolding.getScaffoldingCells();
         for (Iterator iter = scaffoldingCells.iterator(); iter.hasNext();) {
            ScaffoldingCell sCell = (ScaffoldingCell) iter.next();
            if (sCell.getLevel().getId().getValue().equals(levelId)) {
               Set cells = sCell.getCells();
               for (Iterator i=cells.iterator(); i.hasNext();) {
                  Cell cell = (Cell) i.next();
                  lockManager.removeAllLocks(cell.getId().getValue());
                  i.remove();
               }
               
               iter.remove();
            }
         }
      }
      
      sessionBean.setScaffolding(scaffolding);
      session.put(EditedScaffoldingStorage.EDITED_SCAFFOLDING_STORAGE_SESSION_KEY,
            sessionBean);
      
      model.put(EditedScaffoldingStorage.STORED_SCAFFOLDING_FLAG, "true");
      return new ModelAndView("success", model);
   }
   
   public MatrixManager getMatrixManager() {
      return matrixManager;
   }
   public void setMatrixManager(MatrixManager matrixManager) {
      this.matrixManager = matrixManager;
   }
   public LockManager getLockManager() {
      return lockManager;
   }
   public void setLockManager(LockManager lockManager) {
      this.lockManager = lockManager;
   }
}

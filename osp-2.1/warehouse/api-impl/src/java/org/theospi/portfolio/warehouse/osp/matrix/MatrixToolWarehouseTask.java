/**********************************************************************************
* $URL:$
* $Id:$
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
package org.theospi.portfolio.warehouse.osp.matrix;

import org.theospi.portfolio.matrix.MatrixManager;
import org.theospi.portfolio.matrix.model.Matrix;
import org.theospi.portfolio.warehouse.impl.BaseWarehouseTask;
import java.util.Collection;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.HashSet;


class MatrixToolWarehouseTask extends BaseWarehouseTask {

   private MatrixManager matrixManager;
   
   protected Collection getItems() {
//      Collection matrices = matrixManager.getMatrixTools();
//      
//      for(Iterator i = matrices.iterator(); i.hasNext(); ) {
//         MatrixTool tool = (MatrixTool)i.next();
//         
//         tool.getId();
//         tool.setMatrix(new HashSet(matrixManager.getMatrices(tool.getId(), null)));
//         Collection mats = tool.getMatrix();
//         
//         for(Iterator ii = mats.iterator(); ii.hasNext(); ) {
//            Matrix mat = (Matrix)ii.next();
//            
//            mat.getId();
//            mat.setMatrixTool(tool);
//         }
//      }
//      
      return null;
   }

   public MatrixManager getMatrixManager() {
      return matrixManager;
   }

   public void setMatrixManager(MatrixManager matrixManager) {
      this.matrixManager = matrixManager;
   }
}
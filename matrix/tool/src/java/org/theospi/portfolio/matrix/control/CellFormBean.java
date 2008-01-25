/**********************************************************************************
* $URL:https://source.sakaiproject.org/svn/osp/trunk/matrix/tool/src/java/org/theospi/portfolio/matrix/control/CellFormBean.java $
* $Id:CellFormBean.java 9134 2006-05-08 20:28:42Z chmaurer@iupui.edu $
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

import java.util.List;

import org.theospi.portfolio.matrix.model.Cell;

/**
 * @author chmaurer
 */
public class CellFormBean {

   private Cell cell;
   private List nodes;
   private List assignments;
   private String[] selectedArtifacts;

   /**
    * @return
    */
   public List getNodes() {
      return nodes;
   }

   /**
    * @return
    */
   public Cell getCell() {
      return cell;
   }

   /**
    * @param list
    */
   public void setNodes(List list) {
      nodes = list;
   }

   /**
    * @param cell
    */
   public void setCell(Cell cell) {
      this.cell = cell;
   }
    
    /**
     * @return Returns the selectedArtifacts.
     */
    public String[] getSelectedArtifacts() {
        return selectedArtifacts;
    }
    
    /**
     * @param selectedArtifacts The selectedArtifacts to set.
     */
    public void setSelectedArtifacts(String[] selectedArtifacts) {
        this.selectedArtifacts = selectedArtifacts;
    }
    
   /**
    * @param boolean
    */
   public void setAssignments(List assignments) {
      this.assignments = assignments;
   }

   /**
    * @param cell
    */
   public List getAssignments() {
      return assignments;
   }
    
}

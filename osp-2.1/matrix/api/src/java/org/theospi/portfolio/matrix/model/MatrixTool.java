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

import org.sakaiproject.metaobj.shared.model.Id;
import org.sakaiproject.metaobj.shared.model.IdentifiableObject;

import java.util.HashSet;
import java.util.Set;

/**
 * @author rpembry
 */
public class MatrixTool extends IdentifiableObject {

   private Id id;
   private Scaffolding scaffolding;
   private Set matrix = new HashSet();

   public MatrixTool() {;}
   
   public MatrixTool(Id id, Scaffolding scaffolding) {
      this.id = id;
      this.scaffolding = scaffolding;
   }
   /**
    * @return Returns the wizardPage.
    */
   public Set getMatrix() {
      return matrix;
   }

   /**
    * @param cell The wizardPage to set.
    */
   public void setMatrix(Set matrix) {
      this.matrix = matrix;
   }

   /**
    * @return Returns the id.
    */
   public Id getId() {
      return id;
   }

   /**
    * @param id The id to set.
    */
   public void setId(Id id) {
      this.id = id;
   }

   /**
    * @return Returns the scaffold class.
    */
   public Scaffolding getScaffolding() {
      return scaffolding;
   }

   /**
    * @param scaffoldId The scaffold instance to set.
    */
   public void setScaffolding(Scaffolding scaffolding) {
      this.scaffolding = scaffolding;
   }

   public void add(Matrix matrix) {
      this.getMatrix().add(matrix);
      matrix.setMatrixTool(this);
   }

   /* (non-Javadoc)
    * @see java.lang.Object#equals(java.lang.Object)
    */
   public boolean equals(Object other) {
      if (other == this) return true;
      if (other == null || !(other instanceof MatrixTool)) return false;
      //TODO need better equals method
      return (this.getId().equals(((MatrixTool) other).getId()));

   }

   /* (non-Javadoc)
    * @see java.lang.Object#hashCode()
    */
   public int hashCode() {
      //TODO need better hashcode
      Id id = this.getId();
      if (id == null) return 0;
      return id.getValue().hashCode();
   }
}

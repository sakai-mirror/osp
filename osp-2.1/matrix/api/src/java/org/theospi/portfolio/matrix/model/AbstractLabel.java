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

import java.io.Serializable;

import org.sakaiproject.metaobj.shared.model.Id;
import org.sakaiproject.metaobj.shared.model.IdentifiableObject;

/**
 * @author apple
 */
public abstract class AbstractLabel extends IdentifiableObject implements Serializable, Label {
   Id id;
   String description;
   String color;
   String textColor = "#000000";


   /**
    * @return Returns the description.
    */
   public String getDescription() {
      return description;
   }

   /**
    * @param description The description to set.
    */
   public void setDescription(String description) {
      this.description = description;
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
   
   public String getColor() {
      return color;
   }

   public void setColor(String color) {
      this.color = color;
   }

   public String getTextColor() {
      return textColor;
   }

   public void setTextColor(String textColor) {
      this.textColor = textColor;
   }

   public String toString() {
      return "<(" + this.getClass().getName() + ") " + this.getDescription() + " [" + this.getId() + "]>";
   }
}

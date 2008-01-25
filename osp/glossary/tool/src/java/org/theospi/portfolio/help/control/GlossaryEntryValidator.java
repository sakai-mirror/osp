/**********************************************************************************
* $URL$
* $Id$
***********************************************************************************
*
* Copyright (c) 2007 The Sakai Foundation.
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

/**
 * 
 */
package org.theospi.portfolio.help.control;

import org.springframework.validation.Errors;
import org.theospi.portfolio.help.model.GlossaryEntry;
import org.theospi.utils.mvc.impl.ValidatorBase;

/**
 * @author chrismaurer
 *
 */
public class GlossaryEntryValidator extends ValidatorBase {


   public boolean supports(Class clazz) {
      if (GlossaryEntry.class.isAssignableFrom(clazz)) return true;
      else return false;
   }

   /* (non-Javadoc)
    * @see org.springframework.validation.Validator#validate(java.lang.Object, org.springframework.validation.Errors)
    */
   public void validate(Object obj, Errors errors) {
      GlossaryEntry entry = (GlossaryEntry)obj;
      if (entry.getTerm() == null || entry.getTerm().equals("")) {
         errors.rejectValue("term", "error.required", "required");
      }
      if (entry.getTerm() != null && entry.getTerm().length() > 255) {
         errors.rejectValue("description", "error.lengthExceded", new Object[]{"255"}, "Value must be less than {0} characters");
      }
      if (entry.getDescription() == null || entry.getDescription().equals("")) {
         errors.rejectValue("description", "error.required", "required");
      }
      if (entry.getDescription() != null && entry.getDescription().length() > 255) {
         errors.rejectValue("description", "error.lengthExceded", new Object[]{"255"}, "Value must be less than {0} characters");
      }
      if (entry.getLongDescription() == null || entry.getLongDescription().equals("")) {
         errors.rejectValue("longDescription", "error.required", "required");
      }
     //TODO Should there be a length check on the long description?
      
   }

}

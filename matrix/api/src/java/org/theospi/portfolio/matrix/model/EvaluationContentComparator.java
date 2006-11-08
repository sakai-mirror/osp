/**********************************************************************************
* $URL$
* $Id$
***********************************************************************************
*
* Copyright (c) 2006 The Sakai Foundation.
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

import java.util.Comparator;

import org.theospi.portfolio.shared.model.EvaluationContentWrapper;



public class EvaluationContentComparator implements Comparator {

   public static final String SORT_DATE = "date";
   public static final String SORT_OWNER = "owner";
   public static final String SORT_TITLE = "title";
   public static final String SORT_TYPE = "type";
   
   // the criteria
   private String criteria = null;

   // the criteria - asc
   private boolean asc = true;

   /**
    * constructor
    * @param criteria The sort criteria string
    * @param asc The sort order string. "true" if ascending; "false" otherwise.
    */
   public EvaluationContentComparator(String criteria, boolean asc)
   {
      this.criteria = criteria;
      this.asc = asc;

   } // constructor

   /**
    * implementing the compare function
    * @param o1 The first object
    * @param o2 The second object
    * @return The compare result. 1 is o1 < o2; -1 otherwise
    */
   public int compare(Object o1, Object o2)
   {
      int result = -1;

      if (criteria.equals(SORT_TITLE))
      {
         // sorted by the discussion message subject
         result =
            ((EvaluationContentWrapper) o1)
               .getTitle()
               .compareToIgnoreCase(
                  ((EvaluationContentWrapper) o2)
                     .getTitle());
      }
      
      else if (criteria.equals(SORT_DATE))
      {
         // sorted by the date
         if (((EvaluationContentWrapper) o1)
            .getSubmittedDate()
            .before(
               ((EvaluationContentWrapper) o2)
                  .getSubmittedDate()))
         {
            result = -1;
         }
         else
         {
            result = 1;
         }
      }
      else if (criteria.equals(SORT_OWNER))
      {
         // sorted by the owner
         result =
            ((EvaluationContentWrapper) o1)
               .getOwner()
               .getSortName()
               .compareToIgnoreCase(
                  ((EvaluationContentWrapper) o2)
                     .getOwner()
                     .getSortName());
      }
      else if (criteria.equals(SORT_TYPE))
      {
         // sorted by the owner
         result =
            ((EvaluationContentWrapper) o1)
               .getEvalType()
               .compareToIgnoreCase(
                  ((EvaluationContentWrapper) o2)
                     .getEvalType());
      }
      

      // sort ascending or descending
      if (!asc)
      {
         result = -result;
      }
      return result;

   } // compare
   
}

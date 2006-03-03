/**********************************************************************************
* $URL: https://source.sakaiproject.org/svn/trunk/osp/osp-2.1/common/api/src/java/org/theospi/portfolio/shared/model/OspException.java $
* $Id: OspException.java 5557 2006-01-26 06:02:52Z john.ellis@rsmart.com $
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
package org.theospi.portfolio.help.model;

public class UnsupportedFileTypeException
   extends RuntimeException {

   /**
    *
    */
   public UnsupportedFileTypeException() {
      super();
   }

   /**
    * @param cause
    */
   public UnsupportedFileTypeException(Throwable cause) {
      super(cause);
   }

   /**
    * @param message
    */
   public UnsupportedFileTypeException(String message) {
      super(message);
   }

   /**
    * @param message
    * @param cause
    */
   public UnsupportedFileTypeException(String message, Throwable cause) {
      super(message, cause);
   }

}


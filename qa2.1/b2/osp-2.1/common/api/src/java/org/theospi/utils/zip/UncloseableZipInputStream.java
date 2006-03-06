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
package org.theospi.utils.zip;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.zip.ZipInputStream;
import java.io.InputStream;
import java.io.IOException;

public class UncloseableZipInputStream extends ZipInputStream {
   protected final transient Log logger = LogFactory.getLog(getClass());

   public UncloseableZipInputStream(InputStream in) {
      super(in);
   }

   /**
    * Closes the ZIP input stream.
    *
    * @throws java.io.IOException if an I/O error has occurred
    */
   public void close() throws IOException {
      // do nothing
   }

   public void reallyClose() throws IOException {
      super.close();
   }
}

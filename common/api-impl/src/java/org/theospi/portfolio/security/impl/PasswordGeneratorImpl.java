/**********************************************************************************
* $URL:https://source.sakaiproject.org/svn/osp/trunk/common/api-impl/src/java/org/theospi/portfolio/security/impl/PasswordGeneratorImpl.java $
* $Id:PasswordGeneratorImpl.java 9134 2006-05-08 20:28:42Z chmaurer@iupui.edu $
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
package org.theospi.portfolio.security.impl;


import java.util.Random;

import org.sakaiproject.metaobj.security.PasswordGenerator;

public class PasswordGeneratorImpl implements PasswordGenerator {
   private int length;
   public String generate() {
      return generate(getLength());
   }

   public String generate(int length) {
      Random rand = new Random();
      char[] pass = new char[length];
      for (int i = 0; i < length; i++) {
         int val = rand.nextInt(52);
         // need to add appropriate values to get to the ascii values
         if (val < 26)
            val += 65;
         else
            val += 71;
         pass[i] = (char) val;
      }
      return new String(pass);
   }

   public int getLength() {
      return length;
   }

   public void setLength(int length) {
      this.length = length;
   }
}

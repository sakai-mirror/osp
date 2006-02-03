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
package org.theospi.portfolio.guidance.model;

import org.sakaiproject.metaobj.shared.model.IdentifiableObject;

import java.util.Set;
import java.util.List;
import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Nov 11, 2005
 * Time: 12:06:34 PM
 * To change this template use File | Settings | File Templates.
 */
public class GuidanceItem extends IdentifiableObject {

   private String type;
   private String text = "";
   private Guidance guidance;
   private List attachments;

   public GuidanceItem() {
   }

   public GuidanceItem(Guidance guidance, String type) {
      this.type = type;
      this.guidance = guidance;
      attachments = new ArrayList();
   }

   public String getType() {
      return type;
   }

   public void setType(String type) {
      this.type = type;
   }

   public String getText() {
      return text;
   }

   public void setText(String text) {
      this.text = text;
   }

   public Guidance getGuidance() {
      return guidance;
   }

   public void setGuidance(Guidance guidance) {
      this.guidance = guidance;
   }

   /**
    * @return List of GuidanceItemAttachment
    */
   public List getAttachments() {
      return attachments;
   }

   /**
    * @param attachments List of GuidanceItemAttachment
    */
   public void setAttachments(List attachments) {
      this.attachments = attachments;
   }
}

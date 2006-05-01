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
package org.theospi.portfolio.guidance.tool;

import org.theospi.portfolio.guidance.model.GuidanceItem;
import org.theospi.portfolio.guidance.model.GuidanceItemAttachment;
import org.sakaiproject.service.legacy.filepicker.FilePickerHelper;
import org.sakaiproject.service.legacy.entity.Reference;
import org.sakaiproject.api.kernel.session.ToolSession;
import org.sakaiproject.api.kernel.session.cover.SessionManager;

import javax.faces.model.SelectItem;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Nov 14, 2005
 * Time: 4:53:06 PM
 * To change this template use File | Settings | File Templates.
 */
public class DecoratedGuidanceItem {

   private GuidanceItem base;
   private GuidanceTool tool;

   public DecoratedGuidanceItem(GuidanceTool tool, GuidanceItem base) {
      this.tool = tool;
      this.base = base;
   }

   public GuidanceItem getBase() {
      return base;
   }

   public void setBase(GuidanceItem base) {
      this.base = base;
   }

   public List getAttachments() {
      ToolSession session = SessionManager.getCurrentToolSession();
      if (session.getAttribute(FilePickerHelper.FILE_PICKER_CANCEL) == null &&
         session.getAttribute(FilePickerHelper.FILE_PICKER_ATTACHMENTS) != null &&
         session.getAttribute(GuidanceTool.ATTACHMENT_TYPE).equals(base.getType())) {

         List refs = (List)session.getAttribute(FilePickerHelper.FILE_PICKER_ATTACHMENTS);
         List newAttachments = new ArrayList();

         for(int i=0; i<refs.size(); i++) {
            Reference ref = (Reference) refs.get(i);
            Reference fullRef = tool.decorateReference(ref.getReference());
            GuidanceItemAttachment attachment = new GuidanceItemAttachment(base,
                           ref, fullRef);

            if (base.getAttachments().contains(attachment)) {
               attachment =
                  (GuidanceItemAttachment) base.getAttachments().get(base.getAttachments().indexOf(attachment));
            }

            newAttachments.add(attachment);
         }
         session.removeAttribute(FilePickerHelper.FILE_PICKER_ATTACHMENTS);
         session.removeAttribute(GuidanceTool.ATTACHMENT_TYPE);
         base.getAttachments().clear();
         base.getAttachments().addAll(newAttachments);
      }

      return base.getAttachments();
   }

   public String processActionManageAttachments() {
      return tool.processActionManageAttachments(base.getType());
   }

   public List getAttachmentLinks() {
      List attachments = getAttachments();
      List returned = new ArrayList();

      for (Iterator i=attachments.iterator();i.hasNext();) {
         GuidanceItemAttachment attachment = (GuidanceItemAttachment)i.next();
         SelectItem item = new SelectItem(attachment.getFullReference().getBase().getUrl(),
               attachment.getDisplayName());
         returned.add(item);
      }

      return returned;
   }
}

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

package org.theospi.portfolio.migration;

import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.sakaiproject.content.api.ContentHostingService;
import org.sakaiproject.content.api.ContentResource;
import org.sakaiproject.exception.IdUnusedException;
import org.sakaiproject.exception.InUseException;
import org.sakaiproject.exception.PermissionException;
import org.sakaiproject.exception.ServerOverloadException;
import org.sakaiproject.exception.TypeException;
import org.sakaiproject.metaobj.shared.mgt.MetaobjEntityManager;
import org.sakaiproject.tool.cover.SessionManager;

public class UpdateFormPropsJob implements Job {

   private ContentHostingService contentHosting;
   protected final transient Log logger = LogFactory.getLog(getClass());
   
   public void execute(JobExecutionContext arg0) throws JobExecutionException {
      logger.info("Quartz job started: "+this.getClass().getName());
      
      org.sakaiproject.tool.api.Session sakaiSession = SessionManager.getCurrentSession();
      String userId = sakaiSession.getUserId();
      sakaiSession.setUserId("admin");
      sakaiSession.setUserEid("admin");
      List resources = getContentHosting().getAllResources("/");
      logger.debug("Total Resources Found " + resources.size());
      
      int formCount = 0;
      for (Iterator i = resources.iterator(); i.hasNext();) {
         ContentResource resource = (ContentResource) i.next();
         try {
            if (resource.getContentType().equalsIgnoreCase("application/x-osp")) {
             
            getContentHosting().addProperty(resource.getId(), 
                  ContentHostingService.PROP_ALTERNATE_REFERENCE, 
                  MetaobjEntityManager.METAOBJ_ENTITY_PREFIX);
            formCount += 1;
            }
         } catch (PermissionException e) {
            logger.warn("Failed to update properties for resource: " + resource.getId(), e);
         } catch (IdUnusedException e) {
            logger.warn("Failed to update properties for resource: " + resource.getId(), e);
         } catch (TypeException e) {
            logger.warn("Failed to update properties for resource: " + resource.getId(), e);
         } catch (InUseException e) {
            logger.warn("Failed to update properties for resource: " + resource.getId(), e);
         } catch (ServerOverloadException e) {
            logger.warn("Failed to update properties for resource: " + resource.getId(), e);
         }
      }
      logger.debug("Forms found " + formCount);
      sakaiSession.setUserEid(userId);
      sakaiSession.setUserId(userId);
      logger.info("Quartz job finished: "+this.getClass().getName());
   }

   public ContentHostingService getContentHosting() {
      return contentHosting;
   }

   public void setContentHosting(ContentHostingService contentHosting) {
      this.contentHosting = contentHosting;
   }

}

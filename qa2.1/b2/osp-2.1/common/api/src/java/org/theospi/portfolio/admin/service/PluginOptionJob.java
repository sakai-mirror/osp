package org.theospi.portfolio.admin.service;

import org.quartz.*;
import org.theospi.portfolio.admin.intf.SakaiIntegrationPlugin;
import org.theospi.portfolio.admin.model.IntegrationOption;
import org.sakaiproject.api.app.scheduler.SchedulerManager;
import org.sakaiproject.api.kernel.session.cover.SessionManager;
import org.sakaiproject.api.kernel.component.cover.ComponentManager;

import java.util.Date;
import java.util.Map;
import java.util.Iterator;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Mar 2, 2006
 * Time: 10:52:35 PM
 * To change this template use File | Settings | File Templates.
 */
public class PluginOptionJob implements Job {

   public final static String PLUGIN = "org.theospi.portfolio.admin.service.PluginOptionJob.job";
   public final static String INTERVAL = "org.theospi.portfolio.admin.service.PluginOptionJob.interval";

   public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
      org.sakaiproject.api.kernel.session.Session sakaiSession = SessionManager.getCurrentSession();
      String userId = sakaiSession.getUserId();

      try {
         sakaiSession.setUserId("admin");
         sakaiSession.setUserEid("admin");
         Map details = jobExecutionContext.getJobDetail().getJobDataMap();
         SchedulerManager manager = (SchedulerManager) ComponentManager.get(SchedulerManager.class);
         String pluginId = (String) details.get(PLUGIN);
         SakaiIntegrationPlugin plugin = (SakaiIntegrationPlugin) ComponentManager.get(pluginId);
         long millis = Long.parseLong((String) details.get(INTERVAL));

         boolean done = true;
         for (Iterator i=plugin.getPotentialIntegrations().iterator();i.hasNext();) {
            if (!plugin.executeOption((IntegrationOption) i.next())) {
               done = false;
               break;
            }
         }

         if (!done) {
            try {
               schedule(manager, pluginId, millis);
            } catch (SchedulerException e) {
               throw new JobExecutionException(e);
            }
         }
      } finally {
         sakaiSession.setUserEid(userId);
         sakaiSession.setUserId(userId);
      }
   }

   public static void schedule(SchedulerManager manager, String pluginId, long millis) throws SchedulerException {
      JobDetail detail = new JobDetail(pluginId + "." + System.currentTimeMillis(),
         PluginOptionJob.class.toString(), PluginOptionJob.class);
      detail.getJobDataMap().put(PLUGIN, pluginId);
      detail.getJobDataMap().put(INTERVAL, millis + "");
      manager.getScheduler().scheduleJob(detail, new SimpleTrigger(pluginId + ".tr." + System.currentTimeMillis(),
         PluginOptionJob.class.toString(), new Date(System.currentTimeMillis() + millis)));
   }
}

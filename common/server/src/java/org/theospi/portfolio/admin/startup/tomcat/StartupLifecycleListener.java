package org.theospi.portfolio.admin.startup.tomcat;

import org.apache.catalina.Context;
import org.apache.catalina.Host;
import org.apache.catalina.Lifecycle;
import org.apache.catalina.LifecycleEvent;
import org.apache.catalina.LifecycleListener;
import org.theospi.portfolio.admin.startup.ServerListeningService;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Apr 10, 2006
 * Time: 3:59:22 PM
 * To change this template use File | Settings | File Templates.
 */
public class StartupLifecycleListener implements LifecycleListener {

   public void lifecycleEvent(LifecycleEvent lifecycleEvent) {
      if (lifecycleEvent.getSource() instanceof Context &&
         lifecycleEvent.getType().equals(Lifecycle.BEFORE_START_EVENT)) {

         Context context = (Context) lifecycleEvent.getSource();
         Lifecycle parent = (Lifecycle) context.getParent();
         parent.addLifecycleListener(this);
      }
      else if (lifecycleEvent.getSource() instanceof Host &&
         lifecycleEvent.getType().equals(Lifecycle.AFTER_START_EVENT)) {
         ServerListeningService.getInstance().triggerEvent(ServerListeningService.SERVER_STARTUP_COMPLETE);
      }
      else if (lifecycleEvent.getSource() instanceof Host &&
         lifecycleEvent.getType().equals(Lifecycle.BEFORE_STOP_EVENT)) {
         ServerListeningService.getInstance().triggerEvent(ServerListeningService.SERVER_SHUTDOWN_STARTED);
      }
   }
}

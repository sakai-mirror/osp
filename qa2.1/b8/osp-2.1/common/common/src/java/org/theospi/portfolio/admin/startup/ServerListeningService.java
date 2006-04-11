package org.theospi.portfolio.admin.startup;

import java.util.List;
import java.util.Observer;
import java.util.ArrayList;
import java.util.Iterator;


/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Apr 10, 2006
 * Time: 6:21:53 PM
 * To change this template use File | Settings | File Templates.
 */
public class ServerListeningService {

   public static final String SERVER_STARTUP_COMPLETE = "startupComplete";
   public static final String SERVER_SHUTDOWN_STARTED = "shutdownStarted";

   private static ServerListeningService singleton = new ServerListeningService();

   private List listeners = new ArrayList();

   public static ServerListeningService getInstance() {
      return singleton;
   }

   public void addListener(ServerListener listener) {
      listeners.add(listener);
   }

   public void triggerEvent(String event) {
      for (Iterator i=listeners.iterator();i.hasNext();) {
         ServerListener listener = (ServerListener) i.next();
         listener.triggerEvent(event);
      }
   }

}

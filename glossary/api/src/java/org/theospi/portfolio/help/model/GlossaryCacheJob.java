package org.theospi.portfolio.help.model;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.sakaiproject.component.cover.ComponentManager;

/**
 * Created by IntelliJ IDEA.
 * User: johnellis
 * Date: Feb 13, 2007
 * Time: 9:09:32 AM
 * To change this template use File | Settings | File Templates.
 */
public class GlossaryCacheJob implements Job {

   public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
      Glossary glossary = (Glossary) ComponentManager.get("glossary");
      glossary.checkCache();
   }
}

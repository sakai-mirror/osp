package org.theospi.portfolio.admin.service;

import java.util.Properties;
import java.util.Set;

import org.sakaiproject.tool.api.Tool;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Mar 2, 2006
 * Time: 4:37:02 PM
 * To change this template use File | Settings | File Templates.
 */
public class ToolWrapper implements Tool {

   private String id;

   public ToolWrapper(String id) {
      this.id = id;
   }

   public String getId() {
      return id;
   }

   public String getTitle() {
      return null;
   }

   public String getDescription() {
      return null;
   }

   public String getHome() {
	   return null;
   }

   public Properties getRegisteredConfig() {
      return null;
   }

   public Properties getMutableConfig() {
      return null;
   }

   public Properties getFinalConfig() {
      return null;
   }

   public Set getKeywords() {
      return null;
   }

   public Set getCategories() {
      return null;
   }

   public AccessSecurity getAccessSecurity() {
      return null;
   }
}

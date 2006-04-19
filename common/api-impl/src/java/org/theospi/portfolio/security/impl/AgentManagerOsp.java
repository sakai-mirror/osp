package org.theospi.portfolio.security.impl;

import org.sakaiproject.metaobj.shared.model.Agent;
import org.sakaiproject.metaobj.shared.model.OspException;
import org.sakaiproject.service.legacy.user.UserEdit;
import org.theospi.portfolio.shared.model.AgentImplOsp;

/**
 * Created by IntelliJ IDEA.
 * User: bbiltimier
 * Date: Apr 18, 2006
 * Time: 3:35:28 PM
 * To change this template use File | Settings | File Templates.
 */
public class AgentManagerOsp extends org.sakaiproject.metaobj.security.impl.sakai.AgentManager{
    public Agent createAgent(Agent agent) {
      if (!agent.isInRole(Agent.ROLE_GUEST)) {
         // we don't support creating real agents
         throw new UnsupportedOperationException();
      }

      try {
         UserEdit uEdit = org.sakaiproject.service.legacy.user.cover.UserDirectoryService.addUser(agent.getId().getValue());

         //set email address
         uEdit.setEmail(agent.getId().getValue());

         // set id
         uEdit.setId(agent.getId().getValue());

         // set the guest user type
         uEdit.setType("guest");

         String pw = getPasswordGenerator().generate();
         uEdit.setPassword(pw);
         org.sakaiproject.service.legacy.user.cover.UserDirectoryService.commitEdit(uEdit);


         AgentImplOsp impl = (AgentImplOsp) agent;
         impl.setPassword(pw);


         return agent;
      }
      catch (RuntimeException exp) {
         throw exp;
      }
      catch (Exception exp) {
         throw new OspException(exp);
      }
   }



}

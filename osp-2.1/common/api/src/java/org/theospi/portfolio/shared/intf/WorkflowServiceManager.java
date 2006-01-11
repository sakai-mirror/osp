package org.theospi.portfolio.shared.intf;

import org.sakaiproject.metaobj.shared.model.Id;

public interface WorkflowServiceManager {
   
   public void processWorkflow(Id workflowId, Id actionObjId);

}

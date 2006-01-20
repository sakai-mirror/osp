package org.theospi.portfolio.workflow.mgt;

import java.util.List;

import org.sakaiproject.metaobj.shared.model.Id;
import org.sakaiproject.service.legacy.entity.Reference;
import org.theospi.portfolio.workflow.model.Workflow;

public interface WorkflowManager {

   public final static String CURRENT_WORKFLOW = "org.theospi.portfolio.workflow.currentWorkflow";
   public final static String CURRENT_WORKFLOW_ID = "org.theospi.portfolio.workflow.currentWorkflowId";

   public Workflow createNew(String description, String siteId, Id securityQualifier,
                             String securityViewFunction, String securityEditFunction);

   public Workflow getWorkflow(Id workflowId);

   public Workflow saveWorkflow(Workflow workflow);

   public void deleteWorkflow(Workflow workflow);

   public Reference decorateReference(Workflow workflow, String reference);

   public List listWorkflows(String siteId);

   public Workflow getWorkflow(String id);
}

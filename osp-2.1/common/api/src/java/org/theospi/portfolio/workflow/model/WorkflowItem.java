package org.theospi.portfolio.workflow.model;

import org.sakaiproject.metaobj.shared.model.Id;
import org.sakaiproject.metaobj.shared.model.IdentifiableObject;


public class WorkflowItem extends IdentifiableObject {

   public final static int NOTIFICATION_WORKFLOW = 0;
   public final static int STATUS_CHANGE_WORKFLOW = 1;
   public final static int CONTENT_LOCKING_WORKFLOW = 2;
   
   public final static String CONTENT_LOCKING_LOCK = "LOCK";
   public final static String CONTENT_UNLOCKING_UNLOCK = "UNLOCK";
   
   
   private int actionType;
   private Id actionObjectId;
   private String actionValue;
   private Workflow workflow;
   
   /**
    * @return Returns the action.
    */
   public int getActionType() {
      return actionType;
   }
   /**
    * @param action The action to set.
    */
   public void setActionType(int actionType) {
      this.actionType = actionType;
   }
   /**
    * @return Returns the actionObjectId.
    */
   public Id getActionObjectId() {
      return actionObjectId;
   }
   /**
    * @param actionObjectId The actionObjectId to set.
    */
   public void setActionObjectId(Id actionObjectId) {
      this.actionObjectId = actionObjectId;
   }
   /**
    * @return Returns the actionValue.
    */
   public String getActionValue() {
      return actionValue;
   }
   /**
    * @param actionValue The actionValue to set.
    */
   public void setActionValue(String actionValue) {
      this.actionValue = actionValue;
   }
   /**
    * @return Returns the workflow.
    */
   public Workflow getWorkflow() {
      return workflow;
   }
   /**
    * @param workflow The workflow to set.
    */
   public void setWorkflow(Workflow workflow) {
      this.workflow = workflow;
   }
   
   
   
}

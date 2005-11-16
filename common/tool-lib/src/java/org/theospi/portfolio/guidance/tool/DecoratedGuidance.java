package org.theospi.portfolio.guidance.tool;

import org.theospi.portfolio.guidance.model.Guidance;
import org.theospi.portfolio.guidance.model.GuidanceItem;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Nov 14, 2005
 * Time: 4:52:55 PM
 * To change this template use File | Settings | File Templates.
 */
public class DecoratedGuidance {
   private Guidance base;
   private GuidanceTool tool;

   public DecoratedGuidance(GuidanceTool tool, Guidance base) {
      this.base = base;
      this.tool = tool;
   }

   public Guidance getBase() {
      return base;
   }

   public void setBase(Guidance base) {
      this.base = base;
   }

   protected DecoratedGuidanceItem getItem(String type) {
      return new DecoratedGuidanceItem(tool, base.getItem(type));
   }

   public DecoratedGuidanceItem getInstruction() {
      return getItem(Guidance.INSTRUCTION_TYPE);
   }

   public DecoratedGuidanceItem getExample() {
      return getItem(Guidance.EXAMPLE_TYPE);
   }

   public DecoratedGuidanceItem getRationale() {
      return getItem(Guidance.RATIONALE_TYPE);
   }

   public String processActionEdit() {
      return tool.processActionEdit(base);
   }

   public String processActionView() {
      return tool.processActionView(base);
   }

   public String processActionDelete() {
      return tool.processActionDelete(base);
   }
}

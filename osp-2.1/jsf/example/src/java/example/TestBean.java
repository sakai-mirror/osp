package example;

public class TestBean {

   private String label = "Select Content";
   private boolean disabled = true;
   private boolean rendered = false;
   private String currentStep = "1";
   
   
   public String getCurrentStep() {
      return currentStep;
   }
   public void setCurrentStep(String currentStep) {
      this.currentStep = currentStep;
   }
   public boolean isDisabled() {
      return disabled;
   }
   public void setDisabled(boolean disabled) {
      this.disabled = disabled;
   }
   public String getLabel() {
      return label;
   }
   public void setLabel(String label) {
      this.label = label;
   }
   public boolean isRendered() {
      return rendered;
   }
   public void setRendered(boolean rendered) {
      this.rendered = rendered;
   }
   
   
}

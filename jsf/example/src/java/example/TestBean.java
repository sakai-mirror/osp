package example;

import javax.faces.event.ActionEvent;
import java.util.List;
import java.util.ArrayList;

public class TestBean {

   private String label = "Select Content";
   private boolean disabled = true;
   private boolean rendered = false;
   private String currentStep = "1";
   private List subBeans = new ArrayList();

   public TestBean() {
      for (int i=0;i<10;i++) {
         subBeans.add(new TestSubBean());
      }
   }

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

   public void processTestButton(ActionEvent event) {
      int i = 1;

      i++;
   }

   public List getSubBeans() {
      return subBeans;
   }

   public void setSubBeans(List subBeans) {
      this.subBeans = subBeans;
   }

}

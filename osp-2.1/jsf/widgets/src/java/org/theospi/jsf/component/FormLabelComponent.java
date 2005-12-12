package org.theospi.jsf.component;

import javax.faces.component.UIOutput;

public class FormLabelComponent extends UIOutput {
   
   private String valueRequired = "false";
   
   public FormLabelComponent()
   {
      super();
      this.setRendererType("org.theospi.FormLabel");
   }

   public String getValueRequired() {
      return valueRequired;
   }

   public void setValueRequired(String valueRequired) {
      this.valueRequired = valueRequired;
   }

}

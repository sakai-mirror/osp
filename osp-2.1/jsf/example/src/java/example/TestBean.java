package example;

import org.theospi.jsf.intf.XmlTagFactory;
import org.theospi.jsf.impl.DefaultXmlTagFactory;
import org.theospi.jsf.impl.DefaultXmlTagHandler;

import javax.faces.event.ActionEvent;
import java.util.List;
import java.util.ArrayList;
import java.io.InputStream;

import example.xml.TestXmlTagFactory;

public class TestBean {

   private String label = "Select Content";
   private boolean disabled = true;
   private boolean rendered = false;
   private String currentStep = "1";
   private List subBeans = new ArrayList();
   private XmlTagFactory factory = null;

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
      getSubBeans().add(new TestSubBean());
   }

   public List getSubBeans() {
      return subBeans;
   }

   public void setSubBeans(List subBeans) {
      this.subBeans = subBeans;
   }

   public XmlTagFactory getFactory() {
      if (factory == null) {
         factory = new TestXmlTagFactory();
         ((DefaultXmlTagFactory)factory).setDefaultHandler(new DefaultXmlTagHandler(factory));
      }
      return factory;
   }

   public InputStream getSampleXmlFile() {
      return this.getClass().getResourceAsStream("/xmlDocTagSample.xhtml");
   }

}

<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ taglib uri="http://sakaiproject.org/jsf/sakai" prefix="sakai" %>
<%@ taglib uri="http://www.theospi.org/jsf/osp" prefix="ospx" %>

<%
      response.setContentType("text/html; charset=UTF-8");
      response.addDateHeader("Expires", System.currentTimeMillis() - (1000L * 60L * 60L * 24L * 365L));
      response.addDateHeader("Last-Modified", System.currentTimeMillis());
      response.addHeader("Cache-Control", "no-store, no-cache, must-revalidate, max-age=0, post-check=0, pre-check=0");
      response.addHeader("Pragma", "no-cache");
%>

<f:view>
<f:loadBundle basename="org.theospi.portfolio.wizard.messages" var="msgs"/>
<sakai:view>

<ospx:wizardSteps currentStep="4">
   <ospx:wizardStep label="#{msgs.wizard_step_begin}" />
   <ospx:wizardStep label="#{msgs.wizard_step_select}" />
   <ospx:wizardStep label="#{msgs.wizard_step_support}" />
   <ospx:wizardStep label="#{msgs.wizard_step_design}" />
   <ospx:wizardStep label="#{msgs.wizard_step_properties}" />
</ospx:wizardSteps>

<h:form>

   <sakai:view_title value="#{msgs.edit_wizard}"/>
   <sakai:instruction_message value="Guidance Test Tool" />
<%--   <sakai:instruction_message value=" Last saved: " />
   <sakai:instruction_message value="#{wizard.lastSavedId}" /> --%>
   <sakai:messages />
   
   <%@include file="wizardPropertiesFrame.jspf"%>
   <h:selectBooleanCheckbox id="asTool" value="#{wizard.current.base.exposeAsTool}" />
   <h:outputLabel value="#{msgs.expose_as_tool}" for="asTool" />
   <sakai:button_bar>
      <sakai:button_bar_item id="cancel" value="#{msgs.cancel_wizard}" action="#{wizard.processActionCancel}" immediate="true" />
      <sakai:button_bar_item id="submit" value="#{msgs.save_continue_wizard}" 
         action="#{wizard.processActionSave}" />
   </sakai:button_bar>
</h:form>
</sakai:view>

</f:view>
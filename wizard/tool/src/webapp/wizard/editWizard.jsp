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
<h:form styleClass="portletBody">

<ospx:wizardSteps currentStep="0">
   <ospx:wizardStep label="#{msgs.wizard_step_begin}" />
   <ospx:wizardStep label="#{msgs.wizard_step_select}" />
   <ospx:wizardStep label="#{msgs.wizard_step_support}" />
   <ospx:wizardStep label="#{msgs.wizard_step_design}" />
   <ospx:wizardStep label="#{msgs.wizard_step_properties}" />
</ospx:wizardSteps>

   <sakai:view_title value="#{msgs.edit_wizard}"/>
   <sakai:instruction_message value="#{msgs.wizard_instructions}" />
<%--   <sakai:instruction_message value=" Last saved: " />
   <sakai:instruction_message value="#{wizard.lastSavedId}" /> --%>
   <sakai:messages />
   <sakai:panel_edit>

      <h:outputLabel for="name" id="nameLabel" value="#{msgs.wizard_name}" />
      <h:panelGroup>
         <h:inputText id="name" value="#{wizard.current.base.name}" required="true">
            <f:validateLength minimum="1" maximum="255" />
         </h:inputText>
         <h:message for="name" styleClass="validationEmbedded" />
      </h:panelGroup>
      <h:outputLabel for="description" id="descriptionLabel" value="#{msgs.wizard_description}" />
      <h:panelGroup>
         <h:inputText id="description" value="#{wizard.current.base.description}" required="true">
            <f:validateLength minimum="1" maximum="255" />
         </h:inputText>
         <h:message for="description" styleClass="validationEmbedded" />
      </h:panelGroup>
      <h:outputLabel for="keywords" id="keywordsLabel" value="#{msgs.wizard_keywords}" />
      <h:panelGroup>
         <h:inputText id="keywords" value="#{wizard.current.base.keywords}" required="true">
            <f:validateLength minimum="1" maximum="255" />
         </h:inputText>
         <h:message for="keywords" styleClass="validationEmbedded" />
      </h:panelGroup>
   </sakai:panel_edit>
   <sakai:button_bar>
      <sakai:button_bar_item id="cancel" value="#{msgs.cancel_wizard}" action="#{wizard.processActionCancel}" immediate="true" />
      <sakai:button_bar_item id="submit" value="#{msgs.save_continue_wizard}" 
         action="#{wizard.processActionGoToEditWizardSupport}" />
   </sakai:button_bar>
</h:form>
</sakai:view>

</f:view>

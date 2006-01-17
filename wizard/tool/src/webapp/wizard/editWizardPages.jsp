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

<ospx:wizardSteps currentStep="1">
   <ospx:wizardStep label="#{msgs.wizard_step_begin}" />
   <ospx:wizardStep label="#{msgs.wizard_step_select}" />
   <ospx:wizardStep label="#{msgs.wizard_step_support}" />
   <ospx:wizardStep label="#{msgs.wizard_step_design}" />
   <ospx:wizardStep label="#{msgs.wizard_step_properties}" />
</ospx:wizardSteps>

   <sakai:tool_bar>
      <sakai:tool_bar_item
         rendered="#{wizard.current.base.type == 'org.theospi.portfolio.wizard.model.Wizard.sequential'}"
         action="#{wizard.current.rootCategory.processActionNewPage}"
         value="#{msgs.new_wizard_page}" />
      <sakai:tool_bar_item
         rendered="#{wizard.current.base.type == 'org.theospi.portfolio.wizard.model.Wizard.hierarchical'}"
         action="#{wizard.current.processActionNewCategory}"
         value="#{msgs.new_wizard_category}" />
   </sakai:tool_bar>

   <sakai:view_title value="#{msgs.edit_wizard_content}"/>
   <sakai:instruction_message value="#{msgs.wizard_instructions}" />
<%--   <sakai:instruction_message value=" Last saved: " />
   <sakai:instruction_message value="#{wizard.lastSavedId}" /> --%>
   <sakai:messages />

   <sakai:flat_list value="#{wizard.current.rootCategory.categoryPageList}" var="item">
      <h:column>
         <f:facet name="header">
            <h:outputText value="#{msgs.user_column_header}" />
         </f:facet>
         <h:selectBooleanCheckbox id="itemSelect" value="#{item.selected}" />
         <h:outputLabel value="#{item.title}" for="itemSelect" />
      </h:column>
      <h:column>
         <f:facet name="header">
            <h:outputText value="#{msgs.re_order}" />
         </f:facet>
         <h:commandLink action="#{item.moveUp}" rendered="#{item.base.sequence != 0}">
            <h:graphicImage value="/img/arrowUp.gif" />
         </h:commandLink>
         <h:commandLink action="#{item.moveDown}" rendered="#{!item.last}">
            <h:graphicImage value="/img/arrowDown.gif" />
         </h:commandLink>
      </h:column>
   </sakai:flat_list>

   <sakai:button_bar>
      <sakai:button_bar_item id="cancel" value="#{msgs.cancel_wizard}" action="#{wizard.processActionCancel}" immediate="true" />
      <sakai:button_bar_item id="submit" value="#{msgs.save_continue_wizard}" 
         action="#{wizard.processActionGoToEditWizardSupport}" />
   </sakai:button_bar>
</h:form>
</sakai:view>

</f:view>

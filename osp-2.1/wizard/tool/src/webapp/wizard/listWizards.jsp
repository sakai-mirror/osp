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
<h:form>
   <sakai:tool_bar>
      <sakai:tool_bar_item
      action="#{wizard.processActionNew}"
      value="#{msgs.new_wizard}" />
   </sakai:tool_bar>

   <sakai:view_title value="#{msgs.wizard_title}"/>
   <sakai:instruction_message value="Guidance Test Tool" />
<%--   <sakai:instruction_message value=" Last saved: " />
   <sakai:instruction_message value="#{wizard.lastSavedId}" /> --%>
   <sakai:messages />


   <sakai:flat_list value="#{wizard.wizards}" var="wizardItem">
      <h:column>
         <f:facet name="header">
            <h:outputText value="#{msgs.wizards}" />
         </f:facet>
      </h:column>
      <h:column>
         <h:outputText value="#{wizardItem.base.name}"/>
         <h:commandLink action="#{wizardItem.processActionEdit}">
            <h:outputText value="#{msgs.edit}"/>
         </h:commandLink>
         <h:outputText value=" | " />
         <h:commandLink action="#{wizardItem.processActionDelete}">
            <h:outputText value="#{msgs.delete}" />
         </h:commandLink>
      </h:column>
   </sakai:flat_list>
   
</h:form>
</sakai:view>

</f:view>

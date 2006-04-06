<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ taglib uri="http://sakaiproject.org/jsf/sakai" prefix="sakai" %>
<%@ taglib uri="http://www.theospi.org/jsf/osp" prefix="ospx" %>
<%@ include file="/WEB-INF/jsp/include.jsp" %>

<%
      response.setContentType("text/html; charset=UTF-8");
      response.addDateHeader("Expires", System.currentTimeMillis() - (1000L * 60L * 60L * 24L * 365L));
      response.addDateHeader("Last-Modified", System.currentTimeMillis());
      response.addHeader("Cache-Control", "no-store, no-cache, must-revalidate, max-age=0, post-check=0, pre-check=0");
      response.addHeader("Pragma", "no-cache");
%>

<f:view>
<f:loadBundle basename="org.theospi.portfolio.wizard.bundle.Messages" var="msgs"/>
<sakai:view>
<h:form styleClass="portletBody">

   <%@include file="steps.jspf"%>

   <sakai:view_title value="#{msgs.edit_wizard}" rendered='#{!wizard.current.newWizard}'/>
   <sakai:view_title value="#{msgs.add_wizard}"  rendered='#{wizard.current.newWizard}'/>
    
   <sakai:instruction_message value="#{msgs.wizard_instructions}" />
<%--   <sakai:instruction_message value=" Last saved: " />
   <sakai:instruction_message value="#{wizard.lastSavedId}" /> --%>
   <sakai:messages />
   <sakai:panel_edit>

      <h:outputLabel for="name" id="nameLabel" value="#{msgs.wizard_name}" />
      <h:panelGroup>
         <h:inputText id="name" value="#{wizard.current.base.name}" required="true" size="30">
            <f:validateLength minimum="1" maximum="255" />
         </h:inputText>
         <h:message for="name" styleClass="validationEmbedded" />
      </h:panelGroup>
      <h:outputLabel for="description" id="descriptionLabel" value="#{msgs.wizard_description}" />
      <h:panelGroup>
         <h:inputTextarea id="description" value="#{wizard.current.base.description}" cols="60" rows="6">
            <f:validateLength minimum="1" maximum="1024" />
         </h:inputTextarea>
         <h:message for="description" styleClass="validationEmbedded" />
      </h:panelGroup>
      <h:outputLabel for="keywords" id="keywordsLabel" value="#{msgs.wizard_keywords}" />
      <h:panelGroup>
         <h:inputTextarea id="keywords" value="#{wizard.current.base.keywords}" cols="60" rows="6">
            <f:validateLength minimum="1" maximum="1024" />
         </h:inputTextarea>
         <h:message for="keywords" styleClass="validationEmbedded" />
      </h:panelGroup>
      <h:outputLabel for="styleFile" id="styleLabel" value="#{msgs.wizard_style}" />
      <h:panelGroup>
         <h:inputText id="styleFile" value="#{wizard.current.styleName}" 
               readonly="true" disabled="true" required="false" />
         <h:commandLink action="#{wizard.current.processActionSelectStyle}" immediate="true">
            <h:outputText value="#{msgs.select_style}"/>
         </h:commandLink>
      </h:panelGroup>
      <h:panelGroup>
	   <h:selectBooleanCheckbox id="asTool" value="#{wizard.current.exposeAsTool}" />
	   <h:outputLabel value="#{msgs.expose_as_tool}" for="asTool" />
      </h:panelGroup>
   </sakai:panel_edit>

   <%@include file="builderButtons.jspf"%>

</h:form>
</sakai:view>

</f:view>

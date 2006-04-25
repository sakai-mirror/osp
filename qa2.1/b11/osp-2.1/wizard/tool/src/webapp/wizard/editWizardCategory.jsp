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
<sakai:view>
<h:form styleClass="portletBody">

   <%@include file="steps.jspf"%>

   <sakai:view_title value="#{msgs.edit_wizard_category}" rendered='#{wizard.currentCategory.base.id != null}'/>
   <sakai:view_title value="#{msgs.add_wizard_category}"  rendered='#{wizard.currentCategory.base.id == null}'/>

   <sakai:instruction_message value="#{msgs.wizard_category_instructions}" />
<%--   <sakai:instruction_message value=" Last saved: " />
   <sakai:instruction_message value="#{wizard.lastSavedId}" /> --%>
   <sakai:messages />

   <sakai:panel_edit>

      <h:outputLabel for="title" id="titleLabel" value="#{msgs.wizard_category_title}" />
      <h:panelGroup>
         <h:inputText id="title" value="#{wizard.currentCategory.base.title}" required="true">
            <f:validateLength minimum="1" maximum="255" />
         </h:inputText>
         <h:message for="title" styleClass="validationEmbedded" />
      </h:panelGroup>
      <h:outputLabel for="description" id="descriptionLabel" value="#{msgs.wizard_category_description}" />
      <h:panelGroup>
         <h:inputTextarea id="description" value="#{wizard.currentCategory.base.description}">
            <f:validateLength minimum="0" maximum="255" />
         </h:inputTextarea>
         <h:message for="description" styleClass="validationEmbedded" />
      </h:panelGroup>
      <h:outputLabel for="keywords" id="keywordsLabel" value="#{msgs.wizard_category_keywords}" />
      <h:panelGroup>
         <h:inputTextarea id="keywords" value="#{wizard.currentCategory.base.keywords}">
            <f:validateLength minimum="0" maximum="255" />
         </h:inputTextarea>
         <h:message for="keywords" styleClass="validationEmbedded" />
      </h:panelGroup>
   </sakai:panel_edit>

   <sakai:button_bar>
      <sakai:button_bar_item id="submit" value="#{msgs.save_wizard_category}"
         action="#{wizard.currentCategory.processActionSave}" />
      <sakai:button_bar_item id="cancel" value="#{msgs.cancel_wizard_category}"
         action="#{wizard.currentCategory.processActionCancel}" immediate="true" />
   </sakai:button_bar>
</h:form>
</sakai:view>

</f:view>

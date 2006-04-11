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
<sakai:view>
<h:form>
   <sakai:tool_bar>
      <sakai:tool_bar_item rendered="#{wizard.canCreate && wizard.canView}"
      action="#{wizard.processActionNew}"
      value="#{msgs.new_wizard}" />

      <sakai:tool_bar_item rendered="#{wizard.maintainer}"
          action="#{wizard.processPermissions}"
          value="#{msgs.permissions_link}" />
          
      <sakai:tool_bar_item rendered="#{wizard.canCreate && wizard.canView}"
          action="#{wizard.importWizard}"
          value="#{msgs.import}" />

   </sakai:tool_bar>

   <sakai:view_title value="#{msgs.wizard_title}"/>
   <sakai:instruction_message value="#{msgs.wizard_instruction_message}" />
<%--   <sakai:instruction_message value=" Last saved: " />
   <sakai:instruction_message value="#{wizard.lastSavedId}" /> --%>
   <sakai:messages />
   
   
   <h:selectOneMenu id="users" immediate="true" value="#{wizard.currentUserId}">
      <f:selectItems value="#{wizard.userListForSelect}"/>
   </h:selectOneMenu>
   <sakai:button_bar>
      <sakai:button_bar_item id="go" value="#{msgs.go}"
         action="#{wizard.processActionChangeUser}" />
   </sakai:button_bar>
   
   <%@include file="showWizardOwnerMessage.jspf"%>
   
   <h:dataTable  value="#{wizard.wizards}" var="wizardItem" styleClass="lines listHier" headerClass="exclude">
      <h:column>
         <f:facet name="header">
            <h:outputText value="#{msgs.wizards}" />
         </f:facet>
         <h:outputText value="#{wizardItem.base.name}"/>
         <f:subview id="viewPermss" rendered="#{wizard.canView}">
			<f:verbatim><div class="itemAction"></f:verbatim>
	           <f:subview id="runLink" rendered="#{wizardItem.base.published || wizardItem.owner}">
	              <h:commandLink action="#{wizardItem.processActionRunWizard}">
	                  <h:outputText value="#{msgs.run_wizard}"/>
	              </h:commandLink>
	           </f:subview>
	           <f:subview id="editLink" rendered="#{wizardItem.canEdit}">
	              <h:outputText value=" | "  rendered="#{wizardItem.base.published || wizardItem.owner}" />
	              <h:commandLink action="#{wizardItem.processActionEdit}">
	                 <h:outputText value="#{msgs.edit}"/>
	              </h:commandLink>
	           </f:subview>
	           <f:subview id="exportLink" rendered="#{wizardItem.canExport}">
	              <h:outputText value=" | " />
	              <h:outputLink value="#{wizardItem.currentExportLink}">
	                  <h:outputText value="#{msgs.export}"/>
	              </h:outputLink>
	           </f:subview>
	           <f:subview id="deleteLink" rendered="#{wizardItem.canDelete}">
	              <h:outputText value=" | " />
	              <h:commandLink action="#{wizardItem.processActionDelete}">
	                 <h:outputText value="#{msgs.delete}" />
	              </h:commandLink>
	           </f:subview>
	           <f:subview id="publishLink" rendered="#{wizardItem.canPublish}">
	              <h:outputText value=" | " />
	                 <h:commandLink action="#{wizardItem.processActionPublish}">
	                 <h:outputText value="#{msgs.publish}" />
	              </h:commandLink>
	           </f:subview>
			<f:verbatim></div></f:verbatim>
         </f:subview>
      </h:column>
      <h:column>
         <f:facet name="header">
            <h:outputText value="#{msgs.wizard_description}" />
         </f:facet>
         <h:outputText value="#{wizardItem.concatDescription}"/>
      </h:column>
      <h:column>
         <f:facet name="header">
            <h:outputText value="#{msgs.re_order}" />
         </f:facet>
         <h:commandLink action="#{wizardItem.moveUp}" rendered="#{!wizardItem.first}">
            <h:graphicImage value="/img/arrowUp.gif" />
         </h:commandLink>
	     <f:subview id="publishLink" rendered="#{wizardItem.first}">
	        <h:outputText value="&nbsp;&nbsp;&nbsp;&nbsp;" escape="false" />
	     </f:subview>
         <h:commandLink action="#{wizardItem.moveDown}" rendered="#{!wizardItem.last}">
            <h:graphicImage value="/img/arrowDown.gif" />
         </h:commandLink>
      </h:column>
   </h:dataTable>
   
</h:form>
</sakai:view>

</f:view>

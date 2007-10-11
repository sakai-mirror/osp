<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ taglib uri="http://sakaiproject.org/jsf/sakai" prefix="sakai" %>
<%@ taglib uri="http://www.theospi.org/jsf/osp" prefix="ospx" %>
<%@ taglib uri="http://www.theospi.org/help/jsf" prefix="help" %>

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

<sakai:tool_bar rendered="#{wizard.canCreate ||  wizard.maintainer}">
      <sakai:tool_bar_item rendered="#{wizard.canCreate}"
      action="#{wizard.processActionNew}"
      value="#{msgs.new_wizard}" />
          
      <sakai:tool_bar_item rendered="#{wizard.canCreate}"
          action="#{wizard.importWizard}"
          value="#{msgs.import}" />

      <sakai:tool_bar_item rendered="#{wizard.maintainer}"
          action="#{wizard.processPermissions}"
          value="#{msgs.permissions_link}" />

   </sakai:tool_bar>
   <sakai:view_title value="#{msgs.wizard_title}" rendered="#{wizard.canCreate ||  wizard.maintainer}"/>
   <sakai:view_title value="#{msgs.wizard_title_user}" rendered="#{not (wizard.canCreate ||  wizard.maintainer)}"/>
   <%--
   <sakai:instruction_message value="#{msgs.wizard_instruction_message}" />
  --%>  
<%--   <sakai:instruction_message value=" Last saved: " />
   <sakai:instruction_message value="#{wizard.lastSavedId}" /> --%>
   <sakai:messages />
   
   <h:outputText value="#{wizard.lastSaveWizard} #{msgs.wizard_was_submitted}" styleClass="success" rendered="#{wizard.lastSaveWizard != ''}" />
   <h:outputText value="#{wizard.lastSavePage} #{msgs.page_was_submitted}" styleClass="success" rendered="#{wizard.lastSavePage != ''}" />
   <h:outputText value="#{wizard.lastError} #{msgs.wizard_bad_file_type}" styleClass="validation" rendered="#{wizard.lastError == 'badFileType'}" />
   <h:outputText value="#{wizard.lastError} #{msgs.wizard_bad_import}" styleClass="validation" rendered="#{wizard.lastError == 'badImport'}" />

   <sakai:instruction_message   value="#{msgs.no_wizards}" rendered="#{empty wizard.wizards}" />

   <h:dataTable  value="#{wizard.wizards}" var="wizardItem" styleClass="lines listHier nolines" headerClass="exclude" summary="#{msgs.wizard_list_summary}" rendered="#{not empty wizard.wizards}" border="0">
      <h:column>
         <f:facet name="header">
            <h:outputText value="#{msgs.wizards}" />
         </f:facet>
         <h:outputText value="#{wizardItem.base.name}" rendered="#{!wizardItem.canOperateOnWizardInstance}"/>
         <f:subview id="runLink" rendered="#{wizardItem.canOperateOnWizardInstance}">
            <h:commandLink action="#{wizardItem.processActionRunWizard}" title="#{msgs.run_wizard}">
               <h:outputText value="#{wizardItem.base.name}"/>
            </h:commandLink>
         </f:subview>
	     <sakai:separatedList id="wizActionList" separator=" | " styleClass="itemAction">
	           <f:subview id="previewLink" rendered="#{wizardItem.canPublish && wizardItem.totalPages > 0 && !wizardItem.base.preview && !wizardItem.base.published}">
	                 <h:commandLink action="#{wizardItem.processActionPreview}">
	                 <h:outputText value="#{msgs.preview}" />
	              </h:commandLink>
	           </f:subview>
	           <f:subview id="publishLink" rendered="#{wizardItem.canPublish && wizardItem.totalPages > 0 && wizardItem.base.preview}">
	                 <h:commandLink action="#{wizardItem.processActionPublish}">
	                 <h:outputText value="#{msgs.publish}" />
	              </h:commandLink>
	           </f:subview>
	           <f:subview id="editLink" rendered="#{wizardItem.canEdit}">
	              <h:commandLink action="#{wizardItem.processActionEdit}">
	                 <h:outputText value="#{msgs.edit}"/>
	              </h:commandLink>
	           </f:subview>
	           <f:subview id="deleteLink" rendered="#{wizardItem.canDelete}">
	              <h:commandLink action="#{wizardItem.processActionConfirmDelete}">
	                 <h:outputText value="#{msgs.delete}" />
	              </h:commandLink>
	           </f:subview>
	           <f:subview id="exportLink" rendered="#{wizardItem.canExport}">
	              <h:outputLink value="#{wizardItem.currentExportLink}">
	                  <h:outputText value="#{msgs.export}"/>
	              </h:outputLink>
	           </f:subview>
	     </sakai:separatedList>
      </h:column>
<%-- TODO having the description here really throws rendering off -- would be ok as a separate row, but this is JSF  
      <h:column>
         <f:facet name="header">
            <h:outputText value="#{msgs.wizard_description}" />
         </f:facet>
         <help:glossary link="true" hover="false"><h:outputText value="#{wizardItem.concatDescription}"/></help:glossary>
      </h:column>
--%>
	  <%--
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
      </h:column>  --%>
      <h:column>
         <f:facet name="header">
            <h:outputText value="#{msgs.owner_title}" />
         </f:facet>
         <h:outputText value="#{wizardItem.base.owner.displayName}" />
      </h:column>
      <h:column>
         <f:facet name="header">
            <h:outputText value="#{msgs.published_title}" />
         </f:facet>
         <h:outputText value="#{msgs.preview}" rendered="#{wizardItem.base.preview}"/>
         <h:outputText value="#{msgs.published}" rendered="#{wizardItem.base.published}"/>
         <h:outputText value="#{msgs.unpublished}" rendered="#{!wizardItem.base.preview && !wizardItem.base.published}"/>
      </h:column>
      <h:column>
         <f:facet name="header">
            <h:outputText value="#{msgs.wizard_type}" />
         </f:facet>
         <f:subview id="hiertype" rendered="#{wizardItem.base.type == 'org.theospi.portfolio.wizard.model.Wizard.hierarchical'}">
            <h:outputText value="#{msgs.org_theospi_portfolio_wizard_model_Wizard_hierarchical}"/>
         </f:subview>
         <f:subview id="seqtype" rendered="#{wizardItem.base.type == 'org.theospi.portfolio.wizard.model.Wizard.sequential'}">
            <h:outputText value="#{msgs.org_theospi_portfolio_wizard_model_Wizard_sequential}"/>
         </f:subview>
      </h:column>
      <h:column>
         <f:facet name="header">
            <h:outputText value="#{msgs.wizard_list_submitted_pages}" />
         </f:facet>
         <h:outputText value="#{wizardItem.usersWizard.submittedPages}"/>
      </h:column>
      <h:column>
         <f:facet name="header">
            <h:outputText value="#{msgs.wizard_list_total_pages}" />
         </f:facet>
         <h:outputText value="#{wizardItem.totalPages}"/>
      </h:column>
      <h:column>
         <f:facet name="header">
            <h:outputText value="#{msgs.wizard_list_last_visited}" />
         </f:facet>
         <h:outputFormat id="lastVisited" value="#{msgs.date_format}"
                rendered="#{!empty wizardItem.usersWizard.base.lastVisited}">
             <f:param value="#{wizardItem.usersWizard.base.lastVisited}"/>
         </h:outputFormat>
      </h:column>

   </h:dataTable>
   
</h:form>
</sakai:view>

</f:view>

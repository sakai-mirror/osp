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
<f:loadBundle basename="org.theospi.portfolio.wizard.bundle.Messages" var="msgs"/>
<sakai:view>


<h:form>

   <sakai:view_title value="#{msgs.import_wizard_title}"/>
   <sakai:messages />
   
   <sakai:panel_edit>
      <h:outputLabel for="name" id="nameLabel" value="#{msgs.wizard_name}" />
      <h:panelGroup>
         <h:inputText id="files" value="#{wizard.importFilesString}" disabled="true" />
         <h:commandLink action="#{wizard.processPickImportFiles}">
            <h:outputText value="#{msgs.pick_import_files}"/>
         </h:commandLink>
         <h:message for="files" styleClass="validationEmbedded" />
      </h:panelGroup>
   </sakai:panel_edit>
   
   <sakai:button_bar>
       <sakai:button_bar_item id="import" value="#{msgs.import_wizard_button}"
          action="#{wizard.processImportWizards}" />
       <sakai:button_bar_item id="cancel" value="#{msgs.cancel_wizard}" 
          action="#{wizard.processActionCancel}" />
   </sakai:button_bar>
</h:form>
</sakai:view>

</f:view>

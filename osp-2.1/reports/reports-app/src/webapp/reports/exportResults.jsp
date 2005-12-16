<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ taglib uri="http://sakaiproject.org/jsf/sakai" prefix="sakai" %>
<% response.setContentType("text/html; charset=UTF-8"); %>
<f:view>
    <f:loadBundle basename="org.theospi.tool.reports.bundle.Messages" var="msgs" />
    <sakai:view title="#{msgs.title_export_results}">
            <h:form>
                
                <sakai:view_title value="#{msgs.title_export_results}" indent="1" />
                
                <sakai:instruction_message value="#{msgs.export_instructions} #{msgs.btn_export_results} #{msgs.export_instructions_post}" />
                
                <h:outputText value="#{msgs.select_export}" />
                
                <h:selectOneMenu value="#{ReportsTool.workingResult.currentExportXsl}">
                    <f:selectItems value="#{ReportsTool.workingResult.exportXslSeletionList}" />
                </h:selectOneMenu>
                
                
                <sakai:button_bar>
                    <sakai:button_bar_item
                        action="#{ReportsTool.processExportResultsToFile}"
                        value="#{msgs.btn_export_results}" />
                    <sakai:button_bar_item
                        action="#{ReportsTool.processCancelExport}"
                        value="#{msgs.cancel}" />
                </sakai:button_bar>
                
            </h:form>
    </sakai:view>
</f:view>

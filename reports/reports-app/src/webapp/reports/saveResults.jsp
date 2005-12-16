<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ taglib uri="http://sakaiproject.org/jsf/sakai" prefix="sakai" %>
<% response.setContentType("text/html; charset=UTF-8"); %>
<f:view>
    <f:loadBundle basename="org.theospi.tool.reports.bundle.Messages" var="msgs" />
    <sakai:view title="#{msgs.title_report_results}">
            <h:form>
                <sakai:tool_bar>
                    <sakai:tool_bar_item
                        action="#{ReportsTool.processSaveResults}"
                        value="#{msgs.saveResults}" />
                    <sakai:tool_bar_item
                        action="#{ReportsTool.processExport}"
                        value="#{msgs.exportResults}" />
                </sakai:tool_bar>
                
                <sakai:view_title value="#{ReportsTool.workingResult.title}" indent="1" />
                
                <h:outputText value="#{msgs.select_view}" />
                
                <h:selectOneMenu value="#{ReportsTool.workingResult.currentViewXsl}">
                    <f:selectItems value="#{ReportsTool.workingResult.viewXslSeletionList}" />
                </h:selectOneMenu>
                
                <h:outputText value="#{ReportsTool.workingResult.currentViewResults}" />
                
            </h:form>
    </sakai:view>
</f:view>

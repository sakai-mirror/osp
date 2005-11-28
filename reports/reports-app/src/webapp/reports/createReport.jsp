<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ taglib uri="http://sakaiproject.org/jsf/sakai" prefix="sakai" %>
<% response.setContentType("text/html; charset=UTF-8"); %>
<f:view>
    <f:loadBundle basename="org.theospi.tool.reports.bundle.Messages"
        var="msgs" />
    <sakai:view_container title="#{msgs.title_list}">
        <sakai:view_content>
            <h:form>
                
                The report is: 
                <h:outputText value="#{ReportsTool.workingReportDefinition.reportDefinition.title}" />
                
                
                <h:dataTable var="report"
                    value="#{ReportsTool.workingReportDefinition.reportDefinition.reportDefinitionParams}">
                    <h:column>
                        <f:facet name="header">
                            <h:outputText value="Parameter Name" />
                        </f:facet>

                        <h:outputText value="#{report.paramName}" />
                    </h:column>
                    <h:column>
                        <f:facet name="header">
                            <h:outputText value="Type" />
                        </f:facet>

                        <h:outputText value="#{report.type}" />
                    </h:column>
                    <h:column>
                        <f:facet name="header">
                            <h:outputText value="value type" />
                        </f:facet>

                        <h:outputText value="#{report.valueType}" />
                    </h:column>
                    <h:column>
                        <f:facet name="header">
                            <h:outputText value="Value" />
                        </f:facet>

                        <h:outputText value="#{report.value}" />
                    </h:column>
                </h:dataTable>
            </h:form>
        </sakai:view_content>
    </sakai:view_container>
</f:view>

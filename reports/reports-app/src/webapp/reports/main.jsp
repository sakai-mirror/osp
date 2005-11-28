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
                <sakai:tool_bar>
                    <sakai:tool_bar_item
                        action="#{ReportsTool.gotoOptions}"
                        value="#{msgs.options}" />
                </sakai:tool_bar>


                <h:dataTable var="report"
                    value="#{ReportsTool.reports}">
                    <h:column>
                        <f:facet name="header">
                            <h:outputText value="Title" />
                        </f:facet>

                        <h:commandLink
                            action="#{report.selectReportDefinition}">
                            <h:outputText
                                value="#{report.reportDefinition.title}" />
                        </h:commandLink>
                    </h:column>
                </h:dataTable>
            </h:form>
        </sakai:view_content>
    </sakai:view_container>
</f:view>

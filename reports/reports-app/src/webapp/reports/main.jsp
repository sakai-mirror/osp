<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ taglib uri="http://sakaiproject.org/jsf/sakai" prefix="sakai" %>
<% response.setContentType("text/html; charset=UTF-8"); %>
<f:view>
    <f:loadBundle basename="org.theospi.tool.reports.bundle.Messages" var="msgs" />
    <sakai:view title="#{msgs.title_main}">
            <h:form>
                <sakai:tool_bar>
                    <sakai:tool_bar_item
                        action="#{ReportsTool.gotoOptions}"
                        value="#{msgs.options}" />
                </sakai:tool_bar>

                <sakai:view_title value="#{msgs.title_main}" indent="1" />

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
                <h:outputText value="<br/><br/>#{msgs.report_results}" escape="false"/>
                <h:dataTable var="result"
                    value="#{ReportsTool.results}">
                    <h:column>
                        <f:facet name="header">
                            <h:outputText value="Title" />
                        </f:facet>

                        <h:commandLink
                            action="#{result.processSelectReportResult}">
                            <h:outputText value="#{result.title}" />
                            
                            <h:outputText rendered="#{result.isLive}"
                                value="(#{msgs.is_a_live_report})" />
                        </h:commandLink>
                    </h:column>
                    <h:column>
                        <f:facet name="header">
                            <h:outputText value="Creation Date" />
                        </f:facet>
                        <h:outputText value="#{result.creationDate}" />
                    </h:column>
                </h:dataTable>
            </h:form>
    </sakai:view>
</f:view>

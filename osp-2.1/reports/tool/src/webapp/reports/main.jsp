<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ taglib uri="http://sakaiproject.org/jsf/sakai" prefix="sakai" %>
<%@ taglib uri="http://www.theospi.org" prefix="osp" %>
<% response.setContentType("text/html; charset=UTF-8"); %>
<f:view>
    <f:loadBundle basename="org.theospi.portfolio.reports.bundle.Messages" var="msgs" />
    <sakai:view title="#{msgs.title_main}">
            <h:form>
                <sakai:tool_bar>
                    <h:outputLink value="sakai.permissions.helper.helper/tool"
                        rendered="#{ReportsTool.maintainer}"
                        title="#{msgs.permissions_link}">
                       <f:param name="session.sakaiproject.permissions.description"
                        value="#{ReportsTool.permissionsMessage}" />
                       <f:param name="session.sakaiproject.permissions.siteRef"
                           value="#{ReportsTool.worksite.reference}" />
                       <f:param name="session.sakaiproject.permissions.prefix"
                           value="#{ReportsTool.reportFunctionPrefix}" />
                       <h:outputText value="#{msgs.permissions_link}" />
                    </h:outputLink>
                </sakai:tool_bar>

                <sakai:view_title value="#{msgs.title_main}" indent="1" />

                <h:dataTable var="report"
                    value="#{ReportsTool.reports}" rendered="#{ReportsTool.userCan.createReport}">
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
                    value="#{ReportsTool.results}"
                        rendered="#{ReportsTool.userCan.createReport ||
                                    ReportsTool.userCan.runReport ||
                                    ReportsTool.userCan.viewReport}">
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

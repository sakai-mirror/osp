<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ taglib uri="http://sakaiproject.org/jsf/sakai" prefix="sakai" %>
<% response.setContentType("text/html; charset=UTF-8"); %>
<f:view>
    <f:loadBundle basename="org.theospi.tool.reports.bundle.Messages" var="msgs" />
    <sakai:view title="#{msgs.title_create_report_params}">
            <h:form>
                
                The report is: 
                <h:outputText value="#{ReportsTool.workingReportDefinition.reportDefinition.title}" />
                
                
                <h:dataTable var="decoratedReportParam"
                    value="#{ReportsTool.workingReport.reportParams}">
                    <h:column>
                        <f:facet name="header">
                            <h:outputText value="Parameter Name" />
                        </f:facet>

                        <h:outputText value="#{decoratedReportParam.reportDefinitionParam.title}" />
                    </h:column>
                    <h:column>
                        <f:facet name="header">
                            <h:outputText value="Value" />
                        </f:facet>
                        <h:inputText value="#{decoratedReportParam.textValue}" id="fillin" rendered="#{decoratedReportParam.isFillIn}"/>
                        
                        <h:selectOneMenu value="#{decoratedReportParam.menuValue}" 
                                rendered="#{decoratedReportParam.isSet && !decoratedReportParam.isMultiSelectable}">
                            <f:selectItems value="#{decoratedReportParam.selectableValues}" />
                        </h:selectOneMenu>
                        
                        <h:outputText value="#{decoratedReportParam.staticValue}" rendered="#{decoratedReportParam.isStatic}"/>
                    </h:column>
                </h:dataTable>
                
                <sakai:button_bar>
                    <sakai:button_bar_item
                        action="#{ReportsTool.workingReport.processEditParamsContinue}"
                        value="#{msgs.generate_report_results}" />
                    <sakai:button_bar_item
                        action="#{ReportsTool.workingReport.processCancel}"
                        value="#{msgs.cancel}" />
                </sakai:button_bar>
                
            </h:form>
    </sakai:view>
</f:view>

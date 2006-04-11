<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ taglib uri="http://sakaiproject.org/jsf/sakai" prefix="sakai" %>
<% response.setContentType("text/html; charset=UTF-8"); %>
<f:view>
    <sakai:view title="#{msgs.title_create_report_params}">
            <h:form id="rpForm">
<script type="text/javascript" src="calendar/popcalendar.js"></script>
<link href="calendar/theme.css" rel="stylesheet" type="text/css" />
                
                <sakai:view_title value="#{msgs.title_create_report_params}" indent="1" />
                
                <h:outputText value="#{msgs.report_title_is} #{ReportsTool.workingReportDefinition.reportDefinition.title}" />
                
                <br /><br /><h:outputText value="<span style='color:#F66'>#{ReportsTool.workingReport.paramErrorMessages}</span>
                " escape="false" />
                
                <h:dataTable var="decoratedReportParam" id="paramsTable"
                    value="#{ReportsTool.workingReport.reportParams}">
                    <h:column>
                        <f:facet name="header">
                            <h:outputText value="#{msgs.th_param_name}" />
                        </f:facet>

                        <h:outputText value="#{decoratedReportParam.reportDefinitionParam.title}" />
                    </h:column>
                    <h:column>
                        <f:facet name="header">
                            <h:outputText value="#{msgs.th_value}" />
                        </f:facet>
                        <h:inputText value="#{decoratedReportParam.textValue}" id="fillin" 
                            rendered="#{decoratedReportParam.isFillIn && !decoratedReportParam.isDate}"/>

                        <% /*
                        <sakai:input_date value="#{decoratedReportParam.dateValue}" showDate="true"
                            showTime="true" rendered="#{decoratedReportParam.isFillIn && decoratedReportParam.isDate}" />
						*/ %>
						<h:inputText id="dueDate" value="#{decoratedReportParam.dateValue}" 
							rendered="#{decoratedReportParam.isFillIn && decoratedReportParam.isDate}"
							onkeypress="return submitOnEnter(event, 'rpForm:saveButton');">
						</h:inputText>
						<h:outputText escape="false" rendered="#{decoratedReportParam.isFillIn && decoratedReportParam.isDate}"
							value="
							<input type=\"image\" id=\"dueDatePopup\" src=\"images/calendar_icon.gif\"
								onclick=\"jscalendarPopUpCalendar(this,this.form.elements['rpForm:paramsTable:#{decoratedReportParam.index}:dueDate'],'M/d/yyyy'); return false;\" />
							"
						/>
		
                        <h:selectOneMenu value="#{decoratedReportParam.menuValue}" 
                                rendered="#{decoratedReportParam.isSet && !decoratedReportParam.isMultiSelectable}">
                            <f:selectItems value="#{decoratedReportParam.selectableValues}" />
                        </h:selectOneMenu>
                        
                        <h:outputText value="#{decoratedReportParam.staticValue}" rendered="#{decoratedReportParam.isStatic}"/>
                    </h:column>
                </h:dataTable>
                
                <sakai:button_bar>
                    <sakai:button_bar_item
                        action="#{ReportsTool.processEditParamsBack}"
                        value="#{msgs.btn_back}" />
                    <sakai:button_bar_item
						id="saveButton"
                        action="#{ReportsTool.processEditParamsContinue}"
                        value="#{msgs.generate_report_results}" />
                    <sakai:button_bar_item
                        action="#{ReportsTool.processCancelReport}"
                        value="#{msgs.cancel}" />
                </sakai:button_bar>
                
            </h:form>
    </sakai:view>
</f:view>

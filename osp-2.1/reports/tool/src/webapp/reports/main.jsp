<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ taglib uri="http://sakaiproject.org/jsf/sakai" prefix="sakai" %>
<%@ taglib uri="http://www.theospi.org" prefix="osp" %>
<% response.setContentType("text/html; charset=UTF-8"); %>
<f:view>
    <fmt:setLocale value="${locale}" />
    <f:loadBundle basename="org.theospi.portfolio.reports.bundle.Messages" var="msgs" />
    <sakai:view title="#{msgs.title_main}">
            <h:form>
                <sakai:tool_bar>
                        <h:commandLink rendered="#{ReportsTool.maintainer}"
                            action="#{ReportsTool.processPermissions}">
                            <h:outputText
                                value="#{msgs.permissions_link}" />
                        </h:commandLink>
                </sakai:tool_bar>

                <sakai:view_title value="#{msgs.title_main}" indent="1" />

                <h:dataTable var="report" styleClass="listHier"
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
                <h:dataTable var="result" styleClass="listHier"
                    value="#{ReportsTool.results}"
                        rendered="#{ReportsTool.userCan.runReport ||
                                    ReportsTool.userCan.viewReport ||
                                    ReportsTool.userCan.editReport ||
                                    ReportsTool.userCan.deleteReport}">
                    <h:column>
                        <f:facet name="header">
                            <h:outputText value="Title" />
                        </f:facet>

                            <h:outputText value="#{result.title}" />
                            
                            <h:outputText rendered="#{result.isLive}"
                                value=" (#{msgs.is_a_live_report})" />
                        
                        <f:verbatim escape="false">
                           <div class="itemAction">
                        </f:verbatim>
                           <h:commandLink action="#{result.processSelectReportResult}"
                                    rendered="#{!result.isLive && ReportsTool.userCan.viewReport}">
                              <h:outputText value="#{msgs.view_report}" />
                           </h:commandLink>
                           
                           <h:commandLink action="#{result.processSelectReportResult}"
                                    rendered="#{result.isLive && ReportsTool.userCan.runReport}">
                              <h:outputText value="#{msgs.run_report}" />
                           </h:commandLink>
                           
                           <h:outputText value="#{' &nbsp; | &nbsp; '}" escape="false"
                                    rendered="#{result.isLive && ReportsTool.userCan.runReport && 
                                                result.isLive && ReportsTool.userCan.editReport}"/>
                           <h:commandLink action="#{result.processEditReport}"
                                    rendered="#{result.isLive && ReportsTool.userCan.editReport}">
                              <h:outputText value="#{msgs.edit_report}" />
                           </h:commandLink>
                           
                           <h:outputText value="#{' &nbsp; | &nbsp; '}"  escape="false"
                                    rendered="#{ReportsTool.userCan.deleteReport && 
                                                ((!result.isLive && ReportsTool.userCan.viewReport) ||
                                                (result.isLive && ReportsTool.userCan.runReport) || 
                                                (result.isLive && ReportsTool.userCan.editReport))}"/>
                           <h:commandLink action="#{result.processDelete}"
                                    rendered="#{ReportsTool.userCan.deleteReport}">
                              <h:outputText value="#{msgs.delete_report}" />
                           </h:commandLink>
                        <f:verbatim escape="false">
                           </div>
                        </f:verbatim>
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

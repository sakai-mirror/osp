<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ taglib uri="http://sakaiproject.org/jsf/sakai" prefix="sakai" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>

<%
		response.setContentType("text/html; charset=UTF-8");
		response.addDateHeader("Expires", System.currentTimeMillis() - (1000L * 60L * 60L * 24L * 365L));
		response.addDateHeader("Last-Modified", System.currentTimeMillis());
		response.addHeader("Cache-Control", "no-store, no-cache, must-revalidate, max-age=0, post-check=0, pre-check=0");
		response.addHeader("Pragma", "no-cache");
%>

<f:loadBundle basename="org.theospi.portfolio.common.bundle.Messages" var="msgs"/>

<f:view>
<sakai:view_container title="#{msgs.title_list}">
<h:form>

	<sakai:tool_bar>	
		<sakai:tool_bar_item
			action="#{ListTool.processActionOptions}"
			value="#{msgs.bar_options}" />
	</sakai:tool_bar>

	<sakai:view_content>

		<h:messages showSummary="false" showDetail="true" />
	
		<sakai:flat_list value="#{ListTool.entries}" var="co">
		
			<h:column rendered="#{ListTool.currentConfig.selected['0'].selected}">
				<f:facet name="header">
					<h:outputText value="#{msgs[ListTool.currentConfig.selected['0'].name]}" />
				</f:facet>
				<h:outputLink rendered="#{!co.newWindow}" value="#{co.redirectUrl}" target="_top" >
					<h:outputText value="#{co.columnValues['0']}"/>
				</h:outputLink>
				<h:outputLink rendered="#{co.newWindow}" value="#{co.redirectUrl}" target="_new" >
					<h:outputText value="#{co.columnValues['0']}"/>
				</h:outputLink>
			</h:column>
			<h:column rendered="#{ListTool.currentConfig.selected['1'].selected}">
				<f:facet name="header">
					<h:outputText value="#{msgs[ListTool.currentConfig.selected['1'].name]}" />
				</f:facet>
				<h:outputText value="#{co.columnValues['1']}"/>
			</h:column>
			<h:column rendered="#{ListTool.currentConfig.selected['2'].selected}">
				<f:facet name="header">
					<h:outputText value="#{msgs[ListTool.currentConfig.selected['2'].name]}" />
				</f:facet>
				<h:outputText value="#{co.columnValues['2']}"/>
			</h:column>
			<h:column rendered="#{ListTool.currentConfig.selected['3'].selected}">
				<f:facet name="header">
					<h:outputText value="#{msgs[ListTool.currentConfig.selected['3'].name]}" />
				</f:facet>
				<h:outputText value="#{co.columnValues['3']}"/>
			</h:column>
			<h:column rendered="#{ListTool.currentConfig.selected['4'].selected}">
				<f:facet name="header">
					<h:outputText value="#{msgs[ListTool.currentConfig.selected['4'].name]}" />
				</f:facet>
				<h:outputText value="#{co.columnValues['4']}"/>
			</h:column>
			<h:column rendered="#{ListTool.currentConfig.selected['5'].selected}">
				<f:facet name="header">
					<h:outputText value="#{msgs[ListTool.currentConfig.selected['5'].name]}" />
				</f:facet>
				<h:outputText value="#{co.columnValues['5']}"/>
			</h:column>
			<h:column rendered="#{ListTool.currentConfig.selected['6'].selected}">
				<f:facet name="header">
					<h:outputText value="#{msgs[ListTool.currentConfig.selected['6'].name]}" />
				</f:facet>
				<h:outputText value="#{co.columnValues['6']}"/>
			</h:column>
			<h:column rendered="#{ListTool.currentConfig.selected['7'].selected}">
				<f:facet name="header">
					<h:outputText value="#{msgs[ListTool.currentConfig.selected['7'].name]}" />
				</f:facet>
				<h:outputText value="#{co.columnValues['7']}"/>
			</h:column>
			<h:column rendered="#{ListTool.currentConfig.selected['8'].selected}">
				<f:facet name="header">
					<h:outputText value="#{msgs[ListTool.currentConfig.selected['8'].name]}" />
				</f:facet>
				<h:outputText value="#{co.columnValues['8']}"/>
			</h:column>
			<h:column rendered="#{ListTool.currentConfig.selected['9'].selected}">
				<f:facet name="header">
					<h:outputText value="#{msgs[ListTool.currentConfig.selected['9'].name]}" />
				</f:facet>
				<h:outputText value="#{co.columnValues['9']}"/>
			</h:column>

		</sakai:flat_list>

	</sakai:view_content>

</h:form>
</sakai:view_container>
</f:view>

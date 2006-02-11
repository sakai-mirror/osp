<%
		response.setContentType("text/html; charset=UTF-8");
		response.addDateHeader("Expires", System.currentTimeMillis() - (1000L * 60L * 60L * 24L * 365L));
		response.addDateHeader("Last-Modified", System.currentTimeMillis());
		response.addHeader("Cache-Control", "no-store, no-cache, must-revalidate, max-age=0, post-check=0, pre-check=0");
		response.addHeader("Pragma", "no-cache");
%>

<f:loadBundle basename="org.theospi.portfolio.common.messages" var="msgs"/>

<f:view>
<sakai:view_container title="#{msgs.title_options}">
<h:form>

	<sakai:tool_bar_message value="#{msgs.options_message}" />

	<sakai:view_content>

		<h:messages showSummary="false" showDetail="true" />
	
		<sakai:instruction_message value="#{msgs.options_instructions}" />
	
		<sakai:group_box title="#{msgs.list_config_group}">
			<sakai:panel_edit>
	 
				<h:outputText value="#{msgs.prop_hdr_title}"/>
				<h:inputText value="#{ListTool.currentConfig.title}" required="true" />
				<h:outputText value="#{msgs.prop_hdr_rows}"/>
				<h:inputText value="#{ListTool.currentConfig.rows}" required="true" />

            <sakai:flat_list value="#{ListTool.currentConfig.columns}" var="column">
      			<h:column>
      				<f:facet name="header">
      					<h:outputText value="#{msgs.show_header}"/>
      				</f:facet>
      				<h:selectBooleanCheckbox value="#{column.selected}"/>
      			</h:column>               
      			<h:column>
      				<f:facet name="header">
      					<h:outputText value="#{msgs.title_header}"/>
      				</f:facet>
      				<h:outputText value="#{msgs[column.name]}"/>
      			</h:column>               
            </sakai:flat_list>

			</sakai:panel_edit>
		</sakai:group_box>

		<sakai:button_bar>
			<sakai:button_bar_item
					action="#{ListTool.processActionOptionsSave}"
					value="#{msgs.bar_save}" />
			<sakai:button_bar_item
					immediate="true"
					action="#{ListTool.processMain}"
					value="#{msgs.bar_cancel}" />
		</sakai:button_bar>

	</sakai:view_content>

</h:form>
</sakai:view_container>
</f:view>

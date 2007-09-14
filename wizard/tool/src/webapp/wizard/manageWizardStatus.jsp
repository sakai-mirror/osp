<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ taglib uri="http://sakaiproject.org/jsf/sakai" prefix="sakai" %>


<f:view>
	<sakai:view>
		<h:form>


			<f:subview id="title"> 
   				<sakai:view_title value="#{msgs.manage_wizard_status}"/>
   			</f:subview>
			
   			<sakai:messages />
   			
   			<br>
   			
   			<f:subview id="dropDown">
   				<h:outputText value="#{msgs.legend_changeStatusTo}" style="font-weight: bold"/>
   				<h:selectOneMenu id="newStatus" immediate="true" value="#{wizard.current.runningWizard.base.status}">
		  				<f:selectItems value="#{wizard.current.runningWizard.statusLists}"/>
				</h:selectOneMenu>
			</f:subview>	
			
			<br>
			
			<sakai:button_bar>
				<sakai:button_bar_item id="submitContinue" value="#{msgs.save_continue_wizard}"
	      	 		action="#{wizard.current.runningWizard.processManageStatus}" accesskey="s" styleClass="active" />
	      	 	<sakai:button_bar_item id="cancel" value="#{msgs.cancel_wizard}"
	      	 		action="runWizard" accesskey="c"/>
			</sakai:button_bar>
			
			
		</h:form>
	</sakai:view>
</f:view>
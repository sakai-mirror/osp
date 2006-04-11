<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ taglib uri="http://sakaiproject.org/jsf/sakai" prefix="sakai" %>
<%@ taglib uri="http://www.theospi.org/jsf/osp" prefix="ospx" %>

<%
      response.setContentType("text/html; charset=UTF-8");
      response.addDateHeader("Expires", System.currentTimeMillis() - (1000L * 60L * 60L * 24L * 365L));
      response.addDateHeader("Last-Modified", System.currentTimeMillis());
      response.addHeader("Cache-Control", "no-store, no-cache, must-revalidate, max-age=0, post-check=0, pre-check=0");
      response.addHeader("Pragma", "no-cache");
%>

<f:view>
<sakai:view>

   <%@include file="steps.jspf"%>

<h:form>

   <sakai:view_title value="#{msgs.edit_wizard}" rendered='#{!wizard.current.newWizard}'/>
   <sakai:view_title value="#{msgs.add_wizard}"  rendered='#{wizard.current.newWizard}'/>

   <sakai:instruction_message value="#{msgs.wizard_instruction_message}" />
   <sakai:messages />
   
   
   <ospx:xheader>
      <ospx:xheadertitle id="guidanceTitle">
      	<h:outputText value="#{msgs.guidance_title}"/>
      </ospx:xheadertitle>
   </ospx:xheader>

   <sakai:panel_edit>
	     <h:outputLabel id="editInstructionsLabel" value="#{msgs.guidance_instructions}" />
	     
	     <h:panelGroup>
	     	<h:outputText value="#{wizard.current.guidanceInstructions}" escape="false" />
            <sakai:flat_list value="#{wizard.current.guidanceInstructionsAttachments}" var="attachment">
               <h:column>
               		<h:outputText value="<img src = '/library/image/sakai/generic.gif' border= '0' alt ='' hspace='0' />" escape="false" />
                     <h:outputLink title="#{attachment.displayName}"
                        value="#{attachment.fullReference.base.url}" target="_new">
                        <h:outputText value="#{attachment.displayName}"/>
                     </h:outputLink>
                        <h:outputText value=" (#{attachment.contentLength})"/>
               </h:column>
            </sakai:flat_list>
            <sakai:button_bar>
 	           <sakai:button_bar_item id="addInstructions" value="#{msgs.guidance_instructions_add}"
     	 	      action="#{wizard.current.processActionEditInstructions}" rendered="#{empty wizard.current.guidanceInstructions}" />
 	           <sakai:button_bar_item id="editInstructions" value="#{msgs.guidance_instructions_revise}"
     	 	      action="#{wizard.current.processActionEditInstructions}" rendered="#{not empty wizard.current.guidanceInstructions}"/>
     	    </sakai:button_bar>
	     </h:panelGroup>
	     
	     <h:outputLabel id="editRationaleLabel" value="#{msgs.guidance_rationale}" />
	     <h:panelGroup>
	     	<h:outputText value="#{wizard.current.guidanceRationale}" escape="false"/>
            <sakai:flat_list value="#{wizard.current.guidanceRationaleAttachments}" var="attachment">
               <h:column>
               		<h:outputText value="<img src = '/library/image/sakai/generic.gif' border= '0' alt ='' hspace='0' />" escape="false" />
                     <h:outputLink title="#{attachment.displayName}"
                        value="#{attachment.fullReference.base.url}" target="_new">
                        <h:outputText value="#{attachment.displayName}"/>
                     </h:outputLink>
                        <h:outputText value=" (#{attachment.contentLength})"/>
               </h:column>
            </sakai:flat_list>
            <sakai:button_bar>
 	           <sakai:button_bar_item id="addRationale" value="#{msgs.guidance_rationale_add}"
     	 	      action="#{wizard.current.processActionEditRationale}" rendered="#{empty wizard.current.guidanceRationale}" />
 	           <sakai:button_bar_item id="editRationale" value="#{msgs.guidance_rationale_revise}"
     	 	      action="#{wizard.current.processActionEditRationale}" rendered="#{not empty wizard.current.guidanceRationale}" />
     	    </sakai:button_bar>
	     </h:panelGroup>
	     
	     <h:outputLabel id="editExamplesLabel" value="#{msgs.guidance_examples}" />
	     <h:panelGroup>
	     	<h:outputText value="#{wizard.current.guidanceExamples}" escape="false"/>
            <sakai:flat_list value="#{wizard.current.guidanceExamplesAttachments}" var="attachment">
               <h:column>
               		<h:outputText value="<img src = '/library/image/sakai/generic.gif' border= '0' alt ='' hspace='0' />" escape="false" />
                     <h:outputLink title="#{attachment.displayName}"
                        value="#{attachment.fullReference.base.url}" target="_new">
                        <h:outputText value="#{attachment.displayName}"/>
                     </h:outputLink>
                        <h:outputText value=" (#{attachment.contentLength})"/>
               </h:column>
            </sakai:flat_list>
            <sakai:button_bar>
 	           <sakai:button_bar_item id="addExamples" value="#{msgs.guidance_examples_add}"
     	 	      action="#{wizard.current.processActionEditExamples}" rendered="#{empty wizard.current.guidanceExamples}" />
 	           <sakai:button_bar_item id="editExamples" value="#{msgs.guidance_examples_revise}"
     	 	      action="#{wizard.current.processActionEditExamples}" rendered="#{not empty wizard.current.guidanceExamples}" />
     	    </sakai:button_bar>
	     </h:panelGroup>
	     
   </sakai:panel_edit>
   
   <ospx:xheader>
      <ospx:xheadertitle id="comRefTitle">
      	<h:outputText value="#{msgs.com_ref_title}"/>
      </ospx:xheadertitle>
   </ospx:xheader>
         
   <sakai:instruction_message value="#{msgs.com_ref_instruction}" />

   <sakai:panel_edit>
      <h:outputLabel for="reflectionItems" id="reflectionLabel" value="#{msgs.reflection_item}" />
      <h:panelGroup>
         <h:selectOneMenu id="reflectionItems"
            immediate="true" disabled="#{wizard.current.base.published}"
            value="#{wizard.reflectionItem}">
            <f:selectItem itemLabel="#{msgs.choose_reflection_item}" itemValue=""/>
            <f:selectItem itemLabel="#{msgs.choose_form}" itemValue="1"/>
            <f:selectItems value="#{wizard.reflectionFormsForSelect}"/>
         <% /*   <f:selectItem itemLabel="#{msgs.choose_wizard}" itemValue="2"/>
            <f:selectItems value="#{wizard.reflectionWizardsForSelect}"/> */ %>
         </h:selectOneMenu>
      </h:panelGroup>
      <h:outputLabel for="commentItems" id="commentLabel" value="#{msgs.comment_item}" />
      <h:panelGroup>
         <h:selectOneMenu id="commentItems"
            immediate="true" disabled="#{wizard.current.base.published}"
            value="#{wizard.commentItem}">
            <f:selectItem itemLabel="#{msgs.choose_comment_item}" itemValue=""/>
            <f:selectItem itemLabel="#{msgs.choose_form}" itemValue="1"/>
            <f:selectItems value="#{wizard.commentFormsForSelect}"/>
          <% /*  <f:selectItem itemLabel="#{msgs.choose_wizard}" itemValue="2"/>
            <f:selectItems value="#{wizard.commentWizardsForSelect}"/> */ %>
         </h:selectOneMenu>
      </h:panelGroup>
   </sakai:panel_edit>
   <h:outputText value="<br><br>" escape="false" />
   
   
   <ospx:xheader>
      <ospx:xheadertitle id="evalTitle">
      	<h:outputText value="#{msgs.eval_title}"/>
      </ospx:xheadertitle>
   </ospx:xheader>
   <sakai:instruction_message value="#{msgs.eval_instruction}" />
   <sakai:panel_edit>
      <h:outputLabel for="evaluationItems" id="evaluationLabel" value="#{msgs.evaluation_item}" />
      <h:panelGroup>
         <h:selectOneMenu id="evaluationItems"
            immediate="true" disabled="#{wizard.current.base.published}"
            value="#{wizard.evaluationItem}">
            <f:selectItem itemLabel="#{msgs.choose_evaluation_item}" itemValue=""/>
            <f:selectItem itemLabel="#{msgs.choose_form}" itemValue=""/>
            <f:selectItems value="#{wizard.evaluationFormsForSelect}"/>
          <% /* <f:selectItem itemLabel="#{msgs.choose_wizard}" itemValue=""/>
            <f:selectItems value="#{wizard.evaluationWizardsForSelect}"/> */ %>
         </h:selectOneMenu>
      </h:panelGroup>
   </sakai:panel_edit>
   <h:outputText value="<br><br>" escape="false" />
   
   <ospx:xheader>
      <ospx:xheadertitle id="evaluatorsTitle">
      	<h:outputText value="#{msgs.audience_title}"/>
      </ospx:xheadertitle>
   </ospx:xheader>
   
   <sakai:panel_edit>
      <h:panelGroup>
            <sakai:flat_list value="#{wizard.current.evaluators}" var="evaluator">
               <h:column>
               		<h:outputText value="#{evaluator}" />
               </h:column>
            </sakai:flat_list>
            <f:subview id="moveFooter" rendered="#{empty wizard.current.evaluators}">
               		<h:outputText value="#{msgs.no_evaluators}" />
            </f:subview>
         <sakai:button_bar>
 	        <sakai:button_bar_item id="selectEvaluators" value="#{msgs.select_reviewers}"
     	 	   action="#{wizard.processActionAudienceHelper}" />
     	 </sakai:button_bar>
      </h:panelGroup>
   </sakai:panel_edit>
	   
   <%@include file="builderButtons.jspf"%>
   
</h:form>
</sakai:view>

</f:view>

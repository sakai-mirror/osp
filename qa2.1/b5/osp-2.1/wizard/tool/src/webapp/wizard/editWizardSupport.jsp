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
<f:loadBundle basename="org.theospi.portfolio.wizard.messages" var="msgs"/>
<sakai:view>

   <%@include file="steps.jspf"%>

<h:form>

   <sakai:view_title value="#{msgs.edit_wizard}" rendered='#{!wizard.current.newWizard}'/>
   <sakai:view_title value="#{msgs.add_wizard}"  rendered='#{wizard.current.newWizard}'/>

   <sakai:instruction_message value="#{msgs.wizard_instruction_message}" />
<%--   <sakai:instruction_message value=" Last saved: " />
   <sakai:instruction_message value="#{wizard.lastSavedId}" /> --%>
   <sakai:messages />
   
   <%@include file="wizardPropertiesFrame.jspf"%>
   
   <ospx:xheader>
      <ospx:xheadertitle id="guidanceTitle" value="#{msgs.guidance_title}" />
      <ospx:xheaderdrawer initiallyexpanded="#{wizard.expandedGuidanceSection}" cssclass="drawerBorder">
         <sakai:instruction_message value="#{msgs.guidance_instruction_message}" />
         <sakai:flat_list value="#{wizard.current.base.guidance}" var="guidance">
                  <h:column>
                     <f:facet name="header">
                        <sakai:button_bar>
                           <sakai:button_bar_item id="guidance" value="#{msgs.edit_guidance}" 
                              action="#{wizard.processActionGuidanceHelper}" immediate="true" />
                        </sakai:button_bar>
                     </f:facet>
                     <h:commandLink title="#{guidance.description}"
                        action="#{wizard.processActionGuidanceHelper}">
                        <h:outputText value="#{guidance.description}"/>
                     </h:commandLink>
                  </h:column>
                  <h:column>
                     <f:facet name="header" />
                     <sakai:button_bar>
                           <sakai:button_bar_item id="removeGuidance" value="#{msgs.remove_guidance}" 
                              action="#{wizard.processActionRemoveGuidance}" immediate="true" />
                        </sakai:button_bar>
                  </h:column>
               </sakai:flat_list>
      </ospx:xheaderdrawer>
   </ospx:xheader>
   
   <ospx:xheader>
      <ospx:xheadertitle id="comRefTitle" value="#{msgs.com_ref_title}" />
      <ospx:xheaderdrawer initiallyexpanded="false" cssclass="drawerBorder">
         <h:outputLabel for="commentItems" id="commentLabel" value="#{msgs.comment_item}" />
         <h:selectOneMenu id="commentItems"
            immediate="true"
            value="#{wizard.commentItem}">
            <f:selectItem itemLabel="#{msgs.choose_comment_item}" itemValue=""/>
            <f:selectItem itemLabel="#{msgs.choose_form}" itemValue="1"/>
            <f:selectItems value="#{wizard.commentFormsForSelect}"/>
            <f:selectItem itemLabel="#{msgs.choose_wizard}" itemValue="2"/>
            <f:selectItems value="#{wizard.commentWizardsForSelect}"/>
         </h:selectOneMenu>
         <h:outputLabel for="reflectionItems" id="reflectionLabel" value="#{msgs.reflection_item}" />
         <h:selectOneMenu id="reflectionItems"
            immediate="true"
            value="#{wizard.reflectionItem}">
            <f:selectItem itemLabel="#{msgs.choose_reflection_item}" itemValue=""/>
            <f:selectItem itemLabel="#{msgs.choose_form}" itemValue="1"/>
            <f:selectItems value="#{wizard.reflectionFormsForSelect}"/>
            <f:selectItem itemLabel="#{msgs.choose_wizard}" itemValue="2"/>
            <f:selectItems value="#{wizard.reflectionWizardsForSelect}"/>
         </h:selectOneMenu>
      </ospx:xheaderdrawer>
   </ospx:xheader>
   
   <ospx:xheader>
      <ospx:xheadertitle id="evalTitle" value="#{msgs.eval_title}" />
      <ospx:xheaderdrawer initiallyexpanded="false" cssclass="drawerBorder">
         <h:outputLabel for="evaluationItems" id="evaluationLabel" value="#{msgs.evaluation_item}" />
         <h:selectOneMenu id="evaluationItems"
            immediate="true"
            value="#{wizard.evaluationItem}">
            <f:selectItem itemLabel="#{msgs.choose_evaluation_item}" itemValue=""/>
            <f:selectItem itemLabel="#{msgs.choose_form}" itemValue=""/>
            <f:selectItems value="#{wizard.evaluationFormsForSelect}"/>
            <f:selectItem itemLabel="#{msgs.choose_wizard}" itemValue=""/>
            <f:selectItems value="#{wizard.evaluationWizardsForSelect}"/>
         </h:selectOneMenu>
      </ospx:xheaderdrawer>
   </ospx:xheader>

   <%@include file="builderButtons.jspf"%>
   
</h:form>
</sakai:view>

</f:view>

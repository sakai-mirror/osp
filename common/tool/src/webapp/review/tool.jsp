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
<f:loadBundle basename="org.theospi.portfolio.common.messages" var="msgs"/>

<sakai:view>
   <sakai:view_title value="#{review.title}"/>
   <sakai:messages />

<h:form>

This is the review form.

   <sakai:instruction_message value="#{review.instructions}" />
         
   <sakai:panel_edit>

      <ospx:formLabel valueRequired="true">
         <h:outputLabel for="title" id="titleLabel" value="#{msgs.review_title}" />
      </ospx:formLabel>
      <h:panelGroup>
         <h:inputText id="title" value="#{review.current.base.title}" required="true">
            <f:validateLength minimum="1" maximum="255" />
         </h:inputText>
         <h:message for="title" styleClass="validationEmbedded" />
      </h:panelGroup>
       
      <ospx:formLabel valueRequired="true">
         <h:outputLabel for="formSelection" id="formSelectionLabel" value="#{review.formLabel}" />
      </ospx:formLabel>  
      <h:panelGroup>
         <sakai:flat_list id="formSelection" value="#{review.current.base.reviewAttachment}" var="content">
            <h:column>
               <f:facet name="header">
                  <h:commandButton action="#{review.processActionChooseForm}"
                        value="#{review.manageContentLabel}" immediate="true" />
               </f:facet>
               <h:outputLink title="#{content.displayName}"
                     value="#{content.fullReference.base.url}" target="_new">
                  <h:outputText value="#{content.displayName}"/>
               </h:outputLink>
            </h:column>
         </sakai:flat_list>
         <h:message for="formSelection" styleClass="validationEmbedded" />
      </h:panelGroup>
      <ospx:formLabel valueRequired="true">
         <h:outputLabel for="visibility" id="visibilityLabel" value="#{review.visibilityLabel}" />
      </ospx:formLabel>
      <h:panelGroup>
         <h:selectOneRadio id="visibility" value="#{review.current.base.visibility}" 
               layout="pageDirection" required="true">
            <f:selectItems value="#{review.visibilityOptions}"/>
         </h:selectOneRadio>
         <h:message for="visibility" styleClass="validationEmbedded" />
      </h:panelGroup>
   </sakai:panel_edit>

   <sakai:button_bar>
      <sakai:button_bar_item id="cancel" value="#{msgs.cancel_review}" 
         action="#{review.processActionCancel}" immediate="true" />
      <sakai:button_bar_item id="submit" value="#{msgs.save_review}" 
         action="#{review.processActionSave}" />
   </sakai:button_bar>
   
</h:form>
</sakai:view>

</f:view>
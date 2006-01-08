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
<f:loadBundle basename="org.theospi.portfolio.presentation.messages" var="msgs"/>

<sakai:view>
<h:form>

   <sakai:view_title value=""/>
   <sakai:instruction_message value="" />
   <sakai:messages />

   <ospx:splitarea direction="horizontal" width="100%">
      <ospx:splitsection size="75%" valign="top">
          <ospx:splitarea direction="horizontal" width="100%">
             <ospx:splitsection size="85%" valign="top">
               <sakai:panel_edit>
                  <h:outputLabel for="title" id="titleLabel" value="#{msgs.page_itle}" />
                  <h:panelGroup>
                     <h:inputText id="title" value="#{freeForm.currentPage.base.title}" required="true">
                        <f:validateLength minimum="1" maximum="255" />
                     </h:inputText>
                     <h:message for="title" styleClass="validationEmbedded" />
                  </h:panelGroup>
                  <h:outputLabel for="description" id="descriptionLabel" value="#{msgs.page_description}" />
                  <h:panelGroup>
                     <h:inputTextarea id="description" value="#{freeForm.currentPage.base.description}" required="false">
                        <f:validateLength minimum="0" maximum="255" />
                     </h:inputTextarea>
                     <h:message for="description" styleClass="validationEmbedded" />
                  </h:panelGroup>

                  <h:outputLabel for="keywords" id="keywordsLabel" value="#{msgs.page_keywords}" />
                  <h:panelGroup>
                     <h:inputTextarea id="keywords" value="#{freeForm.currentPage.base.keywords}" required="false">
                        <f:validateLength minimum="0" maximum="255" />
                     </h:inputTextarea>
                     <h:message for="keywords" styleClass="validationEmbedded" />
                  </h:panelGroup>
               </sakai:panel_edit>
             </ospx:splitsection>
             <ospx:splitsection valign="top">
               <h:outputLabel for="modified" id="modifiedLabel" value="#{msgs.page_modified}" />
               <h:outputText id="modified" value="#{freeForm.currentPage.base.modified}" />
             </ospx:splitsection>
         </ospx:splitarea>
      </ospx:splitsection>
      <ospx:splitsection size="25%" valign="top" cssclass="selectedListBox">
         <f:subview id="selectedAudience">
            <%@ include file="items.jspf" %>
         </f:subview>
      </ospx:splitsection>
   </ospx:splitarea>

   <f:subview id="navigation">
      <%@ include file="navigation.jspf" %>
   </f:subview>

</h:form>

</sakai:view>
</f:view>

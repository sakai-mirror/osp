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
             <ospx:splitsection size="80%" valign="top">
               <sakai:panel_edit>
                  <ospx:formLabel valueRequired="true">
                     <h:outputLabel for="title" id="titleLabel" value="#{msgs.page_title}" />
                  </ospx:formLabel>
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
             
                  <ospx:formLabel valueRequired="true">
                     <h:outputLabel for="layoutFile" id="layoutLabel" value="#{msgs.page_layout}" />
                  </ospx:formLabel>
                  <h:panelGroup>
                     <f:subview id="originalLayout" rendered="#{freeForm.currentPage.hasLayout}">
                        <sakai:doc_properties>
                           <h:outputLabel for="layout" id="layoutLabel" value="#{msgs.original_layout}"/>
                           <h:outputText id="layout" value="#{freeForm.currentPage.base.layout.name}"/>
                        </sakai:doc_properties>
                     </f:subview>
                     <h:inputText id="layoutFile" value="#{freeForm.currentPage.layoutName}" 
                           readonly="true" />

                     <h:commandLink action="#{freeForm.currentPage.processActionSelectLayout}" immediate="true">
                        <h:outputText value="#{msgs.select_layout}"/>
                     </h:commandLink>
                     <h:message for="layoutFileHidden" styleClass="validationEmbedded" />
                     <h:graphicImage height="125" width="100"
                        value="#{freeForm.currentPage.selectedLayout.previewImage.externalUri}"
                        rendered="#{freeForm.currentPage.layoutSelected}"
                        />
                  </h:panelGroup>
                  
                  <h:outputLabel for="styleFile" id="styleLabel" value="#{msgs.page_style}" />
                  <h:panelGroup>
                     <h:inputText id="styleFile" value="#{freeForm.currentPage.styleName}" 
                           readonly="true" required="false" />
                     <h:commandLink action="#{freeForm.currentPage.processActionSelectStyle}" immediate="true">
                        <h:outputText value="#{msgs.select_style}"/>
                     </h:commandLink>
                  </h:panelGroup>

                  <h:outputLabel for="advancedNavigation" id="advancedNavigationLabel" value="#{msgs.advanced_navigation}" />
                  <h:panelGroup>
                     <h:selectBooleanCheckbox id="advancedNavigation" value="#{freeForm.currentPage.base.advancedNavigation}" />
                     <h:outputText value="#{msgs.advanced_navigation_disclaimer}"/>
                  </h:panelGroup>

               </sakai:panel_edit>
             </ospx:splitsection>
             <ospx:splitsection valign="top">
               <sakai:panel_edit>
                  <h:outputLabel for="modified" id="modifiedLabel" value="#{msgs.page_modified}" />
                  <h:outputFormat id="modified" value="#{msgs.date_format}"
                     rendered="#{!empty freeForm.currentPage.base.modified}">
                     <f:param value="#{freeForm.currentPage.base.modified}" />
                  </h:outputFormat>
               </sakai:panel_edit>
             </ospx:splitsection>
         </ospx:splitarea>
      </ospx:splitsection>
      <ospx:splitsection size="25%" valign="top" cssclass="selectedListBox">
         <f:subview id="selectedAudience">
            <%@ include file="items.jspf" %>
         </f:subview>
      </ospx:splitsection>
   </ospx:splitarea>

   <ospx:splitarea direction="vertical" width="100%">
      <ospx:splitsection>
         <h:commandButton action="main"
            actionListener="#{freeForm.currentPage.pagePropertiesSaved}"
            value="#{msgs.saveAndReturnToPageList}"/>
         <h:commandButton action="arrange"
            actionListener="#{freeForm.currentPage.pagePropertiesSaved}"
            value="#{msgs.arrangePage}"/>
      </ospx:splitsection>
      <ospx:splitsection>
         <f:subview id="navigation">
            <%@ include file="navigationFromPage.jspf" %>
         </f:subview>
      </ospx:splitsection>
   </ospx:splitarea>

</h:form>

</sakai:view>
</f:view>

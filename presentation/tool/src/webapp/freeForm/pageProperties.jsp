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
<h:form>
<sakai:view_title value="#{msgs.title_addPresentation1}" rendered="#{freeForm.presentation.newObject}"/>
<sakai:view_title value="#{msgs.title_editPresentation1}" rendered="#{!freeForm.presentation.newObject}"/>
<%@include file="steps.jspf"%>
<sakai:view_title value="#{msgs.add_page}" rendered="#{freeForm.presentation.newObject}"/>
<sakai:view_title value="#{msgs.edit_page}" rendered="#{!freeForm.presentation.newObject}"/>

<sakai:instruction_message value=""/>
<sakai:messages/>
<ospx:xheader>
<ospx:xheadertitle id="styleTitle" value="#{msgs.page_information_title}"/>
<ospx:xheaderdrawer initiallyexpanded="#{freeForm.currentPage.expandedInformationSection}" cssclass="drawerBorder">
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
                                <f:validateLength minimum="1" maximum="255"/>
                            </h:inputText>
                            <h:message for="title" styleClass="validationEmbedded"/>
                        </h:panelGroup>
                        <h:outputLabel for="description" id="descriptionLabel"
                                       value="#{msgs.page_description}"/>
                        <h:panelGroup>
                            <h:inputTextarea id="description" value="#{freeForm.currentPage.base.description}"
                                             required="false">
                                <f:validateLength minimum="0" maximum="255"/>
                            </h:inputTextarea>
                            <h:message for="description" styleClass="validationEmbedded"/>
                        </h:panelGroup>

                        <h:outputLabel for="keywords" id="keywordsLabel" value="#{msgs.page_keywords}"/>
                        <h:panelGroup>
                            <h:inputTextarea id="keywords" value="#{freeForm.currentPage.base.keywords}"
                                             required="false">
                                <f:validateLength minimum="0" maximum="255"/>
                            </h:inputTextarea>
                            <h:message for="keywords" styleClass="validationEmbedded"/>
                        </h:panelGroup>

                        <ospx:formLabel valueRequired="true">
                            <h:outputLabel for="layoutFile" id="layoutLabel" value="#{msgs.page_layout}"/>
                        </ospx:formLabel>
                        <h:panelGroup>
                            <f:subview id="originalLayout" rendered="#{freeForm.currentPage.hasLayout}">
                                <sakai:doc_properties>
                                    <h:outputLabel for="layout" id="layoutLabel"
                                                   value="#{msgs.original_layout}"/>
                                    <h:outputLabel for="layout" id="layoutLabel"
                                                   value=" "/>
                                    <h:outputText id="layout" value="#{freeForm.currentPage.base.layout.name}"/>
                                </sakai:doc_properties>
                            </f:subview>
                            <h:message for="layoutFileHidden" styleClass="validationEmbedded"/>
                            <h:inputText id="layoutFile" value="#{freeForm.currentPage.layoutName}"
                                         readonly="true" rendered="#{freeForm.currentPage.renderLayoutName}"/>

                            <h:inputHidden id="layoutFileHidden" value=""
                                           required="true"
                                           rendered="#{freeForm.currentPage.selectedLayout.base == null}"/>
                            <h:inputHidden id="layoutFileHidden" value="#{freeForm.currentPage.selectedLayout}"
                                           required="true"
                                           rendered="#{freeForm.currentPage.selectedLayout.base != null}"/>

                            <h:commandLink action="#{freeForm.currentPage.processActionSelectLayout}"
                                           immediate="true">
                                <h:outputText value="#{msgs.select_layout}"/>
                            </h:commandLink>


                        </h:panelGroup>
                        <h:outputLabel id="blank" value=""/>
                        <h:panelGroup>
                        <h:graphicImage height="125" width="100"
												                    value="/img/page-new.png"
												                    rendered="#{!freeForm.currentPage.layoutPreviewImage}"/>
                        <h:graphicImage height="125" width="100"
                                                      value="#{freeForm.currentPage.selectedLayout.previewImage.externalUri}"
                                                      rendered="#{freeForm.currentPage.layoutPreviewImage}"
                                /></h:panelGroup>
                        <h:outputLabel for="styleFile" id="styleLabel" value="#{msgs.page_style}"/>
                        <h:panelGroup>
                            <h:inputText id="styleFile" value="#{freeForm.currentPage.styleName}"
                                         readonly="true" required="false"/>
                            <h:commandLink action="#{freeForm.currentPage.processActionSelectStyle}"
                                           immediate="true">

                                <h:outputText value="#{msgs.select_style}"/>
                            </h:commandLink>
                        </h:panelGroup>


                    </sakai:panel_edit>
                </ospx:splitsection>
                <ospx:splitsection valign="top">
                    <sakai:panel_edit>
                        <h:outputLabel for="modified" id="modifiedLabel" value="#{msgs.page_modified} "/>
                        <h:outputFormat id="modified" value="#{msgs.date_format}"
                                        rendered="#{!empty freeForm.currentPage.base.modified}">
                            <f:param value="#{freeForm.currentPage.base.modified}"/>
                        </h:outputFormat>
                    </sakai:panel_edit>
                </ospx:splitsection>
            </ospx:splitarea>
        </ospx:splitsection>
    </ospx:splitarea>
</ospx:xheaderdrawer>
</ospx:xheader>
<ospx:xheader>
    <ospx:xheadertitle id="styleTitle2" value="#{msgs.page_content_title}"/>
    <ospx:xheaderdrawer initiallyexpanded="true" cssclass="drawerBorder">
        <f:subview id="arrange">
            <h:panelGrid columns="1">
                <sakai:instruction_message value = "#{msgs.manage_items_instructions}"/>
                        <h:commandButton actionListener="#{freeForm.processActionManageItems}"
                                         value="#{msgs.manage_items}"/>
                    </h:panelGrid>
            <ospx:xmlDocument factory="#{freeForm.factory}"
                              xmlFile="#{freeForm.currentPage.xmlFile}"
                              var="freeForm.currentPage.regionMap" rendered ="#{freeForm.currentPage.xmlFile != null}"/>
        </f:subview>             
    </ospx:xheaderdrawer>
</ospx:xheader>

<f:subview id="navigation">
    <%@ include file="navigationFromPage.jspf" %>
</f:subview>
</h:form>

</sakai:view>
</f:view>

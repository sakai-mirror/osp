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
            <sakai:instruction_message value="#{msgs.instructions_freeForm}"/>
            <sakai:messages/>
            <f:subview id="newPage">
            <h:panelGrid >
               <h:column>
                    <h:commandLink action="#{freeForm.processActionNewPage}">
                        <h:graphicImage height="125" width="100" value="/img/page-new.png"/>
                    </h:commandLink>
                    <sakai:doc_section>
                        <h:commandLink action="#{freeForm.processActionNewPage}">
                            <h:outputText value="#{msgs.add_page}"/>
                        </h:commandLink>
                    </sakai:doc_section>

                </h:column>
            </h:panelGrid>
            </f:subview>
            <sakai:flat_list value="#{freeForm.pageList}" var="page">

                <h:column>
                    <h:panelGrid columns="5">

                        <h:panelGroup>
                            <h:commandLink action="#{page.moveUp}" rendered="#{page.base.sequence != 0}">
                                <h:graphicImage value="/img/arrowLeft.png"/>
                            </h:commandLink>
                            <h:commandLink action="#{page.processActionEdit}">
                                <h:graphicImage height="125" width="100"
                                                value="#{page.selectedLayout.previewImage.externalUri}"
                                                rendered="#{page.layoutSelected}"/>
                            </h:commandLink>
                            <h:commandLink action="#{page.moveDown}" rendered="#{!page.last}">
                                <h:graphicImage value="/img/arrowRight.png"/>
                            </h:commandLink>
                            <sakai:doc_section>
                                <h:commandLink action="#{page.processActionEdit}">
                                    <h:outputText value="#{page.base.title}"/>
                                </h:commandLink>
                            </sakai:doc_section>

                        </h:panelGroup>
                    </h:panelGrid>
                </h:column>
            </sakai:flat_list>
            <h:panelGrid columns="3" cellspacing="1">
                <h:outputLabel for="styleFile" id="styleLabel" value="#{msgs.page_style}"/>
                <h:inputText id="styleFile" value="#{freeForm.styleName}"
                             readonly="true" required="false"/>
                <h:commandLink action="#{freeForm.processActionSelectStyle}" immediate="true">
                    <h:outputText value="#{msgs.select_style}"/>
                </h:commandLink>


                <h:selectBooleanCheckbox disabled="true"
                                         value="#{freeForm.presentation.advancedNavigation}" rendered="#{freeForm.pageCount == 0}"/>
                <h:selectBooleanCheckbox id="advancedNavigation"
                                         value="#{freeForm.presentation.advancedNavigation}" rendered="#{freeForm.pageCount > 0}"/>
                <h:outputLabel for="advancedNavigation" id="advancedNavigationLabel"
                               value="#{msgs.advanced_navigation}"/>
                <sakai:instruction_message value="#{msgs.advanced_navigation_disclaimer}"/>
            </h:panelGrid>

            <f:subview id="navigation">
                <%@ include file="navigation.jspf" %>
            </f:subview>
        </h:form>
    </sakai:view>
</f:view>
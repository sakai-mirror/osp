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
<f:loadBundle basename="org.theospi.portfolio.common.bundle.Messages" var="msgs"/>

<sakai:view>
<sakai:view_title value="#{audience.globalTitle}"/>
<f:subview rendered="#{audience.portfolioWizard}" id="steps">
    <%@ include file="steps.jspf" %>
</f:subview>

<sakai:instruction_message value="#{audience.instructions}"/>
<sakai:messages/>

<h:form>
    <ospx:splitarea direction="horizontal" width="100%">
        <ospx:splitsection size="100%" valign="top">
            <!-- group drawer -->
            <ospx:xheader>
                <ospx:xheadertitle id="groupTitle" value="#{audience.groupTitle}"/>
                <ospx:xheaderdrawer initiallyexpanded="true" cssclass="drawerBorder">
                    <h:panelGrid id="transferTable" columns="3" columnClasses="available,transferButtons,selected">

                        <h:panelGroup>
                            <x:div>
                                <h:outputFormat value="#{msgs.role_name_label}"/>
                            </x:div>
                            <x:div>
                                <h:selectManyListbox id="availableUsers" value="#{audience.availableRoleMember}"
                                                     size="10" style="width:250px;">
                                    <f:selectItems value="#{audience.availableRoleMemberList}"/>
                                </h:selectManyListbox>
                            </x:div>
                        </h:panelGroup>

                        <h:panelGrid id="transferButtons" columns="1" columnClasses="transferButtonTable">
                            <sakai:button_bar>
                                <sakai:button_bar_item id="add_selected" action="#{audience.processActionAdd}"
                                                       value="#{msgs.add_members}"/>
                                <sakai:button_bar_item id="remove_button" action="#{audience.processActionRemoveSelected}"
                                                       value="#{msgs.remove_members}"/>
                            </sakai:button_bar>
                        </h:panelGrid>

                        <h:panelGroup>
                            <h:outputFormat value="#{audience.selectedTitle}"/>

                            <h:selectManyListbox id="selectedUsers" size="10" value="#{audience.selectedRoleMember}"
                                                 style="width:250px;">
                                <f:selectItems value="#{audience.selectedRoleMemberList}"/>
                            </h:selectManyListbox>
                        </h:panelGroup>
                    </h:panelGrid>
                    <h:panelGrid rendered="#{audience.maxList}" columns="2">
                    <h:panelGroup>
                     <h:outputFormat value="#{msgs.browseUserInstruction}"/>

                     <h:commandLink id="browse_button" action="browse" value="#{common_msgs.browse_members}"
                                       style="white-space:nowrap;"/>
                     </h:panelGroup>
                    </h:panelGrid>
                    <ospx:splitarea rendered="#{audience.emailCapable}" direction="horizontal" width="100%">
                        <ospx:splitsection size="25%" valign="center">
                            <h:outputLabel value="#{common_msgs.email_label}:" for="emails"/>
                        </ospx:splitsection>
                        <ospx:splitsection size="25%" valign="center">
                            <h:inputText value="#{audience.searchEmails}" id="emails" size="70"/>
                        </ospx:splitsection>
                        <ospx:splitsection size="25%" valign="center">
                            <h:outputText value=" "/>
                        </ospx:splitsection>
                        <ospx:splitsection size="25%" valign="top" align="left">
                            <sakai:button_bar>
                                <sakai:button_bar_item id="add_email_button"
                                                       action="#{audience.processActionAddEmail}"
                                                       value="#{common_msgs.add_members}"/>
                            </sakai:button_bar>
                        </ospx:splitsection>
                    </ospx:splitarea>
                </ospx:xheaderdrawer>
            </ospx:xheader>

            <!-- Public URL Drawer -->
            <ospx:xheader rendered="#{audience.publicCapable}">
                <ospx:xheadertitle id="publicTitle" value="#{audience.publicTitle}"/>
                <ospx:xheaderdrawer initiallyexpanded="true" cssclass="drawerBorder">
                    <ospx:splitarea direction="horizontal">
                        <ospx:splitsection size="100%">
                            <sakai:instruction_message value="#{audience.publicInstructions}"/>
                        </ospx:splitsection>
                    </ospx:splitarea>
                    <ospx:splitarea direction="horizontal" width="100%">
                        <ospx:splitsection size="50%">
                            <h:selectBooleanCheckbox id="isPublic" value="#{audience.publicAudience}"/>
                            <h:outputLabel value="#{common_msgs.public_label}: " for="isPublic"
                                           style="white-space:nowrap;"/>
                        </ospx:splitsection>
                        <ospx:splitsection size="50%">
                            <h:outputLink id="publicURL" value="#{audience.publicURL}">
                                <h:outputText id="publicURLtext" value="#{audience.publicURL}"/>
                            </h:outputLink>
                        </ospx:splitsection>
                    </ospx:splitarea>
                </ospx:xheaderdrawer>
            </ospx:xheader>
        </ospx:splitsection>
    </ospx:splitarea>

    <sakai:button_bar>
        <sakai:button_bar_item id="save_button" action="#{audience.processActionSave}"
                               value="#{common_msgs.save_audience}"/>
        <sakai:button_bar_item id="saveNotify_button" action="#{audience.processActionSaveNotify}"
                               value="#{common_msgs.save_notify_audience}" rendered="#{audience.portfolioWizard}"/>
        <sakai:button_bar_item id="back_button" action="#{audience.processActionBack}"
                               rendered="#{audience.portfolioWizard}"
                               value="#{common_msgs.back_audience}"/>
        <sakai:button_bar_item id="_target1" action="#{audience.processActionCancel}"
                               value="#{common_msgs.cancel_audience}"/>
    </sakai:button_bar>
</h:form>

</sakai:view>
</f:view>






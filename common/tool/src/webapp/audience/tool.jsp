<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ taglib uri="http://java.sun.com/jstl/core_rt" prefix="c" %>
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

<sakai:view_title rendered="#{not audience.portfolioAudience}" value="#{common_msgs.audience_global_title}"/>
<sakai:view>   
<f:subview rendered="#{audience.portfolioAudience}" id="steps">
    <%@ include file="steps.jspf" %>
</f:subview>

<c:if test="${audience.wizardAudience}">
  <sakai:instruction_message value="#{common_msgs.audience_wizard_instructions}"/>
</c:if>
<c:if test="${audience.matrixAudience}">
  <sakai:instruction_message value="#{common_msgs.audience_matrix_instructions}"/>
</c:if>
<c:if test="${audience.portfolioAudience}">
  <sakai:instruction_message value="#{common_msgs.audience_portfolio_instructions}"/>
</c:if>

<sakai:messages/>

<h:form id="mainForm">
    <ospx:splitarea direction="horizontal" width="100%">
        <ospx:splitsection size="100%" valign="top">
            <!-- group drawer -->
            <ospx:xheader>
               <c:if test="${audience.wizardAudience}">
                  <ospx:xheadertitle id="groupTitle" value="#{common_msgs.audience_group_title}" />
               </c:if>
               <c:if test="${audience.matrixAudience}">
                  <ospx:xheadertitle id="groupTitle" value="#{common_msgs.audience_group_title}" />
               </c:if>
               <c:if test="${audience.portfolioAudience}">
                  <ospx:xheadertitle id="groupTitle" value="#{common_msgs.audience_portfolio_group_title}" />
               </c:if>
               <ospx:xheaderdrawer initiallyexpanded="true" cssclass="drawerBorder">
                  <h:panelGrid id="transferTable" columns="3" columnClasses="available,transferButtons,selected">

                     <h:panelGroup>
                        <ospx:splitarea direction="vertical">
                           <ospx:splitsection valign="top">
                              <h:outputFormat value="#{common_msgs.role_name_label}"/>
                           </ospx:splitsection>
                           <ospx:splitsection valign="top">
                              <h:selectManyListbox id="availableUsers" value="#{audience.availableRoleMember}"
                                                   size="10" style="width:250px;">
                                 <f:selectItems value="#{audience.availableRoleMemberList}"/>
                              </h:selectManyListbox>
                           </ospx:splitsection>
                        </ospx:splitarea>
                     </h:panelGroup>
   
                     <h:panelGrid id="transferButtons" columns="1" columnClasses="transferButtonTable">
                        <ospx:splitarea width="120" direction="vertical">
                           <ospx:splitsection valign="top" align="center">
                              <sakai:button_bar>
                                 <sakai:button_bar_item id="add_selected" action="#{audience.processActionAdd}"
                                                         value="#{common_msgs.add_members}"/>
                              </sakai:button_bar>
                           </ospx:splitsection>
                           <ospx:splitsection valign="top" align="center">
                              <sakai:button_bar>
                                 <sakai:button_bar_item id="remove_button" action="#{audience.processActionRemoveSelected}"
                                                        value="#{common_msgs.remove_members}"/>
                              </sakai:button_bar>
                           </ospx:splitsection>
                        </ospx:splitarea>
                     </h:panelGrid>
   
                     <h:panelGroup>
                        <ospx:splitarea direction="vertical">
                           <ospx:splitsection valign="top">
                              <c:if test="${audience.wizardAudience}">
                                    <h:outputFormat value="#{common_msgs.audience_selected_evaluators}"/>
                              </c:if>
                              <c:if test="${audience.matrixAudience}">
                                    <h:outputFormat value="#{common_msgs.audience_selected_evaluators}"/>
                              </c:if>
                              <c:if test="${audience.portfolioAudience}">
                                    <h:outputFormat value="#{common_msgs.audience_selected_audience}"/>
                              </c:if>
                           </ospx:splitsection>
                           <ospx:splitsection valign="top">
                              <h:selectManyListbox id="selectedUsers" size="10" value="#{audience.selectedRoleMember}"
                                                   style="width:250px;">
                                 <f:selectItems value="#{audience.selectedRoleMemberList}"/>
                              </h:selectManyListbox>
                           </ospx:splitsection>
                        </ospx:splitarea>
                     </h:panelGroup>
                  </h:panelGrid>
                  <h:panelGrid  rendered="#{audience.maxList}" columns="1">
                     <h:panelGroup>
                        <h:outputFormat value = "#{audience.browseMessage}"/>
                            <h:outputFormat value = " "/>
                        <h:commandLink id="browse_button" action="browse" value="#{common_msgs.browse_members}"
                                       style="white-space:nowrap;"/>
                        <h:outputFormat value = " "/>
                        <c:if test="${audience.wizardAudience}">
                           <h:outputFormat value = "#{common_msgs.audience_individual_evaluators}" />
                        </c:if>
                        <c:if test="${audience.matrixAudience}">
                           <h:outputFormat value = "#{common_msgs.audience_individual_evaluators}" />
                        </c:if>
                        <c:if test="${audience.portfolioAudience}">
                           <h:outputFormat value = "#{common_msgs.audience_individual_users}" />
                        </c:if>
                     </h:panelGroup>
                  </h:panelGrid>
                  
                  <f:subview id="thePagesCat" rendered="#{audience.portfolioAudience}" >
                     <f:verbatim><p class='shorttext'></f:verbatim>
                     <h:outputLabel value="#{common_msgs.email_label}:" for="emails"/>
                                  
                     <h:inputText value="#{audience.searchEmails}" id="emails" size="60"/>
                     <h:outputText value=" "/>
                     <h:commandButton id="add_email_button"
                                      action="#{audience.processActionAddEmail}"
                                      value="#{common_msgs.add_members}"/>
                     <f:verbatim></p></f:verbatim>
                  </f:subview>
               </ospx:xheaderdrawer>
            </ospx:xheader>

            <!-- Public URL Drawer -->
            <ospx:xheader rendered="#{audience.portfolioAudience}">
               <ospx:xheadertitle id="publicTitle" value="#{common_msgs.audience_public_title}"/>
               <ospx:xheaderdrawer initiallyexpanded="true" cssclass="drawerBorder">
                  <ospx:splitarea direction="horizontal">
                     <ospx:splitsection size="100%">
                        <sakai:instruction_message value="#{common_msgs.audience_public_instructions}"/>
                     </ospx:splitsection>
                  </ospx:splitarea>
                       
                  <f:verbatim><script>
                     function setPublicURLDisabled(){
                        isPublic = document.getElementById("mainForm:isPublic");
                        publicUrl = document.getElementById("mainForm:publicUrl");
   
                        publicUrl.disabled = !isPublic.checked;
                     }
                     var selected = false;
                  </script></f:verbatim>
                  <f:verbatim><p class='shorttext'><label for="isPublic"></f:verbatim>
                  <h:selectBooleanCheckbox id="isPublic" value="#{audience.publicAudience}"
                                           onclick="setPublicURLDisabled()"/>
                  <h:outputText value="#{common_msgs.public_label}: " style="white-space:nowrap;"/>
                  <f:verbatim></label></f:verbatim>
                  <h:inputText value="#{audience.publicURL}" id="publicUrl" size="60" readonly="true"
                               onclick="if(!selected){this.focus(); this.select();selected=true;}"/>
                               
                  <f:verbatim><script>
                     setPublicURLDisabled();
                  </script></f:verbatim>
                  <f:verbatim></p></f:verbatim>
               </ospx:xheaderdrawer>
            </ospx:xheader>
        </ospx:splitsection>
    </ospx:splitarea>
    <sakai:button_bar>
        <sakai:button_bar_item id="save_button" action="#{audience.processActionSave}"
                               value="#{common_msgs.save_audience}" styleClass="active" accesskey="s" />

        <sakai:button_bar_item id="saveNotify_button" action="#{audience.processActionSaveNotify}"
                               rendered="#{audience.portfolioAudience}"
                               value="#{common_msgs.save_notify_audience}" />
        <sakai:button_bar_item id="back_button" action="#{audience.processActionBack}"
                               rendered="#{audience.portfolioAudience}"
                               value="#{common_msgs.back_audience}"/>
        <sakai:button_bar_item id="_target1" action="#{audience.processActionCancel}"
                               value="#{common_msgs.cancel_audience}" accesskey="x" />
    </sakai:button_bar>
</h:form>

</sakai:view>
</f:view>

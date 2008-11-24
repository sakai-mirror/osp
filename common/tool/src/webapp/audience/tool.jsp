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

<sakai:view_title rendered="#{not audience.portfolioAudience}" value="#{common_msgs.audience_eval_title}"/>
<sakai:view>   
<f:subview rendered="#{audience.portfolioAudience}" id="steps">
    <%@ include file="steps.jspf" %>
</f:subview>


<f:subview rendered="#{audience.wizardAudience}" id="wizardInstructs">
	<sakai:instruction_message value="#{common_msgs.audience_wizard_instructions}"/>
</f:subview>

<f:subview rendered="#{audience.portfolioAudience}" id="portInstructs">
	<sakai:instruction_message value="#{common_msgs.audience_portfolio_instructions}"/>
</f:subview>

<f:subview rendered="#{audience.matrixAudience}" id="matrixInstructs">
	<sakai:instruction_message value="#{common_msgs.audience_matrix_instructions}"/>
</f:subview>


<sakai:messages/>

<h:form id="mainForm">
    <ospx:splitarea direction="horizontal" width="100%">
      <ospx:splitsection size="100%" valign="top">
        
         <!-- worksite user section -->
         <sakai:view_title id="userTitle" value="#{common_msgs.audience_user_title}" rendered="#{audience.wizardAudience}" />
 
         <sakai:view_title id="userTitle1" value="#{common_msgs.audience_user_title}"  rendered="#{audience.matrixAudience}" />
 
         <sakai:view_title id="userTitle3" value="#{common_msgs.audience_portfolio_user_title}" rendered="#{audience.portfolioAudience}"/>
                  <h:panelGrid id="transferUserTable" columns="3" columnClasses="available,transferButtons,selected" summary="#{common_msgs.name_table_summary}">

                     <h:panelGroup>
                        <ospx:splitarea direction="vertical">
                           <ospx:splitsection valign="top">
                              <h:outputFormat value="#{common_msgs.name_label}"/>
                           </ospx:splitsection>
                           <ospx:splitsection valign="top">
                              <h:selectManyListbox id="availableUsers" value="#{audience.availableUserArray}"
                                                   size="10" style="width:350px;">
                                 <f:selectItems value="#{audience.availableUserList}"/>
                              </h:selectManyListbox>
                           </ospx:splitsection>
                        </ospx:splitarea>
                     </h:panelGroup>
   
                     <h:panelGrid id="userTransferButtons" columns="1" columnClasses="transferButtonTable">
                        <ospx:splitarea width="120" direction="vertical">
                           <ospx:splitsection valign="top" align="center">
                              <sakai:button_bar>
                                 <sakai:button_bar_item id="add_user_button" action="#{audience.processActionAddUser}"
                                                         value="#{common_msgs.add_members}"/>
                              </sakai:button_bar>
                           </ospx:splitsection>
                           <ospx:splitsection valign="top" align="center">
                              <sakai:button_bar>
                                 <sakai:button_bar_item id="remove_user_button" action="#{audience.processActionRemoveUser}"
                                                        value="#{common_msgs.remove_members}"/>
                              </sakai:button_bar>
                           </ospx:splitsection>
                        </ospx:splitarea>
                     </h:panelGrid>
      
                     <h:panelGroup>
                        <ospx:splitarea direction="vertical">
                           <ospx:splitsection valign="top">
                           	<f:subview rendered="#{audience.wizardAudience}" id="wizSubView2">
                                    <h:outputFormat value="#{common_msgs.audience_selected_evaluators}"/>
                              </f:subview>
                              <f:subview rendered="#{audience.matrixAudience}" id="matSubView2">
                                    <h:outputFormat value="#{common_msgs.audience_selected_evaluators}"/>
                              </f:subview>
                              <f:subview rendered="#{audience.portfolioAudience}" id="portSubView2">
                                    <h:outputFormat value="#{common_msgs.audience_selected_audience}"/>
                              </f:subview>
                           </ospx:splitsection>
                           <ospx:splitsection valign="top">
                              <h:selectManyListbox id="selectedUsers" size="10" value="#{audience.selectedUserArray}"
                                                   style="width:350px;">
                                 <f:selectItems value="#{audience.selectedUserList}"/>
                              </h:selectManyListbox>
                           </ospx:splitsection>
                        </ospx:splitarea>
                     </h:panelGroup>
                  </h:panelGrid>
                  
                  <!-- other user and email user option -->
                  <f:subview id="emailUser" rendered="#{audience.portfolioAudience}">
                     <f:verbatim><h3></f:verbatim>
                     <h:outputText value="#{common_msgs.audience_portfolio_other_title}" />
                     <f:verbatim></h3></f:verbatim>
                  
                     <f:verbatim><p class='shorttext'></f:verbatim>
                     <f:subview rendered="#{!audience.guestUserEnabled}" id="guestSubView">
                        <h:outputLabel value="#{common_msgs.any_user_label}:" for="emails"/>
                     </f:subview>
                     <f:subview rendered="#{audience.guestUserEnabled}" id="guestSubView2">
                        <h:outputLabel value="#{common_msgs.email_label}:" for="emails"/>
                     </f:subview>
                     
                     <h:inputText value="#{audience.searchEmails}" id="emails" size="60"/>
                     <h:outputText value=" "/>
                     <h:commandButton id="add_email_button"
                                      action="#{audience.processActionAddEmailUser}"
                                      value="#{common_msgs.add_members}"/>
                     <f:verbatim></p></f:verbatim>
                  </f:subview>

                  <!-- optional message for browse user selection -->
                  <f:subview id="browseUser" rendered="#{audience.maxList}" >
                     <f:verbatim><p class='shorttext'></f:verbatim>
                     <h:outputFormat value = "#{audience.browseMessage}"/>
                         <h:outputFormat value = " "/>
                     <h:commandLink id="browse_button" action="browse" value="#{common_msgs.browse_members}"
                                    style="white-space:nowrap;"/>
                     <h:outputFormat value = " "/>
                     <f:subview id="audSubV1" rendered="#{audience.wizardAudience}">
                        <h:outputFormat value = "#{common_msgs.audience_individual_evaluators}" />
                     </f:subview>
                     <f:subview id="audSubV2" rendered="#{audience.matrixAudience}">
                        <h:outputFormat value = "#{common_msgs.audience_individual_evaluators}" />
                     </f:subview>
                     <f:subview id="audSubV4" rendered="#{audience.portfolioAudience}">
                        <h:outputFormat value = "#{common_msgs.audience_individual_users}" />
                     </f:subview>
                     <f:verbatim></p></f:verbatim>
                  </f:subview>
 
                  <!-- worksite role section -->
                  <ospx:splitarea width="825" direction="horizontal">
                    <ospx:splitsection valign="top" align="left">
                      <sakai:view_title id="roleTitle" value="#{common_msgs.audience_role_title}"  rendered="#{audience.wizardAudience}"/>
 
                      <sakai:view_title id="roleTitle2" value="#{common_msgs.audience_role_title}"  rendered="#{audience.matrixAudience}"/>
    
                      <sakai:view_title id="roleTitle3" value="#{common_msgs.audience_portfolio_role_title}"  rendered="#{audience.portfolioAudience}" />
                    </ospx:splitsection>
							
                    <!-- option to show/hide all site roles -->
                    <ospx:splitsection valign="bottom" align="right">
                      <f:subview id="showAllSiteRoles" rendered="#{audience.renderShowAllSiteRoles}">
                         <h:commandLink id="update_role_button1" 
                                                action="#{audience.processActionShowAllSiteRoles}"
                                                rendered="#{!audience.showAllSiteRoles}"
                                                value="#{common_msgs.update_allsite_roles}"/>
                         <h:commandLink id="update_role_button2" 
                                                action="#{audience.processActionHideAllSiteRoles}"
                                                rendered="#{audience.showAllSiteRoles}"
                                                value="#{common_msgs.update_site_roles}"/>
                      </f:subview>
                    </ospx:splitsection>
                  </ospx:splitarea>
                   <h:panelGrid id="transferRoleTable" columns="3" columnClasses="available,transferButtons,selected"  summary="#{common_msgs.role_table_summary}">
                     <h:panelGroup>
                        <ospx:splitarea direction="vertical">
                           <ospx:splitsection valign="top">
                              <h:outputFormat value="#{common_msgs.role_label}"/>
                           </ospx:splitsection>
                           <ospx:splitsection valign="top">
                              <h:selectManyListbox id="availableRoles" value="#{audience.availableRoleArray}"
                                                   size="10" style="width:350px;">
                                 <f:selectItems value="#{audience.availableRoleList}"/>
                              </h:selectManyListbox>
                           </ospx:splitsection>
                        </ospx:splitarea>
                     </h:panelGroup>
   
                     <h:panelGrid id="roleTransferButtons" columns="1" columnClasses="transferButtonTable">
                        <ospx:splitarea width="120" direction="vertical">
                           <ospx:splitsection valign="top" align="center">
                              <sakai:button_bar>
                                 <sakai:button_bar_item id="add_role_button" action="#{audience.processActionAddRole}"
                                                         value="#{common_msgs.add_members}"/>
                              </sakai:button_bar>
                           </ospx:splitsection>
                           <ospx:splitsection valign="top" align="center">
                              <sakai:button_bar>
                                 <sakai:button_bar_item id="remove_role_button" action="#{audience.processActionRemoveRole}"
                                                        value="#{common_msgs.remove_members}"/>
                              </sakai:button_bar>
                           </ospx:splitsection>
                        </ospx:splitarea>
                     </h:panelGrid>
   
                     <h:panelGroup>
                        <ospx:splitarea direction="vertical">
                           <ospx:splitsection valign="top">
                           	  <f:subview id="audSubV12" rendered="#{audience.wizardAudience}">
                                    <h:outputFormat value="#{common_msgs.audience_selected_evaluators}"/>
                              </f:subview>
                              <f:subview id="audSubV14" rendered="#{audience.matrixAudience}">
                                    <h:outputFormat value="#{common_msgs.audience_selected_evaluators}"/>
                              </f:subview>
                              <f:subview id="audSubV15" rendered="#{audience.portfolioAudience}">
                                    <h:outputFormat value="#{common_msgs.audience_selected_audience}"/>
                              </f:subview>
                           </ospx:splitsection>
                           <ospx:splitsection valign="top">
                              <h:selectManyListbox id="selectedRoles" size="10" value="#{audience.selectedRoleArray}"
                                                   style="width:350px;">
                                 <f:selectItems value="#{audience.selectedRoleList}"/>
                              </h:selectManyListbox>
                           </ospx:splitsection>
                        </ospx:splitarea>
                     </h:panelGroup>
                   </h:panelGrid>
                  
            <!-- Public URL subview -->
            <ospx:splitarea direction="vertical" rendered="#{audience.portfolioAudience}">
				<ospx:splitsection>
               <sakai:view_title id="publicTitle" value="#{common_msgs.audience_public_title}"/>
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
               </ospx:splitsection>
            </ospx:splitarea>
            
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

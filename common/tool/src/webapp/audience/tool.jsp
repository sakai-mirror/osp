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

<sakai:view_title value="#{common_msgs.audience_eval_title}"/>

<h3><div class="highlight"><h:outputText value="#{audience.pageContext}"/></div></h3>
<div class="highlight"><h:outputText value="#{audience.pageContext2}"/></div>
<sakai:view>   

<f:subview rendered="#{audience.wizardAudience}" id="wizardInstructs">
	<sakai:instruction_message value="#{common_msgs.audience_wizard_instructions}"/>
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
                  
                  <!-- worksite role section -->
                  <ospx:splitarea width="825" direction="horizontal">
                    <ospx:splitsection valign="top" align="left">
                      <sakai:view_title id="roleTitle" value="#{common_msgs.audience_role_title}"  rendered="#{audience.wizardAudience}"/>

                      <sakai:view_title id="roleTitle2" value="#{common_msgs.audience_role_title}"  rendered="#{audience.matrixAudience}"/>
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
                  
      </ospx:splitsection>
    </ospx:splitarea>
    <sakai:button_bar>
	 
        <sakai:button_bar_item id="save_button" action="#{audience.processActionSave}"
                               value="#{common_msgs.save_audience}" styleClass="active" accesskey="s" />

        <sakai:button_bar_item id="_target1" action="#{audience.processActionCancel}"
                               value="#{common_msgs.cancel_audience}" accesskey="x" />
    </sakai:button_bar>
</h:form>

</sakai:view>
</f:view>
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
	<f:subview rendered="#{audience.wizardAudience}" id="sV1">
     <sakai:instruction_message value="#{common_msgs.audience_wizard_instructions}"/>
   </f:subview>
   <f:subview rendered="#{audience.matrixAudience}" id="sv2">
     <sakai:instruction_message value="#{common_msgs.audience_matrix_instructions}"/>
   </f:subview>
   <f:subview rendered="#{audience.portfolioAudience}" id="sV3">
     <sakai:instruction_message value="#{common_msgs.audience_portfolio_instructions}"/>
   </f:subview>
   
   <sakai:messages />

<h:form>
   <ospx:splitarea direction="horizontal" width="100%">
      <ospx:splitsection size="75%" valign="top">
         <ospx:xheader>
            <ospx:xheadertitle id="browseFilter" value="#{common_msgs.audience_filter_instructions}  ">
            </ospx:xheadertitle>
             <ospx:xheaderdrawer initiallyexpanded="true" cssclass="drawerBorder">
                <ospx:splitarea direction="vertical" height="85%">
                   <ospx:splitsection valign="top">
                      <h:panelGrid columns="4">
                         <h:outputLabel value="#{common_msgs.filter_roles}  " for="filterRoles" />
                         <h:outputText value=""/>
                         <h:outputText value=""/>
                         <h:outputLabel value="#{common_msgs.filter_groups}  " for="filterGroups" rendered="#{audience.hasGroups}" />
                         <h:selectManyListbox size="4" value="#{audience.selectedRolesFilter}" id="filterRoles">
                            <f:selectItems value="#{audience.roles}" />
                         </h:selectManyListbox>
                         <h:outputText value=""/>
                         <h:outputText value=""/>
                         <h:selectManyListbox size="4" value="#{audience.selectedGroupsFilter}"
                            id="filterGroups" rendered="#{audience.hasGroups}">
                            <f:selectItems value="#{audience.groups}" />
                         </h:selectManyListbox>
                      </h:panelGrid>
                   </ospx:splitsection>
                   <ospx:splitsection valign="center">
                      <sakai:button_bar>
                        <sakai:button_bar_item id="apply_filter_button"  
                           action="#{audience.processActionApplyFilter}"
                           value="#{common_msgs.apply_filter}"/>
                        <sakai:button_bar_item id="clear_filter_button" 
                           action="#{audience.processActionClearFilter}"
                           value="#{common_msgs.clear_filter}"/>
                      </sakai:button_bar>
                   </ospx:splitsection>
                </ospx:splitarea>
             </ospx:xheaderdrawer>
         </ospx:xheader>
         <ospx:splitarea direction="horizontal" width="100%">
            <ospx:splitsection size="50%" valign="bottom">
               <sakai:button_bar>
                  <sakai:button_bar_item id="add_selected_button"  
                     action="#{audience.processActionAddBrowseSelected}"
                     value="#{common_msgs.add_members}"/>
                </sakai:button_bar>
            </ospx:splitsection>
            <ospx:splitsection size="50%" valign="top" align="right">
               <sakai:pager id="pager"
                   totalItems="#{audience.browseUsers.totalItems}"
                   firstItem="#{audience.browseUsers.firstItem}"
                   pageSize="#{audience.browseUsers.pageSize}"
                   textStatus="#{common_msgs.browse_user_pager_status}" />
            </ospx:splitsection>
          </ospx:splitarea>
          <sakai:flat_list value="#{audience.browseUsers.subList}" var="member">
            <h:column>
               <f:facet name="header">
                  <h:outputText value="#{common_msgs.user_column_header}" />
               </f:facet>
               <h:selectBooleanCheckbox id="memberSelect" value="#{member.selected}" />
               <h:outputLabel value="#{member.base.eid.value}" for="memberSelect" />
            </h:column>
            <h:column>
               <f:facet name="header">
                  <h:outputText value="#{common_msgs.name_column_header}" />
               </f:facet>
               <h:outputText value="#{member.base.displayName}" />
            </h:column>
            <h:column>
               <f:facet name="header">
                  <h:outputText value="#{common_msgs.role_column_header}" />
               </f:facet>
               <h:outputText value="#{member.role.displayName}" />
            </h:column>
          </sakai:flat_list>

          <sakai:button_bar>
             <sakai:button_bar_item id="back_button"  action="main"
               value="#{common_msgs.return_audience}"/>
           </sakai:button_bar>

      </ospx:splitsection>
      <ospx:splitsection size="25%" valign="top" cssclass="selectedListBox">
         <f:subview id="selectedAudience">
            <%@ include file="audience.inc" %>
         </f:subview>
      </ospx:splitsection>
   </ospx:splitarea>

</h:form>

</sakai:view>
</f:view>

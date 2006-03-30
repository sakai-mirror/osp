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
   <sakai:instruction_message value="#{audience.instructions}" />
   <sakai:messages />

<h:form>
   <ospx:splitarea direction="horizontal" width="100%">
      <ospx:splitsection size="75%" valign="top">
         <ospx:xheader>
            <ospx:xheadertitle id="groupTitle" value="#{audience.groupTitle}" />
            <ospx:xheaderdrawer initiallyexpanded="true" cssclass="drawerBorder">
               <!-- worksite group drawer -->
							<ospx:splitarea direction="horizontal" width="100%">
								<ospx:splitsection size="25%" valign="top">
                  <h:outputLabel value="Worksite: #{audience.site.title}" for="siteRoles" />
								</ospx:splitsection>
								<ospx:splitsection size="50%" valign="top">
									<ospx:scrollablearea width="400px"  height="100px">
	                  <h:selectManyCheckbox id="siteRoles" value="#{audience.selectedRoles}" layout="pageDirection">
	                     <f:selectItems value="#{audience.siteRoles}" />
	                  </h:selectManyCheckbox>
									</ospx:scrollablearea>
								</ospx:splitsection>
								<ospx:splitsection size="25%" valign="top">
                  <h:panelGrid columns="1">
                     <sakai:button_bar>
                        <sakai:button_bar_item id="add_group_button" 
                           action="#{audience.processActionAddGroup}"
                           value="#{msgs.add_all}"/>
                     </sakai:button_bar>
                  </h:panelGrid>
								</ospx:splitsection>
							</ospx:splitarea>
            </ospx:xheaderdrawer>
         </ospx:xheader>
         <ospx:xheader>
            <ospx:xheadertitle id="individualTitle" value="#{audience.individualTitle}" />
            <ospx:xheaderdrawer initiallyexpanded="true" cssclass="drawerBorder">
               <!-- user drawer -->
							<ospx:splitarea direction="horizontal" width="75%">
								<ospx:splitsection size="25%" valign="bottom">
									<h:panelGrid columns="1">
	                  <h:outputLabel value="#{msgs.user_id_label}" for="userId" />
	              	</h:panelGrid>
								</ospx:splitsection>
								<ospx:splitsection size="50%" valign="top">
										<sakai:instruction_message value="#{msgs.userid_instructions}" />
                  	<h:inputText value="#{audience.searchUsers}" id="userId" size="70" />
								</ospx:splitsection>
								<ospx:splitsection size="24%" valign="bottom">
	                       <sakai:button_bar>
                            <sakai:button_bar_item id="add_user_button" 
                                action="#{audience.processActionAddUser}"
	                             value="#{msgs.add_members}"/>
                           </sakai:button_bar>
								</ospx:splitsection>
							</ospx:splitarea>
              <sakai:button_bar>
                  <sakai:button_bar_item id="browse_button" action="browse"
                 value="#{msgs.browse_members}"/>
                 </sakai:button_bar>
                       <h:outputText value="" />
                       <h:outputText value="" />
							<ospx:splitarea rendered="#{audience.emailCapable}" direction="horizontal" width="75%">
								<ospx:splitsection size="25%" valign="bottom">
	                  <h:outputLabel value="#{msgs.email_label}" for="emails" />
								</ospx:splitsection>
								<ospx:splitsection size="50%" valign="top">
										<sakai:instruction_message value="#{msgs.email_instructions}" />
	                	<h:inputText value="#{audience.searchEmails}" id="emails" size="70" />
								</ospx:splitsection>
								<ospx:splitsection size="24%" valign="bottom">
	                       <sakai:button_bar>
                           <sakai:button_bar_item id="add_email_button" 
                              action="#{audience.processActionAddEmail}"
      	                     value="#{msgs.add_members}"/>
                        </sakai:button_bar>
									</ospx:splitsection>
							</ospx:splitarea>
            </ospx:xheaderdrawer>
         </ospx:xheader>
         <ospx:xheader rendered="#{audience.publicCapable}">
            <ospx:xheadertitle id="publicTitle" value="#{audience.publicTitle}" />
            <ospx:xheaderdrawer initiallyexpanded="true"
               cssclass="drawerBorder">
               <h:selectBooleanCheckbox id="isPublic" value="#{audience.publicAudience}" />
               <h:outputLabel value="#{msgs.public_label}" for="isPublic" />
            </ospx:xheaderdrawer>
         </ospx:xheader>
      </ospx:splitsection>
      <ospx:splitsection size="25%" valign="top" cssclass="selectedListBox">
         <f:subview id="selectedAudience">
            <%@ include file="audience.inc" %>
         </f:subview>
      </ospx:splitsection>
   </ospx:splitarea>

<sakai:button_bar>
   <sakai:button_bar_item id="save_button" action="#{audience.processActionSave}"
      value="#{msgs.save_audience}"/>
   <sakai:button_bar_item id="cancel_button" action="#{audience.processActionCancel}"
      value="#{msgs.cancel_audience}"/>
</sakai:button_bar>
</h:form>

</sakai:view>
</f:view>
